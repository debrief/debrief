package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.support.TestSupport;
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
	public static final String RANGE = "range";
	public static final String SOUND_SPEED = "speedSound";
	public static final String F_NOUGHT = "fNought";
	public static final String RANGE_BOUNDS = "rangeBounds";

	/**
	 * fNought for radiated source (Hz)
	 * 
	 */
	private double fNought = 150;

	/**
	 * speed of sound in water (kts)
	 * 
	 */
	private double speedSound = 3000;

	private Double calculatedRange;

	private transient Double calculatedMinRange;

	private transient Double calculatedMaxRange;

	private String rangeBounds;

	@Override
	protected double cumulativeScoreFor(CoreRoute route)
	{
		double min = this.calculatedMinRange == null ? 0 : this.calculatedMinRange;
		double max = this.calculatedMaxRange == null ? 0 : this.calculatedMaxRange;
		if (!isActive() || route.getType() == LegType.ALTERING
				|| calculatedRange == null || calculatedRange < min
				|| calculatedRange > max)
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

					double temp = distance - calculatedRange;
					sum += temp * temp;
					count++;
				}
			}
		}
		if (count == 0)
		{
			return 0;
		}
		double norm = Math.max(Math.abs(max - calculatedRange),
				Math.abs(min - calculatedRange));
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
		double range = calculateRange(rDotDotKts, bDot);

		// convert from kyds to m
		range = GeoSupport.yds2m(Math.abs(range) * 1000d);

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
		double minR = range - error;
		double maxR = range + error;

		// sanity check on minR
		minR = Math.max(minR, 100);

		// store these values
		setRange(range);
		setMinMaxRange(minR, maxR);

		// get the cut near the centre of the dataset
		FrequencyMeasurement measure = measurements.get(measurements.size() / 2);

		// NOTE: the following line is from when we applied a range forecast at
		// every point.
		// for (FrequencyMeasurement measure : measurements)
		// {
		// capture this time
		Date subjectTime = measure.getDate();

		// ok, get a locaiton
		final GeoPoint loc = measure.getLocation();
		final Point pt = loc.asPoint();

		// yes, ok we can centre our donut on that
		LinearRing outer = GeoSupport.geoRing(pt, maxR);
		LinearRing inner = GeoSupport.geoRing(pt, minR);
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
		BoundedState thisS = space.getBoundedStateAt(subjectTime);

		if (thisS == null)
		{
			// nope, better create it
			thisS = new BoundedState(subjectTime);
			space.add(thisS);
		}

		// apply the range
		thisS.constrainTo(myRa);
		// }  // end of looping through all frequency measurements
	}

	private double calcRDotKts(double rDotDotHz)
	{
		return rDotDotHz * speedSound / fNought;
	}

	private double calculateRange(double rDotDotKts, double bDot)
	{
		return 110.85 * rDotDotKts / Math.pow(bDot, 2);
	}

	private double calculateBearingRate(ProblemSpace space)
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

	private double calculateFreqRate()
	{
		double freq = 0;

		// put the frequencies into an array
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<Double> times = new ArrayList<Double>();

		Long firstTime = null;

		Iterator<FrequencyMeasurement> iter = measurements.iterator();
		while (iter.hasNext())
		{
			FrequencyMeasurement frequencyMeasurement = (FrequencyMeasurement) iter
					.next();
			values.add(frequencyMeasurement.getFrequency());
			long millis = frequencyMeasurement.getDate().getTime();

			if (firstTime == null)
			{
				firstTime = millis;
			}

			double mins = (millis - firstTime) / 1000d / 60;
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
	private double getSlope(ArrayList<Double> x_values, ArrayList<Double> y_values)
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
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	/**
	 * how many freq measuremnts we contain
	 * 
	 * @return
	 */
	public int size()
	{
		return measurements.size();
	}

	public int getNumObservations()
	{
		return measurements.size();
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

	/**
	 * whether this contribution has any measurements yet
	 * 
	 * @return
	 */
	private boolean hasData()
	{
		return measurements.size() > 0;
	}

	/**
	 * subject radiated freq (hz)
	 * 
	 * @return
	 */
	public double getfNought()
	{
		return fNought;
	}

	public void setfNought(double fNought)
	{
		this.fNought = fNought;
	}

	/**
	 * local speed of sounds (kts)
	 * 
	 * @return
	 */
	public double getSpeedSound()
	{
		return speedSound;
	}

	public void setSpeedSound(double speedSound)
	{
		this.speedSound = speedSound;
	}

	private void loadFrom(List<String> lines)
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

	// ///////////////////////////////////////

	protected static class Range1959ForecastContributionTest
	{

		private Range1959ForecastContribution _freq;

		@Before
		public void setUp() throws Exception
		{
			_freq = new Range1959ForecastContribution();
		}

		@SuppressWarnings("deprecation")
		@Test
		public void testLoadFromOne() throws Exception
		{
			assertFalse("should be empty", _freq.hasData());

			_freq.loadFrom(TestSupport.getFreqDataOne());
			assertTrue("should not be empty", _freq.hasData());
			assertEquals("correct start date", new Date(110, 00, 12, 13, 10, 00),
					_freq.getStartDate());
			assertEquals("correct finish date", new Date(110, 00, 12, 13, 20, 00),
					_freq.getFinishDate());

			// and apply it to a problem space
			double rDotDotHz = _freq.calculateFreqRate();
			assertEquals("correct freq", -0.0239, rDotDotHz, 0.0001);
			ProblemSpace space = new ProblemSpace();
			// and add the bearings
			addBearing(space, "100112", "131000", -99.53);
			addBearing(space, "100112", "131050", -97.18);
			addBearing(space, "100112", "131140", -94.8);
			addBearing(space, "100112", "131230", -92.42);
			addBearing(space, "100112", "131320", -90.04);
			addBearing(space, "100112", "131410", -87.66);
			addBearing(space, "100112", "131500", -85.3);
			addBearing(space, "100112", "131550", -82.96);
			addBearing(space, "100112", "131640", -80.65);
			addBearing(space, "100112", "131730", -78.37);
			addBearing(space, "100112", "131820", -76.14);
			addBearing(space, "100112", "131910", -73.95);
			addBearing(space, "100112", "132000", -71.82);

			double rDotDotKts = _freq.calcRDotKts(rDotDotHz);
			double bDot = _freq.calculateBearingRate(space);
			double range = _freq.calculateRange(rDotDotKts, bDot);
			assertEquals("correct rDotDotHz", -0.0239763, rDotDotHz, 0.000001);
			assertEquals("correct rDotDotKts", -0.47952, rDotDotKts, 0.00001);
			assertEquals("correct bDot", 2.7869, bDot, 0.00001);
			assertEquals("correct range", -6.8439, range, 0.001);

			// ok, now check that the new bound is generated
			_freq.actUpon(space);

			// check the new constraint is in there.
		}

		private void addBearing(ProblemSpace space, String date, String time,
				double bearingDegs) throws IncompatibleStateException
		{
			Date theDate = ObjectUtils.safeParseDate(new SimpleDateFormat(
					"yyMMdd HHmmss"), date + " " + time);

			BoundedState state = new BoundedState(theDate);
			state.setBearingValue(Math.toRadians(bearingDegs));
			space.add(state);
		}

		@SuppressWarnings("deprecation")
		@Test
		public void testLoadFromTwo() throws Exception
		{
			assertFalse("should be empty", _freq.hasData());

			_freq.loadFrom(TestSupport.getFreqDataTwo());
			assertTrue("should not be empty", _freq.hasData());
			assertEquals("correct start date", new Date(110, 00, 12, 13, 35, 00),
					_freq.getStartDate());
			assertEquals("correct finish date", new Date(110, 00, 12, 13, 40, 00),
					_freq.getFinishDate());

			// and apply it to a problem space
			double rDotDotHz = _freq.calculateFreqRate();
			ProblemSpace space = new ProblemSpace();
			// and add the bearings
			addBearing(space, "100112", "133500", -48.41);
			addBearing(space, "100112", "133550", -47.79);
			addBearing(space, "100112", "133640", -47.19);
			addBearing(space, "100112", "133730", -46.6);
			addBearing(space, "100112", "133820", -46.03);
			addBearing(space, "100112", "133910", -45.48);
			addBearing(space, "100112", "134000", -44.94);
			addBearing(space, "100112", "134050", -44.42);

			double rDotDotKts = _freq.calcRDotKts(rDotDotHz);
			double bDot = _freq.calculateBearingRate(space);
			double range = _freq.calculateRange(rDotDotKts, bDot);
			assertEquals("correct freq", -0.001943, rDotDotHz, 0.00001);
			assertEquals("correct rDotDotKts", -0.03887, rDotDotKts, 0.0001);
			assertEquals("correct bDot", 0.69385, bDot, 0.00001);
			assertEquals("correct range", -8.95088, range, 0.00001);
		}

	}

	/**
	 * get the calculated range (m)
	 * 
	 * @return
	 */
	public Double getRange()
	{
		return calculatedRange;
	}

	public void setRange(Double range)
	{
		Double oldRange = calculatedRange;

		this.calculatedRange = range;

		firePropertyChange(RANGE, oldRange, range);
		firePropertyChange(HARD_CONSTRAINTS, oldRange, range);
	}

	public void setMinMaxRange(Double minR, Double maxR)
	{
		this.calculatedMinRange = minR;
		this.calculatedMaxRange = maxR;

		// ok, now put the range into a string value
		StringBuffer buffer = new StringBuffer();
		if (calculatedMinRange != null)
		{
			buffer.append(new Integer(calculatedMinRange.intValue()).toString());
		}
		buffer.append("-");
		if (calculatedMaxRange != null)
		{
			buffer.append(new Integer(calculatedMaxRange.intValue()).toString());
		}
		String value = buffer.toString();
		if (!"-".equals(value))
		{
			setRangeBounds(value);
		}
	}

	public String getRangeBounds()
	{
		return rangeBounds;
	}

	public void setRangeBounds(String rangeBounds)
	{
		String oldRange = this.rangeBounds;
		this.rangeBounds = rangeBounds;

		firePropertyChange(RANGE_BOUNDS, oldRange, rangeBounds);
		firePropertyChange(HARD_CONSTRAINTS, oldRange, rangeBounds);

	}
}
