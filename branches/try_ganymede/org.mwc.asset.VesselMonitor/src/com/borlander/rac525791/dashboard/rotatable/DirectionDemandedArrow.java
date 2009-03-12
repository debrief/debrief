package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

public class DirectionDemandedArrow extends CircleDecoration {
	private static Color DARK_GREEN = SpeedDepthDemandedValueArrow.DARK_GREEN; 
	private static Color LIGHT_GREEN = SpeedDepthDemandedValueArrow.LIGHT_GREEN;
	
	public DirectionDemandedArrow() {
		super(new Point(42, 0), 2, DARK_GREEN, LIGHT_GREEN);
	}
}
