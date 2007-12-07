// Copyright MWC 1999, Debrief 3 Project
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

import MWC.Algorithms.PlainProjection;
import MWC.GUI.Canvas.Metafile.WMF;
import MWC.GUI.Canvas.Metafile.WMFGraphics;
import MWC.GUI.CanvasType;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

	public MetafileCanvas(String directory)
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
		java.util.Date tNow = new java.util.Date();

		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("mm_ss");

		name = name + "_" + df.format(tNow) + ".wmf";

		return name;
	}

	public void endDraw(Object theVal)
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
    	
			java.io.FileOutputStream fo = new FileOutputStream(_outputFileName);

			_cachedMetafile.writeWMF(fo);
			// wmf.writePlaceableWMF(fo, 5, 5, 200, 200, 200);
			fo.close();
		}
		catch (java.io.FileNotFoundException f)
		{
			MWC.GUI.Dialogs.DialogFactory.showMessage("Write WMF",
					"Sorry, directory name may be invalid, please check properties");
			if (DEBUG_OUTPUT)
				MWC.Utilities.Errors.Trace.trace(f, "Directory not found");
		}
		catch (IOException e)
		{

			if (DEBUG_OUTPUT)
				MWC.Utilities.Errors.Trace.trace(e);
		}
	}

	public void startDraw(Object theVal)
	{
		// create the metafile
		_cachedMetafile = new WMF();
		_lastPlotSize = _proj.getScreenArea();
		g = new WMFGraphics(_cachedMetafile, _lastPlotSize.width, _lastPlotSize.height);
	}

	public void updateMe()
	{
	}

	public void drawOval(int x, int y, int width, int height)
	{
		g.drawOval(x, y, width, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawOval");
	}

	public void fillOval(int x, int y, int width, int height)
	{
		g.fillOval(x, y, width, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillOval");
	}

	public void drawText(String str, int x, int y)
	{
		// @@@ IM ignore the WMFGraphics method, since it relies on JDK1.2 code
		// (AttributedIterator)
		// g.drawString(str, x, y);
		_cachedMetafile.textOut(x, y, str);

		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawText");
	}

	public void drawText(java.awt.Font theFont, String str, int x, int y)
	{
		// remember the current font
		java.awt.Font ft = g.getFont();

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

	public void setColor(java.awt.Color theCol)
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
	public void setLineStyle(int style)
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
	public void setLineWidth(float width)
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
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
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
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
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
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
	{
		g.drawPolyline(xPoints, yPoints, nPoints);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawPolyline");
	}

	final public void drawPolyline(int[] points) {
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}
    
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("draw image");
		return g.drawImage(img, x, y, width, height, observer);
	}

	public void drawLine(int x1, int y1, int x2, int y2)
	{
		g.drawLine(x1, y1, x2, y2);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawLine, x:" + x1 + " y:" + y1 + " wid:" + x2
					+ " ht:" + y2);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		g.fillArc(x, y, width, height, startAngle, arcAngle);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillArc");
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		g.drawArc(x, y, width, height, startAngle, arcAngle);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawArc");
	}

	public void drawRect(int x1, int y1, int wid, int height)
	{
		g.drawRect(x1, y1, wid, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("drawRect");
	}

	public void fillRect(int x, int y, int wid, int height)
	{
		g.fillRect(x, y, wid, height);
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("fillRect");
	}

	public int getStringHeight(java.awt.Font theFont)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("getStringHeight");
		java.awt.FontMetrics fm = null;

		if (theFont != null)
			fm = g.getFontMetrics(theFont);
		else
			fm = g.getFontMetrics();

		int ht = fm.getHeight();
		return ht;
	}

	public int getStringWidth(java.awt.Font theFont, String theString)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("getStringWidth");
		java.awt.FontMetrics fm = null;

		if (theFont != null)
			fm = g.getFontMetrics(theFont);
		else
			fm = g.getFontMetrics();

		int wid = fm.stringWidth(theString);
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

	public void setProjection(MWC.Algorithms.PlainProjection val)
	{
		_proj = val;
	}

	public java.awt.Point toScreen(MWC.GenericData.WorldLocation val)
	{
		return _proj.toScreen(val);
	}

	public MWC.GenericData.WorldLocation toWorld(java.awt.Point val)
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

	public void setBackgroundColor(java.awt.Color theColor)
	{
		if (DEBUG_OUTPUT)
			MWC.Utilities.Errors.Trace.trace("setBackgroundColor");
		g.setBackgroundColor(theColor);

	}

	public void addPainter(CanvasType.PaintListener listener)
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (addPainter)");
	}

	public java.util.Enumeration getPainters()
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (getPainters)");
		return null;
	}

	public void removePainter(CanvasType.PaintListener listener)
	{
		System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED");
	}

	public void setTooltipHandler(CanvasType.TooltipHandler handler)
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

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////
}
