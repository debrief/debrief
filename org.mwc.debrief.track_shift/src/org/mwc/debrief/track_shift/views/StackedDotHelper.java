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
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.util.ShapeUtilities;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.zig_detector.ArtificalLegDetector;
import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.mwc.debrief.track_shift.zig_detector.ownship.PeakTrackingOwnshipLegDetector;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ISecondaryTrack;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.TrackDataProvider;

public final class StackedDotHelper
{
  private static class TargetDoublet
  {

    public TrackSegment targetParent;
    public FixWrapper targetFix;

  }

  public static class TestSlicing extends junit.framework.TestCase
  {
    public void testOSLegDetector()
    {
      final TimeSeries osC = new TimeSeries(new FixedMillisecond());
      long time = 0;
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 21d);
      osC.add(new FixedMillisecond(time++), 22d);
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 21d);
      osC.add(new FixedMillisecond(time++), 20d);

      assertFalse(containsIdenticalValues(osC, 3));

      // inject some more duplicates
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 20d);

      assertTrue(containsIdenticalValues(osC, 3));

      osC.clear();
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 21d);
      osC.add(new FixedMillisecond(time++), 21d);
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 21d);
      osC.add(new FixedMillisecond(time++), 21d);
      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 20d);

      assertFalse("check we're verifying single runs of matches",
          containsIdenticalValues(osC, 3));

      osC.add(new FixedMillisecond(time++), 20d);
      osC.add(new FixedMillisecond(time++), 20d);

      assertTrue(containsIdenticalValues(osC, 3));
    }

    public void testSetLeg()
    {
      final TrackWrapper host = new TrackWrapper();
      host.setName("Host Track");

      // create a sensor
      final SensorWrapper sensor = new SensorWrapper("Sensor");
      sensor.setHost(host);
      host.add(sensor);

      // add some cuts
      final ArrayList<SensorContactWrapper> contacts =
          new ArrayList<SensorContactWrapper>();
      for (int i = 0; i < 30; i++)
      {
        final HiResDate thisDTG = new HiResDate(10000 * i);
        final WorldLocation thisLocation =
            new WorldLocation(2 + 0.01 * i, 2 + 0.03 * i, 0);
        final SensorContactWrapper scw =
            new SensorContactWrapper(host.getName(), thisDTG,
                new WorldDistance(4, WorldDistance.MINUTES), 25d, thisLocation,
                Color.RED, "" + i, 0, sensor.getName());
        sensor.add(scw);
        contacts.add(scw);

        // also create a host track fix at this DTG
        final Fix theFix = new Fix(thisDTG, thisLocation, 12d, 3d);
        final FixWrapper newF = new FixWrapper(theFix);
        host.add(newF);
      }

      // produce the target leg
      final TrackWrapper target = new TrackWrapper();
      target.setName("Tgt Track");

      // add a TMA leg
      final Layers theLayers = new Layers();
      theLayers.addThisLayer(host);
      theLayers.addThisLayer(target);

      final SensorContactWrapper[] contactArr =
          contacts.toArray(new SensorContactWrapper[]
          {});
      final RelativeTMASegment newLeg =
          new RelativeTMASegment(contactArr, new WorldVector(1, 1, 0),
              new WorldSpeed(12, WorldSpeed.Kts), 12d, theLayers, Color.red);
      target.add(newLeg);

      final BaseStackedDotsView view = new BaseStackedDotsView(true, false)
      {
        @Override
        protected ZoneSlicer getOwnshipZoneSlicer(final ColorProvider blueProv)
        {
          return null;
        }

        @Override
        protected String getType()
        {
          return null;
        }

        @Override
        protected String getUnits()
        {
          return null;
        }

        @Override
        protected void updateData(final boolean updateDoublets)
        {
          // no, nothing to do.
        }

        @Override
        protected String formatValue(double value)
        {
          return "" + value;
        }

        @Override
        protected boolean allowDisplayOfTargetOverview()
        {
          return false;
        }

        @Override
        protected boolean allowDisplayOfZoneChart()
        {
          return false;
        }
      };

      // try to set a zone on the track
      Zone trimmedPeriod = new Zone(150000, 220000, Color.RED);
      view.setLeg(host, target, trimmedPeriod);

      // ok, check the leg has changed
      assertEquals("leg start changed", 150000, target.getStartDTG().getDate()
          .getTime());
      assertEquals("leg start changed", 220000, target.getEndDTG().getDate()
          .getTime());

      // ok, also see if we can create a new leg
      trimmedPeriod = new Zone(250000, 320000, Color.RED);
      view.setLeg(host, target, trimmedPeriod);

    }
  }

  /**
   * sort out data of interest
   * 
   */
  public static TreeSet<Doublet> getDoublets(final TrackWrapper sensorHost,
      final ISecondaryTrack targetTrack, final boolean onlyVis,
      final boolean needBearing, final boolean needFrequency)
  {
    final TreeSet<Doublet> res = new TreeSet<Doublet>();

    // friendly fix-wrapper to save us repeatedly creating it
    final FixWrapper index =
        new FixWrapper(new Fix(null, new WorldLocation(0, 0, 0), 0.0, 0.0));

    final Vector<TrackSegment> theSegments;
    if (targetTrack != null)
    {
      theSegments = getTargetLegs(targetTrack);
    }
    else
    {
      theSegments = null;
    }

    // loop through our sensor data
    final Enumeration<Editable> sensors = sensorHost.getSensors().elements();
    if (sensors != null)
    {
      while (sensors.hasMoreElements())
      {
        final SensorWrapper wrapper = (SensorWrapper) sensors.nextElement();
        if (!onlyVis || (onlyVis && wrapper.getVisible()))
        {
          final Enumeration<Editable> cuts = wrapper.elements();

          // we're walking through ownship track again, so reset the cache
          resetCache();

          while (cuts.hasMoreElements())
          {
            final SensorContactWrapper scw =
                (SensorContactWrapper) cuts.nextElement();

            if (!onlyVis || (onlyVis && scw.getVisible()))
            {
              // is this cut suitable for what we're looking for?
              if (needBearing)
              {
                if (!scw.getHasBearing())
                {
                  continue;
                }
              }

              // aaah, but does it meet the frequency requirement?
              if (needFrequency)
              {
                if (!scw.getHasFrequency())
                {
                  continue;
                }
              }

              final TargetDoublet doublet =
                  getTargetDoublet(index, theSegments, scw);

              final Doublet thisDub;
              final FixWrapper hostFix;

              final boolean newWay = false;
              if (newWay)
              {
                // we no longer need to do it our own way,
                // TrackWrapper processing has been optimised to
                // cache the iterator
                hostFix =
                    getNearestPositionOnHostTrack(sensorHost, scw.getDTG());
              }
              else
              {
                final Watchable[] matches =
                    sensorHost.getNearestTo(scw.getDTG());
                if (matches != null && matches.length == 1)
                {
                  hostFix = (FixWrapper) matches[0];
                }
                else
                {
                  hostFix = null;
                }
              }

              if (doublet.targetFix != null && hostFix != null)
              {
                thisDub =
                    new Doublet(scw, doublet.targetFix, doublet.targetParent,
                        hostFix);

                // if we've no target track add all the points
                if (targetTrack == null)
                {
                  // store our data
                  res.add(thisDub);
                }
                else
                {
                  // if we've got a target track we only add points
                  // for which we
                  // have
                  // a target location
                  if (doublet.targetFix != null)
                  {
                    // store our data
                    res.add(thisDub);
                  }
                } // if we know the track
                // if there are any matching items

              } // if we find a match
              else if (targetTrack == null && hostFix != null)
              {
                // no target data, just use ownship sensor data
                thisDub = new Doublet(scw, null, null, hostFix);
                res.add(thisDub);
              }
            } // if cut is visible
          } // loop through cuts
        } // if sensor is visible
      } // loop through sensors
    }// if there are sensors
    return res;
  }

  private static FixWrapper getNearestPositionOnHostTrack(
      final TrackWrapper host, final HiResDate dtg)
  {
    final FixWrapper res;

    // check we're in the period for the track
    if (dtg.greaterThanOrEqualTo(host.getStartDTG())
        && dtg.lessThanOrEqualTo(host.getEndDTG()))
    {
      // ok, worth trying
      boolean needReset = false;
      if (host != _cachedTrack)
      {
        needReset = true;
      }

      if (_cachedTime != null && dtg.lessThan(_cachedTime))
      {
        needReset = true;
      }

      if (needReset)
      {
        _cachedValue = null;
        _cachedTrack = host;
        _cachedIterator = null;
      }

      if (_cachedIterator == null)
      {
        _cachedIterator = _cachedTrack.getPositionIterator();
      }

      if (_cachedValue != null
          && _cachedValue.getDTG().greaterThanOrEqualTo(dtg))
      {
        _cachedTime = dtg;
        return _cachedValue;
      }
      else
      {
        // carry on walking forward
        while (_cachedIterator.hasMoreElements())
        {
          _cachedTime = dtg;
          _cachedValue = (FixWrapper) _cachedIterator.nextElement();
          if (_cachedValue.getDateTimeGroup().greaterThanOrEqualTo(dtg))
          {
            return _cachedValue;
          }
        }
      }
      // failed to find it
      res = null;
    }
    else
    {
      // out of period
      res = null;
    }

    return res;
  }

  // ////////////////////////////////////////////////
  // CONSTRUCTOR
  // ////////////////////////////////////////////////

  // ////////////////////////////////////////////////
  // MEMBER METHODS
  // ////////////////////////////////////////////////

  private static TargetDoublet getTargetDoublet(final FixWrapper index,
      final Vector<TrackSegment> theSegments, final SensorContactWrapper scw)
  {
    final TargetDoublet doublet = new TargetDoublet();
    if (theSegments != null && !theSegments.isEmpty())
    {
      final Iterator<TrackSegment> iter = theSegments.iterator();
      while (iter.hasNext())
      {
        final TrackSegment ts = iter.next();

        final TimePeriod validPeriod =
            new TimePeriod.BaseTimePeriod(ts.startDTG(), ts.endDTG());
        if (validPeriod.contains(scw.getDTG()))
        {
          // sorted. here we go
          doublet.targetParent = ts;

          // create an object with the right time
          index.getFix().setTime(scw.getDTG());

          // and find any matching items
          final SortedSet<Editable> items = ts.tailSet(index);
          if (!items.isEmpty())
          {
            doublet.targetFix = (FixWrapper) items.first();
          }
        }
      }
    }

    return doublet;
  }

  private static Vector<TrackSegment> getTargetLegs(
      final ISecondaryTrack targetTrack)
  {
    final Vector<TrackSegment> _theSegments = new Vector<TrackSegment>();
    final Enumeration<Editable> trkData = targetTrack.segments();

    while (trkData.hasMoreElements())
    {
      final Editable thisI = trkData.nextElement();
      if (thisI instanceof SegmentList)
      {
        final SegmentList thisList = (SegmentList) thisI;
        final Enumeration<Editable> theElements = thisList.elements();
        while (theElements.hasMoreElements())
        {
          final TrackSegment ts = (TrackSegment) theElements.nextElement();
          if (ts.getVisible())
          {
            _theSegments.add(ts);
          }
        }

      }
      else if (thisI instanceof TrackSegment)
      {
        final TrackSegment ts = (TrackSegment) thisI;
        _theSegments.add(ts);
      }
    }
    return _theSegments;
  }

  private static void resetCache()
  {
    _cachedIterator = null;
    _cachedValue = null;
    _cachedTime = null;
    _cachedTrack = null;
  }

  /**
   * the maximum number of items we plot as symbols. Above this we just use a line
   */
  private final int MAX_ITEMS_TO_PLOT = 1000;

  /**
   * the track being dragged
   */
  private TrackWrapper _primaryTrack;

  /**
   * the secondary track we're monitoring
   */
  private ISecondaryTrack _secondaryTrack;

  /**
   * the set of points to watch on the primary track. This is stored as a sorted set because if we
   * have multiple sensors they may be suppled in chronological order, or they may represent
   * overlapping time periods
   */
  private TreeSet<Doublet> _primaryDoublets;

  private static Enumeration<Editable> _cachedIterator;

  private static FixWrapper _cachedValue;

  private static HiResDate _cachedTime;

  private static TrackWrapper _cachedTrack;

  /**
   * determine if this time series contains many identical values - this is an indicator for data
   * coming from a simulator, for which turns can't be determined by our peak tracking algorithm.
   * 
   * @param dataset
   * @return
   */
  private static boolean containsIdenticalValues(final TimeSeries dataset,
      final Integer NumMatches)
  {
    final int num = dataset.getItemCount();

    final int numMatches;
    if (NumMatches != null)
    {
      numMatches = NumMatches;
    }
    else
    {
      final double MATCH_PROPORTION = 0.1;
      numMatches = (int) (num * MATCH_PROPORTION);
    }

    double lastCourse = 0d;
    int matchCount = 0;

    for (int ctr = 0; ctr < num; ctr++)
    {
      final TimeSeriesDataItem thisItem = dataset.getDataItem(ctr);
      final double thisCourse = (Double) thisItem.getValue();
      if (thisCourse == lastCourse)
      {
        // ok, count the duplicates
        matchCount++;

        if (matchCount >= numMatches)
        {
          return true;
        }
      }
      else
      {
        matchCount = 0;
      }
      lastCourse = thisCourse;
    }

    return false;
  }

  public static ArrayList<Zone> sliceOwnship(final TimeSeries osCourse,
      final ZoneChart.ColorProvider colorProvider)
  {
    // make a decision on which ownship slicer to use
    final IOwnshipLegDetector detector;
    if (containsIdenticalValues(osCourse, null))
    {
      detector = new ArtificalLegDetector();
    }
    else
    {
      detector = new PeakTrackingOwnshipLegDetector();
    }

    final int num = osCourse.getItemCount();
    final long[] times = new long[num];
    final double[] speeds = new double[num];
    final double[] courses = new double[num];

    for (int ctr = 0; ctr < num; ctr++)
    {
      final TimeSeriesDataItem thisItem = osCourse.getDataItem(ctr);
      final FixedMillisecond thisM = (FixedMillisecond) thisItem.getPeriod();
      times[ctr] = thisM.getMiddleMillisecond();
      speeds[ctr] = 0;
      courses[ctr] = (Double) thisItem.getValue();
    }
    final List<LegOfData> legs =
        detector.identifyOwnshipLegs(times, speeds, courses, 5, Precision.LOW);
    final ArrayList<Zone> res = new ArrayList<Zone>();

    for (final LegOfData leg : legs)
    {
      final Zone newZone =
          new Zone(leg.getStart(), leg.getEnd(), colorProvider.getZoneColor());
      res.add(newZone);
    }

    return res;
  }

  /**
   * produce a color shade, according to whether the max error is inside 3 degrees or not.
   * 
   * @param errorSeries
   * @return
   */
  private static Paint calculateErrorShadeFor(
      final TimeSeriesCollection errorSeries, final double cutOffValue)
  {
    final Paint col;
    double maxError = 0d;
    final TimeSeries ts = errorSeries.getSeries(0);
    final List<?> items = ts.getItems();
    for (final Iterator<?> iterator = items.iterator(); iterator.hasNext();)
    {
      final TimeSeriesDataItem item = (TimeSeriesDataItem) iterator.next();
      final boolean useMe;
      // check this isn't infill
      if (item instanceof ColouredDataItem)
      {
        final ColouredDataItem cd = (ColouredDataItem) item;
        useMe = cd.isShapeFilled();
      }
      else
      {
        useMe = true;
      }
      if (useMe)
      {
        final double thisE = (Double) item.getValue();
        maxError = Math.max(maxError, Math.abs(thisE));
      }
    }

    if (maxError > cutOffValue)
    {
      col = new Color(1f, 0f, 0f, 0.05f);
    }
    else
    {
      final float shade = (float) (0.03f + (cutOffValue - maxError) * 0.02f);
      col = new Color(0f, 1f, 0f, shade);
    }

    return col;
  }

  public List<SensorContactWrapper> getBearings(
      final TrackWrapper primaryTrack, final boolean onlyVis,
      final TimePeriod targetPeriod)
  {
    final List<SensorContactWrapper> res =
        new ArrayList<SensorContactWrapper>();

    // loop through our sensor data
    final Enumeration<Editable> sensors = primaryTrack.getSensors().elements();
    if (sensors != null)
    {
      while (sensors.hasMoreElements())
      {
        final SensorWrapper wrapper = (SensorWrapper) sensors.nextElement();
        if (!onlyVis || (onlyVis && wrapper.getVisible()))
        {
          final Enumeration<Editable> cuts = wrapper.elements();
          while (cuts.hasMoreElements())
          {
            final SensorContactWrapper scw =
                (SensorContactWrapper) cuts.nextElement();
            if (!onlyVis || (onlyVis && scw.getVisible()))
            {
              if (targetPeriod == null || targetPeriod.contains(scw.getDTG()))
              {
                res.add(scw);
              }
              // if we find a match
            } // if cut is visible
          } // loop through cuts
        } // if sensor is visible
      } // loop through sensors
    }// if there are sensors

    return res;
  }

  public TreeSet<Doublet> getDoublets(final boolean onlyVis,
      final boolean needBearing, final boolean needFrequency)
  {
    return getDoublets(_primaryTrack, _secondaryTrack, onlyVis, needBearing,
        needFrequency);
  }

  public TrackWrapper getPrimaryTrack()
  {
    return _primaryTrack;
  }

  public ISecondaryTrack getSecondaryTrack()
  {
    return _secondaryTrack;
  }

  /**
   * initialise the data, check we've got sensor data & the correct number of visible tracks
   * 
   * @param showError
   * @param onlyVis
   * @param holder
   */
  void initialise(final TrackDataProvider tracks, final boolean showError,
      final boolean onlyVis, final Composite holder, final ErrorLogger logger,
      final String dataType, final boolean needBrg, final boolean needFreq)
  {

    // have we been created?
    if (holder == null)
    {
      return;
    }

    // are we visible?
    if (holder.isDisposed())
    {
      return;
    }

    _secondaryTrack = null;
    _primaryTrack = null;

    // do we have some data?
    if (tracks == null)
    {
      // output error message
      logger.logError(IStatus.INFO, "Please open a Debrief plot", null);
      return;
    }

    // check we have a primary track
    final WatchableList priTrk = tracks.getPrimaryTrack();
    if (priTrk == null)
    {
      logger.logError(IStatus.INFO,
          "A primary track must be placed on the Tote", null);
      return;
    }
    else
    {
      if (!(priTrk instanceof TrackWrapper))
      {
        logger.logError(IStatus.INFO,
            "The primary track must be a vehicle track", null);
        return;
      }
      else
      {
        _primaryTrack = (TrackWrapper) priTrk;
      }
    }

    // now the sec track
    final WatchableList[] secs = tracks.getSecondaryTracks();

    // any?
    if ((secs == null) || (secs.length == 0))
    {
      logger.logError(IStatus.INFO, "No secondary track assigned", null);
    }
    else
    {
      // too many?
      if (secs.length > 1)
      {
        logger.logError(IStatus.INFO,
            "Only 1 secondary track may be on the tote", null);
        return;
      }

      // correct sort?
      final WatchableList secTrk = secs[0];
      if (!(secTrk instanceof ISecondaryTrack))
      {
        logger.logError(IStatus.INFO,
            "The secondary track must be a vehicle track", null);
        return;
      }
      else
      {
        _secondaryTrack = (ISecondaryTrack) secTrk;
      }
    }

    // must have worked, hooray
    logger.logError(IStatus.OK, null, null);

    // ok, get the positions
    updateDoublets(onlyVis, needBrg, needFreq);

  }

  /**
   * clear our data, all is finished
   */
  public void reset()
  {
    if (_primaryDoublets != null)
    {
      _primaryDoublets.clear();
    }
    _primaryDoublets = null;
    _primaryTrack = null;
    _secondaryTrack = null;
  }

  /**
   * ok, our track has been dragged, calculate the new series of offsets
   * 
   * @param linePlot
   * @param dotPlot
   * @param onlyVis
   * @param showCourse
   * @param b
   * @param holder
   * @param logger
   * @param targetCourseSeries
   * @param targetSpeedSeries
   * @param ownshipCourseSeries
   * @param targetBearingSeries
   * @param overviewSpeedRenderer
   * @param _overviewCourseRenderer
   * 
   * @param currentOffset
   *          how far the current track has been dragged
   */
  public void updateBearingData(final XYPlot dotPlot, final XYPlot linePlot,
      final XYPlot targetPlot, final TrackDataProvider tracks,
      final boolean onlyVis, final boolean showCourse, final boolean flipAxes,
      final Composite holder, final ErrorLogger logger,
      final boolean updateDoublets,
      final TimeSeriesCollection targetCourseSeries,
      final TimeSeriesCollection targetSpeedSeries,
      final TimeSeries measuredValues, final TimeSeries ambigValues,
      final TimeSeries ownshipCourseSeries,
      final TimeSeries targetBearingSeries,
      final TimeSeries targetCalculatedSeries,
      final ResidualXYItemRenderer overviewSpeedRenderer,
      final WrappingResidualRenderer overviewCourseRenderer)
  {
    // do we even have a primary track
    if (_primaryTrack == null)
    {
      return;
    }

    // ok, find the track wrappers
    if (_secondaryTrack == null)
    {
      initialise(tracks, false, onlyVis, holder, logger, "Bearing", true, false);
    }

    // did it work?
    // if (_secondaryTrack == null)
    // return;

    // ok - the tracks have moved. better update the doublets
    if (updateDoublets)
    {
      updateDoublets(onlyVis, true, false);
    }

    // aah - but what if we've ditched our doublets?
    if ((_primaryDoublets == null) || (_primaryDoublets.size() == 0))
    {
      // better clear the plot
      dotPlot.setDataset(null);
      linePlot.setDataset(null);
      targetPlot.setDataset(null);
      targetPlot.setDataset(1, null);
      return;
    }

    // create the collection of series
    final TimeSeriesCollection errorSeries = new TimeSeriesCollection();
    final TimeSeriesCollection actualSeries = new TimeSeriesCollection();

    // the previous steps occupy some time.
    // just check we haven't lost the primary track while they were running
    if (_primaryTrack == null)
    {
      return;
    }

    // produce a dataset for each track
    final TimeSeries errorValues = new TimeSeries(_primaryTrack.getName());
    final TimeSeries ambigErrorValues =
        new TimeSeries(_primaryTrack.getName() + "(A)");
    final TimeSeries calculatedValues = new TimeSeries("Calculated");
    final TimeSeries osCourseValues = new TimeSeries("O/S Course");
    final TimeSeries tgtCourseValues = new TimeSeries("Tgt Course");
    final TimeSeries tgtSpeedValues = new TimeSeries("Tgt Speed");
    final TimeSeries allCuts = new TimeSeries("Sensor cuts");

    // createa list of series, so we can pause their updates
    final List<TimeSeries> sList = new Vector<TimeSeries>();
    sList.add(errorValues);
    sList.add(ambigErrorValues);
    sList.add(measuredValues);
    sList.add(ambigValues);
    sList.add(calculatedValues);
    sList.add(osCourseValues);
    sList.add(tgtCourseValues);
    sList.add(tgtSpeedValues);
    sList.add(allCuts);
    sList.add(targetCalculatedSeries);
    sList.add(targetBearingSeries);
    sList.add(ownshipCourseSeries);

    final List<TimeSeriesCollection> tList = new Vector<TimeSeriesCollection>();
    tList.add(targetCourseSeries);
    tList.add(targetSpeedSeries);
    tList.add(errorSeries);
    tList.add(actualSeries);

    // ok, wrap the switching on/off of notify in try/catch,
    // to be sure to switch notify back on at end
    try
    {

      // now switch off updates
      for (final TimeSeriesCollection series : tList)
      {
        series.setNotify(false);
      }
      for (final TimeSeries series : sList)
      {
        series.setNotify(false);

        // and clear the list
        series.clear();
      }

      // clear the existing target datasets
      targetCourseSeries.removeAllSeries();
      targetSpeedSeries.removeAllSeries();

      // create the color for resolved ambig data
      final Color grayShade = new Color(155, 155, 155, 50);

      // ok, run through the points on the primary track
      final Iterator<Doublet> iter = _primaryDoublets.iterator();
      while (iter.hasNext())
      {
        final Doublet thisD = iter.next();

        final boolean parentIsNotDynamic =
            thisD.getTargetTrack() == null
                || !(thisD.getTargetTrack() instanceof DynamicInfillSegment);

        try
        {
          // obvious stuff first (stuff that doesn't need the tgt data)
          final Color thisColor = thisD.getColor();
          double measuredBearing = thisD.getMeasuredBearing();
          double ambigBearing = thisD.getAmbiguousMeasuredBearing();
          final HiResDate currentTime = thisD.getDTG();
          final FixedMillisecond thisMilli =
              new FixedMillisecond(currentTime.getDate().getTime());

          final boolean hasAmbiguous = !Double.isNaN(ambigBearing);

          // ok, we need to make the color darker if it's starboard
          final boolean bearingToPort = thisD.getSensorCut().isBearingToPort();

          // make the color darker, if it's to stbg
          final Color bearingColor;
          if (bearingToPort)
          {
            bearingColor = thisColor;
          }
          else
          {
            bearingColor = thisColor.darker();
          }

          // put the measured bearing back in the positive domain
          if (measuredBearing < 0)
          {
            measuredBearing += 360d;
          }

          // stop, stop, stop - do we wish to plot bearings in the +/- 180 domain?
          if (flipAxes)
          {
            if (measuredBearing > 180)
            {
              measuredBearing -= 360;
            }
          }

          final ColouredDataItem mBearing =
              new ColouredDataItem(thisMilli, measuredBearing, bearingColor,
                  false, null, true, parentIsNotDynamic, thisD.getSensorCut());

          // and add them to the series
          measuredValues.addOrUpdate(mBearing);

          if (hasAmbiguous)
          {
            // put the ambig baering into the correct domain
            while (ambigBearing < 0)
            {
              ambigBearing += 360;
            }

            if (flipAxes && ambigBearing > 180)
            {
              ambigBearing -= 360;
            }

            // make the color darker, if we're on the stbd bearnig
            final Color ambigColor;
            if (bearingToPort)
            {
              ambigColor = thisColor.darker();
            }
            else
            {
              ambigColor = thisColor;
            }

            // if this cut has been resolved, we don't show a symbol
            // for the ambiguous cut
            final boolean showSymbol = true;
            final Color color =
                thisD.getHasBeenResolved() ? grayShade : ambigColor;

            final ColouredDataItem amBearing =
                new ColouredDataItem(thisMilli, ambigBearing, color, false,
                    null, showSymbol, parentIsNotDynamic, thisD.getSensorCut());
            ambigValues.addOrUpdate(amBearing);
          }

          // do we have target data?
          if (thisD.getTarget() != null)
          {
            // and has this target fix know it's location?
            // (it may not, if it's a relative leg that has been extended)
            if (thisD.getTarget().getFixLocation() != null)
            {
              double calculatedBearing = thisD.getCalculatedBearing(null, null);
              final Color errorColor = thisD.getTarget().getColor();
              final double thisTrueError =
                  thisD.calculateBearingError(measuredBearing,
                      calculatedBearing);

              if (flipAxes)
              {
                if (calculatedBearing > 180)
                {
                  calculatedBearing -= 360;
                }
              }
              else
              {
                if (calculatedBearing < 0)
                {
                  calculatedBearing += 360;
                }
              }

              final Color brgColor;
              if (bearingToPort)
              {
                brgColor = errorColor;
              }
              else
              {
                brgColor = errorColor.darker();
              }

              final ColouredDataItem newTrueError =
                  new ColouredDataItem(thisMilli, thisTrueError, brgColor,
                      false, null, true, parentIsNotDynamic);

              final ColouredDataItem cBearing =
                  new ColouredDataItem(thisMilli, calculatedBearing, brgColor,
                      true, null, true, parentIsNotDynamic, thisD.getTarget());

              errorValues.addOrUpdate(newTrueError);
              calculatedValues.addOrUpdate(cBearing);

              // and the ambiguous error, if it hasn't been resolved
              if (!thisD.getHasBeenResolved())
              {
                if (flipAxes)
                {
                  if (ambigBearing > 180)
                  {
                    ambigBearing -= 360;
                  }
                }

                final Color ambigColor;
                if (bearingToPort)
                {
                  ambigColor = errorColor.darker();
                }
                else
                {
                  ambigColor = errorColor;
                }

                final double thisAmnigError =
                    thisD
                        .calculateBearingError(ambigBearing, calculatedBearing);
                final ColouredDataItem newAmbigError =
                    new ColouredDataItem(thisMilli, thisAmnigError, ambigColor,
                        false, null, true, parentIsNotDynamic);
                ambigErrorValues.addOrUpdate(newAmbigError);
              }

            }
          }

        }
        catch (final SeriesException e)
        {
          CorePlugin.logError(IStatus.INFO,
              "some kind of trip whilst updating bearing plot", e);
        }

      }

      // just double-check we've still got our primary doublets
      if (_primaryDoublets == null)
      {
        CorePlugin.logError(IStatus.WARNING,
            "FOR SOME REASON PRIMARY DOUBLETS IS NULL - INVESTIGATE", null);
        return;
      }

      if (_primaryDoublets.size() == 0)
      {
        CorePlugin.logError(IStatus.WARNING,
            "FOR SOME REASON PRIMARY DOUBLETS IS ZERO LENGTH - INVESTIGATE",
            null);
        return;
      }

      // right, we do course in a special way, since it isn't dependent on the
      // target track. Do course here.
      final HiResDate startDTG = _primaryDoublets.first().getDTG();
      final HiResDate endDTG = _primaryDoublets.last().getDTG();

      if (startDTG.greaterThan(endDTG))
      {
        System.err.println("in the wrong order, start:" + startDTG + " end:"
            + endDTG);
        return;
      }

      // special case - if the primary track is a single location
      if (_primaryTrack.isSinglePointTrack())
      {
        // ok, it's a single point. We'll use the sensor cut times for the course data

        // get the single location
        final FixWrapper loc =
            (FixWrapper) _primaryTrack.getPositionIterator().nextElement();

        final Enumeration<Editable> segments = _secondaryTrack.segments();
        while (segments.hasMoreElements())
        {
          final Editable nextE = segments.nextElement();
          // if there's just one segment - then we need to wrap it
          final SegmentList segList;
          if (nextE instanceof SegmentList)
          {
            segList = (SegmentList) nextE;
          }
          else
          {
            segList = new SegmentList();
            segList.addSegment((TrackSegment) nextE);
          }

          final Enumeration<Editable> segIter = segList.elements();
          while (segIter.hasMoreElements())
          {
            final TrackSegment segment = (TrackSegment) segIter.nextElement();

            final Enumeration<Editable> enumer = segment.elements();
            while (enumer.hasMoreElements())
            {
              final FixWrapper thisTgtFix = (FixWrapper) enumer.nextElement();

              double ownshipCourse =
                  MWC.Algorithms.Conversions.Rads2Degs(loc.getCourse());

              // stop, stop, stop - do we wish to plot bearings in the +/- 180 domain?
              if (flipAxes && ownshipCourse > 180)
              {
                ownshipCourse -= 360;
              }
              final FixedMillisecond thisMilli =
                  new FixedMillisecond(thisTgtFix.getDateTimeGroup().getDate()
                      .getTime());
              final ColouredDataItem crseBearing =
                  new ColouredDataItem(thisMilli, ownshipCourse,
                      loc.getColor(), true, null, true, true);
              osCourseValues.addOrUpdate(crseBearing);
            }

          }
        }
      }
      else
      {

        // loop through using the iterator
        final Enumeration<Editable> pIter = _primaryTrack.getPositionIterator();
        final TimePeriod validPeriod =
            new TimePeriod.BaseTimePeriod(startDTG, endDTG);
        final List<Editable> validItems = new LinkedList<Editable>();
        while (pIter.hasMoreElements())
        {
          final FixWrapper fw = (FixWrapper) pIter.nextElement();
          if (validPeriod.contains(fw.getDateTimeGroup()))
          {
            validItems.add(fw);
          }
          else
          {
            // have we passed the end of the requested period?
            if (fw.getDateTimeGroup().greaterThan(endDTG))
            {
              // ok, drop out
              break;
            }
          }
        }

        // ok, now go through the list
        final Iterator<Editable> vIter = validItems.iterator();
        final int freq = Math.max(1, validItems.size() / MAX_ITEMS_TO_PLOT);
        int ctr = 0;
        while (vIter.hasNext())
        {
          final Editable ed = vIter.next();
          if (ctr++ % freq == 0)
          {
            final FixWrapper fw = (FixWrapper) ed;
            final FixedMillisecond thisMilli =
                new FixedMillisecond(fw.getDateTimeGroup().getDate().getTime());
            double ownshipCourse =
                MWC.Algorithms.Conversions.Rads2Degs(fw.getCourse());

            // stop, stop, stop - do we wish to plot bearings in the +/- 180 domain?
            if (flipAxes && ownshipCourse > 180)
            {
              ownshipCourse -= 360;
            }

            final ColouredDataItem crseBearing =
                new ColouredDataItem(thisMilli, ownshipCourse, fw.getColor(),
                    true, null, true, true);
            osCourseValues.addOrUpdate(crseBearing);
          }
        }

      }

      if (_secondaryTrack != null)
      {
        // sort out the target course/speed
        final Enumeration<Editable> segments = _secondaryTrack.segments();
        final TimePeriod period =
            new TimePeriod.BaseTimePeriod(startDTG, endDTG);
        while (segments.hasMoreElements())
        {
          final Editable nextE = segments.nextElement();
          // if there's just one segment - then we need to wrap it
          final SegmentList segList;
          if (nextE instanceof SegmentList)
          {
            segList = (SegmentList) nextE;
          }
          else
          {
            segList = new SegmentList();
            segList.setWrapper((TrackWrapper) _secondaryTrack);
            segList.addSegment((TrackSegment) nextE);
          }

          final Enumeration<Editable> segIter = segList.elements();
          while (segIter.hasMoreElements())
          {
            final TrackSegment segment = (TrackSegment) segIter.nextElement();

            // is this an infill segment
            final boolean isInfill = segment instanceof DynamicInfillSegment;

            // check it has values, and is in range
            if (segment.isEmpty() || segment.startDTG().greaterThan(endDTG)
                || segment.endDTG().lessThan(startDTG))
            {
              // ok, we can skip this one
            }
            else
            {
              final Enumeration<Editable> points = segment.elements();
              Double lastCourse = null;
              while (points.hasMoreElements())
              {
                final FixWrapper fw = (FixWrapper) points.nextElement();
                if (period.contains(fw.getDateTimeGroup()))
                {
                  // ok, create a point for it
                  final FixedMillisecond thisMilli =
                      new FixedMillisecond(fw.getDateTimeGroup().getDate()
                          .getTime());

                  double tgtCourse =
                      MWC.Algorithms.Conversions.Rads2Degs(fw.getCourse());
                  final double tgtSpeed = fw.getSpeed();

                  // see if we need to change the domain of the course to match
                  // the previous value
                  if (lastCourse != null)
                  {
                    if (tgtCourse - lastCourse > 190)
                    {
                      tgtCourse = tgtCourse - 360;
                    }
                    else if (tgtCourse - lastCourse < -180)
                    {
                      tgtCourse = 360 + tgtCourse;
                    }
                  }
                  lastCourse = tgtCourse;

                  // trim to +/- domain if we're flipping axes
                  if (flipAxes && tgtCourse > 180)
                  {
                    tgtCourse -= 360;
                  }

                  // we use the raw color for infills, to help find which
                  // infill we're referring to (esp in random infills)
                  final Color courseColor;
                  final Color speedColor;
                  if (isInfill)
                  {
                    courseColor = fw.getColor();
                    speedColor = fw.getColor();
                  }
                  else
                  {
                    courseColor = fw.getColor().brighter();
                    speedColor = fw.getColor().darker();
                  }

                  final ColouredDataItem crseBearingItem =
                      new ColouredDataItem(thisMilli, tgtCourse, courseColor,
                          isInfill, null, true, true);
                  tgtCourseValues.addOrUpdate(crseBearingItem);
                  final ColouredDataItem tgtSpeedItem =
                      new ColouredDataItem(thisMilli, tgtSpeed, speedColor,
                          isInfill, null, true, true);
                  tgtSpeedValues.addOrUpdate(tgtSpeedItem);
                }
              }
            }
          }
        }
      }

      // sort out the sensor cuts (all of them, not just those when we have target legs)
      final TimePeriod sensorPeriod;
      if (_secondaryTrack != null)
      {
        sensorPeriod =
            new TimePeriod.BaseTimePeriod(_secondaryTrack.getStartDTG(),
                _secondaryTrack.getEndDTG());
      }
      else
      {
        sensorPeriod = null;
      }
      final List<SensorContactWrapper> theBearings =
          getBearings(_primaryTrack, onlyVis, sensorPeriod);
      for (final SensorContactWrapper cut : theBearings)
      {
        double theBearing;

        // ensure it's in the positive domain
        if (cut.getBearing() < 0)
        {
          theBearing = cut.getBearing() + 360;
        }
        else
        {
          theBearing = cut.getBearing();
        }

        // put in the correct domain, if necessary
        if (flipAxes)
        {
          if (theBearing > 180d)
          {
            theBearing -= 360d;
          }
        }
        else
        {
          if (theBearing < 0)
          {
            theBearing += 360;
          }
        }

        // ok, store it.
        allCuts.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(cut
            .getDTG().getDate().getTime()), theBearing));
      }

      // ok, add these new series
      if (errorValues.getItemCount() > 0)
      {
        errorSeries.addSeries(errorValues);
      }
      if (ambigErrorValues.getItemCount() > 0)
      {
        errorSeries.addSeries(ambigErrorValues);
      }

      actualSeries.addSeries(measuredValues);

      if (ambigValues.getItemCount() > 0)
      {
        actualSeries.addSeries(ambigValues);
      }

      if (calculatedValues.getItemCount() > 0)
      {
        actualSeries.addSeries(calculatedValues);
      }

      if (tgtCourseValues.getItemCount() > 0)
      {
        targetCourseSeries.addSeries(tgtCourseValues);

        // ok, sort out the renderer
        overviewCourseRenderer.setLightweightMode(tgtCourseValues
            .getItemCount() > MAX_ITEMS_TO_PLOT);
      }

      if (tgtSpeedValues.getItemCount() > 0)
      {
        targetSpeedSeries.addSeries(tgtSpeedValues);

        overviewSpeedRenderer
            .setLightweightMode(tgtSpeedValues.getItemCount() > MAX_ITEMS_TO_PLOT);
      }

      if (showCourse)
      {
        targetCourseSeries.addSeries(osCourseValues);
      }

      if (calculatedValues.getItemCount() > 0)
      {
        targetCalculatedSeries.addAndOrUpdate(calculatedValues);
      }

      // and the course data for the zone chart
      if (!osCourseValues.isEmpty())
      {
        if (ownshipCourseSeries != null)
        {
          // is it currently empty?
          if (ownshipCourseSeries.isEmpty())
          {
            ownshipCourseSeries.addAndOrUpdate(osCourseValues);
          }
          else
          {
            // ok, ignore it. we only assign the data in the first pass
          }
        }
      }

      // and the bearing data for the zone chart
      if (!allCuts.isEmpty())
      {
        if (targetBearingSeries != null)
        {
          // is it currently empty?
          if (targetBearingSeries.isEmpty())
          {
            targetBearingSeries.addAndOrUpdate(allCuts);
          }
          else
          {
            // ok, ignore it. we only assign the data in the first pass
          }
        }
      }

      // find the color for maximum value in the error series, if we have error data
      if (errorSeries.getSeriesCount() > 0)
      {
        // retrieve the cut-off value
        final double cutOffValue;
        final String prefValue =
            Application.getThisProperty(RelativeTMASegment.CUT_OFF_VALUE_DEGS);
        if (prefValue != null && prefValue.length() > 0
            && Double.valueOf(prefValue) != null)
        {
          cutOffValue = Double.valueOf(prefValue);
        }
        else
        {
          cutOffValue = 3d;
        }

        final Paint errorColor =
            calculateErrorShadeFor(errorSeries, cutOffValue);
        dotPlot.setBackgroundPaint(errorColor);
      }

      dotPlot.setDataset(errorSeries);
      linePlot.setDataset(actualSeries);
      targetPlot.setDataset(0, targetCourseSeries);
      targetPlot.setDataset(1, targetSpeedSeries);
    }
    finally
    {
      // now switch off updates
      for (final Series series : sList)
      {
        series.setNotify(true);
      }
      // now switch off updates
      for (final TimeSeriesCollection series : tList)
      {
        series.setNotify(true);
      }
    }
  }

  /**
   * go through the tracks, finding the relevant position on the other track.
   * 
   */
  private void updateDoublets(final boolean onlyVis, final boolean needBearing,
      final boolean needFreq)
  {
    // ok - we're now there
    // so, do we have primary and secondary tracks?
    if (_primaryTrack != null)
    {
      // cool sort out the list of sensor locations for these tracks
      _primaryDoublets =
          getDoublets(_primaryTrack, _secondaryTrack, onlyVis, needBearing,
              needFreq);
    }
  }

  /**
   * ok, our track has been dragged, calculate the new series of offsets
   * 
   * @param linePlot
   * @param dotPlot
   * @param onlyVis
   * @param holder
   * @param logger
   * @param fZeroMarker
   * 
   * @param currentOffset
   *          how far the current track has been dragged
   */
  public void updateFrequencyData(final XYPlot dotPlot, final XYPlot linePlot,
      final TrackDataProvider tracks, final boolean onlyVis,
      final Composite holder, final ErrorLogger logger,
      final boolean updateDoublets, final ValueMarker fZeroMarker)
  {

    // do we have anything?
    if (_primaryTrack == null)
    {
      return;
    }

    // ok, find the track wrappers
    if (_secondaryTrack == null)
    {
      initialise(tracks, false, onlyVis, holder, logger, "Frequency", false,
          true);
    }

    // ok - the tracks have moved. better update the doublets
    if (updateDoublets)
    {
      updateDoublets(onlyVis, false, true);
    }

    // aah - but what if we've ditched our doublets?
    // aah - but what if we've ditched our doublets?
    if ((_primaryDoublets == null) || (_primaryDoublets.size() == 0))
    {
      // better clear the plot
      dotPlot.setDataset(null);
      linePlot.setDataset(null);
      return;
    }

    // create the collection of series
    final TimeSeriesCollection errorSeries = new TimeSeriesCollection();
    final TimeSeriesCollection actualSeries = new TimeSeriesCollection();

    
    if(_primaryTrack == null)
      return;
    
    // produce a dataset for each track
    final TimeSeries errorValues = new TimeSeries(_primaryTrack.getName());

    final TimeSeries measuredValues = new TimeSeries("Measured");
    // final TimeSeries correctedValues = new TimeSeries("Corrected");
    final TimeSeries predictedValues = new TimeSeries("Predicted");
    final TimeSeries baseValues = new TimeSeries("Base");

    // ok, run through the points on the primary track
    final Iterator<Doublet> iter = _primaryDoublets.iterator();
    while (iter.hasNext())
    {
      final Doublet thisD = iter.next();
      try
      {

        final Color thisColor = thisD.getColor();
        final double measuredFreq = thisD.getMeasuredFrequency();
        final HiResDate currentTime = thisD.getDTG();
        final FixedMillisecond thisMilli =
            new FixedMillisecond(currentTime.getDate().getTime());

        final ColouredDataItem mFreq =
            new ColouredDataItem(thisMilli, measuredFreq, thisColor, false,
                null, true, true, thisD.getSensorCut());

        // final ColouredDataItem corrFreq = new ColouredDataItem(
        // new FixedMillisecond(currentTime.getDate().getTime()),
        // correctedFreq, thisColor, false, null);
        measuredValues.addOrUpdate(mFreq);

        // do we have target data?
        if (thisD.getTarget() != null)
        {
          // final double correctedFreq = thisD.getCorrectedFrequency();
          final double baseFreq = thisD.getBaseFrequency();
          final Color calcColor = thisD.getTarget().getColor();

          // final ColouredDataItem corrFreq =
          // new ColouredDataItem(thisMilli, correctedFreq, thisColor, false,
          // null, true, true);

          // did we get a base frequency? We may have a track
          // with a section of data that doesn't have frequency, you see.
          if (!Double.isNaN(baseFreq))
          {
            final double predictedFreq = thisD.getPredictedFrequency();
            final double thisError =
                thisD.calculateFreqError(measuredFreq, predictedFreq);
            final ColouredDataItem pFreq =
                new ColouredDataItem(thisMilli, predictedFreq, calcColor, true,
                    null, true, true, thisD.getTarget());
            final ColouredDataItem bFreq =
                new ColouredDataItem(thisMilli, baseFreq, thisColor, true,
                    null, true, true);            
            final ColouredDataItem eFreq =
                new ColouredDataItem(thisMilli, thisError, thisColor, false,
                    null, true, true);
            baseValues.addOrUpdate(bFreq);
            predictedValues.addOrUpdate(pFreq);
            errorValues.addOrUpdate(eFreq);
          }

          // correctedValues.addOrUpdate(corrFreq);
        }

      }
      catch (final SeriesException e)
      {
        CorePlugin.logError(IStatus.INFO,
            "some kind of trip whilst updating frequency plot", e);
      }

    }

    // ok, add these new series
    if (errorValues.getItemCount() > 0)
    {
      errorSeries.addSeries(errorValues);
    }

    // find the color for maximum value in the error series, if we have error data
    if (errorSeries.getSeriesCount() > 0)
    {
      final double cutOffValue;

      // retrieve the cut-off value
      final String prefValue =
          Application.getThisProperty(RelativeTMASegment.CUT_OFF_VALUE_HZ);
      if (prefValue != null && prefValue.length() > 0
          && Double.valueOf(prefValue) != null)
      {
        cutOffValue = Double.valueOf(prefValue) / 100d;
      }
      else
      {
        cutOffValue = 1d;
      }

      final Paint errorColor = calculateErrorShadeFor(errorSeries, cutOffValue);
      dotPlot.setBackgroundPaint(errorColor);
    }

    actualSeries.addSeries(measuredValues);
    // actualSeries.addSeries(correctedValues);

    if (predictedValues.getItemCount() > 0)
    {
      actualSeries.addSeries(predictedValues);
    }
    if (baseValues.getItemCount() > 0)
    {
      actualSeries.addSeries(baseValues);
      
      // sort out the rendering for the BaseFrequencies.
      // we want to show a solid line, with no markers
      final int BaseFreqSeries = 2;
      ColourStandardXYItemRenderer lineRend =
          (ColourStandardXYItemRenderer) linePlot.getRenderer();
      lineRend.setSeriesShape(BaseFreqSeries, ShapeUtilities
          .createDiamond(0.2f));
      lineRend.setSeriesStroke(BaseFreqSeries, new BasicStroke(4));
      lineRend.setSeriesShapesVisible(BaseFreqSeries, false);
      lineRend.setSeriesShapesFilled(BaseFreqSeries, false);
    }

    dotPlot.setDataset(errorSeries);
    linePlot.setDataset(actualSeries);
  }
}
