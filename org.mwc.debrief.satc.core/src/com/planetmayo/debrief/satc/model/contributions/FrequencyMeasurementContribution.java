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

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.DopplerCalculator;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

import MWC.Utilities.TextFormatting.GMTDateFormat;

public class FrequencyMeasurementContribution
		extends CoreMeasurementContribution<FrequencyMeasurementContribution.FMeasurement> {
	/**
	 * utility class for storing a measurement
	 *
	 * @author ian
	 *
	 */
	public static class FMeasurement extends CoreMeasurementContribution.CoreMeasurement {
		/**
		 * the (optional) maximum range for this measurement
		 *
		 */
		private Double frequency;
		private Double osCourse = null;
		private Double osSpeed = null;
		private GeoPoint osOrigin;

		public FMeasurement(final Date time, final Double frequency) {
			super(time);
			this.frequency = frequency;
		}

		public Double getCourse() {
			return osCourse;
		}

		public Double getFrequency() {
			return frequency;
		}

		public GeoPoint getOrigin() {
			return osOrigin;
		}

		public Double getSpeed() {
			return osSpeed;
		}

		public void setFrequency(final Double theFreq) {
			this.frequency = theFreq;
		}

		public void setOrigin(final GeoPoint origin) {
			osOrigin = origin;
		}

		public void setState(final double crseRads, final double spdMs) {
			osCourse = crseRads;
			osSpeed = spdMs;
		}

	}

	private static final long serialVersionUID = 1L;
	public static final String SOUND_SPEED = "soundSpeed";

	public static final String F_NOUGHT = "baseFrequency";

	/**
	 * the radiated frequency for this noise source
	 *
	 */
	private double baseFrequency;

	/**
	 * the speed of sound for this body of water (m/s)
	 *
	 */
	private double soundSpeed = 2000;

	@Override
	public void actUpon(final ProblemSpace space) throws IncompatibleStateException {
		// hmm, can't think of anything clever to do here
	}

//	@Override
//	protected double calcError(State thisState)
//	{
//		double res = 0;
//
//		Date date = thisState.getTime();
//
//		FMeasurement meas = measurementAt(date);
//
//		if (meas != null)
//		{
//			if (meas.isActive())
//			{
//				// right, do we know the ownship origin?
//				GeoPoint origin = meas.getOrigin();
//
//				if (origin != null)
//				{
//					// and check the course/speed
//					Double course = meas.getCourse();
//					Double speed = meas.getSpeed();
//
//					// ok, can we do a calculation
//					if ((course != null) && (speed != null))
//					{
//						// calculate the forecast frequency
//
//						// what's the bearing?
//						GeodeticCalculator calc = GeoSupport.createCalculator();
//						calc.setStartingGeographicPoint(new Point2D.Double(origin.getLon(),
//								origin.getLat()));
//						calc.setDestinationGeographicPoint(new Point2D.Double(thisState
//								.getLocation().getX(), thisState.getLocation().getY()));
//						double bearing = Math.toRadians(calc.getAzimuth());
//
//						// now try for the predicted doppler
//						DopplerCalculator calculator = SATC_Activator.getDefault()
//								.getDopplerCalculator();
//
//						// do we hav a calculator?
//						if (calculator != null)
//						{
//							final double predicted = calculator.calcPredictedFreq(soundSpeed,
//									meas.getCourse(), thisState.getCourse(), meas.getSpeed(),
//									thisState.getSpeed(), bearing, getBaseFrequency());
//
//							double error = predicted - meas.frequency;
//
//							res += Math.pow(error, 2);
//						}
//						else
//						{
//							SATC_Activator.log(Status.ERROR,
//									"Doppler calculator not assigned in SATC_Activator", null);
//						}
//					}
//				}
//			}
//		}
//
////		System.out.println("res:" + res);
//
//		return res;
//	}

	@Override
	protected double cumulativeScoreFor(final CoreRoute route) {
		if (!isActive() || route.getType() == LegType.ALTERING) {
			return 0;
		}
		double res = 0;
		int count = 0;
		for (final FMeasurement meas : measurements) {
			final Date dateMeasurement = meas.getDate();
			if (dateMeasurement.compareTo(route.getStartTime()) >= 0
					&& dateMeasurement.compareTo(route.getEndTime()) <= 0) {
				final State state = route.getStateAt(dateMeasurement);
				if (state != null && state.getLocation() != null) {

					if (meas.isActive()) {
						// right, do we know the ownship origin?
						final GeoPoint origin = meas.getOrigin();

						if (origin != null) {
							// and check the course/speed
							final Double course = meas.getCourse();
							final Double speed = meas.getSpeed();

							// ok, can we do a calculation
							if ((course != null) && (speed != null)) {
								// calculate the forecast frequency

								// what's the bearing?
								final GeodeticCalculator calc = GeoSupport.createCalculator();
								calc.setStartingGeographicPoint(new Point2D.Double(origin.getLon(), origin.getLat()));
								calc.setDestinationGeographicPoint(
										new Point2D.Double(state.getLocation().getX(), state.getLocation().getY()));
								final double bearing = Math.toRadians(calc.getAzimuth());

								// now try for the predicted doppler
								final DopplerCalculator calculator = SATC_Activator.getDefault().getDopplerCalculator();

								// do we hav a calculator?
								if (calculator != null) {
									final double predicted = calculator.calcPredictedFreq(soundSpeed, meas.getCourse(),
											state.getCourse(), meas.getSpeed(), state.getSpeed(), bearing,
											getBaseFrequency());

									final double error = predicted - meas.frequency;

									// store the error
									state.setScore(this, error * this.getWeight() / 10);

									// and the cumulative (RMS) score
									res += error * error;
									count++;

								}
							}
						}
					}

				}
			}
		}
		if (count > 0) {
			res = Math.sqrt(res / count);
		}
		return res;
	}

	public double getBaseFrequency() {
		return baseFrequency;
	}

	public double getSoundSpeed() {
		return soundSpeed;
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

			// and the range
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

			final FMeasurement measure = new FMeasurement(theDate, Double.valueOf(range));

			addMeasurement(measure);

		}
	}

	/**
	 * indicate the base frequency for this block of data
	 *
	 * @param baseFrequency
	 */
	public void setBaseFrequency(final double baseFrequency) {
		final double oldFreq = this.baseFrequency;

		this.baseFrequency = baseFrequency;

		firePropertyChange(F_NOUGHT, oldFreq, this.baseFrequency);
	}

	public void setSoundSpeed(final double soundSpeed) {
		final double oldSpeed = this.soundSpeed;

		this.soundSpeed = soundSpeed;

		firePropertyChange(SOUND_SPEED, oldSpeed, this.soundSpeed);
	}

}
