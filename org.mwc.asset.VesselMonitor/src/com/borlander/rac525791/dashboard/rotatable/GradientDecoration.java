/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

public class GradientDecoration extends AbstractGradientDecoration {
	private final Point TEMP_FIRST = new Point();
	private final Point TEMP_SECOND = new Point();
	private Color myFirstColor;
	private Color mySecondColor;
	private final Point myFirstPoint = new Point();
	private final Point mySecondPoint = new Point();

	/**
	 * @param first
	 *            coordinate of the second gradient point in the coordinate
	 *            system defined by template PointList
	 * @param second
	 *            coordinate of the second gradient point in the coordinate
	 *            system defined by template PointList
	 */
	public void setGradient(Point first, Color firstColor, Point second, Color secondColor) {
		myFirstPoint.setLocation(first);
		mySecondPoint.setLocation(second);
		myFirstColor = firstColor;
		mySecondColor = secondColor;
	}

	/**
	 * @param first
	 *            index of the first gradient point based on template PointList
	 * @param second
	 *            index of the second gradient point based on template PointList
	 */
	public void setGradient(int firstIndex, Color firstColor, int secondIndex, Color secondColor) {
		setGradient(getTemplatePoint(firstIndex, TEMP_FIRST), firstColor, getTemplatePoint(secondIndex, TEMP_SECOND), secondColor);
	}
	
	@Override
	protected Pattern createPattern() {
		Point first = transformPoint(myFirstPoint);
		Point second = transformPoint(mySecondPoint);
		
		return new Pattern(null, first.x, first.y, second.x, second.y, myFirstColor, mySecondColor);
	}

}
