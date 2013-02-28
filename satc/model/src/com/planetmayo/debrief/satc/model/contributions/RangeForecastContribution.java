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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class RangeForecastContribution extends BaseContribution
{

	private static final long serialVersionUID = 1L;

	public static final String MIN_RANGE = "minRange";

	public static final String MAX_RANGE = "maxRange";

	/**
	 * for UI components, this is the maximum range that a user can select
	 * 
	 */
	public static final double MAX_SELECTABLE_RANGE_M = 10000;

	private static final double ABSOLUTELY_HUGE_RANGE_DEGS = 2;

	protected Double _minRangeM = 0d;

	protected Double _maxRangeM = 0d;

	protected Double _estimate = 0d;

	/**
	 * the set of measurements we store
	 * 
	 */
	private ArrayList<ROrigin> _measurements = new ArrayList<ROrigin>();

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{

		// loop through our measurements
		Iterator<ROrigin> iter = _measurements.iterator();
		while (iter.hasNext())
		{
			RangeForecastContribution.ROrigin origin = iter.next();

			Date thisT = origin._time;

			boolean useIt = true;
			// is it in time?
			if (super.getStartDate() != null)
			{
				if (super.getStartDate().after(thisT))
				{
					useIt = false;
				}
			}
			if (super.getFinishDate() != null)
			{
				if (super.getFinishDate().before(thisT))
				{
					useIt = false;
				}
			}

			if (!useIt)
				continue;

			Point pt = origin._origin.asPoint();

			// yes, ok we can centre our donut on that
			LinearRing outer = getOuterRing(pt);
			LinearRing inner = getInnerRing(pt);
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

		//	GeoSupport.writeGeometry("rng_" + ctr, thePoly);
			
			// create a LocationRange for the poly
			// now define the polygon
			final LocationRange myRa = new LocationRange(thePoly);

			// is there already a bounded state at this time?
			BoundedState thisS = space.getBoundedStateAt(origin._time);

			if (thisS == null)
			{
				// nope, better create it
				thisS = new BoundedState(origin._time);
				space.add(thisS);
			}

			// apply the range
			thisS.constrainTo(myRa);
		}
	}

	/**
	 * store this new measurement
	 * 
	 * @param measure
	 */
	public void addThis(ROrigin measure)
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

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate()
	{
		return _estimate;
	}

	private LinearRing getInnerRing(Point pt)
	{
		final LinearRing res;
		double theRange;

		// yes, ok we have an inner ring
		theRange = GeoSupport.m2deg(getMinRange());

		// do we have an inner range?
		if (theRange == 0d)
		{
			// no, ok, just choose an absolutely monster range
			res = null;
		}
		else
		{
			// ok, now we create the inner circle
			res = (LinearRing) pt.buffer(theRange).getBoundary();

		}

		return res;
	}

	public Double getMaxRange()
	{
		return _maxRangeM;
	}

	public Double getMinRange()
	{
		return _minRangeM;
	}

	private LinearRing getOuterRing(Point pt)
	{
		// do we have a max range?
		double theRange;

		// yes, ok we have an outer ring
		theRange = GeoSupport.m2deg(getMaxRange());

		if (theRange == 0d)
		{
			// no, ok, just choose an absolutely monster range
			theRange = ABSOLUTELY_HUGE_RANGE_DEGS;
		}

		// ok, now we create the inner circle
		Geometry geom = pt.buffer(theRange);
		LinearRing res = (LinearRing) geom.getBoundary();
		return res;
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
			@SuppressWarnings("unused")
			String bearing = elements[13];

			// and the range
			@SuppressWarnings("unused")
			String range = elements[14];

			// ok,now construct the date=time
			Date theDate = SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", date
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
			ROrigin measure = new ROrigin(theLoc, theDate);

			addThis(measure);
		}

		// give us some max/min data
		this.setMaxRange(9000d);
		this.setMinRange(5d);
		this.setEstimate(4000d);

		// TODO: set the start/end times = just for tidiness
	}

	public void setEstimate(Double estimate)
	{
		Double oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxRange(Double maxRngM)
	{
		Double oldMaxRange = _maxRangeM;
		this._maxRangeM = maxRngM;
		firePropertyChange(MAX_RANGE, oldMaxRange, maxRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldMaxRange, maxRngM);
	}

	public void setMinRange(Double minRngM)
	{
		Double oldMinRange = _minRangeM;
		this._minRangeM = minRngM;
		firePropertyChange(MIN_RANGE, oldMinRange, minRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldMinRange, minRngM);
	}
	
	/**
	 * utility class for storing a measurement
	 * 
	 * @author ian
	 * 
	 */
	public static class ROrigin
	{
		private final GeoPoint _origin;
		private final Date _time;

		public ROrigin(GeoPoint loc, Date time)
		{
			_origin = loc;
			_time = time;
		}
	}
}
