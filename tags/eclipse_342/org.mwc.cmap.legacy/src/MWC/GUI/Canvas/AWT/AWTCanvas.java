// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTCanvas.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: AWTCanvas.java,v $
// Revision 1.3  2004/10/07 14:23:04  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/25 14:44:01  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:07  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-02-07 09:49:17+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.4  2002-10-28 09:23:31+00  ian_mayo
// support line widths
//
// Revision 1.3  2002-07-23 08:52:58+01  ian_mayo
// Implement Line width support
//
// Revision 1.2  2002-05-28 09:25:38+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:19+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-22 15:32:25+00  administrator
// When resizing to show all data, provide default area if none is available (no screen elements return bounds)
//
// Revision 1.2  2001-08-10 10:12:34+01  administrator
// added new setLineStyle method to allow dotted lines
//
// Revision 1.1  2001-07-18 15:43:20+01  administrator
// add drawPolyLine command to draw connected series of lines
//
// Revision 1.0  2001-07-17 08:46:31+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-16 15:00:05+01  novatech
// add polygon methods (draw & fill)
//
// Revision 1.2  2001-01-16 19:27:37+00  novatech
// added fillArc method
//
// Revision 1.1  2001-01-03 13:43:02+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:12  ianmayo
// initial version
//
// Revision 1.9  2000-10-03 14:13:29+01  ian_mayo
// add correct implementation of drawArc method
//
// Revision 1.8  2000-08-30 14:46:35+01  ian_mayo
// tidied up
//
// Revision 1.7  2000-02-14 16:50:23+00  ian_mayo
// added fillOval method
//
// Revision 1.6  2000-02-03 15:07:52+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.5  2000-01-13 15:33:46+00  ian_mayo
// added fillRect method
//
// Revision 1.4  1999-12-13 11:27:49+00  ian_mayo
// added tooltip handlers
//
// Revision 1.3  1999-11-30 11:18:29+00  ian_mayo
// better implementation of get graphics temp()
//
// Revision 1.2  1999-11-23 10:37:55+00  ian_mayo
// moved directory to more sensible location
//
// Revision 1.1  1999-11-23 09:13:56+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-10-14 12:00:09+01  ian_mayo
// improved labelling of some functions (to correctly reflect width/height)
//
// Revision 1.1  1999-10-12 15:37:02+01  ian_mayo
// Initial revision
//
// Revision 1.5  1999-08-17 10:30:58+01  administrator
// tidy up, and improve double-buffering
//
// Revision 1.4  1999-08-09 13:33:32+01  administrator
// Double-buffering chart
//
// Revision 1.3  1999-08-04 09:45:33+01  administrator
// minor mods, tidying up
//
// Revision 1.2  1999-07-27 12:09:42+01  administrator
// changed way fonts are set
//
// Revision 1.1  1999-07-27 10:50:48+01  administrator
// Initial revision
//


package MWC.GUI.Canvas.AWT;

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * AWT implementation of a canvas
 */
