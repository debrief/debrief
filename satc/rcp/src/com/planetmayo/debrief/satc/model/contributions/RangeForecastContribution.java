package com.planetmayo.debrief.satc.model.contributions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
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
	public static final double MAX_SELECTABLE_RANGE_M = 40000;

	protected Double minRangeM = 0d;

	protected Double maxRangeM = 0d;

	protected Double estimate = 0d;

	/**
	 * the set of measurements we store
	 * 
	 */
	private ArrayList<ROrigin> sensorOrigins = new ArrayList<ROrigin>();

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{

		// just double-check that we have some sensor origins
		if (sensorOrigins.size() == 0)
			throw new IncompatibleStateException(
					"Range Forecast doesn't know vehicle origins", null, null);

		// loop through our sensor origins
		Iterator<ROrigin> iter = sensorOrigins.iterator();
		while (iter.hasNext())
		{
			RangeForecastContribution.ROrigin origin = iter.next();

			Date thisT = origin.time;

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

			Point pt = origin.origin.asPoint();

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

			// GeoSupport.writeGeometry("rng_" + ctr, thePoly);

			// create a LocationRange for the poly
			// now define the polygon
			final LocationRange myRa = new LocationRange(thePoly);

			// is there already a bounded state at this time?
			BoundedState thisS = space.getBoundedStateAt(origin.time);

			if (thisS == null)
			{
				// nope, better create it
				thisS = new BoundedState(origin.time);
				space.add(thisS);
			}

			// apply the range
			thisS.constrainTo(myRa);
		}
	}

	@Override
	protected double cumulativeScoreFor(CoreRoute route)
	{
		double min = this.minRangeM == null ? 0 : this.minRangeM;
		double max = this.maxRangeM == null ? 0 : this.maxRangeM;
		if (!isActive() || route.getType() == LegType.ALTERING || estimate == null
				|| estimate < min || estimate > max)
		{
			return 0;
		}
		double sum = 0;
		int count = 0;
		for (ROrigin origin : sensorOrigins)
		{
			Date currentDate = origin.time;
			if (currentDate.compareTo(route.getStartTime()) >= 0
					&& currentDate.compareTo(route.getEndTime()) <= 0)
			{
				State state = route.getStateAt(currentDate);
				Point location = state.getLocation();
				GeodeticCalculator calculator = new GeodeticCalculator();
				calculator.setStartingGeographicPoint(location.getX(), location.getY());
				calculator.setDestinationGeographicPoint(origin.origin.getLon(),
						origin.origin.getLat());
				double distance = calculator.getOrthodromicDistance();

				double temp = distance - estimate;
				sum += temp * temp;
				count++;
			}
		}
		if (count == 0) 
		{
			return 0;
		}
		double norm = Math.max(Math.abs(max - estimate), Math.abs(min - estimate));
		return Math.sqrt(sum / count) / norm;
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
			this.setStartDate(measure.time);
			this.setFinishDate(measure.time);
		}
		else
		{
			long newTime = measure.time.getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure.time);
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure.time);
		}

		sensorOrigins.add(measure);
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate()
	{
		return estimate;
	}

	private LinearRing getInnerRing(Point pt)
	{
		double range = getMinRange();

		// do we have an inner range?
		if (range == 0d)
		{
			// no, ok, just choose an absolutely monster range
			return null;
		}
		return GeoSupport.geoRing(pt, range);
	}

	public Double getMaxRange()
	{
		return maxRangeM;
	}

	public Double getMinRange()
	{
		return minRangeM;
	}

	private LinearRing getOuterRing(Point pt)
	{
		// do we have a max range?
		double range = getMaxRange();
		if (range == 0d)
		{
			// no, ok, just choose an absolutely monster range
			range = MAX_SELECTABLE_RANGE_M;
		}

		// ok, now we create the outer circle
		return GeoSupport.geoRing(pt, range);
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
			ROrigin measure = new ROrigin(theLoc, theDate);

			addThis(measure);
		}

		// give us some max/min data
		this.setMaxRange(9000d);
		this.setMinRange(5d);
		this.setEstimate(4000d);

		// TODO: set the start/end times = just for tidiness
	}

	public void setEstimate(Double newEstimate)
	{
		Double oldEstimate = estimate;
		this.estimate = newEstimate;
		firePropertyChange(ESTIMATE, oldEstimate, newEstimate);
	}

	public void setMaxRange(Double maxRngM)
	{
		Double oldMaxRange = maxRangeM;
		this.maxRangeM = maxRngM;
		firePropertyChange(MAX_RANGE, oldMaxRange, maxRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldMaxRange, maxRngM);
	}

	public void setMinRange(Double minRngM)
	{
		Double oldMinRange = minRangeM;
		this.minRangeM = minRngM;
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
		private final GeoPoint origin;
		private final Date time;

		public ROrigin(GeoPoint loc, Date time)
		{
			this.origin = loc;
			this.time = time;
		}
	}
}
