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
package MWC.GUI.Shapes.Symbols.Vessels;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public abstract class WorldScaledSym extends PlainSymbol
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<double[][]> _myCoords;

	/** the (user-specified) real world dimensions of the symbol
	 * 
	 */
	protected WorldDistance _subjectLength;
	protected WorldDistance _subjectWidth;

	/** constructor - including the default dimensions
	 * 
	 * @param length length of the platform
	 * @param width width (beam) of the platform
	 */
	public WorldScaledSym(final WorldDistance length, final WorldDistance width)
	{
		super();
		this._subjectLength = length;
		this._subjectWidth = width;
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
		return _subjectLength;
	}

	public void setLength(final WorldDistance length)
	{
		_subjectLength = length;
	}

	public WorldDistance getWidth()
	{
		return _subjectWidth;
	}

	public void setHeight(final WorldDistance width)
	{
		_subjectWidth = width;
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

		final java.awt.Point origin = dest.toScreen(theLocation);		

		// create centre rotation
		final AffineTransform thisRotation = AffineTransform.getRotateInstance(
				-direction, 0, 0);

		// do the scale-factor
		final double lenFactor =  _subjectLength.getValueIn(WorldDistance.METRES) / getLengthNormalFactor() ;
		final double widFactor =   _subjectWidth.getValueIn(WorldDistance.METRES) / getWidthNormalFactor();

		final AffineTransform scaleToBoat = AffineTransform
				.getScaleInstance(widFactor, lenFactor);

		// scale it to the screen coords - let's use 1 km step instead of 1m, since  
		// we'll get too much quantisation from pixel measurements with 1px step.
		WorldVector lenOffset = new WorldVector(Math.PI/2, Conversions.m2Degs(1000), 0);
		WorldLocation newEnd = theLocation.add(lenOffset);
		Point lenScreen = dest.toScreen(newEnd);
		double lenPixels = (origin.x - lenScreen.x) / 1000.0;
		
		final AffineTransform boatToScreen = AffineTransform
				.getScaleInstance(-lenPixels, -lenPixels);
				
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
				final Point2D postScaleToBoat = new Point2D.Double();
				final Point2D postScaleToScreen = new Point2D.Double();
				final Point2D postTurn = new Point2D.Double();

				// scale the point as if it's a 1m boat
				scaleToBoat.transform(raw, postScaleToBoat);
				
				// now scale that 1m to screen pizel size
				boatToScreen.transform(postScaleToBoat, postScaleToScreen);
				
				// and rotate for the direction of motion
				thisRotation.transform(postScaleToScreen, postTurn);
								
				// generate the location of this point
				Point newPos = new Point(origin.x + (int) postTurn.getX(),
						origin.y -(int) postTurn.getY());
				
				if (lastPoint != null)
				{
					dest.drawLine(lastPoint.x, lastPoint.y, newPos.x, newPos.y);
				}

				lastPoint = new Point(newPos);
			}
		}

	}

}
