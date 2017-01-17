package Debrief.ReaderWriter.NMEA;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class ImportNMEA
{

  /** prefix we use for ownship track that's extractd
   * from NMEA data
   */
  public static final String WECDIS_OWNSHIP_PREFIX = "WECDIS_OWNSHIP";

  private enum MsgType
  {
    VESSEL_NAME, OS_POS, CONTACT, TIMESTAMP, UNKNOWN, AIS, OS_DEPTH,
    OS_COURSE_SPEED;
  }

  private static class State
  {
    static SimpleDateFormat sdf;

    private static Date dateFor(final String dateStr, final String timeStr)
    {
      if (sdf == null)
      {
        sdf = new SimpleDateFormat("yyyyMMdd,HHmmss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      }
      Date res = null;
      try
      {
        res = sdf.parse(dateStr + "," + timeStr);
      }
      catch (final ParseException e)
      {
        Application.logError2(ToolParent.ERROR, "AIS importer failed to parse:"
            + dateStr + ", " + timeStr, e);
      }
      return res;
    }

    public final Date date;
    public final String name;

    public final WorldLocation location;

    public State(final String name, final String dateStr, final String timeStr,
        final String tLat, final String tLong)
    {
      if (dateStr != null && timeStr != null)
      {
        date = dateFor(dateStr, timeStr);
      }
      else
      {
        date = null;
      }
      this.name = name;
      location = locationFor(tLat, tLong);
    }

    private WorldLocation locationFor(final String tLat, final String tLong)
    {
      final double dLat = degsFor(tLat);
      final double dLong = degsFor(tLong);
      final WorldLocation loc = new WorldLocation(dLat, dLong, 0);
      return loc;
    }
  }

  public static class TestImportAIS extends TestCase
  {
    @SuppressWarnings("deprecation")
    public void testDate()
    {
      final String dateStr = "20160720";
      final String timeStr = "082807.345";
      final Date date = State.dateFor(dateStr, timeStr);
      assertNotNull("got date");
      assertEquals("got right date/time", "20 Jul 2016 08:28:07 GMT", date
          .toGMTString());
    }

    public void testDegs()
    {
      assertEquals("got it", 34.159, degsFor("3409.5794,N"), 0.001);
      assertEquals("got it", -15.621, degsFor("01537.3128,W"), 0.001);
      assertEquals("got it", -34.159, degsFor("3409.5794,S"), 0.001);
      assertEquals("got it", 15.621, degsFor("01537.3128,E"), 0.001);
      assertEquals("got it", -34.5, degsFor("3430.0,S"), 0.001);
      assertEquals("got it", 15.25, degsFor("01515.0,E"), 0.001);
      assertEquals("got it", 15.25, degsFor("01515.0,E"), 0.001);
      assertEquals("got it", 2.693, degsFor("00241.5907,E"), 0.001);
      assertEquals("got it", 36.2395, degsFor("3614.3708,N"), 0.001);
    }

    public void testFullImport() throws Exception
    {
      testImport("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/20160720.log");
    }

    public void testImport(final String testFile) throws Exception
    {
      final File testI = new File(testFile);

      // only run the test if we have the log-file available
      if (testI.exists())
      {
        assertTrue(testI.exists());

        final InputStream is = new FileInputStream(testI);

        final Layers tLayers = new Layers();

        final ImportNMEA importer = new ImportNMEA(tLayers);
        importer.importThis(testFile, is, 0l, 0l);

        assertEquals("got tracks", 416, tLayers.size());
      }

      // TODO: also test that we use correct sample frequency - though that's prob best done on a
      // smaller file.
    }

    @SuppressWarnings(
    {"deprecation"})
    public void testKnownImport()
    {
      final String test1 =
          "$POSL,CONTACT,OC,DR,CHARLIE NAME,CHARLIE NAME,13.0,254.6,T,20160720,082807.345,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,3409.5794,N,01537.3128,W,0,,,*5D";
      final String test2 = "$POSL,VNM,HMS NONSUCH*03";
      final String test3 =
          "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41";
      final String test4 = "$POSL,DZA,20160720,000000.859,0007328229*42";
      final String test5 =
          "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,010059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E";
      final String test6 =
          "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C";
      final String test7 =
          "$POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06";
      final String test8 = "$POSL,PDS,9.2,M*03";
      final String test9 = "$POSL,VEL,GPS,276.3,4.6,,,*35";

      assertEquals("Tgt POS", MsgType.CONTACT, parseType(test1));
      assertEquals("Vessel name", MsgType.VESSEL_NAME, parseType(test2));
      assertEquals("OS POS", MsgType.OS_POS, parseType(test3));
      assertEquals("Timestamp", MsgType.TIMESTAMP, parseType(test4));

      // ok, let's try the ownship name
      assertEquals("got name", "HMS NONSUCH", parseMyName(test2));

      // and the AIS track fields
      final State aisState1 = parseContact(test1);
      assertNotNull("found state", aisState1);
      assertEquals("got name", "CHARLIE NAME", aisState1.name);
      assertNotNull("found date", aisState1.date);
      assertEquals("got date", "20 Jul 2016 08:28:07 GMT", aisState1.date
          .toGMTString());
      assertNotNull("found location", aisState1.location);
      assertEquals("got lat", 34.1596, aisState1.location.getLat(), 0.001);
      assertEquals("got long", -15.622, aisState1.location.getLong(), 0.001);

      // and the AIS track fields
      final State aisState = parseContact(test5);
      assertNotNull("found state", aisState);
      assertEquals("got name", "AIS 5", aisState.name);

      // and the ownship track fields
      final State oState1 = parseOwnship(test3, "test_name");
      assertNotNull("found state", oState1);
      assertEquals("got name", "test_name_POS_GPS", oState1.name);
      assertNull("found date", oState1.date);
      assertNotNull("found location", oState1.location);
      assertEquals("got lat", 11.370, oState1.location.getLat(), 0.001);
      assertEquals("got long", -7.211, oState1.location.getLong(), 0.001);

      // and the ownship track fields
      final State oState2 = parseOwnship(test6, "test_name2");
      assertNotNull("found state", oState2);
      assertEquals("got name", "test_name2_POS2_GPS", oState2.name);
      assertNull("found date", oState2.date);
      assertNotNull("found location", oState2.location);
      assertEquals("got lat", 44.368, oState2.location.getLat(), 0.001);
      assertEquals("got long", -8.201, oState2.location.getLong(), 0.001);

      // and the ownship track fields
      final State oState3 = parseAIS(test7);
      assertNotNull("found state", oState3);
      assertEquals("got name", "564166000", oState3.name);
      assertNull("found date", oState3.date);
      assertNotNull("found location", oState3.location);

      // ok, let's try the ownship name
      assertEquals("got time", "20 Jul 2016 00:00:00 GMT", parseMyDate(test4)
          .toGMTString());

      assertEquals("got depth", 9.2d, parseMyDepth(test8), 0.001);

      assertEquals("got course", 276.3d, parseMyCourse(test9), 0.001);
      assertEquals("got speed", 4.6d, parseMySpeed(test9), 0.001);
    }
  }

  /**
   * $POSL,AIS,564166000,1212.1234,N,12312.1234,W,0,7.8,327.9,0,330.0,AIS1,0,0*06
   */
  final private static Pattern aisPattern =
      Pattern
          .compile("\\$POSL,AIS,(?<MMSI>\\d+?),"
              + "(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*,AIS1,.*");

  /**
   * $POSL,CONTACT,OC,DELETE,AIS 5,AIS
   * 5,1.0,125.3,T,20160720,010059.897,FS,SFSP------^2a^2a^2a^2a^2a
   * ,0.0,M,1212.1313,N,12312.1234,W,0,,,*6E"
   */
  final private static Pattern contactPattern = Pattern
      .compile("\\$POSL,CONTACT,OC,\\w*,(?<NAME>.*?),.*"
          + ",(?<DATE>\\d{8}),(?<TIME>\\d{6}.\\d{3}),.*,"
          + "(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*");
  /**
   * $POSL,VEL,GPS,276.3,4.6,,,*35
   */
  final private static Pattern coursePattern = Pattern
      .compile("\\$POSL,VEL,GPS,(?<COURSE>\\d+.\\d+),.*");

  /**
   * $POSL,DZA,20160720,000000.859,0007328229*42
   */
  final private static Pattern datePattern = Pattern
      .compile("\\$POSL,DZA,(?<DATE>\\d{8}),(?<TIME>\\d{6}.\\d{3}),.*");

  /**
   * $POSL,PDS,9.2,M*0
   */
  final private static Pattern depthPattern = Pattern
      .compile("\\$POSL,PDS,(?<DEPTH>\\d+.\\d+),.*");
  /**
   * "$POSL,VNM,HMS NONSUCH*03";
   */
  final private static Pattern namePattern = Pattern
      .compile("\\$POSL,VNM,(?<NAME>.*)\\*\\d\\d");
  /**
   * $POSL,VEL,GPS,276.3,4.6,,,*35
   */
  final private static Pattern speedPattern = Pattern
      .compile("\\$POSL,VEL,GPS,.*,(?<SPEED>\\d+.\\d+),.*");

  /**
   * "$POSL,POS,GPS,1122.2222,N,12312.1234,W,0.00,,Center of Rotation,N,,,,,*41";
   */
  final private static Pattern osPattern =
      Pattern
          .compile("\\$POSL,(?<SOURCE>\\w?POS\\d?,.*),(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*");

  final private static Pattern typePattern = Pattern
      .compile("\\$POSL,(?<TYPE1>\\w*),(?<TYPE2>\\w*),*.*");

  private static double degsFor(final String text)
  {
    final int dec = text.indexOf(".");
    final String degs = text.substring(0, dec - 2);
    final String mins = text.substring(dec - 2, text.length() - 2);
    final String hemi = text.substring(text.length() - 1);
    double res = Double.valueOf(degs) + Double.valueOf(mins) / 60d;
    if (hemi.equals("S") | hemi.equals("W"))
    {
      // switch the direction
      res = -res;
    }

    return res;
  }

  private static FixWrapper fixFor(final Date date, final State state,
      final FixWrapper lastFix, final Double myDepth)
  {
    // set the depth, if we have it
    if (myDepth != null)
    {
      state.location.setDepth(myDepth);
    }

    final Fix theF = new Fix(new HiResDate(date), state.location, 0d, 0d);
    final FixWrapper fw = new FixWrapper(theF);

    if (lastFix != null)
    {
      // have a go at the course & speed
      final WorldVector diff = state.location.subtract(lastFix.getLocation());

      // first the course
      fw.getFix().setCourse(diff.getBearing());

      final double m_travelled =
          new WorldDistance(diff.getRange(), WorldDistance.DEGS)
              .getValueIn(WorldDistance.METRES);
      final double timeDiffMillis =
          (date.getTime() - lastFix.getDTG().getDate().getTime());
      final double speed_m_s = m_travelled / (timeDiffMillis / 1000d);
      final WorldSpeed theSpeed = new WorldSpeed(speed_m_s, WorldSpeed.M_sec);

      // and the speed
      final double speed_yps = theSpeed.getValueIn(WorldSpeed.ft_sec) / 3d;
      fw.getFix().setSpeed(speed_yps);

    }

    return fw;
  }

  static private State parseAIS(final String nmea_sentence)
  {

    final Matcher m = aisPattern.matcher(nmea_sentence);
    final State res;
    if (m.matches())
    {
      final String name = m.group("MMSI");
      final String tLat = m.group("LAT");
      final String tLong = m.group("LONG");
      res = new State(name, null, null, tLat, tLong);
    }
    else
    {
      res = null;
    }

    return res;
  }

  static private State parseContact(final String nmea_sentence)
  {

    final Matcher m = contactPattern.matcher(nmea_sentence);
    final State res;
    if (m.matches())
    {
      final String name = m.group("NAME");
      final String dateStr = m.group("DATE");
      final String timeStr = m.group("TIME");
      final String tLat = m.group("LAT");
      final String tLong = m.group("LONG");
      res = new State(name, dateStr, timeStr, tLat, tLong);
    }
    else
    {
      res = null;
    }

    return res;
  }

  static private double parseMyCourse(final String nmea_sentence)
  {
    final Matcher m = coursePattern.matcher(nmea_sentence);
    final double res;
    if (m.matches())
    {
      res = Double.parseDouble(m.group("COURSE"));
    }
    else
    {
      res = 0d;
    }
    return res;
  }

  private static Date parseMyDate(final String nmea_sentence)
  {
    final Matcher m = datePattern.matcher(nmea_sentence);
    final Date res;
    if (m.matches())
    {
      final String dateStr = m.group("DATE");
      final String timeStr = m.group("TIME");
      res = State.dateFor(dateStr, timeStr);
    }
    else
    {
      res = null;
    }

    return res;
  }

  static private double parseMyDepth(final String nmea_sentence)
  {

    final Matcher m = depthPattern.matcher(nmea_sentence);
    final double res;
    if (m.matches())
    {
      res = Double.parseDouble(m.group("DEPTH"));
    }
    else
    {
      res = 0d;
    }
    return res;
  }

  static private String parseMyName(final String nmea_sentence)
  {

    final Matcher m = namePattern.matcher(nmea_sentence);
    final String res;
    if (m.matches())
    {
      res = m.group("NAME");
    }
    else
    {
      res = null;
    }
    return res;
  }

  static private double parseMySpeed(final String nmea_sentence)
  {
    final Matcher m = speedPattern.matcher(nmea_sentence);
    final double res;
    if (m.matches())
    {
      res = Double.parseDouble(m.group("SPEED"));
    }
    else
    {
      res = 0d;
    }
    return res;
  }

  static private State parseOwnship(final String nmea_sentence,
      final String myName)
  {
    final Matcher m = osPattern.matcher(nmea_sentence);
    final State res;
    if (m.matches())
    {
      final String tLat = m.group("LAT");
      final String tLong = m.group("LONG");
      final String source = m.group("SOURCE").replace(",", "_");
      res = new State(myName + "_" + source, null, null, tLat, tLong);
    }
    else
    {
      res = null;
    }

    return res;

  }

  static private MsgType parseType(final String nmea_sentence)
  {
    final Matcher m = typePattern.matcher(nmea_sentence);
    final MsgType res;
    if (m.matches())
    {
      final String str = m.group("TYPE1");
      final String str2 = m.group("TYPE2");
      if (str.equals("VNM"))
        res = MsgType.VESSEL_NAME;
      else if (str.equals("POS") && str2.equals("GPS"))
        res = MsgType.OS_POS;
      else if (str.contains("VEL") && str2.equals("GPS"))
        res = MsgType.OS_COURSE_SPEED;
      else if (str.equals("CONTACT"))
        res = MsgType.CONTACT;
      else if (str.equals("AIS"))
        res = MsgType.AIS;
      else if (str.equals("DZA"))
        res = MsgType.TIMESTAMP;
      else if (str.equals("PDS"))
        res = MsgType.OS_DEPTH;
      else
        res = MsgType.UNKNOWN;
    }
    else
    {
      res = MsgType.UNKNOWN;
    }
    return res;
  }

  /**
   * where we write our data
   * 
   */
  private final Layers _layers;

  /**
   * the set of tracks we build up, to reduce screen updates
   * 
   */
  HashMap<String, ArrayList<FixWrapper>> tracks =
      new HashMap<String, ArrayList<FixWrapper>>();

  /**
   * the set of tracks we build up, to reduce screen updates
   * 
   */
  HashMap<String, Color> colors = new HashMap<String, Color>();

  public ImportNMEA(final Layers target)
  {
    super();
    _layers = target;
  }

  public void importThis(final String fName, final InputStream is,
      final long osFreq, final long aisFreq) throws Exception
  {
    String myName = null;
    double myDepth = 0d;
    Date date = null;

    final boolean importOS = !(osFreq == Long.MAX_VALUE);
    final boolean importAIS = !(aisFreq == Long.MAX_VALUE);
    final boolean importContacts = false;

    // reset our list of tracks
    tracks.clear();
    colors.clear();

    // remember the first ownship location, for the DR track
    WorldLocation origin = null;

    // ok, loop through the lines
    final BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String nmea_sentence;

    int ctr = 0;

    // loop through the lines
    while ((nmea_sentence = br.readLine()) != null)
    {

      final MsgType msg = parseType(nmea_sentence);

      ctr++;

      if (ctr % 10000 == 0)
      {
        System.out.print(".");
      }
      if (ctr % 50000 == 0)
      {
        System.out.println("");
        System.out.print(ctr);
      }

      switch (msg)
      {
      case TIMESTAMP:
        // ok, extract the rest of the body
        date = parseMyDate(nmea_sentence);

        // and remember the name
        break;
      case VESSEL_NAME:
        // ok, extract the rest of the body
        myName = parseMyName(nmea_sentence);
        break;
      case OS_DEPTH:
        if (importOS)
        {
          // ok, extract the rest of the body
          myDepth = parseMyDepth(nmea_sentence);
        }
        break;
      case OS_COURSE_SPEED:
        if (importOS)
        {
          // ok, extract the rest of the body
          final double myCourseDegs = parseMyCourse(nmea_sentence);
          final double mySpeedKts = parseMySpeed(nmea_sentence);

          // do we know our origin?
          if (origin != null)
          {
            // ok, grow the DR track
            storeDRFix(origin, myCourseDegs, mySpeedKts, date, myName, myDepth,
                DebriefColors.PURPLE);
          }
        }
        break;
      case OS_POS:
        if (importOS)
        {
          // note: if we don't know ownship name yet,
          // let's make one up
          if (myName == null)
          {
            myName = WECDIS_OWNSHIP_PREFIX;
          }

          // extract the location
          final State state = parseOwnship(nmea_sentence, myName);

          // do we need an origin?
          if (origin == null)
          {
            origin = new WorldLocation(state.location);
          }

          // do we know our name yet?
          if (state != null && date != null)
          {
            // now store the ownship location
            storeLocation(date, state, osFreq, DebriefColors.BLUE, myDepth);
          }
        }

        break;
      case CONTACT:
        if (importContacts)
        {
          // extract the location
          final State hisState = parseContact(nmea_sentence);

          if (hisState == null)
          {
            System.out.println("INVALID CONTACT");
          }
          else
          {
            // now store the ownship location
            storeLocation(hisState.date, hisState, aisFreq,
                DebriefColors.GREEN, null);
          }
        }
        break;
      case AIS:
        if (importAIS)
        {
          // extract the location
          final State hisState = parseAIS(nmea_sentence);

          if (hisState == null)
          {
            // ok, it was prob the "other" AIS receiver
          }
          else
          {
            if (date != null)
            {
              // now store the ownship location
              storeLocation(date, hisState, aisFreq, DebriefColors.YELLOW, null);
            }
          }
        }
        break;
      case UNKNOWN:
        break;
      }
    }

    for (final String trackName : tracks.keySet())
    {
      final ArrayList<FixWrapper> track = tracks.get(trackName);
      // ok, build the track
      final TrackWrapper tr = new TrackWrapper();
      tr.setName(trackName);
      tr.setColor(colors.get(trackName));

      System.out.println("storing " + track.size() + " for " + trackName);

      // SPECIAL HANDLING - we filter DR tracks at this stage
      Long lastTime = null;
      final boolean resample = trackName.endsWith("-DR");

      for (final FixWrapper fix : track)
      {
        // ok, also do the label
        fix.resetName();

        final long thisTime = fix.getDateTimeGroup().getDate().getTime();

        long delta = Long.MAX_VALUE;

        if (resample && lastTime != null)
        {
          delta = thisTime - lastTime;
        }

        if ((!resample) || lastTime == null || delta >= osFreq)
        {
          tr.add(fix);
          lastTime = thisTime;
        }
      }

      _layers.addThisLayer(tr);
    }

  }

  private void storeDRFix(final WorldLocation origin,
      final double myCourseDegs, final double mySpeedKts, final Date date,
      final String myName, final double myDepth, final Color color)
  {
    final String trackName = myName + "-DR";

    // find the track
    ArrayList<FixWrapper> track = tracks.get(trackName);

    final FixWrapper newFix;

    // do we have any?
    if (track == null)
    {
      track = new ArrayList<FixWrapper>();
      tracks.put(trackName, track);
      colors.put(trackName, color);

      // nope, create the origin
      final Fix fix =
          new Fix(new HiResDate(date.getTime()), origin, Math
              .toRadians(myCourseDegs), MWC.Algorithms.Conversions
              .Kts2Yps(mySpeedKts));
      newFix = new FixWrapper(fix);
    }
    else
    {
      // ok, get the last point
      final FixWrapper lastFix = track.get(track.size() - 1);

      // now calculate the new point
      final long timeDelta =
          date.getTime() - lastFix.getDateTimeGroup().getDate().getTime();

      // calculate the distance travelled
      final double m_s =
          new WorldSpeed(mySpeedKts, WorldSpeed.Kts)
              .getValueIn(WorldSpeed.M_sec);
      final double distanceM = m_s * timeDelta / 1000d;

      final double distanceDegs =
          new WorldDistance(distanceM, WorldDistance.METRES)
              .getValueIn(WorldDistance.DEGS);
      final WorldVector offset =
          new WorldVector(Math.toRadians(myCourseDegs), distanceDegs, 0);

      final WorldLocation newLoc = lastFix.getLocation().add(offset);

      final Fix fix =
          new Fix(new HiResDate(date.getTime()), newLoc, Math
              .toRadians(myCourseDegs), MWC.Algorithms.Conversions
              .Kts2Yps(mySpeedKts));
      newFix = new FixWrapper(fix);
      
      // no, don't set the color, we want the fix to take
      // the color of the parent track
      // newFix.setColor(color);
    }

    track.add(newFix);

  }

  private void storeLocation(final Date date, final State state,
      final long freq, final Color color, final Double myDepth)
  {
    final String myName = state.name;
    ArrayList<FixWrapper> track = tracks.get(myName);

    final boolean addIt;

    final FixWrapper lastFix;

    if (track == null)
    {
      track = new ArrayList<FixWrapper>();
      tracks.put(myName, track);
      colors.put(myName, color);

      // ok. we're certainly adding this one
      addIt = true;

      lastFix = null;
    }
    else
    {
      // find the time of the last fix stored
      lastFix = track.get(track.size() - 1);
      final long lastTime = lastFix.getDateTimeGroup().getDate().getTime();

      // have we passed the indicated frequency?
      if (date.getTime() >= lastTime + freq)
      {
        addIt = true;
      }
      else
      {
        addIt = false;
      }
    }

    if (addIt)
    {
      // create a fix from the state
      final FixWrapper theF = fixFor(date, state, lastFix, myDepth);

      // and store it
      track.add(theF);
    }
  }
}
