/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.ReaderWriter.FlatFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class CLogFileImporter
{

  public interface CLog_Helper
  {
    String getTrackName();
  }
  /**
   * package up the action that adds the data to the layers target
   *
   */
  private static class ImportCLogFileAction implements Action
  {
    private final List<FixWrapper> _track;
    private final Layers _layers;
    private final String _name;
    private boolean _trackCreated = false;

    public ImportCLogFileAction(final List<FixWrapper> track, final String name,
        final Layers layers)
    {
      super();

      _name = name;
      _track = track;
      _layers = layers;
    }

    @Override
    public void execute()
    {
      // see of we this track already exists
      Layer layer = _layers.findLayer(_name);
      final TrackWrapper track;
      if (layer != null)
      {
        if (layer instanceof TrackWrapper)
        {
          track = (TrackWrapper) layer;
        }
        else
        {
          DialogFactory.showMessage("Import Log File",
              "Can't use this track name, it clashes with existing non-track layer");
          track = null;
        }
      }
      else
      {
        track = new TrackWrapper();
        track.setName(_name);
        track.setColor(DebriefColors.BLUE);
        _trackCreated = true;
        _layers.addThisLayer(track);
      }
      if (track != null)
      {
        for(FixWrapper fix : _track)
        {
          track.addFix(fix);
        }
      }
    }

    @Override
    public boolean isRedoable()
    {
      return true;
    }

    @Override
    public boolean isUndoable()
    {
      return true;
    }

    @Override
    public void undo()
    {
      // see of we this track already exists
      Layer layer = _layers.findLayer(_name);
      if (layer != null)
      {
        if (layer instanceof TrackWrapper)
        {
          TrackWrapper track = (TrackWrapper) layer;
          for(FixWrapper fix: _track)
          {
            track.removeFix(fix);
          }
          // also remove the track, if necessary
          if(_trackCreated)
          {
            _layers.removeThisLayer(track);
          }
        }
        else
        {
          DialogFactory.showMessage("Undo Import Log File",
              "Can't find track containing new fixes");
        }
        
      }
    }
  }

  public static class CLogFile_ImporterTest extends TestCase
  {
    static class Logger implements ErrorLogger
    {
      private final List<String> messages = new ArrayList<String>();

      private final boolean console = true;

      private void clear()
      {
        messages.clear();
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

    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    private final Logger _logger = new Logger();

    @Override
    public void setUp()
    {
      _logger.clear();
    }
    
    public void NOTtestExport() throws IOException
    {
      final String ownship_track =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());
      
      // ok, now export in the new format
      FileWriter fw=new FileWriter("CLog_Trial.txt");    
      fw.write("Unknown blah blah blah\n");    
      fw.write("Blah blah blah blah\n");    
      
      // and now the positions
      int ctr = 0;
      TrackWrapper track = (TrackWrapper) tLayers.findLayer("Nelson");
      Enumeration<Editable> posits = track.getPositionIterator();
      while(ctr < 10 && posits.hasMoreElements())
      {
        FixWrapper fix = (FixWrapper) posits.nextElement();
        String asLog = toLogFile(fix);
        fw.write(asLog);
      }
      fw.close();
    }

    private String toLogFile(FixWrapper fix)
    {
      String res = "";
      final String blah = "blah";
      final String separator = " ";
      final String nl = System.lineSeparator();
      
      res += (blah + separator); // 1
      res += (blah + separator); // 2
      res += (blah + separator); // 3
      res += (blah + separator); // 4
      res += (blah + separator); // 5
      res += (blah + separator); // 6
      res += (blah + separator); // 7
      res += (blah + separator); // 8
      res += (blah + separator); // 9

      res += (fix.getCourse() + separator);  // 10 - Course in radians      
      res +=  (new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec) + separator); // 11 - Speed in metres/second
      res += (Math.toRadians(fix.getLocation().getLat()) + separator);  //      12 - Latitude in Radians
      res += (Math.toRadians(fix.getLocation().getLong()) + separator);  //      13 - Longitude in Radians
      res += (fix.getLocation().getDepth() + separator );  //      14 - Depth in metres
      res += (blah + separator); // 15
      res += (blah + separator); // 16
      res += (timeStampFor(fix.getDateTimeGroup()) + separator); //    17 - timestamp in Nanos since epoch (19 digits!)
      
      // and newline
      res += nl;
      
      return res;
      }
    
    private long timeStampFor(HiResDate date)
    {
      final long millis = date.getDate().getTime();
      return millis * 1000000;
    }
    private final String ownship_track =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/CLog_Trial.txt";

    public void testGoodLoad() throws Exception
    {
      final CLogFileImporter importer = new CLogFileImporter();
      final Layers layers = new Layers();

      final CLog_Helper brtHelper = new CLog_Helper() {

        @Override
        public String getTrackName()
        {
          return "Dave";
        }};
      

      assertTrue("input file exists", new File(ownship_track).exists());

      assertEquals("empty before", 0, layers.size());

      final InputStream is = new FileInputStream(ownship_track);
      final ImportCLogFileAction action = importer.importThis(brtHelper, is,
          layers, _logger);
      action.execute();

      assertEquals("has data", 1, layers.size());

      final TrackWrapper track = (TrackWrapper) layers.elementAt(0);

      assertEquals("correct fixes", 402, track.numFixes());

      // and undo it
      action.undo();

      assertEquals("has data", 0, layers.size());
    }
    
    private FixWrapper createF(long time)
    {
      WorldLocation loc = new WorldLocation(2, 2, 2);
      Fix newF = new Fix(new HiResDate(time), loc, 0, 0);
      FixWrapper fw = new FixWrapper(newF);
      return fw;
    }
    
    public void testGoodLoadOnExisting() throws Exception
    {
      final CLogFileImporter importer = new CLogFileImporter();
      final Layers layers = new Layers();
      final String trackName = "Dumbo";

      final CLog_Helper brtHelper = new CLog_Helper() {

        @Override
        public String getTrackName()
        {
          return trackName;
        }};
      
      final String ownship_track =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/CLog_Trial.txt";

      assertTrue("input file exists", new File(ownship_track).exists());

      TrackWrapper tw = new TrackWrapper();
      tw.setName(trackName);
      tw.add(createF(10000000L));
      tw.add(createF(20000000L));
      tw.add(createF(30000000L));
      layers.addThisLayer(tw);
      
      assertEquals("not empty before", 1, layers.size());
      assertEquals("only 3 items before", 3, tw.numFixes());

      final InputStream is = new FileInputStream(ownship_track);
      final ImportCLogFileAction action = importer.importThis(brtHelper, is,
          layers, _logger);
      action.execute();

      assertEquals("has data", 1, layers.size());

      final TrackWrapper track = (TrackWrapper) layers.elementAt(0);

      assertEquals("correct fixes", 405, track.numFixes());

      // and undo it
      action.undo();

      assertEquals("has data", 3, track.numFixes());
    }
  }

  private static final String HEADER_STR = "Unknown";

  public static boolean canLoad(final ErrorLogger logger,
      final BufferedReader r) throws IOException
  {
    // try this first line
    final String line = r.readLine();

    final boolean res = (line != null && line.startsWith(HEADER_STR));

    if (!res)
    {
      logger.logError(ErrorLogger.INFO, "CLog Import rejecting file, Header:"
          + res , null);
    }
    return res;
  }

  public static boolean canLoad(final String fileName, final ErrorLogger logger)
  {
    boolean res = false;
    BufferedReader r = null;
    FileInputStream fis = null;
    try
    {
      fis = new FileInputStream(fileName);
      r = new BufferedReader(new InputStreamReader(fis));
      res = canLoad(logger, r);
    }
    catch (final Exception e)
    {
      logger.logError(ErrorLogger.ERROR, "Trouble whilst checking valid CLog",
          e);
    }
    finally
    {
      try
      {
        if (r != null)
          r.close();
        if (fis != null)
          fis.close();
      }
      catch (final IOException e)
      {
        logger.logError(ErrorLogger.ERROR, "Couldn't close file:" + fileName,
            e);
      }
    }
    return res;
  }

  private static double courseFor(final String courseRadsStr, final ErrorLogger logger)
  {
    return Double.parseDouble(courseRadsStr);
  }

  private static ImportCLogFileAction createImportAction(
      final List<FixWrapper> track, final Layers layers, final String trackName)
  {
    return new ImportCLogFileAction(track, trackName, layers);
  }

  private static HiResDate dateFor(final String timeStr, final ErrorLogger logger)
  {
    final long nanos = Long.parseLong(timeStr);
    final long millis = nanos / 1000000;
    return new HiResDate(millis);
  }

  public static TrackWrapper findTrack(final TrackWrapper[] allTracks)
  {
    if (allTracks.length == 1)
    {
      return allTracks[0];
    }
    int amountOfBlueTracks = 0;
    int indexOfBlueTrack = 0;
    for (int i = 0; i < allTracks.length; i++)
    {
      if (DebriefColors.BLUE.equals(allTracks[i].getTrackColor()))
      {
        ++amountOfBlueTracks;
        indexOfBlueTrack = i;
      }
    }
    if (amountOfBlueTracks == 1)
    {
      return allTracks[indexOfBlueTrack];
    }
    return null;
  }

  private static WorldLocation locationFrom(final String latStr,
      String longStr, final String depthStr, final ErrorLogger logger)
  {
    final double latRads = Double.parseDouble(latStr);
    final double longRads = Double.parseDouble(longStr);
    final double depthM = Double.parseDouble(depthStr);
    return new WorldLocation(Math.toDegrees(latRads), Math.toDegrees(longRads),
        depthM);
  }

  public static FixWrapper produceFix(final ErrorLogger logger,
      final String line)
  {
    // ok, tokenize the line
    String[] tokens = line.split("\\s+");
    
    final WorldLocation loc = locationFrom(tokens[11], tokens[12], tokens[13], logger);
    final HiResDate date = dateFor(tokens[16], logger);
    final double courseRads = courseFor(tokens[9], logger);
    final double speedYps = speedFor(tokens[10], logger);
    final Fix fix = new Fix(date, loc, courseRads, speedYps);
    final FixWrapper wrapped = new FixWrapper(fix);
    return wrapped;
  }

  private static List<FixWrapper> readCLogData(final InputStream is,
      final ErrorLogger logger) throws IOException
  {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    String line;

    List<FixWrapper> res = null;

    // skip 2 header rows
    reader.readLine();
    reader.readLine();

    while ((line = reader.readLine()) != null)
    {
      // ok, generate a position
      final FixWrapper wrapped = produceFix(logger, line);
      if (wrapped != null)
      {
        if (res == null)
        {
          res = new ArrayList<FixWrapper>();
        }
        res.add(wrapped);
      }
    }
    return res;
  }

  private static double speedFor(final String line, final ErrorLogger logger)
  {
    final Double speedMs = Double.parseDouble(line);
    return new WorldSpeed(speedMs, WorldSpeed.M_sec).getValueIn(
        WorldSpeed.ft_sec) / 3;
  }

  public ImportCLogFileAction importThis(final CLog_Helper helper,
      final InputStream is, final Layers layers, final ErrorLogger logger)
      throws Exception
  {
    final List<FixWrapper> brtData = readCLogData(is, logger);
    final String trackName = helper.getTrackName();
    return createImportAction(brtData, layers, trackName);
  }
}
