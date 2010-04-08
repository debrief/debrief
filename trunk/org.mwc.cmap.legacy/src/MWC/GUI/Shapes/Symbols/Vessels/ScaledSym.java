package MWC.GUI.Shapes.Symbols.Vessels;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public abstract class ScaledSym extends PlainSymbol
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

	/** give us a chance to cache the coordinates
	 * 
	 * @return
	 */
	private Vector<double[][]> getMyCoords()
	{
		if(_myCoords == null)
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

		// create our centre point

		AffineTransform thisRotation = AffineTransform.getRotateInstance( -direction , theLocation.getLong(), theLocation.getLat());

		Vector<double[][]> hullLines = getMyCoords();


		Iterator<double[][]> iter = hullLines.iterator();
		while (iter.hasNext())
		{
			Point lastPoint = null;
			double[][] thisLine = iter.next();
			for (int i = 0; i < thisLine.length; i++)
			{
				double thisX = MWC.Algorithms.Conversions.m2Degs(thisLine[i][0])
						+ theLocation.getLong();
				double thisY = MWC.Algorithms.Conversions.m2Degs(thisLine[i][1])
						+ theLocation.getLat();

				Point2D before = new Point2D.Double(thisX, thisY);
				Point2D after = new Point2D.Double();
				after = thisRotation.transform(before, after);

				java.awt.Point newP = dest.toScreen(new WorldLocation(after.getY(),
						after.getX(), 0));

				if (lastPoint != null)
				{

					dest.drawLine(lastPoint.x, lastPoint.y, newP.x, newP.y);
				}

				lastPoint = new Point(newP);
			}
		}

	}

}
