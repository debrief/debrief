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
	public WorldScaledSym(WorldDistance length, WorldDistance width)
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

	public void setLength(WorldDistance length)
	{
		_length = length;
	}

	public WorldDistance getWidth()
	{
		return _width;
	}

	public void setHeight(WorldDistance width)
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
		java.awt.Dimension res = new java.awt.Dimension(
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
	public void paint(CanvasType dest, WorldLocation centre)
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
	public void paint(CanvasType dest, WorldLocation theLocation, double direction)
	{
		// set the colour
		dest.setColor(getColor());

		// create centre rotation
		AffineTransform thisRotation = AffineTransform.getRotateInstance(
				-direction, 0, 0);

		// do the scale-factor
		double lenFactor =  _length.getValueIn(WorldDistance.METRES) / getLengthNormalFactor() ;
		double widFactor =   _width.getValueIn(WorldDistance.METRES) / getWidthNormalFactor();
		
		AffineTransform scale = AffineTransform
				.getScaleInstance(widFactor, lenFactor);

		// find the lines that make up the shape
		Vector<double[][]> hullLines = getMyCoords();

		// start looping through - to paint them
		Iterator<double[][]> iter = hullLines.iterator();
		while (iter.hasNext())
		{
			Point lastPoint = null;
			double[][] thisLine = iter.next();
			for (int i = 0; i < thisLine.length; i++)
			{
				Point2D raw = new Point2D.Double(thisLine[i][0], thisLine[i][1]);
				Point2D postTurn = new Point2D.Double();
				Point2D postScale = new Point2D.Double();

				scale.transform(raw, postScale);
				thisRotation.transform(postScale, postTurn);

				double latM = MWC.Algorithms.Conversions.m2Degs(postTurn.getY())
						+ theLocation.getLat();
				double longM = MWC.Algorithms.Conversions.m2Degs(postTurn.getX())
						+ theLocation.getLong();

				WorldLocation loc = new WorldLocation(latM, longM, 0d);

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

				java.awt.Point newP = dest.toScreen(loc);

				if (lastPoint != null)
				{

					dest.drawLine(lastPoint.x, lastPoint.y, newP.x, newP.y);
				}

				lastPoint = new Point(newP);
			}
		}

	}

}
