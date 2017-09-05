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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.ResolvedLeg;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ITimeVariableProvider;
import MWC.GenericData.HiResDate;

public class BearingResidualsView extends BaseStackedDotsView implements
    ITimeVariableProvider
{

  private class DeleteCutsOperation extends CMAPOperation
  {

    /** the cuts to be deleted
     * 
     */
    final private List<SensorContactWrapper> _cutsToDelete;
    
    /** cuts that have been deleted (with the sensor that they were removed from)
     * 
     */
    final private Map<SensorWrapper, ArrayList<SensorContactWrapper>> _deletedCuts;

    public DeleteCutsOperation(final List<SensorContactWrapper> cutsToDelete)
    {
      super("Delete cuts in O/S Turn");

      _cutsToDelete = cutsToDelete;
      _deletedCuts = new HashMap<SensorWrapper, ArrayList<SensorContactWrapper>>(); 
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      _deletedCuts.clear();

      for (final SensorContactWrapper t : _cutsToDelete)
      {
        // store the details of this sensor, so we can undo it
        ArrayList<SensorContactWrapper> list = _deletedCuts.get(t.getSensor());

        if (list == null)
        {
          list = new ArrayList<SensorContactWrapper>();
          _deletedCuts.put(t.getSensor(), list);
        }

        list.add(t);

        t.getSensor().removeElement(t);
      }

      // and refresh
      updateData(true);

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Delete cuts in O/S turn successful", null);
      return res;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      for (final SensorWrapper sensor : _deletedCuts.keySet())
      {
        final ArrayList<SensorContactWrapper> cuts = _deletedCuts.get(sensor);
        for (final SensorContactWrapper cut : cuts)
        {
          sensor.add(cut);
        }
      }

      // and refresh the UI
      updateData(true);

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Restore cuts in O/S turn successful", null);
      return res;
    }

  }

  private class ResolveCutsOperation extends CMAPOperation
  {
  
    /** the cuts to be deleted
     * 
     */
    private List<ResolvedLeg> _cutsToResolve;    
    final private AmbiguityResolver _resolver;
    private TrackWrapper _primary;
    private Zone[] _zones;
  
  
    public ResolveCutsOperation(AmbiguityResolver resolver,
        TrackWrapper primaryTrack, Zone[] zones)
    {
      super("Resolve ambiguous cuts");
      _resolver = resolver;
      _primary = primaryTrack;
      _zones = zones;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      _cutsToResolve = _resolver.resolve(_primary, _zones);
      
      // and refresh
      updateData(true);
  
      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Delete cuts in O/S turn successful", null);
      return res;
    }
  
    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      _resolver.undoDitchBearings(_cutsToResolve);
  
      // and refresh the UI
      updateData(true);
  
      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Restore cuts in O/S turn successful", null);
      return res;
    }
  
  }

  private static final String SHOW_COURSE = "SHOW_COURSE";
  private static final String SCALE_ERROR = "SCALE_ERROR";
  private Action showCourse;
  private Action relativeAxes;

  private Action scaleError;
  private Action _doStep1;

  private Action _doStep2;

  private Action _doStep3;

  // protected Action _5degResize;

  public BearingResidualsView()
  {
    super(true, false);
  }

  @Override
  protected void addExtras(final IToolBarManager toolBarManager)
  {
    super.addExtras(toolBarManager);
    toolBarManager.add(showCourse);
    toolBarManager.add(_doStep1);
    toolBarManager.add(_doStep2);
    toolBarManager.add(_doStep3);
  }

  @Override
  public boolean applyStyling()
  {
    return scaleError.isChecked();
  }

  protected void deleteCutsInTurn()
  {
    // create the resolver
    final AmbiguityResolver resolver = new AmbiguityResolver();
    final Zone[] zones = ownshipZoneChart.getZones();

    final List<SensorContactWrapper> cutsToDelete =
        resolver.findCutsToDropInTurn(super._myHelper.getPrimaryTrack(), zones,
            null);

    final IUndoableOperation deleteOperation =
        new DeleteCutsOperation(cutsToDelete);

    // wrap the operation
    undoRedoProvider.execute(deleteOperation);
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

    // now the course action
    _doStep1 = new Action("A1", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        super.run();
        deleteCutsInTurn();
      }
    };

    // now the course action
    _doStep2 = new Action("A2", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        super.run();
        resolveAmbiguousCuts();
      }
    };

    // now the course action
    _doStep3 = new Action("B", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        super.run();
        newResolveAmbiguity();
      }
    };
  }

  protected void newResolveAmbiguity()
  {
    final AmbiguityResolver resolver = new AmbiguityResolver();

    TrackWrapper primaryTrack = super._myHelper.getPrimaryTrack();
    List<LegOfCuts> zones = resolver.sliceIntoLegsUsingAmbiguity(primaryTrack);
    resolver.resolve(zones);
    
//    IUndoableOperation resolveCuts = new ResolveCutsOperation(resolver, primaryTrack, zones);
//    
//    undoRedoProvider.execute(resolveCuts);
    
    // and refresh
    updateData(true);

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
  
  protected void resolveAmbiguousCuts()
  {
    // create the resolver
    final AmbiguityResolver resolver = new AmbiguityResolver();
    final Zone[] zones = ownshipZoneChart.getZones();

    IUndoableOperation resolveCuts = new ResolveCutsOperation(resolver, super._myHelper.getPrimaryTrack(), zones);
    
    undoRedoProvider.execute(resolveCuts);
    
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
        _targetCourseSeries, _targetSpeedSeries, ownshipCourseSeries,
        targetBearingSeries, targetCalculatedSeries, _overviewSpeedRenderer,
        _overviewCourseRenderer);
  }
}
