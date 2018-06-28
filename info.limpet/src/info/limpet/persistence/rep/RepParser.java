package info.limpet.persistence.rep;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocumentBuilder;
import info.limpet.persistence.FileParser;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

public class RepParser extends FileParser
{
  /** parent for helpers that process series of data items 
   * 
   * @author Ian
   *
   */
  abstract private static interface Generator
  {
    /** handle sensor cuts
     * 
     * @author Ian
     *
     */
    public static class CutGenerator implements Generator
    {
      private final LocationDocumentBuilder _origin;
      private final NumberDocumentBuilder _freq;
      private final NumberDocumentBuilder _brg;
      private final NumberDocumentBuilder _brg2;
      private final NumberDocumentBuilder _range;
      private final StringDocumentBuilder _label;
      private final String _name;
    
      public CutGenerator(final String name)
      {
        _name = name;
        // generate the builders
        _origin =
            new LocationDocumentBuilder(name + "-location", null,
                SampleData.MILLIS);
        _brg =
            new NumberDocumentBuilder(name + "-brg", SampleData.DEGREE_ANGLE,
                null, SampleData.MILLIS);
        _brg2 =
            new NumberDocumentBuilder(name + "-brg (ambig)",
                SampleData.DEGREE_ANGLE, null, SampleData.MILLIS);
        _range =
            new NumberDocumentBuilder(name + "-range", METRE, null,
                SampleData.MILLIS);
        _freq =
            new NumberDocumentBuilder(name + "-freq", SI.HERTZ, null,
                SampleData.MILLIS);
        _label =
            new StringDocumentBuilder(name + "-label", null, SampleData.MILLIS);
      }
    
      @Override
      public void add(final Item thisEntry)
      {
        final Item.CutItem cut = (Item.CutItem) thisEntry;
        final long theDate;
        if (cut._dtg != null)
        {
          theDate = cut._dtg.getTime();
        }
        else
        {
          theDate = -1;
        }
    
        if (cut._brg != null)
        {
          _brg.add(theDate, cut._brg);
        }
        if (cut._brg2 != null)
        {
          _brg2.add(theDate, cut._brg2);
        }
        if (cut._rng != null)
        {
          _range.add(theDate, cut._rng);
        }
        if (cut._freq != null)
        {
          _freq.add(theDate, cut._freq);
        }
        if (cut._origin != null)
        {
          _origin.add(theDate, cut._origin);
        }
        if (cut._text != null)
        {
          _label.add(theDate, cut._text);
        }
      }
    
      @Override
      public void storeTo(final StoreGroup group)
      {
        // ok, create group for this track
        final StoreGroup thisData = new StoreGroup(_name);
    
        // now add the constituents
        if (!_origin.getValues().isEmpty())
        {
          thisData.add(_origin.toDocument());
        }
        if (!_brg.getValues().isEmpty())
        {
          thisData.add(_brg.toDocument());
        }
        if (!_brg2.getValues().isEmpty())
        {
          thisData.add(_brg2.toDocument());
        }
        if (!_freq.getValues().isEmpty())
        {
          thisData.add(_freq.toDocument());
        }
        if (!_range.getValues().isEmpty())
        {
          thisData.add(_range.toDocument());
        }
        if (!_label.getValues().isEmpty())
        {
          thisData.add(_label.toDocument());
        }
    
        group.add(thisData);
      }
    }

    /** handle platform positions
     * 
     * @author Ian
     *
     */
    public static class TrackGenerator implements Generator
    {
      final private LocationDocumentBuilder _locB;
      final private NumberDocumentBuilder _speed;
      final private NumberDocumentBuilder _course;
      final private NumberDocumentBuilder _depth;
      final private String _name;
    
      public TrackGenerator(final String name)
      {
        _name = name;
        // generate the builders
        _locB =
            new LocationDocumentBuilder(name + "-location", null,
                SampleData.MILLIS);
        _course =
            new NumberDocumentBuilder(name + "-course",
                SampleData.DEGREE_ANGLE, null, SampleData.MILLIS);
        _speed =
            new NumberDocumentBuilder(name + "-speed", METRE.divide(SECOND)
                .asType(Velocity.class), null, SampleData.MILLIS);
        _depth =
            new NumberDocumentBuilder(name + "-depth", METRE, null,
                SampleData.MILLIS);
      }
    
