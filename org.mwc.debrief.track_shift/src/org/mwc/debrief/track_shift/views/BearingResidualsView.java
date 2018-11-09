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

import java.awt.Paint;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.axis.NumberAxis;
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
import org.mwc.debrief.track_shift.views.StackedDotHelper.SetBackgroundShade;

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
import junit.framework.TestCase;

public class BearingResidualsView extends BaseStackedDotsView implements
    ITimeVariableProvider
{

  private static class BearingFormatter extends DecimalFormat
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BearingFormatter(final String baseFormat)
    {
      super(baseFormat);
    }

    @Override
    public StringBuffer format(final double number,
        final StringBuffer toAppendTo, final FieldPosition pos)
    {
      final double adjusted = number < 0 ? number + 360 : number;
      return super.format(adjusted, toAppendTo, pos);
    }

    @Override
    public StringBuffer format(long number, final StringBuffer toAppendTo,
        final FieldPosition pos)
    {
      final long adjusted = number < 0 ? number + 360 : number;
      return super.format(adjusted, toAppendTo, pos);
    }

    @Override
    public Number parse(final String source, final ParsePosition parsePosition)
    {
      throw new IllegalArgumentException("Parse not implemented");
    }

  }

  private class OwnshipZoneSlicer implements ZoneSlicer
  {
    private final ColorProvider _blueProv;

    Map<Zone, ZoneModes> zoneTracker = new HashMap<Zone, ZoneModes>();

    public OwnshipZoneSlicer(final ColorProvider blueProv)
    {
      _blueProv = blueProv;
    }

    @Override
    public boolean ambigDataPresent()
    {
      // hmm, see if we have ambiguous data
      final TrackWrapper primary = _myHelper.getPrimaryTrack();
      return hasAmbiguousCuts(primary);
    }

    private TimePeriod getAnalysisPeriod(final boolean wholePeriod,
        final TimePeriod trackPeriod, final List<Zone> existingZones)
    {
      final TimePeriod interPeriod;
      if (!wholePeriod && existingZones != null && !existingZones.isEmpty())
      {
        // ok, we've got some zones. Let's start with the last zone
        final Zone lastZone = existingZones.get(existingZones.size() - 1);
        final HiResDate lastZoneStart = new HiResDate(lastZone.getStart());
        final HiResDate lastZoneEnd = new HiResDate(lastZone.getEnd());
        final TimePeriod sensorDataCoverage = timePeriodFor(_myHelper
            .getPrimaryTrack());

        // have we reached the end of the data?
        if (lastZoneEnd.equals(sensorDataCoverage.getEndDTG()))
        {
          // ok, we're at the end. Just use the whole period
          interPeriod = sensorDataCoverage.intersects(trackPeriod);
        }
        else
        {
          // we're not at the end - let the algorithm run to the end
          final TimePeriod visiblePeriod = new TimePeriod.BaseTimePeriod(
              lastZoneStart, sensorDataCoverage.getEndDTG());
          interPeriod = visiblePeriod.intersects(trackPeriod);
        }
      }
      else
      {
        // we don't have any zones
        final TimePeriod visiblePeriod = ownshipZoneChart.getVisiblePeriod();
        interPeriod = visiblePeriod.intersects(trackPeriod);
      }
      return interPeriod;
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

    private boolean hasAmbiguousCuts(final TrackWrapper primary)
    {
      boolean hasAmbiguous = false;
      if (primary != null)
      {
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
      }
      return hasAmbiguous;
    }

    @Override
    public ArrayList<Zone> performSlicing(final boolean wholePeriod)
    {
      // hmm, see if we have ambiguous data
      final TrackWrapper primary = _myHelper.getPrimaryTrack();
      final boolean hasAmbiguous = hasAmbiguousCuts(primary);

      final ArrayList<Zone> slicedZones;
      if (hasAmbiguous)
      {
        // ok, we'll use our fancy slicer that relies on ambiguity
        final double MIN_ZIG = TrackShiftActivator.getDefault()
            .getPreferenceStore().getDouble(PreferenceConstants.MIN_ZIG);
        final double MIN_BOTH = TrackShiftActivator.getDefault()
            .getPreferenceStore().getDouble(PreferenceConstants.MIN_TURN_RATE);
        final double MIN_LEG_LENGTH = TrackShiftActivator.getDefault()
            .getPreferenceStore().getDouble(PreferenceConstants.MIN_LEG_LENGTH);
        final double OS_TURN_MIN_COURSE_CHANGE = TrackShiftActivator
            .getDefault().getPreferenceStore().getDouble(
                PreferenceConstants.OS_TURN_MIN_COURSE_CHANGE);
        final long OS_TURN_MIN_TIME_INTERVAL = TrackShiftActivator.getDefault()
            .getPreferenceStore().getLong(
                PreferenceConstants.OS_TURN_MIN_TIME_INTERVAL);
        final Integer MAX_LEGS;
        if (wholePeriod)
        {
          MAX_LEGS = null;
        }
        else
        {
          MAX_LEGS = TrackShiftActivator.getDefault().getPreferenceStore()
              .getInt(PreferenceConstants.OS_TURN_MAX_LEGS);
        }
        final AmbiguityResolver resolver = new AmbiguityResolver();
        final Logger logger = getLogger();

        // sort out what time period we're using
        final TimePeriod trackPeriod = _myHelper.getPrimaryTrack()
            .getVisiblePeriod();

        // ok, we may be walking along. see if we have some zones assigned
        final TimePeriod analysisPeriod = getAnalysisPeriod(wholePeriod,
            trackPeriod, ownshipZoneChart.getZones());

        final LegsAndZigs legsAndCuts = resolver
            .sliceTrackIntoLegsUsingAmbiguity(_myHelper.getPrimaryTrack(),
                MIN_ZIG, MIN_BOTH, MIN_LEG_LENGTH, logger, ambigScores,
                OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL,
                analysisPeriod, MAX_LEGS);

        slicedZones = new ArrayList<Zone>();
        for (final LegOfCuts leg : legsAndCuts.getLegs())
        {
          final Zone thisZone = new Zone(leg.get(0).getDTG().getDate()
              .getTime(), leg.get(leg.size() - 1).getDTG().getDate().getTime(),
              _blueProv.getZoneColor());
          slicedZones.add(thisZone);

          // if we have a limit, have we reached it?
          if (MAX_LEGS != null && slicedZones.size() >= MAX_LEGS)
          {
            break;
          }
        }

        // ok, now we have to trim the visible period to these legs
        if (!slicedZones.isEmpty())
        {
          final TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(
              slicedZones.get(0).getStart()), new HiResDate(slicedZones.get(
                  slicedZones.size() - 1).getEnd()));

          // what about the zigs? Extend the time period to include any trailing cuts
          final LegOfCuts zigs = legsAndCuts.getZigs();
          if (!zigs.isEmpty())
          {
            period.extend(zigs.getEndDTG());
          }

          ownshipZoneChart.setPeriod(period);
          targetZoneChart.setPeriod(period);

          // we may also need to resize the range coverage
          ownshipZoneChart.resetRangeCoverage();

        }
      }
      else
      {
        // ok, not ambiguous. Just use the old one.
        slicedZones = StackedDotHelper.sliceOwnship(ownshipCourseSeries,
            _blueProv);
      }

      return slicedZones;
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
            final SensorContactWrapper contact = (SensorContactWrapper) elements
                .nextElement();

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

    private TimePeriod timePeriodFor(final TrackWrapper track)
    {
      final Enumeration<Editable> sensors = track.getSensors().elements();
      TimePeriod res = null;
      while (sensors.hasMoreElements())
      {
        final SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
        if (sensor.getVisible())
        {
          final Enumeration<Editable> ele = sensor.elements();
          while (ele.hasMoreElements())
          {
            final SensorContactWrapper scw = (SensorContactWrapper) ele
                .nextElement();
            if (scw.getVisible())
            {
              final HiResDate thisT = scw.getDTG();
              if (res == null)
              {
                res = new TimePeriod.BaseTimePeriod(thisT, thisT);
              }
              else
              {
                res.extend(thisT);
              }
            }
          }
        }
      }
      return res;
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
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
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

      final IStatus res = new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
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
        final List<Zone> zones = zoneChart.getZones();

        // hmm let's take a safe copy, in case the next call
        // clears the zone list
        final List<Zone> zoneList = new ArrayList<Zone>();
        zoneList.addAll(zones);

        // fire the update. Note: doing this will remove
        // the displayed zones, since the chart will think
        // the track has changed.
        sensor.getHost().firePropertyChange(SupportsPropertyListeners.EXTENDED,
            null, System.currentTimeMillis());

        // and restore the zones
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

      final IStatus res = new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
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
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
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

        res = new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
            "Resolve legs successful", null);
      }
      else
      {
        res = new Status(IStatus.ERROR, TrackShiftActivator.PLUGIN_ID,
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

      final IStatus res = new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
          "Un-resolve legs successful", null);
      return res;
    }

  }

  public static class TestResiduals extends TestCase
  {
    private static TrackWrapper getData(final String name) throws FileNotFoundException
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
      final SensorWrapper sensor = (SensorWrapper) track.getSensors().elements()
          .nextElement();
      sensor.setVisible(true);

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor
          .getStartDTG(), sensor.getEndDTG());

      // try to get zones using ambiguity delta
      final LegsAndZigs res = solver.sliceTrackIntoLegsUsingAmbiguity(track,
          0.2, 0.2, 240, null, null, null, null, timePeriod, null);
      final List<LegOfCuts> legs = res.getLegs();
      final LegOfCuts zigs = res.getZigs();

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 12, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 22, zigs.size());

      // ok, ditch those cuts
      final int fullSensorLen = sensor.size();
      Map<SensorWrapper, LegOfCuts> deleted = DeleteCutsOperation
          .deleteTheseCuts(zigs);
      assertEquals("fewer cuts", 99, sensor.size());

      // ok, and undo them
      BearingResidualsView.restoreCuts(deleted);
      assertEquals("fewer cuts", fullSensorLen, sensor.size());

      // and do it again, so we've got fewer cuts
      deleted = DeleteCutsOperation.deleteTheseCuts(zigs);

      final List<ResolvedLeg> resolvedLegs = solver.resolve(legs);
      assertNotNull(resolvedLegs);
      assertEquals("right num legs", 12, legs.size());

      assertEquals("correct leg", 251.33d, resolvedLegs.get(0).leg.get(0)
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
      TimePeriod period = ZoneChart.calculatePanData(false, 1000, 2000, 1100,
          1300);
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

  private static class DeleteAmbiguousCutsOperation extends CMAPOperation
  {
  
    /**
     * the cuts to be deleted
     *
     */
    final private List<SensorContactWrapper> _cutsToDelete;
  
    private final Runnable _fireUpdateData;
  
    private final ZoneChart zoneChart;
  
    public DeleteAmbiguousCutsOperation(final List<SensorContactWrapper> cutsToDelete,
        final Runnable updateData, final ZoneChart zoneChart)
    {
      super("Delete ambiguous cuts");
  
      _cutsToDelete = cutsToDelete;
      _fireUpdateData = updateData;
      this.zoneChart = zoneChart;
    }
  
    @Override
    public boolean canRedo()
    {
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      
      for (final SensorContactWrapper t : _cutsToDelete)
      {
         t.setAmbiguousBearing(Double.NaN);
      }
      
      // share the good news
      fireModified();
  
      // and refresh
      _fireUpdateData.run();
  
      // and the ownship zone chart
  
      final IStatus res = new Status(IStatus.OK, TrackShiftActivator.PLUGIN_ID,
          "Delete ambiguous cuts successful", null);
      return res;
    }
  
    private void fireModified()
    {
      // remember the zones
      final List<Zone> zones = zoneChart.getZones();

      // hmm let's take a safe copy, in case the next call
      // clears the zone list
      final List<Zone> zoneList = new ArrayList<Zone>();
      zoneList.addAll(zones);

      // and restore the zones
      zoneChart.setZones(zoneList);

    }
  
    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      throw new IllegalArgumentException("Undo not supported");
    }
  }

  private static final String SHOW_COURSE = "SHOW_COURSE";

  private static final String SCALE_ERROR = "SCALE_ERROR";

  protected static void deleteAmbiguousCuts(final TrackWrapper primaryTrack,
      final Runnable updateData, final ZoneChart ownshipZoneChart,
      final ZoneUndoRedoProvider undoRedoProvider)
  {
    // find the time period of hte chart
    final TimePeriod outerPeriod = ownshipZoneChart.getVisiblePeriod();

    // ok, produce the list of cuts to cull
    final LegOfCuts cutsToDelete = findResolvedAmbiguousCuts(
        outerPeriod, primaryTrack);

    // delete the ambiguous cuts
    final IUndoableOperation deleteOperation = new DeleteAmbiguousCutsOperation(
        cutsToDelete, updateData, ownshipZoneChart);

    // wrap the operation
    undoRedoProvider.execute(deleteOperation);
  }
  
  protected static void deleteCutsInTurnB(final TrackWrapper primaryTrack,
      final Runnable updateData, final ZoneChart ownshipZoneChart,
      final ZoneUndoRedoProvider undoRedoProvider)
  {
    final List<Zone> ownshipZones = ownshipZoneChart.getZones();

    if (ownshipZones != null && ownshipZones.size() > 0)
    {
      // find the time period of hte chart
      final TimePeriod outerPeriod = ownshipZoneChart.getVisiblePeriod();

      // ok, produce the list of cuts to cull
      final LegOfCuts cutsToDelete = findCutsNotInZones(ownshipZones,
          outerPeriod, primaryTrack);

      // delete this list of cuts
      final IUndoableOperation deleteOperation = new DeleteCutsOperation(
          cutsToDelete, updateData, ownshipZoneChart);

      // wrap the operation
      undoRedoProvider.execute(deleteOperation);
    }
    else
    {
      CorePlugin.showMessage("Resolve Ambiguity", "Please slice data first");
    }
  }

  public static LegOfCuts findCutsNotInZones(final List<Zone> ownshipZones,
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
          final SensorContactWrapper cut = (SensorContactWrapper) cuts
              .nextElement();
          if (cut.getVisible() && outerPeriod.contains(cut.getDTG()))
          {
            final long thisT = cut.getDTG().getDate().getTime();

            // ok, see if it;s in one of the zones
            boolean inZone = false;
            for (final Zone zone : ownshipZones)
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

  public static LegOfCuts findResolvedAmbiguousCuts(final TimePeriod outerPeriod, 
      final TrackWrapper primaryTrack)
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
          final SensorContactWrapper cut = (SensorContactWrapper) cuts
              .nextElement();
          if (cut.getVisible() && outerPeriod.contains(cut.getDTG()))
          {
            // was it in a zone, and has it been resolved?
            if (!cut.getHasAmbiguousBearing())
            {
              // is there still a value sitting there?
              if (!Double.isNaN(cut.getAmbiguousBearing()))
              {
                res.add(cut);
              }
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

  private BearingFormatter _crossHairFormatter;

  public BearingResidualsView()
  {
    super(true, false);
  }

  @Override
  protected void addToolbarExtras(final IToolBarManager toolBarManager)
  {
    super.addToolbarExtras(toolBarManager);
    toolBarManager.add(showCourse);
  }

  @Override
  protected boolean allowDisplayOfTargetOverview()
  {
    return true;
  }

  @Override
  protected boolean allowDisplayOfZoneChart()
  {
    return true;
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
  protected String formatValue(final double value)
  {
    final String res;
    if (_crossHairFormatter != null)
    {
      res = _crossHairFormatter.format(value);
    }
    else
    {
      res = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(
          value);
    }

    return res;
  }

  protected Runnable getDeleteAmbiguousCutsOperation()
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

            deleteAmbiguousCuts(_myHelper.getPrimaryTrack(), updateData,
                ownshipZoneChart, undoRedoProvider);
          }
        };
        BusyIndicator.showWhile(Display.getCurrent(), wrappedDelete);
      }
    };
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

  private static Logger getLogger()
  {
    final Logger logger;
    final boolean doLogging = TrackShiftActivator.getDefault()
        .getPreferenceStore().getBoolean(PreferenceConstants.DIAGNOSTICS);
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
          TrackShiftActivator.getDefault().getLog().log(new Status(IStatus.INFO,
              TrackShiftActivator.PLUGIN_ID, record.getMessage()));
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
    return new OwnshipZoneSlicer(blueProv);
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
    final RegularTimePeriod myTime = new FixedMillisecond(dtg.getMicros()
        / 1000);

    // get the set of calculated error values that we have stored in the graph
    final TimeSeriesCollection dataset = (TimeSeriesCollection) _dotPlot
        .getDataset();

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
    scaleError = new Action("Show the error when dragging",
        IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
      }
    };
    scaleError.setChecked(false);
    scaleError.setToolTipText(
        "Show symbol scaled to per-cut error when dragging");
    scaleError.setImageDescriptor(TrackShiftActivator.getImageDescriptor(
        "icons/24/scale.png"));

    // see if there's an existing setting for this.

    // now the course action
    relativeAxes = new Action("Centre Bearing axis on North",
        IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();

        processRelativeAxes();
      }
    };
    relativeAxes.setChecked(false);
    relativeAxes.setToolTipText("Centre Bearing axis on North");
    relativeAxes.setImageDescriptor(TrackShiftActivator.getImageDescriptor(
        "icons/24/swap_axis.png"));

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
    showCourse.setImageDescriptor(TrackShiftActivator.getImageDescriptor(
        "icons/24/ShowCourse.png"));

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
    _autoResize.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/24/fit_to_win.png"));
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
    final NumberFormat numFormat;

    if (relativeAxes.isChecked())
    {
      minVal = -180d;
      maxVal = 180d;

      numFormat = new BearingFormatter("0");
      _crossHairFormatter = new BearingFormatter("0.0");
    }
    else
    {
      minVal = 0d;
      maxVal = 360d;

      numFormat = null;
      _crossHairFormatter = null;
    }

    _overviewCourseRenderer.setRange(minVal, maxVal);

    // try to format the labels appropriately
    final NumberAxis numA = (NumberAxis) _linePlot.getRangeAxis();

    numA.setNumberFormatOverride(numFormat);

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

    final List<Zone> zones = ownshipZoneChart.getZones();
    if (zones == null || zones.size() == 0)
    {
      CorePlugin.showMessage("Resolve ambiguity",
          "Please slice ownship legs before resolving ambiguity");
    }
    else
    {
      final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
      for (final Zone zone : zones)
      {
        final TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(
            zone.getStart()), new HiResDate(zone.getEnd()));
        final List<SensorContactWrapper> cuts = StackedDotHelper.getBearings(_myHelper
            .getPrimaryTrack(), true, period);
        final LegOfCuts leg = new LegOfCuts();
        leg.addAll(cuts);

        legs.add(leg);
      }

      final AmbiguityResolver resolver = new AmbiguityResolver();
      final IUndoableOperation resolveCuts = new ResolveCutsOperationAmbig(
          resolver, legs);
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

    SetBackgroundShade backShader = new SetBackgroundShade()
    {

      @Override
      public void setShade(Paint errorColor)
      {
        _dotPlot.setBackgroundPaint(errorColor);
      }
    };
    // update the current datasets

    TimeSeriesCollection errorData = (TimeSeriesCollection) _dotPlot
        .getDataset();
    TimeSeriesCollection lineData = (TimeSeriesCollection) _linePlot
        .getDataset();
    
    // have we been created?
    if (_holder == null || _holder.isDisposed())
    {
      return;
    }
    
    
    
    _myHelper.updateBearingData(errorData, lineData, _switchableTrackDataProvider,
        _onlyVisible.isChecked(), showCourse.isChecked(), relativeAxes
            .isChecked(), this, updateDoublets, _targetCourseSeries,
        _targetSpeedSeries, measuredValuesColl, ambigValuesColl,
        ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries,
        _overviewSpeedRenderer, _overviewCourseRenderer, backShader);

    // and tell the O/S zone chart to update it's controls
    if (ownshipZoneChart != null)
    {
      ownshipZoneChart.updateControls();
    }
  }

}
