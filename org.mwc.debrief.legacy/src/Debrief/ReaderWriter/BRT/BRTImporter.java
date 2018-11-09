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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

public class BRTImporter
{

  /**
   * layer that we are going to be load data to
   * 
   */
  private final Layers _layers;

  /**
   * brt info given by user.
   */
  private final BRTHelper _brtHelper;

  private BRTData brtData;

  /**
   * Default Constructor
   * 
   * @param hB
   *          Contains the information given by the user using the UI.
   * @param _layers
   *          layers available in the plot
   */
  public BRTImporter(BRTHelper hB, Layers _layers)
  {
    super();
    this._layers = _layers;
    this._brtHelper = hB;
  }

  public BRTData getBrtData()
  {
    return brtData;
  }

  /**
   * loop through layers, find tracks
   * 
   * @param layers
   *          Layers to loop
   * @return Tracks available in the layers
   */
  private TrackWrapper[] getTracks()
  {
    List<TrackWrapper> result = new ArrayList<>();

    Enumeration<Editable> layerIterator = _layers.elements();

    while (layerIterator.hasMoreElements())
    {
      Layer layer = (Layer) layerIterator.nextElement();
      if (TrackWrapper.class.isAssignableFrom(layer.getClass()))
      {
        result.add((TrackWrapper) layer);
      }
    }

    return result.toArray(new TrackWrapper[]
    {});
  }

  public ImportBRTAction importThis(final String fName, final InputStream is)
  {
    // TODO.
    return createImportAction(fName);
  }

  private ImportBRTAction createImportAction(final String fName)
  {
    // find track
    TrackWrapper track = findTrack();
    SensorWrapper sensor = new SensorWrapper(fName);
    // load data
    // determine color/cut-length to sensor
    // create sensor
    final ImportBRTAction action = new ImportBRTAction(sensor, track);
    // set default color
    // set array offset (if needed)
    //loadThese(Sensor…, color, cut-length)
    return action;
  }

  private TrackWrapper findTrack()
  {
    // TODO
    return null;
  }

  public static class ImportBRTAction implements Action
  {
    private SensorWrapper sensorAdded;
    private TrackWrapper track;

    public ImportBRTAction(SensorWrapper sensorAdded, TrackWrapper track)
    {
      super();
      this.sensorAdded = sensorAdded;
      this.track = track;
    }

    @Override
    public boolean isUndoable()
    {
      return true;
    }

    @Override
    public boolean isRedoable()
    {
      return true;
    }

    @Override
    public void undo()
    {
      track.removeElement(sensorAdded);
    }

    @Override
    public void execute()
    {
      track.add(sensorAdded);
    }

  }

  public static class BRTImporterTest extends TestCase
  {

    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    Layer[] allLayers = new Layer[]
    {new NarrativeWrapper("Test0"), new NarrativeWrapper("Test1"),
        new LightweightTrackWrapper("firstBlue", true, true, Color.BLUE, 1),
        new NarrativeWrapper("Test2"), new LightweightTrackWrapper("secondBlue",
            true, true, Color.BLUE, 1), new LightweightTrackWrapper("firstRed",
                true, true, Color.RED, 1)};

    public void testGetTracks()
    {
      Layers testLayers = new Layers();
      for (Layer l : allLayers)
      {
        testLayers.addThisLayer(l);
      }

      BRTImporter importer = new BRTImporter(null, testLayers);
      TrackWrapper[] tracks = importer.getTracks();
      assertEquals("getTrack getting TrackWrapper", allLayers[2], tracks[0]);
      assertEquals("getTrack getting TrackWrapper", allLayers[4], tracks[1]);
      assertEquals("getTrack getting TrackWrapper", allLayers[5], tracks[1]);
      assertEquals("amount of tracks extracted", 3, tracks.length);
    }

    public void findTrack()
    {
      Layers testLayers = new Layers();
      for (Layer l : allLayers)
      {
        testLayers.addThisLayer(l);
      }

      BRTHelperHeadless headless = new BRTHelperHeadless(true, null, null, null,
          0);

      BRTImporter importer = new BRTImporter(headless, testLayers);
      TrackWrapper selectedTrack = importer.findTrack();
      assertEquals("Selecting first track, having several blue tracks",
          (TrackWrapper) allLayers[2], selectedTrack);

      importer = new BRTImporter(new BRTHelperHeadless(true, null, null, null,
          1), testLayers);
      selectedTrack = importer.findTrack();
      assertEquals("Selecting first track, having several blue tracks",
          (TrackWrapper) allLayers[4], selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[4]);

      importer = new BRTImporter(headless, testLayers);
      selectedTrack = importer.findTrack();
      assertEquals("Selecting track from only one blue track",
          (TrackWrapper) allLayers[4], selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[4]);
      testLayers.addThisLayer(allLayers[5]);

      importer = new BRTImporter(headless, testLayers);
      selectedTrack = importer.findTrack();
      assertEquals("Selecting track from only one blue track",
          (TrackWrapper) allLayers[4], selectedTrack);

      testLayers.clear();
      testLayers.addThisLayer(allLayers[0]);
      testLayers.addThisLayer(allLayers[5]);

      importer = new BRTImporter(headless, testLayers);
      selectedTrack = importer.findTrack();
      assertEquals("Selecting track from only one track",
          (TrackWrapper) allLayers[5], selectedTrack);
    }

    public void testImport()
    {
      final String filename = "TestStringInputStreamFile";
      final String fileContent = "1263297600.000000, 69.00\n"
          + "1263297840.000000, 58.90\n" + "1263298080.000000, 56.70";

      BRTHelperHeadless headless = new BRTHelperHeadless(true, null, Color.BLUE,
          null, 0);
      Layers testLayers = new Layers();
      for (Layer l : allLayers)
      {
        testLayers.addThisLayer(l);
      }

      BRTImporter importer = new BRTImporter(headless, testLayers);
      importer.importThis(filename, new ByteArrayInputStream(fileContent
          .getBytes(StandardCharsets.UTF_8)));

      BRTData data = importer.getBrtData();
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
}
