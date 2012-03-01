/**
 * 
 */
package Debrief.Wrappers.Track;

import java.awt.Color;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public final class Doublet implements Comparable<Doublet>
{
	public static final double INVALID_BASE_FREQUENCY = -1d;

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
			TrackSegment parent, final FixWrapper hostFix)
	{
		_sensor = sensor;
		_targetFix = targetFix;
		_targetTrack = parent;
		_hostFix = hostFix;
	}

	public FixWrapper getHost()
	{
		return _hostFix;
	}

	public FixWrapper getTarget()
	{
		return _targetFix;
	}

	/**
	 * ok find bearing error (wrapped to -..360)
	 * 
	 * @param measuredValue
	 * @param calcValue
	 * @return
	 */
	public double calculateBearingError(double measuredValue, double calcValue)
	{
		double theError = measuredValue - calcValue;

		while (theError > 180)
			theError -= 360.0;

		while (theError < -180)
			theError += 360.0;

		return theError;
	}

	public double getMeasuredBearing()
	{
		return _sensor.getBearing();
	}

	public double getAmbiguousMeasuredBearing()
	{
		double res = INVALID_BASE_FREQUENCY;
		if (_sensor.getHasAmbiguousBearing())
			res = _sensor.getAmbiguousBearing();

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

	public int compareTo(Doublet o)
	{
		int res = 0;
		res = _hostFix.getDTG().compareTo(o._hostFix.getDTG());
		return res;
	}

	/**
	 * ok find frequency error
	 * 
	 * @param measuredValue
	 * @param calcValue
	 * @return
	 */
	public double calculateFreqError(double measuredValue, double calcValue)
	{
		double theError = measuredValue - calcValue;

		while (theError > 180)
			theError -= 360.0;

		while (theError < -180)
			theError += 360.0;

		return theError;
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
			CoreTMASegment tma = (CoreTMASegment) _targetTrack;
			res = tma.getBaseFrequency();
		}

		return res;
	}

	public double getMeasuredFrequency()
	{
		return _sensor.getFrequency();
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
		double observedFreq = _sensor.getFrequency();
		final double dopplerComponent = calcDopplerComponent(theBearingRads,
				myCourseRads, mySpeedKts, observedFreq);

		correctedFreq = observedFreq + dopplerComponent;

		return correctedFreq;
	}

	private static double calcDopplerComponent(final double theBearingRads,
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

	/**
	 * Calculate the current doppler shift between the two positons
	 * 
	 * @param SpeedOfSound
	 *          the speed of sound to use, m/sec
	 * @return
	 */
	public static double getDopplerShift(double SpeedOfSound, Fix host, Fix tgt)
	{
		double osSpeed = MWC.Algorithms.Conversions.Kts2Mps(host.getSpeed());
		double tgtSpeed = MWC.Algorithms.Conversions.Kts2Mps(tgt.getSpeed());
		double osHeadingRads = host.getCourse();
		double tgtHeadingRads = tgt.getCourse();
		double osBearingRads = tgt.getLocation().subtract(host.getLocation())
				.getBearing();
		double tgtBearingRads = host.getLocation().subtract(tgt.getLocation())
				.getBearing();
		return calcDopplerShift(SpeedOfSound, osHeadingRads, tgtHeadingRads,
				osSpeed, tgtSpeed, osBearingRads, tgtBearingRads, host.getLocation(),
				tgt.getLocation());
	}

	private static double calcDopplerShift(double SpeedOfSound,
			double osHeadingRads, double tgtHeadingRads, double osSpeed,
			double tgtSpeed, double osBearingRads, double tgtBearingRads,
			WorldLocation hostLoc, WorldLocation tgtLoc)
	{
		// double a = Math.atan((hostLoc.getLat()-tgtLoc.getLat()) /
		// (hostLoc.getLong() -tgtLoc.getLong()));
		double dLat = hostLoc.getLat() - tgtLoc.getLat();
		double dLong = hostLoc.getLong() - tgtLoc.getLong();
		double a = Math.atan2(dLong, dLat);
		a = -a;

		if (a - Math.PI / 2 < 0)
			a += 3 * Math.PI / 2;
		else
			a -= Math.PI / 2;

		double b = tgtHeadingRads;
		double c = a - b;
		double d = osHeadingRads;
		double e = a - d;

		double s1 = Math.cos(c) * tgtSpeed;
		double s2 = Math.cos(e) * osSpeed;

		double doppler = (s2 - s1 + SpeedOfSound) / SpeedOfSound;

		return doppler;
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

		if (_targetTrack instanceof RelativeTMASegment)
		{
			RelativeTMASegment rt = (RelativeTMASegment) _targetTrack;
			final double theBearingDegs = getCalculatedBearing(null, null);
			final double theBearingRads = MWC.Algorithms.Conversions
					.Degs2Rads(theBearingDegs);
			final double myCourseRads = _hostFix.getCourse();

			final double mySpeedKts = _hostFix.getSpeed();
			double baseFreq = rt.getBaseFrequency();
			final double myDopplerComponent = calcDopplerComponent(theBearingRads,
					myCourseRads, mySpeedKts, baseFreq);

			final double hisCourseRads = _targetFix.getCourse();
			final double hisSpeedKts = _targetFix.getSpeed();

			final double hisDopplerComponent = calcDopplerComponent(Math.PI
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

	/**
	 * get the colour of this sensor fix
	 */
	public Color getColor()
	{
		return _sensor.getColor();
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

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testCalc extends junit.framework.TestCase
	{

		private double convertAndTest(double myCrseDegs, double bearingDegs,
				double mySpeedKts, double observedFreq)
		{
			double myCrseRads = MWC.Algorithms.Conversions.Degs2Rads(myCrseDegs);
			double bearingRads = MWC.Algorithms.Conversions.Degs2Rads(bearingDegs);
			return calcDopplerComponent(bearingRads, myCrseRads, mySpeedKts,
					observedFreq);
		}

		public void testDopplerShift()
		{
			final double SPEED_OF_SOUND = 1500;

			HiResDate time = new HiResDate(1000);
			WorldLocation hostLoc = new WorldLocation(4, 4, 0);
			WorldLocation tgtLoc = new WorldLocation(4, 7, 0);
			double hostCourse = MWC.Algorithms.Conversions.Degs2Rads(60);
			double hostSpeed = 8d; // MWC.Algorithms.Conversions.Kts2Yps(8);
			double tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(300);
			double tgtSpeed = 4; // MWC.Algorithms.Conversions.Kts2Yps(4);

			Fix host = new Fix(time, hostLoc, hostCourse, hostSpeed);
			Fix tgt = new Fix(time, tgtLoc, tgtCourse, tgtSpeed);

			double osBearingRads = tgt.getLocation().subtract(host.getLocation())
					.getBearing();
			double tgtBearingRads = host.getLocation().subtract(tgt.getLocation())
					.getBearing();

			double dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 1.00133, dShift, 0.00001d);

			// and another permutation
			hostCourse = MWC.Algorithms.Conversions.Degs2Rads(12d);
			tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(333d);
			hostSpeed = 3;
			tgtSpeed = 4;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 0.9995840, dShift, 0.00001d);

			// move to southern hemi
			hostLoc.setLat(-4);
			tgtLoc.setLat(-5);
			hostCourse = 0;
			tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(180);

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 1.004427, dShift, 0.00001d);

			hostCourse = MWC.Algorithms.Conversions.Degs2Rads(12d);
			tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(333d);
			hostSpeed = 3;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 0.99908, dShift, 0.00001d);

			hostLoc.setLong(-2);
			tgtLoc.setLong(-3);

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 0.99914, dShift, 0.00001d);

			// slow down target, so they're divering more slowly
			hostSpeed = 0.1;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 1.000768, dShift, 0.000001d);

			// stop them both
			hostSpeed = 0d;
			tgtSpeed = 0d;

			dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
					hostSpeed, tgtSpeed, osBearingRads, tgtBearingRads, hostLoc, tgtLoc);

			assertEquals("correct doppler shift", 1.000, dShift, 0.000001d);
		}

		public void testCorrected()
		{
			double res = convertAndTest(320, 28, 8, 300);
			assertEquals("right freq", -0.304, res, 0.1);

			res = convertAndTest(320, 328, 8, 300);
			assertEquals("right freq", -0.805, res, 0.1);

			res = convertAndTest(320, 158, 8, 300);
			assertEquals("right freq", 0.7734, res, 0.01);

			res = convertAndTest(320, 158, 9, 300);
			assertEquals("right freq", 0.870, res, 0.01);

			res = convertAndTest(150, 158, 9, 300);
			assertEquals("right freq", -0.906, res, 0.01);
		}
	}

}