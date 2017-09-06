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
package org.mwc.debrief.track_shift.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.TextAnchor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.core.editors.PlotOutlinePage;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneChartConfig;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.controls.ZoneUndoRedoProvider;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.target.ILegStorer;
import org.mwc.debrief.track_shift.zig_detector.target.IZigStorer;
import org.mwc.debrief.track_shift.zig_detector.target.ZigDetector;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ISecondaryTrack;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.TrackDataProvider;
import MWC.TacticalData.TrackDataProvider.TrackDataListener;
import MWC.TacticalData.TrackDataProvider.TrackShiftListener;

/**
 */

abstract public class BaseStackedDotsView extends ViewPart implements
    ErrorLogger
{
 

  private static final String SHOW_DOT_PLOT = "SHOW_DOT_PLOT";
  private static final String SHOW_OVERVIEW = "SHOW_OVERVIEW";
  private static final String SHOW_LINE_PLOT = "SHOW_LINE_PLOT";
  private static final String SHOW_ZONES = "SHOW_ZONES";
  private static final String SELECT_ON_CLICK = "SELECT_ON_CLICK";
  private static final String SHOW_ONLY_VIS = "ONLY_SHOW_VIS";

  private static final String SHOW_CROSSHAIRS = "SHOW_CROSSHAIRS";

 
  /*
   * Undo and redo actions
   */
  private HandlerAction undoAction;

  private HandlerAction redoAction;

  private IUndoContext undoContext;

  // private enum SliceMode
  // {
  // ORIGINAL, PEAK_FIT, AREA_UNDER_CURVE, ARTIFICIAL_LEG;
  // }

  private final IOperationHistory operationHistory =
      new DefaultOperationHistory();

  protected final ZoneUndoRedoProvider undoRedoProvider =
      new ZoneUndoRedoProvider()
      {
        @Override
        public void execute(final IUndoableOperation operation)
        {
          operation.addContext(undoContext);
          try
          {
            operationHistory.execute(operation, null, null);
          }
          catch (final ExecutionException e)
          {
            e.printStackTrace();
          }
          finally
          {
            if (undoAction != null)
            {
              undoAction.refreah();
            }
            if (redoAction != null)
            {
              redoAction.refreah();
            }
            getViewSite().getActionBars().updateActionBars();
          }
        }
      };

  /**
   * helper application to help track creation/activation of new plots
   */
  protected PartMonitor _myPartMonitor;

  /**
   * the errors we're plotting
   */
  protected XYPlot _dotPlot;

  /**
   * and the actual values
   * 
   */
  protected XYPlot _linePlot;

  /**
   * and the actual values
   * 
   */
  protected XYPlot _targetOverviewPlot;

  /**
   * declare the tgt course dataset, we need to give it to the renderer
   * 
   */
  final protected TimeSeriesCollection _targetCourseSeries =
      new TimeSeriesCollection();

  /**
   * declare the tgt speed dataset, we need to give it to the renderer
   * 
   */
  final protected TimeSeriesCollection _targetSpeedSeries =
      new TimeSeriesCollection();

  /**
   * legacy helper class
   */
  final protected StackedDotHelper _myHelper;
  /**
   * our listener for tracks being shifted...
   */
  final protected TrackShiftListener _myShiftListener;
  /**
   * buttons for which plots to show
   * 
   */
  protected Action _showLinePlot;
  protected Action _showDotPlot;

  protected Action _showTargetOverview;

  protected Action _showZones;

  /**
   * flag indicating whether we should show cross-hairs
   * 
   */
  private Action _showCrossHairs;

  /**
   * flag indicating whether we should only show stacked dots for visible fixes
   */
  protected Action _onlyVisible;

  /**
   * flag indicating whether we should select the clicked item in the Outline View
   */
  private Action _selectOnClick;

  /**
   * our layers listener...
   */
  protected DataListener _layersListener;

  /**
   * the set of layers we're currently listening to
   */
  protected Layers _ourLayersSubject;

  protected TrackDataProvider _myTrackDataProvider;

  protected ChartComposite _holder;

  private JFreeChart _myChart;

  private Vector<Action> _customActions;

  protected Action _autoResize;

  private CombinedDomainXYPlot _combined;

  final protected TrackDataListener _myTrackDataListener;

  // private Action _magicBtn;

  /**
   * does our output need bearing in the data?
   * 
   */
  private final boolean _needBrg;

  /**
   * does our output need frequency in the data?
   * 
   */
  private final boolean _needFreq;

  protected Vector<ISelectionProvider> _selProviders;

  protected ISelectionChangedListener _mySelListener;
  protected Vector<DraggableItem> _draggableSelection;
  protected boolean _itemSelectedPending = false;
  protected ZoneChart ownshipZoneChart;
  private ZoneChart targetZoneChart;
  final protected TimeSeries ownshipCourseSeries = new TimeSeries(
      "Ownship course");

  final protected TimeSeries targetBearingSeries = new TimeSeries("Bearing");
  final protected TimeSeries targetCalculatedSeries = new TimeSeries(
      "Calculated Bearing");
  private Precision _slicePrecision = Precision.MEDIUM;
  private Action _precisionOne;
  private Action _precisionTwo;

  private Action _precisionThree;
  private final PropertyChangeListener _infillListener;
  /**
   * text label associated with the cross-hair marker
   * 
   */
  private XYTextAnnotation crossHairAnnotation;

  protected ResidualXYItemRenderer _overviewSpeedRenderer;

  protected WrappingResidualRenderer _overviewCourseRenderer;

  /**
   * 
   * @param needBrg
   *          if the algorithm needs bearing data
   * @param needFreq
   *          if the agorithm needs frequency data
   */
  protected BaseStackedDotsView(final boolean needBrg, final boolean needFreq)
  {
    _myHelper = new StackedDotHelper();
    _needBrg = needBrg;
    _needFreq = needFreq;
    // create the actions - the 'centre-y axis' action may get called before
    // the
    // interface is shown
    makeActions();

    // declare the listeners
    _myShiftListener = new TrackShiftListener()
    {
      @Override
      public void trackShifted(final WatchableList subject)
      {
        // the tracks have moved, we haven't changed
        // the tracks or
        // anything like that...
        updateStackedDots(false);
      }
    };

    final ErrorLogger logger = this;
    _myTrackDataListener = new TrackDataListener()
    {
      @Override
      public void tracksUpdated(final WatchableList primary,
          final WatchableList[] secondaries)
      {

        // has the primary changed?
        final boolean primarySame =
            (_myHelper.getPrimaryTrack() != null)
                && (_myHelper.getPrimaryTrack().equals(primary));
        final boolean secSame =
            (_myHelper.getSecondaryTrack() != null) && secondaries != null
                && secondaries.length == 1
                && (_myHelper.getSecondaryTrack().equals(secondaries[0]));

        // ok, have things changed?
        _myHelper.initialise(_myTrackDataProvider, false, _onlyVisible
            .isChecked(), _holder, logger, getType(), _needBrg, _needFreq);

        // clear the zone charts, but maybe not the primary
        clearZoneCharts(!secSame || !primarySame, !secSame, !secSame);

        // ahh, the tracks have changed, better
        // update the doublets

        // ok, do the recalc
        updateStackedDots(true);

        // ok - if we're on auto update, do the
        // update
        updateLinePlotRanges();

        // initialise the zones
        updateTargetZones();
        // List<Zone> zones = getTargetZones();
        // if (targetZoneChart != null)
        // {
        // targetZoneChart.setZones(zones);
        // }
      }
    };

    _infillListener = new PropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        if (evt.getNewValue() instanceof DynamicInfillSegment)
        {
          updateStackedDots(false);
        }
      }
    };

  }

  /**
   * additional method, to allow extra items to be added before the segment modes
   * 
   * @param toolBarManager
   */
  protected void addExtras(final IToolBarManager toolBarManager)
  {
  }

  /**
   * the track has been moved, update the dots
   */
  void clearPlots()
  {
    final Runnable runner = new Runnable()
    {
      @Override
      public void run()
      {
        _dotPlot.setDataset(null);
        _linePlot.setDataset(null);
        _targetOverviewPlot.setDataset(null);
        _targetOverviewPlot.setDataset(1, null);
      }
    };

    if (Thread.currentThread() == Display.getDefault().getThread())
    {
      runner.run();
    }
    else
    {
      // we're not in the display thread - make it so!
      Display.getDefault().syncExec(runner);
    }
  }

  private void clearZoneCharts(final boolean osChanged,
      final boolean tgtChanged, final boolean clearTgtData)
  {
    if (osChanged)
    {
      ownshipCourseSeries.clear();

      if (ownshipZoneChart != null)
      {
        ownshipZoneChart.clearZones();
      }
    }

    // and the secondary
    if (tgtChanged)
    {
      if (clearTgtData)
      {
        targetBearingSeries.clear();
        targetCalculatedSeries.clear();
      }

      if (targetZoneChart != null)
      {
        targetZoneChart.clearZones();
      }
    }
  }

  private void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void createGlobalActionHandlers()
  {
    final IActionBars actionBars = getViewSite().getActionBars();
    // set up action handlers that operate on the current context
    undoAction = new HandlerAction()
    {
      @Override
      public void excecute()
      {
        try
        {
          operationHistory.undo(undoContext, null, null);
        }
        catch (final ExecutionException e)
        {
          e.printStackTrace();
        }

      }

      @Override
      public void refreah()
      {
        setEnabled(operationHistory.canUndo(undoContext));
        if (isEnabled())
        {
          final String toolTipText =
              "Undo "
                  + operationHistory.getUndoOperation(undoContext).getLabel();
          setText(toolTipText);
          setToolTipText(toolTipText);
        }
        else
        {
          setText("Undo");
          setToolTipText("Undo");
        }
      }
    };
    // todo:change to debrief version of icons
    undoAction.setText("Undo");
    undoAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/undo.png"));
    undoAction.setDisabledImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/undo.png"));
    undoAction.setActionDefinitionId(ActionFactory.UNDO.getCommandId());
    redoAction = new HandlerAction()
    {

      @Override
      public void excecute()
      {
        try
        {
          operationHistory.redo(undoContext, null, null);
        }
        catch (final ExecutionException e)
        {
          e.printStackTrace();
        }
      }

      @Override
      public void refreah()
      {
        setEnabled(operationHistory.canRedo(undoContext));
        if (isEnabled())
        {
          final String toolTipText =
              "Redo "
                  + operationHistory.getRedoOperation(undoContext).getLabel();
          setText(toolTipText);
          setToolTipText(toolTipText);
        }
        else
        {
          setText("Redo");
          setToolTipText("Redo");
        }
      }
    };

    // todo:change to debrief version of icons
    redoAction.setText("Redo");
    redoAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/redo.png"));
    redoAction.setDisabledImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/redo.png"));
    redoAction.setActionDefinitionId(ActionFactory.REDO.getCommandId());

    getViewSite().getPage().addPartListener(new IPartListener()
    {

      AtomicBoolean activate = new AtomicBoolean(false);

      @Override
      public void partActivated(final IWorkbenchPart part)
      {
        refresh(part);
      }

      @Override
      public void partBroughtToTop(final IWorkbenchPart part)
      {
        refresh(part);
      }

      @Override
      public void partClosed(final IWorkbenchPart part)
      {
        refresh(part);
      }

      @Override
      public void partDeactivated(final IWorkbenchPart part)
      {
        refresh(part);
      }

      @Override
      public void partOpened(final IWorkbenchPart part)
      {
        refresh(part);
      }

      void refresh(final IWorkbenchPart part)
      {
        if (part == BaseStackedDotsView.this)
        {
          activate.set(true);
          undoAction.refreah();
          redoAction.refreah();
          actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
              undoAction);
          actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
              redoAction);
          actionBars.updateActionBars();
        }
        else if (activate.getAndSet(false))
        {
          actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
          actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
          actionBars.updateActionBars();
        }
      }
    });

    operationHistory
        .addOperationHistoryListener(new IOperationHistoryListener()
        {
          @Override
          public void historyNotification(final OperationHistoryEvent event)
          {
            if (event.getEventType() == OperationHistoryEvent.REDONE
                || event.getEventType() == OperationHistoryEvent.UNDONE)
            {
              undoAction.refreah();
              redoAction.refreah();
              actionBars.updateActionBars();
            }
          }
        });
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent)
  {
    initializeOperationHistory();
    createGlobalActionHandlers();
    parent.setLayout(new FillLayout(SWT.VERTICAL));
    final SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
    _holder =
        new ChartComposite(sashForm, SWT.NONE, null, 400, 600, 300, 200, 1800,
            1800, true, true, true, true, true, true)
        {
          @Override
          public void mouseUp(final MouseEvent event)
          {
            super.mouseUp(event);
            final JFreeChart c = getChart();
            if (c != null)
            {
              c.setNotify(true); // force redraw
            }
          }
        };

    // hey - now create the stacked plot!
    createStackedPlot();

    // /////////////////////////////////////////
    // ok - listen out for changes in the view
    // /////////////////////////////////////////
    _selProviders = new Vector<ISelectionProvider>();
    _mySelListener = new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(final SelectionChangedEvent event)
      {
        final ISelection sel = event.getSelection();
        final Vector<DraggableItem> dragees = new Vector<DraggableItem>();
        if (sel instanceof StructuredSelection)
        {
          final StructuredSelection str = (StructuredSelection) sel;
          final Iterator<?> iter = str.iterator();
          while (iter.hasNext())
          {
            final Object object = iter.next();
            if (object instanceof EditableWrapper)
            {
              final EditableWrapper ew = (EditableWrapper) object;
              final Editable item = ew.getEditable();
              if (item instanceof DraggableItem)
              {
                dragees.add((DraggableItem) item);
              }
            }
            else
            {
              return;
            }
          }

          // ok, we've just got draggable items - override the current
          // item
          _draggableSelection = dragees;
        }
      }
    };

    // sort out the part monitor
    _myPartMonitor =
        new PartMonitor(getSite().getWorkbenchWindow().getPartService());

    // now start listening out for people's parts
    watchMyParts();

    // put the actions in the UI
    contributeToActionBars();

    // we will also listen out for zone changes
    @SuppressWarnings("unused")
    final ZoneChart.ZoneListener ownshipListener = getOwnshipListener();
    final ZoneChart.ZoneListener targetListener = getTargetListener();

    final Zone[] osZones = new ZoneChart.Zone[]
    {};
    final ZoneChart.ColorProvider blueProv = new ZoneChart.ColorProvider()
    {
      @Override
      public Color getZoneColor()
      {
        return DebriefColors.BLUE;
      }
    };

    // put the courses into a TimeSeries
    final ZoneSlicer ownshipLegSlicer = new ZoneSlicer()
    {
      @Override
      public ArrayList<Zone> performSlicing()
      {
        return StackedDotHelper.sliceOwnship(ownshipCourseSeries, blueProv);
      }
    };

    final ZoneChartConfig oZoneConfig =
        new ZoneChart.ZoneChartConfig("Ownship Legs", "Course",
            DebriefColors.BLUE);
    ownshipZoneChart =
        ZoneChart.create(oZoneConfig, undoRedoProvider, sashForm, osZones,
            ownshipCourseSeries, null, blueProv, ownshipLegSlicer);

    final Zone[] tgtZones = getTargetZones().toArray(new Zone[]
    {});
    // we need a color provider for the target legs
    final ZoneChart.ColorProvider randomProv = new ZoneChart.ColorProvider()
    {
      @Override
      public Color getZoneColor()
      {
        final Random random = new Random();
        final float hue = random.nextFloat();
        // Saturation between 0.1 and 0.3
        final float saturation = (random.nextInt(2000) + 7000) / 10000f;
        final float luminance = 0.9f;
        final Color color = Color.getHSBColor(hue, saturation, luminance);
        return color;
      }
    };

    // put the bearings into a TimeSeries

    final ZoneSlicer targetLegSlicer = new ZoneSlicer()
    {
      @Override
      public List<Zone> performSlicing()
      {
        // hmm, the above set of bearings only covers windows where we have
        // target track defined. But, in order to consider the actual extent
        // of the target track we need all the data. So, get the bearings
        // captured during the whole outer time period of the secondary track

        final ISecondaryTrack secondary = _myHelper.getSecondaryTrack();

        final List<Zone> res;

        if (secondary != null)
        {
          // now find data in the primary track
          final TimePeriod period =
              new TimePeriod.BaseTimePeriod(secondary.getStartDTG(), secondary
                  .getEndDTG());
          final List<SensorContactWrapper> bearings =
              _myHelper.getBearings(_myHelper.getPrimaryTrack(), _onlyVisible
                  .isChecked(), period);
          res =
              sliceTarget(ownshipZoneChart.getZones(), bearings, randomProv,
                  secondary, _slicePrecision);
        }
        else
        {
          res = null;
        }
        return res;
      }
    };

    final ZoneChartConfig tZoneConfig =
        new ZoneChart.ZoneChartConfig("Target Legs", "Bearing",
            DebriefColors.RED);
    targetZoneChart =
        ZoneChart.create(tZoneConfig, undoRedoProvider, sashForm, tgtZones,
            targetBearingSeries, targetCalculatedSeries, randomProv,
            targetLegSlicer);

    targetZoneChart.addZoneListener(targetListener);

    // and set the proportions of space allowed
    sashForm.setWeights(new int[]
    {4, 1, 1});
    sashForm
        .setBackground(sashForm.getDisplay().getSystemColor(SWT.COLOR_GRAY));

    // sort out zone chart visibility
    setZoneChartsVisible(_showZones.isChecked());
  }

  /**
   * method to create a working plot (to contain our data)
   * 
   * @return the chart, in it's own panel
   */
  @SuppressWarnings("deprecation")
  protected void createStackedPlot()
  {
    // first create the x (time) axis
    final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
    _df.setTimeZone(TimeZone.getTimeZone("GMT"));

    final DateAxis xAxis = new CachedTickDateAxis("");
    xAxis.setDateFormatOverride(_df);
    final Font tickLabelFont = new Font("Courier", Font.PLAIN, 13);
    xAxis.setTickLabelFont(tickLabelFont);
    xAxis.setTickLabelPaint(Color.BLACK);
    xAxis.setStandardTickUnits(DateAxisEditor
        .createStandardDateTickUnitsAsTickUnits());
    xAxis.setAutoTickUnitSelection(true);

    // create the special stepper plot
    _dotPlot = new XYPlot();
    final NumberAxis errorAxis = new NumberAxis("Error (" + getUnits() + ")");
    final Font axisLabelFont = new Font("Courier", Font.PLAIN, 16);
    errorAxis.setLabelFont(axisLabelFont);
    errorAxis.setTickLabelFont(tickLabelFont);
    _dotPlot.setRangeAxis(errorAxis);
    _dotPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
    _dotPlot
        .setRenderer(new ColourStandardXYItemRenderer(null, null, _dotPlot));

    _dotPlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    _dotPlot.setRangeGridlineStroke(new BasicStroke(2));
    _dotPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
    _dotPlot.setDomainGridlineStroke(new BasicStroke(2));

    // now try to do add a zero marker on the error bar
    final Paint thePaint = Color.DARK_GRAY;
    final Stroke theStroke = new BasicStroke(3);
    final ValueMarker zeroMarker = new ValueMarker(0.0, thePaint, theStroke);
    _dotPlot.addRangeMarker(zeroMarker);

    _linePlot = new XYPlot();
    final NumberAxis absBrgAxis =
        new NumberAxis("Absolute (" + getUnits() + ")");
    absBrgAxis.setLabelFont(axisLabelFont);
    absBrgAxis.setTickLabelFont(tickLabelFont);
    _linePlot.setRangeAxis(absBrgAxis);
    absBrgAxis.setAutoRangeIncludesZero(false);
    _linePlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
    final DefaultXYItemRenderer lineRend =
        new ColourStandardXYItemRenderer(null, null, _linePlot);
    lineRend.setPaint(Color.DARK_GRAY);
    _linePlot.setRenderer(lineRend);

    _linePlot.setDomainCrosshairVisible(_showCrossHairs.isChecked());
    _linePlot.setRangeCrosshairVisible(_showCrossHairs.isChecked());
    _linePlot.setDomainCrosshairPaint(Color.GRAY);
    _linePlot.setRangeCrosshairPaint(Color.GRAY);
    _linePlot.setDomainCrosshairStroke(new BasicStroke(3.0f));
    _linePlot.setRangeCrosshairStroke(new BasicStroke(3.0f));
    _linePlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    _linePlot.setRangeGridlineStroke(new BasicStroke(2));
    _linePlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
    _linePlot.setDomainGridlineStroke(new BasicStroke(2));

    _targetOverviewPlot = new XYPlot();
    final NumberAxis overviewCourse = new NumberAxis("Course (\u00b0)")
    {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public NumberTickUnit getTickUnit()
      {
        final NumberTickUnit tickUnit = super.getTickUnit();
        if (tickUnit.getSize() < 15)
        {
          return tickUnit;
        }
        else if (tickUnit.getSize() < 45)
        {
          return new NumberTickUnit(20);
        }
        else if (tickUnit.getSize() < 90)
        {
          return new NumberTickUnit(30);
        }
        else if (tickUnit.getSize() < 180)
        {
          return new NumberTickUnit(45);
        }
        else
        {
          return new NumberTickUnit(90);
        }
      }
    };
    overviewCourse.setUpperMargin(0);
    overviewCourse.setLabelFont(axisLabelFont);
    overviewCourse.setTickLabelFont(tickLabelFont);
    final NumberAxis overviewSpeed = new NumberAxis("Speed (Kts)");
    overviewSpeed.setLabelFont(axisLabelFont);
    overviewSpeed.setTickLabelFont(tickLabelFont);
    _targetOverviewPlot.setRangeAxis(overviewCourse);
    _targetOverviewPlot.setRangeAxis(1, overviewSpeed);
    absBrgAxis.setAutoRangeIncludesZero(false);
    _targetOverviewPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
    _overviewCourseRenderer =
        new WrappingResidualRenderer(null, null, _targetCourseSeries, 0, 360);
    _overviewCourseRenderer.setSeriesPaint(0, DebriefColors.RED.brighter());
    _overviewCourseRenderer.setSeriesPaint(1, DebriefColors.BLUE);
    _overviewCourseRenderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0,
        8.0, 8.0));
    _overviewCourseRenderer.setSeriesShapesVisible(1, false);
    _overviewCourseRenderer.setSeriesStroke(0, new BasicStroke(2f));
    _overviewCourseRenderer.setSeriesStroke(1, new BasicStroke(2f));
    _overviewSpeedRenderer =
        new ResidualXYItemRenderer(null, null, _targetSpeedSeries);
    _overviewSpeedRenderer.setPaint(DebriefColors.RED.darker());
    _overviewSpeedRenderer.setSeriesShape(0, new Rectangle2D.Double(-4.0, -4.0,
        8.0, 8.0));
    _overviewSpeedRenderer.setSeriesStroke(0, new BasicStroke(2f));
    _targetOverviewPlot.setRenderer(0, _overviewCourseRenderer);
    _targetOverviewPlot.setRenderer(1, _overviewSpeedRenderer);
    _targetOverviewPlot.mapDatasetToRangeAxis(0, 0);
    _targetOverviewPlot.mapDatasetToRangeAxis(1, 1);
    _targetOverviewPlot.setRangeAxisLocation(0, AxisLocation.TOP_OR_LEFT);
    _targetOverviewPlot.setRangeAxisLocation(1, AxisLocation.TOP_OR_LEFT);
    _targetOverviewPlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    _targetOverviewPlot.setRangeGridlineStroke(new BasicStroke(2));
    _targetOverviewPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
    _targetOverviewPlot.setDomainGridlineStroke(new BasicStroke(2));

    // and the plot object to display the cross hair value
    crossHairAnnotation = new XYTextAnnotation("-----", 2, 2);
    crossHairAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);

    final Font annotationFont = new Font("Courier", Font.BOLD, 16);
    crossHairAnnotation.setFont(annotationFont);
    crossHairAnnotation.setPaint(Color.DARK_GRAY);
    crossHairAnnotation.setBackgroundPaint(Color.white);
    if (_showCrossHairs.isChecked())
    {
      _linePlot.addAnnotation(crossHairAnnotation);
    }

    // give them a high contrast backdrop
    _dotPlot.setBackgroundPaint(Color.white);
    _linePlot.setBackgroundPaint(Color.white);
    _targetOverviewPlot.setBackgroundPaint(Color.white);

    // set the y axes to autocalculate
    _dotPlot.getRangeAxis().setAutoRange(true);
    _linePlot.getRangeAxis().setAutoRange(true);
    _targetOverviewPlot.getRangeAxis().setAutoRange(true);

    _combined = new CombinedDomainXYPlot(xAxis);
    _combined.add(_linePlot);
    _combined.add(_dotPlot);
    _combined.add(_targetOverviewPlot);
    _combined.setOrientation(PlotOrientation.HORIZONTAL);

    // put the plot into a chart
    _myChart = new JFreeChart(null, null, _combined, true);

    final LegendItemSource[] sources =
    {_linePlot, _targetOverviewPlot};
    _myChart.getLegend().setSources(sources);

    _myChart.addProgressListener(new ChartProgressListener()
    {
      @Override
      public void chartProgress(final ChartProgressEvent cpe)
      {
        if (cpe.getType() != ChartProgressEvent.DRAWING_FINISHED)
        {
          return;
        }

        // is hte line plot visible?
        if (!_showLinePlot.isChecked())
        {
          return;
        }

        // double-check our label is still in the right place
        final double xVal = _linePlot.getRangeAxis().getLowerBound();
        final double yVal = _linePlot.getDomainAxis().getUpperBound();
        boolean annotChanged = false;
        if (crossHairAnnotation.getX() != yVal)
        {
          crossHairAnnotation.setX(yVal);
          annotChanged = true;
        }
        if (crossHairAnnotation.getY() != xVal)
        {
          crossHairAnnotation.setY(xVal);
          annotChanged = true;
        }
        // and write the text
        final String numA =
            MWC.Utilities.TextFormatting.GeneralFormat
                .formatOneDecimalPlace(_linePlot.getRangeCrosshairValue());
        final Date newDate =
            new Date((long) _linePlot.getDomainCrosshairValue());
        final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
        _df.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String dateVal = _df.format(newDate);
        final String theMessage = " [" + dateVal + "," + numA + "]";
        if (!theMessage.equals(crossHairAnnotation.getText()))
        {
          crossHairAnnotation.setText(theMessage);
          annotChanged = true;
        }
        if (annotChanged && _showCrossHairs.isChecked())
        {
          _linePlot.removeAnnotation(crossHairAnnotation);
          _linePlot.addAnnotation(crossHairAnnotation);
        }

        // ok, do we also have a selection event pending
        if (_itemSelectedPending && _selectOnClick.isChecked())
        {
          _itemSelectedPending = false;
          showFixAtThisTime(newDate);
        }
      }
    });

    // and insert into the panel
    _holder.setChart(_myChart);

    _holder.addChartMouseListener(new ChartMouseListener()
    {
      @Override
      public void chartMouseClicked(final ChartMouseEvent arg0)
      {
        // ok, remember it was clicked
        _itemSelectedPending = true;
      }

      @Override
      public void chartMouseMoved(final ChartMouseEvent arg0)
      {
      }
    });

    // do a little tidying to reflect the memento settings
    if (!_showLinePlot.isChecked())
    {
      _combined.remove(_linePlot);
    }
    if (!_showDotPlot.isChecked() && _showLinePlot.isChecked())
    {
      _combined.remove(_dotPlot);
    }
    if (!_showTargetOverview.isChecked())
    {
      _combined.remove(_targetOverviewPlot);
    }
  }

  /**
   * view is closing, shut down, preserve life
   */
  @Override
  public void dispose()
  {
    // get parent to ditch itself
    super.dispose();

    // ditch the actions
    if (_customActions != null)
    {
      _customActions.removeAllElements();
    }

    // are we listening to any layers?
    if (_ourLayersSubject != null)
    {
      _ourLayersSubject.removeDataReformattedListener(_layersListener);
      _ourLayersSubject.removeDataExtendedListener(_layersListener);
    }

    if (_myTrackDataProvider != null)
    {
      _myTrackDataProvider.removeTrackShiftListener(_myShiftListener);
      _myTrackDataProvider.removeTrackDataListener(_myTrackDataListener);

      // remove ourselves as a location listener from the sec track, if there is one
      final WatchableList[] secs = _myTrackDataProvider.getSecondaryTracks();
      if (secs != null && secs.length == 1)
      {
        final TrackWrapper secT = (TrackWrapper) secs[0];
        secT.removePropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
            _infillListener);
      }
    }

    // stop the part monitor
    _myPartMonitor.ditch();

  }

  protected void fillLocalPullDown(final IMenuManager manager)
  {
    manager.add(_showCrossHairs);
    manager.add(_onlyVisible);
    manager.add(_selectOnClick);
    // and the help link
    manager.add(new Separator());

    // TEMPORARILY INTRODUCE SLICE MODE
    // MenuManager sliceMenu = new MenuManager("Ownship slicing algorithm");
    // manager.add(sliceMenu);
    //
    // // ok - try to add modes for the slicing algorithm
    // _modePeak = new Action("Peak tracking", SWT.TOGGLE)
    // {
    // @Override
    // public void run()
    // {
    // super.run();
    // _sliceMode = SliceMode.PEAK_FIT;
    // _modeThreshold.setChecked(false);
    // _modeArea.setChecked(false);
    // _modeArtificial.setChecked(false);
    // _modePeak.setChecked(true);
    // }
    // };
    // _modeArtificial = new Action("Artificial legs", SWT.TOGGLE)
    // {
    // @Override
    // public void run()
    // {
    // super.run();
    // _sliceMode = SliceMode.ARTIFICIAL_LEG;
    // _modeThreshold.setChecked(false);
    // _modePeak.setChecked(false);
    // _modeArea.setChecked(false);
    // _modeArtificial.setChecked(true);
    // }
    // };
    // _modeArea = new Action("Area under curve", SWT.TOGGLE)
    // {
    // @Override
    // public void run()
    // {
    // super.run();
    // _sliceMode = SliceMode.AREA_UNDER_CURVE;
    // _modePeak.setChecked(false);
    // _modeThreshold.setChecked(false);
    // _modeArtificial.setChecked(false);
    // _modeArea.setChecked(true);
    // }
    // };
    // _modeThreshold = new Action("Original", SWT.TOGGLE)
    // {
    // @Override
    // public void run()
    // {
    // super.run();
    // _sliceMode = SliceMode.ORIGINAL;
    // _modePeak.setChecked(false);
    // _modeArea.setChecked(false);
    // _modeArtificial.setChecked(false);
    // _modeThreshold.setChecked(true);
    // // _modeTwo.setChecked(false);
    // }
    // };
    //
    // _modePeak.setChecked(true);
    // sliceMenu.add(_modePeak);
    // sliceMenu.add(_modeArtificial);
    // sliceMenu.add(_modeThreshold);
    // sliceMenu.add(_modeArea);

    // also allo the target tracking accuracy to vary

    // TEMPORARILY INTRODUCE SLICE precision
    final MenuManager accuracyMenu = new MenuManager("Target Slice Precision");
    manager.add(accuracyMenu);

    // ok - try to add modes for the slicing algorithm
    _precisionOne = new Action("Low", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        super.run();
        _slicePrecision = Precision.LOW;
        _precisionTwo.setChecked(false);
        _precisionThree.setChecked(false);
        _precisionOne.setChecked(true);
        // _modeTwo.setChecked(false);
      }
    };
    _precisionTwo = new Action("Medium", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        super.run();
        _slicePrecision = Precision.MEDIUM;
        _precisionThree.setChecked(false);
        _precisionOne.setChecked(false);
        _precisionTwo.setChecked(true);
      }
    };
    _precisionThree = new Action("High", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        super.run();
        _slicePrecision = Precision.HIGH;
        _precisionTwo.setChecked(false);
        _precisionOne.setChecked(false);
        _precisionThree.setChecked(true);
      }
    };
    _precisionTwo.setChecked(true);

    accuracyMenu.add(_precisionOne);
    accuracyMenu.add(_precisionTwo);
    accuracyMenu.add(_precisionThree);

    // and the help
    manager.add(new Separator());
    manager.add(CorePlugin.createOpenHelpAction(
        "org.mwc.debrief.help.TrackShifting", null, this));

  }

  protected void fillLocalToolBar(final IToolBarManager toolBarManager)
  {
    // Note: we have undo/redo buttons on the toolbar. Let's not bother with them here, there are
    // lots of cuttons on this toolbar.
    // toolBarManager.add(undoAction);
    // toolBarManager.add(redoAction);
    toolBarManager.add(_autoResize);
    toolBarManager.add(_onlyVisible);
    toolBarManager.add(_showCrossHairs);
    toolBarManager.add(_showLinePlot);
    toolBarManager.add(_showDotPlot);
    toolBarManager.add(_showTargetOverview);
    toolBarManager.add(_showZones);

    addExtras(toolBarManager);

    // and a separator
    toolBarManager.add(new Separator());

    final Vector<Action> actions = DragSegment.getDragModes();
    for (final Iterator<Action> iterator = actions.iterator(); iterator
        .hasNext();)
    {
      final Action action = iterator.next();
      toolBarManager.add(action);
    }
  }

  private void getCutsForThisLeg(final List<SensorContactWrapper> cuts,
      final long wholeStart, final long wholeEnd,
      final List<Long> thisLegTimes, final List<Double> thisLegBearings)
  {
    // get the bearings in this time period
    final Iterator<SensorContactWrapper> lIter = cuts.iterator();
    while (lIter.hasNext())
    {
      final SensorContactWrapper td = lIter.next();
      final long thisTime = td.getDTG().getDate().getTime();
      if (thisTime >= wholeStart)
      {
        if (thisTime <= wholeEnd)
        {
          thisLegTimes.add(thisTime);
          thisLegBearings.add(td.getBearing());
        }
        else
        {
          // ok, we've passed the end
          break;
        }
      }
    }
  }

  private ZoneChart.ZoneListener getOwnshipListener()
  {
    return new ZoneChart.ZoneAdapter();
  }

  public ISharedImages getSharedImages()
  {
    return getViewSite().getWorkbenchWindow().getWorkbench().getSharedImages();
  }

  private ZoneChart.ZoneListener getTargetListener()
  {
    return new ZoneChart.ZoneListener()
    {
      @Override
      public void added(final Zone zone)
      {
        fireUpdates();
      }

      @Override
      public void deleted(final Zone zone)
      {
        // capture the time period
        final TimePeriod zonePeriod =
            new TimePeriod.BaseTimePeriod(new HiResDate(zone.getStart()),
                new HiResDate(zone.getEnd()));

        // ok, delete the relevant leg
        // see if there is already a leg for this time
        final ISecondaryTrack secTrack = _myHelper.getSecondaryTrack();
        Enumeration<Editable> iter = secTrack.segments();
        while (iter.hasMoreElements())
        {
          final Editable nextSeg = iter.nextElement();
          if (nextSeg instanceof SegmentList)
          {
            // oh, this track has already been split into legs. We need to iterate through them
            // instead
            final SegmentList list = (SegmentList) nextSeg;
            iter = list.elements();
            continue;
          }

          // ok, we know we're working through segments
          final TrackSegment cSeg = (TrackSegment) nextSeg;

          final TimePeriod legPeriod =
              new TimePeriod.BaseTimePeriod(cSeg.startDTG(), cSeg.endDTG());

          if (zonePeriod.equals(legPeriod))
          {
            // ok, delete this segment
            final TrackWrapper secTr = (TrackWrapper) secTrack;
            secTr.removeElement(cSeg);
          }
        }

        // now do some updates, to double-check
        fireUpdates();
      }

      private void fireUpdates()
      {
        // collate the current list of legs
        final Zone[] zones = targetZoneChart.getZones();

        final ISecondaryTrack secTrack = _myHelper.getSecondaryTrack();
        final TrackWrapper priTrack = _myHelper.getPrimaryTrack();

        // fire the finished event
        for (int i = 0; i < zones.length; i++)
        {
          final Zone zone = zones[i];
          setLeg(priTrack, secTrack, zone);
        }

        // ok, fire some updates
        if (_ourLayersSubject != null)
        {
          // share the good news
          _ourLayersSubject.fireModified((Layer) _myHelper.getSecondaryTrack());

          // do a fire extended, so the outline re-calculates itself
          _ourLayersSubject.fireExtended(null, (HasEditables) _myHelper
              .getSecondaryTrack());

          // and re-generate the doublets
          updateData(true);
        }

      }

      @Override
      public void moved(final Zone zone)
      {
        fireUpdates();
      }

      @Override
      public void resized(final Zone zone)
      {
        fireUpdates();
      }
    };
  }

  /**
   * collate some zones based on legs in the target track
   * 
   * @return
   */
  protected List<Zone> getTargetZones()
  {
    final List<Zone> zones = new ArrayList<Zone>();
    if (_myTrackDataProvider != null)
    {
      final WatchableList[] secTracks =
          _myTrackDataProvider.getSecondaryTracks();
      if (secTracks != null && secTracks.length == 1)
      {
        final TrackWrapper sw = (TrackWrapper) secTracks[0];
        final Enumeration<Editable> iter = sw.getSegments().elements();
        while (iter.hasMoreElements())
        {
          final TrackSegment thisSeg = (TrackSegment) iter.nextElement();
          if (thisSeg instanceof RelativeTMASegment)
          {
            // do we have a first color?
            final Editable firstElement = thisSeg.elements().nextElement();
            final Color color;
            if (firstElement != null)
            {
              final FixWrapper fix = (FixWrapper) firstElement;
              color = fix.getColor();
            }
            else
            {
              color = Color.RED;
            }

            final RelativeTMASegment rel = (RelativeTMASegment) thisSeg;
            final Zone newZ =
                new Zone(rel.getDTG_Start().getDate().getTime(), rel
                    .getDTG_End().getDate().getTime(), color);
            zones.add(newZ);
          }
          else if (thisSeg instanceof AbsoluteTMASegment)
          {
            final AbsoluteTMASegment seg = (AbsoluteTMASegment) thisSeg;
            if (!thisSeg.isEmpty())
            {
              final FixWrapper firstE =
                  (FixWrapper) thisSeg.elements().nextElement();
              final Color color = firstE.getColor();
              final Zone newZ =
                  new Zone(seg.getDTG_Start().getDate().getTime(), seg
                      .getDTG_End().getDate().getTime(), color);
              zones.add(newZ);
            }
          }
        }
      }
    }
    return zones;
  }

  abstract protected String getType();

  abstract protected String getUnits();

  @Override
  public void init(final IViewSite site, final IMemento memento)
      throws PartInitException
  {
    super.init(site, memento);
    if (memento != null)
    {
      final Boolean showLineVal = memento.getBoolean(SHOW_LINE_PLOT);
      final Boolean showDotVal = memento.getBoolean(SHOW_DOT_PLOT);
      final Boolean showOverview = memento.getBoolean(SHOW_OVERVIEW);
      final Boolean showZones = memento.getBoolean(SHOW_ZONES);
      final Boolean doSelectOnClick = memento.getBoolean(SELECT_ON_CLICK);
      final Boolean showOnlyVis = memento.getBoolean(SHOW_ONLY_VIS);
      final Boolean showCrosshairs = memento.getBoolean(SHOW_CROSSHAIRS);
      if (showLineVal != null)
      {
        _showLinePlot.setChecked(showLineVal);
      }
      if (showDotVal != null)
      {
        _showDotPlot.setChecked(showDotVal);
      }
      if (showZones != null)
      {
        _showZones.setChecked(showZones);
      }
      if (doSelectOnClick != null)
      {
        _selectOnClick.setChecked(doSelectOnClick);
      }
      if (showOnlyVis != null)
      {
        _onlyVisible.setChecked(showOnlyVis);
      }
      if (showOverview != null)
      {
        _showTargetOverview.setChecked(showOverview);
      }
      if (showCrosshairs != null)
      {
        _showCrossHairs.setChecked(showCrosshairs);
      }
    }
  }

  /*
   * Initialize the workbench operation history for our undo context.
   */
  private void initializeOperationHistory()
  {
    undoContext = new ObjectUndoContext(this);
    operationHistory.setLimit(undoContext, 100);
  }

  protected List<Zone> legsFromZigs(final long startTime, final long endTime,
      final List<Zone> zigs, final ColorProvider randomProv)
  {
    final List<Zone> legs = new ArrayList<Zone>();

    Zone lastZig = null;
    for (final Zone zig : zigs)
    {
      // first zig?
      if (legs.size() == 0)
      {
        // ok, run from start time up to this
        legs.add(new Zone(startTime, zig.getStart(), randomProv.getZoneColor()));
      }
      else
      {
        // create a leg from the previous end to this start
        legs.add(new Zone(lastZig.getEnd(), zig.getStart(), randomProv
            .getZoneColor()));
      }

      // remember the zig
      lastZig = zig;
    }

    // and insert a trailing leg
    if (lastZig != null)
    {
      legs.add(new Zone(lastZig.getEnd(), endTime, randomProv.getZoneColor()));
    }
    else
    {
      // ok, no zigs, just one leg
      legs.add(new Zone(startTime, endTime, randomProv.getZoneColor()));
    }

    return legs;
  }

  @Override
  public void logError(final int statusCode, final String string,
      final Exception object)
  {
    // somehow, put the message into the UI
    _myChart.setTitle(string);

    // is it a fail status
    if (statusCode != IStatus.OK)
    {
      // and store the problem into the log
      CorePlugin.logError(statusCode, string, object);

      // also ditch the data in the plots - to blank them out
      // No, don't. We re-use clearPlots() to clear some calculated
      // data in BearingResidualsView
      // clearPlots();
    }
  }

  @Override
  public void logError(final int status, final String text, final Exception e,
      final boolean revealLog)
  {
    logError(status, text, e);
  }

  @Override
  public void logStack(final int status, final String text)
  {
    CorePlugin.logError(status, text, null, true);
  }

  protected void makeActions()
  {
    _autoResize = new Action("Auto resize", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        final boolean val = _autoResize.isChecked();
        if (_showLinePlot.isChecked())
        {
          // ok - redraw the plot we may have changed the axis
          // centreing
          _linePlot.getRangeAxis().setAutoRange(val);
          _linePlot.getDomainAxis().setAutoRange(val);
        }
        if (_showDotPlot.isChecked())
        {
          _dotPlot.getRangeAxis().setAutoRange(val);
          _dotPlot.getDomainAxis().setAutoRange(val);
        }
      }
    };
    _autoResize.setChecked(true);
    _autoResize.setToolTipText("Keep plot sized to show all data");
    _autoResize.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/fit_to_win.png"));

    _showZones = new Action("Show slicing charts", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        if (_showZones.isChecked())
        {
          // show the charts
          setZoneChartsVisible(true);
        }
        else
        {
          setZoneChartsVisible(false);
        }
      }
    };
    _showZones.setChecked(false);
    _showZones.setToolTipText("Show the slicing graphs");
    _showZones.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/GanttBars.png"));

    _showLinePlot = new Action("Actuals plot", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        if (_showLinePlot.isChecked())
        {
          _combined.remove(_linePlot);
          _combined.remove(_dotPlot);

          _combined.add(_linePlot);
          if (_showDotPlot.isChecked())
          {
            _combined.add(_dotPlot);
          }
        }
        else
        {
          if (_combined.getSubplots().size() > 1)
          {
            _combined.remove(_linePlot);
          }
        }
      }
    };
    _showLinePlot.setChecked(true);
    _showLinePlot.setToolTipText("Show the actuals plot");
    _showLinePlot.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/stacked_lines.png"));

    _showDotPlot = new Action("Error plot", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        if (_showDotPlot.isChecked())
        {
          _combined.remove(_linePlot);
          _combined.remove(_dotPlot);

          if (_showLinePlot.isChecked())
          {
            _combined.add(_linePlot);
          }
          _combined.add(_dotPlot);
        }
        else
        {
          if (_combined.getSubplots().size() > 1)
          {
            _combined.remove(_dotPlot);
          }
        }
      }
    };
    _showDotPlot.setChecked(true);
    _showDotPlot.setToolTipText("Show the error plot");
    _showDotPlot.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/stacked_dots.png"));

    _showTargetOverview = new Action("Target Overview", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        if (_showTargetOverview.isChecked())
        {
          _combined.add(_targetOverviewPlot);
        }
        else
        {
          _combined.remove(_targetOverviewPlot);
        }
      }
    };
    _showTargetOverview.setChecked(true);
    _showTargetOverview.setToolTipText("Show the overview plot");
    _showTargetOverview.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/tgt_overview.png"));

    // get an error logger
    final ErrorLogger logger = this;
    _onlyVisible =
        new Action("Only draw dots for visible data points",
            IAction.AS_CHECK_BOX)
        {

          @Override
          public void run()
          {
            super.run();

            // set the title, so there's something useful in there
            _myChart.setTitle("");

            // we need to get a fresh set of data pairs - the number may
            // have
            // changed
            _myHelper.initialise(_myTrackDataProvider, true, _onlyVisible
                .isChecked(), _holder, logger, getType(), _needBrg, _needFreq);

            // and a new plot please
            updateStackedDots(true);
          }
        };
    _onlyVisible.setText("Only plot visible data");
    _onlyVisible.setChecked(true);
    _onlyVisible.setToolTipText("Only draw dots for visible data points");
    _onlyVisible.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/reveal.png"));

    _selectOnClick =
        new Action("Select TMA Fix in outline when clicked",
            IAction.AS_CHECK_BOX)
        {
        };
    _selectOnClick.setChecked(true);
    _selectOnClick
        .setToolTipText("Reveal the respective TMA Fix when an error clicked on plot");
    _selectOnClick.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/outline.png"));

    _showCrossHairs =
        new Action("Show/hide crosshair marker", IAction.AS_CHECK_BOX)
        {

          @Override
          public void run()
          {
            super.run();

            _linePlot.setRangeCrosshairVisible(_showCrossHairs.isChecked());
            _linePlot.setDomainCrosshairVisible(_showCrossHairs.isChecked());

            if (_showCrossHairs.isChecked())
            {
              // ok, show it
              _linePlot.removeAnnotation(crossHairAnnotation);
              _linePlot.addAnnotation(crossHairAnnotation);
            }
            else
            {
              _linePlot.removeAnnotation(crossHairAnnotation);
            }
          }
        };
    _showCrossHairs.setText("Show cross-hair marker");
    _showCrossHairs.setChecked(true);
    _showCrossHairs.setToolTipText("Show/hide cross-hair marker");
    _showCrossHairs.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/fix.png"));

  }

  @Override
  public void saveState(final IMemento memento)
  {
    super.saveState(memento);

    // remember if we're showing the error plot
    memento.putBoolean(SHOW_LINE_PLOT, _showLinePlot.isChecked());
    memento.putBoolean(SHOW_DOT_PLOT, _showDotPlot.isChecked());
    memento.putBoolean(SHOW_OVERVIEW, _showTargetOverview.isChecked());
    memento.putBoolean(SHOW_ZONES, _showZones.isChecked());
    memento.putBoolean(SELECT_ON_CLICK, _selectOnClick.isChecked());
    memento.putBoolean(SHOW_ONLY_VIS, _onlyVisible.isChecked());
    memento.putBoolean(SHOW_CROSSHAIRS, _showCrossHairs.isChecked());

  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
  }

  /**
   * create a leg of data for the specified time period
   * 
   * @param secTrack
   * @param leg
   */
  public void setLeg(final TrackWrapper primaryTrack,
      final ISecondaryTrack secTrack, final Zone leg)
  {
    final TimePeriod zonePeriod =
        new TimePeriod.BaseTimePeriod(new HiResDate(leg.getStart()),
            new HiResDate(leg.getEnd()));

    RelativeTMASegment otherSegment = null;

    // see if there is already a leg for this time
    Enumeration<Editable> iter = secTrack.segments();
    boolean legFound = false;
    while (iter.hasMoreElements())
    {
      final Editable nextSeg = iter.nextElement();
      if (nextSeg instanceof SegmentList)
      {
        // oh, this track has already been split into legs. We need to iterate through them instead
        final SegmentList list = (SegmentList) nextSeg;
        iter = list.elements();
        continue;
      }

      // ok, we know we're working through segments
      // check the time period for this leg
      final TrackSegment cSeg = (TrackSegment) nextSeg;
      final TimePeriod legPeriod =
          new TimePeriod.BaseTimePeriod(cSeg.startDTG(), cSeg.endDTG());

      // remember that segment, it may prove useful to us
      if (cSeg instanceof RelativeTMASegment)
      {
        final RelativeTMASegment seg = (RelativeTMASegment) cSeg;
        otherSegment = seg;
      }

      if (zonePeriod.overlaps(legPeriod))
      {
        // just check the periods don't match - if they match, we don't need to do anything
        if (zonePeriod.equals(legPeriod))
        {
          // ok, we can have a rest
          legFound = true;
          continue;
        }
        else
        {

          if (legFound)
          {
            // ok, we've already create our leg. But, this one overlaps
            // with us. we should delete it, unless it's a Dynamic Infill
            final TrackWrapper secondary = (TrackWrapper) secTrack;
            secondary.removeElement(cSeg);

            CorePlugin.logError(IStatus.INFO,
                "Existing leg overlaps with auto-generated one. deleting:"
                    + cSeg, null);
          }
          else
          {
            if (cSeg instanceof RelativeTMASegment)
            {
              final RelativeTMASegment seg = (RelativeTMASegment) cSeg;
              otherSegment = seg;
              // leg not found yet. this one will do!

              // ok, set this leg to the relevant time period
              seg.setDTG_Start(zonePeriod.getStartDTG());
              seg.setDTG_End(zonePeriod.getEndDTG());

              // also update the points to be this color
              final Enumeration<Editable> fIter = seg.elements();
              while (fIter.hasMoreElements())
              {
                final FixWrapper thisF = (FixWrapper) fIter.nextElement();
                thisF.setColor(leg.getColor());
              }
              legFound = true;

              // tell the leg to share the good news
              // share the good news
              if(_ourLayersSubject != null)
              {
                _ourLayersSubject.fireExtended(seg, (HasEditables) _myHelper
                    .getSecondaryTrack());
              }
            }
            else if (cSeg instanceof AbsoluteTMASegment)
            {
              final AbsoluteTMASegment at = (AbsoluteTMASegment) cSeg;
              at.setDTG_Start(zonePeriod.getStartDTG());
              at.setDTG_End(zonePeriod.getEndDTG());
              //
              // // tell the leg to share the good news
              // // share the good news
              // _ourLayersSubject.fireExtended(at, (HasEditables) _myHelper
              // .getSecondaryTrack());
            }
            else
            {
              CorePlugin.logError(IStatus.WARNING,
                  "Ignoring this leg,  it's not relative TMA:" + cSeg, null);
              continue;
            }

          }
        }
      }
    }

    if (!legFound)
    {
      // ok, we've got to create a new TMA segment

      // get the host cuts for this time period
      final TimePeriod period =
          new TimePeriod.BaseTimePeriod(new HiResDate(leg.getStart()),
              new HiResDate(leg.getEnd()));
      final List<SensorContactWrapper> cuts =
          _myHelper.getBearings(primaryTrack, false, period);
      final SensorContactWrapper[] observations =
          cuts.toArray(new SensorContactWrapper[]
          {});

      final double courseDegs;
      final WorldSpeed speed;
      final WorldVector offset;

      final WorldVector defaultOffset =
          new WorldVector(Math.toDegrees(135), new WorldDistance(2,
              WorldDistance.NM), new WorldDistance(0, WorldDistance.METRES));

      if (otherSegment != null)
      {
        // ok, put this leg off the end of the previous one
        // collate the other data
        courseDegs = otherSegment.getCourse();
        speed = new WorldSpeed(otherSegment.getSpeed());

        // ok, get the last position
        FixWrapper lastFix = null;
        final Enumeration<Editable> sIter = otherSegment.elements();
        while (sIter.hasMoreElements())
        {
          lastFix = (FixWrapper) sIter.nextElement();
        }

        if (lastFix != null)
        {
          // ok, build up the vector
          final long timePeriod =
              leg.getStart() - lastFix.getDTG().getDate().getTime();
          final double distTravelled =
              speed.getValueIn(WorldSpeed.M_sec) * timePeriod / 1000d;
          final WorldVector vector =
              new WorldVector(Math.toRadians(otherSegment.getCourse()),
                  new WorldDistance(distTravelled, WorldDistance.METRES),
                  new WorldDistance(0, WorldDistance.DEGS));

          final WorldLocation legStart = lastFix.getFixLocation().add(vector);

          // work out the offset from the host at this time
          final Watchable[] matches =
              primaryTrack.getNearestTo(new HiResDate(leg.getStart()));

          if (matches != null && matches.length > 0)
          {
            final WorldLocation hostLoc = matches[0].getLocation();
            offset = legStart.subtract(hostLoc);
          }
          else
          {
            CorePlugin
                .logError(
                    IStatus.WARNING,
                    "Couldn't create target leg properly,  couldn't find matching point in ownship leg",
                    null);
            offset = defaultOffset;
          }
        }
        else
        {
          CorePlugin
              .logError(
                  IStatus.WARNING,
                  "Couldn't create target leg properly,  couldn't last fix in existing leg",
                  null);
          offset = defaultOffset;
        }
      }
      else
      {
        courseDegs = 0d;
        speed = new WorldSpeed(5, WorldSpeed.Kts);
        offset = defaultOffset;
      }

      // take the course from the previous leg
      final Layers theLayers = _ourLayersSubject;
      final Color override = leg.getColor();

      // check we've got some observations
      if (observations == null || observations.length == 0)
      {
        // ok, don't bother adding it
        System.err.println("Trying to add empty leg");
      }
      else
      {
        // ok, ready to go
        final RelativeTMASegment newLeg =
            new RelativeTMASegment(observations, offset, speed, courseDegs,
                theLayers, override);

        // ok, now add the leg to the secondary track
        final TrackWrapper secondary = (TrackWrapper) secTrack;
        secondary.add(newLeg);
      }
    }
  }

  /**
   * show/hide the two zone charts
   * 
   * @param isVisible
   */
  private void setZoneChartsVisible(final boolean isVisible)
  {
    // hide the charts
    ownshipZoneChart.setVisible(isVisible);
    targetZoneChart.setVisible(isVisible);

    // and get the parent to redo the layout
    ownshipZoneChart.getParent().layout(true);

    // we should probably update them, true
    updateTargetZones();
  }

  private void showFixAtThisTime(final Date newDate)
  {
    if (_myTrackDataProvider != null)
    {
      if (_myTrackDataProvider.getSecondaryTracks().length != 1)
      {
        return;
      }

      final HiResDate theDate = new HiResDate(newDate);
      EditableWrapper subject = null;

      // ok, get the editor
      final IWorkbench wb = PlatformUI.getWorkbench();
      final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
      final IWorkbenchPage page = win.getActivePage();
      final IEditorPart editor = page.getActiveEditor();

      final Layers layers = (Layers) editor.getAdapter(Layers.class);

      // did we find the layers
      if (layers == null)
      {
        return;
      }

      final TrackWrapper secTrack =
          (TrackWrapper) _myTrackDataProvider.getSecondaryTracks()[0];
      final SegmentList segs = secTrack.getSegments();
      final Enumeration<Editable> sIter = segs.elements();
      while (sIter.hasMoreElements())
      {
        final TrackSegment thisSeg = (TrackSegment) sIter.nextElement();
        if (thisSeg.startDTG().lessThanOrEqualTo(theDate)
            && thisSeg.endDTG().greaterThanOrEqualTo(theDate))
        {
          // ok, loop through them
          final Enumeration<Editable> pts = thisSeg.elements();
          while (pts.hasMoreElements())
          {
            final FixWrapper fix = (FixWrapper) pts.nextElement();
            if (fix.getDTG().equals(theDate))
            {
              // done.
              final EditableWrapper parentP =
                  new EditableWrapper(secTrack, null, layers);
              subject = new EditableWrapper(fix, parentP, null);
              break;
            }
          }
        }
      }

      if (subject != null)
      {
        final IStructuredSelection selection = new StructuredSelection(subject);

        final IContentOutlinePage outline =
            (IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class);
        // did we find an outline?
        if (outline != null)
        {
          // now set the selection
          outline.setSelection(selection);

          // see uf we can expand the selection
          if (outline instanceof PlotOutlinePage)
          {
            final PlotOutlinePage plotOutline = (PlotOutlinePage) outline;
            plotOutline.editableSelected(selection, subject);
          }
        }
      }
    }
  }

  /**
   * slice the target bearings according to these zones
   * 
   * @param ownshipLegs
   * @param randomProv
   * @param slicePrecision
   * @param secondaryTrack
   * @param targetBearingSeries2
   * @return
   */
  protected List<Zone> sliceTarget(final Zone[] ownshipLegs,
      final List<SensorContactWrapper> cuts, final ColorProvider randomProv,
      final ISecondaryTrack tgtTrack, final Precision slicePrecision)
  {
    final ZigDetector slicer = new ZigDetector();
    final List<Zone> zigs = new ArrayList<Zone>();
    final List<Zone> legs = new ArrayList<Zone>();

    // check we have some data
    if (cuts.isEmpty())
    {
      Application.logError2(ToolParent.ERROR, "List of cuts is empty", null);
      return null;
    }

    if (ownshipLegs == null || ownshipLegs.length == 0)
    {
      Application.logError2(ToolParent.ERROR, "List of ownship legs is empty",
          null);
      return null;
    }

    final IZigStorer zigStorer = new IZigStorer()
    {
      @Override
      public void finish()
      {
      }

      @Override
      public void storeZig(final String scenarioName, final long tStart,
          final long tEnd, final double rms)
      {
        final Zone newZone = new Zone(tStart, tEnd, randomProv.getZoneColor());
        // System.out.println("got zig:" + newZone);
        zigs.add(newZone);
      }
    };

    final ILegStorer legStorer = new ILegStorer()
    {
      @Override
      public void storeLeg(final String scenarioName, final long tStart,
          final long tEnd, final double rms)
      {
        final Zone newZone = new Zone(tStart, tEnd, randomProv.getZoneColor());
        legs.add(newZone);
      }
    };

    final double optimiseTolerance = 0.000001;
    final double RMS_ZIG_RATIO;
    switch (slicePrecision)
    {
      case LOW:
        RMS_ZIG_RATIO = 20;
        break;
      case MEDIUM:
      default:
        RMS_ZIG_RATIO = 10d;
        break;
      case HIGH:
        RMS_ZIG_RATIO = 5;
    }

    // get a logger to use
    final ILog log;
    if (TrackShiftActivator.getDefault() == null)
    {
      log = new TrackShiftActivator().getLog();
    }
    else
    {
      log = TrackShiftActivator.getDefault().getLog();
    }

    // ok, loop through the ownship legs
    for (final Zone thisZ : ownshipLegs)
    {
      final long wholeStart = thisZ.getStart();
      final long wholeEnd = thisZ.getEnd();

      // get the bearings in this leg
      final List<Long> thisLegTimes = new ArrayList<Long>();
      final List<Double> thisLegBearings = new ArrayList<Double>();
      getCutsForThisLeg(cuts, wholeStart, wholeEnd, thisLegTimes,
          thisLegBearings);

      // slice the leg
      slicer.sliceThis(log, TrackShiftActivator.PLUGIN_ID, "Some scenario",
          wholeStart, wholeEnd, legStorer, zigStorer, RMS_ZIG_RATIO,
          optimiseTolerance, thisLegTimes, thisLegBearings);
    }

    // special case: if we've manually deleted the target legs, and have to re-create them
    final long startTime;
    final long endTime;
    final Enumeration<Editable> segments = tgtTrack.segments();
    boolean hasData = segments.hasMoreElements();

    // hmm, if it's a normal track, the list may be a list of different data types
    // have a look at the first one
    if (hasData)
    {
      final Editable first = segments.nextElement();
      if (first instanceof SegmentList)
      {
        final SegmentList list = (SegmentList) first;
        hasData = list.size() > 0;
      }
    }

    // if (hasData)
    // {
    // // we can retrieve the start/end time
    // startTime = tgtTrack.getStartDTG().getDate().getTime();
    // endTime = tgtTrack.getEndDTG().getDate().getTime();
    // }
    // else
    // {
    // // we've got to regenerate the points from the cuts
    // startTime = cuts.get(0).getDTG().getDate().getTime();
    // endTime = cuts.get(cuts.size() - 1).getDTG().getDate().getTime();
    // }
    // get the start/end from the ownship legs
    startTime = ownshipLegs[0].getStart();
    endTime = ownshipLegs[ownshipLegs.length - 1].getEnd();

    // ok, we've got to turn the zigs into legs
    final List<Zone> oldLegs =
        legsFromZigs(startTime, endTime, zigs, randomProv);

    // ok, loop through the legs, updating our TMA legs
    for (final Zone leg : oldLegs)
    {
      // ok, see if there is already a leg at this time
      setLeg(_myHelper.getPrimaryTrack(), tgtTrack, leg);
    }

    // ok, fire some updates
    if (_ourLayersSubject != null)
    {
      // share the good news
      _ourLayersSubject.fireModified((Layer) _myHelper.getSecondaryTrack());

      // and re-generate the doublets
      updateData(true);
    }

    // ok, done.
    return oldLegs;
  }

  abstract protected void updateData(boolean updateDoublets);

  /**
   * some data has changed. if we're auto ranging, update the axes
   * 
   */
  protected void updateLinePlotRanges()
  {
    // have a look at the auto resize
    if (_autoResize.isChecked())
    {
      if (_showLinePlot.isChecked())
      {
        _linePlot.getRangeAxis().setAutoRange(false);
        _linePlot.getDomainAxis().setAutoRange(false);
        _linePlot.getRangeAxis().setAutoRange(true);
        _linePlot.getDomainAxis().setAutoRange(true);
      }
    }
  }

  /**
   * the track has been moved, update the dots
   */
  void updateStackedDots(final boolean updateDoublets)
  {

    final Runnable updateEvent = new Runnable()
    {
      @Override
      public void run()
      {
        // update the current datasets
        wrappedUpdateStackedDots(updateDoublets);
      }
    };

    final Runnable doUpdate = new Runnable()
    {
      @Override
      public void run()
      {
        if (Thread.currentThread() == Display.getDefault().getThread())
        {
          // it's ok we're already in a display thread
          updateEvent.run();
        }
        else
        {
          // we're not in the display thread - make it so!
          Display.getDefault().syncExec(updateEvent);
        }
      }
    };

    doUpdate.run();
  }

  /**
   * the layesr manager has told use that the sec track has been extended. So, update the zones.
   */
  private void updateTargetZones()
  {
    if (targetZoneChart != null && targetZoneChart.getVisible())
    {
      // clear the zone charts, but maybe not the primary
      clearZoneCharts(false, true, false);

      // do we have a target zone chart?
      if (targetZoneChart != null)
      {
        // initialise the zones
        final List<Zone> zones = getTargetZones();
        targetZoneChart.setZones(zones);
      }
    }
  }

  /**
   * sort out what we're listening to...
   */
  private final void watchMyParts()
  {

    final ErrorLogger logger = this;

    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final ISelectionProvider prov = (ISelectionProvider) part;

            // am I already listning to this
            if (_selProviders.contains(prov))
            {
              // ignore, we're already listening to it
            }
            else
            {
              prov.addSelectionChangedListener(_mySelListener);
              _selProviders.add(prov);
            }
          }
        });
    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.CLOSED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final ISelectionProvider prov = (ISelectionProvider) part;

            // am I already listning to this
            if (_selProviders.contains(prov))
            {
              // ok, ditch this listener
              _selProviders.remove(prov);

              // and stop listening
              prov.removeSelectionChangedListener(_mySelListener);
            }
            else
            {
              // hey, we're not even listening to it.
            }
          }
        });
    _myPartMonitor.addPartListener(TrackDataProvider.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // cool, remember about it.
            final TrackDataProvider dataP = (TrackDataProvider) part;

            // is this the one we're already listening to?
            if (_myTrackDataProvider != dataP)
            {
              // ok - let's start off with a clean plot
              _dotPlot.setDataset(null);

              // nope, better stop listening then
              if (_myTrackDataProvider != null)
              {
                _myTrackDataProvider.removeTrackShiftListener(_myShiftListener);
                _myTrackDataProvider
                    .removeTrackDataListener(_myTrackDataListener);

                // remove ourselves as a location listener from the sec track, if there is one
                final WatchableList[] secs =
                    _myTrackDataProvider.getSecondaryTracks();
                if (secs != null && secs.length == 1)
                {
                  final TrackWrapper secT = (TrackWrapper) secs[0];
                  secT.removePropertyChangeListener(
                      PlainWrapper.LOCATION_CHANGED, _infillListener);
                }
              }

              // ok, start listening to it anyway
              _myTrackDataProvider = dataP;
              _myTrackDataProvider.addTrackShiftListener(_myShiftListener);
              _myTrackDataProvider.addTrackDataListener(_myTrackDataListener);

              // special case = we have to register to listen to infills changing
              // since they aren't triggered by the UI (mouse drag) events.

              final WatchableList[] secs =
                  _myTrackDataProvider.getSecondaryTracks();
              if (secs != null && secs.length == 1)
              {
                final TrackWrapper secT = (TrackWrapper) secs[0];
                secT.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
                    _infillListener);
              }

              // hey, new plot. clear the zone charts
              clearZoneCharts(true, true, true);

              // set the title, so there's something useful in
              // there
              _myChart.setTitle("");

              // ok - fire off the event for the new tracks
              _myHelper
                  .initialise(_myTrackDataProvider, false, _onlyVisible
                      .isChecked(), _holder, logger, getType(), _needBrg,
                      _needFreq);

              // hey - fire a dot update
              updateStackedDots(true);

              // and the zones
              // initialise the zones
              final List<Zone> zones = getTargetZones();
              if (targetZoneChart != null)
              {
                targetZoneChart.setZones(zones);
              }

            }
          }
        });

    _myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TrackDataProvider tdp = (TrackDataProvider) part;
            tdp.removeTrackShiftListener(_myShiftListener);
            tdp.removeTrackDataListener(_myTrackDataListener);

            if (tdp == _myTrackDataProvider)
            {
              // remove ourselves as a location listener from the sec track, if there is one
              final WatchableList[] secs =
                  _myTrackDataProvider.getSecondaryTracks();
              if (secs != null && secs.length == 1)
              {
                final TrackWrapper secT = (TrackWrapper) secs[0];
                secT.removePropertyChangeListener(
                    PlainWrapper.LOCATION_CHANGED, _infillListener);
              }

              _myTrackDataProvider = null;

              // hey - lets clear our plot
              updateStackedDots(true);

              // and the helper
              _myHelper.reset();

              // and clear the zone charts
              clearZoneCharts(true, true, true);
            }

          }
        });

    _myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final Layers theLayers = (Layers) part;

            // do we need to create our listener
            if (_layersListener == null)
            {
              _layersListener = new Layers.DataListener2()
              {
                @Override
                public void dataExtended(final Layers theData)
                {
                }

                @Override
                public void dataExtended(final Layers theData,
                    final Plottable newItem, final HasEditables parent)
                {
                  // ok, see if this is our secondary track
                  if (parent != null
                      && parent.equals(_myHelper.getSecondaryTrack()))
                  {
                    // also update the zone charts, the secondary
                    // may have been split/merged
                    updateTargetZones();
                  }
                }

                @Override
                public void dataModified(final Layers theData,
                    final Layer changedLayer)
                {
                }

                @Override
                public void dataReformatted(final Layers theData,
                    final Layer changedLayer)
                {
                  _myHelper.initialise(_myTrackDataProvider, false,
                      _onlyVisible.isChecked(), _holder, logger, getType(),
                      _needBrg, _needFreq);

                  updateStackedDots(true);

                }
              };
            }

            // is this what we're listening to?
            if (_ourLayersSubject != theLayers)
            {
              // nope, stop listening to the old one (if there is
              // one!)
              if (_ourLayersSubject != null)
              {
                _ourLayersSubject
                    .removeDataReformattedListener(_layersListener);
                _ourLayersSubject.removeDataExtendedListener(_layersListener);
              }

              // and remember the new one
              _ourLayersSubject = theLayers;
            }

            // now start listening to the new one.
            theLayers.addDataReformattedListener(_layersListener);
            theLayers.addDataExtendedListener(_layersListener);
          }
        });
    _myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final Layers theLayers = (Layers) part;

            // is this what we're listening to?
            if (_ourLayersSubject == theLayers)
            {
              // yup, stop listening
              _ourLayersSubject.removeDataReformattedListener(_layersListener);

              clearPlots();

              // ok, clear the zone charts
              clearZoneCharts(true, true, true);
            }
          }
        });

    // ok we're all ready now. just try and see if the current part is valid
    _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
        .getActivePage());
  }

  /**
   * the track has been moved, update the dots
   */
  void wrappedUpdateStackedDots(final boolean updateDoublets)
  {

    // update the current datasets
    updateData(updateDoublets);

    // note, we also update the domain axis if we're updating the data in
    // question
    if (updateDoublets)
    {
      // trigger recalculation of date axis ticks
      final CachedTickDateAxis date =
          (CachedTickDateAxis) _combined.getDomainAxis();
      date.clearTicks();

      if (_showDotPlot.isChecked())
      {
        _dotPlot.getDomainAxis().setAutoRange(false);
        _dotPlot.getDomainAxis().setAutoRange(true);
        _dotPlot.getDomainAxis().setAutoRange(false);
      }
      if (_showLinePlot.isChecked())
      {
        _linePlot.getDomainAxis().setAutoRange(false);
        _linePlot.getDomainAxis().setAutoRange(true);
        _linePlot.getDomainAxis().setAutoRange(false);
      }
      if (_showTargetOverview.isChecked())
      {
        _targetOverviewPlot.getDomainAxis().setAutoRange(false);
        _targetOverviewPlot.getDomainAxis().setAutoRange(true);
        _targetOverviewPlot.getDomainAxis().setAutoRange(false);
      }
    }

    // right, are we updating the range data?
    if (_autoResize.isChecked())
    {
      if (_showDotPlot.isChecked())
      {
        _dotPlot.getRangeAxis().setAutoRange(false);
        _dotPlot.getRangeAxis().setAutoRange(true);
      }
      if (_showLinePlot.isChecked())
      {
        _linePlot.getRangeAxis().setAutoRange(false);
        _linePlot.getRangeAxis().setAutoRange(true);
      }
      if (_showTargetOverview.isChecked())
      {
        _targetOverviewPlot.getRangeAxis().setAutoRange(false);
        _targetOverviewPlot.getRangeAxis().setAutoRange(true);
      }
    }

  }

}