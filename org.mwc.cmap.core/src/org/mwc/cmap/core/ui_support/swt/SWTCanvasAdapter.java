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
// $RCSfile: SWTCanvasAdapter.java,v $
// @author $Author$
// @version $Revision$
// $Log: SWTCanvasAdapter.java,v $
// Revision 1.26  2007/01/25 15:53:57  ian.mayo
// Better GC maangement
//
// Revision 1.25  2006/11/28 10:52:04  Ian.Mayo
// Use better width measurer
//
// Revision 1.24  2006/11/16 08:45:51  Ian.Mayo
// Improve text layout
//
// Revision 1.23  2006/09/19 10:41:59  Ian.Mayo
// Handle omitted font
//
// Revision 1.22  2006/05/17 15:20:07  Ian.Mayo
// We need the bkgnd color for filled shapes to work
//
// Revision 1.21  2006/05/17 08:35:08  Ian.Mayo
// Refactor setting background color
//
// Revision 1.20  2006/05/11 15:04:43  Ian.Mayo
// Ditch gash
//
// Revision 1.19  2006/05/02 13:44:24  Ian.Mayo
// Allow us to over-ride setColor method
//
// Revision 1.18  2006/04/05 08:15:00  Ian.Mayo
// Allow screen size to be overwritten
//
// Revision 1.17  2006/03/31 14:30:14  Ian.Mayo
// Correct silly typo preventing arcs getting plotted
//
// Revision 1.16  2006/03/23 15:22:01  Ian.Mayo
// Cache local colour & line width, so we don't have to retrieve them from operating system
//
// Revision 1.15  2006/01/17 10:28:13  Ian.Mayo
// Better error management
//
// Revision 1.14  2005/09/13 10:58:53  Ian.Mayo
// Make plot background color editable
//
// Revision 1.13  2005/09/08 11:01:42  Ian.Mayo
// Makeing more robust when plotting fails through disposed GC
//
// Revision 1.12  2005/08/31 15:03:09  Ian.Mayo
// Check the dest isn't disposed before we call it
//
// Revision 1.11  2005/06/14 08:22:18  Ian.Mayo
// Minor tidying
//
// Revision 1.10  2005/06/13 09:08:41  Ian.Mayo
// Tidy up font management, investigate antiAlias bug
//
// Revision 1.9  2005/06/10 14:11:04  Ian.Mayo
// Implement setFont support, minor tidying
//
// Revision 1.8  2005/06/09 14:51:50  Ian.Mayo
// Implement SWT plotting
//
// Revision 1.7  2005/06/09 10:59:09  Ian.Mayo
// Correct silly drawText error
//
// Revision 1.6  2005/06/07 10:49:24  Ian.Mayo
// Minor tidying
//
// Revision 1.5  2005/06/06 14:50:45  Ian.Mayo
// Correctly support plotting polylines & line-styles
//
// Revision 1.4  2005/06/01 13:24:53  Ian.Mayo
// Safe fall-over for missing GDI libs
//
// Revision 1.3  2005/06/01 10:45:08  Ian.Mayo
// Re-instate anti-alias graphics
//
// Revision 1.2  2005/05/25 15:31:54  Ian.Mayo
// Get double-buffering going
//
// Revision 1.1  2005/05/25 14:18:18  Ian.Mayo
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

package org.mwc.cmap.core.ui_support.swt;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.ImageObserver;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.property_support.FontHelper;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * SWT implementation of a canvas.
 */
