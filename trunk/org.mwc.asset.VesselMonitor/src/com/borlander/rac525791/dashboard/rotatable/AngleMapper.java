package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Dimension;

public interface AngleMapper {
	public void setAnglesRange(Dimension minDirection, Dimension maxDirection);
	public double computeAngle(double value);
}