      @Override
      public void add(final Item thisE)
      {
        final Item.FixItem thisEntry = (Item.FixItem) thisE;
        final long theDate;
        if (thisEntry._date != null)
        {
          theDate = thisEntry._date.getTime();
        }
        else
        {
          theDate = -1;
        }
        _locB.add(theDate, thisEntry._loc);
        _speed.add(theDate, thisEntry._speedMS);
        _course.add(theDate, thisEntry._course);
        _depth.add(theDate, thisEntry._depth);
      }
    
      public boolean isSingleton()
      {
        return _speed.getValues().size() == 1;
      }
    
      @Override
      public void storeTo(final StoreGroup group)
      {
        // ok, separate processing if it's a singleton
        // ok, only add the others if they're present
        if (isSingleton())
        {
          // clear the index units
          _locB.setIndexUnits(null);
          
          // just do the single location
          final LocationDocument locDoc = _locB.toDocument();
          group.add(locDoc);
        }
        else
        {
          // ok, create group for this track
          final StoreGroup thisTrack = new StoreGroup(_name);
    
          // now add the constituents
          thisTrack.add(_locB.toDocument());
          thisTrack.add(_speed.toDocument());
          thisTrack.add(_course.toDocument());
          thisTrack.add(_depth.toDocument());
          group.add(thisTrack);
        }
      }
    }

    public void add(Item item);

    public void storeTo(StoreGroup group);
  }

  /** parent class for stored data item
   * 
   * @author Ian
   *
   */
  private static interface Item
  {
    /** sensor cut data item
     * 
     * @author Ian
     *
     */
    public static class CutItem implements Item
    {
      final private String _host;
      final private String _sensor;
      private final Point2D _origin;
      private final Date _dtg;
      private final Double _brg;
      private final Double _brg2;
      private final Double _freq;
      private final Double _rng;
      private final String _text;

      public CutItem(final String host, final String sensor,
          final Point2D origin, final Date theDtg, final Double brg,
          final Double brg2, final Double freq, final Double rng,
          final String theText)
      {
        _host = host;
        _sensor = sensor;
        _origin = origin;
        _dtg = theDtg;
        _brg = brg;
        _brg2 = brg2;
        _freq = freq;
        _rng = rng;
        _text = theText;
      }

      @Override
      public String getName()
      {
        return _host + "-" + _sensor;
      }

    }

    /** store platform location/state
     * 
     * @author Ian
     *
     */
    public static class FixItem implements Item
    {
      final private Date _date;
      final private Point2D _loc;
      final private double _speedMS;
      final private double _course;
      final private String _name;
      @SuppressWarnings("unused")
      final private String _sym;
      final private double _depth;

      public FixItem(final Date theDate, final Point2D theLoc,
          final double theCourse, final double speedMS, final double depth,
          final String theTrackName, final String symbology)
      {
        _date = theDate;
        _loc = theLoc;
        _course = theCourse;
        _speedMS = speedMS;
        _name = theTrackName;
        _sym = symbology;
        _depth = depth;
      }

      @Override
      public String getName()
      {
        return _name;
      }

    }

    /**
     * get the name to use for this item
     * 
     * @return
     */
    public String getName();
  }

  private static final DateFormat FOUR_DIGIT_YEAR_FORMAT =
      new SimpleDateFormat("yyyyMMdd HHmmss");
  private static final DateFormat TWO_DIGIT_YEAR_FORMAT = new SimpleDateFormat(
      "yyMMdd HHmmss");

  /**
   * the normal token delimiter (for comma & white-space separated fields)
   */
  static final private String normalDelimiters = " \t\n\r\f";

  /**
   * the quoted delimiter, for quoted track names
   */
  static private final String quoteDelimiter = "\"";

  private static String checkForQuotedName(final StringTokenizer st,
      final String nameIn)
  {
    String theName = nameIn;

    // so, does the track name contain a quote character?
    final int quoteIndex = theName.indexOf("\"");
    if (quoteIndex >= 0)
    {
      // aah, but, we may have just read in all of the item. just check if
      // the
      // token contains
      // both speech marks...
      final int secondQuoteIndex = theName.indexOf("\"", quoteIndex + 1);

      if (secondQuoteIndex >= 0)
      {
        // yes, we have caught both quotes
        // just trim off the quote marks
        theName = theName.substring(1, theName.length() - 1);
      }
      else
      {
        // no, we just caught the first quote.
        // fish around for the second one.

        String lastPartOfName = st.nextToken(quoteDelimiter);

        // yup. the ne
        theName += lastPartOfName;

        // and trim away the quote
        theName = theName.substring(theName.indexOf("\"") + 1);

        // consume the trailing quote delimiter (note - we allow spaces
        // & tabs)
        lastPartOfName = st.nextToken(" \t");
      }
    }
    return theName;
  }

