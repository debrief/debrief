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
package Debrief.Wrappers.Track;

import java.awt.Color;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import MWC.Algorithms.Conversions;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public final class Doublet implements Comparable<Doublet>
{
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testCalc extends junit.framework.TestCase
	{

		private double convertAndTest(final double myCrseDegs, final double bearingDegs,
				final double mySpeedKts, final double observedFreq)
		{
			final double myCrseRads = MWC.Algorithms.Conversions.Degs2Rads(myCrseDegs);
			final double bearingRads = MWC.Algorithms.Conversions.Degs2Rads(bearingDegs);
			return FrequencyCalcs.calcDopplerComponent(bearingRads, myCrseRads, mySpeedKts,
					observedFreq);
		}

		public void testCorrected()
		{
			double res = convertAndTest(320, 28, 8, 300);
			assertEquals("right freq", 0.304, res, 0.1);

			res = convertAndTest(320, 328, 8, 300);
			assertEquals("right freq", 0.805, res, 0.1);

			res = convertAndTest(320, 158, 8, 300);
			assertEquals("right freq", -0.7734, res, 0.01);

			res = convertAndTest(320, 158, 9, 300);
			assertEquals("right freq", -0.870, res, 0.01);

			res = convertAndTest(150, 158, 9, 300);
			assertEquals("right freq", 0.906, res, 0.01);
		}
	}

	public static final double INVALID_BASE_FREQUENCY = -1d;

	/**
	 * Calculate the current doppler shift between the two positons
	 * 
	 * @param SpeedOfSound
	 *          the speed of sound to use, m/sec
	 * @return
	 */
	public static double getDopplerShift(final double SpeedOfSound, final double fNought, final Fix host, final Fix tgt)
	{
		final double osKts = Conversions.Yps2Kts(host.getSpeed());
		final double tgtKts = Conversions.Yps2Kts(tgt.getSpeed());
		
		final double osSpeed = MWC.Algorithms.Conversions.Kts2Mps(osKts);
		final double tgtSpeed = MWC.Algorithms.Conversions.Kts2Mps(tgtKts);
		final double osHeadingRads = host.getCourse();
		final double tgtHeadingRads = tgt.getCourse();

		// produce dLat, dLong at the correct point on the earth
		final WorldVector offset = tgt.getLocation().subtract(host.getLocation());

		// done, go for it.
		return FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound, osHeadingRads, tgtHeadingRads,
				osSpeed, tgtSpeed, offset.getBearing(), fNought);
	}

	private final SensorContactWrapper _sensor;

	private final FixWrapper _targetFix;

	private final FixWrapper _hostFix;

	private final TrackSegment _targetTrack;

	// ////////////////////////////////////////////////
	// working variables to help us along.
	// ////////////////////////////////////////////////
	private final WorldLocation _workingSensorLocation = new WorldLocation(0.0,
			0.0, 0.0);

	private final WorldLocation _workingTargetLocation = new WorldLocation(0.0,
			0.0, 0.0);

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////
	public Doublet(final SensorContactWrapper sensor, final FixWrapper targetFix,
			final TrackSegment parent, final FixWrapper hostFix)
	{
		_sensor = sensor;
		_targetFix = targetFix;
		_targetTrack = parent;
		_hostFix = hostFix;
	}

	/**
	 * ok find bearing error (wrapped to -..360)
	 * 
	 * @param measuredValue
	 * @param calcValue
	 * @return
	 */
	public double calculateBearingError(final double measuredValue, final double calcValue)
	{
		double theError = measuredValue - calcValue;

		while (theError > 180)
			theError -= 360.0;

		while (theError < -180)
			theError += 360.0;

		return theError;
	}

	/**
	 * ok find frequency error
	 * 
	 * @param measuredValue
	 * @param calcValue
	 * @return
	 */
	public double calculateFreqError(final double measuredValue, final double calcValue)
	{
		double theError = measuredValue - calcValue;

		while (theError > 180)
			theError -= 360.0;

		while (theError < -180)
			theError += 360.0;

		return theError;
	}

	@Override
	public int compareTo(final Doublet o)
	{
		int res = 0;
		
		// hmm, do they have the same host DTG?
		if(_hostFix.getDTG().equals(o._hostFix.getDTG()))
		{
			// yes, then lets compare the target fixes. This happens
			// when the sensor cuts are more frequent than the 
			// sensor platform positions
			res = _targetFix.getDTG().compareTo(o._targetFix.getDTG());					
		}
		else
		{		
			res = _hostFix.getDTG().compareTo(o._hostFix.getDTG());
		}
		return res;
	}

	public double getAmbiguousMeasuredBearing()
	{
		double res = INVALID_BASE_FREQUENCY;
		if (_sensor.getHasAmbiguousBearing())
			res = _sensor.getAmbiguousBearing();

		return res;
	}

	/**
	 * get the base frequency of this track participant, if it has one
	 * 
	 * @return
	 */
	public double getBaseFrequency()
	{
		double res = INVALID_BASE_FREQUENCY;
		if (_targetTrack instanceof CoreTMASegment)
		{
			final CoreTMASegment tma = (CoreTMASegment) _targetTrack;
			res = tma.getBaseFrequency();
		}

		return res;
	}

	public double getCalculatedBearing(final WorldVector sensorOffset,
			final WorldVector targetOffset)
	{
		// copy our locations
		_workingSensorLocation.copy(_sensor.getCalculatedOrigin(null));
		_workingTargetLocation.copy(_targetFix.getLocation());

		// apply the offsets
		if (sensorOffset != null)
			_workingSensorLocation.addToMe(sensorOffset);
		if (targetOffset != null)
			_workingTargetLocation.addToMe(targetOffset);

		// calculate the current bearing
		final WorldVector error = _workingTargetLocation
				.subtract(_workingSensorLocation);
		double calcBearing = error.getBearing();
		calcBearing = Conversions.Rads2Degs(calcBearing);

		if (calcBearing < 0)
			calcBearing += 360;

		return calcBearing;
	}

	/**
	 * get the colour of this sensor fix
	 */
	public Color getColor()
	{
		return _sensor.getColor();
	}

	/**
	 * calculate the corrected frequency (take out ownship doppler)
	 * 
	 * @return
	 */
	public double getCorrectedFrequency()
	{
		double correctedFreq = 0;

		final double theBearingDegs = getCalculatedBearing(null, null);
		final double theBearingRads = MWC.Algorithms.Conversions
				.Degs2Rads(theBearingDegs);
		final double myCourseRads = _hostFix.getCourse();

		final double mySpeedKts = _hostFix.getSpeed();
		final double observedFreq = _sensor.getFrequency();
		final double dopplerComponent = FrequencyCalcs.calcDopplerComponent(theBearingRads,
				myCourseRads, mySpeedKts, observedFreq);

		correctedFreq = observedFreq + dopplerComponent;

		return correctedFreq;
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////
	/**
	 * get the DTG of this contact
	 * 
	 * @return the DTG
	 */
	public HiResDate getDTG()
	{
		return _sensor.getDTG();
	}

	public FixWrapper getHost()
	{
		return _hostFix;
	}

	public double getMeasuredBearing()
	{
		return _sensor.getBearing();
	}

	public double getMeasuredFrequency()
	{
		return _sensor.getFrequency();
	}

	/**
	 * calculate what the frequency of the target should be (base freq plus both
	 * dopplers)
	 * 
	 * @return
	 */
	public double getPredictedFrequency()
	{
		double predictedFreq = 0;

		if (_targetTrack instanceof CoreTMASegment)
		{
			final CoreTMASegment rt = (CoreTMASegment) _targetTrack;
			final double theBearingDegs = getCalculatedBearing(null, null);
			final double theBearingRads = MWC.Algorithms.Conversions
					.Degs2Rads(theBearingDegs);
			final double myCourseRads = _hostFix.getCourse();

			final double mySpeedKts = _hostFix.getSpeed();
			final double baseFreq = rt.getBaseFrequency();
			final double myDopplerComponent = FrequencyCalcs.calcDopplerComponent(theBearingRads,
					myCourseRads, mySpeedKts, baseFreq);

			final double hisCourseRads = _targetFix.getCourse();
			final double hisSpeedKts = _targetFix.getSpeed();

			final double hisDopplerComponent = FrequencyCalcs.calcDopplerComponent(Math.PI
					+ theBearingRads, hisCourseRads, hisSpeedKts, baseFreq);

			// note, we've changed the sign of how we add the two components to the
			// base freq
			// - this wasn't based on theoretical evidence, but on empirical
			// observations
			// by users
			predictedFreq = baseFreq - (myDopplerComponent + hisDopplerComponent);
		}
		return predictedFreq;
	}

	public SensorContactWrapper getSensorCut()
	{
		return _sensor;
	}

	public FixWrapper getTarget()
	{
		return _targetFix;
	}

	public TrackSegment getTargetTrack()
	{
		return _targetTrack;
	}

}