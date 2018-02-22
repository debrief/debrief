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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI;

import java.awt.Shape;

public interface ExtendedCanvasType extends ShapeCanvasType
{
	/** produce a semi-transparent filled oval
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void semiFillOval(final int x, final int y, final int width,
			final int height);

	/** produce a semi-transparent filled polygon
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public void semiFillPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints);

	/** produce a semi-transparent filled arc
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param arcAngle
	 */
	public void semiFillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle);

	/** produce a semi-transparent filled rectangle
	 * 
	 * @param x
	 * @param y
	 * @param wid
	 * @param height
	 */
	public void semiFillRect(final int x, final int y, final int wid,
			final int height);
	
	public void semiFillShape(Shape shape);
	
  public void drawLine(final int x1, final int y1, final int x2,
      final int y2, final int transparency);

	
	public void setXORMode(boolean mode);
	
	public boolean getXORMode();
}
