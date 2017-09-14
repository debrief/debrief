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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.LegsAndZigs;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.ResolvedLeg;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts;
import org.mwc.debrief.track_shift.ambiguity.preferences.PreferenceConstants;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ITimeVariableProvider;
import MWC.GUI.Editable;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class BearingResidualsView extends BaseStackedDotsView implements
    ITimeVariableProvider
{

  private class DeleteCutsOperation extends CMAPOperation
  {

    /**
     * the cuts to be deleted
     * 
     */
    final private List<SensorContactWrapper> _cutsToDelete;

    /**
     * cuts that have been deleted (with the sensor that they were removed from)
     * 
     */
    private Map<SensorWrapper, LegOfCuts> _deletedCuts;

    public DeleteCutsOperation(final List<SensorContactWrapper> cutsToDelete)
    {
      super("Delete cuts in O/S Turn");

      _cutsToDelete = cutsToDelete;
      _deletedCuts = new HashMap<SensorWrapper, LegOfCuts>();
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      if (_deletedCuts != null)
      {
        _deletedCuts.clear();
        _deletedCuts = null;
      }

      _deletedCuts = BearingResidualsView.deleteTheseCuts(_cutsToDelete);

      // share the good news
      fireModified();

      // and refresh
      updateData(true);

      // and the ownship zone chart

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Delete cuts in O/S turn successful", null);
      return res;
    }

    private void fireModified()
    {

      final SensorWrapper sensor;
      if (_deletedCuts != null && !_deletedCuts.isEmpty())
      {
        sensor = _deletedCuts.keySet().iterator().next();
      }
      else if (_cutsToDelete != null && !_cutsToDelete.isEmpty())
      {
        sensor = _cutsToDelete.get(0).getSensor();
      }
      else
      {
        sensor = null;
      }

      // fire modified event
      if (sensor != null)
      {
        // remember the zones
        final Zone[] zones = ownshipZoneChart.getZones();

        // fire the update. Note: doing this will remove
        // the displayed zones, since the chart will think
        // the track has changed.
        sensor.getHost().firePropertyChange(SupportsPropertyListeners.EXTENDED,
            null, System.currentTimeMillis());

        final List<Zone> zoneList = new ArrayList<Zone>();
        for (final Zone z : zones)
        {
          zoneList.add(z);
        }
        ownshipZoneChart.setZones(zoneList);
      }
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      BearingResidualsView.restoreCuts(_deletedCuts);

      _deletedCuts.clear();
      _deletedCuts = null;

      // share the good news
      fireModified();

      // and refresh the UI
      updateData(true);

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Restore cuts in O/S turn successful", null);
      return res;
    }

  }

  private class ResolveCutsOperationAmbig extends CMAPOperation
  {

    /**
     * the cuts to be deleted
     * 
     */
    final private AmbiguityResolver _resolver;
    final private List<LegOfCuts> _legs;
    private List<ResolvedLeg> _resolved;

    public ResolveCutsOperationAmbig(final AmbiguityResolver resolver,
        final List<LegOfCuts> legs)
    {
      super("Resolve ambiguous cuts");
      _resolver = resolver;
      _legs = legs;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      _resolved = _resolver.resolve(_legs);

      // and refresh
      updateData(true);

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Resolve legs successful", null);
      return res;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      undoResolveBearings(_resolved);

      // and clear the resolved list
      _resolved.clear();
      _resolved = null;

      // and refresh the UI
      updateData(true);

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Un-resolve legs successful", null);
      return res;
    }

  }

  private static final String SHOW_COURSE = "SHOW_COURSE";

  private static final String SCALE_ERROR = "SCALE_ERROR";

  public static Map<SensorWrapper, LegOfCuts> deleteTheseCuts(
      final List<SensorContactWrapper> cutsToDelete)
  {
    final Map<SensorWrapper, LegOfCuts> deletedCuts =
        new HashMap<SensorWrapper, LegOfCuts>();

    for (final SensorContactWrapper t : cutsToDelete)
    {
      // store the details of this sensor, so we can undo it
      LegOfCuts list = deletedCuts.get(t.getSensor());

      if (list == null)
      {
        list = new LegOfCuts();
        deletedCuts.put(t.getSensor(), list);
      }

      list.add(t);

      t.getSensor().removeElement(t);
    }
    return deletedCuts;
  }

  public static void
      restoreCuts(final Map<SensorWrapper, LegOfCuts> deletedCuts)
  {
    for (final SensorWrapper sensor : deletedCuts.keySet())
    {
      final ArrayList<SensorContactWrapper> cuts = deletedCuts.get(sensor);
      for (final SensorContactWrapper cut : cuts)
      {
        sensor.add(cut);
      }
    }
  }

  public static void undoResolveBearings(final List<ResolvedLeg> legs)
  {
    for (final ResolvedLeg leg : legs)
    {
      for (final SensorContactWrapper cut : leg.leg)
      {
        // cool, we have a course - we can go for it. remember the bearings
        final double bearing1 = cut.getBearing();
        final double bearing2 = cut.getAmbiguousBearing();

        if (leg.keepFirst)
        {
          cut.setBearing(bearing2);
          cut.setAmbiguousBearing(bearing1);
        }
        else
        {
          cut.setBearing(bearing1);
          cut.setAmbiguousBearing(bearing2);
        }

        // remember we're morally ambiguous
        cut.setHasAmbiguousBearing(true);
      }
    }
  }

  private Action showCourse;
  private Action relativeAxes;

  private Action scaleError;

  private Action _doStepADelete;

  private Action _doStepAResolve;
  private Action _doStepBDelete;

  // protected Action _5degResize;

  private Action _doStepBResolve;

  private LegsAndZigs _ambiguousResolverLegsAndCuts;

  public BearingResidualsView()
  {
    super(true, false);
  }

  @Override
  protected void addExtras(final IToolBarManager toolBarManager)
  {
    super.addExtras(toolBarManager);
    toolBarManager.add(showCourse);

    final boolean showControls =
        TrackShiftActivator.getDefault().getPreferenceStore().getBoolean(
            PreferenceConstants.DISPLAY);

    if (showControls)
    {
      toolBarManager.add(_doStepADelete);
      toolBarManager.add(_doStepAResolve);
      toolBarManager.add(_doStepBDelete);
      toolBarManager.add(_doStepBResolve);
    }
  }

  @Override
  public boolean applyStyling()
  {
    return scaleError.isChecked();
  }

  @Override
  protected void clearPlots()
  {
    super.clearPlots();

    // also, forget the stored set of ambiguous data
    if (_ambiguousResolverLegsAndCuts != null)
    {
      _ambiguousResolverLegsAndCuts.getLegs().clear();
      _ambiguousResolverLegsAndCuts.getZigs().clear();
      _ambiguousResolverLegsAndCuts = null;
    }
  }

  protected void deleteCutsInTurnB()
  {
    final Zone[] ownshipZones = ownshipZoneChart.getZones();

    if (ownshipZones != null && ownshipZones.length > 0)
    {
      // ok, produce the list of cuts to cull
      final long startTime = ownshipZones[0].getStart();
      final long endTime = ownshipZones[ownshipZones.length - 1].getEnd();
      final TimePeriod outerPeriod =
          new TimePeriod.BaseTimePeriod(new HiResDate(startTime),
              new HiResDate(endTime));
      final LegOfCuts cutsToDelete =
          findCutsNotInZones(ownshipZones, outerPeriod);

      // delete this list of cuts
      final IUndoableOperation deleteOperation =
          new DeleteCutsOperation(cutsToDelete);

      // wrap the operation
      undoRedoProvider.execute(deleteOperation);
    }
    else
    {
      CorePlugin.showMessage("Resolve Ambiguity",
          "Please delete cuts in turn first");
    }

  }

  @Override
  protected void fillLocalPullDown(final IMenuManager manager)
  {
    manager.add(relativeAxes);
    manager.add(scaleError);
    super.fillLocalPullDown(manager);
  }

  @Override
  protected void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(relativeAxes);
    manager.add(scaleError);
    super.fillLocalToolBar(manager);

  }

  private LegOfCuts findCutsNotInZones(final Zone[] zones,
      final TimePeriod outerPeriod)
  {
    final LegOfCuts res = new LegOfCuts();
    final Enumeration<Editable> sensors =
        _myHelper.getPrimaryTrack().getSensors().elements();
    while (sensors.hasMoreElements())
    {
      final SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
      if (sensor.getVisible())
      {
        final Enumeration<Editable> cuts = sensor.elements();
        while (cuts.hasMoreElements())
        {
          final SensorContactWrapper cut =
              (SensorContactWrapper) cuts.nextElement();
          if (cut.getVisible())
          {
            // is it in our time period
            if (outerPeriod.contains(cut.getDTG()))
            {
              final long thisT = cut.getDTG().getDate().getTime();

              // ok, see if it;s in one of the zones
              boolean inZone = false;
              for (final Zone zone : zones)
              {
                if (zone.getStart() <= thisT && zone.getEnd() >= thisT)
                {
                  inZone = true;
                }
              }

              // was it in a zone?
              if (!inZone)
              {
                // ok, have to delete it
                res.add(cut);
              }
            }
          }
        }
      }
    }
    return res;
  }

  @Override
  protected Runnable getDeleteCutsOperation()
  {
    return new Runnable()
    {

      @Override
      public void run()
      {
        deleteCutsInTurnB();
      }
    };
  }

  private Logger getLogger()
  {
    final Logger logger;
    final boolean doLogging =
        TrackShiftActivator.getDefault().getPreferenceStore().getBoolean(
            PreferenceConstants.DIAGNOSTICS);
    if (doLogging)
    {
      logger = Logger.getLogger("Residuals.Logger", null);
      logger.setLevel(Level.INFO);
      logger.setUseParentHandlers(false);
      final Handler handler = new ConsoleHandler()
      {
        @Override
        public void publish(final LogRecord record)
        {
          // and to the system log
          TrackShiftActivator.getDefault().getLog().log(
              new Status(IStatus.INFO, TrackShiftActivator.PLUGIN_ID, record
                  .getMessage()));
        }
      };
      logger.addHandler(handler);
    }
    else
    {
      logger = null;
    }
    return logger;
  }

  @Override
  protected ZoneSlicer getOwnshipZoneSlicer(final ColorProvider blueProv)
  {
    return new ZoneSlicer()
    {
      @Override
      public ArrayList<Zone> performSlicing()
      {
        // hmm, see if we have ambiguous data
        final TrackWrapper primary = _myHelper.getPrimaryTrack();
        boolean hasAmbiguous = false;
        final Enumeration<Editable> sEnum = primary.getSensors().elements();
        while (sEnum.hasMoreElements() && !hasAmbiguous)
        {
          final SensorWrapper sensor = (SensorWrapper) sEnum.nextElement();
          if (sensor.size() > 0)
          {
            final Enumeration<Editable> elements = sensor.elements();
            while (elements.hasMoreElements() && !hasAmbiguous)
            {
              final SensorContactWrapper contact =
                  (SensorContactWrapper) elements.nextElement();
              hasAmbiguous = contact.getHasAmbiguousBearing();
            }
          }
        }

        final ArrayList<Zone> zones;
        if (hasAmbiguous)
        {
          // ok, we'll use our fancy slicer that relies on ambiguity
          final double MIN_ZIG =
              TrackShiftActivator.getDefault().getPreferenceStore().getDouble(
                  PreferenceConstants.MIN_ZIG);
          final double MAX_STEADY =
              TrackShiftActivator.getDefault().getPreferenceStore().getDouble(
                  PreferenceConstants.MAX_STEADY);
          final AmbiguityResolver resolver = new AmbiguityResolver();
          final Logger logger = getLogger();
          _ambiguousResolverLegsAndCuts =
              resolver.sliceIntoLegsUsingAmbiguity(_myHelper.getPrimaryTrack(),
                  MIN_ZIG, MAX_STEADY, logger, ambigScores);
          zones = new ArrayList<Zone>();
          for (final LegOfCuts leg : _ambiguousResolverLegsAndCuts.getLegs())
          {
            final Zone thisZone =
                new Zone(leg.get(0).getDTG().getDate().getTime(), leg.get(
                    leg.size() - 1).getDTG().getDate().getTime(), blueProv
                    .getZoneColor());
            zones.add(thisZone);
          }
        }
        else
        {
          zones = StackedDotHelper.sliceOwnship(ownshipCourseSeries, blueProv);
        }

        return zones;
      }
    };

  }

  @Override
  protected String getType()
  {
    return "Bearing";
  }

  @Override
  protected String getUnits()
  {
    return "\u00b0";
  }

  @Override
  public long getValueAt(final HiResDate dtg)
  {
    // get the time
    final RegularTimePeriod myTime =
        new FixedMillisecond(dtg.getMicros() / 1000);

    // get the set of calculated error values that we have stored in the graph
    final TimeSeriesCollection dataset =
        (TimeSeriesCollection) _dotPlot.getDataset();

    if (dataset != null)
    {
      if (dataset.getSeriesCount() > 0)
      {
        final TimeSeries series = dataset.getSeries(0);
        final int index = series.getIndex(myTime);
        if (index >= 0)
        {
          final TimeSeriesDataItem thisV = series.getDataItem(index);
          return thisV.getValue().longValue();
        }
      }
    }
    return 0;
  }

  @Override
  public void init(final IViewSite site, final IMemento memento)
      throws PartInitException
  {
    super.init(site, memento);

    if (memento != null)
    {

      final Boolean doCourse = memento.getBoolean(SHOW_COURSE);
      if (doCourse != null)
      {
        showCourse.setChecked(doCourse.booleanValue());
      }

      final Boolean doScaleError = memento.getBoolean(SCALE_ERROR);
      if (doScaleError != null)
      {
        scaleError.setChecked(doScaleError.booleanValue());
      }
    }
  }

  @Override
  protected void makeActions()
  {
    super.makeActions();

    // now the course action
    scaleError =
        new Action("Show the error when dragging", IAction.AS_CHECK_BOX)
        {
          @Override
          public void run()
          {
            super.run();
          }
        };
    scaleError.setChecked(false);
    scaleError
        .setToolTipText("Show symbol scaled to per-cut error when dragging");
    scaleError.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/scale.png"));

    // see if there's an existing setting for this.

    // now the course action
    relativeAxes =
        new Action("Use +/- 180 scale for absolute data", IAction.AS_CHECK_BOX)
        {
          @Override
          public void run()
          {
            super.run();

            processRelativeAxes();
          }
        };
    relativeAxes.setChecked(false);
    relativeAxes.setToolTipText("Use +/- 180 scale for absolute data");
    relativeAxes.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/swap_axis.png"));

    // now the course action
    showCourse = new Action("Show ownship course", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();

        processShowCourse();
      }
    };
    showCourse.setChecked(true);
    showCourse.setToolTipText("Show ownship course in overview chart");
    showCourse.setImageDescriptor(TrackShiftActivator
        .getImageDescriptor("icons/24/ShowCourse.png"));

    _autoResize = new Action("Auto resize", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        processAutoResize();
      }
    };
    _autoResize.setChecked(true);
    _autoResize.setToolTipText("Keep plot sized to show all data");
    _autoResize.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/fit_to_win.png"));
  }

  private void processAutoResize()
  {
    final boolean val = _autoResize.isChecked();
    if (_showDotPlot.isChecked())
    {
      if (val)
      {
        _dotPlot.getRangeAxis().setAutoRange(true);
        _autoResize.setToolTipText("Keep plot sized to show all data");
      }
      else
      {
        _dotPlot.getRangeAxis().setRange(-5, 5);
        _dotPlot.getRangeAxis().setAutoRange(false);
        _autoResize.setToolTipText("Fix bearing range to +/- 5 degs");
      }
    }
  }

  private void processRelativeAxes()
  {
    final double minVal;
    final double maxVal;
    if (relativeAxes.isChecked())
    {
      minVal = -180d;
      maxVal = 180d;
    }
    else
    {
      minVal = 0d;
      maxVal = 360d;
    }

    _overviewCourseRenderer.setRange(minVal, maxVal);

    processShowCourse();
  }

  private void processShowCourse()
  {
    // ok - redraw the plot we may have changed the course visibility
    updateStackedDots(false);

    // ok - if we're on auto update, do the update
    updateLinePlotRanges();
  }

  protected void resolveAmbiguousCutsB()
  {

    if (_ambiguousResolverLegsAndCuts != null)
    {
      final AmbiguityResolver resolver = new AmbiguityResolver();
      final IUndoableOperation resolveCuts =
          new ResolveCutsOperationAmbig(resolver, _ambiguousResolverLegsAndCuts
              .getLegs());
      undoRedoProvider.execute(resolveCuts);
    }
    else
    {
      CorePlugin.showMessage("Resolve Ambiguity",
          "Please delete cuts in turn first");
    }

    // and refresh
    updateData(true);

  }

  @Override
  public void saveState(final IMemento memento)
  {
    super.saveState(memento);

    memento.putBoolean(SHOW_COURSE, showCourse.isChecked());
    memento.putBoolean(SCALE_ERROR, scaleError.isChecked());
  }

  @Override
  protected void updateData(final boolean updateDoublets)
  {
    // update the current datasets
    _myHelper.updateBearingData(_dotPlot, _linePlot, _targetOverviewPlot,
        _myTrackDataProvider, _onlyVisible.isChecked(), showCourse.isChecked(),
        relativeAxes.isChecked(), _holder, this, updateDoublets,
        _targetCourseSeries, _targetSpeedSeries, measuredValues, ambigValues,
        ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries,
        _overviewSpeedRenderer, _overviewCourseRenderer);
  }
}
