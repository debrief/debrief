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
/**
 *
 */
package org.mwc.debrief.core.editors;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.core.history.LocalFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionEditorInput;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.cmap.media.dialog.VideoPlayerStartTimeDialog;
import org.mwc.cmap.media.views.VideoPlayerView;
import org.mwc.cmap.plotViewer.actions.Pan;
import org.mwc.cmap.plotViewer.actions.Pan.PanMode;
import org.mwc.cmap.plotViewer.actions.RangeBearing;
import org.mwc.cmap.plotViewer.actions.RangeBearing.RangeBearingMode;
import org.mwc.cmap.plotViewer.actions.ZoomIn;
import org.mwc.cmap.plotViewer.actions.ZoomIn.ZoomInMode;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts.ShadeCutsOperation;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts.ShadeOperation;
import org.mwc.debrief.core.actions.DragComponent;
import org.mwc.debrief.core.actions.DragComponent.DragComponentMode;
import org.mwc.debrief.core.actions.DragFeature;
import org.mwc.debrief.core.actions.DragFeature.DragFeatureMode;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.core.actions.DragSegment.DragSegmentMode;
import org.mwc.debrief.core.actions.RadioHandler;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.interfaces.IPlotLoader.BaseLoader;
import org.mwc.debrief.core.interfaces.IPlotLoader.DeferredPlotLoader;
import org.mwc.debrief.core.loaders.LoaderManager;
import org.mwc.debrief.core.loaders.ReplayLoader;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.core.operations.ExportDopplerShift;
import org.mwc.debrief.core.operations.ExportTimeDataToClipboard;
import org.mwc.debrief.core.operations.ExportToFlatFile;
import org.mwc.debrief.core.operations.ExportToFlatFile2;
import org.mwc.debrief.core.operations.PlotOperations;
import org.mwc.debrief.core.preferences.PrefsPage;
import org.osgi.framework.Bundle;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.TrackDataProvider;
import MWC.TacticalData.TrackDataProvider.TrackDataListener;
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
@SuppressWarnings(
{"deprecation", "restriction"})
public class PlotEditor extends org.mwc.cmap.plotViewer.editors.CorePlotEditor
{
  public static class TestMe extends TestCase
  {

