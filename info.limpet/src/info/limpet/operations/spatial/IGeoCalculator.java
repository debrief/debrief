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
package info.limpet.operations.spatial;

import java.awt.geom.Point2D;

public interface IGeoCalculator {

	/**
	 *
	 * @param pos1 origin
	 * @param angle radians
	 * @param distance metres
	 * @return
	 */
	Point2D calculatePoint(Point2D pos1, double angle, double distance);

	/**
	 *
	 * @param dLong
	 * @param dLat
	 * @return
	 */
	Point2D createPoint(double dLong, double dLat);

	/**
	 *
	 * @param locA
	 * @param locB
	 * @return
	 */
	double getAngleBetween(Point2D locA, Point2D locB);

	/**
	 *
	 * @param locA origin
	 * @param locB designation
	 * @return metres
	 */
	double getDistanceBetween(Point2D locA, Point2D locB);
}
