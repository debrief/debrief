package com.planetmayo.debrief.satc.util;

/* Debrief application libraries have a Doppler Calculator algorithm. This interface is a way
 * of wrapping the existing implementation such that satc.core can access it without a
 * direct dependency
 */
public interface DopplerCalculator
{
	public double calcPredictedFreq(final double SpeedOfSound,
			final double osHeadingRads, final double tgtHeadingRads, final double osSpeed,
			final double tgtSpeed, final double angle, double fNought);

}
