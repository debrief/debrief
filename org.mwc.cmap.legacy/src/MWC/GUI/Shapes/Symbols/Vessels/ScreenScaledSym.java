/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Shapes.Symbols.Vessels;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public abstract class ScreenScaledSym extends PlainSymbol
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<double[][]> _myCoords;

	/**
	 * getBounds
	 * 
	 * @return the returned java.awt.Dimension
	 */
	public java.awt.Dimension getBounds()
	{
		// sort out the size of the symbol at the current scale factor
		final java.awt.Dimension res = new java.awt.Dimension(
				(int) (2 * 4 * getScaleVal()), (int) (2 * 4 * getScaleVal()));
		return res;
	}

	/**
	 * paint - ignoring the current direction
	 * 
	 * @param dest
	 *          parameter for paint
	 * 
	 */
	public void paint(final CanvasType dest, final WorldLocation centre)
	{
		paint(dest, centre, 90.0 / 180 * Math.PI);
	}

	abstract protected Vector<double[][]> getCoords();

	/**
	 * give us a chance to cache the coordinates
	 * 
	 * @return
	 */
	private Vector<double[][]> getMyCoords()
	{
		if (_myCoords == null)
			_myCoords = getCoords();
		return _myCoords;
	}

	/**
	 * paint
	 * 
	 * @param dest
	 *          parameter for paint
	 * @param theLocation
	 *          centre for symbol
	 * @param direction
	 *          direction in Radians
	 */
	public void paint(final CanvasType dest, final WorldLocation theLocation, final double direction)
	{
		// set the colour
		dest.setColor(getColor());

		// get the origin in screen coordinates
		final Point centre = dest.toScreen(theLocation);

		final AffineTransform thisRotation = AffineTransform.getRotateInstance(
				direction, 0, 0);
		final AffineTransform thisTranslate = AffineTransform.getTranslateInstance(
				centre.x, centre.y);
		final AffineTransform thisScale = AffineTransform.getScaleInstance(getScaleVal(), getScaleVal());

		// find the lines that make up the shape
		final Vector<double[][]> hullLines = getMyCoords();

		// now for our reusable data objects
		final Point2D raw = new Point2D.Double();
		final Point2D postTurn = new Point2D.Double();
		final Point2D postScale  = new Point2D.Double();
		final Point2D postTranslate = new Point2D.Double();

		// start looping through the lines - to paint them
		final Iterator<double[][]> iter = hullLines.iterator();
		while (iter.hasNext())
		{
			Point2D lastPoint = null;
			final double[][] thisLine = iter.next();
			// now loop through the points
			for (int i = 0; i < thisLine.length; i++)
			{
				// ok, get this point
				raw.setLocation(thisLine[i][0], thisLine[i][1]);

				// apply transformations
				thisRotation.transform(raw, postTurn);
				thisScale.transform(postTurn, postScale);
				thisTranslate.transform(postScale, postTranslate);

				// and plot (as long as it isn't the first point)
				if (lastPoint != null)
				{
					dest.drawLine((int) lastPoint.getX(), (int) lastPoint.getY(),
							(int) postTranslate.getX(), (int) postTranslate.getY());
				}

				// remember the last point
				lastPoint = new Point2D.Double(postTranslate.getX(),
						postTranslate.getY());
			}
		}

	}

}
