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
package com.planetmayo.debrief.satc.util;

/* Debrief application libraries have a Doppler Calculator algorithm. This interface is a way
 * of wrapping the existing implementation such that satc.core can access it without a
 * direct dependency
 */
public interface DopplerCalculator {
	public double calcPredictedFreq(final double SpeedOfSound, final double osHeadingRads, final double tgtHeadingRads,
			final double osSpeed, final double tgtSpeed, final double angle, double fNought);

}
