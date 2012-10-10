package com.planetmayo.debrief.satc.model.contributions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
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

	/** for UI components, this is the maximum range that a user can select
	 * 
	 */
	public static final int MAX_SELECTABLE_RANGE_M = 10000;

	private static double ABSOLUTELY_HUGE_RANGE_DEGS = 2;

	/**
	 * utility class for storing a measurement
	 * 
	 * @author ian
	 * 
	 */
	private static class ROrigin
	{
		private final GeoPoint _origin;
		private final Date _time;

		public ROrigin(GeoPoint loc, Date time)
		{
			_origin = loc;
			_time = time;
		}
	}

	/**
	 * utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static BaseContribution getSample()
	{
		BaseContribution res = new RangeForecastContribution();
		res.setActive(true);
		res.setWeight(4);
		res.setName("Easterly Leg");
		res.setStartDate(new Date(111111000));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		return res;
	}

	protected double _minRangeM;

	protected double _maxRangeM;

	protected double _estimate;

	/**
	 * the set of measurements we store
	 * 
	 */
	private ArrayList<ROrigin> _measurements = new ArrayList<ROrigin>();

	public void loadFrom(InputStream fstream)
	{
		// load from this source
		// ;;IGNORE YYMMDD HHMMSS IGNORE IGNORE LAT_DEG LAT_MIN LAT_SEC LAT_HEM
		// LONG_DEG LONG_MIN LONG_SEC LONG_HEM BEARING MAX_RNG
		// ;SENSOR: 100112 121329 SENSOR @A 0 3 57.38 S 30 0 8.65 W 1.5 15000
		try
		{

			// Get the object of DataInputStream
			// DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null)
			{
				// hey, is this a comment line?
				if (strLine.startsWith(";;"))
				{
					// just ignore it
				}
				else
				{
					// ok, get parseing it
					StringTokenizer st = new StringTokenizer(strLine, " \t");

					// skip the read of the header
					st.nextToken();

					// now the date
					String date = st.nextToken();

					// and the time
					String time = st.nextToken();

					// ignore the next
					st.nextElement();

					// and the next
					st.nextElement();

					String latDegs = st.nextToken();
					String latMins = st.nextToken();
					String latSecs = st.nextToken();
					String latHemi = st.nextToken();

					String lonDegs = st.nextToken();
					String lonMins = st.nextToken();
					String lonSecs = st.nextToken();
					String lonHemi = st.nextToken();

					// and the beraing
					@SuppressWarnings("unused")
					String bearing = st.nextToken();

					// and the range
					@SuppressWarnings("unused")
					String range = st.nextToken();

					// ok,now construct the date=time
					DateFormat df = new SimpleDateFormat("yyMMdd hhmmss");
					Date theDate = df.parse(date + " " + time);

					// and the location
					double lat = Double.valueOf(latDegs) + Double.valueOf(latMins) / 60d
							+ Double.valueOf(latSecs) / 60d / 60d;
					if (latHemi.toUpperCase().equals("W"))
						lat = -lat;
					double lon = Double.valueOf(lonDegs) + Double.valueOf(lonMins) / 60d
							+ Double.valueOf(lonSecs) / 60d / 60d;
					if (lonHemi.toUpperCase().equals("S"))
						lon = -lon;

					GeoPoint theLoc = new GeoPoint(lat, lon);
					ROrigin measure = new ROrigin(theLoc, theDate);

					addThis(measure);

				}
			}

			// Close the input stream
			fstream.close();

		}
		catch (Exception e)
		{
			// TODO: do logging!
			//SATC_Activator.log(Status.ERROR, "File load problem", e);
		}


		// give us some max/min data
		this.setMaxRange(3000);
		this.setMinRange(500);
		
		// TODO: set the start/end times = just for tidiness
	}

	/**
	 * store this new measurement
	 * 
	 * @param measure
	 */
	private void addThis(ROrigin measure)
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
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{

		// loop through our measurements
		Iterator<ROrigin> iter = _measurements.iterator();
		while (iter.hasNext())
		{
			RangeForecastContribution.ROrigin origin = (RangeForecastContribution.ROrigin) iter
					.next();

			Point pt = origin._origin.asPoint();

			// yes, ok we can centre our donut on that
			LinearRing outer = getOuterRing(pt);
			LinearRing inner = getInnerRing(pt);

			// did we generate an inner?
//			if (inner != null)
//			{
//				
//				// yes, better delete it then
//				Geometry res = thePolygon.difference(inner);
//				thePolygon = (Polygon) res;
//			}
			
			// and create a polygon for it.
			Polygon thePoly = GeoSupport.getFactory().createPolygon(outer, new LinearRing[]{inner});

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

	private LinearRing getOuterRing(Point pt)
	{
		// TODO: handle case where range not provided

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
		LinearRing res = (LinearRing)geom.getBoundary();
		return res;
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

	public double getEstimate()
	{
		return _estimate;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + ((int) _minRangeM) + " - " + ((int) _maxRangeM);
	}

	public double getMaxRange()
	{
		return _maxRangeM;
	}

	public double getMinRange()
	{
		return _minRangeM;
	}

	public void setEstimate(double estimate)
	{
		double oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxRange(double maxRngM)
	{
		double oldMaxRange = _maxRangeM;
		String oldConstraints = getHardConstraints();
		this._maxRangeM = maxRngM;
		firePropertyChange(MAX_RANGE, oldMaxRange, maxRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinRange(double minRngM)
	{
		double oldMinRange = _minRangeM;
		String oldConstraints = getHardConstraints();
		this._minRangeM = minRngM;
		firePropertyChange(MIN_RANGE, oldMinRange, minRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

}
