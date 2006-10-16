package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Dimension;

public class FullCircleAngleMapper implements AngleMapper {
	private final double myZeroAngle;

	private final double myFull;

	public FullCircleAngleMapper(double zeroAngle, double full) {
		myZeroAngle = zeroAngle;
		myFull = full;
	}

	public double computeAngle(double value) {
		return value / myFull * 2 * Math.PI + myZeroAngle;
	}

	public void setAnglesRange(Dimension minDirection, Dimension maxDirection) {
		throw new UnsupportedOperationException(
				"I am mapping the whole circle. These directions do not make sense for me");

	}
}
