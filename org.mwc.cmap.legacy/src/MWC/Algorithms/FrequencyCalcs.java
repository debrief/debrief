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

package MWC.Algorithms;

import MWC.GenericData.WorldSpeed;

public class FrequencyCalcs {

	final public static double SpeedOfSoundKts = 2951;

	final public static String SPEED_OF_SOUND_KTS_PROPERTY = "SpeedOfSound";

	/**
	 * calculate the doppler component of the observed frequency
	 *
	 * @param theBearingRads bearing to the target
	 * @param myCourseRads   ownship course
	 * @param mySpeedKts     ownship speed
	 * @param observedFreq   observed frequency (hz)
	 * @return ownship component of doppler
	 */
	public static double calcDopplerComponent(final double theBearingRads, final double myCourseRads,
			final double mySpeedKts, final double observedFreq) {

		final double speedOfSoundKts = 2951;
		final double relBearingRads = theBearingRads - myCourseRads;

		final double ownSpeedAlongKts = Math.cos(relBearingRads) * mySpeedKts;

		final double dopplerOffset = (ownSpeedAlongKts * observedFreq) / speedOfSoundKts;

		return dopplerOffset;
	}

	/**
	 * perform doppler shift calculation Note: we receive dLat, dLong to support
	 * different range calculations. The Excel spreadsheet (DopplerEffect.xls) that
	 * is used to verify the algorithm uses flat-earth calcs. In normal use we wish
	 * to use round-earth calcs
	 *
	 * @param SpeedOfSound   m/s
	 * @param osHeadingRads  rads
	 * @param tgtHeadingRads rads
	 * @param osSpeed        m/s
	 * @param tgtSpeed       m/s
	 * @param bearing        radians
	 * @return
	 */
	public static double calcPredictedFreqSI(final double SpeedOfSound, final double osHeadingRads,
			final double tgtHeadingRads, final double osSpeed, final double tgtSpeed, final double bearing,
			final double fNought) {
		final double relB = bearing - osHeadingRads;

		// note - contrary to some publications TSL uses the
		// angle along the bearing, not the angle back down the bearing (ATB).
		final double hisBearing = Math.PI + bearing;
		final double angleOffTheOtherB = hisBearing - tgtHeadingRads;

		final double OSL = Math.cos(relB) * osSpeed;
		final double TSL = Math.cos(angleOffTheOtherB) * tgtSpeed;

		final double freq = fNought * (SpeedOfSound + OSL) / (SpeedOfSound - TSL);

		return freq;
	}

	/**
	 * calculate what the received frequency should in these circumstances
	 *
	 * @param f0
	 * @param speedOfSoundKts
	 * @param rxSpeedKts
	 * @param rxCourseDegs
	 * @param txSpeedKts
	 * @param txCourseDegs
	 * @param bearingDegs
	 * @return
	 */
	public static double getPredictedFreq(final double f0, final double speedOfSoundKts, final double rxSpeedKts,
			final double rxCourseDegs, final double txSpeedKts, final double txCourseDegs, final double bearingDegs) {
		// collate the data
		final double C = new WorldSpeed(speedOfSoundKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
		final double rxSpeed = new WorldSpeed(rxSpeedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
		final double txSpeed = new WorldSpeed(txSpeedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);

		final double bearingRads = Math.toRadians(bearingDegs);
		final double rxCrse = Math.toRadians(rxCourseDegs);
		final double txCrse = Math.toRadians(txCourseDegs);

		return calcPredictedFreqSI(C, rxCrse, txCrse, rxSpeed, txSpeed, bearingRads, f0);
	}

	static double travellingTowards(final double relB) {
		double brg = relB;
		if (relB > Math.PI) {
			brg -= (2 * Math.PI);
		}
		if (relB < -Math.PI) {
			brg += (2 * Math.PI);
		}
		return Math.abs(brg) < Math.PI / 2 ? 1 : -1;
	}
}
