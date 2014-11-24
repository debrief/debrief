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
package MWC.Algorithms;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class FrequencyCalcs
{

	/**
	 * calculate the doppler component of the observed frequency
	 * 
	 * @param theBearingRads
	 *          bearing to the target
	 * @param myCourseRads
	 *          ownship course
	 * @param mySpeedKts
	 *          ownship speed
	 * @param observedFreq
	 *          observed frequency (hz)
	 * @return ownship component of doppler
	 */
	public static double calcDopplerComponent(final double theBearingRads,
			final double myCourseRads, final double mySpeedKts,
			final double observedFreq)
	{

		final double speedOfSoundKts = 2951;
		double relBearingRads = theBearingRads - myCourseRads;

		final double ownSpeedAlongKts = Math.abs(Math.cos(relBearingRads)
				* mySpeedKts);

		// put rel brg into +/- 180 domain
		while (relBearingRads > Math.PI)
			relBearingRads -= (2 * Math.PI);
		while (relBearingRads < -(Math.PI))
			relBearingRads += (2 * Math.PI);

		double dopplerOffset = (ownSpeedAlongKts * observedFreq) / speedOfSoundKts;

		if (Math.abs(relBearingRads) < (Math.PI / 2))
			dopplerOffset = -dopplerOffset;
		return dopplerOffset;
	}

	public static double getObservedFreq(double f0, double speedOfSoundKts,
			double rxSpeedKts, double rxCourseDegs, double txSpeedKts,
			double txCourseDegs, double bearingDegs)
	{
		// collate the data
		double C = new WorldSpeed(speedOfSoundKts, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.M_sec);
		double rxSpeed = new WorldSpeed(rxSpeedKts, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.M_sec);
		double txSpeed = new WorldSpeed(txSpeedKts, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.M_sec);

		double bearingRads = Math.toRadians(bearingDegs);
		double rxCrse = Math.toRadians(rxCourseDegs);
		double txCrse = Math.toRadians(txCourseDegs);

		return calcObservedFreqCollate(f0, C, bearingRads, rxCrse, rxSpeed, txCrse,
				txSpeed);
	}

	private static double calcObservedFreqCollate(double f0, double C,
			double bearing, double rxCourse, double rxSpeed, double txCourse,
			double txSpeed)
	{
		// collate the data
		double rxSpeedAlong = rxSpeed * Math.cos(-(bearing - rxCourse));
		double txSpeedAlong = txSpeed * Math.cos(txCourse - bearing);

		return calcObservedFreqCore(f0, C, rxSpeedAlong, txSpeedAlong);
	}

	/**
	 * raw function, taken from:
	 * http://en.wikipedia.org/wiki/Doppler_effect#General
	 * 
	 * @param f0
	 *          - radiated frequency
	 * @param C
	 *          = speed of sound in water
	 * @param vR
	 *          = velocity of receiver in medium
	 * @param vS
	 *          = velocity of source in medium
	 * @return observed frequency
	 */
	private static double calcObservedFreqCore(double f0, double C, double vR,
			double vS)
	{
		return (C + vR) / (C + vS) * f0;
	}

	/**
	 * perform doppler shift calculation Note: we receive dLat, dLong to support
	 * different range calculations. The Excel spreadsheet (DopplerEffect.xls)
	 * that is used to verify the algorithm uses flat-earth calcs. In normal use
	 * we wish to use round-earth calcs
	 * 
	 * @param SpeedOfSound
	 *          m/s
	 * @param osHeadingRads
	 *          rads
	 * @param tgtHeadingRads
	 *          rads
	 * @param osSpeed
	 *          m/s
	 * @param tgtSpeed
	 *          m/s
	 * @param dLat
	 *          degs
	 * @param dLong
	 *          degs
	 * @return
	 */
	public static double calcDopplerShift(final double SpeedOfSound,
			final double osHeadingRads, final double tgtHeadingRads,
			final double osSpeed, final double tgtSpeed, final double dLat,
			final double dLong)
	{
		double a = -Math.atan2(dLong, dLat); // angle between points (rads)
		return calcDopplerShift(SpeedOfSound, osHeadingRads, tgtHeadingRads,
				osSpeed, tgtSpeed, a);
	}

	/**
	 * perform doppler shift calculation Note: we receive dLat, dLong to support
	 * different range calculations. The Excel spreadsheet (DopplerEffect.xls)
	 * that is used to verify the algorithm uses flat-earth calcs. In normal use
	 * we wish to use round-earth calcs
	 * 
	 * @param SpeedOfSound
	 *          m/s
	 * @param osHeadingRads
	 *          rads
	 * @param tgtHeadingRads
	 *          rads
	 * @param osSpeed
	 *          m/s
	 * @param tgtSpeed
	 *          m/s
	 * @param bearing
	 *          radians
	 * @return
	 */
	public static double calcDopplerShift(final double SpeedOfSound,
			final double osHeadingRads, final double tgtHeadingRads,
			final double osSpeed, final double tgtSpeed, double bearing)
	{

		// trim to +/-180
		if (bearing - Math.PI / 2 < 0)
			bearing += 3 * Math.PI / 2;
		else
			bearing -= Math.PI / 2;

		final double b = tgtHeadingRads;
		final double c = bearing - b;
		final double d = osHeadingRads;
		final double e = bearing - d;

		final double s1 = Math.cos(c) * tgtSpeed;
		final double s2 = Math.cos(e) * osSpeed;

		final double doppler = (s2 - s1 + SpeedOfSound) / SpeedOfSound;

		return doppler;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testCalc extends junit.framework.TestCase
	{

		public void testDemoFigures()
		{
			// (figures as provided by Iain)
			double osKts = 2.5;
			double osDegs = 208.2;
			double tgtKts = 9;
			double tgtDegs = 350.17;
			double bearing = 260.47;
			double Ckts = 2951;
			double f0 = 150;

			double osMS = new WorldSpeed(osKts, WorldSpeed.Kts)
					.getValueIn(WorldSpeed.M_sec);
			double tgtMS = new WorldSpeed(tgtKts, WorldSpeed.Kts)
					.getValueIn(WorldSpeed.M_sec);
			double osRads = Math.toRadians(osDegs);
			double tgtRads = Math.toRadians(tgtDegs);
			double brgRads = Math.toRadians(bearing);
			double Cms = new WorldSpeed(Ckts, WorldSpeed.Kts)
					.getValueIn(WorldSpeed.M_sec);

			double relBrg = bearing - osDegs;
			double OSL = osKts * Math.cos(Math.toRadians(-relBrg));
			double ATB = tgtDegs - bearing;
			double TSL = tgtKts * Math.cos(Math.toRadians(ATB));

			assertEquals("correct OSL", 1.5298, OSL, 0.0001);
			assertEquals("correct TSL", 0.04712, TSL, 0.0001);

			double res = FrequencyCalcs.calcObservedFreqCollate(f0, Cms, brgRads,
					osRads, osMS, tgtRads, tgtMS);

			assertEquals("correct obs", 150.0750, res, 0.001);

		}

		public void testDopplerShiftLowLevel()
		{
			final double SPEED_OF_SOUND = 1500;

			final WorldLocation hostLoc = new WorldLocation(4, 4, 0);
			final WorldLocation tgtLoc = new WorldLocation(4, 7, 0);
			double hostCourse = MWC.Algorithms.Conversions.Degs2Rads(60);
			double hostSpeed = 8d; // MWC.Algorithms.Conversions.Kts2Yps(8);
			double tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(300);
			double tgtSpeed = 4; // MWC.Algorithms.Conversions.Kts2Yps(4);

			double dLat = hostLoc.getLat() - tgtLoc.getLat();
			double dLong = hostLoc.getLong() - tgtLoc.getLong();

			double dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 1.00133, dShift, 0.00001d);

			// and another permutation
			hostCourse = MWC.Algorithms.Conversions.Degs2Rads(12d);
			tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(333d);
			hostSpeed = 3;
			tgtSpeed = 4;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 0.9995840, dShift, 0.00001d);

			// move to southern hemi
			hostLoc.setLat(-4);
			tgtLoc.setLat(-5);
			hostCourse = 0;
			tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(180);

			dLat = hostLoc.getLat() - tgtLoc.getLat();
			dLong = hostLoc.getLong() - tgtLoc.getLong();

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 1.004427, dShift, 0.00001d);

			hostCourse = MWC.Algorithms.Conversions.Degs2Rads(12d);
			tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(333d);
			hostSpeed = 3;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 0.99908, dShift, 0.00001d);

			hostLoc.setLong(-2);
			tgtLoc.setLong(-3);
			dLat = hostLoc.getLat() - tgtLoc.getLat();
			dLong = hostLoc.getLong() - tgtLoc.getLong();

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 0.99914, dShift, 0.00001d);

			// slow down target, so they're divering more slowly
			hostSpeed = 0.1;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 1.000768, dShift, 0.000001d);

			// stop them both
			hostSpeed = 0d;
			tgtSpeed = 0d;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, dLat, dLong);

			assertEquals("correct doppler shift", 1.000, dShift, 0.000001d);
		}
	}
}
