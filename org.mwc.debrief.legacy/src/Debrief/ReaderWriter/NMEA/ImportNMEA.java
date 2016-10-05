package Debrief.ReaderWriter.NMEA;

import java.awt.Color;
import java.io.BufferedReader;
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
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class ImportNMEA
{

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

  private enum MsgType
  {
    VESSEL_NAME, OS_POS, CONTACT, TIMESTAMP, UNKNOWN, AIS, OS_DEPTH;
  }

  private static class State
  {
    static SimpleDateFormat sdf;

    public final Date date;
    public final String name;
    public final WorldLocation location;

    public State(String name, String dateStr, String timeStr, String tLat,
        String tLong)
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

    private WorldLocation locationFor(String tLat, String tLong)
    {
      // 3409.5794,N 01537.3128,W
      final double dLat = degsFor(tLat);
      final double dLong = degsFor(tLong);
      final WorldLocation loc = new WorldLocation(dLat, dLong, 0);
      return loc;
    }

    private static Date dateFor(String dateStr, String timeStr)
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
      catch (ParseException e)
      {
        Application.logError2(Application.ERROR,
            "AIS importer failed to parse:" + dateStr + ", " + timeStr, e);
      }
      return res;
    }
  }

  public void importThis(final String fName, final InputStream is)
      throws Exception
  {
    String myName = null;
    double myDepth = 0d;
    Date date = null;

    final long OS_FREQ = 1000 * 5;
    final long AIS_FREQ = 1000 * 60;

    final boolean importOS = true;
    final boolean importAIS = true;
    final boolean importContacts = true;

    // reset our list of tracks
    tracks.clear();
    colors.clear();

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

        // and remember the name
        break;
      case OS_DEPTH:
        // ok, extract the rest of the body
        myDepth = parseMyDepth(nmea_sentence);

        // and remember the name
        break;
      case OS_POS:
        if (importOS)
        {
          // note: if we don't know ownship name yet,
          // let's make one up
          if (myName == null)
          {
            myName = "AIS_OWNSHIP";
          }

          // extract the location
          final State state = parseOwnship(nmea_sentence, myName);

          // do we know our name yet?
          if (state != null && date != null)
          {
            // now store the ownship location
            storeLocation(date, state, OS_FREQ, DebriefColors.BLUE, myDepth);
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
            storeLocation(hisState.date, hisState, AIS_FREQ,
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
              storeLocation(date, hisState, AIS_FREQ, DebriefColors.YELLOW, null);
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
      TrackWrapper tr = new TrackWrapper();
      tr.setName(trackName);
      tr.setColor(colors.get(trackName));

      System.out.println("storing " + track.size() + " for " + trackName);

      for (final FixWrapper fix : track)
      {
        // ok, also do the label
        fix.resetName();

        tr.add(fix);
      }

      _layers.addThisLayer(tr);
    }

  }

  static private MsgType parseType(String nmea_sentence)
  {
    Matcher m =
        Pattern.compile("\\$POSL,(?<TYPE1>\\w*),(?<TYPE2>\\w*),*.*").matcher(nmea_sentence);
    final MsgType res;
    if (m.matches())
    {
      String str = m.group("TYPE1");
      String str2 = m.group("TYPE2");
      if (str.equals("VNM"))
        res = MsgType.VESSEL_NAME;
      else if (str.equals("POS") && str2.equals("GPS"))
        res = MsgType.OS_POS;
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

  static private State parseContact(String nmea_sentence)
  {
    // $POSL,CONTACT,OC,DELETE,AIS 5,AIS
    // 5,1.0,125.3,T,20160720,010059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,3545.5390,N,00542.7723,W,0,,,*6E"

    Matcher m =
        Pattern
            .compile(
                "\\$POSL,CONTACT,OC,\\w*,(?<NAME>.*?),.*"
                    + ",(?<DATE>\\d{8}),(?<TIME>\\d{6}.\\d{3}),.*,"
                    + "(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*")
            .matcher(nmea_sentence);
    final State res;
    if (m.matches())
    {
      String name = m.group("NAME");
      String dateStr = m.group("DATE");
      String timeStr = m.group("TIME");
      String tLat = m.group("LAT");
      String tLong = m.group("LONG");
      res = new State(name, dateStr, timeStr, tLat, tLong);
    }
    else
    {
      res = null;
    }

    return res;
  }

  static private State parseAIS(String nmea_sentence)
  {

    // $POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06

    Matcher m =
        Pattern
            .compile(
                "\\$POSL,AIS,(?<MMSI>\\d+?),"
                    + "(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*,AIS1,.*")
            .matcher(nmea_sentence);
    final State res;
    if (m.matches())
    {
      String name = m.group("MMSI");
      String tLat = m.group("LAT");
      String tLong = m.group("LONG");
      res = new State(name, null, null, tLat, tLong);
    }
    else
    {
      res = null;
    }

    return res;
  }

  private static Date parseMyDate(String nmea_sentence)
  {
    // $POSL,DZA,20160720,000000.859,0007328229*42
    Matcher m =
        Pattern
            .compile("\\$POSL,DZA,(?<DATE>\\d{8}),(?<TIME>\\d{6}.\\d{3}),.*")
            .matcher(nmea_sentence);
    final Date res;
    if (m.matches())
    {
      String dateStr = m.group("DATE");
      String timeStr = m.group("TIME");
      res = State.dateFor(dateStr, timeStr);
    }
    else
    {
      res = null;
    }

    return res;
  }

  private static double degsFor(String text)
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

  private void storeLocation(final Date date, final State state,
      final long freq, final Color color, Double myDepth)
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
      long lastTime = lastFix.getDateTimeGroup().getDate().getTime();

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
      FixWrapper theF = fixFor(date, state, lastFix, myDepth);

      // and store it
      track.add(theF);
    }
  }

  private static FixWrapper fixFor(Date date, State state, FixWrapper lastFix, Double myDepth)
  {
    // set the depth, if we have it
    if(myDepth != null)
    {
      state.location.setDepth(myDepth);
    }
    
    Fix theF = new Fix(new HiResDate(date), state.location, 0d, 0d);
    FixWrapper fw = new FixWrapper(theF);

    if (lastFix != null)
    {
      // have a go at the course & speed
      WorldVector diff = state.location.subtract(lastFix.getLocation());

      // first the course
      fw.getFix().setCourse(diff.getBearing());

      double m_travelled =
          new WorldDistance(diff.getRange(), WorldDistance.DEGS)
              .getValueIn(WorldDistance.METRES);
      double timeDiffMillis =
          (date.getTime() - lastFix.getDTG().getDate().getTime());
      double speed_m_s = m_travelled / (timeDiffMillis / 1000d);
      WorldSpeed theSpeed = new WorldSpeed(speed_m_s, WorldSpeed.M_sec);

      // and the speed
      double speed_yps = theSpeed.getValueIn(WorldSpeed.ft_sec) / 3d;
      fw.getFix().setSpeed(speed_yps);

    }

    return fw;
  }

  static private State parseOwnship(String nmea_sentence, String myName)
  {
    // "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41";
    Matcher m =
        Pattern
            .compile(
                "\\$POSL,(?<SOURCE>\\w?POS\\d?,.*),(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*")
            .matcher(nmea_sentence);
    final State res;
    if (m.matches())
    {
      String tLat = m.group("LAT");
      String tLong = m.group("LONG");
      String source = m.group("SOURCE").replace(",", "_");
      res = new State(myName + "_" + source, null, null, tLat, tLong);
    }
    else
    {
      res = null;
    }

    return res;

  }


  static private double parseMyDepth(String nmea_sentence)
  {
    // $POSL,PDS,9.2,M*0

    Matcher m =
        Pattern.compile("\\$POSL,PDS,(?<DEPTH>\\d+.\\d+),.*").matcher(
            nmea_sentence);
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
  
  static private String parseMyName(String nmea_sentence)
  {
    // "$POSL,VNM,HMS NONSUCH*03";

    Matcher m =
        Pattern.compile("\\$POSL,VNM,(?<NAME>.*)\\*\\d\\d").matcher(
            nmea_sentence);
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

  //
  // @SuppressWarnings("deprecation")
  // private void processQueuedPositions(final Timestamp lastTime)
  // {
  //
  // // anything to process?
  // if (_queuedFixes.isEmpty())
  // return;
  //
  // // we need the seconds from the timestamp
  // final int lastSecs = lastTime.getSeconds();
  //
  // // loop through the pending fixes
  // final Iterator<FixWrapper> iter = _queuedFixes.iterator();
  // while (iter.hasNext())
  // {
  // final FixWrapper fix = iter.next();
  //
  // // what is the seconds for the recorded position?
  // final int newSecs = fix.getTime().getDate().getSeconds();
  //
  // // build the new date
  // final Date newDate = new Date(lastTime.getTime());
  //
  // // is this less than the queued secs
  // if (isPreviousMinute(lastSecs, newSecs))
  // {
  // // ok, we have to decrement the minutes
  // newDate.setMinutes(newDate.getMinutes() - 1);
  // }
  // else if (isNextMinute(lastSecs, newSecs))
  // {
  // // ok, we have to increment the minutes
  // newDate.setMinutes(newDate.getMinutes() + 1);
  // }
  //
  // // and the seconds
  // newDate.setSeconds(newSecs);
  //
  // // and store it
  // fix.getFix().setTime(new HiResDate(newDate));
  //
  // // ok, find the track
  // final String parentName = nameFor(Integer.valueOf(fix.getLabel()));
  // final Layer parent = _layers.findLayer(parentName);
  //
  // // cool, now store it
  // parent.add(fix);
  //
  // // ok, we've used the name value that was sneaked into
  // // the label, now we can override it
  // fix.resetName();
  // }
  //
  // // done, clear the list
  // _queuedFixes.clear();
  // }
  //
  // @SuppressWarnings("deprecation")
  // private void storeThis(final double latitude, final double longitude,
  // final double cog, final double sog, final int mmsi, final int secs,
  // final Timestamp lastTime)
  // {
  // // try to do a name lookup
  // final String layerName = nameFor(mmsi);
  //
  // // does this track exist?
  // Layer layer = _layers.findLayer(layerName);
  // if (layer == null)
  // {
  // // nope, better create it then
  // final TrackWrapper tw = new TrackWrapper();
  // tw.setColor(new Color(188, 93, 6));
  // layer = tw;
  // layer.setName(layerName);
  // _layers.addThisLayer(layer);
  // }
  //
  // // determine what date value to use for this new position
  // final Date newDate;
  //
  // // do we have a base timestamp?
  // if (lastTime != null)
  // {
  // // ok, extract the time
  // final long theTime = lastTime.getTime();
  // newDate = new Date(theTime);
  //
  // // store the new value of seconds
  // newDate.setSeconds(secs);
  //
  // // should this new point be from the previous, or next minute?
  // if (isPreviousMinute(lastTime.getSeconds(), secs))
  // {
  // newDate.setMinutes(newDate.getMinutes() - 1);
  // }
  // else if (isNextMinute(lastTime.getSeconds(), secs))
  // {
  // newDate.setMinutes(newDate.getMinutes() + 1);
  // }
  // }
  // else
  // {
  // // no existing timestamp - just safely store the seconds
  // newDate = new Date(secs * 1000);
  // newDate.setSeconds(secs);
  // }
  //
  // // ok - now we can create the time value
  // final HiResDate hDate = new HiResDate(newDate);
  //
  // // now collate the other fix-related data
  // final WorldLocation theLocation = new WorldLocation(latitude, longitude, 0);
  // final double theCourseRads = Math.toRadians(cog);
  // final double theSpeedYps =
  // new WorldSpeed(sog, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3d;
  // // ok, now add the position
  // final Fix newFix = new Fix(hDate, theLocation, theCourseRads, theSpeedYps);
  // final FixWrapper fixWrapper = new FixWrapper(newFix);
  //
  // // ok, do we have a time offset yet? if we don't we should queue up this fix
  // if (lastTime == null)
  // {
  // // no previous time, let's sneak the track name into the label
  // fixWrapper.setLabel("" + mmsi);
  //
  // // and remember the fix, for later procssing
  // _queuedFixes.add(fixWrapper);
  // }
  // else
  // {
  // // that's all easy then. Remember to reset the time label
  // fixWrapper.resetName();
  //
  // // and store it in the parent.
  // layer.add(fixWrapper);
  // }
  //
  // }

  public static class TestImportAIS extends TestCase
  {
    public void testFullImport() throws Exception
    {
      testImport("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/20160720.log");
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

      WorldLocation newLoc =
          new WorldLocation(degsFor("3614.3708,N"), degsFor("00241.5907,E"), 0);
      assertEquals("right location", " 36°14'22.25\"N 002°41'35.44\"E ", newLoc
          .toString());
    }

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

    public void testImport(final String testFile) throws Exception
    {
//      final File testI = new File(testFile);
//      assertTrue(testI.exists());

      // skip the remaining tests, we don't have test data available
//      final InputStream is = new FileInputStream(testI);
//
//      final Layers tLayers = new Layers();
//
//      final ImportNMEA importer = new ImportNMEA(tLayers);
//      importer.importThis(testFile, is);
//
//      assertEquals("got tracks", 26, tLayers.size());
    }

    @SuppressWarnings(
    {"deprecation"})
    public void testKnownImport()
    {
      final String contactTrack =
          "$POSL,CONTACT,OC,DR,CHARLIE NAME,CHARLIE NAME,13.0,254.6,T,20160720,082807.345,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,3409.5432,N,01537.2345,W,0,,,*5D";
      final String testMyName = "$POSL,VNM,HMS NONSUCH*03";
      final String ownshipTrack =
          "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41";
      final String dateStr = "$POSL,DZA,20160720,000000.859,0007328229*42";
      final String test5 =
          "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,010059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,3545.4321,N,00542.4321,W,0,,,*6E";
      final String test6 =
          "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C";
      final String aisTrack =
          "$POSL,AIS,564166000,3612.1234,N,00512.1234,W,0,7.8,327.9,0,330.0,AIS1,0,0*06";
      final String depthStr =
          "$POSL,PDS,9.2,M*03";

      assertEquals("Tgt POS", MsgType.CONTACT, parseType(contactTrack));
      assertEquals("Vessel name", MsgType.VESSEL_NAME, parseType(testMyName));
      assertEquals("OS POS", MsgType.OS_POS, parseType(ownshipTrack));
      assertEquals("Timestamp", MsgType.TIMESTAMP, parseType(dateStr));

      // ok, let's try the ownship name
      assertEquals("got name", "HMS NONSUCH", parseMyName(testMyName));

      // and the AIS track fields
      State contactState = parseContact(contactTrack);
      assertNotNull("found state", contactState);
      assertEquals("got name", "CHARLIE NAME", contactState.name);
      assertNotNull("found date", contactState.date);
      assertEquals("got date", "20 Jul 2016 08:28:07 GMT", contactState.date
          .toGMTString());
      assertNotNull("found location", contactState.location);
      assertEquals("got lat", 34.1596, contactState.location.getLat(), 0.001);
      assertEquals("got long", -15.62057, contactState.location.getLong(), 0.001);

      // and the AIS track fields
      State contactState2 = parseContact(test5);
      assertNotNull("found state", contactState2);
      assertEquals("got name", "AIS 5", contactState2.name);

      // and the ownship track fields
      State oState1 = parseOwnship(ownshipTrack, "test_name");
      assertNotNull("found state", oState1);
      assertEquals("got name", "test_name_POS_GPS", oState1.name);
      assertNull("found date", oState1.date);
      assertNotNull("found location", oState1.location);
      assertEquals("got lat", 11.370, oState1.location.getLat(), 0.001);
      assertEquals("got long", -7.211, oState1.location.getLong(), 0.001);

      // and the ownship track fields
      State oState2 = parseOwnship(test6, "test_name2");
      assertNotNull("found state", oState2);
      assertEquals("got name", "test_name2_POS2_GPS", oState2.name);
      assertNull("found date", oState2.date);
      assertNotNull("found location", oState2.location);
      assertEquals("got lat", 44.368, oState2.location.getLat(), 0.001);
      assertEquals("got long", -8.201, oState2.location.getLong(), 0.001);

      // and the ownship track fields
      State aisState = parseAIS(aisTrack);
      assertNotNull("found state", aisState);
      assertEquals("got name", "564166000", aisState.name);
      assertNull("found date", aisState.date);
      assertNotNull("found location", aisState.location);

      // ok, let's try the ownship name
      assertEquals("got time", "20 Jul 2016 00:00:00 GMT", parseMyDate(dateStr)
          .toGMTString());

      assertEquals("got depth", 9.2d, parseMyDepth(depthStr), 0.001);

    }

    // public void testNewImport() throws Exception
    // {
    // String testFile =
    // "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0914.txt";
    // File testI = new File(testFile);
    // assertTrue(testI.exists());
    //
    // InputStream is = new FileInputStream(testI);
    //
    // final Layers tLayers = new Layers();
    //
    // final ImportNMEA_AIS importer = new ImportNMEA_AIS(tLayers);
    // importer.importThis(testFile, is);
    //
    // // ok, now for the second file
    // is.close();
    // testFile =
    // "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0924.txt";
    // testI = new File(testFile);
    // assertTrue(testI.exists());
    //
    // is = new FileInputStream(testI);
    // importer.importThis(testFile, is);
    //
    // // hmmm, how many tracks
    // assertEquals("got new tracks", 15, tLayers.size());
    //
    // final TrackWrapper thisT = (TrackWrapper) tLayers.findLayer("LOLLAND");
    // final Enumeration<Editable> fixes = thisT.getPositions();
    // while (fixes.hasMoreElements())
    // {
    // final FixWrapper thisF = (FixWrapper) fixes.nextElement();
    // System.out.println(thisF.getDateTimeGroup().getDate() + " COG:"
    // + (int) Math.toDegrees(thisF.getCourse()) + " SOG:"
    // + (int) thisF.getSpeed() + " loc:" + thisF.getLocation());
    //
    // }
    //
    // }

  }
}
