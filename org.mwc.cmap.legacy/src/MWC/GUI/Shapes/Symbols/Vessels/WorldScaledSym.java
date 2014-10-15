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
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public abstract class WorldScaledSym extends PlainSymbol
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<double[][]> _myCoords;

	protected WorldDistance _length;
	protected WorldDistance _width;

	/** constructor - including the default dimensions
	 * 
	 * @param length length of the platform
	 * @param width width (beam) of the platform
	 */
	public WorldScaledSym(final WorldDistance length, final WorldDistance width)
	{
		super();
		this._length = length;
		this._width = width;
	}

	
/** what factor do we have to apply to normalise this shape to one unit wide
 * 
 * @return
 */
	abstract protected double getWidthNormalFactor();
	
	/** what factor do we have to apply to normalise this shape to one unit long
	 * 
	 * @return
	 */
	abstract protected double getLengthNormalFactor();


	public WorldDistance getLength()
	{
		return _length;
	}

	public void setLength(final WorldDistance length)
	{
		_length = length;
	}

	public WorldDistance getWidth()
	{
		return _width;
	}

	public void setHeight(final WorldDistance width)
	{
		_width = width;
	}

	
	
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
	 * paint
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
	//	if (_myCoords == null)
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

		// create centre rotation
		final AffineTransform thisRotation = AffineTransform.getRotateInstance(
				-direction, 0, 0);

		// do the scale-factor
		final double lenFactor =  _length.getValueIn(WorldDistance.METRES) / getLengthNormalFactor() ;
		final double widFactor =   _width.getValueIn(WorldDistance.METRES) / getWidthNormalFactor();
		
		final AffineTransform scale = AffineTransform
				.getScaleInstance(widFactor, lenFactor);

		// find the lines that make up the shape
		final Vector<double[][]> hullLines = getMyCoords();

		// start looping through - to paint them
		final Iterator<double[][]> iter = hullLines.iterator();
		while (iter.hasNext())
		{
			Point lastPoint = null;
			final double[][] thisLine = iter.next();
			for (int i = 0; i < thisLine.length; i++)
			{
				final Point2D raw = new Point2D.Double(thisLine[i][0], thisLine[i][1]);
				final Point2D postTurn = new Point2D.Double();
				final Point2D postScale = new Point2D.Double();

				scale.transform(raw, postScale);
				thisRotation.transform(postScale, postTurn);

				final double latM = MWC.Algorithms.Conversions.m2Degs(postTurn.getY())
						+ theLocation.getLat();
				final double longM = MWC.Algorithms.Conversions.m2Degs(postTurn.getX())
						+ theLocation.getLong();

				final WorldLocation loc = new WorldLocation(latM, longM, 0d);

				// double thisX = MWC.Algorithms.Conversions.m2Degs(thisLine[i][0])
				// + theLocation.getLong();
				// double thisY = MWC.Algorithms.Conversions.m2Degs(thisLine[i][1])
				// + theLocation.getLat();
				//
				// Point2D before = new Point2D.Double(thisX, thisY);
				// Point2D after2 = new Point2D.Double();
				// Point2D after3 = new Point2D.Double();
				//
				// // do the rotate
				// thisRotation.transform(before, after2);
				//
				// // and the scale
				// scale.transform(after2, after3);

				final java.awt.Point newP = dest.toScreen(loc);

				if (lastPoint != null)
				{

					dest.drawLine(lastPoint.x, lastPoint.y, newP.x, newP.y);
				}

				lastPoint = new Point(newP);
			}
		}

	}

}
