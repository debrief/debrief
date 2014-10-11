/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: MetafileCanvas.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: MetafileCanvas.java,v $
// Revision 1.6  2006/02/01 14:34:44  Ian.Mayo
// Introduce debug line to indicate where file is going to
//
// Revision 1.5  2006/01/25 10:32:36  Ian.Mayo
// Elect to either write plot to WMF in normal place or tmp directory
//
// Revision 1.4  2006/01/19 13:01:35  Ian.Mayo
// Provide accessors to help us copy WMFs to clipboard
//
// Revision 1.3  2004/12/06 09:11:16  Ian.Mayo
// Optimise to reduce object creation, only ask for new colour/line width/line style if different to previous one
//
// Revision 1.2  2004/05/25 14:43:55  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:06  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-02-07 09:49:21+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.4  2002-10-28 09:23:38+00  ian_mayo
// support line widths
//
// Revision 1.3  2002-07-23 08:52:27+01  ian_mayo
// Implement Line width support
//
// Revision 1.2  2002-05-28 09:25:37+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:21+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-02-18 20:16:30+00  administrator
// Refactor create file name method, so that we can call it from our testing code
//
// Revision 1.2  2001-08-10 10:12:35+01  administrator
// added new setLineStyle method to allow dotted lines
//
// Revision 1.1  2001-07-18 15:43:21+01  administrator
// add drawPolyLine command to draw connected series of lines
//
// Revision 1.0  2001-07-17 08:46:32+01  administrator
// Initial revision
//
// Revision 1.5  2001-07-16 15:00:04+01  novatech
// add polygon methods (draw & fill)
//
// Revision 1.4  2001-06-04 09:38:11+01  novatech
// include optional debug statements
//
// Revision 1.3  2001-01-17 09:42:01+00  novatech
// remove unnecessary import statements
//
// Revision 1.2  2001-01-16 19:27:38+00  novatech
// added fillArc method
//
// Revision 1.1  2001-01-03 13:43:03+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:07  ianmayo
// initial version
//
// Revision 1.17  2000-11-24 11:51:53+00  ian_mayo
// removing unnecessary comments
//
// Revision 1.16  2000-10-10 13:05:47+01  ian_mayo
// handle directory not found
//
// Revision 1.15  2000-10-09 13:35:55+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.14  2000-10-03 14:13:29+01  ian_mayo
// add correct implementation of drawArc method
//
// Revision 1.13  2000-08-30 16:34:39+01  ian_mayo
// correction to filename creation
//
// Revision 1.12  2000-08-30 14:47:52+01  ian_mayo
// implemented remaining CanvasType functionality, also added retrieval of WMF directory from properties
//
// Revision 1.11  2000-04-19 11:37:01+01  ian_mayo
// provide more complete feature set
//
// Revision 1.10  2000-03-07 10:13:10+00  ian_mayo
// implement getPainters
//
// Revision 1.9  2000-02-22 13:50:48+00  ian_mayo
// white space only
//
// Revision 1.8  2000-02-16 16:24:08+00  ian_mayo
// briefly switch back to using (protected) metafile classes
//
// Revision 1.7  2000-02-16 11:59:02+00  ian_mayo
// switched back to using compiled WMFGraphics code, since plots not appearing on page
//
//
package MWC.GUI.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Canvas.Metafile.WMF;
import MWC.GUI.Canvas.Metafile.WMFGraphics;

public class MetafileCanvas implements CanvasType
{

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	protected PlainProjection _proj;

	WMF _cachedMetafile;

	WMFGraphics g;

	String _directory;

	// remember the current colour, so we don't needlessly update the colour when
	// it's already the right one.
	private Color _currentColor = null;

	// remember the current style, so we don't needlessly update the style when
	// it's already the right one.
	private int _lastLineStyle = -10;

	// remember the current pen width r, so we don't needlessly update the width
	// when
	// it's already the right one.
	private float _lastPenWidth = -10;

	private static final boolean DEBUG_OUTPUT = false;

	/**
	 * the last set of dimensions we plotted
	 */
	private static Dimension _lastPlotSize;

	/**
	 * the last output filename we used
	 */
	private static String _outputFileName = null;
	
  
  /** write to tmp file
   * 
   */
  private boolean _writeToTmpFile = false;	

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

	public MetafileCanvas(final String directory)
	{
		if (directory != null)
		{
			_directory = directory;
		}
	}


  public MetafileCanvas()
  {
  	this(null);
  	
  	_writeToTmpFile = true;
  }
  	
	// /////////////////////////////////
	// member functions
	// ////////////////////////////////
	public static String getFileName()
	{
		String name = "d3_";
		final java.util.Date tNow = new java.util.Date();

		final java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("mm_ss");

		name = name + "_" + df.format(tNow) + ".wmf";

		return name;
	}

