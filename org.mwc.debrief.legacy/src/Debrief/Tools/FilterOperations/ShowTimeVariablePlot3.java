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
package Debrief.Tools.FilterOperations;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.Series;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Tote.StepControl;
import Debrief.Tools.Tote.toteCalculation;
import Debrief.Tools.Tote.Calculations.atbCalc;
import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.bearingRateCalc;
import Debrief.Tools.Tote.Calculations.courseCalc;
import Debrief.Tools.Tote.Calculations.depthCalc;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Tools.Tote.Calculations.relBearingCalc;
import Debrief.Tools.Tote.Calculations.speedCalc;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ISecondaryTrack;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Editable;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.MetafileCanvasGraphics2d;
import MWC.GUI.JFreeChart.BearingRateFormatter;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GUI.JFreeChart.CourseFormatter;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.DatedToolTipGenerator;
import MWC.GUI.JFreeChart.DepthFormatter;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.JFreeChart.RelBearingFormatter;
import MWC.GUI.JFreeChart.RelativeDateAxis;
import MWC.GUI.JFreeChart.StepperChartPanel;
import MWC.GUI.JFreeChart.StepperXYPlot;
import MWC.GUI.JFreeChart.formattingOperation;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.ptplot.Swing.SwingPlot;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.Utilities.Errors.Trace;

public final class ShowTimeVariablePlot3 implements FilterOperation
{
  static public class CalculationHolder
  {
    public final toteCalculation _theCalc;

    public final formattingOperation _theFormatter;

    public final boolean _isRelative;

    public final double _clipMax;

    private final CalculationWizard _wizard;

    public CalculationHolder(final toteCalculation theCalcVal,
        final formattingOperation theFormatterVal, final boolean isRelative,
        final double clipMax)
    {
      this(theCalcVal, theFormatterVal, isRelative, clipMax, null);
    }

    public CalculationHolder(final toteCalculation theCalcVal,
        final formattingOperation theFormatterVal, final boolean isRelative,
        final double clipMax, final CalculationWizard wizard)
    {
      _theCalc = theCalcVal;
      _theFormatter = theFormatterVal;
      _isRelative = isRelative;
      _clipMax = clipMax;
      _wizard = wizard;
    }

    public CalculationWizard getWizard()
    {
      return _wizard;
    }

    /**
     * method to indicate whether this calculation uses relative data - in which case we do not need
     * to plot the primary track, but we only plot calculated data where both primary and secondary
     * data are present
     *
     * @return yes/no
     */
    public boolean isARelativeCalculation()
    {
      return _isRelative;
    }

    @Override
    public final String toString()
    {
      return _theCalc.toString();
    }
  }

  public interface CalculationWizard
  {
    public int open(toteCalculation calc, WatchableList primary,
        Editable[] subjects);
  }

  static public final class MySwingPlot extends SwingPlot
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * the XYPlot we are showing. we have a link to it, so that we can open it's editor
     */
    private NewFormattedJFreeChart _xyPlot = null;

    /*
     * @param thePlot
     *
     * @param theParent
     */
    public MySwingPlot(final JPanel thePlot, final PropertiesPanel theParent,
        final NewFormattedJFreeChart xyPlot)
    {
      super(thePlot, theParent);
      _xyPlot = xyPlot;
    }

    /**
     * open up an editor for the xy plot
     */
    public final void doEdit()
    {
      super._theParent.addEditor(_xyPlot.getInfo(), null);
    }

    public final void doWMF()
    {
      doWMF(System.getProperty("user.home"));
    }

    public final void doWMF(final String dir)
    {
      // TODO: reinstate this

      // get the old background colour
      final Paint oldColor = _xyPlot.getBackgroundPaint();

      // set the background to clear
      _xyPlot.setBackgroundPaint(null);

      // create the metafile graphics
      final MetafileCanvasGraphics2d mf = new MetafileCanvasGraphics2d(dir,
          (Graphics2D) getPanel().getGraphics());

      // copy the projection
      final MWC.Algorithms.Projections.FlatProjection fp = new FlatProjection();
      fp.setScreenArea(_thePlot.getSize());
      mf.setProjection(fp);

      // start drawing
      mf.startDraw(null);

      // sort out the background colour
      mf.setBackgroundColor(java.awt.Color.white);

      // ask the canvas to paint the image
      getPanel().paintWMFComponent(mf);

      // and finish
      mf.endDraw(null);

      // and restore the background colour
      _xyPlot.setBackgroundPaint(oldColor);

    }

    private StepperChartPanel getPanel()
    {
      return (StepperChartPanel) _thePlot;
    }

