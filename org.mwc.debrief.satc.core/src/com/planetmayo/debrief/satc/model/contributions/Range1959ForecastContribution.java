package com.planetmayo.debrief.satc.model.contributions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution.ROrigin;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class Range1959ForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	private ArrayList<FrequencyMeasurement> measurements = new ArrayList<FrequencyMeasurement>();
	public static final String OBSERVATIONS_NUMBER = "numObservations";

	private double _fNought = 150;
	private double _C = 3000;

	private transient Double _range;

	private transient Double _minR;

	private transient Double _maxR;

	public Range1959ForecastContribution()
	{
	}

	public void dummyActUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{

		for (BoundedState state : space.getBoundedStatesBetween(startDate,
				finishDate))
		{

			// ok, get a locaiton
			final GeoPoint loc = measurements.get(0).getLocation();
			final Point pt = loc.asPoint();

			// yes, ok we can centre our donut on that
			LinearRing outer = GeoSupport.geoRing(pt, 12000);
			LinearRing inner = GeoSupport.geoRing(pt, 600);
			LinearRing[] holes;

			// did we generate an inner?
			if (inner == null)
			{
				// nope = provide empty inner
				holes = null;
			}
			else
			{
				holes = new LinearRing[]
				{ inner };
			}

			// and create a polygon for it.
			Polygon thePoly = GeoSupport.getFactory().createPolygon(outer, holes);

			// GeoSupport.writeGeometry("rng_" + ctr, thePoly);

			// create a LocationRange for the poly
			// now define the polygon
			final LocationRange myRa = new LocationRange(thePoly);

			// is there already a bounded state at this time?
			BoundedState thisS = space.getBoundedStateAt(state.getTime());

			if (thisS == null)
			{
				// nope, better create it
				thisS = new BoundedState(state.getTime());
				space.add(thisS);
			}

			// apply the range
			thisS.constrainTo(myRa);
		}
	}

	@Override
	protected double cumulativeScoreFor(CoreRoute route)
	{
		double min = this._minR == null ? 0 : this._minR;
		double max = this._maxR == null ? 0 : this._maxR;
		if (!isActive() || route.getType() == LegType.ALTERING || _range == null
				|| _range < min || _range > max)
		{
			return 0;
		}
		double sum = 0;
		int count = 0;
		for (FrequencyMeasurement origin : measurements)
		{
			Date currentDate = origin.getDate();
			if (currentDate.compareTo(route.getStartTime()) >= 0
					&& currentDate.compareTo(route.getEndTime()) <= 0)
			{
				State state = route.getStateAt(currentDate);
				// check that the route has a state at this time (since we may not
				// be generating states for all of our measurements)
				if (state != null)
				{
					Point location = state.getLocation();
					GeodeticCalculator calculator = GeoSupport.createCalculator();
					calculator.setStartingGeographicPoint(location.getX(),
							location.getY());
					calculator.setDestinationGeographicPoint(origin.getLocation()
							.getLon(), origin.getLocation().getLat());
					double distance = calculator.getOrthodromicDistance();

					double temp = distance - _range;
					sum += temp * temp;
					count++;
				}
			}
		}
		if (count == 0)
		{
			return 0;
		}
		double norm = Math.max(Math.abs(max - _range), Math.abs(min - _range));
		return Math.sqrt(sum / count) / norm;
	}

	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// calculate rDotDotHz
		double rDotDotHz = calculateFreqRate();

		// calculate rDotDotKts
		double rDotDotKts = calcRDotKts(rDotDotHz);

		// calculate bearing rate
		double bDot = calculateBearingRate(space);

		// calculate range
		_range = calculateRange(rDotDotKts, bDot);

		// convert from kyds to m
		_range = GeoSupport.yds2m(Math.abs(_range) * 1000d);

		// calculate range error
		final double error;
		if (bDot < 3)
		{
			error = 10000;
		}
		else if (bDot < 5)
		{
			error = 5000;
		}
		else if (bDot < 8)
		{
			error = 3000;
		}
		else
		{
			error = 1000;
		}

		// calculate min/max ranges
		_minR = _range - error;
		_maxR = _range + error;

		// sanity check on minR
		_minR = Math.max(_minR, 100);

		for (BoundedState state : space.getBoundedStatesBetween(startDate,
				finishDate))
		{

			// ok, get a locaiton
			final GeoPoint loc = measurements.get(0).getLocation();
			final Point pt = loc.asPoint();

			// yes, ok we can centre our donut on that
			LinearRing outer = GeoSupport.geoRing(pt, _maxR);
			LinearRing inner = GeoSupport.geoRing(pt, _minR);
			LinearRing[] holes;

			// did we generate an inner?
			if (inner == null)
			{
				// nope = provide empty inner
				holes = null;
			}
			else
			{
				holes = new LinearRing[]
				{ inner };
			}

			// and create a polygon for it.
			Polygon thePoly = GeoSupport.getFactory().createPolygon(outer, holes);

			// GeoSupport.writeGeometry("rng_" + ctr, thePoly);

			// create a LocationRange for the poly
			// now define the polygon
			final LocationRange myRa = new LocationRange(thePoly);

			// is there already a bounded state at this time?
			BoundedState thisS = space.getBoundedStateAt(state.getTime());

			if (thisS == null)
			{
				// nope, better create it
				thisS = new BoundedState(state.getTime());
				space.add(thisS);
			}

			// apply the range
			thisS.constrainTo(myRa);
		}

	}

	public FrequencyMeasurement getMidWayPoint()
	{
		long midTime = this.getStartDate().getTime()
				+ (this.getFinishDate().getTime() - this.getStartDate().getTime()) / 2;

		Iterator<FrequencyMeasurement> iter = measurements.iterator();
		long bestDiff = -1;
		FrequencyMeasurement bestM = null;
		while (iter.hasNext())
		{
			FrequencyMeasurement thisM = (FrequencyMeasurement) iter.next();
			long thisDiff = Math.abs(thisM.getDate().getTime() - midTime);

			if (bestM == null)
			{
				// ok, this is the first one
				bestM = thisM;
				bestDiff = thisDiff;
			}
			else
			{
				// test it
				if (thisDiff < bestDiff)
				{
					bestDiff = thisDiff;
					bestM = thisM;
				}
				else
				{
					// ok, we've passed the centre - drop out
					break;
				}
			}
		}

		return bestM;
	}

	private void applyConstraint(ProblemSpace space, Point pt, double minRange,
			double maxRange, Date time) throws IncompatibleStateException
	{
		// yes, ok we can centre our donut on that
		LinearRing outer = getRing(pt, minRange);
		LinearRing inner = getRing(pt, maxRange);
		LinearRing[] holes = new LinearRing[]
		{ inner };

		// and create a polygon for it.
		Polygon thePoly = GeoSupport.getFactory().createPolygon(outer, holes);

		System.out.println("1959:" + thePoly);

		// create a LocationRange for the poly
		// now define the polygon
		final LocationRange myRa = new LocationRange(thePoly);

		// is there already a bounded state at this time?
		BoundedState thisS = space.getBoundedStateAt(time);

		if (thisS == null)
		{
			// nope, better create it
			thisS = new BoundedState(time);
			space.add(thisS);
		}

		// apply the range
		thisS.constrainTo(myRa);
	}

	private LinearRing getRing(Point pt, double rangeM)
	{
		// ok, now we create the outer circle
		return GeoSupport.geoRing(pt, rangeM);
	}

	public double calcRDotKts(double rDotDotHz)
	{
		return rDotDotHz * _C / _fNought;
	}

	public double calculateRange(double rDotDotKts, double bDot)
	{
		return 110.85 * rDotDotKts / Math.pow(bDot, 2);
	}

	public double calculateBearingRate(ProblemSpace space)
	{
		// get the states for our time period
		// get a few bounded states
		Collection<BoundedState> testStates = space.getBoundedStatesBetween(
				this.getStartDate(), this.getFinishDate());
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<Double> times = new ArrayList<Double>();
		for (Iterator<BoundedState> iterator = testStates.iterator(); iterator
				.hasNext();)
		{
			BoundedState boundedState = iterator.next();
			// does it have a bearing?
			if (boundedState.hasBearing())
			{
				values.add(boundedState.getBearing());
				long millis = boundedState.getTime().getTime();
				double mins = millis / 1000 / 60d;
				times.add(mins);
			}
		}

		double bDotRads = getSlope(times, values);

		// convert the bearing rate to degs (our formula needs it
		return Math.toDegrees(bDotRads);
	}

	public double calculateFreqRate()
	{
		double freq = 0;

		// put the frequencies into an array
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<Double> times = new ArrayList<Double>();

		Iterator<FrequencyMeasurement> iter = measurements.iterator();
		while (iter.hasNext())
		{
			FrequencyMeasurement frequencyMeasurement = (FrequencyMeasurement) iter
					.next();
			values.add(frequencyMeasurement.getFrequency());
			long millis = frequencyMeasurement.getDate().getTime();
			double mins = millis / 1000 / 60;
			times.add(mins);
		}

		freq = getSlope(times, values);

		return freq;
	}

	/**
	 * calculate the gradient, using least squares
	 * 
	 * @param x_values
	 * @param y_values
	 * @return
	 * @throws Exception
	 */
	public double getSlope(ArrayList<Double> x_values, ArrayList<Double> y_values)
			throws IllegalArgumentException
	{

		if (x_values.size() != y_values.size())
			throw new IllegalArgumentException("The arrays aren't the same lenth");

		SimpleRegression sr = new SimpleRegression();
		for (int i = 0; i < x_values.size(); i++)
		{
			double x = x_values.get(i);
			double y = y_values.get(i);
			sr.addData(x, y);
		}

		return sr.getSlope();

	}

	@Override
	protected double calcError(State thisState)
	{
		double delta = 0;

		return delta;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public void clear()
	{
		measurements.clear();
		super.setStartDate(null);
		super.setFinishDate(null);
	}

	/**
	 * add this new measurement
	 * 
	 * @param measure
	 */
	public void addMeasurement(FrequencyMeasurement measure)
	{
		// extend the time period accordingly
		final Date theTime = measure.getDate();
		if (this.getStartDate() == null)
		{
			this.setStartDate(theTime);
			this.setFinishDate(theTime);
		}
		else
		{
			long newTime = theTime.getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(theTime);
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(theTime);
		}
		measurements.add(measure);
		firePropertyChange(OBSERVATIONS_NUMBER, measurements.size(),
				measurements.size());
	}

	public int size()
	{
		return measurements.size();
	}

	/**
	 * whether this contribution has any measurements yet
	 * 
	 * @return
	 */
	public boolean hasData()
	{
		return measurements.size() > 0;
	}

	public double getfNought()
	{
		return _fNought;
	}

	public void setfNought(double fNought)
	{
		this._fNought = fNought;
	}

	public void loadFrom(List<String> lines)
	{
		// load from this source
		// ;SENSOR2: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B
		// CCC.C
		// FFF.F RRRR yy..yy xx..xx
		// ;; date, ownship name, symbology, sensor lat/long (or the single word
		// NULL),
		// bearing (degs) [or the single word NULL], ambigous bearing (degs) [or the
		// single word NULL], frequency(Hz) [or the single word NULL], range(yds)
		// [or the single word NULL], sensor name, label (to end of line)

		// ;;IGNORE YYMMDD HHMMSS IGNORE IGNORE LAT_DEG LAT_MIN LAT_SEC LAT_HEM
		// LONG_DEG LONG_MIN LONG_SEC LONG_HEM BEARING MAX_RNG
		// ;SENSOR: 100112 121329 SENSOR @A 0 3 57.38 S 30 0 8.65 W 1.5 15000

		// Read File Line By Line
		for (String strLine : lines)
		{
			// hey, is this a comment line?
			if (strLine.startsWith(";;"))
			{
				continue;
			}
			// ok, get parseing it
			String[] elements = strLine.split("\\s+");

			// now the date
			String date = elements[1];

			// and the time
			String time = elements[2];

			String latDegs = elements[5];
			String latMins = elements[6];
			String latSecs = elements[7];
			String latHemi = elements[8];

			String lonDegs = elements[9];
			String lonMins = elements[10];
			String lonSecs = elements[11];
			String lonHemi = elements[12];

			// // and the beraing
			// String bearing = elements[13];
			// String ambigBearing = elements[14];

			// and the range
			String freq = elements[15];

			// ok,now construct the date=time
			Date theDate = ObjectUtils.safeParseDate(new SimpleDateFormat(
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

			GeoPoint theLoc = new GeoPoint(lat, lon);
			// double angle = Math.toRadians(Double.parseDouble(bearing));

			FrequencyMeasurement measure = new FrequencyMeasurement(theDate, theLoc,
					Double.parseDouble(freq));

			addMeasurement(measure);

		}
	}
}
