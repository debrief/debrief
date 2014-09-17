package MWC.Algorithms;

import MWC.GenericData.WorldLocation;

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

	
	public static double newDopplerShift(WorldLocation transmitter, WorldLocation receiver, double txHeadingRads, double rxHdgRads,
			double txSpeedKts, double rxSpeedKts, double fNought, double C)
	{
		return 0;
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