    public void testAmbig()
    {
      final SensorWrapper sensor = new SensorWrapper("Some name");

      assertFalse("Should not find ambig data", isAmbiguousData(sensor));

      // give it some none freq data
      sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000),
          null, 100d, null, null, null, Color.RED, "label", 1, "Some name"));

      assertFalse("Should still not find ambig data", isAmbiguousData(sensor));

      // and another cut
      sensor.add(new SensorContactWrapper("the track", new HiResDate(1003000),
          null, 100d, null, null, null, Color.RED, "label", 1, "Some name"));

      assertFalse("Should still not find ambig data", isAmbiguousData(sensor));

      // clear the cuts
      sensor.removeElement(sensor.elements().nextElement());
      sensor.removeElement(sensor.elements().nextElement());

      assertEquals("now empty", 0, sensor.size());
      ;

      // give it some freq data
      sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000),
          null, 122d, 222d, null, null, Color.RED, "label", 1, "Some name"));

      assertTrue("Should find ambig data", isAmbiguousData(sensor));
    }

    public void testFreq()
    {
      final SensorWrapper sensor = new SensorWrapper("Some name");

      assertFalse("Should not find freq data", hasFrequencyData(sensor));

      // give it some none freq data
      sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000),
          null, 100d, 200d, null, null, Color.RED, "label", 1, "Some name"));

      assertFalse("Should still not find freq data", hasFrequencyData(sensor));

      // and another cut
      sensor.add(new SensorContactWrapper("the track", new HiResDate(1003000),
          null, 100d, 200d, null, null, Color.RED, "label", 1, "Some name"));

      assertFalse("Should still not find freq data", hasFrequencyData(sensor));

      // clear the cuts
      sensor.removeElement(sensor.elements().nextElement());
      sensor.removeElement(sensor.elements().nextElement());

      assertEquals("now empty", 0, sensor.size());
      ;

      // give it some freq data
      sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000),
          null, null, null, 22d, null, Color.RED, "label", 1, "Some name"));

      assertTrue("Should find freq data", hasFrequencyData(sensor));
    }
  }

  // Extension point tag and attributes in plugin.xml
  private static final String EXTENSION_POINT_ID = "DebriefPlotLoader";

  private static final String EXTENSION_TAG = "loader";

  private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

  private static final String EXTENSION_TAG_EXTENSIONS_ATTRIB = "extensions";

  private static final String EXTENSION_TAG_FIRST_LINE_ATTRIB = "first_line";

  // private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

  private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

  // Plug-in ID from <plugin> tag in plugin.xml
  private static final String PLUGIN_ID = "org.mwc.debrief.core";

  private static boolean _updatingPlot = false;

  private static TimePeriod extend(final TimePeriod period,
      final HiResDate date)
  {
    TimePeriod result = period;
    // have we received a date?
    if (date != null)
    {
      if (result == null)
      {
        result = new TimePeriod.BaseTimePeriod(date, date);
      }
      else
      {
        result.extend(date);
      }
    }

    return result;
  }

  private static String getAbsoluteName(final IFile iff) throws CoreException
  {
    String name;
    URI uri = iff.getLocationURI();
    if (iff.isLinked())
    {
      uri = iff.getRawLocationURI();
    }
    final File javaFile = EFS.getStore(uri).toLocalFile(0,
        new NullProgressMonitor());
    name = javaFile.getAbsolutePath();
    return name;
  }

  private static TimePeriod getPeriodFor(final Layers theData)
  {
    TimePeriod res = null;

    for (final Enumeration<Editable> iter = theData.elements(); iter
        .hasMoreElements();)
    {
      final Layer thisLayer = (Layer) iter.nextElement();

      // and through this layer
      if (thisLayer instanceof TrackWrapper)
      {
        final TrackWrapper thisT = (TrackWrapper) thisLayer;
        res = extend(res, thisT.getStartDTG());
        res = extend(res, thisT.getEndDTG());
      }
      else if (thisLayer instanceof BaseLayer)
      {
        final Enumeration<Editable> elements = thisLayer.elements();
        while (elements.hasMoreElements())
        {
          final Plottable nextP = (Plottable) elements.nextElement();
          if (nextP instanceof Watchable)
          {
            final Watchable wrapped = (Watchable) nextP;
            final HiResDate dtg = wrapped.getTime();
            if (dtg != null)
            {
              res = extend(res, dtg);

              // also see if it this data type an end time
              if (wrapped instanceof WatchableList)
              {
                // ok, make sure we also handle the end time
                final WatchableList wl = (WatchableList) wrapped;
                final HiResDate endD = wl.getEndDTG();
                if (endD != null)
                {
                  res = extend(res, endD);
                }
              }
            }
          }
        }
      }
    }

    return res;
  }

  private static boolean hasFrequencyData(final SensorWrapper thisS)
  {

    final boolean hasFreq;
    if (thisS.size() > 0)
    {
      final SensorContactWrapper firstCut = (SensorContactWrapper) thisS
          .elements().nextElement();
      hasFreq = firstCut.getHasFrequency();
    }
    else
    {
      hasFreq = false;
    }
    return hasFreq;

  }

  private static boolean isAmbiguousData(final SensorWrapper thisS)
  {
    final boolean isTowed;
    if (thisS.size() > 0)
    {
      final SensorContactWrapper firstCut = (SensorContactWrapper) thisS
          .elements().nextElement();
      isTowed = firstCut.getHasAmbiguousBearing();
    }
    else
    {
      isTowed = false;
    }
    return isTowed;
  }

  /**
   * helper object which loads plugin file-loaders
   */
  private final LoaderManager _loader;

  /**
   * we keep the reference to our track-type adapter
   */
  private final TrackDataProvider _trackDataProvider;

  /**
   * The job used to handle large changes in layers
   */
  private final Job _refreshJob;

  /**
   * something to look after our layer painters
   */
  private final LayerPainterManager _layerPainterManager;

  /**
   * and how we view the time
   *
   */
  protected final TimeControlPreferences _timePreferences;

  private final PlotOperations _myOperations;

  /**
   * support tool that provides a relative plot
   */
  private RelativeProjectionParent _myRelativeWrapper;

  /**
   * handle narrative management
   */
  protected IRollingNarrativeProvider _theNarrativeProvider;

  /**
   * an object to look after all of the time bits
   */
  private final TimeManager _timeManager;

  private final org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore _timeControllerOperations;

  /**
   * note: the outline page isn't final, since the user may close the page, after which we will have
   * a new one
   */
  private PlotOutlinePage _outlinePage;

  private final TraverseListener dragModeListener = createTraverseListener();

  private PlotPropertySheetPage _propertySheetPage;

  private final IPropertyChangeListener _sensorTransparencyListener;

  /**
   * constructor - quite simple really.
   */
  public PlotEditor()
  {
    super();

    // create the track manager to manage the primary & secondary tracks
    _trackDataProvider = new TrackManager(_myLayers);

    // and listen out form modifications, because we want to mark ourselves
    // as
    // dirty once they've updated
    _trackDataProvider.addTrackDataListener(new TrackDataListener()
    {
      @Override
      public void tracksUpdated(final WatchableList primary,
          final WatchableList[] secondaries)
      {
        fireDirty();
      }
    });

    // sort out the time controlleroperations
    _timeControllerOperations =
        new org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore();
    _timeControllerOperations.add(new ExportTimeDataToClipboard());
    _timeControllerOperations.add(new ExportToFlatFile());
    _timeControllerOperations.add(new ExportToFlatFile2());
    _timeControllerOperations.add(new ExportDopplerShift());

    _layerPainterManager = new LayerPainterManager(_trackDataProvider);
    _layerPainterManager.addPropertyChangeListener(new PropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent arg0)
      {
        // ok, trigger repaint of plot
        if (getChart() != null)
        {
          getChart().update();
        }
      }
    });

    // create the time manager. cool
    _timeManager = new TimeManager();
    _timeManager.addListener(_timeListener,
        TimeProvider.TIME_CHANGED_PROPERTY_NAME);

    // and how time is managed
    _timePreferences = new TimeControlProperties();

    // listen for the sensor transparency hcanging
    _sensorTransparencyListener = new IPropertyChangeListener()
    {
      @Override
      public void propertyChange(
          final org.eclipse.jface.util.PropertyChangeEvent event)
      {
        if (SensorContactWrapper.TRANSPARENCY.equals(event.getProperty()))
        {
          // ok, trigger redraw
          _myLayers.fireExtended();
        }
      }
    };
    CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(
        _sensorTransparencyListener);

    _refreshJob = createRefreshJob();

    // ok - declare and load the supplemental plugins which can load
    // datafiles
    _loader = initialiseFileLoaders();

    // listen out for when our input changes, since we will change the
    // editor
    // window title
    this.addPropertyListener(new IPropertyListener()
    {

      @Override
      @SuppressWarnings(
      {"synthetic-access"})
      public void propertyChanged(final Object source, final int propId)
      {
        if (propId == PROP_INPUT)
        {
          final Object input = getEditorInput();
          if (input instanceof IFileEditorInput)
          {
            final IFileEditorInput inp = (IFileEditorInput) getEditorInput();
            setPartName(inp.getName());
          }
          else if (input instanceof FileStoreEditorInput)
          {
            final FileStoreEditorInput fsi = (FileStoreEditorInput) input;
            final String theName = fsi.getName();
            setPartName(theName);
          }
          else if (input instanceof FileRevisionEditorInput)
          {
            setPartName(((FileRevisionEditorInput) input).getName());
          }
          else
          {
            CorePlugin.logError(IStatus.WARNING,
                "data source for PlotEditor not of expected type:" + input,
                null);
            setPartName(input.toString());
          }
        }
      }
    });

    _myOperations = new PlotOperations()
    {
      // just provide with our complete set of layers
      @Override
      @SuppressWarnings("synthetic-access")
      public Object[] getTargets()
      {
        // ok, return our top level layers as objects
        final Vector<Layer> res = new Vector<Layer>(0, 1);
        for (int i = 0; i < _myLayers.size(); i++)
        {
          res.add(_myLayers.elementAt(i));
        }
        return res.toArray();
      }

      /**
       * override performing the operation, since we'll do a screen update on completion
       */
      @Override
      @SuppressWarnings("synthetic-access")
      public Vector<Layer> performOperation(final AnOperation operationName)
      {
        // make the actual change
        final Vector<Layer> res = super.performOperation(operationName);

        if (res != null)
        {
          if (res.size() != 0)
          {
            for (final Iterator<Layer> iter = res.iterator(); iter.hasNext();)
            {
              final Layer thisL = iter.next();
              // and update the screen
              _myLayers.fireReformatted(thisL);

            }
          }
        }

        return res;

      }
    };

    // do we have some time preferences?
    if (_timePreferences != null)
    {
      final HiResDate startDTG = _timePreferences.getSliderStartTime();
      final HiResDate endDTG = _timePreferences.getSliderEndTime();
      // and were there any times in it?
      if ((startDTG != null) && (endDTG != null))
      {
        // yup, store the time data.
        _myOperations.setPeriod(new TimePeriod.BaseTimePeriod(startDTG,
            endDTG));
      }
    }

    // introduce our new item listener that looks after relative TMA segments being dropped into the
    // layers object
    _myLayers.addDataExtendedListener(new Layers.DataListener2()
    {

      @Override
      public void dataExtended(final Layers theData)
      {
      }

      @Override
      public void dataExtended(final Layers theData, final Plottable newItem,
          final HasEditables parent)
      {
        reconnectSegments(newItem, parent);
      }

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
      }

      @Override
      public void dataReformatted(final Layers theData,
          final Layer changedLayer)
      {
      }
    });
  }

  private void applyRainbowShadingTo(final SensorWrapper thisS)
  {
    SensorWrapper theSensor = null;

    // are they items we're interested in?
    HiResDate startDTG = new HiResDate(Long.MAX_VALUE / 1000, 0);
    HiResDate endDTG = new HiResDate(0);
    final Enumeration<Editable> elements = thisS.elements();
    final ArrayList<Editable> sensors = new ArrayList<Editable>();
    final ArrayList<SensorContactWrapper> list =
        new ArrayList<SensorContactWrapper>();
    while (elements.hasMoreElements())
    {
      sensors.add(elements.nextElement());
    }
    for (final Editable thisE : sensors)
    {
      if (thisE instanceof SensorWrapper)
      {
        // just check that there's only one item selected
        if (sensors.size() == 1)
        {
          theSensor = (SensorWrapper) thisE;
        }
      }
      else if (thisE instanceof SensorContactWrapper)
      {
        list.add((SensorContactWrapper) thisE);
        if (startDTG.compareTo(((SensorContactWrapper) thisE).getDTG()) > 0)
        {
          startDTG = ((SensorContactWrapper) thisE).getDTG();
        }
        if (endDTG.compareTo(((SensorContactWrapper) thisE).getDTG()) < 0)
        {
          endDTG = ((SensorContactWrapper) thisE).getDTG();
        }
      }
    }

    // ok, do we have a single sensor?
    if (theSensor != null)
    {
      startDTG = theSensor.getStartDTG();
      endDTG = theSensor.getEndDTG();
      final Collection<Editable> editables = theSensor.getItemsBetween(theSensor
          .getStartDTG(), theSensor.getEndDTG());
      for (final Editable editable : editables)
      {
        if (editable instanceof SensorContactWrapper)
        {
          list.add((SensorContactWrapper) editable);
        }
      }
    }
    final HiResDate start = startDTG;
    final HiResDate end = endDTG;
    // create this operation
    final String title1 = "Shade in rainbow colors";
    Layer parentLayer = null;

    final Layers parentLayers = _myLayers;
    if (parentLayers != null)
    {
      if (parentLayers.size() == 1)
      {
        parentLayer = parentLayers.elementAt(0);
      }
    }

    final IUndoableOperation theAction = new ShadeCutsOperation(title1,
        parentLayers, parentLayer, list.toArray(new SensorContactWrapper[0]),
        start, end, ShadeOperation.RAINBOW_SHADE);
    CorePlugin.run(theAction);
  }

  private boolean areWeWaitingForRange(final SensorWrapper thisS)
  {
    boolean needsRange = false;
    final Enumeration<Editable> cuts = thisS.elements();
    if (cuts.hasMoreElements())
    {
      final Editable firstCut = cuts.nextElement();
      final SensorContactWrapper scw = (SensorContactWrapper) firstCut;
      // do we have bearing?
      if (scw.getHasBearing())
      {
        // yes. now are we waiting for a range?
        if (scw.getRange() == null)
        {
          needsRange = true;
        }
      }
    }
    return needsRange;
  }

  private void chooseHostFor(final SensorWrapper sensor,
      final List<TrackWrapper> candidateHosts)
  {
    // ok, construct the popup
    final Object[] tArr = candidateHosts.toArray();

    // popup the layers in a question dialog
    final IStructuredContentProvider theVals = new ArrayContentProvider();
    final ILabelProvider theLabels = new LabelProvider();

    // collate the dialog
    final ListDialog list = new ListDialog(Display.getCurrent()
        .getActiveShell());
    list.setContentProvider(theVals);
    list.setLabelProvider(theLabels);
    list.setInput(tArr);
    list.setMessage("Please select the track for sensor titled \"" + sensor
        .getName() + "\"\n(or Cancel to not store it)");
    list.setTitle("Sensor track not found");
    list.setHelpAvailable(false);

    // select the first item, so it's valid to press OK immediately
    list.setInitialSelections(new Object[]
    {tArr[0]});

    // open it
    final int selection = list.open();

    // did user say yes?
    if (selection != Window.CANCEL)
    {
      // yup, store it's name
      final Object[] val = list.getResult();

      // check something got selected
      if (val.length == 1)
      {
        final TrackWrapper selected = (TrackWrapper) val[0];
        sensor.setHost(selected);
      }
    }
  }

  /**
   * Creates the outline page used with this editor.
   *
   * @return the created plot outline page
   */
  protected PlotOutlinePage createOutlinePage()
  {
    final PlotOutlinePage page = new PlotOutlinePage(this, _myLayers);
    return page;
  }

  /**
   * Create the refresh job for the receiver.
   *
   */
  private WorkbenchJob createRefreshJob()
  {
    // Creates a workbench job that will update the UI. But, it can be
    // cancelled and re-scheduled
    // may override.
    final WorkbenchJob refreshJob = new WorkbenchJob("Refresh Filter") //$NON-NLS-1$
    {
      @Override
      public IStatus runInUIThread(final IProgressMonitor monitor)
      {
        Display.getDefault().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            // inform our parent
            PlotEditor.super.layersExtended();

            // we should also recalculate the time period we cover
            final TimePeriod timePeriod = getPeriodFor(_myLayers);

            // and share the good news.
            _timeManager.setPeriod(this, timePeriod);

            // and tell the track data manager that something's happened. One of
            // it's
            // tracks may have been
            // deleted!
            _trackDataProvider.fireTracksChanged();
          }
        });
        return Status.OK_STATUS;
      }
    };

    refreshJob.setSystem(true);

    return refreshJob;
  }

  /**
   * @param parent
   */
  @Override
  protected SWTChart createTheChart(final Composite parent)
  {
    final SWTChart res = new SWTChart(_myLayers, parent, _myGeoHandler)
    {

      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void chartFireSelectionChanged(final ISelection sel)
      {
        fireSelectionChanged(sel);
      }

      @Override
      public SWTCanvas createCanvas(final Composite parent1,
          final GtProjection projection)
      {
        return new CustomisedSWTCanvas(parent1, _myGeoHandler)
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void doSupplementalRightClickProcessing(
              final MenuManager menuManager, final Plottable selected,
              final Layer theParentLayer)
          {
            if (selected instanceof CreateEditorForParent)
            {
              // get the parent track
              final CreateEditorForParent editor =
                  (CreateEditorForParent) selected;
              final Editable parent11 = editor.getParent();
              RightClickSupport.getDropdownListFor(menuManager, new Editable[]
              {parent11}, new Layer[]
              {theParentLayer}, new Layer[]
              {theParentLayer}, getLayers(), true);
            }
          }

          @Override
          public void parentFireSelectionChanged(final ISelection selected)
          {
            chartFireSelectionChanged(selected);
          }
        };
      }

      /**
       * @param thisLayer
       * @param dest
       */
      @Override
      protected void paintThisLayer(final Layer thisLayer,
          final CanvasType dest)
      {
        try
        {
          // get the current time
          final HiResDate tNow = _timeManager.getTime();

          // do we know the time?
          // if (tNow != null)
          if (true)
          {
            // yes. cool, get plotting
            _layerPainterManager.getCurrentPainter().paintThisLayer(thisLayer,
                dest, tNow);

            // if this has a moveable perspective - paint it over the top
            if (thisLayer instanceof DynamicPlottable && tNow != null)
            {
              ((DynamicPlottable) thisLayer).paint(dest, tNow.getDate()
                  .getTime());
            }

            // ok, now sort out the highlight

            // right, what are the watchables
            final Vector<Plottable> watchables = SnailPainter.getWatchables(
                thisLayer);

            // cycle through them
            final Enumeration<Plottable> watches = watchables.elements();
            while (watches.hasMoreElements())
            {
              final WatchableList list = (WatchableList) watches.nextElement();
              // is the primary an instance of layer (with it's
              // own line
              // thickness?)
              if (list instanceof Layer)
              {
                final Layer ly = (Layer) list;
                final int thickness = ly.getLineThickness();
                dest.setLineWidth(thickness);
              }

              // ok, clear the nearest items
              if (tNow != null)
              {
                final Watchable[] wList = list.getNearestTo(tNow);
                for (int i = 0; i < wList.length; i++)
                {
                  final Watchable watch = wList[i];
                  // if (wList.length > 0)
                  // watch = wList[0];

                  if (watch != null)
                  {
                    // aah, is this the primary?
                    final boolean isPrimary = (list == _trackDataProvider
                        .getPrimaryTrack());

                    // plot it
                    _layerPainterManager.getCurrentHighlighter().highlightIt(
                        dest.getProjection(), dest, list, watch, isPrimary);
                  }

                }
              } // whether we have a current time...
            }

          }
        }
        catch (final Exception e)
        {
          CorePlugin.logError(IStatus.ERROR, "Whilst repainting:" + thisLayer,
              e);
        }
      }

    };
    final Control control = res.getCanvasControl();
    if (control != null && !control.isDisposed())
    {
      control.addTraverseListener(dragModeListener);
      control.addDisposeListener(new DisposeListener()
      {

        @Override
        public void widgetDisposed(final DisposeEvent e)
        {
          control.removeTraverseListener(dragModeListener);
          control.removeDisposeListener(this);
        }
      });
    }
    return res;
  }

  private TraverseListener createTraverseListener()
  {
    return new TraverseListener()
    {

      @Override
      public void keyTraversed(final TraverseEvent e)
      {
        if (getChart() == null)
        {
          return;
        }
        final Control control = getChart().getCanvasControl();
        if (control == null || control.isDisposed() || !control.isVisible())
        {
          return;
        }
        if (e.detail == SWT.TRAVERSE_TAB_NEXT)
        {
          final PlotMouseDragger dragMode = getChart().getDragMode();
          if (dragMode != null)
          {
            try
            {
              String currentState = null;
              final ExecutionEvent executionEvent = new ExecutionEvent();
              if (dragMode instanceof DragSegmentMode)
              {
                new DragComponent().execute(executionEvent);
                currentState = RadioHandler.DRAG_COMPONENT;
              }
              else if (dragMode instanceof DragComponentMode)
              {
                new DragFeature().execute(executionEvent);
                currentState = RadioHandler.DRAG_FEATURE;
              }
              else if (dragMode instanceof DragFeatureMode)
              {
                new RangeBearing().execute(executionEvent);
                currentState = RadioHandler.RANGE_BEARING;
              }
              else if (dragMode instanceof RangeBearingMode)
              {
                new Pan().execute(executionEvent);
                currentState = RadioHandler.PAN;
              }
              else if (dragMode instanceof PanMode)
              {
                new ZoomIn().execute(executionEvent);
                currentState = RadioHandler.ZOOM_IN;
              }
              else if (dragMode instanceof ZoomInMode)
              {
                new DragSegment().execute(executionEvent);
                currentState = RadioHandler.DRAG_SEGMENT;
              }
              if (currentState != null)
              {
                final ICommandService service = (ICommandService) getSite()
                    .getService(ICommandService.class);
                final Command command = service.getCommand(RadioHandler.ID);
                HandlerUtil.updateRadioState(command, currentState);
              }
            }
            catch (final Exception e1)
            {
              CorePlugin.logError(IStatus.WARNING, "Cannot change drag mode:",
                  e1);
            }
          }
        }
      }
    };
  }

  private List<TrackWrapper> determineCandidateHosts()
  {
    final List<TrackWrapper> res = new ArrayList<TrackWrapper>();

    final Enumeration<Editable> iter = _myLayers.elements();
    while (iter.hasMoreElements())
    {
      final Editable editable = iter.nextElement();
      if (editable instanceof TrackWrapper)
      {
        res.add((TrackWrapper) editable);
      }
    }

    return res;
  }

  @Override
  public void dispose()
  {
    super.dispose();

    // stop listening to the time manager
    _timeManager.removeListener(_timeListener,
        TimeProvider.TIME_CHANGED_PROPERTY_NAME);

    // stop listening for sensor transparency changes
    CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(
        _sensorTransparencyListener);

    if (_layerPainterManager != null)
    {
      _layerPainterManager.close();
    }

    if (_outlinePage != null)
    {
      _outlinePage.dispose();
      _outlinePage = null;
    }
  }

  /**
   * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
   */
  @Override
  public void doSave(final IProgressMonitor monitor)
  {
    final IEditorInput input = getEditorInput();
    String ext = null;

    // do we have an input
    if (input.exists())
    {
      // get the file suffix
      if (input instanceof IFileEditorInput)
      {
        IFile file = null;
        file = ((IFileEditorInput) getEditorInput()).getFile();
        final IPath path = file.getFullPath();
        ext = path.getFileExtension();
      }
      else if (input instanceof FileStoreEditorInput)
      {
        final FileStoreEditorInput fi = (FileStoreEditorInput) input;
        final URI uri = fi.getURI();
        final Path path = new Path(uri.getPath());
        ext = path.getFileExtension();
      }
      else if (input instanceof FileRevisionEditorInput)
      {
        final FileRevisionEditorInput frei = (FileRevisionEditorInput) input;
        final URI uri = frei.getURI();
        final Path path = new Path(uri.getPath());
        ext = path.getFileExtension();
      }

      // right, have a look at it.
      if ((ext == null) || (!ext.equalsIgnoreCase("xml") && !ext
          .equalsIgnoreCase("dpf")))
      {
        String msg = "Debrief stores data in a structured (xml) text format,";
        msg +=
            "\nwhich is different to the format you've used to load the data.";
        msg += "\nThus you must specify an existing (or new) folder to "
            + "store the plot,\nand provide new filename.";
        msg +=
            "\nNote: it's important that you give the file a .dpf file suffix";
        final MessageDialog md = new MessageDialog(getEditorSite().getShell(),
            "Save as", null, msg, MessageDialog.WARNING, new String[]
            {"Ok"}, 0);
        md.open();

        // not, we have to do a save-as
        doSaveAs(
            "Can't store this file-type, select a target folder, and remember to save as Debrief plot-file (*.dpf)");
      }
      else
      {

        OutputStream tmpOS = null;
        // the workspace has a listenser that will close/rename the current plot
        // editor if it's parent file
        // has been deleted/renamed. We need to cancel that processing whilst we
        // do a file-save,
        // since the file-save includes a name-change.
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(
            resourceChangeListener);
        try
        {
          // NEW STRATEGY. Save to tmp first, then overwrite existing on
          // success.

          // 1. create the temp file
          final File tmpFile = File.createTempFile("DebNG_tmp", ".dpf");
          tmpFile.createNewFile();
          tmpFile.deleteOnExit();

          // 1a. record the name of the tmp file in the log
          final String filePath = tmpFile.getAbsolutePath();
          CorePlugin.logError(IStatus.INFO, "Created temp save file at:"
              + filePath, null);

          // 2. open the file as a stream
          tmpOS = new FileOutputStream(tmpFile);

          // 3. save to this stream
          doSaveTo(tmpOS, monitor);

          tmpOS.close();
          tmpOS = null;

          // sort out the file size
          CorePlugin.logError(IStatus.INFO, "Saved file size is:" + tmpFile
              .length() / 1024 + " Kb", null);

          // 4. Check there's something in the temp file
          if (tmpFile.exists())
          {
            if (tmpFile.length() == 0)
            {
              // save failed throw exception (to be collected shortly
              // afterwards)
              throw new RuntimeException("Stored file is of zero size");
            }
            else
            {

              // save worked. cool.

              // 5. overwrite the existing file with the saved file
              // - note we will only reach this point if the save succeeded.
              // sort out where we're saving to
              if (input instanceof IFileEditorInput)
              {
                CorePlugin.logError(IStatus.INFO,
                    "Performing IFileEditorInput save", null);

                final IFile file = ((IFileEditorInput) getEditorInput())
                    .getFile();

                InputStream source = null;
                try
                {
                  source = new FileInputStream(tmpFile);
                  file.setContents(source, true, true, monitor);
                }
                finally
                {
                  FileUtil.safeClose(source);
                }
              }
              else if (input instanceof FileRevisionEditorInput)
              {
                CorePlugin.logError(IStatus.INFO,
                    "Performing FileRevisionEditorInput save", null);

                final FileRevisionEditorInput frei =
                    (FileRevisionEditorInput) input;
                final IFile file = getFile(frei);

                InputStream source = null;
                try
                {
                  source = new FileInputStream(tmpFile);
                  file.setContents(source, true, true, monitor);
                  final FileEditorInput newInput = new FileEditorInput(file);
                  setInputWithNotify(newInput);
                }
                finally
                {
                  FileUtil.safeClose(source);
                }
              }
              else if (input instanceof FileStoreEditorInput)
              {

                CorePlugin.logError(IStatus.INFO,
                    "Performing FileStoreEditorInput save", null);

                // get the data-file
                final FileStoreEditorInput fi = (FileStoreEditorInput) input;
                final URI _uri = fi.getURI();
                final Path _p = new Path(_uri.getPath());

                // create pointers to the existing file, and the backup file
                final IFileStore existingFile = EFS.getLocalFileSystem()
                    .getStore(_p);
                OutputStream out = null;
                InputStream source = null;
                try
                {
                  out = existingFile.openOutputStream(EFS.OVERWRITE, monitor);
                  source = new FileInputStream(tmpFile);
                  FileUtil.transferStreams(source, out, existingFile.toString(),
                      monitor);
                }
                finally
                {
                  FileUtil.safeClose(source);
                  FileUtil.safeClose(out);
                }
              }
            }
          }

          // ok, lastly indicate that the save worked (if it did!)
          _plotIsDirty = false;
          firePropertyChange(PROP_DIRTY);
        }
        catch (final ResourceException e)
        {
          CorePlugin.showMessage("File save", e.getMessage());
          CorePlugin.logError(IStatus.ERROR,
              "Failed whilst saving external file", e);
        }
        catch (final CoreException e)
        {
          CorePlugin.logError(IStatus.ERROR,
              "Failed whilst saving external file", e);
        }
        catch (final FileNotFoundException e)
        {
          CorePlugin.logError(IStatus.ERROR,
              "Failed to find local file to save to", e);
        }
        catch (final Exception e)
        {
          CorePlugin.logError(IStatus.ERROR, "Unknown file-save error occurred",
              e);
        }
        finally
        {
          try
          {
            if (tmpOS != null)
            {
              tmpOS.close();
            }
          }
          catch (final IOException e)
          {
            CorePlugin.logError(IStatus.ERROR, "Whilst performing save", e);
          }
          ResourcesPlugin.getWorkspace().addResourceChangeListener(
              resourceChangeListener, IResourceChangeEvent.PRE_CLOSE
                  | IResourceChangeEvent.PRE_DELETE
                  | IResourceChangeEvent.POST_CHANGE);
        }
      }
    }
  }

  @Override
  public void doSaveAs()
  {
    doSaveAs("Save as");
  }

  public void doSaveAs(final String message)
  {
    // do we have a project?
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IProject[] projects = workspace.getRoot().getProjects();
    if ((projects == null) || (projects.length == 0))
    {
      String msg = "Debrief plots are stored in 'Projects'";
      msg += "\nBut you do not yet have one defined.";
      msg += "\nPlease follow the 'Generating a project for your data' cheat";
      msg +=
          "\nsheet, accessed from Help/Cheat Sheets then Debrief/Getting started.";
      msg +=
          "\nOnce you have created your project, please start the Save process again.";
      msg +=
          "\nNote: the cheat sheet will open automatically when you close this dialog.";
      final MessageDialog md = new MessageDialog(getEditorSite().getShell(),
          "Save as", null, msg, MessageDialog.WARNING, new String[]
          {"Ok"}, 0);
      md.open();

      // try to open the cheat sheet
      final String CHEAT_ID = "org.mwc.debrief.help.started.generate_project";

      Display.getCurrent().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          final OpenCheatSheetAction action = new OpenCheatSheetAction(
              CHEAT_ID);
          action.run();
        }
      });

      // ok, drop out - we can't do a save anyway
      return;
    }

    // get the workspace

    final SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
    dialog.setTitle("Save Plot As");
    if (getEditorInput() instanceof FileEditorInput)
    {
      // this has been loaded from the navigator
      final IFile oldFile = ((FileEditorInput) getEditorInput()).getFile();
      // dialog.setOriginalFile(oldFile);

      final IPath oldPath = oldFile.getFullPath();
      final IPath newStart = oldPath.removeFileExtension();
      final IPath newPath = newStart.addFileExtension("dpf");
      final File asFile = newPath.toFile();
      final String newName = asFile.getName();
      dialog.setOriginalName(newName);
    }
    else if (getEditorInput() instanceof FileStoreEditorInput)
    {
      // this has been dragged from an explorer window
      final FileStoreEditorInput fi = (FileStoreEditorInput) getEditorInput();
      final URI uri = fi.getURI();
      final File thisFile = new File(uri.getPath());
      String newPath = fileNamePartOf(thisFile.getAbsolutePath());
      newPath += ".dpf";
      dialog.setOriginalName(newPath);
    }

    dialog.create();
    if (message != null)
    {
      dialog.setMessage(message, IMessageProvider.WARNING);
    }
    else
    {
      dialog.setMessage("Save file to another location.");
    }
    dialog.open();
    final IPath path = dialog.getResult();

    if (path == null)
    {
      return;
    }
    else
    {
      final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      if (!file.exists())
      {
        try
        {
          System.out.println("creating:" + file.getName());
          file.create(new ByteArrayInputStream(new byte[]
          {}), false, null);
        }
        catch (final CoreException e)
        {
          DebriefPlugin.logError(IStatus.ERROR,
              "Failed trying to create new file for save-as", e);
          return;
        }
      }

      OutputStream os = null;
      try
      {
        os = new FileOutputStream(file.getLocation().toFile(), false);
        // ok, write to the file
        doSaveTo(os, new NullProgressMonitor());

        // also make this new file our input
        final IFileEditorInput newInput = new FileEditorInput(file);
        setInputWithNotify(newInput);

        // lastly, trigger a navigator refresh
        final IFile iff = newInput.getFile();
        iff.refreshLocal(IResource.DEPTH_ONE, null);
        // refresh navigator
        final IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        final IViewPart view = page.findView(
            "org.eclipse.ui.views.ResourceNavigator");
        if (view instanceof ResourceNavigator)
        {
          ((ResourceNavigator) view).getViewer().refresh(iff);
        }

        // ok, lastly indicate that the save worked
        _plotIsDirty = false;
        firePropertyChange(PROP_DIRTY);
      }
      catch (final FileNotFoundException e)
      {
        CorePlugin.showMessage("File save", e.getMessage());
        CorePlugin.logError(IStatus.ERROR, "Failed whilst performing Save As",
            e);
      }
      catch (final ResourceException e)
      {
        CorePlugin.showMessage("File save", e.getMessage());
        CorePlugin.logError(IStatus.ERROR, "Failed whilst saving external file",
            e);
      }
      catch (final CoreException e)
      {
        CorePlugin.logError(IStatus.ERROR,
            "Refresh failed after saving new file", e);
      }
      finally
      {
        // and close it
        try
        {
          if (os != null)
          {
            os.close();
          }
        }
        catch (final IOException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Whilst performaing save-as", e);
        }
      }
    }
  }

  /**
   * save our plot to the indicated location
   *
   * @param destination
   *          where to save plot to
   * @param monitor
   *          somebody/something to be informed about progress
   */
  private void doSaveTo(final OutputStream os, final IProgressMonitor monitor)
  {
    if (os != null)
    {

      final IProduct prod = Platform.getProduct();
      final Bundle bund = prod.getDefiningBundle();
      final String version = "" + new Date(bund.getLastModified());

      try
      {
        // ok, now write to the file
        DebriefEclipseXMLReaderWriter.exportThis(this, os, version);
      }
      catch (final Exception e)
      {
        DebriefPlugin.logError(IStatus.ERROR, "Error exporting plot file", e);
      }

    }
    else
    {
      DebriefPlugin.logError(IStatus.ERROR,
          "Unable to identify source file for plot", null);
    }

  }

  /**
   * utility function to extact the root part of this filename
   *
   * @param fileName
   *          full file path
   * @return root of file name (before the . marker)
   */
  private String fileNamePartOf(final String fileName)
  {
    if (fileName == null)
    {
      throw new IllegalArgumentException("file name == null");
    }

    // ok, extract the parent portion
    final File wholeFile = new File(fileName);
    final String parentSection = wholeFile.getParent();
    final int parentLen = parentSection.length() + 1;
    final int fileLen = fileName.length();
    final String fileSection = fileName.substring(parentLen, fileLen);

    final int pos = fileSection.lastIndexOf('.');
    if (pos > 0 && pos < fileSection.length() - 1)
    {
      return fileSection.substring(0, pos);
    }
    return "";
  }

  /**
   * utility function to extract filename extension
   * 
   * @param fileName
   *          full file path
   * @return extension of file
   */
  private String getFileNameExtension(final String fileName)
  {
    if (fileName == null)
    {
      throw new IllegalArgumentException("file name == null");
    }
    int pos = fileName.lastIndexOf(".");
    if (pos > 0 && pos < fileName.length())
    {
      return fileName.substring(pos+1, fileName.length());
    }
    return "";

  }

  private boolean isVideoFile(final String fileName)
  {
    String[] supportedVideoFormats = new String[]
    {"avi"};
    for(String format:supportedVideoFormats) {
      if(format.equalsIgnoreCase(getFileNameExtension(fileName))) {
        return true;
      }
    }

    
    return false;
  }

  private void openVideoPlayer(final String fileName)
  {
    //#2940 #6
    //if we cannot get the start time from filename open the dialog
    Date start = PlanetmayoFormats.getInstance().parseDateFromFileName(new File(fileName).getName());
    if(start==null) {
      //try to get the start time from last video start time.
      long startTime = PlatformUI.getPreferenceStore().getLong(VideoPlayerView.LAST_VIDEO_START_TIME);
      if(startTime>0) {
        start = new Date(startTime);
      }
      VideoPlayerStartTimeDialog dialog = new VideoPlayerStartTimeDialog();
      dialog.setStartTime(start);
      dialog.setBlockOnOpen(true);
      if(dialog.open()==Window.OK) {
        showVideoPlayer(fileName,dialog.getStartTime());
      }
    }
    else {
      showVideoPlayer(fileName, start);
    }
  }
  
  private void showVideoPlayer(final String fileName,final Date start) {
    IViewPart view = CorePlugin.openSecondaryView(CorePlugin.VIDEO_PLAYER_VIEW,fileNamePartOf(fileName),IWorkbenchPage.VIEW_ACTIVATE);
    if(view instanceof VideoPlayerView) {
      VideoPlayerView videoView = (VideoPlayerView)view;
      videoView.open(fileName,start);
    }
  }

  @Override
  protected void filesDropped(final String[] fileNames)
  {
    super.filesDropped(fileNames);

    // ok, iterate through the files
    if(fileNames.length==1 && isVideoFile(fileNames[0]))
    {
      openVideoPlayer(fileNames[0]);
    }
    else {
      for (int i = 0; i < fileNames.length; i++)
      {
  
        final String thisFilename = fileNames[i];
        loadThisFile(thisFilename);
      }
    }

    // ok, we're probably done - fire the update
    this._myLayers.fireExtended();

    // and resize to make sure we're showing all the data
    this._myChart.rescale();

    // hmm, we may have loaded more track data - but we don't track
    // loading of individual tracks - just fire a "modified" flag
    _trackDataProvider.fireTracksChanged();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#getAdapter(java.lang.Class)
   */
  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class adapter)
  {
    Object res = null;

    if (adapter == Layers.class)
    {
      if (_myLayers != null)
      {
        res = _myLayers;
      }
    }
    else if (adapter == TrackManager.class)
    {
      res = _trackDataProvider;
    }
    else if (adapter == TrackDataProvider.class)
    {
      res = _trackDataProvider;
    }
    else if (adapter == PlainProjection.class)
    {
      res = super.getChart().getCanvas().getProjection();
    }
    else if (adapter == TimeControllerOperationStore.class)
    {
      res = getTimeControllerOperations();
    }
    else if (adapter == LayerPainterManager.class)
    {
      res = _layerPainterManager;
    }
    else if (adapter == ControllablePeriod.class)
    {
      res = _myOperations;
    }
    else if (adapter == TimeControlPreferences.class)
    {
      res = _timePreferences;
    }
    else if (adapter == ControllableTime.class)
    {
      res = _timeManager;
    }
    else if (adapter == TimeProvider.class)
    {
      res = _timeManager;
    }
    else if (adapter == IGotoMarker.class)
    {
      return new IGotoMarker()
      {
        @Override
        public void gotoMarker(final IMarker marker)
        {
          final String lineNum = marker.getAttribute(IMarker.LINE_NUMBER, "na");
          if (lineNum != "na")
          {
            // right, convert to DTG
            final HiResDate tNow = new HiResDate(0, Long.parseLong(lineNum));
            _timeManager.setTime(this, tNow, true);
          }
        }

      };
    }

    else if (adapter == IRollingNarrativeProvider.class)
    {
      // so, do we have any narrative data?
      final Layer narr = _myLayers.findLayer(ImportReplay.NARRATIVE_LAYER);

      if (narr != null)
      {
        // did we find it?
        // cool, cast to object
        final NarrativeWrapper wrapper = (NarrativeWrapper) narr;

        res = wrapper;
      }
    }
    else if (adapter == RelativeProjectionParent.class)
    {
      if (_myRelativeWrapper == null)
      {
        _myRelativeWrapper = new RelativeProjectionParent()
        {

          private Watchable getFirstPosition(final TrackDataProvider provider,
              final TimeManager manager)
          {
            Watchable res = null;

            // do we have a primary?
            final WatchableList priTrack = provider.getPrimaryTrack();
            if (priTrack == null)
            {
              CorePlugin.logError(IStatus.ERROR,
                  "Can't do relative projection without primary track", null);
            }
            else
            {
              final Watchable[] list = priTrack.getNearestTo(manager.getTime());
              if (list != null)
              {
                if (list.length > 0)
                {
                  res = list[0];
                }
              }
            }

            return res;
          }

          @Override
          public double getHeading()
          {
            double res1 = 0.0;
            final Watchable thePos = getFirstPosition(_trackDataProvider,
                _timeManager);

            if (thePos != null)
            {
              // yup, get the centre point
              res1 = thePos.getCourse();
            }

            return res1;
          }

          @Override
          public WorldLocation getLocation()
          {
            MWC.GenericData.WorldLocation res1 = null;
            final Watchable thePos = getFirstPosition(_trackDataProvider,
                _timeManager);

            if (thePos != null)
            {
              // yup, get the centre point
              res1 = thePos.getBounds().getCentre();
            }
            return res1;
          }
        };
      }
      res = _myRelativeWrapper;
    }
    else if (IContentOutlinePage.class.equals(adapter))
    {
      // lazy instantiation of outline page
      if (_outlinePage == null)
      {
        _outlinePage = createOutlinePage();
      }

      res = _outlinePage;
    }
    else if (IPropertySheetPage.class.equals(adapter))
    {
      if (_propertySheetPage == null)
      {
        _propertySheetPage = new PlotPropertySheetPage(this);
      }
      res = _propertySheetPage;
    }

    // did we find anything?
    if (res == null)
    {
      // nope, see if the parent can find anything
      res = super.getAdapter(adapter);
    }

    // ok, done
    return res;
  }

  private IFile getFile(final FileRevisionEditorInput frei)
  {
    IFile file = null;
    final IFileRevision revision = frei.getFileRevision();
    if (revision instanceof LocalFileRevision)
    {
      final LocalFileRevision localFileRevision = (LocalFileRevision) revision;
      if (localFileRevision.getFile() != null)
      {
        file = localFileRevision.getFile();
      }
      else
      {
        final IFileState state = localFileRevision.getState();
        final IPath path = state.getFullPath();
        file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      }
    }
    return file;
  }

  private SensorImportHelper getSensorImportHelperFor(final String sensorName,
      final Color sensorColor, final String introString,
      final boolean needsRange, final boolean isTowedArray,
      final boolean hasFrequency)
  {
    // ok, check the property
    final String showImportWizard = CorePlugin.getToolParent().getProperty(
        PrefsPage.PreferenceConstants.USE_IMPORT_SENSOR_WIZARD);

    // create the relevant helper
    final SensorImportHelper helper;
    if (Boolean.parseBoolean(showImportWizard))
    {
      helper = new SensorImportHelper.SensorImportHelperUI(sensorName,
          sensorColor, introString, needsRange, isTowedArray, hasFrequency);
    }
    else
    {
      helper = new SensorImportHelper.SensorImportHelperHeadless(sensorName);
    }

    return helper;
  }

  private
      org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore
      getTimeControllerOperations()
  {
    return _timeControllerOperations;
  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input)
      throws PartInitException
  {
    setSite(site);
    setInputWithNotify(input);

    // and start the load
    loadThisFile(input);

    // lastly, set the title (if we have one)
    this.setPartName(input.getName());

    // hmm, does this input have an icon?
    final ImageDescriptor icon = input.getImageDescriptor();
    if (icon != null)
    {
      this.setTitleImage(icon.createImage());
    }
  }

  /**
   *
   */
  private LoaderManager initialiseFileLoaders()
  {
    // hey - sort out our plot readers
    return new LoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG, PLUGIN_ID)
    {
      @Override
      public INamedItem createInstance(
          final IConfigurationElement configElement, final String label)
      {
        // get the attributes
        final String theLabel = configElement.getAttribute(
            EXTENSION_TAG_LABEL_ATTRIB);
        final String icon = configElement.getAttribute(
            EXTENSION_TAG_ICON_ATTRIB);
        final String fileTypes = configElement.getAttribute(
            EXTENSION_TAG_EXTENSIONS_ATTRIB);
        final String firstLine = configElement.getAttribute(
            EXTENSION_TAG_FIRST_LINE_ATTRIB);

        // create the instance
        final INamedItem res = new IPlotLoader.DeferredPlotLoader(configElement,
            theLabel, icon, fileTypes, firstLine);

        // and return it.
        return res;
      }
    };
  }

  @Override
  public boolean isSaveAsAllowed()
  {
    return true;
  }

  private boolean isThisSensorAlreadyPresent(final SensorWrapper thisS)
  {
    boolean alreadyLoaded = false;
    final TrackWrapper trk = thisS.getHost();
    if (trk != null)
    {
      final Enumeration<Editable> enumer = trk.getSensors().elements();
      while (enumer.hasMoreElements())
      {
        final SensorWrapper oldS = (SensorWrapper) enumer.nextElement();
        if (oldS.getName().equals(thisS.getName()))
        {
          alreadyLoaded = true;
        }
      }
    }
    return alreadyLoaded;
  }

  @Override
  protected void layerAdded(final Layer layer)
  {
    super.layerAdded(layer);

    // if we've only got one layer, and we like the look of it,
    // then make it the primary layer
    if (_myLayers.size() == 1 && layer instanceof WatchableList)
    {
      final TrackManager mgr = (TrackManager) _trackDataProvider;

      // do we already have a primary
      if (mgr.getPrimaryTrack() == null)
      {
        final Display display = Display.getDefault();
        if (display != null)
        {
          display.asyncExec(new Runnable()
          {

            @Override
            public void run()
            {
              mgr.setPrimary((WatchableList) layer);
            }
          });
        }
      }
    }
  }

  /**
   * layers have been added/removed
   *
   */
  @Override
  protected void layersExtended()
  {
    // ok. this method (callback) may get called a lot.
    // we can queue up these updates, since the method doesn't
    // receive the id of specific layers, the processing
    // applies itself to the current set of layers. So,
    // deferring processing to skip some updates will still be effective

    _refreshJob.cancel();
    _refreshJob.schedule(200);
  }

  /**
   * method called when a helper object has completed a plot-load operation
   *
   * @param source
   */
  public void loadingComplete(final Object source)
  {

    // ok, stop listening for dirty calls - since there will be so many and
    // we
    // don't want
    // to start off with a dirty plot
    startIgnoringDirtyCalls();

    DebriefPlugin.logError(IStatus.INFO, "File loading complete received",
        null);

    // and update the time management bits
    final TimePeriod timePeriod = getPeriodFor(_myLayers);

    if (timePeriod != null)
    {
      _timeManager.setPeriod(this, timePeriod);

      // also give it a current DTG (if it doesn't have one)
      if (_timeManager.getTime() == null)
      {
        _timeManager.setTime(this, timePeriod.getStartDTG(), false);
      }
    }

    // done - now we can process dirty calls again
    stopIgnoringDirtyCalls();

  }

  /**
   * @param input
   *          the file to insert
   */
  private void loadThisFile(final IEditorInput input)
  {
    InputStream is = null;
    if (!input.exists())
    {
      CorePlugin.logError(IStatus.ERROR, "File cannot be found:" + input
          .getName(), null);
      return;
    }
    String name = input.getName();
    try
    {
      final IPersistableElement persist = input.getPersistable();
      if (input instanceof IFileEditorInput)
      {
        final IFileEditorInput ife = (IFileEditorInput) input;
        final IFile iff = ife.getFile();
        iff.refreshLocal(IResource.DEPTH_ONE, null);
        is = iff.getContents();
        name = getAbsoluteName(iff);
      }
      else if (persist instanceof IFileEditorInput)
      {
        final IFileEditorInput ifi = (IFileEditorInput) persist;
        final IFile iff = ifi.getFile();
        is = iff.getContents();
        name = getAbsoluteName(iff);
      }
      else if (input instanceof FileStoreEditorInput)
      {
        final FileStoreEditorInput _input = (FileStoreEditorInput) input;
        final URI _uri = _input.getURI();
        final Path _p = new Path(_uri.getPath());
        name = _uri.getPath();
        final IFileStore _ifs = EFS.getLocalFileSystem().getStore(_p);
        is = _ifs.openInputStream(EFS.NONE, null);
      }
      else if (input instanceof FileRevisionEditorInput)
      {
        final FileRevisionEditorInput frei = (FileRevisionEditorInput) input;
        final IFile file = getFile(frei);
        if (file != null && file.exists())
        {
          name = getAbsoluteName(file);
          is = frei.getStorage().getContents();
        }
      }
      if (is != null)
      {
        loadThisStream(is, name);
      }
      else
      {
        CorePlugin.logError(IStatus.INFO, "Failed to load file from:" + input,
            null);
      }

    }
    catch (final CoreException e)
    {
      CorePlugin.logError(IStatus.ERROR, "Resource out of sync:" + input
          .getName() + " REFRESH the workspace", null);
      MessageDialog.openError(Display.getDefault().getActiveShell(),
          "File out of sync", "This file has been edited or removed:" + input
              .getName()
              + "\nPlease right-click on your navigator project and press Refresh");
    }
  }

  /**
   * @param input
   *          the file to insert
   */
  private void loadThisFile(final String filePath)
  {
    FileInputStream ifs = null;
    try
    {
      ifs = new FileInputStream(filePath);
      loadThisStream(ifs, filePath);
    }
    catch (final FileNotFoundException e)
    {
      CorePlugin.logError(IStatus.ERROR, "Problem loading data file:"
          + filePath, e);
    }
    finally
    {
      if (ifs != null)
      {
        try
        {
          ifs.close();
        }
        catch (final IOException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Problem closing input stream:"
              + filePath, e);
        }
      }
    }
  }

  private void loadThisStream(final InputStream is, final String fileName)
  {
    // right, see if any of them will do our edit
    final IPlotLoader[] loaders = _loader.findLoadersFor(fileName);
    // did we find any?
    if (loaders.length > 0)
    {
      // cool, give them a go...
      try
      {
        for (int i = 0; i < loaders.length; i++)
        {
          final IPlotLoader thisLoader = loaders[i];

          // get it to load. Just in case it's an asychronous load
          // operation, we
          // rely on it calling us back (loadingComplete)
          thisLoader.loadFile(this, is, fileName);

          // special handling - popup a dialog to allow sensor name/color to be
          // set if there's just one sensor
          if (thisLoader instanceof DeferredPlotLoader)
          {
            final DeferredPlotLoader ld = (DeferredPlotLoader) thisLoader;
            final BaseLoader loader = ld.getLoader();
            if (loader != null)
            {
              if (loader instanceof ReplayLoader)
              {
                final ReplayLoader rl = (ReplayLoader) loader;
                final ImportReplay ir = rl.getReplayLoader();
                List<TrackWrapper> candidateHosts = null;

                final Vector<SensorWrapper> sensors = ir.getPendingSensors();

                // see if there are any sensors awaiting a host
                if (sensors.size() >= 1)
                {
                  final Iterator<SensorWrapper> sIter = sensors.iterator();
                  while (sIter.hasNext())
                  {
                    final SensorWrapper sensor = sIter.next();
                    if (sensor.getHost() == null)
                    {
                      // have we sorted out the hosts?
                      if (candidateHosts == null)
                      {
                        candidateHosts = determineCandidateHosts();
                      }

                      if (candidateHosts.size() == 0)
                      {
                        CorePlugin.showMessage("Loading sensor data",
                            "Sensor data can only be loaded after tracks");
                        return;
                      }

                      // ok, let the user choose
                      chooseHostFor(sensor, candidateHosts);
                    }
                  }
                }
                if (sensors.size() == 1)
                {
                  // generate a default sensor name
                  String defaultName = new File(fileName).getName();
                  if (defaultName.contains("."))
                  {
                    final int index = defaultName.lastIndexOf(".");
                    defaultName = defaultName.substring(0, index);
                  }

                  // see if there are any sensors awaiting a color
                  final SensorWrapper thisS = sensors.firstElement();
                  final boolean success = nameThisSensor(thisS, defaultName);

                  // does user wish to name/format sensor?
                  if (!success)
                  {
                    // nope, cancel the import.
                    ir.clearPendingSensorList();
                  }
                }

                // ok, now we can store the pending sensors in their
                // parent tracks
                ir.storePendingSensors();
              }
            }
          }
        }
      }
      catch (final RuntimeException e)
      {
        CorePlugin.logError(IStatus.ERROR, "Problem loading data file:"
            + fileName, e);
      }
    }
  }

  private boolean nameThisSensor(final SensorWrapper thisS,
      final String defaultName)
  {
    // right, just have a quick look and see if the sensor has range data -
    // because
    // if it doesn't we'll let the user set a default
    final boolean needsRange = areWeWaitingForRange(thisS);

    // see if the data is for a towed array
    final boolean isTowedArray = isAmbiguousData(thisS);

    // see if we have freq data
    final boolean hasFrequency = hasFrequencyData(thisS);

    // next, just see if this track already contains sensor
    // data with this name
    final boolean alreadyLoaded = isThisSensorAlreadyPresent(thisS);

    // inform the user if this sensor name is already in use
    final String introString;
    if (alreadyLoaded)
    {
      introString =
          "a one-word title for this block of sensor contacts (e.g. S2046)\n\n"
              + "Note: [" + thisS.getName() + "] is already in use.";
    }
    else
    {
      introString =
          "a one-word title for this block of sensor contacts (e.g. S2046)";
    }

    final String theName;
    final String currentName = thisS.getName();
    if (currentName != null && currentName.equalsIgnoreCase("UNKNOWN"))
    {
      theName = defaultName;
    }
    else
    {
      theName = currentName;
    }

    final SensorImportHelper importHelper = getSensorImportHelperFor(theName,
        thisS.getColor(), introString, needsRange, isTowedArray, hasFrequency);

    // did it work?
    if (importHelper.success())
    {
      // ok, use the name
      thisS.setName(importHelper.getName());
      thisS.setColor(importHelper.getColor());
      thisS.setVisible(importHelper.getVisiblity());

      // are we doing range?
      if (needsRange)
      {
        final WorldDistance theRange = importHelper.getRange();

        // did a range get entered?
        if ((theRange != null) && (theRange.getValue() != 0))
        {
          final Enumeration<Editable> iter = thisS.elements();
          while (iter.hasMoreElements())
          {
            final SensorContactWrapper cut = (SensorContactWrapper) iter
                .nextElement();
            cut.setRange(new WorldDistance(theRange));
          }
        }
      }

      // is it towed array?
      if (isTowedArray)
      {
        final WorldDistance offset = importHelper.getSensorOffset();
        final WorldDistance fixedOffset = offset != null ? offset
            : new WorldDistance(0, WorldDistance.METRES);
        final ArrayLength arrayLength = new ArrayLength(fixedOffset);
        thisS.setSensorOffset(arrayLength);
      }

      if (hasFrequency)
      {
        // and the base frequency
        final String freqStr = importHelper.getBaseFrequency();
        try
        {
          final double freq = Double.parseDouble(freqStr);
          thisS.setBaseFrequency(freq);
        }
        catch (final NumberFormatException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Couldn't parse base frequency:"
              + freqStr, e);
          e.printStackTrace();
        }
      }

      if (importHelper.applyRainbow())
      {
        applyRainbowShadingTo(thisS);
      }
    }

    return importHelper.success();
  }

  public void outlinePageClosed()
  {
    _outlinePage = null;
  }

  private void reconnectSegments(final Plottable newItem,
      final HasEditables parent)
  {
    // ok, have a look at the pasted track
    if (parent != null && parent instanceof TrackWrapper && newItem == null)
    {
      final TrackWrapper tw = (TrackWrapper) parent;
      SegmentList segs = tw.getSegments();
      Enumeration<Editable> iter = segs.elements();
      boolean layersChanged = false;

      while (iter.hasMoreElements())
      {
        final Editable editable = iter.nextElement();

        // RelativeTMASegment implements the correct interface
        // to let us do this common processing
        if (editable instanceof RelativeTMASegment)
        {
          final RelativeTMASegment needer = (RelativeTMASegment) editable;
          final boolean thisChanged = needer.getLayers() != _myLayers;
          if (thisChanged)
          {
            needer.setLayers(_myLayers);
          }

          layersChanged = layersChanged || thisChanged;
        }
      }

      // ok, have we processed a layer change
      if (layersChanged)
      {
        // yes. ok, loop through and update any dynamic infills
        segs = tw.getSegments();
        iter = segs.elements();

        while (iter.hasMoreElements())
        {
          final Editable editable = iter.nextElement();
          if (editable instanceof DynamicInfillSegment)
          {
            final DynamicInfillSegment ds = (DynamicInfillSegment) editable;

            ds.clear();

            //
            @SuppressWarnings("unused")
            final boolean wasted = ds.getVisible();
          }
        }

      }

    }
  }

  @Override
  public void reload(final IFile file)
  {
    closeEditor(false);
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        final IWorkbenchPage page = getSite().getPage();
        final IEditorDescriptor desc = PlatformUI.getWorkbench()
            .getEditorRegistry().getDefaultEditor(file.getName());
        try
        {
          page.openEditor(new FileEditorInput(file), desc.getId());
        }
        catch (final PartInitException e)
        {
          DebriefPlugin.logError(IStatus.ERROR, "Failed trying to open file: "
              + file.getName(), e);
        }
      }
    });

  }

  @Override
  public void selectPlottable(final Plottable tgt, final Layer parentLayer)
  {
    // handle some special cases.
    if (tgt instanceof FixWrapper)
    {
      CorePlugin.logError(IStatus.INFO,
          "Double-click processed, fixing parent levels for:" + tgt, null);

      // ok, we have to generate the correct object model
      final FixWrapper fix = (FixWrapper) tgt;
      final TrackSegment segment = fix.getSegment();
      final TrackWrapper track = segment.getWrapper();
      final SegmentList segList = track.getSegments();
      final Layers layers = getChart().getLayers();
      final ISelection selected = wrapObjects(parentLayer, fix, segment,
          segList, layers);
      fireSelectionChanged(selected);
    }
    else if (tgt instanceof SensorContactWrapper)
    {
      CorePlugin.logError(IStatus.INFO,
          "Double-click processed, fixing parent levels for:" + tgt, null);

      // ok, we have to generate the correct object model
      final SensorContactWrapper cut = (SensorContactWrapper) tgt;
      final SensorWrapper sensor = cut.getSensor();
      final TrackWrapper track = sensor.getHost();
      final BaseLayer sList = track.getSensors();
      final Layers layers = getChart().getLayers();
      final ISelection selected = wrapObjects(parentLayer, cut, sensor, sList,
          layers);
      fireSelectionChanged(selected);
    }
    else if (tgt instanceof TMAContactWrapper)
    {
      CorePlugin.logError(IStatus.INFO,
          "Double-click processed, fixing parent levels for:" + tgt, null);

      // ok, we have to generate the correct object model
      final TMAContactWrapper cut = (TMAContactWrapper) tgt;
      final TMAWrapper sensor = cut.getTMATrack();
      final TrackWrapper track = sensor.getHost();
      final BaseLayer sList = track.getSolutions();
      final Layers layers = getChart().getLayers();
      final ISelection selected = wrapObjects(parentLayer, cut, sensor, sList,
          layers);
      fireSelectionChanged(selected);
    }
    else
    {
      super.selectPlottable(tgt, parentLayer);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#timeChanged()
   */
  @Override
  protected void timeChanged(final HiResDate newDTG)
  {
    super.timeChanged(newDTG);

    // just check we're ready for plotting.
    if (getChart() == null)
    {
      return;
    }

    if (_updatingPlot)
    {
      // skip the update - we're already at it
    }
    else
    {
      _updatingPlot = true;

      try
      {
        // note, we've learn't to use the default display instead of the
        // current one, we were get a null returned since this thread may not
        // have a display
        final Display myDis = Display.getDefault();
        if (myDis != null)
        {
          myDis.asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              if (getChart() == null || getChart().getCanvas() == null)
              {
                return;
              }
              // ok - update our painter
              getChart().getCanvas().updateMe();
            }
          });
        }

      }
      finally
      {
        _updatingPlot = false;
      }
    }

  }

  private ISelection wrapObjects(final Layer track, final Editable item,
      final Editable itemParent, final Editable parentList, final Layers layers)
  {
    final EditableWrapper parentP = new EditableWrapper(track, null, layers);
    final EditableWrapper segListW = new EditableWrapper(parentList, parentP,
        layers);
    final EditableWrapper segmentW = new EditableWrapper(itemParent, segListW,
        layers);
    final EditableWrapper fixW = new EditableWrapper(item, segmentW, layers);
    final ISelection selected = new StructuredSelection(fixW);
    return selected;
  }
}
