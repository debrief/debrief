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

package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.geometry.Dimension;

import com.borlander.rac525791.dashboard.rotatable.AngleMapper;

public class ArcAngleMapper implements AngleMapper {

	private final int myFirstValue;
	private final int mySecondValue;
	private final boolean mySelectBiggestSector;

	private double myFirstAngle;
	private double mySecondAngle;

	public ArcAngleMapper(final int minValue, final int maxValue, final boolean selectBiggestSector) {
		myFirstValue = minValue;
		mySecondValue = maxValue;
		mySelectBiggestSector = selectBiggestSector;
	}

	@Override
	public double computeAngle(final double value) {
		final double alpha = (value - myFirstValue) / (mySecondValue - myFirstValue);
		return myFirstAngle + alpha * (mySecondAngle - myFirstAngle);
	}

	@Override
	public void setAnglesRange(final Dimension minDirection, final Dimension maxDirection) {
		final double firstAngle = Math.atan2(minDirection.height, minDirection.width);
		double secondAngle = Math.atan2(maxDirection.height, maxDirection.width);

		final boolean isOnBiggestSector = (secondAngle - firstAngle > Math.PI);
		if (isOnBiggestSector != mySelectBiggestSector) {
			if (firstAngle > secondAngle) {
				secondAngle += Math.PI * 2;
			} else {
				secondAngle -= Math.PI * 2;
			}
		}
		myFirstAngle = firstAngle;
		mySecondAngle = secondAngle;
	}

}