	public void endDraw(final Object theVal)
	{
		// and now save it
		try
		{
			
    	if(_writeToTmpFile)
    	{
    		_outputFileName = java.io.File.createTempFile("debrief_plot_", ".wmf").getCanonicalPath();
    	}
    	else
    	{
      	_outputFileName = getFileName();
      	
        if(_directory != null)
        	_outputFileName = _directory + File.separator + _outputFileName;
    	}			

    	System.out.println("Writing Metafile to:" + _outputFileName);
    	
			final java.io.FileOutputStream fo = new FileOutputStream(_outputFileName);

			_cachedMetafile.writeWMF(fo);
			// wmf.writePlaceableWMF(fo, 5, 5, 200, 200, 200);
			fo.close();
		}
		catch (final java.io.FileNotFoundException f)
		{
			MWC.GUI.Dialogs.DialogFactory.showMessage("Write WMF",
					"Sorry, directory name may be invalid, please check properties");
			if (DEBUG_OUTPUT)
				MWC.Utilities.Errors.Trace.trace(f, "Directory not found");
		}
		catch (final IOException e)
		{

			if (DEBUG_OUTPUT)
				MWC.Utilities.Errors.Trace.trace(e);
		}
	}

	public void startDraw(final Object theVal)
	{
		// create the metafile
		_cachedMetafile = new WMF();
		_lastPlotSize = _proj.getScreenArea();
		g = new WMFGraphics(_cachedMetafile, _lastPlotSize.width, _lastPlotSize.height);
	}

	public void updateMe()
	{
	}

