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
package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.DynamicShapeLayer;
import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.ExportLayerAsSingleItem;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.ReaderWriter.ReaderMonitor;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to read in a complete replay file. The class knows of the types of data in Replay format,
 * and users the correct import filters accordingly.
 */

public class ImportReplay extends PlainImporterBase
{

  /**
   * interface for class that is able to retrieve the import mode from the user
   * 
   * @author ianmayo
   * @see ImportReplay.TRACK_IMPORT_MODE
   */
  public static interface ProvidesModeSelector
  {
    public String getSelectedImportMode(final String trackName);
  }

  /**
   * the format we use to parse text
   */
  private final java.text.DateFormat dateFormat =
      new java.text.SimpleDateFormat("yyMMdd HHmmss.SSS");

  private static Vector<PlainLineImporter> _theImporters;

  static private Vector<doublet> colors; // list of Replay colours

  static public final String NARRATIVE_LAYER = "Narratives";

  static private final String ANNOTATION_LAYER = "Annotations";

  /**
   * the prefs provider
   * 
   */
  private static ToolParent _myParent;

  /**
   * the list of formatting objects we know about
   */
  private final LayersFormatter[] _myFormatters =
  {new FormatTracks()};

  /**
   * a list of the sensors we've imported
   * 
   */
  private HashMap<TrackWrapper, Vector<SensorWrapper>> _pendingSensors =
      new HashMap<TrackWrapper, Vector<SensorWrapper>>();
  
  /** a list of any exiting tracks that got modified (so we can tell people they've moved
   * at the end of hte operation 
   */
  private List<TrackWrapper> _existingTracksThatMoved = new ArrayList<TrackWrapper>();

  /**
   * the property name we use for importing tracks (DR/ATG)
   * 
   */
  public final static String TRACK_IMPORT_MODE = "TRACK_IMPORT_MODE";

  /**
   * the property values for importing modes
   * 
   */
  public final static String IMPORT_AS_DR = "DR_IMPORT";
  public final static String IMPORT_AS_OTG = "OTG_IMPORT";
  public final static String ASK_THE_AUDIENCE = "ASK_AUDIENCE";

  /**
   * constructor, initialise Vector with the list of non-Fix items which we will be reading in
   */
  public ImportReplay()
  {

    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    _myTypes = new String[]
    {".rep", ".dsf", ".dtf"};

    checkImporters();

    initialiseColours();
  }

  private synchronized static void initialiseColours()
  {
    // create a list of colours
    if (colors == null)
    {
      colors = new Vector<doublet>(0, 1);
      colors.addElement(new doublet("@", Color.white));
      colors.addElement(new doublet("A", DebriefColors.BLUE));
      colors.addElement(new doublet("B", DebriefColors.GREEN));
      colors.addElement(new doublet("C", DebriefColors.RED));

      colors.addElement(new doublet("D", DebriefColors.YELLOW));
      colors.addElement(new doublet("E", DebriefColors.MAGENTA));
      colors.addElement(new doublet("F", DebriefColors.ORANGE));
      colors.addElement(new doublet("G", DebriefColors.PURPLE));
      colors.addElement(new doublet("H", DebriefColors.CYAN));
      colors.addElement(new doublet("I", DebriefColors.BROWN));
      colors.addElement(new doublet("J", DebriefColors.LIGHT_GREEN));
      colors.addElement(new doublet("K", DebriefColors.PINK));
      colors.addElement(new doublet("L", DebriefColors.GOLD));
      colors.addElement(new doublet("M", DebriefColors.LIGHT_GRAY));
      colors.addElement(new doublet("N", DebriefColors.GRAY));
      colors.addElement(new doublet("O", DebriefColors.DARK_GRAY));
      colors.addElement(new doublet("P", DebriefColors.WHITE));
      colors.addElement(new doublet("Q", DebriefColors.BLACK));
      colors.addElement(new doublet("R", DebriefColors.MEDIUM_BLUE));
      colors.addElement(new doublet("S", DebriefColors.DARK_BLUE));
    }
  }

  /*****************************************************************************
   * member methods
   ****************************************************************************/

  /**
   * format a date using our format
   */
  public static String formatThis(final HiResDate val)
  {
    final String res = DebriefFormatDateTime.toStringHiRes(val);
    return res;
  }

  /**
   * initialise the tool, so that it knows where to get its prefs details
   * 
   * @param theParent
   */
  public static void initialise(final ToolParent theParent)
  {
    _myParent = theParent;
  }

