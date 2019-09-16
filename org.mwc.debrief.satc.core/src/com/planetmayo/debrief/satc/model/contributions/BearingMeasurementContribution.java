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
package com.planetmayo.debrief.satc.model.contributions;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.jfree.data.statistics.Regression;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.views.BaseStackedDotsView;
import org.mwc.debrief.track_shift.views.StackedDotHelper;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.target.ILegStorer;
import org.mwc.debrief.track_shift.zig_detector.target.IZigStorer;
import org.mwc.debrief.track_shift.zig_detector.target.ZigDetector;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.planetmayo.debrief.satc.zigdetector.LegOfData;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public class BearingMeasurementContribution extends
    CoreMeasurementContribution<BearingMeasurementContribution.BMeasurement>
{
  public static class AssumptionsTest extends TestCase
  {
    public void testOLS()
    {
      final double[][] data =
      {
          {0.5, 3},
          {1.5, 2.5},
          {3, 1},
          {3.5, 0.5}};
      final double[] res = Regression.getOLSRegression(data);
      final double gradient = res[1];
      final double intercept = res[0];
      assertEquals("correctly identified gradient", -0.8, gradient, 0.1);
      assertEquals("correctly identified intercept", 3.5, intercept, 0.1);
    }
  }

  /**
   * utility class for storing a measurement
   *
   * @author ian
   *
   */
  public static class BMeasurement extends
      CoreMeasurementContribution.CoreMeasurement
  {
    private static final double MAX_RANGE_METERS =
        RangeForecastContribution.MAX_SELECTABLE_RANGE_M;
    private final GeoPoint origin;
    private final double bearingAngle;
    /**
     * the (optional) maximum range for this measurement
     *
     */
    private final double range;

    public BMeasurement(final GeoPoint loc, final double bearing,
        final Date time, final Double range)
    {
      super(time);
      this.origin = loc;
      this.bearingAngle = MathUtils.normalizeAngle(bearing);

      // tidying up. Give the maximum possible range for this bearing if the
      // data is missing
      this.range = range == null ? MAX_RANGE_METERS : range;
    }

    public double getBearingRads()
    {
      return bearingAngle;
    }

  }

  public static class HostState
  {
    final public long time;
    final public double courseDegs;
    final public double speedKts;
    final public double dLat;
    final public double dLong;

    public HostState(final long time, final double courseDegs,
        final double speedKts, final double dLat, final double dLong)
    {
      this.time = time;
      this.courseDegs = courseDegs;
      this.speedKts = speedKts;
      this.dLat = dLat;
      this.dLong = dLong;
    }
  }

  public static interface MDAResultsListener
  {
    public void ownshipLegs(String contName, ArrayList<BMeasurement> bearings,
        List<LegOfData> ownshipLegs, ArrayList<HostState> hostStates);

    public void sliced(String contName,
        ArrayList<StraightLegForecastContribution> arrayList);

    public void startingSlice(String contName);
  }

  private static class MyLegStorer extends MyStorer implements ILegStorer
  {

    public MyLegStorer(final IContributions theConts,
        final ArrayList<BMeasurement> cuts, final String genName)
    {
      super(theConts, cuts, genName);
    }
  }

  private static class MyStorer
  {
    int ctr = 1;
    protected ArrayList<StraightLegForecastContribution> slices =
        new ArrayList<StraightLegForecastContribution>();
    protected final IContributions _contributions;
    protected final ArrayList<BMeasurement> _cuts;
    protected final String _genName;

    public MyStorer(final IContributions theConts,
        final ArrayList<BMeasurement> cuts, final String genName)
    {
      _contributions = theConts;
      _cuts = cuts;
      _genName = genName;
    }

    private Color colorAt(final Date date)
    {
      Color res = null;
      final Iterator<BMeasurement> iter = _cuts.iterator();
      while (iter.hasNext())
      {
        final BearingMeasurementContribution.BMeasurement measurement = iter
            .next();

        // check if it's on or after the supplied date
        if (!measurement.getDate().before(date))
        {
          res = measurement.getColor();
        }
      }
      return res;
    }

    public ArrayList<StraightLegForecastContribution> getSlices()
    {
      return slices;
    }

    public void storeLeg(final String scenarioName, final long tStart,
        final long tEnd, final double rms)
    {
      final String name = "Tgt-" + ctr++;

      SATC_Activator.log(IStatus.INFO, " FOUND LEG FROM " + new Date(tStart)
          + " - " + new Date(tEnd), null);

      final StraightLegForecastContribution slf =
          new CompositeStraightLegForecastContribution();
      slf.setStartDate(new Date(tStart));
      slf.setAutoGenBy(_genName);
      slf.setFinishDate(new Date(tEnd));
      slf.setColor(colorAt(slf.getStartDate()));
      slf.setActive(true);
      slf.setName(name);
      if (_contributions != null)
      {
        _contributions.addContribution(slf);
      }
      slices.add(slf);
    }
  }

  private static class MyZigStorer extends MyStorer implements IZigStorer
  {

    private long _startTime;
    private final long _endTime;

    public MyZigStorer(final IContributions theConts,
        final ArrayList<BMeasurement> cuts, final String genName,
        final long startTime, final long endTime)
    {
      super(theConts, cuts, genName);
      _startTime = startTime;
      _endTime = endTime;
    }

    @Override
    public void finish()
    {
      // ok, just check if there is a missing last leg
      if (_startTime != Long.MIN_VALUE)
      {
        // ok, append the last leg
        storeLeg(null, _startTime, _endTime, 0);
        _startTime = Long.MIN_VALUE;
      }
    }

    @Override
    public ArrayList<StraightLegForecastContribution> getSlices()
    {
      finish();

      return super.getSlices();
    }

    @Override
    public void storeZig(final String scenarioName, final long tStart,
        final long tEnd, final double rms)
    {
      storeLeg(scenarioName, _startTime, tStart, rms);

      // and move foward the end time
      _startTime = tEnd;
    }
  }

  private static final long serialVersionUID = 1L;

  public static final String BEARING_ERROR = "bearingError";

  public static final String RUN_MDA = "autoDetect";

  /**
   * the allowable bearing error (in radians)
   *
   */
  private Double bearingError = 0d;

  /**
   * flag for whether this contribution should run an MDA on the data
   *
   */
  private boolean runMDA = true;

  /**
   * store the ownship states, if possible. We use this to run the manoeuvre detection algorithm
   */
  private ArrayList<HostState> states;

  /**
   * array of listeners interested in MDA
   *
   */
  private transient ArrayList<MDAResultsListener> _listeners = null;

  /**
   * store any sliced legs
   *
   */
  private transient List<LegOfData> ownshipLegs;

  @Override
  public void actUpon(final ProblemSpace space)
      throws IncompatibleStateException
  {
    // ok, here we really go for it!
    final Iterator<BMeasurement> iter = measurements.iterator();

    // sort out a geometry factory
    final GeometryFactory factory = GeoSupport.getFactory();

    while (iter.hasNext())
    {
      final BearingMeasurementContribution.BMeasurement measurement = iter
          .next();

      // is it active?
      if (measurement.isActive())
      {
        // ok, create the polygon for this measurement
        final GeoPoint origin = measurement.origin;
        final double bearing = measurement.bearingAngle;
        final double range = measurement.range;

        // sort out the left/right edges
        final double leftEdge = bearing - bearingError;
        final double rightEdge = bearing + bearingError;

        // ok, generate the polygon
        final Coordinate[] coords = new Coordinate[5];

        // start off with the origin
        final double lon = origin.getLon();
        final double lat = origin.getLat();

        coords[0] = new Coordinate(lon, lat);

        // create a utility object to help with calcs
        final GeodeticCalculator calc = GeoSupport.createCalculator();

        // now the top-left
        calc.setStartingGeographicPoint(new Point2D.Double(lon, lat));
        calc.setDirection(Math.toDegrees(MathUtils.normalizeAngle2(leftEdge)),
            range);
        Point2D dest = calc.getDestinationGeographicPoint();
        coords[1] = new Coordinate(dest.getX(), dest.getY());

        // now the centre bearing
        calc.setStartingGeographicPoint(new Point2D.Double(lon, lat));
        calc.setDirection(Math.toDegrees(MathUtils.normalizeAngle2(bearing)),
            range);
        dest = calc.getDestinationGeographicPoint();
        coords[2] = new Coordinate(dest.getX(), dest.getY());

        // now the top-right
        calc.setStartingGeographicPoint(new Point2D.Double(lon, lat));
        calc.setDirection(Math.toDegrees(MathUtils.normalizeAngle2(rightEdge)),
            range);
        dest = calc.getDestinationGeographicPoint();
        coords[3] = new Coordinate(dest.getX(), dest.getY());

        // and back to the start
        coords[4] = new Coordinate(coords[0]);

        // ok, store the coordinates
        final CoordinateArraySequence seq = new CoordinateArraySequence(coords);

        // and construct the bounded location object
        final LinearRing ls = new LinearRing(seq, factory);
        final Polygon poly = new Polygon(ls, null, factory);
        final LocationRange lr = new LocationRange(poly);

        // do we have a bounds at this time?
        BoundedState thisState = space.getBoundedStateAt(measurement.time);
        if (thisState == null)
        {
          // ok, do the bounds
          thisState = new BoundedState(measurement.time);
          // and store it
          space.add(thisState);
        }

        // ok, override any existing color for this state, if we have one
        if (measurement.getColor() != null)
          thisState.setColor(measurement.getColor());

        // well, if we didn't - we do now! Apply it!
        thisState.constrainTo(lr);

        final LineString bearingLine = GeoSupport.getFactory().createLineString(
            new Coordinate[]
            {coords[0], coords[2]});
        thisState.setBearingLine(bearingLine);

        // also store the bearing value in the state - since it's of value in
        // other processes (1959)
        thisState.setBearingValue(bearing);
      }
    }

    // hmm, do we run the MDA?
    if (getAutoDetect())
    {
      // get a few bounded states
      final Collection<BoundedState> testStates = space.getBoundedStatesBetween(
          this.getStartDate(), this.getFinishDate());
      int ctr = 0;
      for (final Iterator<BoundedState> iterator = testStates
          .iterator(); iterator.hasNext();)
      {
        final BoundedState boundedState = iterator.next();
        ctr++;
        if (ctr >= 0 && ctr <= 3)
        {
          boundedState.setMemberOf("test MDA leg");
        }
      }
    }
  }

  public void addMeasurement(final double lat, final double lon,
      final Date date, final double brg, final double range)
  {
    final GeoPoint loc = new GeoPoint(lat, lon);
    final BMeasurement measure = new BMeasurement(loc, brg, date, range);
    addMeasurement(measure);
  }

  public void addSliceListener(final MDAResultsListener listener)
  {
    if (_listeners == null)
      _listeners = new ArrayList<MDAResultsListener>();

    _listeners.add(listener);
  }

  public void addState(final HostState newState)
  {
    // check we have our states
    if (states == null)
      states = new ArrayList<HostState>();

    // and store this new one
    states.add(newState);
  }

  private double bearingRateFor(final List<Long> legTimes,
      final List<Double> legBearings)
  {
    // how large is the data?
    final int len = legTimes.size();

    // keep track of the max/min bearings
    double minBrg = Double.MAX_VALUE;
    double maxBrg = Double.MIN_NORMAL;

    final long timeStart = legTimes.get(0);

    // store the data
    final double[][] data = new double[len][2];
    for (int i = 0; i < len; i++)
    {
      data[i][0] = (legTimes.get(i) - timeStart) / (1000 * 60d); // convert to mins
      final Double thisBrg = legBearings.get(i);
      if (thisBrg < minBrg)
        minBrg = thisBrg;
      if (thisBrg > maxBrg)
        maxBrg = thisBrg;
      data[i][1] = thisBrg;
    }

    // ok, we need to check that we don't pass through zero in this data.
    if (maxBrg - minBrg > 180)
    {
      // ok, we need to put them into the same cycle (180..540)
      for (int i = 0; i < len; i++)
      {
        double thisVal = data[i][1];
        if (thisVal < 180)
        {
          thisVal += 360;
          data[i][1] = thisVal;
        }
      }
    }

    // calculate the line
    final double[] res = Regression.getOLSRegression(data);

    // and the rate
    return res[1];
  }

  private boolean checkForTargetZig(final String legName,
      final List<Long> lastLegTimes, final List<Double> lastLegBearings,
      final List<Long> thisLegTimes, final List<Double> thisLegBearings)
  {
    boolean res = false;
    // ok, what's the 1936 range?

    final NumberFormat dp2 = new DecimalFormat("0.00");

    // ok, trim the leg 1 bearings
    final int leg1Len = Math.min(6, lastLegTimes.size());
    final int leg2Len = Math.min(6, thisLegTimes.size());

    // drop out if either are too small
    if ((leg1Len >= 3) && (leg2Len >= 3))
    {

      // OSA 1
      final double leg1Bearing = lastLegBearings.get(lastLegBearings.size()
          - 1);
      final double leg1Speed = speedAt(lastLegTimes.get(lastLegTimes.size()
          - 1));
      final double leg1Course = courseAt(lastLegTimes.get(lastLegTimes.size()
          - 1));
      final double leg1RelBrg = leg1Course - leg1Bearing;
      final double osa1 = leg1Speed * Math.sin(Math.toRadians(leg1RelBrg));

      // OSA 2
      final double leg2Bearing = thisLegBearings.get(0);
      final double leg2Speed = speedAt(thisLegTimes.get(0));
      final double leg2Course = courseAt(thisLegTimes.get(0));
      final double leg2RelBrg = leg2Course - leg2Bearing;
      final double osa2 = leg2Speed * Math.sin(Math.toRadians(leg2RelBrg));

      // dOSA
      final double dOSA = osa2 - osa1;

      // bearing rate
      final double l1BearingRate = bearingRateFor(lastLegTimes.subList(
          lastLegTimes.size() - leg1Len, lastLegTimes.size() - 1),
          lastLegBearings.subList(lastLegTimes.size() - leg1Len, lastLegTimes
              .size() - 1));
      final double l2BearingRate = bearingRateFor(thisLegTimes.subList(0,
          leg2Len), thisLegBearings.subList(0, leg2Len));
      final double deltaBRate = l1BearingRate - l2BearingRate; // (order changed, according to Iain
                                                               // doc)

      // and the range
      final double rng1936m = 1770.28 * dOSA / deltaBRate;

      // ok, what's the bearing rate for this range?
      // double pBoot = 1770.28 * dOSA / rng1936m;

      // hmm, what are the two TSAs?
      final double l1RSA = (l1BearingRate * rng1936m) / 1770.28;
      final double l1TSA = osa1 - l1RSA;

      final double l2RSA = (l2BearingRate * rng1936m) / 1770.28;
      final double l2TSA = osa2 - l2RSA;

      // ok, make a decision - we need a dTSA of less than the threshold,
      // with a +ve range estimate there to be no zig.
      final double deltaTSA = Math.abs(Math.abs(l2TSA) - Math.abs(l1TSA));
      final double deltaTSA_Threshold = 4;
      if ((deltaTSA <= deltaTSA_Threshold) && (rng1936m > 0))
        res = false;
      else
        res = true;

      // ok, output diagnostics
      SATC_Activator.log(IStatus.INFO, "turning onto:" + legName + " range:"
          + (int) rng1936m + " osa1:" + dp2.format(osa1) + " osa2:" + dp2
              .format(osa2) + " dOSA:" + dp2.format(dOSA) + " l1Rate:" + dp2
                  .format(l1BearingRate) + " l2Rate:" + dp2.format(
                      l2BearingRate) + " dRate:" + dp2.format(deltaBRate)
          + " l1TSA:" + dp2.format(l1TSA) + " l2TSA:" + dp2.format(l2TSA)
          + " is Zig:" + res, null);

    }

    return res;
  }

  private double courseAt(final long time)
  {
    double res = -1;
    final Iterator<HostState> iter = states.iterator();
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.HostState hostState = iter.next();
      if (hostState.time >= time)
      {
        res = hostState.courseDegs;
        break;
      }
    }
    return res;
  }

  @Override
  protected double cumulativeScoreFor(final CoreRoute route)
  {
    final double bearingError = this.bearingError == null ? 0
        : this.bearingError;
    if (!isActive() || route.getType() == LegType.ALTERING || bearingError == 0)
    {
      return 0;
    }
    double res = 0;
    int count = 0;
    for (final BMeasurement measurement : measurements)
    {
      final Date dateMeasurement = measurement.getDate();
      if (dateMeasurement.compareTo(route.getStartTime()) >= 0
          && dateMeasurement.compareTo(route.getEndTime()) <= 0)
      {
        final State state = route.getStateAt(dateMeasurement);
        if (state != null && state.getLocation() != null)
        {
          final GeodeticCalculator calculator = GeoSupport.createCalculator();
          calculator.setStartingGeographicPoint(measurement.origin.getLon(),
              measurement.origin.getLat());
          calculator.setDestinationGeographicPoint(state.getLocation().getX(),
              state.getLocation().getY());

          final double radians = MathUtils.normalizeAngle(Math.toRadians(
              calculator.getAzimuth()));
          double angleDiff = MathUtils.angleDiff(measurement.bearingAngle,
              radians, true);

          // make the error a proportion of the bearing error
          angleDiff = angleDiff / (this.getBearingError());

          // store the error
          state.setScore(this, angleDiff * this.getWeight() / 10);

          // and prepare the cumulative score
          final double thisError = angleDiff * angleDiff;
          res += thisError;
          count++;
        }
      }
    }
    if (count > 0)
    {
      res = Math.sqrt(res / count) / bearingError;
    }
    return res;
  }

  public void ditchExistingStraightLegContributions(
      final IContributions contributions)
  {
    final Iterator<BaseContribution> ditchIter = contributions.iterator();
    final ArrayList<StraightLegForecastContribution> toRemove =
        new ArrayList<StraightLegForecastContribution>();
    while (ditchIter.hasNext())
    {
      final BaseContribution baseContribution = ditchIter.next();
      if (baseContribution instanceof StraightLegForecastContribution)
      {
        final StraightLegForecastContribution sfl =
            (StraightLegForecastContribution) baseContribution;
        if (sfl.getAutoGenBy().equals(getName()))
        {
          toRemove.add(sfl);
        }
      }
    }

    // ditch any that we did find
    final Iterator<StraightLegForecastContribution> remover = toRemove
        .iterator();
    while (remover.hasNext())
    {
      final StraightLegForecastContribution toDitch = remover.next();
      contributions.removeContribution(toDitch);
    }
  }

  public boolean getAutoDetect()
  {
    return runMDA;
  }

  /**
   * get the bearing error
   *
   * @param errorRads
   *          (in radians)
   */
  public Double getBearingError()
  {
    return bearingError;
  }

  protected double[] getCourses()
  {
    final double[] res = new double[states.size()];
    final Iterator<HostState> iter = states.iterator();
    int ctr = 0;
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.HostState hostState = iter.next();
      res[ctr++] = hostState.courseDegs;
    }
    return res;
  }

  public List<HostState> getHostState()
  {
    return states;
  }

  /**
   * collate a list of time stamped course values
   *
   * @return
   */
  private TimeSeries getOwnshipCourses()
  {
    final TimeSeries res = new TimeSeries("OS_Courses");

    final Iterator<HostState> iter = states.iterator();
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.HostState hostState = iter.next();
      res.add(new TimeSeriesDataItem(new FixedMillisecond(hostState.time),
          hostState.courseDegs));
    }
    return res;
  }

  public List<LegOfData> getOwnshipLegs()
  {
    return ownshipLegs;
  }

  protected double[] getSpeeds()
  {
    final double[] res = new double[states.size()];
    final Iterator<HostState> iter = states.iterator();
    int ctr = 0;
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.HostState hostState = iter.next();
      res[ctr++] = hostState.speedKts;
    }
    return res;
  }

  protected long[] getTimes()
  {
    final long[] res = new long[states.size()];
    int ctr = 0;
    final Iterator<HostState> iter = states.iterator();
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.HostState hostState = iter.next();
      res[ctr++] = hostState.time;
    }
    return res;
  }

  public void loadFrom(final List<String> lines)
  {
    // load from this source
    // ;;IGNORE YYMMDD HHMMSS IGNORE IGNORE LAT_DEG LAT_MIN LAT_SEC LAT_HEM
    // LONG_DEG LONG_MIN LONG_SEC LONG_HEM BEARING MAX_RNG
    // ;SENSOR: 100112 121329 SENSOR @A 0 3 57.38 S 30 0 8.65 W 1.5 15000

    // Read File Line By Line
    for (final String strLine : lines)
    {
      // hey, is this a comment line?
      if (strLine.startsWith(";;"))
      {
        continue;
      }
      // ok, get parseing it
      final String[] elements = strLine.split("\\s+");

      // now the date
      final String date = elements[1];

      // and the time
      final String time = elements[2];

      final String latDegs = elements[5];
      final String latMins = elements[6];
      final String latSecs = elements[7];
      final String latHemi = elements[8];

      final String lonDegs = elements[9];
      final String lonMins = elements[10];
      final String lonSecs = elements[11];
      final String lonHemi = elements[12];

      // and the beraing
      final String bearing = elements[13];

      // and the range
      final String range = elements[14];

      // ok,now construct the date=time
      final Date theDate = ObjectUtils.safeParseDate(new GMTDateFormat(
          "yyMMdd HHmmss"), date + " " + time);

      // and the location
      double lat = Double.valueOf(latDegs) + Double.valueOf(latMins) / 60d
          + Double.valueOf(latSecs) / 60d / 60d;
      if (latHemi.toUpperCase().equals("S"))
        lat = -lat;
      double lon = Double.valueOf(lonDegs) + Double.valueOf(lonMins) / 60d
          + Double.valueOf(lonSecs) / 60d / 60d;
      if (lonHemi.toUpperCase().equals("W"))
        lon = -lon;

      final GeoPoint theLoc = new GeoPoint(lat, lon);
      final double angle = Math.toRadians(Double.parseDouble(bearing));
      final BMeasurement measure = new BMeasurement(theLoc, angle, theDate,
          Double.parseDouble(range));

      addMeasurement(measure);

    }
    this.setBearingError(Math.toRadians(3d));
  }

  public void removeSliceListener(final MDAResultsListener listener)
  {
    if (_listeners != null)
      _listeners.remove(listener);
  }

  public void runMDA(final IContributions contributions)
  {
    // ok, we've got to find the ownship data, somehow :-(
    if ((states == null) || (states.size() == 0))
    {
      return;
    }

    // decide if we are going to split at ownship and target zigs, or just
    // target zigs
    final boolean justTargetZigs = false;

    // ok, now ditch any straight leg contributions that we generated
    ditchExistingStraightLegContributions(contributions);

    // create object that can store the new legs
    IContributions zigConts, legConts;
    if (justTargetZigs)
    {
      zigConts = contributions;
      legConts = null;
    }
    else
    {
      legConts = contributions;
      zigConts = null;
    }

    final MyLegStorer legStorer = new MyLegStorer(legConts, this
        .getMeasurements(), this.getName());
    final MyZigStorer zigStorer = new MyZigStorer(zigConts, this
        .getMeasurements(), this.getName(), states.get(0).time, states.get(
            states.size() - 1).time);

    // ok, now collate the bearing data
    final ZigDetector detector = new ZigDetector();

    // whether we break up bearing data in blocks of ownship course
    // or not. In 2019, the algorithm is working better when it
    // just receives all of the bearing ata in one chunk
    final boolean sliceByOwnship = false;

    if (sliceByOwnship)
    {
      // first slice the bearing data into ownship legs, then identify
      // target legs within these chunks
      sliceByOwnshipLegs(legStorer, zigStorer, detector);
    }
    else
    {
      // just pass all the data to the algorithm as one big chunk
      sliceAllData(legStorer, zigStorer, detector);
    }

    // ok, slicing done!
    if (_listeners != null)
    {
      final Iterator<MDAResultsListener> iter = _listeners.iterator();
      while (iter.hasNext())
      {
        final BearingMeasurementContribution.MDAResultsListener thisL = iter
            .next();

        if (justTargetZigs)
        {
          thisL.sliced(this.getName(), zigStorer.getSlices());
        }
        else
        {
          thisL.sliced(this.getName(), legStorer.getSlices());
        }
      }
    }
  }

  public void setAutoDetect(final boolean onAuto)
  {
    final boolean previous = runMDA;
    runMDA = onAuto;
    firePropertyChange(RUN_MDA, previous, onAuto);
    firePropertyChange(HARD_CONSTRAINTS, previous, onAuto);

  }

  /**
   * provide the bearing error
   *
   * @return (in radians)
   */
  public void setBearingError(final Double errorRads)
  {

    // IDIOT CHECK - CHECK WE HAVEN'T ACCIDENTALLY GOT DEGREES
    if (errorRads > 2)
      SATC_Activator.log(IStatus.WARNING,
          "Looks like error is being presented in Degs", null);

    final Double old = bearingError;
    this.bearingError = errorRads;
    firePropertyChange(BEARING_ERROR, old, errorRads);
    fireHardConstraintsChange();
  }

  public void sliceAllData(final MyLegStorer legStorer,
      final MyZigStorer zigStorer, final ZigDetector detector)
  {
    final List<Long> thisLegTimes = new ArrayList<Long>();
    final List<Double> thisLegBearings = new ArrayList<Double>();
    final ArrayList<BMeasurement> meas = getMeasurements();
    final Iterator<BMeasurement> iter = meas.iterator();
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.BMeasurement measurement = iter
          .next();
      thisLegTimes.add(measurement.getDate().getTime());
      thisLegBearings.add(Math.toDegrees(measurement.bearingAngle));
    }

    final double zigTolerance = 0.000001;

    final ISolversManager solversManager = SATC_Activator.getDefault()
        .getService(ISolversManager.class, false);
    final Precision precision;
    if (solversManager != null)
    {
      precision = solversManager.getActiveSolver().getPrecision();
    }
    else
    {
      precision = Precision.MEDIUM;
    }
    final double zigScore = BaseStackedDotsView.getPrecision(precision);

    detector.sliceThis2(SATC_Activator.getDefault().getLog(),
        SATC_Activator.PLUGIN_ID, "some name", legStorer, zigScore,
        zigTolerance, thisLegTimes, thisLegBearings);
  }

  public void sliceByOwnshipLegs(final MyLegStorer legStorer,
      final MyZigStorer zigStorer, final ZigDetector detector)
  {
    // get ready to remember the previous leg
    List<Long> lastLegTimes = null;
    List<Double> lastLegBearings = null;

    //

    // ok, work through the legs. In the absence of a Discrete
    // Optimisation algorithm we're taking a brute force approach.
    // Hopefully we can find an optimised alternative to this.
    for (final Iterator<LegOfData> iterator2 = ownshipLegs.iterator(); iterator2
        .hasNext();)
    {
      final LegOfData thisLeg = iterator2.next();

      // ok, slice the data for this leg
      long legStart = thisLeg.getStart();
      long legEnd = thisLeg.getEnd();

      // trim the start/end to the sensor data
      legStart = Math.max(legStart, getStartDate().getTime());
      legEnd = Math.min(legEnd, getFinishDate().getTime());

      final List<Long> thisLegTimes = new ArrayList<Long>();
      final List<Double> thisLegBearings = new ArrayList<Double>();
      final ArrayList<BMeasurement> meas = getMeasurements();
      final Iterator<BMeasurement> iter = meas.iterator();
      while (iter.hasNext())
      {
        final BearingMeasurementContribution.BMeasurement measurement = iter
            .next();
        final long thisTime = measurement.getDate().getTime();
        if ((thisTime >= legStart) && (thisTime <= legEnd))
        {
          thisLegTimes.add(measurement.getDate().getTime());
          thisLegBearings.add(Math.toDegrees(measurement.bearingAngle));
        }
      }

      // ok, before we slice this leg, let's just try to see if there was
      // probably a target zig during the
      // ownship zig
      if (lastLegTimes != null)
      {
        final boolean probWasZig = checkForTargetZig(thisLeg.getName(),
            lastLegTimes, lastLegBearings, thisLegTimes, thisLegBearings);

        if (probWasZig)
        {
          // inject a target leg for the period spanning the ownship manouvre
          final long tStart = lastLegTimes.get(lastLegTimes.size() - 1);
          final long tEnd = thisLegTimes.get(0);
          zigStorer.storeZig("some name", tStart, tEnd, 0);
        }
      }

      final double zigTolerance = 0.000001;

      final ISolversManager solversManager = SATC_Activator.getDefault()
          .getService(ISolversManager.class, false);
      final Precision precision;
      if (solversManager != null)
      {
        precision = solversManager.getActiveSolver().getPrecision();
      }
      else
      {
        precision = Precision.MEDIUM;
      }
      final double zigScore = BaseStackedDotsView.getPrecision(precision);

      detector.sliceThis2(SATC_Activator.getDefault().getLog(),
          SATC_Activator.PLUGIN_ID, "some name", legStorer, zigScore,
          zigTolerance, thisLegTimes, thisLegBearings);

      lastLegTimes = thisLegTimes;
      lastLegBearings = thisLegBearings;
    }
  }

  public void sliceOwnship(final IContributions contributions)
  {
    // ok share the good news - we're about to start
    if (_listeners != null)
    {
      final Iterator<MDAResultsListener> iter = _listeners.iterator();
      while (iter.hasNext())
      {
        final BearingMeasurementContribution.MDAResultsListener thisL = iter
            .next();
        thisL.startingSlice(this.getName());
      }
    }

    final TimeSeries ownshipCourseSeries = getOwnshipCourses();
    final ColorProvider blueProv = new ZoneChart.ColorProvider()
    {
      @Override
      public java.awt.Color getZoneColor()
      {
        return java.awt.Color.blue;
      }
    };

    final ArrayList<Zone> slicedZones = StackedDotHelper.sliceOwnship(
        ownshipCourseSeries, blueProv);

    if (ownshipLegs != null)
    {
      ownshipLegs.clear();
    }
    else
    {
      ownshipLegs = new ArrayList<LegOfData>();
    }

    int ctr = 1;

    for (final Zone zone : slicedZones)
    {
      final long startT = zone.getStart();
      final long endT = zone.getEnd();
      final LegOfData newLeg = new LegOfData("Leg-" + ctr++, startT, endT);
      System.out.println("Leg:" + newLeg);
      ownshipLegs.add(newLeg);
    }

    // ok, share the ownship legs
    // ok, slicing done!
    if (_listeners != null)
    {
      final Iterator<MDAResultsListener> iter = _listeners.iterator();
      while (iter.hasNext())
      {
        final BearingMeasurementContribution.MDAResultsListener thisL = iter
            .next();
        thisL.ownshipLegs(this.getName(), this.getMeasurements(), ownshipLegs,
            states);
      }
    }
  }

  private double speedAt(final long time)
  {
    double res = -1;
    final Iterator<HostState> iter = states.iterator();
    while (iter.hasNext())
    {
      final BearingMeasurementContribution.HostState hostState = iter.next();
      if (hostState.time >= time)
      {
        res = hostState.speedKts;
        break;
      }
    }
    return res;
  }

}
