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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Status;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.planetmayo.debrief.satc.zigdetector.ILegStorer;
import com.planetmayo.debrief.satc.zigdetector.IZigStorer;
import com.planetmayo.debrief.satc.zigdetector.LegOfData;
import com.planetmayo.debrief.satc.zigdetector.OwnshipLegDetector;
import com.planetmayo.debrief.satc.zigdetector.Sensor;
import com.planetmayo.debrief.satc.zigdetector.ZigDetector;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class BearingMeasurementContribution extends
		CoreMeasurementContribution<BearingMeasurementContribution.BMeasurement>
{
	private static final double ZIG_DETECTOR_RMS = 0.6;

	private static final long serialVersionUID = 1L;

	public static final String BEARING_ERROR = "bearingError";
	public static final String RUN_MDA = "autoDetect";

	public static interface MDAResultsListener
	{
		public void startingSlice(String contName);

		public void ownshipLegs(String contName, ArrayList<BMeasurement> bearings,
				List<LegOfData> ownshipLegs,
				ArrayList<HostState> hostStates);
		
		public void sliced(String contName, 
				ArrayList<StraightLegForecastContribution> arrayList);
	}

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
	 * store the ownship states, if possible. We use this to run the manoeuvre
	 * detection algorithm
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
				double range = measurement.range;

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
				GeodeticCalculator calc = GeoSupport.createCalculator();

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

				// ok, override any existing color for this state, if we have one
				if (measurement.getColor() != null)
					thisState.setColor(measurement.getColor());

				// well, if we didn't - we do now! Apply it!
				thisState.constrainTo(lr);

				LineString bearingLine = GeoSupport.getFactory().createLineString(
						new Coordinate[] { coords[0], coords[2] });
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

	@Override
	protected double cumulativeScoreFor(CoreRoute route)
	{
		double bearingError = this.bearingError == null ? 0 : this.bearingError;
		if (!isActive() || route.getType() == LegType.ALTERING || bearingError == 0)
		{
			return 0;
		}
		double res = 0;
		int count = 0;
		for (BMeasurement measurement : measurements)
		{
			Date dateMeasurement = measurement.getDate();
			if (dateMeasurement.compareTo(route.getStartTime()) >= 0
					&& dateMeasurement.compareTo(route.getEndTime()) <= 0)
			{
				State state = route.getStateAt(dateMeasurement);
				if (state != null && state.getLocation() != null)
				{
					GeodeticCalculator calculator = GeoSupport.createCalculator();
					calculator.setStartingGeographicPoint(measurement.origin.getLon(),
							measurement.origin.getLat());
					calculator.setDestinationGeographicPoint(state.getLocation().getX(),
							state.getLocation().getY());

					double radians = MathUtils.normalizeAngle(Math.toRadians(calculator
							.getAzimuth()));
					double angleDiff = MathUtils.angleDiff(measurement.bearingAngle,
							radians, true);

					// make the error a proportion of the bearing error
					angleDiff = angleDiff / (this.getBearingError());

					// store the error
					state.setScore(this, angleDiff * this.getWeight() / 10);

					// and prepare the cumulative score
					double thisError = angleDiff * angleDiff;
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

	public void addMeasurement(double lat, double lon, Date date, double brg,
			double range)
	{
		GeoPoint loc = new GeoPoint(lat, lon);
		BMeasurement measure = new BMeasurement(loc, brg, date, range);
		addMeasurement(measure);
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
			double angle = Math.toRadians(Double.parseDouble(bearing));
			BMeasurement measure = new BMeasurement(theLoc, angle, theDate,
					Double.parseDouble(range));

			addMeasurement(measure);

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

	public List<HostState> getHostState()
	{
		return states;
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
	public static class BMeasurement extends
			CoreMeasurementContribution.CoreMeasurement
	{
		private static final double MAX_RANGE_METERS = RangeForecastContribution.MAX_SELECTABLE_RANGE_M;
		private final GeoPoint origin;
		private final double bearingAngle;
		/**
		 * the (optional) maximum range for this measurement
		 * 
		 */
		private final double range;

		public BMeasurement(GeoPoint loc, double bearing, Date time, Double range)
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

	public long[] getTimes(ArrayList<HostState> states)
	{
		long[] res = new long[states.size()];
		int ctr = 0;
		Iterator<HostState> iter = states.iterator();
		while (iter.hasNext())
		{
			BearingMeasurementContribution.HostState hostState = (BearingMeasurementContribution.HostState) iter
					.next();
			res[ctr++] = hostState.time;
		}
		return res;
	}

	public double[] getCourses(ArrayList<HostState> states)
	{
		double[] res = new double[states.size()];
		Iterator<HostState> iter = states.iterator();
		int ctr = 0;
		while (iter.hasNext())
		{
			BearingMeasurementContribution.HostState hostState = (BearingMeasurementContribution.HostState) iter
					.next();
			res[ctr++] = hostState.courseDegs;
		}
		return res;
	}

	public double[] getSpeeds(ArrayList<HostState> states)
	{
		double[] res = new double[states.size()];
		Iterator<HostState> iter = states.iterator();
		int ctr = 0;
		while (iter.hasNext())
		{
			BearingMeasurementContribution.HostState hostState = (BearingMeasurementContribution.HostState) iter
					.next();
			res[ctr++] = hostState.speedKts;
		}
		return res;
	}

	public static class HostState
	{
		final public long time;
		final public double courseDegs;
		final public double speedKts;
		final public double dLat;
		final public double dLong;

		public HostState(long time, double courseDegs, double speedKts, double dLat, double dLong)
		{
			this.time = time;
			this.courseDegs = courseDegs;
			this.speedKts = speedKts;
			this.dLat = dLat;
			this.dLong = dLong;
		}
	}
	
	public void sliceOwnship(final IContributions contributions)
	{
		// ok share the good news - we're about to start
		if (_listeners != null)
		{
			Iterator<MDAResultsListener> iter = _listeners.iterator();
			while (iter.hasNext())
			{
				BearingMeasurementContribution.MDAResultsListener thisL = (BearingMeasurementContribution.MDAResultsListener) iter
						.next();
				thisL.startingSlice(this.getName());
			}
		}
		
		// ok, extract the ownship legs from this data
		OwnshipLegDetector osLegDet = new OwnshipLegDetector();

		if (ownshipLegs != null)
			ownshipLegs.clear();

		ownshipLegs = osLegDet.identifyOwnshipLegs(getTimes(states),
				getSpeeds(states), getCourses(states), 9);
		
		// ok, share the ownship legs
		// ok, slicing done!
		if (_listeners != null)
		{
			Iterator<MDAResultsListener> iter = _listeners.iterator();
			while (iter.hasNext())
			{
				BearingMeasurementContribution.MDAResultsListener thisL = (BearingMeasurementContribution.MDAResultsListener) iter
						.next();
				thisL.ownshipLegs(this.getName(), this.getMeasurements(), ownshipLegs, states);
			}
		}
	}

	public void runMDA(final IContributions contributions)
	{
		// ok, we've got to find the ownship data, somehow :-(
		if ((states == null) || (states.size() == 0))
		{
			return;
		}
		
		// decide if we are going to split at ownship and target zigs, or just target zigs
		final boolean justTargetZigs = true;
		
		// ok, now ditch any straight leg contributions that we generated
		Iterator<BaseContribution> ditchIter = contributions.iterator();
		ArrayList<StraightLegForecastContribution> toRemove = new ArrayList<StraightLegForecastContribution>();
		while (ditchIter.hasNext())
		{
			BaseContribution baseContribution = (BaseContribution) ditchIter.next();
			if(baseContribution instanceof StraightLegForecastContribution)
			{
				StraightLegForecastContribution sfl = (StraightLegForecastContribution) baseContribution;
				if(sfl.getAutoGenBy().equals(getName()))
				{
					toRemove.add(sfl);
				}
			}
		}
		
		// ditch any that we did find
		Iterator<StraightLegForecastContribution> remover = toRemove.iterator();
		while (remover.hasNext())
		{
			StraightLegForecastContribution toDitch = (StraightLegForecastContribution) remover
					.next();
			contributions.removeContribution(toDitch);
		}
		


		// create object that can store the new legs
		IContributions zigConts, legConts;
		if(justTargetZigs)
		{			
			zigConts = contributions;
			legConts = null;
		}
		else
		{
			legConts= contributions;
			 zigConts = null;			
		}
		
		ILegStorer legStorer = new MyLegStorer(legConts, this.getMeasurements(), this.getName());
		IZigStorer zigStorer = new MyZigStorer(zigConts, this.getMeasurements(), this.getName(), 
				states.get(0).time, 
				states.get(states.size()-1).time);

		// ok, now collate the bearing data
		ZigDetector detector = new ZigDetector();

		// ok, work through the legs. In the absence of a Discrete
		// Optimisation algorithm we're taking a brue force approach.
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

			List<Long> thisLegTimes = new ArrayList<Long>();
			List<Double> thisLegBearings = new ArrayList<Double>();
			ArrayList<BMeasurement> meas = getMeasurements();
			Iterator<BMeasurement> iter = meas.iterator();
			while (iter.hasNext())
			{
				BearingMeasurementContribution.BMeasurement measurement = (BearingMeasurementContribution.BMeasurement) iter
						.next();
				long thisTime = measurement.getDate().getTime();
				if ((thisTime >= legStart) && (thisTime <= legEnd))
				{
					thisLegTimes.add(measurement.getDate().getTime());
					thisLegBearings.add(Math.toDegrees(measurement.bearingAngle));
				}
			}

			double zigScore = ZIG_DETECTOR_RMS;
			zigScore = 0.5;
			detector.sliceThis("some name", legStart, legEnd, null, legStorer, zigStorer, zigScore,
					0.000001, thisLegTimes, thisLegBearings);
		}
		
		// ok, finalise the zig-detector, if we have one		
		zigStorer.finish();

		// ok, slicing done!
		if (_listeners != null)
		{
			Iterator<MDAResultsListener> iter = _listeners.iterator();
			while (iter.hasNext())
			{
				BearingMeasurementContribution.MDAResultsListener thisL = (BearingMeasurementContribution.MDAResultsListener) iter
						.next();
				
				if(justTargetZigs)
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

	public List<LegOfData> getOwnshipLegs()
	{
		return ownshipLegs;
	}

	public void addSliceListener(MDAResultsListener listener)
	{
		if (_listeners == null)
			_listeners = new ArrayList<MDAResultsListener>();

		_listeners.add(listener);
	}

	public void removeSliceListener(MDAResultsListener listener)
	{
		if (_listeners != null)
			_listeners.remove(listener);
	}

	private static class MyLegStorer extends MyStorer implements ILegStorer
	{

		public MyLegStorer(final IContributions theConts,
				ArrayList<BMeasurement> cuts, String genName)
		{
			super(theConts, cuts, genName);
		}
	}
	
	private static class MyZigStorer extends MyStorer implements IZigStorer
	{

		private long _startTime;
		private final long _endTime;

		public MyZigStorer(final IContributions theConts,
				final ArrayList<BMeasurement> cuts, final String genName, final long startTime, final long endTime)
		{
			super(theConts, cuts, genName);
			_startTime = startTime;
			_endTime = endTime;
		}

		@Override
		public void storeZig(String scenarioName, long tStart, long tEnd,
				Sensor sensor, double rms)
		{
			storeLeg(scenarioName, _startTime, tStart, sensor, rms);
			
			// and move foward the end time
			_startTime = tEnd;
		}



		@Override
		public ArrayList<StraightLegForecastContribution> getSlices()
		{
			finish();
			
			return super.getSlices();
		}

		@Override
		public void finish()
		{
			// ok, just check if there is a missing last leg
			if(_startTime != Long.MIN_VALUE)
			{
				// ok, append the last leg
				storeLeg(null, _startTime, _endTime, null, 0);
				_startTime = Long.MIN_VALUE;
			}
		}
	}


	private static class MyStorer 
	{
		int ctr = 1;
		protected ArrayList<StraightLegForecastContribution> slices = new ArrayList<StraightLegForecastContribution>();
		protected final IContributions _contributions;
		protected final ArrayList<BMeasurement> _cuts;
		protected final String _genName;

		public MyStorer(final IContributions theConts,
				ArrayList<BMeasurement> cuts, String genName)
		{
			_contributions = theConts;
			_cuts = cuts;
			_genName = genName;
		}

		public ArrayList<StraightLegForecastContribution> getSlices()
		{
			return slices;
		}

		private Color colorAt(Date date)
		{
			Color res = null;
			Iterator<BMeasurement> iter = _cuts.iterator();
			while (iter.hasNext())
			{
				BearingMeasurementContribution.BMeasurement measurement = (BearingMeasurementContribution.BMeasurement) iter
						.next();
				
				// check if it's on or after the supplied date
				if (!measurement.getDate().before(date))
				{
					res = measurement.getColor();
				}
			}
			return res;
		}

		public void storeLeg(String scenarioName, long tStart, long tEnd,
				Sensor sensor, double rms)
		{
			String name = "Tgt-" + ctr++;
			SATC_Activator.log(Status.INFO, " FOUND LEG FROM " + new Date(tStart) + " - " + new Date(tEnd), null);
			StraightLegForecastContribution slf = new CompositeStraightLegForecastContribution();
			slf.setStartDate(new Date(tStart));
			slf.setAutoGenBy(_genName);
			slf.setFinishDate(new Date(tEnd));
			slf.setColor(colorAt(slf.getStartDate()));
			slf.setActive(true);
			slf.setName(name);
			if(_contributions != null)
			{
				_contributions.addContribution(slf);
			}
			slices .add(slf);
		}
	}
	
	public void addState(final HostState newState)
	{
		// check we have our states
		if (states == null)
			states = new ArrayList<HostState>();

		// and store this new one
		states.add(newState);
	}
}
