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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import junit.framework.TestCase;

public class OTH_Importer
{

  public static class OTH_ImporterTest extends TestCase
  {

    static public final String TEST_ALL_TEST_TYPE = "UNIT";

  }

  private static class ImportOTHAction implements Action
  {

    private final List<TrackWrapper> _tracks;
    private final List<BaseLayer> _ellipseLayers;
    private final Layers _layers;

    public ImportOTHAction(final List<TrackWrapper> tracks,
        final List<BaseLayer> ellipseLayers, final Layers layers,
        final boolean importEllipses)
    {
      super();

      // prevent these objects from being null, to reduce null-checking when we execute
      _tracks = tracks != null ? tracks : Collections.emptyList();
      _ellipseLayers = ellipseLayers != null && importEllipses ? ellipseLayers
          : Collections.emptyList();
      _layers = layers;
    }

    @Override
    public void execute()
    {
      for (TrackWrapper t : _tracks)
      {
        _layers.addThisLayer(t);
      }

      for (BaseLayer b : _ellipseLayers)
      {
        _layers.addThisLayer(b);
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
      for (TrackWrapper t : _tracks)
      {
        _layers.removeThisLayer(t);
      }

      for (BaseLayer b : _ellipseLayers)
      {
        _layers.removeThisLayer(b);
      }
    }

  }

  private static ImportOTHAction createImportAction(final OTH_Helper helper,
      final OTH_Data brtData, final Layers layers)
  {
    // are we loading ellipses?
    final boolean doEllipses = helper.generateEllipses();

    return new ImportOTHAction(brtData.getTracks(), brtData.getEllipseLayers(),
        layers, doEllipses);
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

  private static OTH_Data read_OTH(final InputStream is)
      throws NumberFormatException, IOException, Exception
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

    List<TrackWrapper> tracks = null;
    List<BaseLayer> ellipseLayers = null;
    final OTH_Data brtData = new OTH_Data(tracks, ellipseLayers);

    return brtData;
  }

  /**
   * Default Constructor
   *
   * @param hB
   *          Contains the information given by the user using the UI.
   * @param _layers
   *          layers available in the plot
   */
  public OTH_Importer()
  {
    super();
  }

  public ImportOTHAction importThis(final OTH_Helper brtHelper,
      final InputStream is,final Layers layers) throws Exception
  {
    final OTH_Data brtData = read_OTH(is);

    return createImportAction(brtHelper, brtData, layers);
  }


  private static final String HEADER_STR = "MSGID";
  private static final String TRACK_STR = "CTC";
  private static final String POS_STR = "CTC";
  
  public static boolean canLoad(final String fileName, final ErrorLogger logger)
  {
    boolean res = false;
    BufferedReader r = null;
    FileInputStream fis = null;
    try
    {
      fis = new FileInputStream(fileName);
      r = new BufferedReader(new InputStreamReader(fis));
      boolean hasHeader = false;
      boolean hasPosition = false;
      boolean hasTrack = false;

      final int MAX_LINES = 100;
      int ctr = 0;
      while (ctr < MAX_LINES && !hasHeader && !hasPosition && !hasTrack)
      {
        // try this line
        final String line = r.readLine();

        if (!hasHeader)
        {
          hasHeader = line.startsWith(HEADER_STR);
        }
        if (!hasPosition)
        {
          hasPosition = line.startsWith(POS_STR);
        }
        if (!hasTrack)
        {
          hasTrack = line.startsWith(TRACK_STR);
        }

        // are we ready?
        if (hasHeader && hasPosition && hasTrack)
        {
          res = true;
          break;
        }
      }
    }
    catch (Exception e)
    {
      logger.logError(ErrorLogger.ERROR, "Trouble whilst checking valid OTH",
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
      catch (IOException e)
      {
        logger.logError(ErrorLogger.ERROR, "Couldn't close file:" + fileName,
            e);
      }
    }
    return res;
  }
}
