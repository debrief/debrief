// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CanvasType.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CanvasType.java,v $
// Revision 1.2  2004/05/25 15:45:23  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:02  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-06-25 08:49:55+01  ian_mayo
// Add multi-line marker interface
//
// Revision 1.4  2002-10-28 09:23:39+00  ian_mayo
// support line widths
//
// Revision 1.3  2002-07-23 08:52:15+01  ian_mayo
// Implement Line width support
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:24+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-10 12:22:29+00  administrator
// improve comments
//
// Revision 1.2  2001-08-10 10:12:35+01  administrator
// added new setLineStyle method to allow dotted lines
//
// Revision 1.1  2001-07-18 15:43:21+01  administrator
// add drawPolyLine command to draw connected series of lines
//
// Revision 1.0  2001-07-17 08:46:37+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-16 15:00:05+01  novatech
// add polygon methods (draw & fill)
//
// Revision 1.2  2001-01-16 19:27:38+00  novatech
// added fillArc method
//
// Revision 1.1  2001-01-03 13:43:05+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:45  ianmayo
// initial version
//
// Revision 1.9  2000-10-03 14:13:32+01  ian_mayo
// add correct implementation of drawArc method
//
// Revision 1.8  2000-08-30 14:46:25+01  ian_mayo
// added getSize() method
//
// Revision 1.7  2000-03-07 10:12:54+00  ian_mayo
// add getPainters() method
//
// Revision 1.6  2000-02-14 16:50:24+00  ian_mayo
// added fillOval method
//
// Revision 1.5  2000-01-13 15:33:45+00  ian_mayo
// added fillRect method
//
// Revision 1.4  1999-12-13 11:27:48+00  ian_mayo
// added tooltip handlers
//
// Revision 1.3  1999-12-03 14:36:55+00  ian_mayo
// add more comments
//
// Revision 1.2  1999-10-14 12:00:10+01  ian_mayo
// improved labelling of some functions (to correctly reflect width/height)
//
// Revision 1.1  1999-10-12 15:37:06+01  ian_mayo
// Initial revision
//
// Revision 1.5  1999-08-17 08:16:38+01  administrator
// added setProjection method
//
// Revision 1.4  1999-08-09 13:37:02+01  administrator
// implement "drawOval"
//
// Revision 1.3  1999-08-04 09:45:30+01  administrator
// minor mods, tidying up
//
// Revision 1.2  1999-07-27 12:07:34+01  administrator
// changed implementation of setFont
//
// Revision 1.1  1999-07-27 10:50:49+01  administrator
// Initial revision
//
// Revision 1.4  1999-07-27 09:24:44+01  administrator
// added drawLine method
//
// Revision 1.3  1999-07-19 12:39:43+01  administrator
// Added painting to a metafile
//
// Revision 1.2  1999-07-16 10:01:47+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:07+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:00+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:29+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:33:13+00  sm11td
// Initial revision
//

package MWC.GUI;

import MWC.GenericData.*;
import MWC.Algorithms.PlainProjection;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * interface for canvases to implement for plotting commands
 * The CanvasType interface provides all of the drawing operations
 * which may be made onto a scale plot.
 * The drawing functions themselves are in screen coordinates, but
 * convenience functions are provided which convert between screen
 * and data coordinates using the current projection.
 * The CanvasType class has the following responsibilities:
 * <li> Draw primitives to plot </li>
 * <li> Report low-level mouse operations </li>
 * <li> Handle any projection from data to screen coordinates </li>
 */
public interface CanvasType {
  //////////////////////////////////////
  // Plain drawing commands
  //////////////////////////////////////

  /** list of line styles to (try to) plot
   *
   */

  public static final int SOLID = 0;
  public static final int DOTTED = 2;
  public static final int DOT_DASH = 3;
  public static final int SHORT_DASHES = 4;
  public static final int LONG_DASHES = 1;
  public static final int UNCONNECTED = 5;