  private static String padToken(final String token)
  {
    final String res;
    if (token.length() == 6)
    {
      res = token;
    }
    else
    {
      final int numMissing = 6 - token.length();
      final StringBuffer buffer = new StringBuffer(6);
      for (int i = 0; i < numMissing; i++)
      {
        buffer.append("0");
      }
      buffer.append(token);
      res = buffer.toString();
    }
    return res;
  }

  /**
   * parse a date string using our format
   */
  private static Date parseThis(final String rawText)
  {
    Date date = null;
    Date res = null;

    // right, start off by trimming spaces off the date
    final String theRawText = rawText.trim();

    String secondPart = theRawText;
    String subSecondPart = null;

    // start off by seeing if we have sub-second date
    final int subSecondIndex = theRawText.indexOf('.');
    if (subSecondIndex > 0)
    {
      // so, there is a separator - extract the text before the separator
      secondPart = theRawText.substring(0, subSecondIndex);

      // just check that the '.' isn't the last character
      if (subSecondIndex < theRawText.length() - 1)
      {
        // yes, we do have digits after the separator
        subSecondPart = theRawText.substring(subSecondIndex + 1);
      }
    }

    // next determine if we have a 4-figure year value (in which case the
    // space will be in column 9
    final int spaceIndex = secondPart.indexOf(" ");

    try
    {
      if (spaceIndex > 6)
      {
        date = FOUR_DIGIT_YEAR_FORMAT.parse(secondPart);
      }
      else
      {
        date = TWO_DIGIT_YEAR_FORMAT.parse(secondPart);
      }
    }
    catch (final ParseException e1)
    {
      e1.printStackTrace();
    }

    int millis = 0;

    // do we have a sub-second part?
    if (subSecondPart != null)
    {
      // get the value
      millis = Integer.parseInt(subSecondPart);
    }

    if (millis != -1 && date != null)
    {
      res = new Date(date.getTime() + millis);
    }
    else
    {
      res = date;
    }

    return res;
  }

  /**
   * parse a date string using our format
   */
  private static Date parseThis(final String dateToken, final String timeToken)
  {
    // do we have millis?
    final int decPoint = timeToken.indexOf(".");
    String milliStr;
    String timeStr;
    if (decPoint > 0)
    {
      milliStr = timeToken.substring(decPoint, timeToken.length());
      timeStr = timeToken.substring(0, decPoint);
    }
    else
    {
      milliStr = "";
      timeStr = timeToken;
    }

    // sort out if we have to padd
    // check the date for missing leading zeros
    final String theDateToken = padToken(dateToken);
    timeStr = padToken(timeStr);

    final String composite = theDateToken + " " + timeStr + milliStr;

    return parseThis(composite);
  }

