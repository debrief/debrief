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

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;

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
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.TrackDataProvider;

public final class StackedDotHelper
{
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

  // ////////////////////////////////////////////////
  // CONSTRUCTOR
  // ////////////////////////////////////////////////

  // ////////////////////////////////////////////////
  // MEMBER METHODS
  // ////////////////////////////////////////////////

  public TreeSet<Doublet> getDoublets(final boolean onlyVis,
      final boolean needBearing, final boolean needFrequency)
  {
    return getDoublets(_primaryTrack, _secondaryTrack, onlyVis, needBearing,
        needFrequency);
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
                  continue;
              }

              // aaah, but does it meet the frequency requirement?
              if (needFrequency)
              {
                if (!scw.getHasFrequency())
                  continue;
              }

              FixWrapper targetFix = null;
              TrackSegment targetParent = null;

              if (targetTrack != null)
              {
                // right, get the track segment and fix nearest to
                // this
                // DTG
                final Enumeration<Editable> trkData = targetTrack.segments();
                final Vector<TrackSegment> _theSegments =
                    new Vector<TrackSegment>();

                while (trkData.hasMoreElements())
                {

                  final Editable thisI = trkData.nextElement();
                  if (thisI instanceof SegmentList)
                  {
                    final SegmentList thisList = (SegmentList) thisI;
                    final Enumeration<Editable> theElements =
                        thisList.elements();
                    while (theElements.hasMoreElements())
                    {
                      final TrackSegment ts =
                          (TrackSegment) theElements.nextElement();
                      _theSegments.add(ts);
                    }

                  }
                  if (thisI instanceof TrackSegment)
                  {
                    final TrackSegment ts = (TrackSegment) thisI;
                    _theSegments.add(ts);
                  }
                }

                if (!_theSegments.isEmpty())
                {
                  final Iterator<TrackSegment> iter = _theSegments.iterator();
                  while (iter.hasNext())
                  {
                    final TrackSegment ts = iter.next();

                    final TimePeriod validPeriod =
                        new TimePeriod.BaseTimePeriod(ts.startDTG(), ts
                            .endDTG());
                    if (validPeriod.contains(scw.getDTG()))
                    {
                      // sorted. here we go
                      targetParent = ts;

                      // create an object with the right time
                      index.getFix().setTime(scw.getDTG());

                      // and find any matching items
                      final SortedSet<Editable> items = ts.tailSet(index);
                      if (!items.isEmpty())
                      {
                        targetFix = (FixWrapper) items.first();
                      }
                    }

                  }
                }
              }