	/**
   * update the information currently plotted on chart
   *
   */
  public void updateMe();
  /**
   * drawOval
   *
   * @param x parameter for drawOval
   *
   */
  public void drawOval( int x, int y, int width, int height );
  /**
   * fill an arc on the current destination
   *
   * @param x pixels
   * @param width pixels
   * @param startAngle degrees
   *
   */
  public void fillArc(int x, int y,
               int width, int height,
               int startAngle, int arcAngle);

	/**
   * fillOval
   *
   * @param x parameter for fillOval
   *
   */
  public void fillOval(int x, int y, int width, int height );
  /**
   * drawText
   *
   * @param str parameter for drawText
   *
   */
  public void drawText(String str, int x, int y);
  /**
   * setColor
   *
   * @param theCol parameter for setColor
   */
  public void setColor(java.awt.Color theCol);

  /** set the style for the line, using our constants
   *
   */
  public void setLineStyle(int style);

  /** set the width of the line, in pixels
   *
   */
  public void setLineWidth(float width);

  /** draw image
   * @param img the image to draw
   * @param x the top left coordinate to draw
   * @param y the top left coordinate to draw
   * @param width the width of the image to draw
   * @param height the height of the image to draw
   * @param observer the observer for this image
   * @return boolean flag for whether the image was ready for painting
   *
   */
  public boolean drawImage(Image img,
                                  int x,
                                  int y,
                                  int width,
                                  int height,
                                  ImageObserver observer);

  /**
   * drawLine
   *
   * @param startX parameter for drawLine
   *
   */
  public void drawLine(int startX, int startY, int endX, int endY);

  /**
   * draw a filled polygon
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void fillPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints);


  /**
   * drawPolygon
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void drawPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints);

  /**
   * drawPolyline
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void drawPolyline(int[] xPoints,
                          int[] yPoints,
                          int nPoints);
  

  /**
   * drawPolyline
   *
   * @param points list of x & y coordinates
   */
  public void drawPolyline(int[] xPoints);  

  /**
   * draw an arc on the current destination
   *
   * @param x pixels
   * @param width pixels
   * @param startAngle degrees
   *
   */
  public void drawArc(int x, int y,
               int width, int height,
               int startAngle, int arcAngle);
  /**
   * drawRect
   *
   * @param x1 parameter for drawRect
   *
   */
  public void drawRect(int x1, int y1, int wid, int height);
	/**
   * fillRect
   *
   * @param x parameter for fillRect
   *
   */
  public void fillRect(int x, int y, int wid, int height);
  /**
   * drawText
   *
   * @param theFont parameter for drawText
   *
   */
  public void drawText(java.awt.Font theFont, String theStr, int x, int y);
  /**
   * getStringHeight
   *
   * @param theFont parameter for getStringHeight
   * @return the returned int
   */
  public int getStringHeight(java.awt.Font theFont);
  /**
   * getStringWidth
   *
   * @param theFont parameter for getStringWidth
   *
   * @return the returned int
   */
  public int getStringWidth(java.awt.Font theFont, String theString);

  /** get the current line width (when supported)
   *
   * @return the width, in pixels
   */
  public float getLineWidth();

  /**
   * expose the graphics object, used only for
   * plotting non-persistent graphics
   * (temporary lines, etc).
   *
   */
  public java.awt.Graphics getGraphicsTemp();

  //////////////////////////////////////
  // start/finish plotting commands
  //////////////////////////////////////

  /**
   * client has finished drawing operation
   *
   */
  public void endDraw(Object theVal);

  /**
   * client is about to start drawing operation
   *
   */
  public void startDraw(Object theVal);

  //////////////////////////////////////
  // projection related commands
  /**
   * //////////////////////////////////////
   *
   */
  public PlainProjection getProjection();
  /**
   * setProjection
   *
   * @param val parameter for setProjection
   */
  public void setProjection(PlainProjection val);

