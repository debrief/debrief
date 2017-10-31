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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import junit.framework.TestCase;

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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
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
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.controls.ZoneUndoRedoProvider;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ITimeVariableProvider;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class BearingResidualsView extends BaseStackedDotsView implements
    ITimeVariableProvider
{

  private class BearingZoneSlicer implements ZoneSlicer
  {
    private final ColorProvider _blueProv;

    Map<Zone, ZoneModes> zoneTracker = new HashMap<Zone, ZoneModes>();

    public BearingZoneSlicer(final ColorProvider blueProv)
    {
      _blueProv = blueProv;
    }

    private void handleThisContact(final Zone zone,
        final SensorContactWrapper contact, final ZoneModes zoneMode)
    {
      if (zone.contains(contact.getDTG().getDate().getTime()))
      {
        if (contact.getHasAmbiguousBearing())
        {
          // ok, switch to the primary one
          contact.setHasAmbiguousBearing(false);
        }
        else
        {
          // ok, let's switch it over
          final double brg = contact.getBearing();
          final double aBrg = contact.getAmbiguousBearing();
          contact.setBearing(aBrg);
          contact.setAmbiguousBearing(brg);

          // have a look at the zone mode
          if (zoneMode != null && zoneMode == ZoneModes.CLEAR)
          {
            contact.setHasAmbiguousBearing(true);
          }
        }
      }
    }

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
            
            // change we check if the ambig is NaN
            hasAmbiguous = !Double.isNaN(contact.getAmbiguousBearing());
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
        final double MIN_BOTH =
            TrackShiftActivator.getDefault().getPreferenceStore().getDouble(
                PreferenceConstants.MIN_TURN_RATE);
        final double MIN_LEG_LENGTH =
            TrackShiftActivator.getDefault().getPreferenceStore().getDouble(
                PreferenceConstants.MIN_LEG_LENGTH);
        final double OS_TURN_MIN_COURSE_CHANGE =
            TrackShiftActivator.getDefault().getPreferenceStore().getDouble(
                PreferenceConstants.OS_TURN_MIN_COURSE_CHANGE);
        final long OS_TURN_MIN_TIME_INTERVAL =
            TrackShiftActivator.getDefault().getPreferenceStore().getLong(
                PreferenceConstants.OS_TURN_MIN_TIME_INTERVAL);
        final AmbiguityResolver resolver = new AmbiguityResolver();
        final Logger logger = getLogger();
        _ambiguousResolverLegsAndCuts =
            resolver.sliceTrackIntoLegsUsingAmbiguity(_myHelper
                .getPrimaryTrack(), MIN_ZIG, MIN_BOTH, MIN_LEG_LENGTH, logger,
                ambigScores, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL);
        zones = new ArrayList<Zone>();
        for (final LegOfCuts leg : _ambiguousResolverLegsAndCuts.getLegs())
        {
          final Zone thisZone =
              new Zone(leg.get(0).getDTG().getDate().getTime(), leg.get(
                  leg.size() - 1).getDTG().getDate().getTime(), _blueProv
                  .getZoneColor());
          zones.add(thisZone);
        }
      }
      else
      {
        zones = StackedDotHelper.sliceOwnship(ownshipCourseSeries, _blueProv);
      }

      return zones;
    }

    @Override
    public void switchAmbiguousCuts(final Zone zone)
    {
      // ok, switch the cuts in this zone
      boolean hasAmbiguous = false;
      final TrackWrapper primary = _myHelper.getPrimaryTrack();
      final Enumeration<Editable> sEnum = primary.getSensors().elements();
      while (sEnum.hasMoreElements() && !hasAmbiguous)
      {
        final SensorWrapper sensor = (SensorWrapper) sEnum.nextElement();

        ZoneModes zoneMode = null;

        if (sensor.getVisible() && sensor.size() > 0)
        {
          final Enumeration<Editable> elements = sensor.elements();
          while (elements.hasMoreElements())
          {
            final SensorContactWrapper contact =
                (SensorContactWrapper) elements.nextElement();

            // ok, are both bearings present?
            hasAmbiguous = !Double.isNaN(contact.getAmbiguousBearing());

            // ok, check if we know what to do next for this zone
            if (zoneMode == null)
            {
              zoneMode = zoneTracker.get(zone);
            }

            if (hasAmbiguous)
            {
              handleThisContact(zone, contact, zoneMode);
            }
          }

          // ok, advance the zone mode
          final ZoneModes newMode;
          if (zoneMode == null)
          {
            newMode = ZoneModes.PRIMARY;
          }
          else
          {
            switch (zoneMode)
            {
              case PRIMARY:
                newMode = ZoneModes.SECONDARY;
                break;
              case SECONDARY:
                newMode = ZoneModes.CLEAR;
                break;
              case CLEAR:
                newMode = ZoneModes.PRIMARY;
                break;
              default:
                newMode = ZoneModes.PRIMARY;
            }
          }
          zoneTracker.put(zone, newMode);
          System.out.println("new mode is:" + newMode);

          // tell the plot to update
          _ourLayersSubject.fireModified(sensor.getHost());

          // and note that the cuts have changed
          updateData(true);
        }
      }
    }
  }

  private static class DeleteCutsOperation extends CMAPOperation
  {

    private static Map<SensorWrapper, LegOfCuts> deleteTheseCuts(
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

    private final Runnable _fireUpdateData;

    private final ZoneChart zoneChart;

    public DeleteCutsOperation(final List<SensorContactWrapper> cutsToDelete,
        final Runnable updateData, final ZoneChart zoneChart)
    {
      super("Delete cuts in O/S Turn");

      _cutsToDelete = cutsToDelete;
      _deletedCuts = new HashMap<SensorWrapper, LegOfCuts>();
      _fireUpdateData = updateData;
      this.zoneChart = zoneChart;
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

      _deletedCuts = deleteTheseCuts(_cutsToDelete);

      // share the good news
      fireModified();

      // and refresh
      _fireUpdateData.run();

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
        final Zone[] zones = zoneChart.getZones();

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
        zoneChart.setZones(zoneList);
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

      // and refresh
      _fireUpdateData.run();

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
      final Runnable toResolve = new Runnable()
      {
        @Override
        public void run()
        {
          _resolved = _resolver.resolve(_legs);
        }
      };
      BusyIndicator.showWhile(Display.getCurrent(), toResolve);

      // and refresh
      updateData(true);

      fireModified();

      final IStatus res;
      if (_resolved != null)
      {

        res =
            new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
                "Resolve legs successful", null);
      }
      else
      {
        res =
            new Status(IStatus.ERROR, TrackShiftActivator.PLUGIN_ID,
                "Tried to slice too many legs", null);

      }

      return res;
    }

    private void fireModified()
    {
      _ourLayersSubject.fireModified(_myHelper.getPrimaryTrack());
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

      fireModified();

      final IStatus res =
          new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
              "Un-resolve legs successful", null);
      return res;
    }

  }

  public static class TestResiduals extends TestCase
  {
    private TrackWrapper getData(final String name)
        throws FileNotFoundException
    {
      // get our sample data-file
      final ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/"
              + name;
      final File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      final FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);

      // sort out the sensors
      importer.storePendingSensors();
      assertEquals("has some layers", 3, theLayers.size());

      // get the sensor track
      final TrackWrapper track = (TrackWrapper) theLayers.findLayer("SENSOR");
      return track;
    }

    public void testDitchUsingAmbiguity() throws FileNotFoundException
    {
      final TrackWrapper track = getData("Ambig_tracks2.rep");
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());

      // make the sensor visible
      final SensorWrapper sensor =
          (SensorWrapper) track.getSensors().elements().nextElement();
      sensor.setVisible(true);

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      // try to get zones using ambiguity delta
      final LegsAndZigs res =
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, null,
              null, null, null);
      final List<LegOfCuts> legs = res.getLegs();
      final LegOfCuts zigs = res.getZigs();

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 12, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 22, zigs.size());

      // ok, ditch those cuts
      final int fullSensorLen = sensor.size();
      Map<SensorWrapper, LegOfCuts> deleted =
          DeleteCutsOperation.deleteTheseCuts(zigs);
      assertEquals("fewer cuts", 99, sensor.size());

      // ok, and undo them
      BearingResidualsView.restoreCuts(deleted);
      assertEquals("fewer cuts", fullSensorLen, sensor.size());

      // and do it again, so we've got fewer cuts
      deleted = DeleteCutsOperation.deleteTheseCuts(zigs);

      final List<ResolvedLeg> resolvedLegs = solver.resolve(legs);
      assertNotNull(resolvedLegs);
      assertEquals("right num legs", 12, legs.size());

      assertEquals("correct leg", 252.85d, resolvedLegs.get(0).leg.get(0)
          .getBearing(), 1d);
      assertEquals("correct leg", 253d, resolvedLegs.get(1).leg.get(0)
          .getBearing(), 1d);
      assertEquals("correct leg", 251d, resolvedLegs.get(2).leg.get(0)
          .getBearing(), 1d);
      assertEquals("correct leg", 254d, resolvedLegs.get(3).leg.get(0)
          .getBearing(), 1d);
      assertEquals("correct leg", 258d, resolvedLegs.get(4).leg.get(0)
          .getBearing(), 1d);
      assertEquals("correct leg", 269d, resolvedLegs.get(5).leg.get(0)
          .getBearing(), 1d);

      // ok, and cancel the leg resolving
      BearingResidualsView.undoResolveBearings(resolvedLegs);

      // and re-check they're ambiguous
      assertEquals("is unresloved", true, resolvedLegs.get(0).leg.get(0)
          .getHasAmbiguousBearing());
    }

    /**
     * note: we're testing this zone chart functionality in here, so we can introduce it into the
     * automated test suite.
     */
    public void testPanCalculation()
    {
      TimePeriod period =
          ZoneChart.calculatePanData(false, 1000, 2000, 1100, 1300);
      assertEquals("corrent new times", 1300, period.getStartDTG().getDate()
          .getTime());
      assertEquals("corrent new times", 1500, period.getEndDTG().getDate()
          .getTime());
      period = ZoneChart.calculatePanData(false, 1000, 2000, 1700, 1900);
      assertEquals("corrent new times", 1800, period.getStartDTG().getDate()
          .getTime());
      assertEquals("corrent new times", 2000, period.getEndDTG().getDate()
          .getTime());
      period = ZoneChart.calculatePanData(true, 1000, 2000, 1700, 1900);
      assertEquals("corrent new times", 1500, period.getStartDTG().getDate()
          .getTime());
      assertEquals("corrent new times", 1700, period.getEndDTG().getDate()
          .getTime());
      period = ZoneChart.calculatePanData(true, 1000, 2000, 1100, 1300);
      assertEquals("corrent new times", 1000, period.getStartDTG().getDate()
          .getTime());
      assertEquals("corrent new times", 1200, period.getEndDTG().getDate()
          .getTime());
    }
  }

  private static enum ZoneModes
  {
    PRIMARY, SECONDARY, CLEAR;
  }

  private static final String SHOW_COURSE = "SHOW_COURSE";

  private static final String SCALE_ERROR = "SCALE_ERROR";

  protected static void deleteCutsInTurnB(final TrackWrapper primaryTrack,
      final Runnable updateData, final ZoneChart ownshipZoneChart,
      final ZoneUndoRedoProvider undoRedoProvider)
  {
    final Zone[] ownshipZones = ownshipZoneChart.getZones();

    if (ownshipZones != null && ownshipZones.length > 0)
    {
      // find the time period of hte chart
      final TimePeriod outerPeriod = ownshipZoneChart.getPeriod();

      // ok, produce the list of cuts to cull
      final LegOfCuts cutsToDelete =
          findCutsNotInZones(ownshipZones, outerPeriod, primaryTrack);

      // delete this list of cuts
      final IUndoableOperation deleteOperation =
          new DeleteCutsOperation(cutsToDelete, updateData, ownshipZoneChart);

      // wrap the operation
      undoRedoProvider.execute(deleteOperation);
    }
    else
    {
      CorePlugin.showMessage("Resolve Ambiguity", "Please slice data first");
    }
  }

  private static LegOfCuts findCutsNotInZones(final Zone[] zones,
      final TimePeriod outerPeriod, final TrackWrapper primaryTrack)
  {
    final LegOfCuts res = new LegOfCuts();
    final Enumeration<Editable> sensors = primaryTrack.getSensors().elements();
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
          if (cut.getVisible() && outerPeriod.contains(cut.getDTG()))
          {
            final long thisT = cut.getDTG().getDate().getTime();

            // ok, see if it;s in one of the zones
            boolean inZone = false;
            for (final Zone zone : zones)
            {
              // does this time occur within this zone?
              if (zone.getStart() <= thisT && zone.getEnd() >= thisT)
              {
                inZone = true;
                break;
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
    return res;
  }

  private static void restoreCuts(
      final Map<SensorWrapper, LegOfCuts> deletedCuts)
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

  private static void undoResolveBearings(final List<ResolvedLeg> legs)
  {
    for (final ResolvedLeg leg : legs)
    {
      for (final SensorContactWrapper cut : leg.leg)
      {
        // cool, we have a course - we can go for it. remember the bearings
        final double bearing1 = cut.getBearing();
        final double bearing2 = cut.getAmbiguousBearing();

        switch (leg.bearing)
        {
          case CORE:
            cut.setBearing(bearing2);
            cut.setAmbiguousBearing(bearing1);
            break;
          case AMBIGUOUS:
            cut.setBearing(bearing1);
            cut.setAmbiguousBearing(bearing2);
            break;
          default:
            break;
        }

        // remember we're morally ambiguous
        cut.setHasAmbiguousBearing(true);
      }
    }
  }

  private Action showCourse;

  private Action relativeAxes;

  private Action scaleError;

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
  protected Runnable getDeleteCutsOperation()
  {
    return new Runnable()
    {
      @Override
      public void run()
      {
        final Runnable wrappedDelete = new Runnable()
        {

          @Override
          public void run()
          {
            final Runnable updateData = new Runnable()
            {
              @Override
              public void run()
              {
                updateData(true);
              }
            };

            deleteCutsInTurnB(_myHelper.getPrimaryTrack(), updateData,
                ownshipZoneChart, undoRedoProvider);
          }
        };
        BusyIndicator.showWhile(Display.getCurrent(), wrappedDelete);
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
    return new BearingZoneSlicer(blueProv);
  }

  @Override
  protected Runnable getResolveAmbiguityOperation()
  {
    return new Runnable()
    {

      @Override
      public void run()
      {
        resolveAmbiguousCutsB();
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
    
    // also update the renderer for the zone charts
    ownshipZoneChart.setBearingRange(minVal, maxVal);
    targetZoneChart.setBearingRange(minVal, maxVal);
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

    final Zone[] zones = ownshipZoneChart.getZones();
    if (zones == null || zones.length == 0)
    {
      CorePlugin.showMessage("Resolve ambiguity",
          "Please slice ownship legs before resolving ambiguity");
    }
    else
    {
      final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
      for (final Zone zone : zones)
      {
        final TimePeriod period =
            new TimePeriod.BaseTimePeriod(new HiResDate(zone.getStart()),
                new HiResDate(zone.getEnd()));
        final List<SensorContactWrapper> cuts =
            _myHelper.getBearings(_myHelper.getPrimaryTrack(), true, period);
        final LegOfCuts leg = new LegOfCuts();
        leg.addAll(cuts);

        legs.add(leg);
      }

      final AmbiguityResolver resolver = new AmbiguityResolver();
      final IUndoableOperation resolveCuts =
          new ResolveCutsOperationAmbig(resolver, legs);
      undoRedoProvider.execute(resolveCuts);

      // and refresh
      updateData(true);
    }
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