  /**
   * function to initialise the list of importers
   */
  private synchronized static void checkImporters()
  {
    if (_theImporters == null)
    {
      // create the array of import handlers, by
      _theImporters = new Vector<PlainLineImporter>(0, 1);

      // adding handler we (currently) know of
      _theImporters.addElement(new ImportCircle());
      _theImporters.addElement(new ImportRectangle());
      _theImporters.addElement(new ImportLine());
      _theImporters.addElement(new ImportVector());
      _theImporters.addElement(new ImportEllipse());
      _theImporters.addElement(new ImportPeriodText());
      _theImporters.addElement(new ImportTimeText());
      _theImporters.addElement(new ImportFixFormatter());
      _theImporters.addElement(new ImportLabel());
      _theImporters.addElement(new ImportWheel());
      _theImporters.addElement(new ImportBearing());
      _theImporters.addElement(new ImportNarrative());
      _theImporters.addElement(new ImportNarrative2());
      _theImporters.addElement(new ImportSensor());
      _theImporters.addElement(new ImportSensor2());
      _theImporters.addElement(new ImportSensor3());
      _theImporters.addElement(new ImportTMA_Pos());
      _theImporters.addElement(new ImportTMA_RngBrg());
      _theImporters.addElement(new ImportPolygon());
      _theImporters.addElement(new ImportPolyline());
      // note that we don't rely on ImportFix for importing Replay fixes, since
      // they are handled by the ImportReplay method. We are including it in
      // this list so that we can use it as an exporter
      _theImporters.addElement(new ImportFix());

      _theImporters.addElement(new ImportDynamicRectangle());
      _theImporters.addElement(new ImportDynamicCircle());
      _theImporters.addElement(new ImportDynamicPolygon());

      _theImporters.addElement(new ImportSensorArc());
    }
  }

  private HiResDate processReplayFix(final ReplayFix rf)
  {
    final HiResDate res = rf.theFix.getTime();

    // find the track name
    final String theTrack = rf.theTrackName;
    final Color thisColor = replayColorFor(rf.theSymbology);

    // create the wrapper for this annotation
    final FixWrapper thisWrapper = new FixWrapper(rf.theFix);

    // overwrite the label, if there's one there
    if (rf.label != null)
    {
      thisWrapper.setLabel(rf.label);
      thisWrapper.setUserLabelSupplied(true);
    }

    // keep track of the wrapper for this track
    // is there a layer for this track?
    TrackWrapper trkWrapper = (TrackWrapper) getLayerFor(theTrack);

    // have we found the layer?
    if(trkWrapper != null)
    {
      // ok, remember that we've changed this track
      if(!_existingTracksThatMoved.contains(trkWrapper))
      {
        _existingTracksThatMoved.add(trkWrapper);
      }
    }
    else
    {
      // ok, see if we're importing it as DR or ATG (or ask the audience)
      String importMode = _myParent.getProperty(TRACK_IMPORT_MODE);

      // catch a missing import mode
      if (importMode == null)
      {
        // belt & braces it is then...
        importMode = ImportReplay.ASK_THE_AUDIENCE;
      }

      if (importMode.equals(ImportReplay.ASK_THE_AUDIENCE))
      {
        if (_myParent instanceof ProvidesModeSelector)
        {
          final ProvidesModeSelector selector =
              (ProvidesModeSelector) _myParent;
          importMode = selector.getSelectedImportMode(theTrack);
        }
      }

      TrackSegment initialLayer = null;

      if (importMode == null)
      {
        // and drop out of the whole affair
        throw new RuntimeException("User cancelled import");
      }
      else if (importMode.equals(ImportReplay.IMPORT_AS_OTG))
      {
        initialLayer = new TrackSegment(TrackSegment.ABSOLUTE);
      }
      else if (importMode.equals(ImportReplay.IMPORT_AS_DR))
      {
        initialLayer = new TrackSegment(TrackSegment.RELATIVE);
      }

      // now create the wrapper
      trkWrapper = new TrackWrapper();

      // give it the data container
      trkWrapper.add(initialLayer);

      // if this was relative, make it plot as italic
      if (initialLayer.getPlotRelative())
      {
        // ok, retrieve the original font, and make it italic
        trkWrapper.setTrackFont(trkWrapper.getTrackFont().deriveFont(
            Font.ITALIC));
      }

      // get the colour for this track
      trkWrapper.setColor(thisColor);
      trkWrapper.setSymbolColor(thisColor);

      // set the sym type for the track
      final String theSymType = replayTrackSymbolFor(rf.theSymbology);
      trkWrapper.setSymbolType(theSymType);

      // store the track-specific data
      trkWrapper.setName(theTrack);

      // add our new layer to the Layers object
      addLayer(trkWrapper);

      // see if there is any formatting to be done
      // lastly - see if the layers object has some formatters
      Iterator<INewItemListener> newIiter =
          getLayers().getNewItemListeners().iterator();
      while (newIiter.hasNext())
      {
        INewItemListener newI = newIiter.next();
        newI.newItem(trkWrapper, null, null);
      }
    }

    // Note: line style & thickness only (currently) apply to whole tracks,
    // so we will effectively just use the last value read in.
    if (rf.theSymbology != null && rf.theSymbology.length() > 2)
    {
      trkWrapper.setLineStyle(ImportReplay.replayLineStyleFor(rf.theSymbology
          .substring(2)));
      if (rf.theSymbology.length() > 3)
      {
        trkWrapper.setLineThickness(ImportReplay
            .replayLineThicknesFor(rf.theSymbology.substring(3)));
      }
    }

    // add the fix to the track
    trkWrapper.addFix(thisWrapper);

    // let's also tell the fix about it's track
    ((FixWrapper) thisWrapper).setTrackWrapper(trkWrapper);

    // also, see if this fix is specifying a different colour to use
    if (thisColor != trkWrapper.getColor())
    {
      // give this fix it's unique colour
      thisWrapper.setColor(thisColor);
    }

    // lastly - see if the layers object has some formatters
    Iterator<INewItemListener> newIiter =
        getLayers().getNewItemListeners().iterator();
    while (newIiter.hasNext())
    {
      Layers.INewItemListener newI = (Layers.INewItemListener) newIiter.next();
      newI.newItem(trkWrapper, thisWrapper, rf.theSymbology);
    }

    return res;

  }