final public class AWTCanvas extends java.awt.Canvas
  implements CanvasType, Serializable
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the projection in use
   */
  PlainProjection _theProjection;

  /**
   * our graphics object - only valid between 'start' and 'stop'
   * paint events
   */
  java.awt.Graphics _theDest = null;

  /**
   * the list of registered painters for this canvas
   */
  Vector<PaintListener> _thePainters;

  /**
   * the dimensions of the canvas - we keep our own
   * track of this in order to handle the number
   * of resize messages we get
   */
  java.awt.Dimension _theSize;

  /**
   * our double-buffering safe copy
   */
  transient java.awt.Image _dblBuff;


  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * default constructor
   */
  public AWTCanvas()
  {
    // start with our background colour
    setBackgroundColor(java.awt.Color.black);

    // initialisation
    _thePainters = new Vector<PaintListener>(0, 1);

    // create our projection
    _theProjection = new FlatProjection();

    // add handler to catch canvas resizes
    this.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        setScreenSize(e.getComponent().getSize());
      }
    });
  }

  // constructor taking projection
  public AWTCanvas(PlainProjection theProjection)
  {
    this();
    // take copy of projection
    _theProjection = theProjection;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // projection related
  ////////////////////////////////////////////////////////////
  /**
   * update the projection
   */
  public void setProjection(PlainProjection theProjection)
  {
    _theProjection = theProjection;
  }

  /**
   * get the current projection
   */
  public PlainProjection getProjection()
  {
    return _theProjection;
  }

  /**
   * convenience function
   */
  public java.awt.Point toScreen(WorldLocation val)
  {
    return _theProjection.toScreen(val);
  }

  /**
   * convenience function
   */
  public WorldLocation toWorld(java.awt.Point val)
  {
    return _theProjection.toWorld(val);
  }

  /**
   * re-determine the area of data we cover,
   * then resize to cover it
   */
  public void rescale()
  {
    // get the data area for the current painters
    WorldArea theArea = null;
    Enumeration<PaintListener> enumer = _thePainters.elements();
    while (enumer.hasMoreElements())
    {
      CanvasType.PaintListener thisP = (CanvasType.PaintListener) enumer.nextElement();
      WorldArea thisArea = thisP.getDataArea();
      if (thisArea != null)
      {
        if (theArea == null)
          theArea = new WorldArea(thisArea);
        else
          theArea.extend(thisArea);
      }
    }

    // check if we've found anything
    if (theArea == null)
    {
      // we haven't got sufficient data to do a resize, cover the globe
      theArea = new WorldArea(new WorldLocation(60, -100, 0), new WorldLocation(-60, 100, 0));
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

  /**
   * handler for a screen resize - inform our projection of the resize
   * then inform the painters
   */
  public void setScreenSize(java.awt.Dimension p1)
  {
    // check if this is a real resize
    if ((_theSize == null) ||
      (!_theSize.equals(p1)))
    {

      // ok, now remember it
      _theSize = p1;

      // and pass it onto the projection
      _theProjection.setScreenArea(p1);

      // inform our parent
      super.setSize(p1);

      // erase the double buffer,
      // since it is now invalid
      _dblBuff = null;

      // inform the listeners that we have resized
      Enumeration<PaintListener> enumer = _thePainters.elements();
      while (enumer.hasMoreElements())
      {
        CanvasType.PaintListener thisPainter =
          (CanvasType.PaintListener) enumer.nextElement();
        thisPainter.resizedEvent(_theProjection, p1);
      }

    }
  }


  /////////////////////////////////////////////////////////////
  // graphics plotting related
  ////////////////////////////////////////////////////////////
  public java.awt.FontMetrics getFontMetrics(java.awt.Font theFont)
  {
    return _theDest.getFontMetrics(theFont);
  }

  public int getStringHeight(java.awt.Font theFont)
  {
    return _theDest.getFontMetrics().getHeight();
  }

  public int getStringWidth(java.awt.Font theFont, String theString)
  {
    return _theDest.getFontMetrics().stringWidth(theString);
  }


  /**
   * ONLY USE THIS FOR NON-PERSISTENT PLOTTING
   */
  public java.awt.Graphics getGraphicsTemp()
  {
    java.awt.Graphics res;
    /** if we are in a paint operation already,
     * return the graphics object, since it may
     * be a double-buffering image
     */
    if (_theDest != null)
      res = _theDest.create();
    else
    //      res = this.getGraphics();
      res = _dblBuff.getGraphics();

    return res;
  }

  public void setFont(java.awt.Font theFont)
  {
    //super.setFont(theFont);
  }

  /**
   * draw a filled polygon
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void fillPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints)
  {
    if (_theDest == null)
      return;

    _theDest.fillPolygon(xPoints, yPoints, nPoints);
  }

  /**
   * drawPolyline
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void drawPolyline(int[] xPoints,
                           int[] yPoints,
                           int nPoints)
  {
    if (_theDest == null)
      return;

    _theDest.drawPolyline(xPoints, yPoints, nPoints);
  }
  
  
	final public void drawPolyline(int[] points) {
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}

  /**
   * drawPolygon
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void drawPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints)
  {
    if (_theDest == null)
      return;

    _theDest.drawPolygon(xPoints, yPoints, nPoints);
  }


  public boolean drawImage(Image img,
                           int x,
                           int y,
                           int width,
                           int height,
                           ImageObserver observer)
  {
    if (_theDest == null)
      return true;

    return _theDest.drawImage(img, x, y, width, height, observer);
  }

  public void drawLine(int x1, int y1, int x2, int y2)
  {
    if (_theDest == null)
      return;

    _theDest.drawLine(x1, y1, x2, y2);
  }

  public void drawOval(int x, int y, int width, int height)
  {
    if (_theDest == null)
      return;

    _theDest.drawOval(x, y, width, height);
  }

  public void fillOval(int x, int y, int width, int height)
  {
    if (_theDest == null)
      return;

    _theDest.fillOval(x, y, width, height);
  }

  /**
   * set the style for the line, using our constants
   */
  public void setLineStyle(int style)
  {
    // we can't do this!
  }

  /**
   * set the width of the line, in pixels
   */
  public void setLineWidth(float width)
  {
    // we can't do this!
  }

  /**
   * get the line width, in pixels
   *
   * @return the width (or 1 if we can't do it)
   */
  public float getLineWidth()
  {
    return 1;
  }

  public void setColor(java.awt.Color theCol)
  {
    if (_theDest == null)
      return;

    _theDest.setColor(theCol);
  }

  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
  {
    _theDest.fillArc(x, y, width, height, startAngle, arcAngle);
  }

  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
  {
    _theDest.drawArc(x, y, width, height, startAngle, arcAngle);
  }

  public void startDraw(Object theVal)
  {
    _theDest = (java.awt.Graphics) theVal;
  }

  public void endDraw(Object theVal)
  {
    _theDest = null;
  }

  public void drawText(String theStr, int x, int y)
  {
    if (_theDest == null)
      return;

    _theDest.drawString(theStr, x, y);
  }

  public void drawText(java.awt.Font theFont, String theStr, int x, int y)
  {
    if (_theDest == null)
      return;

    _theDest.setFont(theFont);
    _theDest.drawString(theStr, x, y);
  }

  public void drawRect(int x1, int y1, int wid, int height)
  {
    if (_theDest == null)
      return;

    _theDest.drawRect(x1, y1, wid, height);
  }

  public void fillRect(int x, int y, int wid, int height)
  {
    if (_theDest == null)
      return;

    _theDest.fillRect(x, y, wid, height);
  }

  /**
   * get the current background colour
   */
  public java.awt.Color getBackgroundColor()
  {
    return getBackground();
  }

  /**
   * set the current background colour, and trigger a screen update
   */
  public void setBackgroundColor(java.awt.Color theColor)
  {
    // set the colour in the parent
    setBackground(theColor);
    // invalidate the screen
    updateMe();
  }

  ////////////////////////////////////////////////////////////
  // painter handling
  ////////////////////////////////////////////////////////////
  public void addPainter(CanvasType.PaintListener listener)
  {
    _thePainters.addElement(listener);
  }

  public void removePainter(CanvasType.PaintListener listener)
  {
    _thePainters.removeElement(listener);
  }

  public Enumeration<PaintListener> getPainters()
  {
    return _thePainters.elements();
  }


  //////////////////////////////////////////////////////
  // screen redraw related
  //////////////////////////////////////////////////////

  public void paint(java.awt.Graphics p1)
  {
    // paint code moved to Update function
    update(p1);
  }

  /**
   * screen redraw, just repaint the buffer
   */
  public void update(java.awt.Graphics p1)
  {
    // this is a screen redraw, we can just paint in the buffer
    // (although we may have to redraw it first)

    if (_dblBuff == null)
    {
      paintPlot();
    }

    // and paste the image
    p1.drawImage(_dblBuff, 0, 0, this);

  }

  /**
   * method to produce the buffered image - we paint this
   * buffered image when we get one of the numerous Windows
   * repaint calls
   */
  protected void paintPlot()
  {
    if (_dblBuff == null)
    {
      // we may need to recreate the image if
      // we have just restored this session
      java.awt.Dimension sz = this.getSize();
      _dblBuff = createImage(sz.width,
                             sz.height);

      // see if we have a screen size yet - if not we can't create our buffer
      if (_dblBuff == null)
        return;

    }

    // hey, let's double-buffer it
    java.awt.Graphics g1 = _dblBuff.getGraphics();

    // prepare the ground (remember the graphics dest for a start)
    startDraw(g1);

    // erase background
    java.awt.Dimension sz = this.getSize();
    g1.setColor(this.getBackground());
    g1.fillRect(0, 0, sz.width, sz.height);

    // give us a start colour
    // g1.setColor(java.awt.Color.red);

    // go through our painters
    Enumeration<PaintListener> enumer = _thePainters.elements();
    while (enumer.hasMoreElements())
    {
      CanvasType.PaintListener thisPainter =
        (CanvasType.PaintListener) enumer.nextElement();

      thisPainter.paintMe(this);
    }

    // all finished, close it now
    endDraw(null);

    // and dispose
    g1.dispose();

  }

  /**
   * first repaint the plot, then
   * trigger a screen update
   */
  public void updateMe()
  {
    // reproduce the buffer, since something has clearly changed
    paintPlot();

    // ask the operating system to repaint us when it gets a chance
    repaint();
  }


  public void setSize(int p1, int p2)
  {
    super.setSize(p1, p2);
  }


  public void setTooltipHandler(CanvasType.TooltipHandler handler)
  {
    // tooltip support not provided for AWT canvas type
  }
}
