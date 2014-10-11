/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.Algorithms;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

public class FrequencyCalcs
{

	/** calculate the doppler component of the observed frequency
	 * 
	 * @param theBearingRads bearing to the target
	 * @param myCourseRads ownship course
	 * @param mySpeedKts ownship speed
	 * @param observedFreq observed frequency (hz)
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
			Fix hostFix, Fix tgtFix)
	{
		// collate the data
		double C = new WorldSpeed(speedOfSoundKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
		double rxSpeed = new WorldSpeed(hostFix.getSpeed()*3, WorldSpeed.ft_sec).getValueIn(WorldSpeed.M_sec);
		double txSpeed = new WorldSpeed(tgtFix.getSpeed()*3, WorldSpeed.ft_sec).getValueIn(WorldSpeed.M_sec);
		
		double rxCrse = hostFix.getCourse();
		double txCourse = tgtFix.getCourse();
		
		double bearing = tgtFix.getLocation().bearingFrom(hostFix.getLocation());
		
		return calcObservedFreqCollate(f0, C, bearing, rxCrse, rxSpeed, txCourse, txSpeed);
	}
	
	private static double calcObservedFreqCollate(double f0, double C, double bearing, double rxCourse, double rxSpeed, double txCourse, double txSpeed)
	{
		// collate the data
		double rxSpeedAlong = rxSpeed * Math.cos(rxCourse - bearing);
		double txSpeedAlong = txSpeed * Math.cos(txCourse - bearing);
		
		return calcObservedFreqCore(f0, C, rxSpeedAlong, txSpeedAlong);
	}
	
	/** raw function, taken from:  http://en.wikipedia.org/wiki/Doppler_effect#General
	 * 
	 * @param f0 - radiated frequency
	 * @param C = speed of sound in water
	 * @param vR = velocity of receiver in medium
	 * @param vS = velocity of source in medium
	 * @return observed frequency
	 */
	private static double calcObservedFreqCore(double f0, double C, double vR, double vS)
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
			final double osHeadingRads, final double tgtHeadingRads, final double osSpeed,
			final double tgtSpeed, final double dLat, final double dLong)
	{
		double a = -Math.atan2(dLong, dLat);  // angle between points (rads)
	
		// trim to +/-180
		if (a - Math.PI / 2 < 0)
			a += 3 * Math.PI / 2;
		else
			a -= Math.PI / 2;
	
		final double b = tgtHeadingRads;
		final double c = a - b;
		final double d = osHeadingRads;
		final double e = a - d;
	
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

		public void testBasics()
		{
			final double C_MS = 2900;
			final double f0 = 150;
			
			double res = calcObservedFreqCore(f0, C_MS, 0.707, 0.707); 
			assertEquals("correct raw calc", 150.0, res);

			res = calcObservedFreqCollate(f0, C_MS, Math.PI/4, 0, 1, 0, 1);
			assertEquals("correct raw calc", 150.0, res);
			
			
			final double C_KTS = new WorldSpeed(C_MS,WorldSpeed.M_sec).getValueIn(WorldSpeed.Kts);
			WorldLocation rxLoc = new WorldLocation(0,0,0);
			WorldLocation tgtLoc = new WorldLocation(1,1,0);
			Fix host = new Fix(null, rxLoc, 0, new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			Fix tgt = new Fix(null, tgtLoc, 0, new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertEquals("correct raw calc", 150.0, res);

			// move receiver away
			host = new Fix(null, rxLoc, Math.toRadians(355), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res < 150d);
			
			// move receiver towards
			host = new Fix(null, rxLoc, Math.toRadians(5), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res > 150d);
			
			// reflected angle
			host = new Fix(null, rxLoc, Math.toRadians(85), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res > 150d);


			// accelerate receiver 
			host = new Fix(null, rxLoc, Math.toRadians(0), new WorldSpeed(2, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res > 150d);

			// decelerate receiver 
			host = new Fix(null, rxLoc, Math.toRadians(0), new WorldSpeed(0.5, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res < 150d);

			// back to start
			host = new Fix(null, rxLoc, Math.toRadians(0), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res == 150d);
			
			// move target towards
			tgt = new Fix(null, tgtLoc, Math.toRadians(355), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res > 150d);
			
			// move target away
			tgt = new Fix(null, tgtLoc, Math.toRadians(5), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res < 150d);
			
			// reflected angle
			tgt = new Fix(null, tgtLoc, Math.toRadians(85), new WorldSpeed(1, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res < 150d);


			// accelerate target 
			tgt = new Fix(null, tgtLoc, Math.toRadians(0), new WorldSpeed(2, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res < 150d);

			// decelerate target 
			tgt = new Fix(null, tgtLoc, Math.toRadians(0), new WorldSpeed(0.5, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec/3));
			res = getObservedFreq(f0, C_KTS, host, tgt);			
			assertTrue("correct raw calc", res > 150d);


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