  private HiResDate processSensorContactWrapper(final SensorContactWrapper sw)
  {
    final HiResDate res = sw.getTime();

    SensorWrapper thisSensor = null;

    // do we have a sensor capable of handling this contact?
    final String sensorName = sw.getSensorName();
    String trackName = sw.getTrackName();
    Object val = getLayerFor(trackName);

    // if we failed to get the trackname, try shortening it -
    // it may have been mangled by BabelFish
    if (val == null)
    {
      if (trackName.length() > 6)
      {
        String tmpTrackName = trackName.substring(0,6);
        val = getLayerFor(tmpTrackName);
        
        if(val != null)
        {
          // ok, adopt this track name
          trackName = tmpTrackName;
        }
      }
    }
    
    // so, we've found a track - see if it holds this sensor
    // SPECIAL HANDLING: it may actually still be a null. This is ok,
    // because post-load the invite the user to select
    // the parent track for these "null" tracks.
    final TrackWrapper theTrack = (TrackWrapper) val;

    Vector<SensorWrapper> thisTrackSensors = _pendingSensors.get(theTrack);

    if (thisTrackSensors == null)
    {
      thisTrackSensors = new Vector<SensorWrapper>();
      _pendingSensors.put(theTrack, thisTrackSensors);
    }

    final Iterator<SensorWrapper> iter = thisTrackSensors.iterator();

    // step through this track' sensors
    if (iter != null)
    {
      while (iter.hasNext())
      {
        final SensorWrapper sensorw = (SensorWrapper) iter.next();

        // is this our sensor?
        if (sensorw.getName().equals(sensorName))
        {
          // cool, drop out
          thisSensor = sensorw;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors

    // did we find it?
    if (thisSensor == null)
    {
      // then create it
      thisSensor = new SensorWrapper(sensorName);

      // set it's colour to the colour of the first data point
      thisSensor.setColor(sw.getColor());

      // also set it's name
      thisSensor.setTrackName(sw.getTrackName());

      // oh, and the track
      thisSensor.setHost(theTrack);

      // ok, now remember it
      thisTrackSensors.add(thisSensor);
    }

    // so, we now have the wrapper. have a look to see if the colour
    // of this data item is the same
    // as the sensor - in which case we will erase the colour for
    // this data item so that it always takes the colour of it's parent
    if (sw.getColor().equals(thisSensor.getColor()))
    {
      // clear the colour - so it takes it form it's parent
      sw.setColor(null);
    }

    // now add the new contact to this sensor
    thisSensor.add(sw);

    return res;
  }

  private HiResDate
      processDynamicShapeWrapper(final DynamicTrackShapeWrapper sw)
  {
    final HiResDate res = null;

    DynamicTrackShapeSetWrapper thisShape = null;

    // do we have a sensor capable of handling this contact?
    final String sensorName = sw.getSensorName();
    String trackName = sw.getTrackName();
    Object val = getLayerFor(trackName);

    // if we failed to get the trackname, try shortening it -
    // it may have been mangled by BabelFish
    if (val == null)
      val = getLayerFor(trackName = trackName.substring(6));

    // did we get anything?
    // is this indeed a sensor?
    if (val == null || !(val instanceof TrackWrapper))
      return res;

    // so, we've found a track - see if it holds this shape
    final TrackWrapper theTrack = (TrackWrapper) val;
    final Enumeration<Editable> iter = theTrack.getDynamicShapes().elements();

    // step through this track' shape
    if (iter != null)
    {
      while (iter.hasMoreElements())
      {
        final DynamicTrackShapeSetWrapper shape =
            (DynamicTrackShapeSetWrapper) iter.nextElement();

        // is this our sensor?
        if (shape.getName().equals(sensorName))
        {
          // cool, drop out
          thisShape = shape;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors

    // did we find it?
    if (thisShape == null)
    {
      // then create it
      thisShape = new DynamicTrackShapeSetWrapper(sensorName);

      theTrack.add(thisShape);
    }

    // now add the new contact to this sensor
    thisShape.add(sw);

    return res;
  }

  private HiResDate processContactWrapper(final TMAContactWrapper sw)
  {
    final HiResDate res = sw.getTime();

    TMAWrapper thisWrapper = null;

    // do we have a sensor capable of handling this contact?
    final String solutionName = sw.getSolutionName();

    String trackName = sw.getTrackName();
    Object val = getLayerFor(trackName);

    // if we failed to get the trackname, try shortening it -
    // it may have been mangled by BabelFish
    if (val == null)
      val = getLayerFor(trackName = trackName.substring(6));

    // did we get anything?
    // is this indeed a sensor?
    if (val == null || !(val instanceof TrackWrapper))
      return res;

    // so, we've found a track - see if it holds this solution
    final TrackWrapper theTrack = (TrackWrapper) val;
    final Enumeration<Editable> iter = theTrack.getSolutions().elements();

    // step through this track's solutions
    if (iter != null)
    {
      while (iter.hasMoreElements())
      {
        final TMAWrapper sensorw = (TMAWrapper) iter.nextElement();

        // is this our sensor?
        if (sensorw.getName().equals(solutionName))
        {
          // cool, drop out
          thisWrapper = sensorw;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors

    // did we find it?
    if (thisWrapper == null)
    {
      // then create it
      thisWrapper = new TMAWrapper(solutionName);

      // set it's colour to the colour of the first data point
      thisWrapper.setColor(sw.getColor());

      // also set it's name
      thisWrapper.setTrackName(sw.getTrackName());

      theTrack.add(thisWrapper);
    }

    // so, we now have the wrapper. have a look to see if the colour
    // of this data item is the same
    // as the sensor - in which case we will erase the colour for
    // this data item so that it
    // always takes the colour of it's parent
    if (sw.getColor().equals(thisWrapper.getColor()))
    {
      // clear the colour - so it takes it form it's parent
      sw.setColor(null);
    }

    // lastly inform the sensor contact of it's parent
    sw.setTMATrack(thisWrapper);

    // now add the new contact to this sensor
    thisWrapper.add(sw);

    return res;
  }

  /**
   * parse this line
   * 
   * @param theLine
   *          the line to parse
   */
  public HiResDate readLine(final String theLine) throws java.io.IOException
  {
    HiResDate res = null;

    // is this line invalid
    if (theLine.length() <= 0)
      return null;

    // ok, trim any leading/trailing whitespace
    final String line = theLine.trim();

    // what type of item is this?
    final PlainLineImporter thisOne = getImporterFor(line);

    // check that we have found an importer
    if (thisOne == null)
    {
      // just check it wasn't a comment
      if (line.startsWith(";;"))
      {
        // don't bother, it's just a comment
      }
      else
      {
        MWC.Utilities.Errors.Trace.trace("Annotation type not recognised for:"
            + line);
      }
      return null;
    }

    // now read it in.
    final Object thisObject = thisOne.readThisLine(line);

    // see if we are going to do any special processing

    // is this a fix?
    if (thisObject instanceof ReplayFix)
    {
      res = processReplayFix((ReplayFix) thisObject);
    }
    else if (thisObject instanceof DynamicShapeWrapper)
    {
      proccessShapeWrapper(thisOne, thisObject);
      DynamicShapeWrapper thisWrapper = (DynamicShapeWrapper) thisObject;
      String trackName = thisWrapper.getTrackName();
      DynamicShapeLayer dsl = (DynamicShapeLayer) getLayerFor(trackName);
      if (dsl == null)
      {
        dsl = new DynamicShapeLayer();
        dsl.setName(trackName);
        addLayer(dsl);
      }
      addToLayer(thisWrapper, dsl);
    }
    else if (thisObject instanceof SensorContactWrapper)
    {
      res = processSensorContactWrapper((SensorContactWrapper) thisObject);
    }
    else if (thisObject instanceof DynamicTrackShapeWrapper)
    {
      res = processDynamicShapeWrapper((DynamicTrackShapeWrapper) thisObject);
    }
    else if (thisObject instanceof TMAContactWrapper)
    {
      res = processContactWrapper((TMAContactWrapper) thisObject);
    }
    else if (thisObject instanceof NarrativeEntry)
    {
      final NarrativeEntry entry = (NarrativeEntry) thisObject;

      // remember the dtg
      res = entry.getDTG();

      // have we got a narrative wrapper?
      Layer dest = getLayerFor(NARRATIVE_LAYER);
      if (dest == null)
      {
        dest = new NarrativeWrapper(NARRATIVE_LAYER);
        addLayer(dest);
      }

      // ok, can we provide a track color for it?
      String source = entry.getTrackName();
      Layer host = getLayerFor(source);
      if (host instanceof TrackWrapper)
      {
        TrackWrapper tw = (TrackWrapper) host;
        entry.setColor(tw.getColor());
      }

      addToLayer(entry, dest);
    }
    else if (thisObject instanceof INewItemListener)
    {
      getLayers().addNewItemListener((INewItemListener) thisObject);
    }

    // ////////
    // PlainWrapper is our "fallback" operator, so it's important to leave it
    // to last
    // ////////
    else if (thisObject instanceof PlainWrapper)
    {

      // create the wrapper for this annotation
      final PlainWrapper thisWrapper = (PlainWrapper) thisObject;

      // remember the dtg
      if (thisWrapper instanceof Watchable)
      {
        final Watchable wat = (Watchable) thisWrapper;
        res = wat.getTime();
      }

      if (thisObject instanceof ShapeWrapper)
      {
        proccessShapeWrapper(thisOne, thisObject);
      }

      // not fix, must be annotation, just add it to the correct
      // layer
      Layer dest = getLayerFor(ANNOTATION_LAYER);
      if (dest == null)
      {
        dest = createLayer(ANNOTATION_LAYER);
        addLayer(dest);
      }

      addToLayer(thisWrapper, dest);

    }

    return res;
  }

  private void proccessShapeWrapper(final PlainLineImporter thisOne,
      final Object thisObject)
  {
    ShapeWrapper shapeWrapper = (ShapeWrapper) thisObject;
    String symbology = thisOne.getSymbology();
    if (symbology != null && !symbology.isEmpty() && symbology.length() > 2)
    {
      shapeWrapper.getShape().setLineStyle(
          ImportReplay.replayLineStyleFor(symbology.substring(2)));
      if (symbology.length() > 3)
      {
        shapeWrapper.getShape().setLineWidth(
            ImportReplay.replayLineThicknesFor(symbology.substring(3)));
      }
      if (symbology.length() >= 5)
      {
        PlainShape shape = shapeWrapper.getShape();
        String fillType = symbology.substring(4, 5);
        if ("1".equals(fillType))
        {
          shape.setFilled(true);
        }
        else if ("2".equals(fillType))
        {
          shape.setFilled(true);
          shape.setSemiTransparent(true);
        }
        else
        {
          shape.setFilled(false);
          shape.setSemiTransparent(false);
        }
      }
    }
  }

  public static String replayFillStyleFor(String theSym)
  {
    String res = null;
    if (theSym.length() >= 5)
      res = theSym.substring(4, 5);
    return res;
  }

  private static int replayLineThicknesFor(String theSym)
  {
    int res = 0;
    final String theThicknes = theSym.substring(0, 1);

    try
    {
      res = new Integer(theThicknes);
    }
    catch (NumberFormatException e)
    {
      // ignore; res = 0
    }

    return res;
  }

  /**
   * import data from this stream
   */
  public final void
      importThis(final String fName, final java.io.InputStream is)
  {
    // declare linecounter
    int lineCounter = 0;

    final int numLines = countLinesFor(fName);

    final Reader reader = new InputStreamReader(is);
    final BufferedReader br = new ReaderMonitor(reader, numLines, fName);
    String thisLine = null;
    try
    {

      // check stream is valid
      if (is.available() > 0)
      {
        
        // clear the output list
        _newLayers.clear();
        _existingTracksThatMoved.clear();

        thisLine = br.readLine();

        final long start = System.currentTimeMillis();

        // loop through the lines
        while (thisLine != null)
        {

          // keep line counter
          lineCounter++;

          // catch import problems
          readLine(thisLine);

          // read another line
          thisLine = br.readLine();

        }

        // lastly have a go at formatting these tracks
        for (int k = 0; k < _myFormatters.length; k++)
        {
          _myFormatters[k].formatLayers(_newLayers);
        }
        
        // see if we've modified any existing tracks
        Iterator<TrackWrapper> tIter = _existingTracksThatMoved.iterator();
        while (tIter.hasNext())
        {
          TrackWrapper track = (TrackWrapper) tIter.next();
          
          // tell it that it has changed
          track.sortOutRelativePositions();
        }

        final long end = System.currentTimeMillis();
        System.out.print(" |Elapsed:" + (end - start) + " ");

      }
    }
    catch (final java.lang.NumberFormatException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Number format error", thisLine);
    }
    catch (final IOException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Unknown read error:" + e, thisLine);
    }
    catch (final java.util.NoSuchElementException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Missing field error", thisLine);
    }
  }

  private PlainLineImporter getImporterFor(final String rawString)
  {
    PlainLineImporter res = null;

    // trim any leading whitespace
    final String theLine = rawString.trim();

    // check it's not an empty line
    if (theLine.length() > 0)
    {
      // so, determine if this is a comment
      if (theLine.charAt(0) == ';')
      {
        // look through types of import handler
        final Enumeration<PlainLineImporter> iter = _theImporters.elements();

        // get the type for this comment
        final StringTokenizer st = new StringTokenizer(theLine);
        final String type = st.nextToken();

        // cycle through my types
        while (iter.hasMoreElements())
        {
          final PlainLineImporter thisImporter = iter.nextElement();

          // get the handler correct type?
          final String thisType = thisImporter.getYourType();

          if (thisType == null)
          {
            MWC.Utilities.Errors.Trace.trace("null returned by: "
                + thisImporter);
            return null;
          }

          // does this one fit?
          if (thisType.equals(type))
          {
            res = thisImporter;
            break;
          }
        }
      }
      else
      {
        res = new ImportFix();
      }
    }

    // done
    return res;
  }

  /**
   * convert the item to text, add it to the block we're building up
   */
  public final void exportThis(final Plottable item)
  {

    // check it's real
    if (item == null)
      throw new IllegalArgumentException("duff wrapper");

    checkImporters();

    // just see if it is a track which we are trying to export
    if ((item instanceof Layer) && !(item instanceof ExportLayerAsSingleItem))
    {
      // ok, work through the layer
      final Layer tw = (Layer) item;
      // ha-ha! export the points one at a time
      final java.util.Enumeration<Editable> iter = tw.elements();
      while (iter.hasMoreElements())
      {
        final Plottable pt = (Plottable) iter.nextElement();
        exportThis(pt);
      }
      // ta-da! done.
    }
    else
    {
      // check we have some importers
      if (_theImporters != null)
      {
        final Enumeration<PlainLineImporter> iter = _theImporters.elements();

        // step though our importers, to see if any will 'do the deal;
        while (iter.hasMoreElements())
        {
          final PlainLineImporter thisImporter = iter.nextElement();

          if (thisImporter.canExportThis(item))
          {
            // export it, add it to the data we're building up
            final String thisLine = thisImporter.exportThis(item);
            addThisToExport(thisLine);

            // ok, we can drop out of the loop
            break;
          }
        }
      }
    }
  }

  public final boolean canImportThisFile(final String theFile)
  {
    boolean res = false;
    String theSuffix = null;
    final int pos = theFile.lastIndexOf(".");
    theSuffix = theFile.substring(pos, theFile.length());

    for (int i = 0; i < _myTypes.length; i++)
    {
      if (theSuffix.equalsIgnoreCase(_myTypes[i]))
      {
        res = true;
        break;
      }
    }

    return res;
  }

  public Vector<SensorWrapper> getPendingSensors()
  {

    Vector<SensorWrapper> res = new Vector<SensorWrapper>();
    Iterator<TrackWrapper> tIter = _pendingSensors.keySet().iterator();
    while (tIter.hasNext())
    {
      TrackWrapper trackWrapper = (TrackWrapper) tIter.next();
      res.addAll(_pendingSensors.get(trackWrapper));
    }
    return res;
  }

  /**
   * actually store the sensor data in its parent object
   * 
   */
  public void storePendingSensors()
  {
    // ok, actually add the sensors to their parents now
    Iterator<TrackWrapper> tIter = _pendingSensors.keySet().iterator();
    while (tIter.hasNext())
    {
      TrackWrapper trackWrapper = (TrackWrapper) tIter.next();
      Iterator<SensorWrapper> iter =
          _pendingSensors.get(trackWrapper).iterator();
      while (iter.hasNext())
      {
        SensorWrapper thisS = iter.next();

        // find the track
        TrackWrapper parent = thisS.getHost();

        // now formally add the sensor, if we can
        if(parent != null)
        {
          parent.add(thisS);
        }
        else
        {
          // SPECIAL HANDLING - the user may have declined
          // to assign the sensor to a track, in which case
          // we just skip the assignent
          Application.logError2(Application.INFO,
              "Not storing sensor, track not found", null);
        }
      }
    }

  }

  public void clearPendingSensorList()
  {
    _pendingSensors.clear();
  }

  static public String replaySymbolForLineStyle(final int style)
  {
    switch (style)
    {
    case MWC.GUI.CanvasType.DOTTED:
      return "A";

    case MWC.GUI.CanvasType.DOT_DASH:
      return "B";

    case MWC.GUI.CanvasType.SHORT_DASHES:
      return "C";

    case MWC.GUI.CanvasType.LONG_DASHES:
      return "D";

    case MWC.GUI.CanvasType.UNCONNECTED:
      return "E";

    default:
      break;
    }
    return "@";
  }

  static public int replayLineStyleFor(final String theSym)
  {
    int res = 0;
    final String theStyle = theSym.substring(0, 1);

    if (theStyle.equals("@"))
    {
      res = MWC.GUI.CanvasType.SOLID;
    }
    else if (theStyle.equals("A"))
    {
      res = MWC.GUI.CanvasType.DOTTED;
    }
    else if (theStyle.equals("B"))
    {
      res = MWC.GUI.CanvasType.DOT_DASH;
    }
    else if (theStyle.equals("C"))
    {
      res = MWC.GUI.CanvasType.SHORT_DASHES;
    }
    else if (theStyle.equals("D"))
    {
      res = MWC.GUI.CanvasType.LONG_DASHES;
    }
    else if (theStyle.equals("E"))
    {
      res = MWC.GUI.CanvasType.UNCONNECTED;
    }

    return res;
  }

  public final static String replayTrackSymbolFor(final String theSym)
  {
    String res = null;
    final String colorVal = theSym.substring(0, 1);

    res = SymbolFactory.createSymbolFromId(colorVal);

    // did we manage to find it?
    if (res == null)
      res = SymbolFactory.createSymbolFromId(SymbolFactory.DEFAULT_SYMBOL_TYPE);

    return res;
  }

  static public Color replayColorFor(final int index)
  {
    Color res = null;

    // check we have the colours
    initialiseColours();

    final int theIndex = index % colors.size();

    res = colors.elementAt(theIndex).color;

    return res;
  }

  static public Color replayColorFor(final String theSym)
  {
    Color res = null;
    final String colorVal = theSym.substring(1, 2);

    // check we have the colours
    initialiseColours();

    // step through our list of colours
    final java.util.Enumeration<doublet> iter = colors.elements();
    while (iter.hasMoreElements())
    {
      final doublet db = iter.nextElement();
      if (db.label.equals(colorVal))
      {
        res = db.color;
        break;
      }
    }

    // if label not found, make it RED
    if (res == null)
      res = Color.red;

    return res;
  }

  static public String replaySymbolFor(final Color theCol,
      final String theSymbol)
  {
    String res = null;

    // step through our list of colours
    final java.util.Enumeration<doublet> iter = colors.elements();
    while (iter.hasMoreElements())
    {
      final doublet db = iter.nextElement();
      if (db.color.equals(theCol))
      {
        res = db.label;
        continue;
      }
    }

    String symTxt;
    if (theSymbol == null)
    {
      symTxt = "@";
    }
    else
    {
      symTxt = SymbolFactory.findIdForSymbolType(theSymbol);
    }

    // label not found, make it RED
    if (res == null)
      res = "A";

    res = symTxt + res;

    return res;

  }

  public final void exportThis(final String val)
  {
    if (val != null)
    {
      final java.awt.datatransfer.Clipboard cl =
          java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
      final java.awt.datatransfer.StringSelection ss =
          new java.awt.datatransfer.StringSelection(val);
      cl.setContents(ss, ss);
    }
  }

  final static class doublet
  {
    public final String label;

    public final Color color;

    public doublet(final String theLabel, final Color theColor)
    {
      label = theLabel;
      color = theColor;
    }
  }

  /**
   * interface which we use to implement class capable of formatting a set of layers once they've
   * been read in
   */
  public static interface LayersFormatter
  {
    public void formatLayers(List<Layer> newLayers);
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testImport extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    private static String fileName = "test.rep";

    boolean fileFinished = false;

    boolean allFilesFinished = false;

    public testImport(final String val)
    {
      super(val);
      final String fileRoot = "../org.mwc.debrief.legacy/src";

      //
      assertNotNull("Check data directory is configured", fileRoot);
      fileName = fileRoot + File.separator + fileName;

      // and check the file exists
      final java.io.File iFile = new File(fileName);
      assertTrue("Test file not found", iFile.exists());
    }

    public final void testReadREP()
    {
      java.io.File testFile = null;

      // specify the parent object - so our processing can retrieve the
      // OTG setting
      ImportReplay.initialise(new ToolParent()
      {

        public void addActionToBuffer(final Action theAction)
        {

        }

        @Override
        public void logStack(int status, String text)
        {
          logError(status, "Stack requested:" + text, null);
        }

        public Map<String, String> getPropertiesLike(final String pattern)
        {
          return null;
        }

        public String getProperty(final String name)
        {
          return ImportReplay.IMPORT_AS_OTG;
        }

        public void restoreCursor()
        {

        }

        public void setCursor(final int theCursor)
        {

        }

        public void setProperty(final String name, final String value)
        {

        }

        public void logError(final int status, final String text,
            final Exception e)
        {

        }
      });

      // can we load it directly
      testFile = new java.io.File(fileName);

      if (!testFile.exists())
      {

        // first try to get the URL of the image
        final java.lang.ClassLoader loader = getClass().getClassLoader();
        if (loader != null)
        {
          final java.net.URL imLoc = loader.getResource(fileName);
          if (imLoc != null)
          {
            testFile = new java.io.File(imLoc.getFile());
          }
        }
        else
        {
          fail("Failed to find class loader");
        }
      }

      // did we find it?
      assertTrue("Failed to find file:" + fileName, testFile.exists());

      // ok, now try to read it in
      final MWC.GUI.Layers _theLayers = new MWC.GUI.Layers();
      final File[] _theFiles = new File[]
      {testFile};

      // add the REP importer
      MWC.Utilities.ReaderWriter.ImportManager
          .addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

      // get our thread to import this
      final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader =
          new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
              _theFiles, _theLayers)
          {
            // handle the completion of each file
            public void fileFinished(final File fName, final Layers newData)
            {
              fileFinished = true;
            }

            // handle completion of the full import process
            public void allFilesFinished(final File[] fNames,
                final Layers newData)
            {
              allFilesFinished = true;
            }
          };

      // and start it running
      reader.start();

      // wait for the results
      while (reader.isAlive())
      {
        try
        {
          Thread.sleep(100);
        }
        catch (final java.lang.InterruptedException e)
        {
        }
      }

      // check it went ok
      assertTrue("File finished received", fileFinished);
      assertTrue("All Files finished received", allFilesFinished);

      assertEquals("Count of layers", 3, _theLayers.size());

      // area of coverage
      final MWC.GenericData.WorldArea area =
          _theLayers.elementAt(0).getBounds();
      super.assertEquals("tl lat of first layer", area.getTopLeft().getLat(),
          11.92276, 0.001);
      super.assertEquals("tl long of first layer", area.getTopLeft().getLong(),
          -11.59394, 0.00001);
      super.assertEquals("tl depth of first layer", area.getTopLeft()
          .getDepth(), 0, 0.00001);

      super.assertEquals("br lat of first layer", area.getBottomRight()
          .getLat(), 11.89421, 0.001);
      super.assertEquals("br long of first layer", area.getBottomRight()
          .getLong(), -11.59376, 0.00001);
      super.assertEquals("br depth of first layer", area.getBottomRight()
          .getDepth(), 0, 0.00001);

      // check those narrative lines got read in
      NarrativeWrapper narratives = (NarrativeWrapper) _theLayers.elementAt(2);
      assertEquals("have read in both narrative entries", 2, narratives.size());
    }
  }

  public static void main(final String[] args)
  {
    System.setProperty("dataDir", "d:\\dev\\debrief\\src\\java\\Debrief");
    final testImport ti = new testImport("some name");
    ti.testReadREP();
    System.exit(0);
  }

}
