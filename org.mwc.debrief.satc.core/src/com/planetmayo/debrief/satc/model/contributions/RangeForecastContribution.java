/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import MWC.Utilities.TextFormatting.GMTDateFormat;

public class RangeForecastContribution extends BaseContribution {

	/**
	 * utility class for storing a measurement
	 *
	 * @author ian
	 *
	 */
	public static class ROrigin {
		private final GeoPoint origin;
		private final Date time;

		public ROrigin(final GeoPoint loc, final Date time) {
			this.origin = loc;
			this.time = time;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final String MIN_RANGE = "minRange";

	public static final String MAX_RANGE = "maxRange";

	/**
	 * for UI components, this is the maximum range that a user can select
	 *
	 */
	public static final double MAX_SELECTABLE_RANGE_M = 100000;

	protected Double minRangeM = 0d;

	protected Double maxRangeM = 0d;

	protected Double estimate = 0d;

	/**
	 * the set of measurements we store
	 *
	 */
	private final ArrayList<ROrigin> sensorOrigins = new ArrayList<ROrigin>();

	@Override
	public void actUpon(final ProblemSpace space) throws IncompatibleStateException {

		// just double-check that we have some sensor origins
		if (sensorOrigins.size() == 0)
			throw new IncompatibleStateException("Range Forecast doesn't know vehicle origins", null, null);

		// loop through our sensor origins
		final Iterator<ROrigin> iter = sensorOrigins.iterator();
		while (iter.hasNext()) {
			final RangeForecastContribution.ROrigin origin = iter.next();

			final Date thisT = origin.time;

			boolean useIt = true;
			// is it in time?
			if (super.getStartDate() != null) {
				if (super.getStartDate().after(thisT)) {
					useIt = false;
				}
			}
			if (super.getFinishDate() != null) {
				if (super.getFinishDate().before(thisT)) {
					useIt = false;
				}
			}

			if (!useIt)
				continue;

			final Point pt = origin.origin.asPoint();

			// yes, ok we can centre our donut on that
			final LinearRing outer = getOuterRing(pt);
			final LinearRing inner = getInnerRing(pt);
			LinearRing[] holes;

			// did we generate an inner?
			if (inner == null) {
				// nope = provide empty inner
				holes = null;
			} else {
				holes = new LinearRing[] { inner };
			}

			// and create a polygon for it.
			final Polygon thePoly = GeoSupport.getFactory().createPolygon(outer, holes);

			// GeoSupport.writeGeometry("rng_" + ctr, thePoly);

			// create a LocationRange for the poly
			// now define the polygon
			final LocationRange myRa = new LocationRange(thePoly);

			// is there already a bounded state at this time?
			BoundedState thisS = space.getBoundedStateAt(origin.time);

			if (thisS == null) {
				// nope, better create it
				thisS = new BoundedState(origin.time);
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
	public void addThis(final ROrigin measure) {
		// extend the time period accordingly
		if (this.getStartDate() == null) {
			this.setStartDate(measure.time);
			this.setFinishDate(measure.time);
		} else {
			final long newTime = measure.time.getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure.time);
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure.time);
		}

		sensorOrigins.add(measure);
	}

	@Override
	protected double cumulativeScoreFor(final CoreRoute route) {
		final double min = this.minRangeM == null ? 0 : this.minRangeM;
		final double max = this.maxRangeM == null ? 0 : this.maxRangeM;
		if (!isActive() || route.getType() == LegType.ALTERING || estimate == null || estimate < min
				|| estimate > max) {
			return 0;
		}
		double sum = 0;
		int count = 0;
		for (final ROrigin origin : sensorOrigins) {
			final Date currentDate = origin.time;
			if (currentDate.compareTo(route.getStartTime()) >= 0 && currentDate.compareTo(route.getEndTime()) <= 0) {
				final State state = route.getStateAt(currentDate);
				final Point location = state.getLocation();
				final GeodeticCalculator calculator = GeoSupport.createCalculator();
				calculator.setStartingGeographicPoint(location.getX(), location.getY());
				calculator.setDestinationGeographicPoint(origin.origin.getLon(), origin.origin.getLat());
				final double distance = calculator.getOrthodromicDistance();

				double error = distance - estimate;

				// calculate the error as a proportion to the relevant edge
				if (error < 0) {
					error = error / (estimate - getMinRange());
				} else {
					error = error / (getMaxRange() - estimate);
				}

				// store the error
				state.setScore(this, Math.abs(error * this.getWeight() / 10));

				// and prepare the cumulative score
				sum += error * error;
				count++;
			}
		}
		if (count == 0) {
			return 0;
		}
		final double norm = Math.max(Math.abs(max - estimate), Math.abs(min - estimate));
		return Math.sqrt(sum / count) / norm;
	}

	@Override
	public ContributionDataType getDataType() {
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate() {
		return estimate;
	}

	private LinearRing getInnerRing(final Point pt) {
		final double range = getMinRange();

		// do we have an inner range?
		if (range == 0d) {
			// no, ok, just choose an absolutely monster range
			return null;
		}
		return GeoSupport.geoRing(pt, range);
	}

	public Double getMaxRange() {
		return maxRangeM;
	}

	public Double getMinRange() {
		return minRangeM;
	}

	private LinearRing getOuterRing(final Point pt) {
		// do we have a max range?
		double range = getMaxRange();
		if (range == 0d) {
			// no, ok, just choose an absolutely monster range
			range = MAX_SELECTABLE_RANGE_M;
		}

		// ok, now we create the outer circle
		return GeoSupport.geoRing(pt, range);
	}

	public void loadFrom(final List<String> lines) {
		// load from this source
		// ;;IGNORE YYMMDD HHMMSS IGNORE IGNORE LAT_DEG LAT_MIN LAT_SEC LAT_HEM
		// LONG_DEG LONG_MIN LONG_SEC LONG_HEM BEARING MAX_RNG
		// ;SENSOR: 100112 121329 SENSOR @A 0 3 57.38 S 30 0 8.65 W 1.5 15000

		// Read File Line By Line
		for (final String strLine : lines) {
			// hey, is this a comment line?
			if (strLine.startsWith(";;")) {
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
			@SuppressWarnings("unused")
			final String bearing = elements[13];

			// and the range
			@SuppressWarnings("unused")
			final String range = elements[14];

			// ok,now construct the date=time
			final Date theDate = ObjectUtils.safeParseDate(new GMTDateFormat("yyMMdd HHmmss"), date + " " + time);

			// and the location
			double lat = Double.valueOf(latDegs) + Double.valueOf(latMins) / 60d + Double.valueOf(latSecs) / 60d / 60d;
			if (latHemi.toUpperCase().equals("S"))
				lat = -lat;
			double lon = Double.valueOf(lonDegs) + Double.valueOf(lonMins) / 60d + Double.valueOf(lonSecs) / 60d / 60d;
			if (lonHemi.toUpperCase().equals("W"))
				lon = -lon;

			final GeoPoint theLoc = new GeoPoint(lat, lon);
			final ROrigin measure = new ROrigin(theLoc, theDate);

			addThis(measure);
		}

		// give us some max/min data
		this.setMaxRange(9000d);
		this.setMinRange(5d);
		this.setEstimate(4000d);

		// TODO: set the start/end times = just for tidiness
	}

	public void setEstimate(final Double newEstimate) {
		final Double oldEstimate = estimate;
		this.estimate = newEstimate;
		firePropertyChange(ESTIMATE, oldEstimate, newEstimate);
	}

	public void setMaxRange(final Double maxRngM) {
		final Double oldMaxRange = maxRangeM;
		this.maxRangeM = maxRngM;
		firePropertyChange(MAX_RANGE, oldMaxRange, maxRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldMaxRange, maxRngM);
	}

	public void setMinRange(final Double minRngM) {
		final Double oldMinRange = minRangeM;
		this.minRangeM = minRngM;
		firePropertyChange(MIN_RANGE, oldMinRange, minRngM);
		firePropertyChange(HARD_CONSTRAINTS, oldMinRange, minRngM);
	}
}