  /**
   *  Convert the location provided to screen coordinates
   * GOTCHA there's am optimisation in the default projection
   * where we don't produce a new Point each time, we return
   * a local working copy.  So, if you're working in code
   * which requires two Points (drawing a line, for example)
   * in the creation of one of your points you must use
   * a copy constructor
   *
   */
  public java.awt.Point toScreen(WorldLocation val);
  /**
   * toWorld
   *
   * @param val parameter for toWorld
   * @return the returned WorldLocation
   */
  public WorldLocation toWorld(java.awt.Point val);

  /**
   * retrieve the full data area, and do a fit to window
   *
   */
  public void rescale();

  //////////////////////////////////////
  // screen layout commands
  //////////////////////////////////////

  /**
   * set/get the background colour
   *
   */
  public java.awt.Color getBackgroundColor();
  /**
   * setBackgroundColor
   *
   * @param theColor parameter for setBackgroundColor
   */
  public void setBackgroundColor(java.awt.Color theColor);

  /**
   * getSize
   *
   * @return the returned java.awt.Dimension
   */
  public java.awt.Dimension getSize();
  /**
   * addPainter
   *
   * @param listener parameter for addPainter
   */
  public void addPainter(CanvasType.PaintListener listener);
  /**
   * removePainter
   *
   * @param listener parameter for removePainter
   */
  public void removePainter(CanvasType.PaintListener listener);
  /**
   * getPainters
   *
   * @return the returned java.util.Enumeration
   */
  public java.util.Enumeration<CanvasType.PaintListener> getPainters();

	///////////////////////////////////////////////
	// tooltip handler commands
	/**
   * ///////////////////////////////////////////////
   *
   */
	public void setTooltipHandler(TooltipHandler handler);

  //////////////////////////////////////
  // listener commands (largely for tools/painters)
  //////////////////////////////////////

  /**
   * // support for adding a list of painters to this class
   */
  public interface PaintListener{
    /**
     * repaint yourself to this canvas (me)
     *
     */
    public void paintMe(CanvasType dest);
    /**
     * give me the data area you require
     *
     */
    public WorldArea getDataArea();
    /**
     * inform listeners that we have resized.
     * this is especially useful for painters which plot
     * static data such as a coastline or grid - it
     * triggers a redraw from them
     *
     */
    public void resizedEvent(MWC.Algorithms.PlainProjection theProj,
                             java.awt.Dimension newScreenArea);
    /**
     * what is the name of the type of data you plot?
     *
     */
    public String getName();
  }

  /**
   * duff implementation of PaintListener, so classes can choose to
   * extend just part of the listener interface
   */
  public abstract class PaintAdaptor implements CanvasType.PaintListener{

    /**
     * paintMe
     *
     * @param dest parameter for paintMe
     */
    public void paintMe(CanvasType dest)
    {
      // do nothing
    }

    /**
     * getDataArea
     *
     * @return the returned WorldArea
     */
    public WorldArea getDataArea()
    {
      // return null
      return null;
    }

    /**
     * resizedEvent
     *
     * @param theProj parameter for resizedEvent
     *
     */
    public void resizedEvent(MWC.Algorithms.PlainProjection theProj, java.awt.Dimension newScreenArea)
    {
      // do nothing, since we're not really interested
    }

    /**
     * getName
     *
     * @return the returned String
     */
    abstract public String getName();
  }

	/**
   * class to allow provision of a custom tooltip dependent on the
   * mouse position provided
   */
	public interface TooltipHandler
	{
		/**
     * getString
     *
     * @param loc parameter for getString
     *
     * @return the returned String
     */
    public String getString(MWC.GenericData.WorldLocation loc,
														java.awt.Point point);
	}

  /** marker interface for classes which can provide a multi-line text label
   *
   */
  public interface MultiLineTooltipProvider
  {
    /** get the data name in multi-line format (for tooltips)
     *
     * @return multi-line text label
     */
    public String getMultiLineName();
  }
}

