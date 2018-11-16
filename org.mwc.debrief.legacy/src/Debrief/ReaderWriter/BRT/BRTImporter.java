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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
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

    Layer[] allLayers = new Layer[]
    {new NarrativeWrapper("Test0"), new NarrativeWrapper("Test1"), createTrack(
        "1", DebriefColors.BLUE), new NarrativeWrapper("Test2"), createTrack(
            "2", DebriefColors.BLUE), createTrack("3", DebriefColors.RED)};

    public void testFindTrack()
    {

      final Layers testLayers = new Layers();
      for (final Layer l : allLayers)
      {
        testLayers.addThisLayer(l);
      }

      TrackWrapper selectedTrack;
      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting first track, having several blue tracks",
          allLayers[2], selectedTrack);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting first track, having several blue tracks",
          allLayers[4], selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[4]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one blue track", allLayers[4],
          selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[4]);
      testLayers.addThisLayer(allLayers[5]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from two blue tracks", allLayers[4],
          selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[5]);

      selectedTrack = findTrack(getTracks(testLayers));
      assertEquals("Selecting track from only one track", allLayers[5],
          selectedTrack);
    }

    public void testGetTracks()
    {
      /*
       * final Layers testLayers = new Layers(); for (final Layer l : allLayers) {
       * testLayers.addThisLayer(l); }
       *
       * final BRTImporter importer = new BRTImporter(null, testLayers); final TrackWrapper[] tracks
       * = importer.getTracks(); assertEquals("getTrack getting TrackWrapper", allLayers[2],
       * tracks[0]); assertEquals("getTrack getting TrackWrapper", allLayers[4], tracks[1]);
       * assertEquals("getTrack getting TrackWrapper", allLayers[5], tracks[2]);
       * assertEquals("amount of tracks extracted", 3, tracks.length);
       */
    }

    public void testImport()
    {
      /*
       * final String filename = "TestStringInputStreamFile"; final String fileContent =
       * "1263297600.000000, 69.00\n" + "1263297840.000000, 58.90\n" + "1263298080.000000, 56.70";
       *
       * final BRTHelperHeadless headless = new BRTHelperHeadless(true, null, DebriefColors.BLUE,
       * null, 0); final Layers testLayers = new Layers(); for (final Layer l : allLayers) {
       * testLayers.addThisLayer(l); }
       *
       * final BRTImporter importer = new BRTImporter(headless, testLayers); try {
       * importer.importThis(filename, new ByteArrayInputStream(fileContent
       * .getBytes(StandardCharsets.UTF_8))); } catch (final Exception e) {
       * Assert.fail(e.getMessage()); }
       *
       * final BRTData data = importer.getBrtData(); assertEquals("Size of bearing is correct", 3,
       * data.getBearings().length); assertEquals("Size of time is correct", 3,
       * data.getTimes().length); assertEquals("Reading first bearing correctly", 69.0, data
       * .getBearings()[0], 0.001); assertEquals("Reading second bearing correctly", 58.9, data
       * .getBearings()[1], 0.001); assertEquals("Reading third bearing correctly", 56.7, data
       * .getBearings()[2], 0.001);
       *
       * assertEquals("Reading first date correctly", 1263297600000L, (long) data .getTimes()[0]);
       * assertEquals("Reading first date correctly", 1263297840000L, (long) data .getTimes()[1]);
       * assertEquals("Reading first date correctly", 1263298080000L, (long) data .getTimes()[2]);
       */
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
      final BRTData brtData, final String fName) throws Exception
  {
    // find track
    final TrackWrapper track = helper.select();

    // create sensor
    final SensorWrapper sensor = new SensorWrapper(fName);

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
    loadThese(brtData, sensor, track, helper.arrayOffset(), helper
        .defaultLength());

    return new ImportBRTAction(sensor, track);
  }

  public static TrackWrapper findTrack(final TrackWrapper[] allTracks)
  {
    if (allTracks.length == 1)
    {
      return allTracks[0];
    }
    int amountOfBlueTracks = 0, indexOfBlueTrack = 0;
    for (int i = 0; i < allTracks.length; i++)
    {
      if (DebriefColors.BLUE.equals(allTracks[i].getTrackColor()))
      {
        ++amountOfBlueTracks;
        indexOfBlueTrack = 0;
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
      final TrackWrapper track, final WorldDistance arrayOffset,
      final WorldDistance defaultLength)
  {
    final int num = data.getTimes().length;
    for (int i = 0; i < num; i++)
    {
      final long thisT = data.getTimes()[i]; // TODO: this is expecting millis, check if we're
                                             // converting data-file seconds to millis
      final double thisB = data.getBearings()[i];
      final SensorContactWrapper newS = new SensorContactWrapper(track
          .getName(), new HiResDate(thisT), null, thisB, -thisB, null, null,
          Color.yellow, "pt:" + i, LineStylePropertyEditor.SOLID, sensor
              .getName());

      if (defaultLength != null)
      {
        newS.setRange(defaultLength);
      }

      sensor.add(newS);
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
    final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line;
    int lineCounter = 0;

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

      ++lineCounter;
    }

    final BRTData brtData = new BRTData(times.toArray(new Long[]
    {}), bearings.toArray(new Double[]
    {}));

    System.out.println(lineCounter + " lines read successfully");

    return createImportAction(brtHelper, brtData, fName);
  }
}
