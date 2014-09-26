package com.planetmayo.debrief.satc.model.contributions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.ObjectUtils;

public class Range1959ForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	private ArrayList<FrequencyMeasurement> measurements = new ArrayList<FrequencyMeasurement>();
	public static final String OBSERVATIONS_NUMBER = "numObservations";

	private double _fNought = 150;
	private double _C = 3000;

	public Range1959ForecastContribution()
	{
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// calculate rDotDotHz
		double rDotDotHz = calculateFreqRate();

		// calculate rDotDotKts
		double rDotDotKts = rDotDotHz * _C / _fNought;

		// calculate bearing rate
		double bDot = calculateBearingRate(space);

		// calculate range

		// calculate range error

		// calculate min/max ranges

		// bound state for range bracket

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
			if(boundedState.hasBearing())
			{
				values.add(boundedState.getBearing());
				long millis = boundedState.getTime().getTime();
				double mins = millis / 1000 / 60d;
				times.add(mins);
			}
		}

		return getSlope(times, values);
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
			long millis = frequencyMeasurement.getTime().getTime();
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
		return ContributionDataType.MEASUREMENT;
	}

	/**
	 * add this new measurement
	 * 
	 * @param measure
	 */
	public void addMeasurement(FrequencyMeasurement measure)
	{
		// extend the time period accordingly
		if (this.getStartDate() == null)
		{
			this.setStartDate(measure.getTime());
			this.setFinishDate(measure.getTime());
		}
		else
		{
			long newTime = measure.getTime().getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure.getTime());
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure.getTime());
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

			// String latDegs = elements[5];
			// String latMins = elements[6];
			// String latSecs = elements[7];
			// String latHemi = elements[8];
			//
			// String lonDegs = elements[9];
			// String lonMins = elements[10];
			// String lonSecs = elements[11];
			// String lonHemi = elements[12];
			//
			// // and the beraing
			// String bearing = elements[13];
			// String ambigBearing = elements[14];

			// and the range
			String freq = elements[15];

			// ok,now construct the date=time
			Date theDate = ObjectUtils.safeParseDate(new SimpleDateFormat(
					"yyMMdd HHmmss"), date + " " + time);

			// and the location
			// double lat = Double.valueOf(latDegs) + Double.valueOf(latMins) / 60d
			// + Double.valueOf(latSecs) / 60d / 60d;
			// if (latHemi.toUpperCase().equals("S"))
			// lat = -lat;
			// double lon = Double.valueOf(lonDegs) + Double.valueOf(lonMins) / 60d
			// + Double.valueOf(lonSecs) / 60d / 60d;
			// if (lonHemi.toUpperCase().equals("W"))
			// lon = -lon;
			//
			// GeoPoint theLoc = new GeoPoint(lat, lon);
			// double angle = Math.toRadians(Double.parseDouble(bearing));

			FrequencyMeasurement measure = new FrequencyMeasurement(theDate,
					Double.parseDouble(freq));

			addMeasurement(measure);

		}
	}
}