              final Watchable[] matches = sensorHost.getNearestTo(scw.getDTG());
              if ((matches != null) && (matches.length > 0)
                  && (targetFix != null))
              {
                final FixWrapper hostFix = (FixWrapper) matches[0];

                final Doublet thisDub =
                    new Doublet(scw, targetFix, targetParent, hostFix);

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
                  if (targetFix != null)
                  {
                    // store our data
                    res.add(thisDub);
                  }
                } // if we know the track
              } // if there are any matching items
              // if we find a match
            } // if cut is visible
          } // loop through cuts
        } // if sensor is visible
      } // loop through sensors
    }// if there are sensors

    return res;
  }

  public List<SensorContactWrapper> getBearings(
      final TrackWrapper primaryTrack, final boolean onlyVis,
      HiResDate startDTG, HiResDate endDTG)
  {
    final List<SensorContactWrapper> res =
        new ArrayList<SensorContactWrapper>();

    // sort out the outer period
    TimePeriod targetPeriod = new TimePeriod.BaseTimePeriod(startDTG, endDTG);

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
              if (targetPeriod.contains(scw.getDTG()))
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
   * 
   * @param currentOffset
   *          how far the current track has been dragged
   */
  public void updateBearingData(final XYPlot dotPlot, final XYPlot linePlot,
      XYPlot targetPlot, final TrackDataProvider tracks, final boolean onlyVis,
      final boolean showCourse, final boolean flipAxes, final Composite holder,
      final ErrorLogger logger, final boolean updateDoublets,
      TimeSeriesCollection targetCourseSeries,
      TimeSeriesCollection targetSpeedSeries, TimeSeries ownshipCourseSeries,
      TimeSeries targetBearingSeries, TimeSeries targetCalculatedSeries)
  {
    // System.out.println("updating bearing data:" + ctr++);

    // do we even have a primary track
    if (_primaryTrack == null)
      return;

    // ok, find the track wrappers
    if (_secondaryTrack == null)
      initialise(tracks, false, onlyVis, holder, logger, "Bearing", true, false);

    // did it work?
    // if (_secondaryTrack == null)
    // return;

    // ok - the tracks have moved. better update the doublets
    if (updateDoublets)
      updateDoublets(onlyVis, true, false);

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

    // produce a dataset for each track
    final TimeSeries errorValues = new TimeSeries(_primaryTrack.getName());
    final TimeSeries measuredValues = new TimeSeries("Measured");
    final TimeSeries ambigValues = new TimeSeries("Ambiguous Bearing");
    final TimeSeries calculatedValues = new TimeSeries("Calculated");
    final TimeSeries osCourseValues = new TimeSeries("O/S Course");
    final TimeSeries tgtCourseValues = new TimeSeries("Tgt Course");
    final TimeSeries tgtSpeedValues = new TimeSeries("Tgt Speed");
    final TimeSeries allCuts = new TimeSeries("Sensor cuts");

    // createa list of series, so we can pause their updates
    List<Series> sList = new Vector<Series>();
    sList.add(errorValues);
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

    List<TimeSeriesCollection> tList = new Vector<TimeSeriesCollection>();
    tList.add(targetCourseSeries);
    tList.add(targetSpeedSeries);
    tList.add(errorSeries);
    tList.add(actualSeries);

    // ok, wrap the switching on/off of notify in try/catch,
    // to be sure to switch notify back on at end
    try
    {

      // now switch off updates
      for (final Series series : sList)
      {
        series.setNotify(false);
      }
      for (final TimeSeriesCollection series : tList)
      {
        series.setNotify(false);
      }

      // clear the existing target datasets
      targetCourseSeries.removeAllSeries();
      targetSpeedSeries.removeAllSeries();

      // ok, run through the points on the primary track
      final Iterator<Doublet> iter = _primaryDoublets.iterator();
      while (iter.hasNext())
      {
        final Doublet thisD = iter.next();

        try
        {
          // obvious stuff first (stuff that doesn't need the tgt data)
          final Color thisColor = thisD.getColor();
          double measuredBearing = thisD.getMeasuredBearing();
          double ambigBearing = thisD.getAmbiguousMeasuredBearing();
          final HiResDate currentTime = thisD.getDTG();
          final FixedMillisecond thisMilli =
              new FixedMillisecond(currentTime.getDate().getTime());

          // put the measured bearing back in the positive domain
          if (measuredBearing < 0)
            measuredBearing += 360d;

          // stop, stop, stop - do we wish to plot bearings in the +/- 180 domain?
          if (flipAxes)
            if (measuredBearing > 180)
              measuredBearing -= 360;

          final ColouredDataItem mBearing =
              new ColouredDataItem(thisMilli, measuredBearing, thisColor,
                  false, null, true);

          // and add them to the series
          measuredValues.addOrUpdate(mBearing);

          if (ambigBearing != Doublet.INVALID_BASE_FREQUENCY)
          {
            if (flipAxes)
              if (ambigBearing > 180)
                ambigBearing -= 360;

            final ColouredDataItem amBearing =
                new ColouredDataItem(thisMilli, ambigBearing, thisColor, false,
                    null, true);
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
              final Color calcColor = thisD.getTarget().getColor();
              final double thisError =
                  thisD.calculateBearingError(measuredBearing,
                      calculatedBearing);
              final ColouredDataItem newError =
                  new ColouredDataItem(thisMilli, thisError, thisColor, false,
                      null, true);

              if (flipAxes)
                if (calculatedBearing > 180)
                  calculatedBearing -= 360;

              final ColouredDataItem cBearing =
                  new ColouredDataItem(thisMilli, calculatedBearing, calcColor,
                      true, null, true);

              errorValues.addOrUpdate(newError);
              calculatedValues.addOrUpdate(cBearing);
            }
          }

        }
        catch (final SeriesException e)
        {
          CorePlugin.logError(Status.INFO,
              "some kind of trip whilst updating bearing plot", e);
        }

      }

      // just double-check we've still got our primary doublets
      if (_primaryDoublets == null)
      {
        CorePlugin.logError(Status.WARNING,
            "FOR SOME REASON PRIMARY DOUBLETS IS NULL - INVESTIGATE", null);
        return;
      }

      if (_primaryDoublets.size() == 0)
      {
        CorePlugin.logError(Status.WARNING,
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
        FixWrapper loc =
            (FixWrapper) _primaryTrack.getPositionIterator().nextElement();

        Enumeration<Editable> segments = _secondaryTrack.segments();
        while (segments.hasMoreElements())
        {
          Editable nextE = segments.nextElement();
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

          Enumeration<Editable> segIter = segList.elements();
          while (segIter.hasMoreElements())
          {
            TrackSegment segment = (TrackSegment) segIter.nextElement();

            Enumeration<Editable> enumer = segment.elements();
            while (enumer.hasMoreElements())
            {
              FixWrapper thisTgtFix = (FixWrapper) enumer.nextElement();

              double ownshipCourse =
                  MWC.Algorithms.Conversions.Rads2Degs(loc.getCourse());

              // stop, stop, stop - do we wish to plot bearings in the +/- 180 domain?
              if (flipAxes && ownshipCourse > 180)
                ownshipCourse -= 360;
              final FixedMillisecond thisMilli =
                  new FixedMillisecond(thisTgtFix.getDateTimeGroup().getDate()
                      .getTime());
              final ColouredDataItem crseBearing =
                  new ColouredDataItem(thisMilli, ownshipCourse,
                      loc.getColor(), true, null, true);
              osCourseValues.addOrUpdate(crseBearing);
            }

          }
        }
      }
      else
      {

        // loop through using the iterator
        Enumeration<Editable> pIter = _primaryTrack.getPositionIterator();
        TimePeriod validPeriod =
            new TimePeriod.BaseTimePeriod(startDTG, endDTG);
        while (pIter.hasMoreElements())
        {
          FixWrapper fw = (FixWrapper) pIter.nextElement();
          if (validPeriod.contains(fw.getDateTimeGroup()))
          {
            final FixedMillisecond thisMilli =
                new FixedMillisecond(fw.getDateTimeGroup().getDate().getTime());
            double ownshipCourse =
                MWC.Algorithms.Conversions.Rads2Degs(fw.getCourse());

            // stop, stop, stop - do we wish to plot bearings in the +/- 180 domain?
            if (flipAxes && ownshipCourse > 180)
              ownshipCourse -= 360;

            final ColouredDataItem crseBearing =
                new ColouredDataItem(thisMilli, ownshipCourse, fw.getColor(),
                    true, null, true);
            osCourseValues.addOrUpdate(crseBearing);
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
      }

      // sort out the target course/speed
      Enumeration<Editable> segments = _secondaryTrack.segments();
      TimePeriod period = new TimePeriod.BaseTimePeriod(startDTG, endDTG);
      while (segments.hasMoreElements())
      {
        Editable nextE = segments.nextElement();
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

        Enumeration<Editable> segIter = segList.elements();
        while (segIter.hasMoreElements())
        {
          TrackSegment segment = (TrackSegment) segIter.nextElement();

          // is this an infill segment
          final boolean isInfill = segment instanceof DynamicInfillSegment;

          // check it's in range
          if (segment.startDTG().greaterThan(endDTG)
              || segment.endDTG().lessThan(startDTG))
          {
            // ok, we can skip this one
          }
          else
          {
            Enumeration<Editable> points = segment.elements();
            Double lastCourse = null;
            while (points.hasMoreElements())
            {
              FixWrapper fw = (FixWrapper) points.nextElement();
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
                        isInfill, null, true);
                tgtCourseValues.addOrUpdate(crseBearingItem);
                final ColouredDataItem tgtSpeedItem =
                    new ColouredDataItem(thisMilli, tgtSpeed, speedColor,
                        isInfill, null, true);
                tgtSpeedValues.addOrUpdate(tgtSpeedItem);
              }
            }
          }
        }
      }

      // sort out the sensor cuts (all of them, not just those when we have target legs)
      final List<SensorContactWrapper> theBearings =
          getBearings(_primaryTrack, onlyVis, _secondaryTrack.getStartDTG(),
              _secondaryTrack.getEndDTG());
      for (final SensorContactWrapper cut : theBearings)
      {
        final double theBearing;

        // ensure it's in the positive domain
        if (cut.getBearing() < 0)
        {
          theBearing = cut.getBearing() + 360;
        }
        else
        {
          theBearing = cut.getBearing();
        }

        // ok, store it.
        allCuts.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(cut
            .getDTG().getDate().getTime()), theBearing));
      }

      // ok, add these new series
      if (errorValues.getItemCount() > 0)
        errorSeries.addSeries(errorValues);

      actualSeries.addSeries(measuredValues);

      if (ambigValues.getItemCount() > 0)
        actualSeries.addSeries(ambigValues);

      if (calculatedValues.getItemCount() > 0)
      {
        actualSeries.addSeries(calculatedValues);
      }

      if (tgtCourseValues.getItemCount() > 0)
        targetCourseSeries.addSeries(tgtCourseValues);

      if (tgtSpeedValues.getItemCount() > 0)
        targetSpeedSeries.addSeries(tgtSpeedValues);

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

      // find the color for maximum value in the error series
      Paint errorColor = calculateErrorShadeFor(errorSeries);
      dotPlot.setBackgroundPaint(errorColor);

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
   * produce a color shade, according to whether the max error is inside 3 degrees or not.
   * 
   * @param errorSeries
   * @return
   */
  private Paint calculateErrorShadeFor(TimeSeriesCollection errorSeries)
  {
    final Paint col;
    double maxError = 0d;
    TimeSeries ts = errorSeries.getSeries(0);
    List<?> items = ts.getItems();
    for (Iterator<?> iterator = items.iterator(); iterator.hasNext();)
    {
      TimeSeriesDataItem item = (TimeSeriesDataItem) iterator.next();
      double thisE = (Double) item.getValue();
      maxError = Math.max(maxError, Math.abs(thisE));
    }

    // retrieve the cut-off value
    String prefValue =
        Application.getThisProperty(RelativeTMASegment.CUT_OFF_VALUE_DEGS);
    final double cutOffValue;
    if (prefValue != null && prefValue.length() > 0
        && Double.valueOf(prefValue) != null)
    {
      cutOffValue = Double.valueOf(prefValue);
    }
    else
    {
      cutOffValue = 3d;
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
      return;

    // are we visible?
    if (holder.isDisposed())
      return;

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
        _primaryTrack = (TrackWrapper) priTrk;
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
      _primaryDoublets.clear();
    _primaryDoublets = null;
    _primaryTrack = null;
    _secondaryTrack = null;
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
   * 
   * @param currentOffset
   *          how far the current track has been dragged
   */
  public void updateFrequencyData(final XYPlot dotPlot, final XYPlot linePlot,
      final TrackDataProvider tracks, final boolean onlyVis,
      final Composite holder, final ErrorLogger logger,
      final boolean updateDoublets)
  {

    // do we have anything?
    if (_primaryTrack == null)
      return;

    // ok, find the track wrappers
    if (_secondaryTrack == null)
      initialise(tracks, false, onlyVis, holder, logger, "Frequency", false,
          true);

    // ok - the tracks have moved. better update the doublets
    if (updateDoublets)
      updateDoublets(onlyVis, false, true);

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

    // produce a dataset for each track
    final TimeSeries errorValues = new TimeSeries(_primaryTrack.getName());

    final TimeSeries measuredValues = new TimeSeries("Measured");
    final TimeSeries correctedValues = new TimeSeries("Corrected");
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
                null, true);

        // final ColouredDataItem corrFreq = new ColouredDataItem(
        // new FixedMillisecond(currentTime.getDate().getTime()),
        // correctedFreq, thisColor, false, null);
        measuredValues.addOrUpdate(mFreq);

        // do we have target data?
        if (thisD.getTarget() != null)
        {
          final double correctedFreq = thisD.getCorrectedFrequency();
          final double baseFreq = thisD.getBaseFrequency();
          final Color calcColor = thisD.getTarget().getColor();

          final ColouredDataItem corrFreq =
              new ColouredDataItem(thisMilli, correctedFreq, thisColor, true,
                  null, true);

          // did we get a base frequency? We may have a track
          // with a section of data that doesn't have frequency, you see.
          if (baseFreq != Doublet.INVALID_BASE_FREQUENCY)
          {
            final double predictedFreq = thisD.getPredictedFrequency();
            final double thisError =
                thisD.calculateFreqError(measuredFreq, predictedFreq);
            final ColouredDataItem bFreq =
                new ColouredDataItem(thisMilli, baseFreq, thisColor, true,
                    null, true);
            final ColouredDataItem pFreq =
                new ColouredDataItem(thisMilli, predictedFreq, calcColor,
                    false, null, true);
            final ColouredDataItem eFreq =
                new ColouredDataItem(thisMilli, thisError, thisColor, false,
                    null, true);
            baseValues.addOrUpdate(bFreq);
            predictedValues.addOrUpdate(pFreq);
            errorValues.addOrUpdate(eFreq);
          }

          correctedValues.addOrUpdate(corrFreq);
        }

      }
      catch (final SeriesException e)
      {
        CorePlugin.logError(Status.INFO,
            "some kind of trip whilst updating frequency plot", e);
      }

    }

    // ok, add these new series
    if (errorValues.getItemCount() > 0)
      errorSeries.addSeries(errorValues);

    actualSeries.addSeries(measuredValues);
    actualSeries.addSeries(correctedValues);

    if (predictedValues.getItemCount() > 0)
      actualSeries.addSeries(predictedValues);
    if (baseValues.getItemCount() > 0)
      actualSeries.addSeries(baseValues);

    dotPlot.setDataset(errorSeries);
    linePlot.setDataset(actualSeries);
  }

  public ISecondaryTrack getSecondaryTrack()
  {
    return _secondaryTrack;
  }

  public TrackWrapper getPrimaryTrack()
  {
    return _primaryTrack;
  }

}