public class SWTCanvasAdapter implements CanvasType, Serializable, Editable,
		ExtendedCanvasType
{

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final float UNSET_LINE_WIDTH = -1;

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * remember the background color - SWT has trouble remembering it
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
	protected Vector<PaintListener> _thePainters;

	/**
	 * the dimensions of the canvas - we keep our own track of this in order to
	 * handle the number of resize messages we get.
	 */
	protected java.awt.Dimension _theSize;

	/**
	 * our tool tip handler.
	 */
	protected CanvasType.TooltipHandler _tooltipHandler;

	/**
	 * our editor.
	 */
	transient private Editable.EditorType _myEditor;

	/**
	 * a list of the line-styles we know about.
	 */
	static private java.util.HashMap<Integer, BasicStroke> _myLineStyles = null;

	/**
	 * the current color
	 */
	private java.awt.Color _currentColor;

	/**
	 * the current line width.
	 */
	private float _lineWidth = UNSET_LINE_WIDTH;

	/**
	 * flag for whether we have the GDI library availble. The plotting algs will
	 * keep on failing if it's not. We should remember when its not avaialble, and
	 * not bother calling from there on.
	 */
	private boolean _gdiAvailable = true;

	/**
	 * and our default background color
	 * 
	 */
	private final java.awt.Color DEFAULT_BACKGROUND_COLOR = java.awt.Color.LIGHT_GRAY;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * default constructor.
	 */
	public SWTCanvasAdapter(final PlainProjection proj)
	{
		// start with our background colour
		setBackgroundColor(DEFAULT_BACKGROUND_COLOR);

		// initialisation
		_thePainters = new Vector<PaintListener>(0, 1);

		// create our projection
		if (proj != null)
			_theProjection = proj;
		else
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
		// ok - let's not use the new projection. We'll keep our own projection,
		// but we'll copy the data viewport
		final WorldArea dataArea = theProjection.getDataArea();
		if (dataArea != null)
		{
			_theProjection.setDataArea(dataArea);
		}
	}

	/**
	 * switch anti-aliasing on or off.
	 * 
	 * @param val
	 *          yes/no
	 */
	protected void switchAntiAliasOn(final boolean val)
	{

		// hmmm, has the GDI retrieval already failed
		if (_gdiAvailable)
		{
			// well, this is either the first time, or we already know it's
			// there for
			// us
			try
			{
				if (!_theDest.isDisposed())
					if (!_theDest.isDisposed())

					{

						if (val)
						{
							if (_theDest.getAntialias() != SWT.ON)
								_theDest.setAntialias(SWT.ON);
						}
						else
						{
							if (_theDest.getAntialias() != SWT.OFF)
								_theDest.setAntialias(SWT.OFF);
						}
					}
			}
			catch (final RuntimeException e)
			{
				CorePlugin.logError(Status.INFO, "Graphics library not found", e);
				System.err.println("GDIplus graphics library not found");
				_gdiAvailable = false;
			}
		}
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
		final Enumeration<PaintListener> enumer = _thePainters.elements();
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
		//	_theProjection.zoom(0.0);
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
	public void setScreenSize(final java.awt.Dimension p1)
	{
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

		if (theFont != null)
			res = theFont.getSize();
		else
			res = _theDest.getFontMetrics().getHeight();

		// if (!_theDest.isDisposed())
		// res = _theDest.getFontMetrics().getHeight();
		return res;
	}

	public final int getStringWidth(final java.awt.Font theFont,
			final String theString)
	{
		int res = 0;

		// set the font to start with,
		if (!_theDest.isDisposed())
		{
			if (theFont != null)
			{
				final org.eclipse.swt.graphics.Font myFont = FontHelper.convertFontFromAWT(theFont);
				if (!_theDest.isDisposed())
					_theDest.setFont(myFont);
			}

			for (int thisC = 0; thisC < theString.length(); thisC++)
			{
				final char thisChar = theString.charAt(thisC);
				int thisWid;
				// just check if it's a space - we're not getting the right
				// width back
				if (thisChar == ' ')
				{
					thisWid = _theDest.getFontMetrics().getAverageCharWidth();
				}
				else
				{
					// thisWid = _theDest.getCharWidth(thisChar);
					thisWid = _theDest.getAdvanceWidth(thisChar);
				}
				res += thisWid;
			}
		}
		return res;
	}

	/**
	 * ONLY USE THIS FOR NON-PERSISTENT PLOTTING
	 */
	public final java.awt.Graphics getGraphicsTemp()
	{
		System.err.println("graphics temp not implemented...");
		final java.awt.Graphics res = null;
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
		final org.eclipse.swt.graphics.Font swtFont = FontHelper.convertFontFromAWT(theFont);
		if (!_theDest.isDisposed())
			_theDest.setFont(swtFont);
	}

	public final boolean drawImage(final java.awt.Image img, final int x0,
			final int y0, final int width, final int height,
			final ImageObserver observer)
	{
		if (_theDest == null)
			return true;

		final PaletteData palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
		// PaletteData palette = new PaletteData(new RGB[]{new RGB(255,0,0), new
		// RGB(0,255,0)});
		final ImageData imageData = new ImageData(48, 48, 24, palette);

		for (int x = 0; x < 48; x++)
		{
			for (int y = 0; y < 48; y++)
			{
				if (y > 11 && y < 35 && x > 11 && x < 35)
				{
					imageData.setPixel(x, y, SWTRasterPainter.toSWTColor(255, 0, 0)); // Set
					// the
					// center
					// to
					// red
				}
				else
				{
					imageData.setPixel(x, y, SWTRasterPainter.toSWTColor(0, 255, 0)); // Set
					// the
					// outside
					// to
					// green
				}
			}
		}

		final Image image = new Image(Display.getCurrent(), imageData);

		if (!_theDest.isDisposed())
			_theDest.drawImage(image, 0, 0);

		// return _theDest.drawImage(img, x, y, width, height, observer);

		image.dispose();

		return false;

	}

	public final boolean drawSWTImage(final Image img, final int x, final int y,
			final int width, final int height, final int alphaTransparency)
	{
		if (_theDest == null)
			return true;

		if (!_theDest.isDisposed())
		{
			_theDest.setAlpha(alphaTransparency);
			if (Platform.OS_LINUX.equals(Platform.getOS()))
			{
				// SPECIAL CASE: It should fix background issue on Linux (it is constrained 
				// on Linux; maybe it should be the same on Mac).	The issue happen 
				// because "new GC(image)" ignores transparency on Linux. 
				// It is probably a bug in SWT.
				ImageData data = img.getImageData();
				final java.awt.Color trColor = java.awt.Color.black;
				final int transPx = data.palette.getPixel(new RGB(trColor.getRed(),
						trColor.getGreen(), trColor.getBlue()));
				data.transparentPixel = transPx;
				final Image image = new Image(Display.getCurrent(), data);
				_theDest.drawImage(image, x, y, width, height, x, y, width, height);
				image.dispose();
			}
			else
			{
				_theDest.drawImage(img, x, y, width, height, x, y, width, height);
			}
			_theDest.setAlpha(255);
		}

		// return _theDest.drawImage(img, x, y, width, height, observer);

		return false;

	}

	public final void drawLine(final int x1, final int y1, final int x2,
			final int y2)
	{
		if (_theDest == null)
			return;

		// Decide whether to anti-alias this line
		final float thisWid = this.getLineWidth();
		final boolean doAntiAlias = SWTCanvasAdapter.antiAliasThisLine(thisWid);

		// BUG: when we adjust the anti-alaising, the colours in the ETOPO key
		// were
		// getting messed up. bugger.
		// The bug was fixed on 31st May 2005. Builds after this date should be
		// ok.
		this.switchAntiAliasOn(doAntiAlias);

		// ok, may as well go for it now..
		if (!_theDest.isDisposed())
		{
			int xmin, ymin, xmax, ymax;
			xmin = ymin = 0;
			xmax = this.getSize().width;
			ymax = this.getSize().height;
			SWTClipper.drawLine(_theDest, x1, y1, x2, y2, xmin, xmax, ymin, ymax);
		}
	}

	public final void semiFillPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints)
	{
		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
		{

			// translate the polygon to SWT format
			final int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

			_theDest.setAlpha(45);
			_theDest.fillPolygon(poly);
			_theDest.setAlpha(255);
			_theDest.drawPolygon(poly);
		}
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

		if (!_theDest.isDisposed())
		{

			// translate the polygon to SWT format
			final int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

			_theDest.fillPolygon(poly);
		}
	}

	private static int[] getPolygonArray(final int[] xPoints, final int[] yPoints, final int nPoints)
	{
		final int[] poly = new int[nPoints * 2];

		for (int i = 0; i < nPoints; i++)
		{
			poly[2 * i] = xPoints[i];
			poly[2 * i + 1] = yPoints[i];
		}

		return poly;
	}

	/**
	 * drawPolyline
	 * 
	 * @param xPoints
	 *          list of x coordinates
	 * @param yPoints
	 *          list of y coordinates (or null if xPoints contains both lists - in
	 *          SWT PolyLine format)
	 * @param nPoints
	 *          length of list (ignored if yPoints is null)
	 */
	public final void drawPolyline(final int[] xPoints, final int[] yPoints,
			final int nPoints)
	{
		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
		{
			// translate the polygon to SWT format
			final int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

			drawPolyline(poly);
		}
	}

	/**
	 * drawPolyline
	 * 
	 * @param points
	 *          list of x,y coordinates pairs
	 */
	public final void drawPolyline(final int[] points)
	{
		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
		{
			// doDecide whether to anti-alias this line
			this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this
					.getLineWidth()));

			_theDest.drawPolyline(points);
		}
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

		if (!_theDest.isDisposed())
		{

			// doDecide whether to anti-alias this line
			this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this
					.getLineWidth()));

			// translate the polygon to SWT format
			final int[] poly = getPolygonArray(xPoints, yPoints, nPoints);

			if (poly != null)
				if (poly.length > 0)
					_theDest.drawPolygon(poly);
		}
	}

	public final void drawOval(final int x, final int y, final int width,
			final int height)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
				this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this
						.getLineWidth()));

		if (_theDest != null)
			if (!_theDest.isDisposed())
			{
				_theDest.drawOval(x, y, width, height);
			}
	}

	public final void fillOval(final int x, final int y, final int width,
			final int height)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
			{
				_theDest.fillOval(x, y, width, height);
			}
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);
	}

	public final void semiFillOval(final int x, final int y, final int width,
			final int height)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
			{
				_theDest.setAlpha(45);
				_theDest.fillOval(x, y, width, height);
				_theDest.setAlpha(255);
			}
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);
	}

	public void setColor(final java.awt.Color theCol)
	{
		if (_theDest == null)
			return;

		if (theCol != _currentColor)
		{
			_currentColor = theCol;

			// transfer the color
			final Color swtCol = ColorHelper.getColor(theCol);

			if (!_theDest.isDisposed())
			{
				_theDest.setForeground(swtCol);
				_theDest.setBackground(swtCol);
			}
		}
	}

	static synchronized public java.awt.BasicStroke getStrokeFor(final int style)
	{
		if (_myLineStyles == null)
		{
			_myLineStyles = new java.util.HashMap<Integer, BasicStroke>(5);
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.SOLID),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[]
							{ 5, 0 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.DOTTED),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[]
							{ 2, 6 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.DOT_DASH),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[]
							{ 4, 4, 12, 4 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.SHORT_DASHES),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[]
							{ 6, 6 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.LONG_DASHES),
					new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_MITER, 1, new float[]
							{ 12, 6 }, 0));
			_myLineStyles.put(new Integer(MWC.GUI.CanvasType.UNCONNECTED),
					new java.awt.BasicStroke(1));
		}

		return (java.awt.BasicStroke) _myLineStyles.get(new Integer(style));
	}

	public final void setLineStyle(final int style)
	{
		// convert the swing line-style to SWT
		final int SWT_style = style + 1;

		if (!_theDest.isDisposed())
			if (!_theDest.isDisposed())
				_theDest.setLineStyle(SWT_style);
	}

	/**
	 * set the width of the line, in pixels
	 */
	public final void setLineWidth(final float width)
	{
		float theWidth = width;
		// check we've got a valid width
		theWidth = Math.max(theWidth, 0);

		_lineWidth = theWidth;

		// are we currently in a plot operation?
		if (_theDest != null)
		{
			// create the stroke
			// final java.awt.BasicStroke stk = new BasicStroke(width);
			// final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
			// g2.setStroke(stk);
			if (!_theDest.isDisposed())
				_theDest.setLineWidth((int) theWidth);
		}
	}

	/**
	 * get the width of the line, in pixels
	 */
	public final float getLineWidth()
	{
		float res = 0;

		// try to use our cached line-width, to save fetching system pens &
		// things
		if (_lineWidth != UNSET_LINE_WIDTH)
			res = _lineWidth;
		else
		{
			// are we currently in a plot operation?
			if (_theDest != null)
			{

				// create the stroke
				if (!_theDest.isDisposed())
					res = _theDest.getLineWidth();
				// final java.awt.Graphics2D g2 = (java.awt.Graphics2D)
				// _theDest;
				// final BasicStroke bs = (BasicStroke) g2.getStroke();
				// res = bs.getLineWidth();
			}
			else
			{
				res = _lineWidth;
			}
		}

		return res;
	}

	public final void drawArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle)
	{
		if (_theDest != null)
		{
			// doDecide whether to anti-alias this line
			this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this
					.getLineWidth()));
		}

		if (_theDest != null)
		{
			if (!_theDest.isDisposed())
				_theDest.drawArc(x, y, width, height, startAngle, arcAngle);
		}
	}

	public final void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
				_theDest.fillArc(x, y, width, height, startAngle, arcAngle);
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);

	}
	
	public final void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle,
			final int alpha)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
			{
				_theDest.setAlpha(alpha);
				_theDest.fillArc(x, y, width, height, startAngle, arcAngle);
			}
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);

	}

	public final void semiFillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
			{
				_theDest.setAlpha(44);
				_theDest.fillArc(x, y, width, height, startAngle, arcAngle);
				_theDest.setAlpha(255);
				_theDest.drawArc(x, y, width, height, startAngle, arcAngle);
			}
		// else
		// MWC.Utilities.Errors.Trace.trace("Graphics object not available when
		// painting oval - occasionally happens in first pass", false);

	}

	public final void startDraw(final Object theVal)
	{
		_theDest = (GC) theVal;

		// initialise the background color
		if (!_theDest.isDisposed())
			_theDest.setBackground(_theDest.getBackground());

		// and update the size
		this.setScreenSize(new Dimension(_theDest.getClipping().width, _theDest
				.getClipping().height));
		// ._theSize = _theDest.getClipping();

		// set the thickness
		// final BasicStroke bs = new BasicStroke(_lineWidth);
		// final Graphics2D g2 = (Graphics2D) _theDest;
		// g2.setStroke(bs);
	}

	public final void endDraw(final Object theVal)
	{
		// _theDest = null;

		// and forget the line width
		_lineWidth = UNSET_LINE_WIDTH;

		// and the color
		_currentColor = null;
	}

	public void drawText(final String theStr, final int x, final int y,
			final float rotate)
	{
		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
		{

			final FontData[] fd = _theDest.getFont().getFontData();
			final FontData fontData = fd[0];
			// shift the y. JDK uses bottom left coordinate, SWT uses top-left
			int y2 = y;
			if (rotate == 0)
				y2 -= fontData.getHeight();

			final Transform oldTransform = new Transform(_theDest.getDevice());
			_theDest.getTransform(oldTransform);

			final Transform tr = new Transform(_theDest.getDevice());
			tr.translate(x, y2);
			tr.rotate(rotate);
			final Font awFont = new Font(fontData.getName(), fontData.getStyle(),
					fontData.getHeight());
			final int strWidth = getStringWidth(awFont, theStr);
			tr.translate(-x - strWidth / 2, -y2);

			_theDest.setTransform(tr);
			_theDest.drawText(theStr, x, y, true);

			_theDest.setTransform(oldTransform);
			tr.dispose();
		}
	}

	@Override
	public void drawText(final String theStr, final int x, final int y,
			float rotate, boolean above)
	{
		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
		{

			final FontData[] fd = _theDest.getFont().getFontData();
			final FontData fontData = fd[0];
			
			int deltaX = 0, deltaY = 0;
			
			int height = _theDest.getFontMetrics().getDescent() + _theDest.getFontMetrics().getAscent() + _theDest.getFontMetrics().getLeading();
			
			if (!above) {
				double direction = Math.toRadians(rotate);
				deltaX = -(int) (height * Math.cos(direction));
				deltaY = -(int) (height * Math.sin(direction));
			} else {
				
				height = _theDest.getFontMetrics().getLeading();
				double direction = Math.toRadians(rotate);
				deltaX = (int) (height * Math.cos(direction));
				deltaY = (int) (height * Math.sin(direction));				
			}
			
			if (rotate > 180) {
				rotate -= 180;
				final Font awFont = new Font(fontData.getName(), fontData.getStyle(),
						fontData.getHeight());
				final int distance = getStringWidth(awFont, theStr);
	
				double direction = Math.toRadians(rotate-90);
				if (!above) {
					deltaX = - (int) 1.5*deltaX - (int) (distance * Math.cos(direction));
					deltaY = - (int) 1.5*deltaY - (int) (distance * Math.sin(direction));
				} else {
					deltaX -= (int) (distance * Math.cos(direction));
					deltaY -= (int) (distance * Math.sin(direction));
				}
			}
			rotate -= 90;
			
			
			final Transform oldTransform = new Transform(_theDest.getDevice());
			_theDest.getTransform(oldTransform);

			final Transform tr = new Transform(_theDest.getDevice());
			
			tr.translate(x+deltaX, y+deltaY);
			
			tr.rotate(rotate);
			
			tr.translate(-x-deltaX, -y-deltaY);
			
			_theDest.setTransform(tr);
			_theDest.drawText(theStr, x+deltaX, y+deltaY, true);
			
			//final Font awFont = new Font(fontData.getName(), fontData.getStyle(),
			//		fontData.getHeight());
			//_theDest.drawRectangle(x, y, getStringWidth(awFont, theStr), height);
			
			_theDest.setTransform(oldTransform);
			
			tr.dispose();
		}
	}

	public void drawText(final String theStr, final int x, final int y)
	{

		// don't use the rotate-able command, it mangles the fine positioning for existing elements
//		drawText(theStr, x, y, 0);

		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())

		{
			final FontData[] fd = _theDest.getFont().getFontData();

			final FontData font = fd[0];
			final int fontHt = font.getHeight();

			// shift the y. JDK uses bottom left coordinate, SWT uses top-left
			final int y2 = y - fontHt;
			
			// and draw it
			_theDest.drawText(theStr, x, y2, true);
		}
	}

	public void drawText(final java.awt.Font theFont, final String theStr,
			final int x, final int y)
	{
		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
		{
			// get/set the font
			setFont(theFont);

			// and plot the text
			drawText(theStr, x, y);
		}
	}

	public final void drawRect(final int x1, final int y1, final int wid,
			final int height)
	{
		if (_theDest == null)
			return;

		// doDecide whether to anti-alias this line
		this.switchAntiAliasOn(SWTCanvasAdapter.antiAliasThisLine(this
				.getLineWidth()));

		if (_theDest == null)
			return;

		if (!_theDest.isDisposed())
			_theDest.drawRectangle(x1, y1, wid, height);
	}

	public final void fillRect(final int x, final int y, final int wid,
			final int height)
	{
		if (_theDest == null)
			return;

		// fillOn();

		if (!_theDest.isDisposed())
		{
			// _theDest.setBackground(ColorHelper.getColor(java.awt.Color.green));
			_theDest.fillRectangle(x, y, wid, height);

			// now, the fill only fills in the provided rectangle. we also have
			// to
			// paint
			// in it's border
			_theDest.drawRectangle(x, y, wid, height);
		}

		// fillOff();
	}

	public final void semiFillRect(final int x, final int y, final int wid,
			final int height)
	{
		if (_theDest == null)
			return;

		// fillOn();

		if (!_theDest.isDisposed())
		{
			_theDest.setAlpha(45);
			_theDest.fillRectangle(x, y, wid, height);
			_theDest.setAlpha(255);

			// now, the fill only fills in the provided rectangle. we also have
			// to
			// paint
			// in it's border
			_theDest.drawRectangle(x, y, wid, height);
		}

		// fillOff();
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
		final Color swtCol = ColorHelper.getColor(theColor);

		// set the colour in the parent
		if (_theDest != null)
			if (!_theDest.isDisposed())
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
	public final String getTheToolTipText(final java.awt.Point pt)
	{
		String res = null;
		if (_tooltipHandler != null)
		{
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

	public final Enumeration<PaintListener> getPainters()
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

		public CanvasInfo(final Object data)
		{
			super(data, data.toString(), "");
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("BackgroundColor", "the background color"),
						prop("LineThickness", "the line thickness"), };

				return res;

			}
			catch (final IntrospectionException e)
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
		return "SWT Canvas";
	}

	public Dimension getSize()
	{
		return _theSize;
	}

	public void drawImage(final Image image, final int x, final int y, final int width, final int height)
	{
		if (_theDest != null)
			if (!_theDest.isDisposed())
				_theDest.drawImage(image, x, y);
	}

}
