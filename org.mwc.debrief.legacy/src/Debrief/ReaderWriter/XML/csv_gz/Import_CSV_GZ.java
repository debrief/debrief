/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.ReaderWriter.XML.csv_gz;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public class Import_CSV_GZ
{

  private abstract class Core_Importer
  {
    /**
     * map marker for the contents of the 5th column (when relevant)
     *
     */
    protected static final String SYSTEM_ID = "SYSTEM_ID";

    /**
     * keep a central date formatter
     *
     */
    private final GMTDateFormat _formatter = new GMTDateFormat(CSV_DATE_FORMAT);

    Long perfStep = null;

    /**
     * process the lines
     *
     * @param inputStream
     *
     * @param theLayers
     *          destination for the data
     * @param hostName
     *          the recording platform
     * @param logger
     * @param fileName
     */
    public final void doImport(final InputStream inputStream,
        final Layers theLayers, final String hostName, final ErrorLogger logger,
        final String fileName)
    {
      prepareForImport(theLayers, hostName, logger);

      final InputStreamReader isr = new InputStreamReader(inputStream);
      final BufferedReader br = new BufferedReader(isr);

      try (BufferedReader reader = new BufferedReader(br, 1048576 * 10))
      {
        int ctr = 1;
        final Iterable<CSVRecord> tRecords = CSVFormat.RFC4180.parse(reader);
        for (final CSVRecord line : tRecords)
        {
          try
          {
            perfLog(ctr);
            processThis(theLayers, hostName, logger, line);
          }
          catch (final NumberFormatException | ParseException ne)
          {
            logger.logError(ErrorLogger.ERROR, "Problem at line:" + ctr + " "
                + ne.getMessage(), ne);
            DialogFactory.showMessage("Import CSV.GZ File", "Problem at line:"
                + ctr + " " + ne.getMessage());
          }
          ctr++;
        }

        // ok, finished processing
        finalise(theLayers);
      }
      catch (final IOException e)
      {
        LoggingService.INSTANCE().logError(ErrorLogger.ERROR,
            "Failed while importing CSV file:" + fileName, e);
      }

    }

    protected void finalise(final Layers theLayers)
    {

    }

    /**
     * parse the date
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    protected Date getDate(final String dateStr) throws ParseException
    {
      // 20 Nov 2019 - 11:22:33.000
      final Date date = _formatter.parse(dateStr);
      return date;
    }

    /**
     * wrap the date
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    protected HiResDate getHiResDate(final String dateStr) throws ParseException
    {
      return new HiResDate(getDate(dateStr).getTime());
    }

    /**
     * retrieve the field titles for this processor
     *
     * @return
     */
    abstract public List<String> getMyFields();

    /**
     * parse the tokens on this line
     *
     * @param tokens
     * @param myFields
     * @return
     */
    protected Map<String, String> getTokens(final Iterator<String> tokens,
        final List<String> myFields)
    {
      // we know each importer has already run once
      int ctr = 1;

      final Map<String, String> map = new HashMap<String, String>();

      String pendingToken = null;

      while (tokens.hasNext())
      {
        // get a trimmed token
        final String token = tokens.next().trim();

        // is this the fifth column?
        if (++ctr == 5)
        {
          // quietly store it. Some formats rely on the
          // data in the 5th column
          map.put(SYSTEM_ID, token);
        }

        /**
         * are we waiting for a value?
         *
         */
        if (pendingToken != null)
        {
          // ok, extract the next value
          map.put(pendingToken, token);
          pendingToken = null;
        }
        else
        {
          // is this a token we're looking for?
          for (final String thisT : myFields)
          {
            if (thisT.equals(token))
            {
              pendingToken = thisT;
              break;
            }
          }
        }
      }
      return map;
    }

    /**
     * parse this number
     *
     * @param value
     * @return
     * @throws ParseException
     */
    protected Double parseThis(final String value, final String name)
        throws ParseException
    {
      final double res;
      if (value == null || value.length() == 0)
      {
        throw new ParseException("Missing token for " + name, 0);
      }
      else
      {
        try
        {
          res = Double.parseDouble(value);
        }
        catch (final NumberFormatException ne)
        {
          throw new NumberFormatException("While parsing " + name + "_" + ne
              .getMessage());
        }
      }
      return res;
    }

    protected void perfLog(final long ctr)
    {
      if (ctr != 0)
      {
        final double log10 = Math.log10(ctr);
        if (log10 == (int) log10)
        {
          perfStep = ctr;
          System.out.println(ctr);
        }
        else
        {
          final double steps = ((double) ctr) / perfStep;
          if (steps == (int) steps)
          {
            System.out.println(" " + ctr);
          }
        }
      }
    }

    /**
     * do any necessary preparation
     *
     * @param theLayers
     * @param hostName
     * @param logger
     */
    protected abstract void prepareForImport(Layers theLayers, String hostName,
        ErrorLogger logger);

    /**
     * handle this row
     *
     * @param theLayers
     * @param hostName
     * @param logger
     * @param record
     * @throws ParseException
     */
    protected abstract void processThis(Layers theLayers, String hostName,
        ErrorLogger logger, CSVRecord record) throws ParseException;

    /**
     * trim this track number
     *
     * @param trackStr
     * @return
     */
    protected String trimmedTrackNum(final String trackStr)
    {
      return trackStr.substring(6);
    }
  }

  private class OSD_Importer extends Core_Importer
  {
    private static final String SPEED = "attr_speedOverTheGround";
    private static final String DEPTH = "attr_depth";
    private static final String LAT = "attr_latitude";
    private static final String LONG = "attr_longitude";
    private static final String COURSE = "attr_courseOverTheGround";
    private TrackWrapper _track;

    @Override
    public List<String> getMyFields()
    {
      final List<String> myTokens = new ArrayList<String>();
      myTokens.add(COURSE);
      myTokens.add(LONG);
      myTokens.add(LAT);
      myTokens.add(DEPTH);
      myTokens.add(SPEED);
      return myTokens;
    }

    @Override
    protected void prepareForImport(final Layers theLayers,
        final String hostName, final ErrorLogger logger)
    {
      _track = null;
    }

    public FixWrapper process(final Iterator<String> tokens,
        final ErrorLogger logger) throws ParseException
    {
      final String dateStr = tokens.next();
      final HiResDate date = getHiResDate(dateStr);
      final List<String> myFields = getMyFields();

      final Map<String, String> map = getTokens(tokens, myFields);

      final FixWrapper res;
      if (map.size() >= myFields.size())
      {
        // create fix
        final WorldLocation loc = new WorldLocation(Math.toDegrees(parseThis(map
            .get(LAT), LAT)), Math.toDegrees(parseThis(map.get(LONG), LONG)),
            parseThis(map.get(DEPTH), DEPTH));
        final double speed = new WorldSpeed(parseThis(map.get(SPEED), SPEED),
            WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec) / 3d;
        final Fix fix = new Fix(date, loc, parseThis(map.get(COURSE), COURSE),
            speed);
        res = new FixWrapper(fix);
      }
      else
      {
        String missingFields = "";
        for (final String field : myFields)
        {
          if (!map.keySet().contains(field))
          {
            if (missingFields.length() == 0)
              missingFields += field;
            else
              missingFields += ", " + field;
          }
        }

        // ok, insufficient tokens, throw wobbly?
        logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
        res = null;
      }

      return res;
    }

    @Override
    public void processThis(final Layers theLayers, final String hostName,
        final ErrorLogger logger, final CSVRecord record) throws ParseException
    {
      final FixWrapper nextFix = process(record.iterator(), logger);

      if (nextFix != null)
      {
        if (_track == null)
        {
          _track = trackFor(theLayers, hostName);
          // check it's Debrief blue
          _track.setColor(DebriefColors.BLUE);
        }
        _track.addFix(nextFix);
      }
    }
  }

  private class Sensor_Importer extends Core_Importer
  {
    private static final String BEARING = "attr_bearing";
    private static final String TRACK_ID = "attr_trackNumber";

    private TrackWrapper track;
    private Map<String, SensorWrapper> map;

    @Override
    public List<String> getMyFields()
    {
      final List<String> myTokens = new ArrayList<String>();
      myTokens.add(BEARING);
      myTokens.add(TRACK_ID);
      return myTokens;
    }

    private Map<String, SensorWrapper> getSensors(final TrackWrapper parent)
    {
      final Map<String, SensorWrapper> res =
          new HashMap<String, SensorWrapper>();

      final BaseLayer trackSensors = parent.getSensors();
      if (trackSensors != null)
      {
        final Enumeration<Editable> sensors = trackSensors.elements();
        while (sensors.hasMoreElements())
        {
          final SensorWrapper next = (SensorWrapper) sensors.nextElement();
          res.put(next.getName(), next);
        }
      }
      return res;
    }

    @Override
    protected void prepareForImport(final Layers theLayers,
        final String hostName, final ErrorLogger logger)
    {
      // get the host track
      final Layer host = theLayers.findLayer(hostName);
      if (host == null || !(host instanceof TrackWrapper))
      {
        logger.logStack(ErrorLogger.ERROR, "Can't find host track:" + hostName);
        throw new RuntimeException("Host track not found:" + hostName);
      }

      track = (TrackWrapper) host;
      map = getSensors(track);

    }

    public SensorContactWrapper process(final Iterator<String> tokens,
        final ErrorLogger logger) throws ParseException
    {
      final String dateStr = tokens.next();
      final HiResDate date = getHiResDate(dateStr);
      final List<String> myFields = getMyFields();

      final Map<String, String> map = getTokens(tokens, myFields);

      final SensorContactWrapper res;
      if (map.size() >= myFields.size() + 1)
      {
        final Double bearingDegs = Math.toDegrees(parseThis(map.get(BEARING),
            BEARING));

        final String TRACK_NAME = trimmedTrackNum(map.get(SYSTEM_ID)) + "_"
            + map.get(TRACK_ID);

        // create sensor
        res = new SensorContactWrapper("PENDING", date, null, bearingDegs, null,
            null, null, LineStylePropertyEditor.SOLID, TRACK_NAME);
      }
      else
      {
        String missingFields = "";
        for (final String field : myFields)
        {
          if (!map.keySet().contains(field))
          {
            if (missingFields.length() == 0)
              missingFields += field;
            else
              missingFields += ", " + field;
          }
        }

        // ok, insufficient tokens, throw wobbly?
        logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
        res = null;
      }

      return res;
    }

    @Override
    protected void processThis(final Layers theLayers, final String hostName,
        final ErrorLogger logger, final CSVRecord record) throws ParseException
    {
      final SensorContactWrapper nextFix = process(record.iterator(), logger);

      final String sensorId = nextFix.getSensorName();

      SensorWrapper sensor = map.get(sensorId);
      if (sensor == null)
      {
        sensor = new SensorWrapper(sensorId);

        final Color theCol = DebriefColors.RandomColorProvider.getRandomColor(
            colorCounter++);
        sensor.setColor(theCol);

        track.add(sensor);
        map.put(sensorId, sensor);
      }
      sensor.add(nextFix);
    }
  }

  private class State_Importer extends Core_Importer
  {
    private static final String bearing = "attr_bearing";
    private static final String country = "attr_countryAbbreviation";
    private static final String course = "attr_course";
    private static final String latitude = "attr_latitude";
    private static final String longitude = "attr_longitude";
    private static final String speed = "attr_speed";
    private static final String trackNum = "attr_trackNumber";

    private static final String MY_TRACK_ID = "1";
    private final HashMap<String, LightweightTrackWrapper> _lightTracks;

    public State_Importer()
    {
      _lightTracks = new HashMap<String, LightweightTrackWrapper>();
    }

    @Override
    protected void finalise(final Layers theLayers)
    {
      // let parent tidy up
      super.finalise(theLayers);

      final Layer newBase = new BaseLayer();
      newBase.setName(BULK_TRACKS);

      // store the new lgithweight tracks
      for (final LightweightTrackWrapper track : _lightTracks.values())
      {
        newBase.add(track);
      }

      theLayers.addThisLayer(newBase);
    }

    @Override
    public List<String> getMyFields()
    {
      final List<String> myTokens = new ArrayList<String>();
      myTokens.add(bearing);
      myTokens.add(country);
      myTokens.add(course);
      myTokens.add(latitude);
      myTokens.add(longitude);
      myTokens.add(speed);
      myTokens.add(trackNum);
      return myTokens;
    }

    @Override
    protected void prepareForImport(final Layers theLayers,
        final String hostName, final ErrorLogger logger)
    {

    }

    public FixWrapper process(final Iterator<String> tokens,
        final ErrorLogger logger, final String hostname) throws ParseException
    {
      final String dateStr = tokens.next();
      final HiResDate date = getHiResDate(dateStr);
      final List<String> myFields = getMyFields();

      final Map<String, String> map = getTokens(tokens, myFields);

      final FixWrapper res;
      // note: we allow for missing country field
      if (map.size() >= myFields.size() - 1)
      {
        // create fix
        final Double latVal = parseThis(map.get(latitude), latitude);
        final Double longVal = parseThis(map.get(longitude), longitude);
        if (latVal != 0d && longVal != 0d)
        {

          final WorldLocation loc = new WorldLocation(Math.toDegrees(latVal),
              Math.toDegrees(longVal), 0d);
          final double speedVal = new WorldSpeed(parseThis(map.get(speed),
              speed), WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec) / 3d;
          final Fix fix = new Fix(date, loc, parseThis(map.get(course), course),
              speedVal);
          res = new FixWrapper(fix);

          // store the track name
          final String trackName;
          final String trackStr = map.get(trackNum);
          if (trackStr != null && trackStr.equals(MY_TRACK_ID))
          {
            trackName = hostname;
          }
          else
          {
            final String countryStr = map.get(country);
            String workingName = trackStr;
            if (countryStr != null && countryStr.length() > 0)
            {
              workingName += "_" + countryStr;
            }
            trackName = workingName;
          }

          res.setComment(trackName);

        }
        else
        {
          res = null;
        }
      }
      else
      {
        String missingFields = "";
        for (final String field : myFields)
        {
          if (!map.keySet().contains(field))
          {
            if (missingFields.length() == 0)
              missingFields += field;
            else
              missingFields += ", " + field;
          }
        }

        // ok, insufficient tokens, throw wobbly?
        logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
        res = null;
      }

      return res;
    }

    @Override
    protected void processThis(final Layers theLayers, final String hostName,
        final ErrorLogger logger, final CSVRecord record) throws ParseException
    {
      final FixWrapper nextFix = process(record.iterator(), logger, hostName);

      if (nextFix != null)
      {
        // reset name
        nextFix.resetName();

        final String trackId = nextFix.getComment();
        final String trackName;
        final boolean isOwnship = trackId.equals(hostName);
        final boolean isOfInterest = trackId.contains("_");

        LightweightTrackWrapper thisTrack;
        if (isOwnship || isOfInterest)
        {
          trackName = isOwnship ? hostName : trackId;
          thisTrack = (LightweightTrackWrapper) theLayers.findLayer(trackName);
          if (thisTrack == null)
          {
            final Color trackColor = isOwnship ? DebriefColors.BLUE
                : DebriefColors.RED;
            thisTrack = new TrackWrapper();
            thisTrack.setName(trackName);
            theLayers.addThisLayer(thisTrack);
            thisTrack.setColor(trackColor);
          }
        }
        else
        {
          trackName = trackId;

          // see if we have it already
          thisTrack = _lightTracks.get(trackName);
          if (thisTrack == null)
          {
            thisTrack = new LightweightTrackWrapper(trackName, true, true,
                DebriefColors.RED, LineStylePropertyEditor.SOLID);
            thisTrack.setColor(DebriefColors.GREEN);
            _lightTracks.put(trackName, thisTrack);
          }
        }

        thisTrack.add(nextFix);
      }
    }
  }

  public static class TestCSV_GZ_Import extends TestCase
  {
    static class Logger implements ErrorLogger
    {
      protected final List<String> messages = new ArrayList<String>();

      private final boolean console = true;

      public List<String> getMessages()
      {
        return messages;
      }

      public boolean isEmpty()
      {
        return messages.isEmpty();
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e)
      {
        output(text, e);
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e, final boolean revealLog)
      {
        output(text, e);
      }

      @Override
      public void logStack(final int status, final String text)
      {
        output(text, null);
      }

      public void output(final String text, final Exception e)
      {
        messages.add(text);
        if (console)
        {
          System.out.println(text);
          if (e != null)
          {
            e.printStackTrace();
          }
        }
      }
    }

    @Override
    public void setUp()
    {
      DialogFactory.setRunHeadless(true);
    }

    public void test_parse_bad_OSD_File() throws IOException
    {
      final String root = "../org.mwc.debrief.legacy/test_data/CSV_GZ_Import/";
      final String filename = "BARTON_xxxx_OSD_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_xxxx_OSD_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename));

      final InputStream bs = new FileInputStream(zipFile);

      final Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());

      final Import_CSV_GZ importer = new Import_CSV_GZ();
      final Logger logger = new Logger();
      importer.doZipImport(theLayers, bs, filename, logger);

      // check 3 errors thrown
      assertEquals("3 messages", 3, logger.getMessages().size());
      final List<String> strings = logger.getMessages();
      assertTrue(strings.contains(
          "Problem at line:3 While parsing attr_latitude_For input string: \"a1.585351162\""));
      assertTrue(strings.contains(
          "Problem at line:5 Missing token for attr_latitude"));
      assertTrue(strings.contains(
          "Problem at line:11 Missing token for attr_longitude"));
    }

    public void test_parse_Big_Tracks_File() throws IOException
    {
      final String root =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename = "BARTON_xxxx_SystemTrack_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_xxxx_SystemTrack_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename));

      final InputStream bs = new FileInputStream(zipFile);

      final Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());
      final Logger logger = new Logger();

      new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

      // check empty
      assertEquals("has track", 3, theLayers.size());
      final Layer bTrack = theLayers.findLayer("BARTON");
      assertNotNull("found track", bTrack);
      final TrackWrapper track = (TrackWrapper) bTrack;

      assertEquals("has fixes", 10, track.numFixes());
      final Enumeration<Editable> pIter = track.getPositionIterator();
      // move along a bit
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      final FixWrapper fix5 = (FixWrapper) pIter.nextElement();
      final Date dtg = fix5.getDTG().getDate();
      final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
      assertEquals("correct date", "12/Nov/2019 12:47:40", dfDate.format(dtg));
      final WorldLocation loc = fix5.getLocation();
      assertEquals(-0.8071912410000001, Math.toRadians(loc.getLat()), 0.000001);
      assertEquals(0.788539756051038, Math.toRadians(loc.getLong()), 0.000001);
      assertEquals(0d, loc.getDepth(), 0.000001);
      assertEquals(0.847944870877505, fix5.getCourse(), 0.0001);
      assertEquals(6.8, new WorldSpeed(fix5.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);

      // also check out the bulk trcks
      final BaseLayer bulk = (BaseLayer) theLayers.findLayer(BULK_TRACKS);
      assertNotNull(bulk);
      assertEquals("2 tracks", 2, bulk.size());
    }

    public void test_parse_OSD_File() throws IOException
    {
      final String root =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename = "BARTON_xxxx_OSD_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_xxxx_OSD_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename));

      final InputStream bs = new FileInputStream(zipFile);

      final Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());
      final Logger logger = new Logger();

      new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

      // check empty
      assertEquals("has track", 1, theLayers.size());
      final Layer bTrack = theLayers.findLayer("BARTON");
      assertNotNull("found track", bTrack);
      final TrackWrapper track = (TrackWrapper) bTrack;
      assertEquals("has fixes", 27, track.numFixes());
      final Enumeration<Editable> pIter = track.getPositionIterator();
      // move along a bit
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      final FixWrapper fix5 = (FixWrapper) pIter.nextElement();
      final Date dtg = fix5.getDTG().getDate();
      final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
      assertEquals("correct date", "12/Nov/2019 12:47:00", dfDate.format(dtg));
      final WorldLocation loc = fix5.getLocation();
      assertEquals(-0.789600782, Math.toRadians(loc.getLat()), 0.000001);
      assertEquals(0.765952588191503, Math.toRadians(loc.getLong()), 0.000001);
      assertEquals(37d, loc.getDepth(), 0.000001);
      assertEquals(0.523598776, fix5.getCourse());
      assertEquals(9d, new WorldSpeed(fix5.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec));

    }

    public void test_parse_Sensor_File() throws IOException
    {

      final String root1 =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename1 = "BARTON_xxxx_OSD_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile1 = new File(root1 + filename1);
      assertTrue(zipFile1.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root1 + filename1));
      assertEquals("name", "BARTON_xxxx_OSD_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename1));

      final InputStream bs1 = new FileInputStream(zipFile1);

      final Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());
      final Logger logger = new Logger();

      new Import_CSV_GZ().doZipImport(theLayers, bs1, filename1, logger);

      final String root =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename = "BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename));

      final InputStream bs = new FileInputStream(zipFile);

      // check empty
      assertEquals("has ownship", 1, theLayers.size());

      new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

      // check empty
      assertEquals("has track", 1, theLayers.size());
      final Layer bTrack = theLayers.findLayer("BARTON");
      assertNotNull("found track", bTrack);
      final TrackWrapper track = (TrackWrapper) bTrack;
      assertEquals("has fixes", 27, track.numFixes());

      // get that sensor data
      final BaseLayer sensors = track.getSensors();
      assertEquals(3, sensors.size());

      final SensorWrapper sensor = (SensorWrapper) sensors.elements()
          .nextElement();
      assertEquals("correct cuts", 10, sensor.size());

      final Enumeration<Editable> pIter = sensor.elements();
      // move along a bit
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      final SensorContactWrapper fix5 = (SensorContactWrapper) pIter
          .nextElement();
      final Date dtg = fix5.getDTG().getDate();
      final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
      assertEquals("correct date", "12/Nov/2019 12:47:55", dfDate.format(dtg));
      assertEquals("correct bearing", Math.toDegrees(1.056688125), fix5
          .getBearing());
      assertEquals("correct sensor name", "78902_3000", fix5.getSensorName());

    }

    public void test_parse_Sensor_File_Host_Missing() throws IOException
    {
      final String root =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename = "BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename));

      final InputStream bs = new FileInputStream(zipFile);

      final Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());
      final Logger logger = new Logger();

      try
      {
        new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);
        fail("Should not get here");
      }
      catch (final RuntimeException re)
      {
        assertEquals("correct error message", "Host track not found:BARTON", re
            .getMessage());
      }

    }

    public void test_parse_Tracks_File() throws IOException
    {
      final String root =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename = "BARTON_xxxx_SystemTrack_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());

      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_xxxx_SystemTrack_xxxx.csv", GzipUtils
          .getUncompressedFilename(filename));

      final InputStream bs = new FileInputStream(zipFile);

      final Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());
      final Logger logger = new Logger();

      new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

      // check empty
      assertEquals("has track", 3, theLayers.size());
      final Layer bTrack = theLayers.findLayer("BARTON");
      assertNotNull("found track", bTrack);
      final TrackWrapper track = (TrackWrapper) bTrack;
      assertEquals("has fixes", 10, track.numFixes());
      final Enumeration<Editable> pIter = track.getPositionIterator();
      // move along a bit
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      pIter.nextElement();
      final FixWrapper fix5 = (FixWrapper) pIter.nextElement();
      final Date dtg = fix5.getDTG().getDate();
      final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
      assertEquals("correct date", "12/Nov/2019 12:47:40", dfDate.format(dtg));
      final WorldLocation loc = fix5.getLocation();
      assertEquals(-0.8071912410000001, Math.toRadians(loc.getLat()), 0.000001);
      assertEquals(0.788539756051038, Math.toRadians(loc.getLong()), 0.000001);
      assertEquals(0d, loc.getDepth(), 0.000001);
      assertEquals(0.847944870877505, fix5.getCourse(), 0.0001);
      assertEquals(6.8, new WorldSpeed(fix5.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
    }

    public void testDateParse() throws ParseException
    {
      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final OSD_Importer importer = pImporter.new OSD_Importer();
      final String test_date = "20 Nov 2019 - 11:22:33.000";
      final Date date = importer.getDate(test_date);

      final DateFormat df = new GMTDateFormat(CSV_DATE_FORMAT);
      assertEquals("matching date", test_date, df.format(date));
    }

    public void testOSD() throws ParseException
    {
      final ErrorLogger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_courseOverTheGround");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final OSD_Importer importer = pImporter.new OSD_Importer();
      final FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 22d, res.getFixLocation().getDepth());
      assertEquals("crse", 180d, res.getCourseDegs());
      assertEquals("speed", 1.5d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
    }

    public void testOSD_bad_parse() throws ParseException
    {
      final ErrorLogger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_courseOverTheGround");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("a" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final OSD_Importer importer = pImporter.new OSD_Importer();
      try
      {
        importer.process(tokens.iterator(), logger);
        fail("should have thrown exception");
      }
      catch (final NumberFormatException nf)
      {
        assertNotNull(nf);
      }
    }

    public void testOSD_Short() throws ParseException
    {
      final Logger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_courseOverTheGround");
      tokens.add("" + Math.PI);
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final OSD_Importer importer = pImporter.new OSD_Importer();
      final FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNull("should not have created fix", res);
      assertFalse("should have thrown warning", logger.isEmpty());
      assertEquals("valid message",
          "Missing fields:attr_longitude, attr_latitude", logger.messages.get(
              0));
    }

    public void testParseFilename()
    {
      assertEquals("my1", getTrackPrefix("/C:/users/ronaldo/my1_ball1.csv"));
      assertEquals("my2", getTrackPrefix("//users/ronaldo/my2_ball2.csv"));
      assertEquals("my3", getTrackPrefix("my3_ball3.csv"));
    }

    public void testSensor() throws ParseException
    {
      final ErrorLogger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");
      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_trackNumber");
      tokens.add("2000");
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final Sensor_Importer importer = pImporter.new Sensor_Importer();

      final SensorContactWrapper res = importer.process(tokens.iterator(),
          logger);
      assertNotNull("should have fix", res);
      assertEquals("DTG", "Wed Nov 20 11:22:33 GMT 2019", res.getDTG().getDate()
          .toString());
      assertEquals("bearing", 180d, res.getBearing());
      assertEquals("sensor", "789012345_2000", res.getSensorName());
    }

    public void testSensor_Short() throws ParseException
    {
      final Logger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_trackNumber");
      tokens.add("2000");
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final Sensor_Importer importer = pImporter.new Sensor_Importer();
      final SensorContactWrapper res = importer.process(tokens.iterator(),
          logger);
      assertNull("should not have created fix", res);
      assertFalse("should have thrown warning", logger.isEmpty());
      assertEquals("valid message", "Missing fields:attr_bearing",
          logger.messages.get(0));
    }

    public void testSystem() throws ParseException
    {
      final ErrorLogger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");

      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_countryAbbreviation");
      tokens.add("GBR");
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("1234");

      final String hostName = "DINGO";

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final State_Importer importer = pImporter.new State_Importer();
      final FixWrapper res = importer.process(tokens.iterator(), logger,
          hostName);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 0d, res.getFixLocation().getDepth());
      assertEquals("crse", 22.5d, res.getCourseDegs());
      assertEquals("speed", 33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
      assertEquals("country", "1234_GBR", res.getComment());
    }

    public void testSystem_no_country() throws ParseException
    {
      final ErrorLogger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");
      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("3550");

      final String hostName = "DINGO";

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final State_Importer importer = pImporter.new State_Importer();
      final FixWrapper res = importer.process(tokens.iterator(), logger,
          hostName);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 0d, res.getFixLocation().getDepth());
      assertEquals("crse", 22.5d, res.getCourseDegs());
      assertEquals("speed", 33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
      assertEquals("country", "3550", res.getComment());
    }

    public void testSystem_short() throws ParseException
    {
      final Logger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("11000323223550");

      final String hostName = "DINGO";

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final State_Importer importer = pImporter.new State_Importer();
      final FixWrapper res = importer.process(tokens.iterator(), logger,
          hostName);
      assertNull("should not have created fix", res);
      assertFalse("should have thrown warning", logger.isEmpty());
      assertEquals("valid message",
          "Missing fields:attr_bearing, attr_countryAbbreviation, attr_latitude, attr_longitude",
          logger.messages.get(0));
    }

    public void testSystemOwnship() throws ParseException
    {
      final ErrorLogger logger = new Logger();
      final List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");

      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_countryAbbreviation");
      tokens.add("GBR");
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("1");
      final String hostName = "DINGO";

      final Import_CSV_GZ pImporter = new Import_CSV_GZ();
      final State_Importer importer = pImporter.new State_Importer();
      final FixWrapper res = importer.process(tokens.iterator(), logger,
          hostName);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 0d, res.getFixLocation().getDepth());
      assertEquals("crse", 22.5d, res.getCourseDegs());
      assertEquals("speed", 33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
      assertEquals("country", hostName, res.getComment());
    }

    public void unTest_Logger()
    {
      final State_Importer importer = new Import_CSV_GZ().new State_Importer();
      for (int i = 0; i < 2000; i++)
      {
        importer.perfLog(i);
      }
    }
  }

  private static final String BULK_TRACKS = "Bulk Tracks";

  private static final String CSV_DATE_FORMAT = "dd MMM yyyy - HH:mm:ss.SSS";

  private static String getTrackPrefix(final String fullPath)
  {
    // note: we have to create a fictional file
    // in order to convert the above path to a URI
    // On MS-Windows machines we get a file path,
    // not a URI, and the `Paths.get()` method throws
    // a wobbly
    final File dummy = new File(fullPath);
    final Path path = Paths.get(dummy.toURI());

    // call getFileName() and get FileName path object
    final Path fileName = path.getFileName();
    final String gName = fileName.toString();
    final int index = gName.indexOf("_");
    return gName.substring(0, index);
  }

  /**
   * keep track of how many tracks we've created, so we can generate unique colors
   */
  private int colorCounter = 0;

  private void doImport(final Layers theLayers, final InputStream inputStream,
      final String fileName, final ErrorLogger logger)
  {
    final String trackName = getTrackPrefix(fileName);

    // find out which type it is
    final Core_Importer importer = importerFor(fileName);

    if (importer == null)
    {
      logger.logStack(ErrorLogger.ERROR, "Cant find importer for " + fileName);
    }
    else
    {
      // go for it
      importer.doImport(inputStream, theLayers, trackName, logger, fileName);
    }
  }

  public void doZipImport(final Layers theLayers, final InputStream inputStream,
      final String fileName, final ErrorLogger logger)
  {
    try
    {
      final GZIPInputStream in = new GZIPInputStream(inputStream);
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final BufferedOutputStream fout = new BufferedOutputStream(bos, 4096);
      for (int c = in.read(); c != -1; c = in.read())
      {
        fout.write(c);
      }
      in.close();
      fout.close();
      bos.close();

      // now create a byte input stream from the byte output stream
      final ByteArrayInputStream bis = new ByteArrayInputStream(bos
          .toByteArray());

      // and create it
      doImport(theLayers, bis, fileName, logger);
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
  }

  private Core_Importer importerFor(final String filename)
  {
    if (filename.indexOf("_OSD_") != -1)
    {
      return new OSD_Importer();
    }
    else if (filename.contains("_SystemTrack"))
    {
      return new State_Importer();
    }
    else if (filename.contains("SensorTrack"))
    {
      return new Sensor_Importer();
    }
    else
      return null;
  }

  private TrackWrapper trackFor(final Layers layers, final String trackName)
  {
    final TrackWrapper track;
    final Layer layer = layers.findLayer(trackName);
    if (layer != null && layer instanceof TrackWrapper)
    {
      track = (TrackWrapper) layer;
    }
    else
    {
      final boolean needsRename;
      if (layer == null)
      {
        needsRename = false;
      }
      else
      {
        needsRename = true;
      }

      final String nameToUse;
      if (needsRename)
      {
        final String suffix = "-" + (int) Math.random() * 1000;
        nameToUse = trackName + suffix;
      }
      else
      {
        nameToUse = trackName;
      }

      track = new TrackWrapper();
      track.setName(nameToUse);

      // sort out a color
      final Color theCol = DebriefColors.RandomColorProvider.getRandomColor(
          colorCounter++);
      track.setColor(theCol);

      layers.addThisLayer(track);
    }

    return track;
  }
}
