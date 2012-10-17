package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class BearingMeasurementContribution extends BaseContribution
{
	/**
	 * utility class for storing a measurement
	 * 
	 * @author ian
	 * 
	 */
	public static class BMeasurement
	{
		private final GeoPoint _origin;
		private final double _bearingDegs;
		private final Date _time;
		/**
		 * the (optional) maximum range for this measurement
		 * 
		 */
		private final Double _theRange;

		public BMeasurement(GeoPoint loc, double bearing, Date time, Double theRange)
		{
			_origin = loc;
			_bearingDegs = bearing;
			_time = time;
			_theRange = theRange;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final String BEARING_ERROR = "bearingError";

	/**
	 * utility method to create one of these contributions
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BearingMeasurementContribution getSample()
	{
		BearingMeasurementContribution res = new BearingMeasurementContribution();
		res.setName("Approaching Buoy");
		res.setActive(true);
		res.setWeight(7);

		// add a couple of bearings
		res.addEstimate(12.2, 12.3, new Date(2012, 3, 3, 12, 2, 2), 55, 1200);
		res.addEstimate(12.3, 12.32, new Date(2012, 3, 3, 12, 3, 2), 58, 1200);
		res.addEstimate(12.3, 12.33, new Date(2012, 3, 3, 12, 4, 2), 60, 1200);
		res.addEstimate(12.4, 12.34, new Date(2012, 3, 3, 12, 5, 2), 62, 1200);

		return res;
	}

	/**
	 * the allowable bearing error (in degrees)
	 * 
	 */
	private double _degError = 0;

	/**
	 * the set of measurements we store
	 * 
	 */
	private ArrayList<BMeasurement> _measurements = new ArrayList<BMeasurement>();

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// ok, here we really go for it!
		Iterator<BMeasurement> iter = _measurements.iterator();

		// sort out the bearing error
		double errorRads = _degError / 180 * Math.PI;

		// sort out a geometry factory
		GeometryFactory factory = GeoSupport.getFactory();

		while (iter.hasNext())
		{
			BearingMeasurementContribution.BMeasurement measurement = iter.next();
			// ok, create the polygon for this measurement
			GeoPoint origin = measurement._origin;
			double bearing = measurement._bearingDegs;
			double bearingRads = bearing / 180 * Math.PI;
			double range = measurement._theRange;

			// ok, generate the polygon
			Coordinate[] coords = new Coordinate[5];

			// start off with the origin
			final double lon = origin.getLon();
			final double lat = origin.getLat();

			coords[0] = new Coordinate(lon, lat);

			// now the top-left
			coords[1] = new Coordinate(lon + Math.sin(bearingRads - errorRads)
					* range, lat + Math.cos(bearingRads - errorRads) * range);

			// now the centre bearing
			coords[2] = new Coordinate(lon + Math.sin(bearingRads) * range, lat
					+ Math.cos(bearingRads) * range);

			// now the top-right
			coords[3] = new Coordinate(lon + Math.sin(bearingRads + errorRads)
					* range, lat + Math.cos(bearingRads + errorRads) * range);

			// and back to the start
			coords[4] = new Coordinate(coords[0]);

			CoordinateArraySequence seq = new CoordinateArraySequence(coords);

			// and construct the bounded location object
			LinearRing ls = new LinearRing(seq, factory);
			Polygon poly = new Polygon(ls, null, factory);
			LocationRange lr = new LocationRange(poly);

			// do we have a bounds at this time?
			BoundedState thisState = space.getBoundedStateAt(measurement._time);
			if (thisState == null)
			{
				// ok, do the bounds
				thisState = new BoundedState(measurement._time);
				// and store it
				space.add(thisState);
			}

			// well, if we didn't - we do now! Apply it!
			thisState.constrainTo(lr);
		}
	}

	public void addEstimate(double lat, double lon, Date date, double brg,
			double range)
	{
		GeoPoint loc = new GeoPoint(lat, lon);
		BMeasurement measure = new BMeasurement(loc, brg, date, range);
		addThis(measure);
		firePropertyChange(ESTIMATE, _measurements.size(), _measurements.size());
	}

	/**
	 * store this new measurement
	 * 
	 * @param measure
	 */
	public void addThis(BMeasurement measure)
	{
		// extend the time period accordingly
		if (this.getStartDate() == null)
		{
			this.setStartDate(measure._time);
			this.setFinishDate(measure._time);
		}
		else
		{
			long newTime = measure._time.getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure._time);
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure._time);
		}

		_measurements.add(measure);
	}

	public double getBearingError()
	{
		return _degError;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.MEASUREMENT;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + _measurements.size() + " measurements " + (int) _degError
				+ " Margin";
	}
	
	

	public Object getEstimate()
	{
		return null;
	}

	/**
	 * whether this contribution has any measurements yet
	 * 
	 * @return
	 */
	public boolean hasData()
	{
		return _measurements.size() > 0;
	}

	public void loadFrom(List<String> lines)
	{
		// load from this source
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

			// and the beraing
			String bearing = elements[13];

			// and the range
			String range = elements[14];

			// ok,now construct the date=time
			Date theDate = SupportServices.INSTANCE.parseDate("yyMMdd hhmmss", date
					+ " " + time);

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
			BMeasurement measure = new BMeasurement(theLoc, Double.valueOf(bearing),
					theDate, GeoSupport.m2deg(Double.valueOf(range)));

			addThis(measure);

		}
		this.setBearingError(3d);
		// TODO: set the start/end times = just for tidiness
	}

	public void setBearingError(double errorDegs)
	{
		String oldConstraints = getHardConstraints();
		this._degError = errorDegs;
		firePropertyChange(BEARING_ERROR, errorDegs, errorDegs);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}
}
