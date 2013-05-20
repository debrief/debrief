package com.planetmayo.debrief.satc.model.contributions;

import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.geotools.referencing.GeodeticCalculator;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class BearingMeasurementContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String BEARING_ERROR = "bearingError";
	public static final String RUN_MDA = "autoDetect";
	public static final String OBSERVATIONS_NUMBER = "numObservations";

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
	 * the set of measurements we store
	 * 
	 */
	private ArrayList<BMeasurement> measurements = new ArrayList<BMeasurement>();

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// ok, here we really go for it!
		Iterator<BMeasurement> iter = measurements.iterator();

		// sort out a geometry factory
		GeometryFactory factory = GeoSupport.getFactory();

		while (iter.hasNext())
		{
			BearingMeasurementContribution.BMeasurement measurement = iter.next();

			// is it active?
			if (measurement.isActive())
			{
				// ok, create the polygon for this measurement
				GeoPoint origin = measurement.origin;
				double bearing = measurement.bearingAngle;
				double range = measurement.theRange;

				// sort out the left/right edges
				double leftEdge = bearing - bearingError;
				double rightEdge = bearing + bearingError;

				// ok, generate the polygon
				Coordinate[] coords = new Coordinate[5];

				// start off with the origin
				final double lon = origin.getLon();
				final double lat = origin.getLat();

				coords[0] = new Coordinate(lon, lat);

				// create a utility object to help with calcs
				GeodeticCalculator calc = new GeodeticCalculator();

				// now the top-left
				calc.setStartingGeographicPoint(new Point2D.Double(lon, lat));
				calc.setDirection(toTrimmedDegs(leftEdge), GeoSupport.deg2m(range));
				Point2D dest = calc.getDestinationGeographicPoint();
				coords[1] = new Coordinate(dest.getX(), dest.getY());

				// now the centre bearing
				calc.setStartingGeographicPoint(new Point2D.Double(lon, lat));
				calc.setDirection(toTrimmedDegs(bearing), GeoSupport.deg2m(range));
				dest = calc.getDestinationGeographicPoint();
				coords[2] = new Coordinate(dest.getX(), dest.getY());

				// now the top-right
				calc.setStartingGeographicPoint(new Point2D.Double(lon, lat));
				calc.setDirection(toTrimmedDegs(rightEdge), GeoSupport.deg2m(range));
				dest = calc.getDestinationGeographicPoint();
				coords[3] = new Coordinate(dest.getX(), dest.getY());

				// and back to the start
				coords[4] = new Coordinate(coords[0]);

				// ok, store the coordinates
				CoordinateArraySequence seq = new CoordinateArraySequence(coords);

				// and construct the bounded location object
				LinearRing ls = new LinearRing(seq, factory);
				Polygon poly = new Polygon(ls, null, factory);
				LocationRange lr = new LocationRange(poly);

				// do we have a bounds at this time?
				BoundedState thisState = space.getBoundedStateAt(measurement.time);
				if (thisState == null)
				{
					// ok, do the bounds
					thisState = new BoundedState(measurement.time);
					// and store it
					space.add(thisState);
				}

				// well, if we didn't - we do now! Apply it!
				thisState.constrainTo(lr);
			}
		}

		// hmm, do we run the MDA?
		if (getAutoDetect())
		{
			// get a few bounded states
			Collection<BoundedState> testStates = space.getBoundedStatesBetween(
					this.getStartDate(), this.getFinishDate());
			int ctr = 0;
			for (Iterator<BoundedState> iterator = testStates.iterator(); iterator
					.hasNext();)
			{
				BoundedState boundedState = iterator.next();
				ctr++;
				if (ctr >= 0 && ctr <= 3)
				{
					boundedState.setMemberOf("test MDA leg");
				}
			}
		}
	}

	private double toTrimmedDegs(double rads)
	{
		double res = Math.toDegrees(rads);
		while(res > 180)
			res -= 360;
		while(res < -180)
			res += 360;
		
		return res;
	}
	
	@Override
	protected double cumulativeScoreFor(CoreRoute route)
	{
		double res = 0;

		// Note: we no longer calculate the cumulative score for BMC
		// all we know is that the subject is inside the bearing polygon somewhere.
		// So, all solutions put the vehicle inside the bearing polygons.
		// There isn't merit in determining how close it is to the centre bearing of
		// the area.

		//
		//
		// ArrayList<State> states = route.getStates();
		// Iterator<State> sIter = states.iterator();
		// State thisS = null;
		//
		// // move to the first state
		// if (sIter.hasNext())
		// thisS = sIter.next();
		//
		// // if the list is empty, drop out
		// if (thisS == null)
		// return res;
		//
		// // keep track of how many errors we calculate
		// int errCtr = 0;
		//
		// // ok. work through the bearings
		// Iterator<BMeasurement> iter = measurements.iterator();
		// while (iter.hasNext())
		// {
		// BearingMeasurementContribution.BMeasurement m =
		// (BearingMeasurementContribution.BMeasurement) iter
		// .next();
		// Date time = m.time;
		//
		// // ok, find the state that matches this bearing measurement
		// while (thisS.getTime().before(time) && sIter.hasNext())
		// {
		// thisS = sIter.next();
		// }
		//
		// // check we haven't shot past the end of the states
		// if (time.after(thisS.getTime()))
		// break;
		//
		// // ok, we're at the state which is on or after this measurement.
		// // but is it on?
		// if (thisS.getTime().equals(time))
		// {
		//
		// // now find the error from this location
		// Point loc = thisS.getLocation();
		//
		// // what's the bearing from this origin?
		// double bearing = m.origin.bearingTo(loc);
		//
		// // System.out.println("testing brg:" + time +
		// // " against state:" + thisS.getTime()
		// // + " brg:" + Math.toDegrees(bearing) + " should be:" +
		// // Math.toDegrees(m._bearingAngle));
		//
		// // what's the difference between that and my measurement
		// double thisError = Math.abs(bearing - m.bearingAngle);
		//
		// // what's my measurement error
		// final double errorRange = Math.toRadians(1);
		// thisError /= errorRange;
		//
		// // we now need to 'normalise' this error. The analyst has entered a
		// // bearing error value. So, all points must be within that error range
		//
		// // and accumulate it
		// // res += thisError;
		//
		// errCtr++;
		// }
		//
		// // ok, we now need to calculate the mean error
		// if (errCtr > 0)
		// res /= (double) errCtr;
		//
		// }
		return res;
	}

	public void addEstimate(double lat, double lon, Date date, double brg,
			double range)
	{
		GeoPoint loc = new GeoPoint(lat, lon);
		BMeasurement measure = new BMeasurement(loc, brg, date, range);
		addThis(measure);
		firePropertyChange(OBSERVATIONS_NUMBER, measurements.size(),
				measurements.size());
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

		measurements.add(measure);
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.MEASUREMENT;
	}

	public int getNumObservations()
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
			BMeasurement measure = new BMeasurement(theLoc, Math.toRadians(Double
					.valueOf(bearing)), theDate, GeoSupport.m2deg(Double.valueOf(range)));

			addThis(measure);

		}
		this.setBearingError(Math.toRadians(3d));
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

	/**
	 * provide the bearing error
	 * 
	 * @return (in radians)
	 */
	public void setBearingError(Double errorRads)
	{

		// IDIOT CHECK - CHECK WE HAVEN'T ACCIDENTALLY GOT DEGREES
		if (errorRads > 2)
			SATC_Activator.log(Status.WARNING,
					"Looks like error is being presented in Degs", null);

		Double old = bearingError;
		this.bearingError = errorRads;
		firePropertyChange(BEARING_ERROR, old, errorRads);
		fireHardConstraintsChange();
	}

	public void setAutoDetect(boolean onAuto)
	{
		boolean previous = runMDA;
		runMDA = onAuto;
		firePropertyChange(RUN_MDA, previous, onAuto);
		firePropertyChange(HARD_CONSTRAINTS, previous, onAuto);

	}

	public boolean getAutoDetect()
	{
		return runMDA;
	}

	/**
	 * utility class for storing a measurement
	 * 
	 * @author ian
	 * 
	 */
	public static class BMeasurement
	{
		private final GeoPoint origin;
		private final double bearingAngle;
		private final Date time;
		private boolean _isActive = true;
		/**
		 * the (optional) maximum range for this measurement
		 * 
		 */
		private final Double theRange;

		public BMeasurement(GeoPoint loc, double bearing, Date time, Double theRange)
		{
			this.origin = loc;
			this.bearingAngle = bearing;
			this.time = time;
			this.theRange = theRange;
		}

		public boolean isActive()
		{
			return _isActive;
		}

		public void setActive(boolean active)
		{
			_isActive = active;
		}

	}
}