    @Override
    protected final void initForm()
    {
      super.initForm();

      // and the WMF button
      final JButton wmfBtn = new JButton("WMF");
      wmfBtn.addActionListener(new java.awt.event.ActionListener()
      {
        /**
         * Invoked when an action occurs.
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
          doWMF();
        }
      });
      super._buttonPanel.add(wmfBtn);

      // and the edit properties button
      final JButton editBtn = new JButton("Edit");
      editBtn.addActionListener(new java.awt.event.ActionListener()
      {
        /**
         * Invoked when an action occurs.
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
          doEdit();
        }
      });
      super._buttonPanel.add(editBtn);
    }

  }

  /**
   * worker bee type class which allows us polymorphically to add either a number x-y pair to an x-y
   * series, or to add a TimePeriod value to a BasicTimeSeries
   *
   * @see BasicTimeSeries
   * @see XYSeries
   */
  private static interface VersatileSeriesAdder
  {

    /**
     * add this data point to the supplied series
     *
     * @param thisSeries
     *          ther destination for the point
     * @param theTime
     *          the DTG
     * @param data
     *          the data item
     * @param thisColor
     *          the colour of this point
     * @param connectToPrevious
     *          whether to connect to the previous one
     * @param provider
     *          something to do something with.
     * @param parentIsVisible
     *          TODO
     */
    void add(Series thisSeries, HiResDate theTime, double data, Color thisColor,
        boolean connectToPrevious, ColouredDataItem.OffsetProvider provider,
        boolean parentIsVisible);

    void addSeries(AbstractSeriesDataset collection, Series thisSeries,
        Color defaultColor);
  }

  /**
   * the theshold at which we decide a heading is passing through zero degrees
   */
  private final static double COURSE_THRESHOLD = 250;

  /**
   * interpolate to determine the time at which the course would pass through zero degrees
   *
   * @param last_time
   *          the time of the previous course value
   * @param current_time
   *          the time of the current course value
   * @param lastCourse
   *          the last course value
   * @param currentCourse
   *          the current course value
   * @param clipMax
   * @return the time at which the course would pass through zero
   */
  private static final long calcZeroCrossingTime(final long last_time,
      final long current_time, final double lastCourse,
      final double currentCourse, final double clipMax)
  {
    long res = 0;
    double delta, range;
    double theLastCourse = lastCourse;
    double theCurrentCourse = currentCourse;

    if (clipMax == 180d)
    {
      // put the courses into the correct "frame"
      if (theLastCourse < 0)
      {
        theLastCourse += 360;
      }

      if (theCurrentCourse < 0)
      {
        theCurrentCourse += 360;
      }

      // find the total course change
      range = theCurrentCourse - theLastCourse;

      // how far through this is zero?
      delta = 180 - theLastCourse;

    }
    else
    {
      // put the courses into the correct "frame"
      if (theLastCourse > 180)
      {
        theLastCourse -= 360;
      }

      if (theCurrentCourse > 180)
      {
        theCurrentCourse -= 360;
      }

      // find the total course change
      range = theCurrentCourse - theLastCourse;

      // how far through this is zero?
      delta = 0 - theLastCourse;

    }

    // and as a proportion?
    final double proportion = delta / range;

    // convert this to a time
    res = last_time + (long) ((current_time - last_time) * proportion);

    return res;
  }

  /**
   * @param theCalculation
   *          we're currently performing
   * @param thisPrimary
   *          the point on the primary track
   * @param thisSecondary
   *          the point on the secondary track
   * @param currentTime
   *          the current time
   * @param connectToPrevious
   *          whether to connect to the previous point
   * @param thisColor
   *          the current colour
   * @param thisSeries
   *          the data series we're building up
   * @param theAdder
   * @return
   */
  private static boolean createDataPoint(final toteCalculation theCalculation,
      final Watchable thisPrimary, final Watchable thisSecondary,
      final HiResDate currentTime, final boolean connectToPrevious,
      final Color thisColor, final Series thisSeries,
      final ColouredDataItem.OffsetProvider provider,
      final VersatileSeriesAdder theAdder)
  {
    boolean connectToPrev = connectToPrevious;
    // and perform the calculation
    final double data = theCalculation.calculate(thisSecondary, thisPrimary,
        currentTime);

    // just check that a valid answer was returned (if we don't have data,
    // then NaN is returned
    if (Double.isNaN(data))
    {
      // remember that the next point doesn't connect to it's previous one
      // since we want to show the gap represented by this datum
      connectToPrev = false;
    }
    else
    {
      final boolean parentIsVis;
      if (thisSecondary instanceof FixWrapper)
      {
        final FixWrapper fw = (FixWrapper) thisSecondary;
        parentIsVis = fw.getSymbolShowing() || fw.getArrowShowing();
      }
      else if (thisSecondary instanceof ColouredDataItem.ShowMarker)
      {
        parentIsVis = true;
      }
      else
      {
        parentIsVis = false;
      }

      // yes, we have data - create the data point
      theAdder.add(thisSeries, currentTime, data, thisColor, connectToPrev,
          provider, parentIsVis);

      // right, we've displayed a valid point, allow the next one to
      // connect to
      // this
      connectToPrev = true;
    }

    return connectToPrev;
  }

  /**
   * Collate the data points to plot
   *
   * @param primaryTrack
   *          the primary track
   * @param myOperation
   *          the calculation we're making
   * @param theTracks
   *          the selected set of tracks
   * @param start_time
   *          the start time selected
   * @param end_time
   *          the end time selected
   * @param provider
   *          the provider of the time offset used when plotting time-zero data
   * @return the dataset to plot
   * @see toteCalculation#isWrappableData
   * @see toteCalculation#calculate(Watchable primary,Watchable secondary,HiResDate thisTime)
   * @see Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder#isARelativeCalculation
   * @see WatchableList#getItemsBetween(HiResDate start,HiResDate end)
   * @see TimeSeriesCollection#addSeries(BasicTimeSeries series)
   */
  public static AbstractSeriesDataset getDataSeries(
      final WatchableList primaryTrack, final CalculationHolder myOperation,
      final Vector<WatchableList> theTracks, final HiResDate start_time,
      final HiResDate end_time, final ColouredDataItem.OffsetProvider provider)
  {

    final toteCalculation theCalculation = myOperation._theCalc;

    AbstractSeriesDataset theSeriesCollection = null;

    // ok, now collate the data
    VersatileSeriesAdder theAdder = null;
    // sort out the adder for what we're doing
    if (HiResDate.inHiResProcessingMode())
    {
      theSeriesCollection = new TimeSeriesCollection();
      theAdder = new VersatileSeriesAdder()
      {
        @Override
        public void add(final Series thisSeries, final HiResDate theTime,
            final double data, final Color thisColor,
            final boolean connectToPrevious,
            final ColouredDataItem.OffsetProvider provider1,
            final boolean parentIsVisible)
        {
          // HI-RES NOT DONE - FixedMillisecond should be converted
          // some-how to
          // FixedMicroSecond
          final TimeSeriesDataItem newItem = new ColouredDataItem(
              new FixedMillisecond((long) (theTime.getMicros() / 1000d)), data,
              thisColor, connectToPrevious, provider1, parentIsVisible, true);

          // To change body of implemented methods use File | Settings
          // | File
          // Templates.
          final TimeSeries theSeries = (TimeSeries) thisSeries;
          theSeries.add(newItem);
        }

        @Override
        public void addSeries(final AbstractSeriesDataset collection,
            final Series thisSeries, final Color defaultColor)
        {
          final TimeSeriesCollection coll = (TimeSeriesCollection) collection;
          coll.addSeries((TimeSeries) thisSeries);

        }
      };

    }
    else
    {
      theSeriesCollection = new TimeSeriesCollection();

      // right, just working with normal dates
      theAdder = new VersatileSeriesAdder()
      {
        @Override
        public void add(final Series thisSeries, final HiResDate theTime,
            final double data, final Color thisColor,
            final boolean connectToPrevious,
            final ColouredDataItem.OffsetProvider provider1,
            final boolean parentIsVisible)
        {
          // HI-RES NOT DONE - FixedMillisecond should be converted
          // some-how to
          // FixedMicroSecond
          final ColouredDataItem newItem = new ColouredDataItem(
              new FixedMillisecond(theTime.getDate().getTime()), data,
              thisColor, connectToPrevious, provider1, parentIsVisible, true);

          // To change body of implemented methods use File | Settings
          // | File
          // Templates.
          final TimeSeries theSeries = (TimeSeries) thisSeries;
          theSeries.add(newItem);
        }

        @Override
        public void addSeries(final AbstractSeriesDataset collection,
            final Series thisSeries, final Color defaultColor)
        {
          final TimeSeriesCollection coll = (TimeSeriesCollection) collection;
          coll.addSeries((TimeSeries) thisSeries);
        }

      };
    }

    // calculate the data variables for our tracks
    final Enumeration<WatchableList> iter = theTracks.elements();
    while (iter.hasMoreElements())
    {
      final WatchableList thisSecondaryTrack = iter.nextElement();

      // is this a relative calculation?
      if (myOperation.isARelativeCalculation())
      {
        // yes, but we don't bother with the primary track, see if this
        // is it
        if (thisSecondaryTrack == primaryTrack)
        {
          // just double check that we have primary data
          final Collection<Editable> ss = thisSecondaryTrack.getItemsBetween(
              start_time, end_time);

          if (ss == null)
          {
            Application.logError2(ToolParent.WARNING,
                "Insufficient points found in primary track."
                    + "\nPlease check coverage of time controller bars", null);
            return null;
          }

          // drop out, and wait for the next cycle
          continue;
        }
      }

      // ////////////////////////////////////////////////////
      // step through the track
      //
      final Collection<Editable> secondaryItems = thisSecondaryTrack
          .getItemsBetween(start_time, end_time);

      // indicator for whether we join this data point to the previous one
      boolean connectToPrevious = false;

      // have we found any?. Hey, listen here. The "getItemsBetween"
      // method may return data items, but we may still not be able to do the calc
      // (such as if we have "NaN" for depth). So we still do a sanity check
      // at the end of this method to stop us adding empty data series to the collection.
      if (secondaryItems == null)
      {
        Application.logError2(ToolParent.WARNING,
            "Insufficient points found in secondary track."
                + "\nPlease check coverage of time controller bars", null);
        continue;
      }
      else
      {
        // remember the default color for this series
        Color seriesColor;

        // ok, now collate the data
        Series thisSeries = null;
        // sort out the adder for what we're doing
        if (HiResDate.inHiResProcessingMode())
        {
          thisSeries = new XYSeries(thisSecondaryTrack.getName());
        }
        else
        {
          thisSeries = new TimeSeries(thisSecondaryTrack.getName());
        }

        seriesColor = thisSecondaryTrack.getColor();

        // split into separate processing here, depending on where we're
        // looking
        // at a relative calculation
        if (myOperation.isARelativeCalculation())
        {
          // yes, it is a relative calculation.

          // Find out if it's a special case (where we don't have time
          // data)
          final boolean secSingleton =
              (thisSecondaryTrack instanceof LightweightTrackWrapper)
                  && (((LightweightTrackWrapper) thisSecondaryTrack)
                      .isSinglePointTrack());
          if (thisSecondaryTrack.getStartDTG() == null || secSingleton)
          {
            // do we have any primary data to fall back on (to
            // decide the times
            // for
            // data points)
            if (primaryTrack.getStartDTG() == null || primaryTrack.getStartDTG()
                .equals(primaryTrack.getEndDTG()))
            {

              // ////////////////////////////////////////////////
              // CASE 1 - neither track has time data, relative
              // calc
              // ////////////////////////////////////////////////

              // so, we don't have primary or secondary data.
              // produce data
              // values at the start and end of the track
              // produce data points at the primary track
              // locations
              final Iterator<Editable> it = secondaryItems.iterator();
              final Watchable theSecondaryPoint = (Watchable) it.next();

              // get an iterator for the primary track
              final Collection<Editable> primaryPoints = primaryTrack
                  .getItemsBetween(start_time, end_time);

              // do we have any primary data in this period
              if (primaryPoints != null)
              {
                final Iterator<Editable> throughPrimary = primaryPoints
                    .iterator();
                final Watchable thisPrimary = (Watchable) throughPrimary.next();

                // ok, create the series with it's two points in
                produceTwoPointDataSeries(theCalculation, thisPrimary,
                    theSecondaryPoint, thisSeries, start_time, end_time,
                    provider, theAdder);

              }
            }
            else
            {

              // ////////////////////////////////////////////////
              // CASE 2 - secondary track has time data, relative
              // calc
              // ////////////////////////////////////////////////

              // so, we do have time data for the secondary track,
              // but not on
              // the primary track
              // therefore we produce data points at the primary
              // track locations
              final Watchable[] theSecondaryPoints = thisSecondaryTrack
                  .getNearestTo(start_time);
              final Watchable theSecondaryPoint = theSecondaryPoints[0];

              final Color thisColor = theSecondaryPoint.getColor();

              // get an iterator for the primary track
              final Collection<Editable> primaryPoints = primaryTrack
                  .getItemsBetween(start_time, end_time);

              if (primaryPoints != null)
              {
                final Iterator<Editable> throughPrimary = primaryPoints
                    .iterator();
                while (throughPrimary.hasNext())
                {
                  final Watchable thisPrimary = (Watchable) throughPrimary
                      .next();

                  final HiResDate currentTime = thisPrimary.getTime();

                  // and add the new data point (if we have
                  // to)
                  connectToPrevious = createDataPoint(theCalculation,
                      thisPrimary, theSecondaryPoint, currentTime,
                      connectToPrevious, thisColor, thisSeries, provider,
                      theAdder);

                } // stepping through the primary track

              } // whether we have primary points

            }
          }
          else
          // whether we have DTG data
          {

            // ////////////////////////////////////////////////
            // CASE 3 - both tracks have time data, relative calc
            // ////////////////////////////////////////////////
            // yes, we do have DTG data for this track - hooray!

            // ok, step through the list
            final Iterator<Editable> secIter = secondaryItems.iterator();

            // remember the last point - used to check if we're
            // passing through
            // zero degs
            double lastSecondaryValue = Double.NaN; // we we're
                                                    // using NaN but
                                                    // it
            // was failing the equality
            // test
            HiResDate lastTime = null;

            // we remember the fix, since the first cut after a
            // dynamic infill should take the color of the infill
            Watchable prevItem = null;

            throughThisTrack: while (secIter.hasNext())
            {
              final Watchable thisSecondary = (Watchable) secIter.next();

              Color thisColor = thisSecondary.getColor();

              // what's the current time?
              final HiResDate currentTime = thisSecondary.getTime();

              // is this fix visible?
              if (thisSecondary.getVisible())
              {

                // if it's a fix, hide it if the parent segment is hidden
                if (thisSecondary instanceof FixWrapper)
                {
                  final FixWrapper fw = (FixWrapper) thisSecondary;
                  final Editable parent = fw.getSegment();
                  if (parent != null && parent instanceof TrackSegment)
                  {
                    final TrackSegment ts = (TrackSegment) parent;
                    if (!ts.getVisible())
                    {
                      connectToPrevious = false;

                      // ok, the parent segment is hidden, skip this point
                      continue;
                    }
                  }
                }

                // the point on the primary track we work with
                Watchable thisPrimary = null;

                // find the fix on the primary track which is
                // nearest in
                // time to this one (if we need to)
                Watchable[] nearList;

                // temp switch on interpolation
                Boolean oldInterp = null;
                if (primaryTrack instanceof ISecondaryTrack)
                {
                  final ISecondaryTrack tw = (ISecondaryTrack) primaryTrack;
                  oldInterp = tw.getInterpolatePoints();
                  tw.setInterpolatePoints(true);
                }

                // find it's nearest point on the primary track
                nearList = primaryTrack.getNearestTo(currentTime);

                // and restore the interpolate points setting
                if (oldInterp != null)
                {
                  final ISecondaryTrack tw = (ISecondaryTrack) primaryTrack;
                  tw.setInterpolatePoints(oldInterp.booleanValue());
                }

                // yes. right, we only perform a calc if we have
                // primary data
                // for this point
                if (nearList.length == 0)
                {
                  // remember that the next point doesn't
                  // connect to it's
                  // previous one
                  // since we want to show the gap represented
                  // by this datum
                  connectToPrevious = false;

                  // drop out, and wait for the next cycle
                  continue throughThisTrack;
                }
                else
                {
                  thisPrimary = nearList[0];
                }

                // ////////////////////////////////////////////////
                // NOW PUT IN BIT TO WRAP THROUGH ZERO WHERE
                // APPLICABLE
                // ////////////////////////////////////////////////

                // produce the new calculated value
                final double thisVal = theCalculation.calculate(thisSecondary,
                    thisPrimary, currentTime);

                // SPECIAL HANDLING - do we need to check if
                // this data passes
                // through 360 degs?
                if (theCalculation.isWrappableData())
                {
                  // add extra points, if we need to
                  connectToPrevious = insertWrappingPoints(lastSecondaryValue,
                      thisVal, lastTime, currentTime, thisColor, thisSeries,
                      connectToPrevious, provider, theAdder,
                      myOperation._clipMax);
                }
                // ////////////////////////////////////////////////
                // THANK YOU, WE'RE PLEASED TO RETURN YOU TO
                // YOUR NORMAL PROGRAM
                // ////////////////////////////////////////////////

                // ok, check the segments. if the last segment was dynamic, and we're not,
                // use the last color. Do we know the previous fix?
                if (prevItem != null && thisSecondary instanceof FixWrapper)
                {
                  final FixWrapper thisF = (FixWrapper) thisSecondary;
                  final TrackSegment thisSeg = thisF.getSegment();
                  if (thisSeg != null && prevItem instanceof FixWrapper)
                  {
                    final FixWrapper prevF = (FixWrapper) prevItem;
                    final TrackSegment prevSeg = prevF.getSegment();
                    // is it from a different segment?
                    if (!thisSeg.equals(prevSeg)
                        && (prevSeg instanceof DynamicInfillSegment
                            && !(thisSeg instanceof DynamicInfillSegment)))
                    {
                      // ok, use the previous color
                      thisColor = prevItem.getColor();
                    }
                  }
                }

                // and add the new data point (if we have to)
                connectToPrevious = createDataPoint(theCalculation, thisPrimary,
                    thisSecondary, currentTime, connectToPrevious, thisColor,
                    thisSeries, provider, theAdder);

                lastSecondaryValue = thisVal;
                lastTime = currentTime;

                prevItem = thisSecondary;

              } // whether this point is visible
            } // stepping through this track
          } // whether we have DTG data

        }
        else
        {
          // so, this is an absolute calculation - we don't need to
          // worry about
          // the primry
          // track

          // do we have time data for this secondary track?
          if (thisSecondaryTrack.getStartDTG() == null)
          {

            // ////////////////////////////////////////////////
            // CASE 4 - no time data, non-relative calculation
            // ////////////////////////////////////////////////

            // it's ok. It we don't have time related data for this
            // point we
            // just create
            // data points for it at the start & end of the track

            // ok, create the series with it's two points in
            // ok, step through the list
            final Iterator<Editable> it = secondaryItems.iterator();
            final Watchable thisSecondary = (Watchable) it.next();

            // and
            produceTwoPointDataSeries(theCalculation, null, thisSecondary,
                thisSeries, start_time, end_time, provider, theAdder);

          }
          else
          {

            // ////////////////////////////////////////////////
            // CASE 5 - with time data, non-relative calculation
            // ////////////////////////////////////////////////

            // ok, step through the list
            final Iterator<Editable> it = secondaryItems.iterator();

            // remember the last point - used to check if we're
            // passing through
            // zero degs
            double lastSecondaryValue = Double.NaN; // we we're
                                                    // using NaN but
                                                    // it
            // was failing the equality
            // test
            HiResDate lastTime = null;

            Watchable prevFix = null;
            Color previousColor = null;

            while (it.hasNext())
            {
              final Watchable thisSecondary = (Watchable) it.next();

              // if it's a fix, hide it if the parent segment is hidden
              if (thisSecondary instanceof FixWrapper)
              {
                final FixWrapper fw = (FixWrapper) thisSecondary;
                final Editable parent = fw.getSegment();
                if (parent != null && parent instanceof TrackSegment)
                {
                  final TrackSegment ts = (TrackSegment) parent;
                  if (!ts.getVisible())
                  {
                    connectToPrevious = false;

                    // ok, the parent segment is hidden, skip this point
                    continue;
                  }
                }
              }

              // what's the time of this data point?
              final HiResDate currentTime = thisSecondary.getTime();

              // / get the colour
              Color thisColor = thisSecondary.getColor();

              // when we have an interpolated point, we would have to use the previous
              // color
              if (thisSecondary instanceof FixWrapper)
              {
                final FixWrapper secondaryAsFixWrapper =
                    ((FixWrapper) thisSecondary);
                if (secondaryAsFixWrapper.isInterpolated())
                {
                  final Color actualColor = secondaryAsFixWrapper
                      .getActualColor();
                  if (actualColor == null && previousColor != null)
                  {
                    thisColor = previousColor;
                  }
                }
              }
              previousColor = thisColor;

              // produce the new calculated value
              final double thisVal = theCalculation.calculate(thisSecondary,
                  null, currentTime);

              // SPECIAL HANDLING - do we need to check if this
              // data passes
              // through 360 degs?
              if (theCalculation.isWrappableData())
              {
                // add extra points, if we need to
                connectToPrevious = insertWrappingPoints(lastSecondaryValue,
                    thisVal, lastTime, currentTime, thisColor, thisSeries,
                    connectToPrevious, provider, theAdder,
                    myOperation._clipMax);
              }

              // is this fix visible?
              if (thisSecondary.getVisible())
              {
                // the point on the primary track we work with
                final Watchable thisPrimary = null;

                // ok, check the segments. if the last segment was dynamic, and we're not,
                // use the last color. Do we know the previous fix?
                if (prevFix != null && thisSecondary instanceof FixWrapper)
                {
                  final FixWrapper fix = (FixWrapper) thisSecondary;
                  final TrackSegment thisSeg = fix.getSegment();
                  if (thisSeg != null)
                  {
                    if (prevFix instanceof FixWrapper)
                    {
                      final FixWrapper pFix = (FixWrapper) prevFix;
                      final TrackSegment prevSeg = pFix.getSegment();
                      // is it from a different segment?
                      if (!thisSeg.equals(prevSeg)
                          && (prevSeg instanceof DynamicInfillSegment
                              && !(thisSeg instanceof DynamicInfillSegment)))
                      {
                        // ok, use the previous color
                        thisColor = prevFix.getColor();
                      }
                    }
                  }
                }

                // and add the new data point (if we have to)
                connectToPrevious = createDataPoint(theCalculation, thisPrimary,
                    thisSecondary, currentTime, connectToPrevious, thisColor,
                    thisSeries, provider, theAdder);
                lastSecondaryValue = thisVal;
                lastTime = new HiResDate(currentTime);

                prevFix = thisSecondary;
              } // whether this point is visible
            } // stepping through this secondary collection
          } // whether there was time-related data for this track
        } // whether this was a relative calculation

        // if the series if empty, set it to null, rather than create
        // one of
        // empty length
        if (thisSeries instanceof XYSeries)
        {
          final XYSeries ser = (XYSeries) thisSeries;
          if (ser.getItemCount() == 0)
          {
            thisSeries = null;
          }
        }
        else if (thisSeries instanceof TimeSeries)
        {
          final TimeSeries ser = (TimeSeries) thisSeries;
          if (ser.getItemCount() == 0)
          {
            thisSeries = null;
          }
        }

        // did we find anything?
        if (thisSeries != null)
        {
          theAdder.addSeries(theSeriesCollection, thisSeries, seriesColor);
        }

      } // if this collection actually had data

    } // looping through the tracks

    if (theSeriesCollection.getSeriesCount() == 0)
    {
      theSeriesCollection = null;
    }

    return theSeriesCollection;
  }

  /**
   * method to decide if we need to insert extra (non-joined) points to reflect fact that data wraps
   * through 360 degs
   *
   * @param lastSecondaryValue
   *          the last value calculated
   * @param thisVal
   *          this calculated value
   * @param lastTime
   *          the time of the last calculation
   * @param currentTime
   *          the time of the current calculation
   * @param thisColor
   *          the colour of this data point
   * @param thisSeries
   *          the data series we add our new point(s) to
   * @param theAdder
   * @param clipMax
   * @param connectToPrevious
   *          whether the next data point should connect to these
   *
   * @return whether the next line segment should connect to this one
   */
  private static boolean insertWrappingPoints(final double lastSecondaryValue,
      final double thisVal, final HiResDate lastTime,
      final HiResDate currentTime, final Color thisColor,
      final Series thisSeries, final boolean connectToPrevious,
      final ColouredDataItem.OffsetProvider provider,
      final VersatileSeriesAdder theAdder, final double clipMax)
  {
    boolean connectToPrev = connectToPrevious;
    // is this the first point?
    if ((!Double.isNaN(lastSecondaryValue)))
    {

      final boolean beyondThreshold = Math.abs(thisVal
          - lastSecondaryValue) > COURSE_THRESHOLD;

      // how far is this from the previous value
      if (beyondThreshold)
      {
        // oooh, we've passed through zero since the last value. Create
        // two data
        // points
        // to simulate the data passing through zero degrees
        final double startCourse = lastSecondaryValue;
        final double endCourse = thisVal;
        final long startTime = lastTime.getMicros();
        final long endTime = currentTime.getMicros();

        long zeroTime;

        zeroTime = ShowTimeVariablePlot3.calcZeroCrossingTime(startTime,
            endTime, startCourse, endCourse, clipMax);

        // just check that zero time isn't equal to either of the ends.
        // if it
        // is, move it forward or back
        // one milli
        boolean shiftZeroUp = false;
        boolean shiftZeroDown = false;

        if (zeroTime == startTime)
        {
          shiftZeroUp = true;
        }

        if (zeroTime == endTime)
        {
          shiftZeroDown = true;
        }

        // ok insert the points at this time.

        // determine whether we go to zero or 360 first
        double firstCourse;
        double secondCourse;

        if (startCourse > clipMax - 180)
        {
          firstCourse = clipMax;
          secondCourse = clipMax - 360;
        }
        else
        {
          firstCourse = clipMax - 360;
          secondCourse = clipMax;
        }

        HiResDate firstDate = null;
        HiResDate secondDate = null;
        if (HiResDate.inHiResProcessingMode())
        {
          if (shiftZeroUp)
          {
            zeroTime += 2;
          }
          if (shiftZeroDown)
          {
            zeroTime -= 2;
          }

          // make it a microsecond either side
          firstDate = new HiResDate(0, zeroTime - 1);
          secondDate = new HiResDate(0, zeroTime + 1);
        }
        else
        {
          if (shiftZeroUp)
          {
            zeroTime += 2000;
          }
          if (shiftZeroDown)
          {
            zeroTime -= 2000;
          }

          // make it a millisecond either side
          final HiResDate zero = new HiResDate(0, zeroTime);
          firstDate = new HiResDate(zero.getDate().getTime() - 1);
          secondDate = new HiResDate(zero.getDate().getTime() + 1);
        }

        try
        {
          // now that we use the inverse of the firstPoint value
          // to
          // indicate whether to join the point to it's previous
          // one.
          theAdder.add(thisSeries, firstDate, firstCourse, thisColor, true,
              provider, false);

          // now that we use the inverse of the firstPoint value
          // to
          // indicate whether to join the point to it's previous
          // one.
          theAdder.add(thisSeries, secondDate, secondCourse, thisColor, false,
              provider, false);

          // right, we're about to cross zero degrees, don't connect
          // to
          // the next
          connectToPrev = true;

        }
        catch (final Exception e)
        {
          Trace.trace(
              "Failed to insert chart point (duplicate of previous point)",
              false);
        }

      }
    }
    return connectToPrev;
  }

  /**
   * create a simple series using the two data points
   *
   * @param theCalculation
   * @param thisPrimary
   * @param thisSecondary
   * @param thisSeries
   * @param start_time
   * @param end_time
   */
  private static void produceTwoPointDataSeries(
      final toteCalculation theCalculation, final Watchable thisPrimary,
      final Watchable thisSecondary, final Series thisSeries,
      final HiResDate start_time, final HiResDate end_time,
      final ColouredDataItem.OffsetProvider provider,
      final VersatileSeriesAdder theAdder)
  {

    // Note, we ignore the value of connect to previous, since we are
    // creating
    // the whole
    // series in this method

    // the colour of this track
    final Color thisColor = thisSecondary.getColor();

    // add the start point
    createDataPoint(theCalculation, thisPrimary, thisSecondary, start_time,
        false, thisColor, thisSeries, provider, theAdder);

    // add the end point
    createDataPoint(theCalculation, thisPrimary, thisSecondary, end_time, true,
        thisColor, thisSeries, provider, theAdder);
  }

  /**
   * the period this operation covers
   */
  private HiResDate _start_time = null;

  private HiResDate _end_time = null;

  /**
   * the tracks we should cover
   */
  private Vector<WatchableList> _theTracks = null;

  /**
   *
   */
  private WatchableList _preselectedPrimaryTrack;

  /**
   * the panel we put our graph into
   */
  private final MWC.GUI.Properties.PropertiesPanel _thePanel;

  /**
   * the operations we provide
   */
  private final Vector<CalculationHolder> _theOperations;

  /**
   * Option used for Debrief Lite. User selects the option before running the XY calculation.
   */
  private CalculationHolder _preselectedOperation;

  /**
   * store a local copy of the line separator
   */
  private final String _theSeparator = System.getProperties().getProperty(
      "line.separator");

  /**
   * the step control we want the plot to listen to
   */
  protected final Debrief.GUI.Tote.StepControl _theStepper;

  private StepperChartPanel _generatedChartPanel;

  private NewFormattedJFreeChart _generatedJFreeChart;

  private StepperXYPlot _generatedXYPlot;

  private MySwingPlot _generatedSwingPlot;

  // /////////////////////////////////////////////////
  // constructor
  // ////////////////////////////////////////////////
  public ShowTimeVariablePlot3(
      final MWC.GUI.Properties.PropertiesPanel thePanel,
      final Debrief.GUI.Tote.StepControl theStepper)
  {
    // remember the panel
    _thePanel = thePanel;

    // remember the stepper
    _theStepper = theStepper;

    _theOperations = new Vector<CalculationHolder>(0, 1);
    _theOperations.addElement(new CalculationHolder(new depthCalc(),
        new DepthFormatter(), false, 0));

    _theOperations.addElement(new CalculationHolder(new courseCalc(),
        new CourseFormatter(), false, 360));

    _theOperations.addElement(new CalculationHolder(new speedCalc(), null,
        false, 0));
    _theOperations.addElement(new CalculationHolder(new rangeCalc(), null, true,
        0));
    _theOperations.addElement(new CalculationHolder(new bearingCalc(), null,
        true, 360));
    _theOperations.addElement(new CalculationHolder(new bearingRateCalc(),
        new BearingRateFormatter(), true, 180));

    // provide extra formatting to the y-axis if we're plotting in uk format
    // (-180...+180).
    // but not for US format
    formattingOperation theFormatter = null;
    if (relBearingCalc.useUKFormat())
    {
      theFormatter = new RelBearingFormatter();
    }
    else
    {
      theFormatter = null;
    }

    // and add the relative bearing calcuation
    _theOperations.addElement(new CalculationHolder(new relBearingCalc(),
        theFormatter, true, 180));
    _theOperations.addElement(new CalculationHolder(new atbCalc(), theFormatter,
        true, 180));
  }

  @Override
  public final void actionPerformed(final java.awt.event.ActionEvent p1)
  {
  }

  @Override
  public final void close()
  {
  }

  @Override
  public final void execute()
  {
  }

  private CalculationHolder getChoice()
  {
    final Object[] opts = new Object[_theOperations.size()];
    _theOperations.copyInto(opts);
    final CalculationHolder res = (CalculationHolder) JOptionPane
        .showInputDialog(null, "Which operation?", "Plot time variables",
            JOptionPane.QUESTION_MESSAGE, null, opts, null);
    return res;
  }

  @Override
  public final MWC.GUI.Tools.Action getData()
  {
    // check that some tracks are selected
    if (_theTracks == null)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Reformat Tracks",
          "Please select one or more tracks");
      return null;
    }

    // find out what the user wants to view
    CalculationHolder theHolder = _preselectedOperation;
    if (theHolder == null)
    {
      theHolder = getChoice();
    }

    // check it worked
    if (theHolder != null)
    {
      // retrieve the necessary input data
      final toteCalculation myOperation = theHolder._theCalc;

      // declare the primary track (even though we may end up not using
      // it)
      WatchableList thePrimary = null;

      // is this a relative calculation?
      if (theHolder._isRelative)
      {
        thePrimary = _preselectedPrimaryTrack;

        if (thePrimary == null)
        {
          // retrieve the necessary input data
          thePrimary = getPrimary();
        }
      }

      // ////////////////////////////////////////////////
      // sort out the title
      // ////////////////////////////////////////////////
      // get the title to use
      String theTitle = myOperation.getTitle() + " vs Time plot";

      // is this a relative operation
      if (theHolder.isARelativeCalculation())
      {
        if (thePrimary != null)
        {
          // if it's relative, we use the primary track name in the
          // title
          theTitle = thePrimary.getName() + " " + theTitle;
        }
      }

      // ///////////////////////////////////////////////////////
      // prepare the plot
      // ///////////////////////////////////////////////////////

      // the working variables we rely on later
      NewFormattedJFreeChart jChart = null;
      XYPlot plot = null;
      ValueAxis xAxis = null;

      XYToolTipGenerator tooltipGenerator = null;

      // the y axis is common to hi & lo res. Format it here
      final NumberAxis yAxis = new NumberAxis(myOperation.getTitle() + " "
          + myOperation.getUnits());

      // hmm, see if we are in hi-res mode. If we are, don't use a
      // formatted
      // y-axis, just use the plain long microseconds
      // value
      if (HiResDate.inHiResProcessingMode())
      {

        // final SimpleDateFormat _secFormat = new SimpleDateFormat("ss");

        System.err.println(
            "XY Plot of HiRes data support is incomplete. Tick formatter (below) is missing.");

        // ok, simple enough for us...
        final NumberAxis nAxis = new NumberAxis("time (secs.micros)");
        // {
        // /**
        // *
        // */
        // private static final long serialVersionUID = 1L;
        //
        // @SuppressWarnings("unused")
        // public String getTickLabel(final double currentTickValue)
        // {
        // final long time = (long) currentTickValue;
        // final Date dtg = new HiResDate(0, time).getDate();
        // final String res = _secFormat.format(dtg) + "."
        // + DebriefFormatDateTime.formatMicros(new HiResDate(0, time));
        // return res;
        // }
        // };
        nAxis.setAutoRangeIncludesZero(false);
        xAxis = nAxis;

        // just show the raw data values
        tooltipGenerator = new StandardXYToolTipGenerator();
      }
      else
      {
        // create a date-formatting axis
        final DateAxis dAxis = new RelativeDateAxis();
        dAxis.setStandardTickUnits(DateAxisEditor
            .createStandardDateTickUnitsAsTickUnits());
        xAxis = dAxis;

        // also create the date-knowledgable tooltip writer
        tooltipGenerator = new DatedToolTipGenerator();
      }

      // create the special stepper plot
      final ColourStandardXYItemRenderer renderer =
          new ColourStandardXYItemRenderer(tooltipGenerator, null, null);
      plot = getPlot((RelativeDateAxis) xAxis, yAxis, _theStepper, renderer);
      _generatedXYPlot = (StepperXYPlot) plot;
      renderer.setPlot(plot);

      // apply any formatting for this choice
      final formattingOperation fo = theHolder._theFormatter;
      if (fo != null)
      {
        fo.format(plot);
      }

      _generatedJFreeChart = jChart = new NewFormattedJFreeChart(theTitle,
          JFreeChart.DEFAULT_TITLE_FONT, plot, true, _theStepper);

      // ////////////////////////////////////////////////////
      // get the data
      // ////////////////////////////////////////////////////
      final AbstractDataset theDataset = getDataSeries(thePrimary, theHolder,
          _theTracks, _start_time, _end_time, jChart.getTimeOffsetProvider());

      // ////////////////////////////////////////////////
      // put the holder into one of our special items
      // ////////////////////////////////////////////////
      final ChartPanel chartInPanel = new StepperChartPanel(jChart, true,
          _theStepper);

      _generatedChartPanel = (StepperChartPanel) chartInPanel;

      // format the chart
      chartInPanel.setName(theTitle);
      chartInPanel.setMouseZoomable(true, true);

      // and insert into the panel
      insertPanel(chartInPanel, jChart);

      // ////////////////////////////////////////////////////
      // put the time series into the plot
      // ////////////////////////////////////////////////////
      plot.setDataset((XYDataset) theDataset);

    } // whether we have a primary

    // return the new action
    return null;
  }

  @Override
  public final String getDescription()
  {
    final StringBuffer res = new StringBuffer("2. Select tracks to be plotted");
    res.append(_theSeparator);
    res.append("3. Press 'Apply' button");
    res.append(_theSeparator);
    res.append("4. Select which data parameter is to be plotted");
    res.append(_theSeparator);
    res.append(
        "5. Drag an area on the graph to zoom in, and press Fill to rescale");
    res.append(_theSeparator);

    return res.toString();
  }

  public StepperChartPanel getGeneratedChartPanel()
  {
    return _generatedChartPanel;
  }

  public NewFormattedJFreeChart getGeneratedJFreeChart()
  {
    return _generatedJFreeChart;
  }

  public MySwingPlot getGeneratedSwingPlot()
  {
    return _generatedSwingPlot;
  }

  public StepperXYPlot getGeneratedXYPlot()
  {
    return _generatedXYPlot;
  }

  @Override
  public final String getImage()
  {
    return null;
  }

  @Override
  public final String getLabel()
  {
    return "Show time variables";
  }

  private XYPlot getPlot(final RelativeDateAxis xAxis, final ValueAxis yAxis,
      final StepControl theStepper, final XYItemRenderer renderer)
  {
    return new StepperXYPlot(null, xAxis, yAxis, theStepper, renderer);
  }

  private WatchableList getPrimary()
  {
    WatchableList res = null;

    // check we have some tracks selected
    if (_theTracks != null)
    {
      final Object[] opts = new Object[_theTracks.size()];
      _theTracks.copyInto(opts);
      res = (WatchableList) JOptionPane.showInputDialog(null,
          "Which is the primary track?" + System.getProperty("line.separator")
              + "  (to be used as the subject of calculations)",
          "Show Time Variable Plot", JOptionPane.QUESTION_MESSAGE, null, opts,
          null);
    }
    else
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Track Selector",
          "Please select one or more tracks");
    }
    return res;
  }

  /**
   * create a properties panel and insert it
   *
   * @param panel
   *          the panel containing the plot
   * @param theXYPlot
   *          the plot itself
   */
  private void insertPanel(final JPanel panel,
      final NewFormattedJFreeChart theXYPlot)
  {
    // create the panel we are using
    if (_thePanel != null)
    {
      final SwingPlot mySwingPlot = new MySwingPlot(panel, _thePanel,
          theXYPlot);
      _generatedSwingPlot = (MySwingPlot) mySwingPlot;
      if (_thePanel instanceof MWC.GUI.Properties.Swing.SwingPropertiesPanel)
      {
        // just check if we are using one of our Swing-aware properties
        // panels,
        // in which case we will insert the
        // panel in a floating toolbar.

        final MWC.GUI.Properties.Swing.SwingPropertiesPanel sp =
            (MWC.GUI.Properties.Swing.SwingPropertiesPanel) _thePanel;
        sp.addThisPanel(mySwingPlot);
      }
      else
      {
        _thePanel.add(panel);
      }
    }
  }

  /**
   * the user has pressed RESET whilst this button is pressed
   *
   * @param startTime
   *          the new start time
   * @param endTime
   *          the new end time
   */
  @Override
  public void resetMe(final HiResDate startTime, final HiResDate endTime)
  {
  }

  @Override
  public final void setPeriod(final HiResDate startDTG,
      final HiResDate finishDTG)
  {
    _start_time = startDTG;
    _end_time = finishDTG;
  }

  public void setPreselectedOperation(
      final CalculationHolder _preselectedOperation)
  {
    this._preselectedOperation = _preselectedOperation;
  }

  public void setPreselectedPrimaryTrack(
      final WatchableList _preselectedPrimaryTrack)
  {
    this._preselectedPrimaryTrack = _preselectedPrimaryTrack;
  }

  @Override
  public final void setTracks(final Vector<WatchableList> selectedTracks)
  {
    _theTracks = selectedTracks;
  }
}