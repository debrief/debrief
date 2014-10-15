/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
