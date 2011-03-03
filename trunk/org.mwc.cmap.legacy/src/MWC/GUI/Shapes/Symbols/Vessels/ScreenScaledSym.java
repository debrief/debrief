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
	 * accessor for whether this type of symbol is specified in world or screen
	 * coordinates
	 * 
	 * @return
	 */
	public boolean inScreenCoords()
	{
		return true;
	}

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
		// AffineTransform thisRotation = AffineTransform.getRotateInstance(
		// -direction, theLocation.getLong(), theLocation.getLat());
		AffineTransform thisRotation = AffineTransform.getRotateInstance(
				-direction, 0, 0);
		Point centre = dest.toScreen(theLocation);
		AffineTransform thisTranslate = AffineTransform.getTranslateInstance(
				centre.x, centre.y);
		AffineTransform thisScale = AffineTransform.getScaleInstance(getScaleVal(), getScaleVal());

		// find the lines that make up the shape
		Vector<double[][]> hullLines = getMyCoords();

		// and our data objects
		Point2D raw = new Point2D.Double();
		Point2D postTurn = new Point2D.Double();
		Point2D postScale  = new Point2D.Double();
		Point2D postTranslate = new Point2D.Double();

		// start looping through the lines - to paint them
		Iterator<double[][]> iter = hullLines.iterator();
		while (iter.hasNext())
		{
			Point2D lastPoint = null;
			double[][] thisLine = iter.next();
			// now loop through the points
			for (int i = 0; i < thisLine.length; i++)
			{
				raw.setLocation(thisLine[i][0], thisLine[i][1]);

				thisRotation.transform(raw, postTurn);
				thisScale.transform(postTurn, postScale);
				thisTranslate.transform(postScale, postTranslate);

				if (lastPoint != null)
				{
					dest.drawLine((int) lastPoint.getX(), (int) lastPoint.getY(),
							(int) postTranslate.getX(), (int) postTranslate.getY());
				}

				lastPoint = new Point2D.Double(postTranslate.getX(),
						postTranslate.getY());
			}
		}

	}

}
