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
package Debrief.ReaderWriter.BRT;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.WormInHoleOffset;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

public class BRTImporter
{

  public static class BRTImporterTest extends TestCase
  {

    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    private static TrackWrapper createTrack(final String name,
        final Color color)
    {
      final TrackWrapper res = new TrackWrapper();
      res.setColor(color);
      res.setName(name);
      return res;
    }

    final Layer[] allLayers = new Layer[]
    {
        new NarrativeWrapper("Test0"), 
        new NarrativeWrapper("Test1"), 
        createTrack("1", DebriefColors.BLUE), 
        new NarrativeWrapper("Test2"), 
        createTrack("2", DebriefColors.BLUE), 
        createTrack("3", DebriefColors.RED),
        createTrack("4", DebriefColors.BLUE) 
    };

    public void testFindTrack()
    {

      final Layers testLayers = new Layers();
      for (final Layer l : allLayers)
      {
        testLayers.addThisLayer(l);
      }

      TrackWrapper selectedTrack;
      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Return null since several blue tracks",
          null, selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[4]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one track", allLayers[4],
          selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[4]);
      testLayers.addThisLayer(allLayers[5]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one blue tracks", allLayers[4],
          selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[5]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one (non-blue) track", allLayers[5],
          selectedTrack);
      
      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[5]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one (non-blue) track", allLayers[5],
          selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[5]);
      testLayers.addThisLayer(allLayers[6]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one (non-blue) track", allLayers[6],
          selectedTrack);

    }

    public void testGetTracks()
    {

      final Layers testLayers = new Layers();
      for (final Layer l : allLayers)
      {
        testLayers.addThisLayer(l);
      }

      final TrackWrapper[] tracks = BRTImporter.getTracks(testLayers);
      assertEquals("getTrack getting TrackWrapper", allLayers[2], tracks[0]);
      assertEquals("getTrack getting TrackWrapper", allLayers[4], tracks[1]);
      assertEquals("getTrack getting TrackWrapper", allLayers[5], tracks[2]);
      assertEquals("amount of tracks extracted", 4, tracks.length);

    }

    private static void loadLayers(final Layers layers, final String filename, String root) throws IOException
    {
      final String fName =
          root + filename;
      // start off with the ownship track
      final File boatFile = new File(fName);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(fName, bs, layers);
    }
    
    public void testImportTowed() throws Exception
    {
      final Layers layers = new Layers();
      final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_data/brt_import/";
      
      // load the parent track
      loadLayers(layers, "All Tracks.rep", root);

      assertEquals("read in track", 2, layers.size());

      final String brtName = root + "TOWED_1000m.brt";
      
      final TrackWrapper sensorTrack = (TrackWrapper) layers.findLayer("SENSOR");
      WorldDistance offset = new WorldDistance(1000, WorldDistance.METRES);
      WorldDistance length = new WorldDistance(20000, WorldDistance.METRES);
      final BRTHelperHeadless headless = new BRTHelperHeadless(true, offset,
          DebriefColors.BLUE, length, sensorTrack);
      
      assertEquals("has zero sensors", 0, sensorTrack.getSensors().size());

      final BRTImporter importer = new BRTImporter();
      ImportBRTAction action = importer.importThis(headless, brtName, new FileInputStream(brtName));
      
      action.execute();
      
      // check the data is loaded as expected
      assertEquals("now has new sensor", 1, sensorTrack.getSensors().size());

      action.undo();
      
      // check the data is loaded as expected
      assertEquals("now has zero sensors", 0, sensorTrack.getSensors().size());

      action.execute();
      
      // check the data is loaded as expected
      assertEquals("now has new sensor", 1, sensorTrack.getSensors().size());
      
      // have a look at the data
      SensorWrapper sensor = (SensorWrapper) sensorTrack.getSensors().elements().nextElement();
      assertEquals("has all cuts", 31, sensor.size());
      assertEquals("correct offset", offset, sensor.getSensorOffset());
      
      // look at a cut
      SensorContactWrapper cut = (SensorContactWrapper) sensor.elements().nextElement();
      assertEquals("correct length", length, cut.getRange());
      assertEquals("correct track", sensorTrack.getName(), cut.getTrackName());
      assertEquals("correct bearing", 0d, cut.getBearing());
      assertEquals("correct ambig bearing", 222d, cut.getAmbiguousBearing());

      double course = MWC.Algorithms.Conversions.Rads2Degs(sensorTrack.getNearestTo(cut.getDTG())[0].getCourse());
      assertEquals("correct ownship course", 291d, course);
      
    }
    

    public void testImportHull() throws Exception
    {
      final Layers layers = new Layers();
      final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_data/brt_import/";
      
      // load the parent track
      loadLayers(layers, "All Tracks.rep", root);

      assertEquals("read in track", 2, layers.size());

      final String brtName = root + "HULL_ZERO.brt";
      
      final TrackWrapper sensorTrack = (TrackWrapper) layers.findLayer("SENSOR");
      WorldDistance offset = null;
      WorldDistance length = new WorldDistance(20000, WorldDistance.METRES);
      final BRTHelperHeadless headless = new BRTHelperHeadless(false, offset,
          DebriefColors.BLUE, length, sensorTrack);
      
      assertEquals("has zero sensors", 0, sensorTrack.getSensors().size());

      final BRTImporter importer = new BRTImporter();
      ImportBRTAction action = importer.importThis(headless, brtName, new FileInputStream(brtName));
      
      action.execute();
      
      // check the data is loaded as expected
      assertEquals("now has new sensor", 1, sensorTrack.getSensors().size());

      action.undo();
      
      // check the data is loaded as expected
      assertEquals("now has zero sensors", 0, sensorTrack.getSensors().size());

      action.execute();
      
      // check the data is loaded as expected
      assertEquals("now has new sensor", 1, sensorTrack.getSensors().size());
      
      // have a look at the data
      SensorWrapper sensor = (SensorWrapper) sensorTrack.getSensors().elements().nextElement();
      assertEquals("has all cuts", 31, sensor.size());
      assertEquals("correct offset", 0d, sensor.getSensorOffset().getValue());
      
      // look at a cut
      SensorContactWrapper cut = (SensorContactWrapper) sensor.elements().nextElement();
      assertEquals("correct length", length, cut.getRange());
      assertEquals("correct track", sensorTrack.getName(), cut.getTrackName());
      assertEquals("correct bearing", 0d, cut.getBearing());
      assertEquals("correct ambig bearing", Double.NaN, cut.getAmbiguousBearing());

      double course = MWC.Algorithms.Conversions.Rads2Degs(sensorTrack.getNearestTo(cut.getDTG())[0].getCourse());
      assertEquals("correct ownship course", 291d, course);
      
    }
    public void testLoadBRT() throws IOException, Exception
    {
      
      final String fileContent = "1263297600.000000, 69.00\n"
          + "1263297840.000000, 58.90\n" 
          + "1263298080.000000, 56.70";
     
      final BRTData data =  BRTImporter.readBRT(new ByteArrayInputStream(fileContent
          .getBytes(StandardCharsets.UTF_8)));
      assertEquals("Size of bearing is correct", 3, data.getBearings().length);
      assertEquals("Size of time is correct", 3, data.getTimes().length);
      assertEquals("Reading first bearing correctly", 69.0, data
          .getBearings()[0], 0.001);
      assertEquals("Reading second bearing correctly", 58.9, data
          .getBearings()[1], 0.001);
      assertEquals("Reading third bearing correctly", 56.7, data
          .getBearings()[2], 0.001);

      assertEquals("Reading first date correctly", 1263297600000L, (long) data
          .getTimes()[0]);
      assertEquals("Reading first date correctly", 1263297840000L, (long) data
          .getTimes()[1]);
      assertEquals("Reading first date correctly", 1263298080000L, (long) data
          .getTimes()[2]);

    }
  }

  public static class ImportBRTAction implements Action
  {
    private final SensorWrapper sensorAdded;
    private final TrackWrapper track;

    public ImportBRTAction(final SensorWrapper sensorAdded,
        final TrackWrapper track)
    {
      super();
      this.sensorAdded = sensorAdded;
      this.track = track;
    }

    @Override
    public void execute()
    {
      track.add(sensorAdded);
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
      track.removeElement(sensorAdded);
    }

  }

  private static ImportBRTAction createImportAction(final BRTHelper helper,
      final BRTData brtData, final String fName)
  {
    // find track
    final TrackWrapper track = helper.select();

    // create sensor
    String fileName = new File(fName).getName();
    if ( fileName.lastIndexOf('.') > 0 )
    {
      fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    }
    final SensorWrapper sensor = new SensorWrapper(fileName);

    // set default color
    sensor.setColor(helper.getColor());

    // set array offset (if needed)
    if (helper.isTowed())
    {
      final WorldDistance offset = helper.arrayOffset();
      if (offset != null)
      {
        final WorldDistance.ArrayLength length = new WorldDistance.ArrayLength(
            offset);
        sensor.setSensorOffset(length);
      }
    }

    // populate the sensor
    loadThese(brtData, sensor, track, helper
        .defaultLength(), helper.isTowed());

    return new ImportBRTAction(sensor, track);
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

  /**
   * loop through layers, find tracks
   *
   * @param layers
   *          Layers to loop
   * @return Tracks available in the layers
   */
  public static TrackWrapper[] getTracks(final Layers layers)
  {
    final List<TrackWrapper> result = new ArrayList<>();

    final Enumeration<Editable> layerIterator = layers.elements();

    while (layerIterator.hasMoreElements())
    {
      final Layer layer = (Layer) layerIterator.nextElement();
      if (TrackWrapper.class.isAssignableFrom(layer.getClass()))
      {
        result.add((TrackWrapper) layer);
      }
    }

    return result.toArray(new TrackWrapper[]
    {});
  }

  private static void loadThese(final BRTData data, final SensorWrapper sensor,
      final TrackWrapper track, final WorldDistance defaultLength,
      final boolean isAmbiguous)
  {
    final int num = data.getTimes().length;
    for (int i = 0; i < num; i++)
    {
      final long thisT = data.getTimes()[i]; 
      final HiResDate dtg = new HiResDate(thisT);
      
      final Double course;
      final ArrayLength offset = sensor.getSensorOffset();
      
      // is it ambiguous, and do we have an offset?
      if(isAmbiguous && offset != null && offset.getValue() != 0)
      {
        // ok, walk back down the track for the relevant distnace
        
        // work out where the ship was when walk back through the
        // offset distance
        final FixWrapper fix = WormInHoleOffset.getWormOffsetFor(track, dtg, offset);
        
        // if we found a position, retrieve the course
        course = fix != null ? MWC.Algorithms.Conversions.Rads2Degs(fix.getCourse()) : null;
      }
      else
      {
        // just use the current location
        // find the ownship course at this time
        final Watchable[] nearest = track.getNearestTo(new HiResDate(thisT));
        if(nearest != null && nearest.length > 0)
        {
          final FixWrapper fix = (FixWrapper) nearest[0];
          course = MWC.Algorithms.Conversions.Rads2Degs(fix.getCourse());
        }
        else
        {
          course = null;
        }
      }
      
      // did we find a course?
      if(course != null)
      {
        final double relBearingDegs = data.getBearings()[i];
        
        // convert from rel brg to bearing by adding to course
        double bearingDegs = course + relBearingDegs;
        if(bearingDegs >= 360d)
        {
          bearingDegs -= 360d;
        }
        
        // if it's towed (ambiguous) we need to generate the
        // mirror (ambiguous) bearing
        Double ambigDegs = isAmbiguous ? course - relBearingDegs
            : null;
        if(ambigDegs != null && ambigDegs >= 360d)
        {
          ambigDegs -= 360d;
        }
        
        final SensorContactWrapper newS = new SensorContactWrapper(track
            .getName(), dtg, null, bearingDegs, ambigDegs, null, null,
            null, "pt:" + i, LineStylePropertyEditor.SOLID, sensor
                .getName());

        // was a length specified?
        if (defaultLength != null)
        {
          newS.setRange(defaultLength);
        }

        // ok, done.
        sensor.add(newS);
      }
      
    }
  }

  /**
   * Default Constructor
   *
   * @param hB
   *          Contains the information given by the user using the UI.
   * @param _layers
   *          layers available in the plot
   */
  public BRTImporter()
  {
    super();
  }

  public ImportBRTAction importThis(final BRTHelper brtHelper,
      final String fName, final InputStream is) throws Exception
  {
    final BRTData brtData = readBRT(is);

    return createImportAction(brtHelper, brtData, fName);
  }

  private static BRTData readBRT(final InputStream is) throws NumberFormatException, IOException, Exception 
  {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line;

    final ArrayList<Long> times = new ArrayList<>();
    final ArrayList<Double> bearings = new ArrayList<>();
    while ((line = reader.readLine()) != null)
    {
      final String[] tokens = line.split(",");
      if (tokens.length != 2)
      {
        throw new Exception(
            "Wrong format in BRT file. Please consult the user manual");
      }
      final Long time = (long) (Double.parseDouble(tokens[0]) * 1000);
      final Double bearing = Double.parseDouble(tokens[1]);
      times.add(time);
      bearings.add(bearing);
    }

    final BRTData brtData = new BRTData(times.toArray(new Long[]
    {}), bearings.toArray(new Double[]
    {}));

    return brtData;
  }
}