	public void drawOval(final int x, final int y, final int width, final int height)
	{
		g.drawOval(x, y, width, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawOval");
	}

	public void fillOval(final int x, final int y, final int width, final int height)
	{
		g.fillOval(x, y, width, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillOval");
	}

	public void drawText(final String str, final int x, final int y)
	{
		// @@@ IM ignore the WMFGraphics method, since it relies on JDK1.2 code
		// (AttributedIterator)
		// g.drawString(str, x, y);
		_cachedMetafile.textOut(x, y, str);

		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawText");
	}

	public void drawText(final java.awt.Font theFont, final String str, final int x, final int y)
	{
		// remember the current font
		final java.awt.Font ft = g.getFont();

		g.setFont(theFont);

		// @@@ IM ignore the WMFGraphics method, since it relies on JDK1.2 code
		// (AttributedIterator)
		// g.drawString(str, x, y);
		_cachedMetafile.textOut(x, y, str);

		// restore the font
		g.setFont(ft);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawText");
	}

	public void setColor(final java.awt.Color theCol)
	{
		// note, we're not using an equals() operator. I ran a check to see if I
		// needed to use .equals()
		// but they when the colours were equal() they were actually the same
		// object. So, this is quicker
		if (theCol != _currentColor)
		{
			g.setColor(theCol);
			if (DEBUG_OUTPUT)
				MWC.Utilities.Errors.Trace.trace("setColor");
			_currentColor = theCol;
		}
	}

	/**
	 * set the style for the line, using our constants
	 */
	public void setLineStyle(final int style)
	{
		// only update if we have to
		if (style != _lastLineStyle)
		{
			g.setPenStyle(style);
			_lastLineStyle = style;
		}
	}

	/**
	 * set the width of the line, in pixels
	 */
	public void setLineWidth(final float width)
	{
		// only update if we have to
		if (width != _lastPenWidth)
		{
			g.setPenWidth((int) width);
			_lastPenWidth = width;
		}
	}

	/**
	 * get the current line width (when supported)
	 * 
	 * @return the width, in pixels
	 */
	public float getLineWidth()
	{
		return g.getPenWidth();
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
	public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints)
	{
		g.fillPolygon(xPoints, yPoints, nPoints);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillPoly");
	}

	/**
	 * drawPolygon
	 * 
	 * @param xPoints
	 *          list of x coordinates
	 * @param yPoints
	 *          list of y coordinates
	 * @param nPoints
	 *          length of list
	 */
	public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints)
	{
		g.drawPolygon(xPoints, yPoints, nPoints);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawPoly");
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
	public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints)
	{
		g.drawPolyline(xPoints, yPoints, nPoints);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawPolyline");
	}

	final public void drawPolyline(final int[] points) {
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}
    
	public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
			final ImageObserver observer)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("draw image");
		return g.drawImage(img, x, y, width, height, observer);
	}

	public void drawLine(final int x1, final int y1, final int x2, final int y2)
	{
		g.drawLine(x1, y1, x2, y2);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawLine, x:" + x1 + " y:" + y1 + " wid:" + x2
					+ " ht:" + y2);
	}

	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle)
	{
		g.fillArc(x, y, width, height, startAngle, arcAngle);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillArc");
	}

	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle)
	{
		g.drawArc(x, y, width, height, startAngle, arcAngle);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawArc");
	}

	public void drawRect(final int x1, final int y1, final int wid, final int height)
	{
		g.drawRect(x1, y1, wid, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawRect");
	}

	public void fillRect(final int x, final int y, final int wid, final int height)
	{
		g.fillRect(x, y, wid, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillRect");
	}

	public int getStringHeight(final java.awt.Font theFont)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("getStringHeight");
		java.awt.FontMetrics fm = null;

		if (theFont != null)
			fm = g.getFontMetrics(theFont);
		else
			fm = g.getFontMetrics();

		final int ht = fm.getHeight();
		return ht;
	}

	@Override
	public void setFont(final Font theFont)
	{
		g.setFont(theFont);
	}
	
	public int getStringWidth(final java.awt.Font theFont, final String theString)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("getStringWidth");
		java.awt.FontMetrics fm = null;

		if (theFont != null)
			fm = g.getFontMetrics(theFont);
		else
			fm = g.getFontMetrics();

		int wid = fm.stringWidth(theString);
		
		// have a go at stretching it by a single character
		final double newWid = (double)wid / theString.length() * (theString.length()+1);
		
		wid = (int) newWid;
		
		return wid;
	}

	public java.awt.Graphics getGraphicsTemp()
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("getGraphicsTemp");
		// return our internal graphics object
		return g;
	}

	public MWC.Algorithms.PlainProjection getProjection()
	{
		return _proj;
	}

	public void setProjection(final MWC.Algorithms.PlainProjection val)
	{
		_proj = val;
	}

	public java.awt.Point toScreen(final MWC.GenericData.WorldLocation val)
	{
		return _proj.toScreen(val);
	}

	public MWC.GenericData.WorldLocation toWorld(final java.awt.Point val)
	{
		return _proj.toWorld(val);
	}

	public void rescale()
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (rescale)");
	}

	public java.awt.Color getBackgroundColor()
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("getBackgroundColor");
		return g.getBackgroundColor();
	}

	public void setBackgroundColor(final java.awt.Color theColor)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("setBackgroundColor");
		g.setBackgroundColor(theColor);

	}

	public void addPainter(final CanvasType.PaintListener listener)
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (addPainter)");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public java.util.Enumeration getPainters()
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (getPainters)");
		return null;
	}

	public void removePainter(final CanvasType.PaintListener listener)
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED");
	}

	public void setTooltipHandler(final CanvasType.TooltipHandler handler)
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED");
	}

	public java.awt.Dimension getSize()
	{
		return null;
	}

	/**
	 * provide the last filename we wrote to
	 * 
	 * @return the filename
	 */
	public static String getLastFileName()
	{
		return _outputFileName;
	}

	/**
	 * accessor to get the last screen size plotted
	 * 
	 * @return
	 */
	public static Dimension getLastScreenSize()
	{
		return _lastPlotSize;
	}


	@Override
	public void drawText(final String str, final int x, final int y, final float rotate) {
		if (str == null || str.trim().length() == 0) {
			return;
		}
		
		FontMetrics fontMetrics = g.getFontMetrics();		
		final int distance = (fontMetrics.stringWidth(str))/2;
		final double direction = Math.toRadians(rotate);
		int deltaX = (int) (distance * Math.cos(direction));
		int deltaY = (int) (distance * Math.sin(direction));
		
		int newEscapement = (int) (-rotate*10);
		int old = g.getFontEscapement();
		g.setFontEscapement(newEscapement);
		drawText(str, x-deltaX, y-deltaY);
		
		g.setFontEscapement(old);
	}
	
	@Override
	public void drawText(final String str, final int x, final int y, float rotate, boolean above) {
		if (str == null || str.trim().length() == 0) {
			return;
		}
		
		FontMetrics fontMetrics = g.getFontMetrics();		
		int distance = fontMetrics.getAscent() + fontMetrics.getDescent() + fontMetrics.getLeading();
		double direction = Math.toRadians(rotate);
		int deltaX = (int) (distance * Math.cos(direction));
		int deltaY = (int) (distance * Math.sin(direction));
		if (!above) {
			deltaX = -deltaX;
			deltaY = -deltaY;
		}
		
		if (rotate > 180) {
			rotate -= 180;
			distance = getStringWidth(g.getFont(), str);

			direction = Math.toRadians(rotate-90);
			if (!above) {
				deltaX =  (int) (1.3*deltaX - (distance * Math.cos(direction)));
				deltaY =  (int) (1.3*deltaY - (distance * Math.sin(direction)));
			} else {
				deltaX -= (int) (0.8*distance * Math.cos(direction));
				deltaY -= (int) (0.8*distance * Math.sin(direction));
			}
		}
		rotate-=90;
		
		
		int newEscapement = (int) (-rotate*10);
		int old = g.getFontEscapement();
		g.setFontEscapement(newEscapement);
		drawText(str, x+deltaX, y+deltaY);
		//drawText(str, x-deltaX, y-deltaY);
		//drawRect(x+deltaX, y+deltaY, fontMetrics.stringWidth(str), distance);
		g.setFontEscapement(old);
	}

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////
}