  private static Item.FixItem parseThisRepLine(final String line)
  {
    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(line);

    // declare local variables
    Point2D theLoc;
    double latDeg;
    double longDeg;
    double latMin;
    double longMin;
    char latHem;
    char longHem;
    double latSec;
    double longSec;
    Date theDate = null;
    double theCourse;
    double speedMS;
    double theDepth;

    String theTrackName;

    // check it's not an empty line
    if (!st.hasMoreTokens())
      return null;

    // parse the line
    // 951212 050000.000 CARPET @C 12 11 10.63 N 11 41 52.37 W 269.7 2.0 0

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDate = parseThis(dateToken, timeToken);

    // trouble - the track name may have been quoted, in which case we will
    // pull
    // in the remaining fields aswell
    theTrackName = checkForQuotedName(st, st.nextToken()).trim();

    final String symbology = st.nextToken(normalDelimiters);

    latDeg = Double.parseDouble(st.nextToken());
    latMin = Double.parseDouble(st.nextToken());
    latSec = Double.parseDouble(st.nextToken());

    /**
     * now, we may have trouble here, since there may not be a space between the hemisphere
     * character and a 3-digit latitude value - so BE CAREFUL
     */
    final String vDiff = st.nextToken();
    if (vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      final String secondPart = vDiff.substring(1, vDiff.length());
      longDeg = Double.parseDouble(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Double.parseDouble(st.nextToken());
    }
    longMin = Double.parseDouble(st.nextToken());
    longSec = Double.parseDouble(st.nextToken());
    longHem = st.nextToken().charAt(0);

    // parse (and convert) the vessel status parameters
    theCourse = Double.parseDouble(st.nextToken());
    speedMS = Double.valueOf(st.nextToken()) * 0.514444;

    // get the depth value
    final String depthStr = st.nextToken();

    // we know that the Depth str may be NaN, but Java can interpret this
    // directly
    if ("NaN".equals(depthStr))
      theDepth = Double.NaN;
    else
      theDepth = Double.parseDouble(depthStr);

    // NEW FEATURE: we take any remaining text, and use it as a label
    String txtLabel = null;
    if (st.hasMoreTokens())
      txtLabel = st.nextToken("\r");
    if (txtLabel != null)
      txtLabel = txtLabel.trim();

    // create the tactical data
    theLoc =
        new Point2D.Double(toDegs(longDeg, longMin, longSec, longHem), toDegs(
            latDeg, latMin, latSec, latHem));

    return new Item.FixItem(theDate, theLoc, theCourse, speedMS, theDepth,
        theTrackName, symbology);
  }

  private static double toDegs(final double degs, final double mins,
      final double secs, final char hem)
  {
    double res = degs + mins / 60 + secs / (60 * 60);
    if (hem == 'S' || hem == 'W')
    {
      res = -res;
    }
    return res;
  }

  private Generator builderFor(final Item thisE, final String name)
  {
    final Generator thisG;
    if (thisE instanceof Item.FixItem)
    {
      thisG = new Generator.TrackGenerator(name);
    }
    else if (thisE instanceof Item.CutItem)
    {
      thisG = new Generator.CutGenerator(name);
    }
    else
    {
      thisG = null;
    }

    return thisG;
  }

  private Point2D
      getOptionalOrigin(final StringTokenizer st, final String next)
  {
    final Point2D res;
    // find out if it's our null value
    if (next.startsWith("N"))
    {
      // ditch it,
      res = null;
    }
    else
    {
      double latDeg;
      double longDeg;
      double latMin;
      double longMin;
      char latHem;
      char longHem;
      double latSec;
      double longSec;

      // get the deg out of this value
      latDeg = Double.parseDouble(next);

      // ok, this is valid data, persevere with it
      latMin = Double.parseDouble(st.nextToken());
      latSec = Double.parseDouble(st.nextToken());

      /**
       * now, we may have trouble here, since there may not be a space between the hemisphere
       * character and a 3-digit latitude value - so BE CAREFUL
       */
      final String vDiff = st.nextToken();
      if (vDiff.length() > 3)
      {
        // hmm, they are combined
        latHem = vDiff.charAt(0);
        final String secondPart = vDiff.substring(1, vDiff.length());
        longDeg = Double.parseDouble(secondPart);
      }
      else
      {
        // they are separate, so only the hem is in this one
        latHem = vDiff.charAt(0);
        longDeg = Double.parseDouble(st.nextToken());
      }

      longMin = Double.parseDouble(st.nextToken());
      longSec = Double.parseDouble(st.nextToken());
      longHem = st.nextToken().charAt(0);

      // create the origin
      res =
          new Point2D.Double(toDegs(longDeg, longMin, longSec, longHem),
              toDegs(latDeg, latMin, latSec, latHem));
    } // whether the duff origin data was entered
    return res;
  }

  @Override
  public List<IStoreItem> parse(final String filePath) throws IOException
  {
    final List<IStoreItem> res = new ArrayList<IStoreItem>();
    final File inFile = new File(filePath);
    final Reader in =
        new InputStreamReader(new FileInputStream(inFile), Charset
            .forName("UTF-8"));
    final String fullFileName = inFile.getName();
    final String fileName = filePrefix(fullFileName);
    final StoreGroup group = new StoreGroup(fileName);
    res.add(group);

    final Map<String, Generator> builders = new HashMap<String, Generator>();

    // ok, loop through the data
    BufferedReader br = null;
    try
    {
      br = new BufferedReader(in);
      for (String line; (line = br.readLine()) != null;)
      {
        Item thisE = null;

        // have a look at the line
        if (line.startsWith(";TEXT:"))
        {
          thisE = parseThisLabel(line);
        }
        else if (line.startsWith(";SENSOR2:"))
        {
          thisE = parseThisSensor2(line);
        }
        else if (line.startsWith(";"))
        {
          if(!line.startsWith(";;"))
          {
            System.err.println("Unable to import line:" + line);
          }           
        }
        else
        {
          // ok, it's not a comment. import
          thisE = parseThisRepLine(line);
        }

        if (thisE != null)
        {
          final String name = thisE.getName();

          // do we know this track already?
          Generator thisG = builders.get(name);

          if (thisG == null)
          {
            thisG = builderFor(thisE, name);
            builders.put(name, thisG);
          }

          // ok, submit the new line
          thisG.add(thisE);
        }
      }
    }
    finally
    {
      if (br != null)
      {
        br.close();
      }
    }

    // ok, store handle the data
    for (final String name : builders.keySet())
    {
      final Generator thisGen = builders.get(name);

      thisGen.storeTo(group);
    }

    return res;

  }

  private Item.FixItem parseThisLabel(final String line)
  {
    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(line);

    // declare local variables
    Point2D theLoc;
    double latDeg;
    double longDeg;
    double latMin;
    double longMin;
    char latHem;
    char longHem;
    double latSec;
    double longSec;
    final Date theDate = null;

    String theTrackName = "UNKNOWN";

    // check it's not an empty line
    if (!st.hasMoreTokens())
      return null;

    // parse the line
    // ;TEXT: @E 21.7 0 0 N 21.5 0 0 W test text

    // skip the label
    st.nextToken();

    // skip the symbology
    final String symbology = st.nextToken();

    // now the location
    latDeg = Double.parseDouble(st.nextToken());
    latMin = Double.parseDouble(st.nextToken());
    latSec = Double.parseDouble(st.nextToken());

    /**
     * now, we may have trouble here, since there may not be a space between the hemisphere
     * character and a 3-digit latitude value - so BE CAREFUL
     */
    final String vDiff = st.nextToken();
    if (vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      final String secondPart = vDiff.substring(1, vDiff.length());
      longDeg = Double.parseDouble(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Double.parseDouble(st.nextToken());
    }
    longMin = Double.parseDouble(st.nextToken());
    longSec = Double.parseDouble(st.nextToken());
    longHem = st.nextToken().charAt(0);

    // use the label as the track name
    if (st.hasMoreTokens())
      theTrackName = st.nextToken("\r");
    if (theTrackName != null)
      theTrackName = theTrackName.trim();

    // create the tactical data
    theLoc =
        new Point2D.Double(toDegs(longDeg, longMin, longSec, longHem), toDegs(
            latDeg, latMin, latSec, latHem));

    return new Item.FixItem(theDate, theLoc, 0, 0, 0, theTrackName, symbology);
  }

  private Item.CutItem parseThisSensor2(final String line)
  {

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(line);

    // declare local variables
    String theText;
    String theTrack;
    String sensorName;
    Point2D origin = null;
    Date theDtg = null;
    Double brg = null;
    Double rng = null;
    Double brg2 = null;
    Double freq = null;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDtg = parseThis(dateToken, timeToken);

    // get the (possibly multi-word) track name
    theTrack = checkForQuotedName(st, st.nextToken());

    // start with the symbology
    st.nextToken(normalDelimiters);

    // now the sensor offsets
    final String next = st.nextToken().trim();

    // get the origin, if we can
    origin = getOptionalOrigin(st, next);

    // get the bearing
    final String brgStr = st.nextToken();
    if (!brgStr.startsWith("N"))
    {
      // cool, we have data
      brg = new Double(Double.parseDouble(brgStr));
    }

    // and now get the ambiguous bearing
    String tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      brg2 = new Double(Double.parseDouble(tmp));
    }

    // and the frequency
    tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      freq = new Double(Double.parseDouble(tmp));
    }

    // and the range
    tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      final double rngYds = Double.parseDouble(tmp);
      rng = rngYds * 0.9144;
    }

    // get the (possibly multi-word) track name
    sensorName = checkForQuotedName(st, st.nextToken());

    // trim the sensor name
    sensorName = sensorName.trim();

    // and lastly read in the message (to the end of the line)
    final String labelTxt = st.nextToken("\r");

    // did we find anything
    if (labelTxt != null)
    {
      theText = labelTxt.trim();
    }
    else
    {
      // nothing found, use empty string
      theText = "";
    }

    return new Item.CutItem(theTrack, sensorName, origin, theDtg, brg, brg2,
        freq, rng, theText);

  }

}
