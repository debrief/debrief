// Copyright MWC 1999, Debrief 3 Project
// $RCSfile$
// @author $Author$
// @version $Revision$
// $Log$
// Revision 1.1  2005-05-25 14:18:18  Ian.Mayo
// Refactor to provide more useful SWT GC wrapper (hopefully suitable for buffered images)
//
// Revision 1.4  2005/05/24 13:26:42  Ian.Mayo
// Start including double-click support.
//
// Revision 1.3  2005/05/24 07:35:57  Ian.Mayo
// Ignore anti-alias bits, sort out text-writing in filling areas
//
// Revision 1.2  2005/05/20 15:34:44  Ian.Mayo
// Hey, practically working!
//
// Revision 1.1  2005/05/20 13:45:03  Ian.Mayo
// Start doing chart
//
//

package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.property_support.FontHelper;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Swing implementation of a canvas.
 */
public class SWTCanvasAdapter implements CanvasType, Serializable, Editable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/** remember the background color - SWT has trouble remembering it
	 * 
	 */
	java.awt.Color _backgroundColor;
	
	/**
	 * the projection in use
	 */
	protected PlainProjection _theProjection;

	/**
	 * our graphics object - only valid between 'start' and 'stop' paint events.
	 */
	private GC _theDest = null;

	/**
	 * the list of registered painters for this canvas.
	 */
	protected Vector _thePainters;

	/**
	 * the dimensions of the canvas - we keep our own track of this in order to
	 * handle the number of resize messages we get.
	 */
	protected java.awt.Dimension _theSize;


	/**
	 * our tool tip handler.
	 */
	private CanvasType.TooltipHandler _tooltipHandler;

	/**
	 * our editor.
	 */
	transient private Editable.EditorType _myEditor;

	/**
	 * a list of the line-styles we know about.
	 */
	static private java.util.HashMap _myLineStyles = null;

	/**
	 * the current line width.
	 */
	private float _lineWidth;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * default constructor.
	 */
	public SWTCanvasAdapter()
	{
		// start with our background colour
		setBackgroundColor(java.awt.Color.black);
		

		// initialisation
		_thePainters = new Vector(0, 1);

		// create our projection
		_theProjection = new FlatProjection();
	}


	// ////////////////////////////////////////////////////
	// screen redraw related
	// ////////////////////////////////////////////////////
	
	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////
	// projection related
	// //////////////////////////////////////////////////////////
	/**
	 * update the projection.
	 */
	public final void setProjection(final PlainProjection theProjection)
	{
		_theProjection = theProjection;
	}

	/**
	 * switch anti-aliasing on or off.
	 * 
	 * @param val
	 *          yes/no
	 */
	protected void switchAntiAliasOn(final boolean val)
	{
		// todo: sort out this bit..
		// // ignore this
		// final Graphics2D g2 = (Graphics2D) _theDest;
		//
		// if (g2 == null)
		// return;
		//

		// try
		// {
		// if (val)
		// {
		// _theDest.setAntialias(SWT.ON);
		// }
		// else
		// {
		// _theDest.setAntialias(SWT.OFF);
		// }
		// } catch (RuntimeException e)
		// {
		// // CorePlugin.logError(Status.ERROR, "Graphics library not found", e);
		// }
	}

	/**
	 * get the current projection.
	 */
	public final PlainProjection getProjection()
	{
		return _theProjection;
	}

	/**
	 * convenience function.
	 */
	public final java.awt.Point toScreen(final WorldLocation val)
	{
		return _theProjection.toScreen(val);
	}

	/**
	 * convenience function.
	 */
	public final WorldLocation toWorld(final java.awt.Point val)
	{
		return _theProjection.toWorld(val);
	}

	/**
	 * re-determine the area of data we cover. then resize to cover it
	 */
	public final void rescale()
	{

		// get the data area for the current painters
		WorldArea theArea = null;
		final Enumeration enumer = _thePainters.elements();
		while (enumer.hasMoreElements())
		{
			final CanvasType.PaintListener thisP = (CanvasType.PaintListener) enumer
					.nextElement();
			final WorldArea thisArea = thisP.getDataArea();
			if (thisArea != null)
			{
				if (theArea == null)
					theArea = new WorldArea(thisArea);
				else
					theArea.extend(thisArea);
			}
		}

		// check we have found a valid area
		if (theArea != null)
		{
			// so, we now have the data area for everything which
			// wants to plot to it, give it to the projection
			_theProjection.setDataArea(theArea);

			// get the projection to refit-itself
			_theProjection.zoom(0.0);
		}

	}

	// public final void setSize(final int p1, final int p2)
	// {
	// // ok, store the dimension
	// _theSize = new Dimension(p1, p2);
	//
	// _myCanvas.setSize(p1, p2);
	//  	
	// // reset our double buffer, since we've changed size
	// _dblBuff = null;
	// }
	//  
	/**
	 * handler for a screen resize - inform our projection of the resize then
	 * inform the painters.
	 */
	protected void setScreenSize(final java.awt.Dimension p1)
	{
		Dimension theDim = p1;
		// check if this is a real resize
		if ((_theSize == null) || (!_theSize.equals(p1)))
		{

			// ok, now remember it
			_theSize = p1;

			// and pass it onto the projection
			_theProjection.setScreenArea(p1);
		}
	}

	// ///////////////////////////////////////////////////////////
	// graphics plotting related
	// //////////////////////////////////////////////////////////
	/**
	 * find out the current metrics.
	 * 
	 * @param theFont
	 *          the font to try
	 * @return the metrics object
	 */
	// public final java.awt.FontMetrics getFontMetrics(final java.awt.Font
	// theFont)
	// {
	// java.awt.FontMetrics res = null;
	//
	// if (_theDest != null)
	// {
	// if (theFont != null)
	// res = _theDest.getFontMetrics(theFont);
	// else
	// res = _theDest.getFontMetrics();
	// }
	//
	// return res;
	// }
	public final int getStringHeight(final java.awt.Font theFont)
	{
		int res = 0;
		// final java.awt.FontMetrics fm = getFontMetrics(theFont);
		// if (fm != null)
		// res = fm.getHeight();

		res = _theDest.getFontMetrics().getHeight();

		return res;
	}

	public final int getStringWidth(final java.awt.Font theFont,
			final String theString)
	{
		int res = 0;

		res = _theDest.getFontMetrics().getAverageCharWidth() * theString.length();

		// final java.awt.FontMetrics fm = getFontMetrics(theFont);
		// if (fm != null)
		// res = fm.stringWidth(theString);

		return res;
	}

	/**
	 * ONLY USE THIS FOR NON-PERSISTENT PLOTTING
	 */
	public final java.awt.Graphics getGraphicsTemp()
	{
		System.err.println("graphics temp not implemented...");
		java.awt.Graphics res = null;
		// /** if we are in a paint operation already,
		// * return the graphics object, since it may
		// * be a double-buffering image
		// */
		// if (_theDest != null)
		// {
		// res = _theDest.create(); // return a copy, so the user can dispose it
		// }
		// else
		// {
		// if (_dblBuff != null)
		// {
		// res = _dblBuff.getGraphics();
		// }
		// else
		// {
		// }
		// }
		//
		return res;
	}

	public final void setFont(final java.awt.Font theFont)
	{
		// super.setFont(theFont);
	}

	public final boolean drawImage(final java.awt.Image img, final int x,
			final int y, final int width, final int height,
			final ImageObserver observer)
	{
		if (_theDest == null)
			return true;

		// return _theDest.drawImage(img, x, y, width, height, observer);

		return false;

	}

	public final void drawLine(final int x1, final int y1, final int x2,
			final int y2)
	{
		if (_theDest == null)
			return;

		// doDecide whether to anti-alias this line
		this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this.getLineWidth()));

		// check that the points are vaguely plottable
		if ((Math.abs(x1) > 9000) || (Math.abs(y1) > 9000) || (Math.abs(x2) > 9000)
				|| (Math.abs(y2) > 9000))
		{
			return;
		}

		// double-check
		if (_theDest == null)
			return;

		_theDest.drawLine(x1, y1, x2, y2);
	}

	/**
	 * draw a filled polygon
	 * 
	 * @param xPoints
	 *          list of x coordinates
	 * @param yPoints
	 *          list of y coordinates
	 * @param nPoints
	 *          length of list
	 */
	public final void fillPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints)
	{
		if (_theDest == null)
			return;

		// translate the polygon to SWT format
		int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

		_theDest.fillPolygon(poly);
	}

	public static int[] getPolygonArray(int[] xPoints, int[] yPoints, int nPoints)
	{
		int[] poly = new int[nPoints * 2];

		for (int i = 0; i < nPoints; i++)
		{
			poly[i] = xPoints[i];
			poly[i + 1] = yPoints[i];
		}

		return poly;
	}

	/**
	 * drawPolyline
	 * 
	 * @param xPoints
	 *          list of x coordinates
	 * @param yPoints
	 *          list of y coordinates
	 * @param nPoints
	 *          length of list
	 */
	public final void drawPolyline(final int[] xPoints, final int[] yPoints,
			final int nPoints)
	{
		if (_theDest == null)
			return;

		// doDecide whether to anti-alias this line
		this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this.getLineWidth()));

		// translate the polygon to SWT format
		int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

		_theDest.drawPolyline(poly);
	}

	/**
	 * drawPolygon.
	 * 
	 * @param xPoints
	 *          list of x coordinates
	 * @param yPoints
	 *          list of y coordinates
	 * @param nPoints
	 *          length of list
	 */
	public final void drawPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints)
	{
		if (_theDest == null)
			return;

		// doDecide whether to anti-alias this line
		this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this.getLineWidth()));

		// translate the polygon to SWT format
		int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

		_theDest.drawPolygon(poly);
	}

	public final void drawOval(final int x, final int y, final int width,
			final int height)
	{
		if (_theDest != null)
			this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this.getLineWidth()));

		if (_theDest != null)
			_theDest.drawOval(x, y, width, height);
	}

	public final void fillOval(final int x, final int y, final int width,
			final int height)
	{
		if (_theDest != null)
			_theDest.fillOval(x, y, width, height);
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);
	}

	public final void setColor(final java.awt.Color theCol)
	{
		if (_theDest == null)
			return;

		// transfer the color
		Color newCol = ColorHelper.getColor(theCol);

		_theDest.setForeground(newCol);
		_theDest.setBackground(newCol);
	}

	static public java.awt.BasicStroke getStrokeFor(final int style)
	{
		if (_myLineStyles == null)
		{
			_myLineStyles = new java.util.HashMap(5);
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.SOLID),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[] { 5, 0 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.DOTTED),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[] { 2, 6 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.DOT_DASH),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[] { 4, 4, 12, 4 },
							0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.SHORT_DASHES),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[] { 6, 6 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.LONG_DASHES),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[] { 12, 6 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.UNCONNECTED),
					new java.awt.BasicStroke(1));
		}

		return (java.awt.BasicStroke) _myLineStyles.get(new Integer(style));
	}

	public final void setLineStyle(final int style)
	{
		_theDest.setLineStyle(style);
		// final java.awt.BasicStroke stk = getStrokeFor(style);
		// final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
		// g2.setStroke(stk);
	}

	/**
	 * set the width of the line, in pixels
	 */
	public final void setLineWidth(float width)
	{
		// check we've got a valid width
		width = Math.max(width, 0);

		_lineWidth = width;

		// are we currently in a plot operation?
		if (_theDest != null)
		{
			// create the stroke
			// final java.awt.BasicStroke stk = new BasicStroke(width);
			// final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
			// g2.setStroke(stk);
			_theDest.setLineWidth((int) width);
		}
	}

	/**
	 * get the width of the line, in pixels
	 */
	public final float getLineWidth()
	{
		final float res;

		// are we currently in a plot operation?
		if (_theDest != null)
		{
			// create the stroke
			res = _theDest.getLineWidth();
			// final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
			// final BasicStroke bs = (BasicStroke) g2.getStroke();
			// res = bs.getLineWidth();
		}
		else
		{
			res = _lineWidth;
		}

		return res;
	}

	public final void drawArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle)
	{
		if (_theDest != null)
		{
			// doDecide whether to anti-alias this line
			this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this.getLineWidth()));
		}

		if (_theDest != null)
		{
			_theDest.drawArc(x, y, width, height, startAngle, arcAngle);
		}
	}

	public final void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle)
	{
		if (_theDest != null)
			_theDest.fillArc(x, y, width, height, startAngle, arcAngle);
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);

	}

	public final void startDraw(final Object theVal)
	{
		_theDest = (GC) theVal;
		
		// initialise the background color
		_theDest.setBackground(_theDest.getBackground());

		// set the thickness
		// final BasicStroke bs = new BasicStroke(_lineWidth);
		// final Graphics2D g2 = (Graphics2D) _theDest;
		// g2.setStroke(bs);
	}

	public final void endDraw(final Object theVal)
	{
		_theDest = null;
	}

	public void drawText(final String theStr, final int x, final int y)
	{
		if (_theDest == null)
			return;

		drawText(theStr, x, y);
	}

	public void drawText(final java.awt.Font theFont, final String theStr,
			final int x, final int y)
	{
		if (_theDest == null)
			return;

		// doDecide the anti-alias
		this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThis(theFont));

		// get/set the font
		org.eclipse.swt.graphics.Font swtFont = FontHelper.convertFont(theFont);
		_theDest.setFont(swtFont);
		
		// shift the y.  JDK uses bottom left coordinate, SWT uses top-left
		FontData[] data = swtFont.getFontData();
		FontData first = data[0];
		int y2 = y - first.height;
		_theDest.drawString(theStr, x, y2, true);
	}

	public final void drawRect(final int x1, final int y1, final int wid,
			final int height)
	{
		if (_theDest == null)
			return;

		// doDecide whether to anti-alias this line
		this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this.getLineWidth()));

		if (_theDest == null)
			return;

		_theDest.drawRectangle(x1, y1, wid, height);
	}

	public final void fillRect(final int x, final int y, final int wid,
			final int height)
	{
		if (_theDest == null)
			return;

	//	fillOn();

	//	_theDest.setBackground(ColorHelper.getColor(java.awt.Color.green));
		_theDest.fillRectangle(x, y, wid, height);
		
		// now, the fill only fills in the provided rectangle. we also have to paint in it's border
		_theDest.drawRectangle(x, y, wid, height);

	//	fillOff();
	}

	private static Color _theOldColor;

	protected void fillOn()
	{
		_theOldColor = _theDest.getBackground();
		Color theForeColor = _theDest.getForeground();
		_theDest.setBackground(theForeColor);
	}

	protected void fillOff()
	{
		_theDest.setBackground(_theOldColor);
		_theOldColor = null;
	}

	/**
	 * get the current background colour
	 */
	public final java.awt.Color getBackgroundColor()
	{
		// don't worry - we've remembered it.
		return _backgroundColor;
	}

	/**
	 * set the current background colour, and trigger a screen update
	 */
	public final void setBackgroundColor(final java.awt.Color theColor)
	{
		// remember it
		_backgroundColor = theColor;
		
		// convert to SWT
		Color swtCol = ColorHelper.getColor(theColor);

		// set the colour in the parent
		if(_theDest != null)
			_theDest.setBackground(swtCol);
	}

	public final BoundedInteger getLineThickness()
	{
		return new BoundedInteger((int) this.getLineWidth(), 0, 4);
	}

	public final void setLineThickness(final BoundedInteger val)
	{
		setLineWidth(val.getCurrent());
	}

	// /////////////////////////////////////////////////////////
	// handle tooltip stuff
	// /////////////////////////////////////////////////////////
	public final void setTooltipHandler(final CanvasType.TooltipHandler handler)
	{
		_tooltipHandler = handler;
	}

	/**
	 * get a string describing the current screen & world location
	 */
	public final String getToolTipText(final MouseEvent p1)
	{
		String res = null;
		if (_tooltipHandler != null)
		{

			final java.awt.Point pt = p1.getPoint();
			// check we have a valid projection
			final java.awt.Dimension dim = getProjection().getScreenArea();
			if (dim != null)
			{
				if (dim.width > 0)
				{
					final WorldLocation loc = toWorld(pt);
					if (loc != null)
						res = _tooltipHandler.getString(loc, pt);
				}
			}
		}

		return res;
	}

	// //////////////////////////////////////////////////////////
	// painter handling
	// //////////////////////////////////////////////////////////
	public final void addPainter(final CanvasType.PaintListener listener)
	{
		_thePainters.addElement(listener);
	}

	public final void removePainter(final CanvasType.PaintListener listener)
	{
		_thePainters.removeElement(listener);
	}

	public final Enumeration getPainters()
	{
		return _thePainters.elements();
	}


	/**
	 * first repaint the plot, then trigger a screen update
	 */
	public void updateMe()
	{
	}

	// ////////////////////////////////////////////////////
	// bean/editable methods
	// ///////////////////////////////////////////////////
	public final Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new CanvasInfo(this);

		return _myEditor;
	}

	public final boolean hasEditor()
	{
		return true;
	}

	/**
	 * provide close method, clear elements.
	 */
	public void close()
	{
		_thePainters.removeAllElements();
		_thePainters = null;
		_theProjection = null;
		_theDest = null;
		_theSize = null;
		_tooltipHandler = null;
	}

	/**
	 * return our name (used in editing)
	 */
	public final String toString()
	{
		return "Appearance";
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class CanvasInfo extends Editable.EditorType
	{

		public CanvasInfo(final SWTCanvasAdapter data)
		{
			super(data, data.toString(), "");
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = {
						prop("BackgroundColor", "the background color"),
						prop("LineThickness", "the line thickness"), };

				return res;

			} catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////
	// methods to support anti-alias decisions
	// ////////////////////////////////////////////////

	/**
	 * do we anti-alias this font.
	 * 
	 * @param theFont
	 *          the font we are looking at
	 * @return yes/no decision
	 */
	public static boolean antiAliasThis(final Font theFont)
	{
		boolean res = false;

		final int size = theFont.getSize();
		final boolean isBold = theFont.isBold();

		if (size >= 14)
		{
			res = true;
		}
		else
		{
			if (isBold && (size >= 12))
			{
				res = true;
			}
		}

		return res;
	}

	/**
	 * doDecide whether this line thickness could be anti-aliased.
	 * 
	 * @param width
	 *          the line width setting
	 * @return yes/no
	 */
	public static boolean antiAliasThisLine(final float width)
	{
		boolean res = false;

		if (width > 1)
			res = true;

		return res;
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return "SWT Canvas";
	}

	public Dimension getSize()
	{
		return _theSize;
	}


}
