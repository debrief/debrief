// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingCanvas.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.10 $
// $Log: SwingCanvas.java,v $
// Revision 1.10  2005/09/23 14:54:02  Ian.Mayo
// Tidying
//
// Revision 1.9  2004/10/13 11:09:44  Ian.Mayo
// Catch another instance where destination not being checked.
//
// Revision 1.8  2004/10/12 15:24:23  Ian.Mayo
// More changes to double-check existence of graphic destination
//
// Revision 1.7  2004/10/12 08:53:40  Ian.Mayo
// More checking to overcome multi-threaded plotting
//
// Revision 1.6  2004/10/11 08:57:59  Ian.Mayo
// Handle multi-threaded plotting
//
// Revision 1.5  2004/10/08 10:55:56  Ian.Mayo
// Don't throw error we try to plot before we have a dest
//
// Revision 1.4  2004/10/07 14:23:05  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3  2004/08/31 09:38:03  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 14:44:09  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:10  Ian.Mayo
// Initial import
//
// Revision 1.15  2003-07-04 11:00:52+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.14  2003-06-30 09:05:55+01  ian_mayo
// Implement IntelliJ inspector recommendations
//
// Revision 1.13  2003-06-25 08:50:33+01  ian_mayo
// Make prot method private
//
// Revision 1.12  2003-04-01 15:53:55+01  ian_mayo
// Remove d-line
//
// Revision 1.11  2003-03-10 14:14:25+00  ian_mayo
// Trap redraw if canvas not ready
//
// Revision 1.10  2003-03-10 10:20:49+00  ian_mayo
// Allow canvas to be overridden (so we can sub-class it for our overview)
//
// Revision 1.9  2003-03-06 15:31:19+00  ian_mayo
// Improved checking of when ready to draw
//
// Revision 1.8  2003-01-17 15:10:58+00  ian_mayo
// Don't plot if there isn't a canvas ready
//
// Revision 1.7  2002-12-16 15:25:55+00  ian_mayo
// Insert controls for anti-aliasing
//
// Revision 1.6  2002-11-28 09:56:23+00  ian_mayo
// Enable anti-aliasing
//
// Revision 1.5  2002-10-28 09:23:30+00  ian_mayo
// support line widths
//
// Revision 1.4  2002-07-23 08:52:37+01  ian_mayo
// Implement Line width support
//
// Revision 1.3  2002-07-12 15:46:13+01  ian_mayo
// Insert minor error trapping
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:16+01  ian_mayo
// Initial revision
//
// Revision 1.9  2002-02-26 09:41:27+00  administrator
// Removed bit where we set world area in absence of data (we are moving it to the Layers object)
//
// Revision 1.8  2002-02-18 09:19:30+00  administrator
// Set the name of the GUI component (largely so that we can access it from JFCUnit)
//
// Revision 1.7  2002-01-24 14:22:28+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.6  2002-01-22 15:32:05+00  administrator
// When resizing to show all data, provide default area if none is available (no screen elements return bounds)
//
// Revision 1.5  2001-09-14 10:01:30+01  administrator
// Tidily handle instance where we don't know enough to go ahead
//
// Revision 1.4  2001-08-13 12:48:22+01  administrator
// Use the correct mitre-width (Swing returned an error)
//
// Revision 1.3  2001-08-10 10:12:34+01  administrator
// added new setLineStyle method to allow dotted lines
//
// Revision 1.2  2001-08-06 12:43:54+01  administrator
// Tidily handle situation where we are trying to draw plot with insufficient data
//
// Revision 1.1  2001-07-18 15:43:20+01  administrator
// add drawPolyLine command to draw connected series of lines
//
// Revision 1.0  2001-07-17 08:46:30+01  administrator
// Initial revision
//
// Revision 1.6  2001-07-16 15:00:04+01  novatech
// add polygon methods (draw & fill)
//
// Revision 1.5  2001-06-04 09:30:53+01  novatech
// improve comments & layout
//
// Revision 1.4  2001-01-22 12:29:30+00  novatech
// added JUnit testing code
//
// Revision 1.3  2001-01-16 19:27:37+00  novatech
// added fillArc method
//
// Revision 1.2  2001-01-09 10:30:59+00  novatech
// check plot is valid before we try to redraw
//
// Revision 1.1  2001-01-03 13:43:01+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:06  ianmayo
// initial version
//
// Revision 1.21  2000-11-24 11:51:43+00  ian_mayo
// removing unnecessary comments
//
// Revision 1.20  2000-11-17 09:08:28+00  ian_mayo
// perform checking, to ensure that we are asking the OS to plot sensible coordinates (this was causing the application to crash with x & y values in their -ve thousands
//
// Revision 1.19  2000-10-03 14:13:31+01  ian_mayo
// add correct implementation of drawArc method
//
// Revision 1.18  2000-09-21 09:06:41+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.17  2000-08-30 14:46:56+01  ian_mayo
// factored out painting code, so that it can be called by the Metafile plotter
//
// Revision 1.16  2000-08-18 13:36:06+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.15  2000-08-14 15:49:00+01  ian_mayo
// Name change
//
// Revision 1.14  2000-08-11 08:42:01+01  ian_mayo
// tidy beaninfod
//
// Revision 1.13  2000-07-05 16:37:42+01  ian_mayo
// tidying up
//
// Revision 1.12  2000-04-19 11:38:55+01  ian_mayo
// implement Close method, clear local storage
//
// Revision 1.11  2000-03-14 09:57:32+00  ian_mayo
// only try to show a tooltip when we have a valid projection
//
// Revision 1.10  2000-02-14 16:50:24+00  ian_mayo
// added fillOval method
//
// Revision 1.9  2000-02-03 15:07:55+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.8  2000-02-02 14:25:27+00  ian_mayo
// check we have a valid area
//
// Revision 1.7  2000-01-13 15:33:45+00  ian_mayo
// added fillRect method
//
// Revision 1.6  2000-01-12 15:39:42+00  ian_mayo
// made editable
//
// Revision 1.5  1999-12-13 11:24:10+00  ian_mayo
// added tooltip hander interface definition
//
// Revision 1.4  1999-12-03 14:36:12+00  ian_mayo
// tidy up handling of double buffer
//
// Revision 1.3  1999-11-30 11:18:06+00  ian_mayo
// better implementation of getGraphics
//
// Revision 1.2  1999-11-23 10:37:56+00  ian_mayo
// moved directory to more sensible location
//
// Revision 1.1  1999-11-23 09:14:21+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-18 11:13:23+00  ian_mayo
// new Swing versions
//


package MWC.GUI.Canvas.Swing;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;


/**
 * Swing implementation of a canvas.
 */
public class SwingCanvas extends javax.swing.JComponent
  implements CanvasType,
  Serializable,
  Editable
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
  private PlainProjection _theProjection;

  /**
   * our graphics object - only valid between 'start' and 'stop'
   * paint events.
   */
  private java.awt.Graphics _theDest = null;

  /**
   * the list of registered painters for this canvas.
   */
  private Vector<CanvasType.PaintListener> _thePainters;

  /**
   * the dimensions of the canvas - we keep our own
   * track of this in order to handle the number
   * of resize messages we get.
   */
  private java.awt.Dimension _theSize;

  /**
   * our double-buffering safe copy.
   */
  private transient java.awt.Image _dblBuff;

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
  static private java.util.HashMap<Integer, BasicStroke> _myLineStyles = null;

  /**
   * the current line width.
   */
  private float _lineWidth;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * default constructor.
   */
  public SwingCanvas()
  {
    super.setName("Canvas");

    // start with our background colour
    setBackgroundColor(java.awt.Color.black);

    // initialisation
    _thePainters = new Vector<CanvasType.PaintListener>(0, 1);

    // create our projection
    _theProjection = new FlatProjection();

    // add handler to catch canvas resizes
    this.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(final ComponentEvent e)
      {
        setScreenSize(e.getComponent().getSize());
      }
    });

    // switch on tooltips for this panel
    setToolTipText("blank");
  }

  // constructor taking projection.
  private SwingCanvas(final PlainProjection theProjection)
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
   * update the projection.
   */
  public final void setProjection(final PlainProjection theProjection)
  {
    _theProjection = theProjection;
  }

  /**
   * switch anti-aliasing on or off.
   *
   * @param val yes/no
   */
  private void switchAntiAliasOn(final boolean val)
  {
    // ignore this
    final Graphics2D g2 = (Graphics2D) _theDest;

    if (g2 == null)
      return;

    if (val)
    {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
    }
    else
    {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_OFF);
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
   * re-determine the area of data we cover.
   * then resize to cover it
   */
  public final void rescale()
  {

    // get the data area for the current painters
    WorldArea theArea = null;
    final Enumeration<CanvasType.PaintListener> enumer = _thePainters.elements();
    while (enumer.hasMoreElements())
    {
      final CanvasType.PaintListener thisP = (CanvasType.PaintListener) enumer.nextElement();
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

  /**
   * handler for a screen resize - inform our projection of the resize
   * then inform the painters.
   */
  private void setScreenSize(final java.awt.Dimension p1)
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
      final Enumeration<CanvasType.PaintListener> enumer = _thePainters.elements();
      while (enumer.hasMoreElements())
      {
        final CanvasType.PaintListener thisPainter =
          (CanvasType.PaintListener) enumer.nextElement();
        thisPainter.resizedEvent(_theProjection, p1);
      }

    }
  }


  /////////////////////////////////////////////////////////////
  // graphics plotting related
  ////////////////////////////////////////////////////////////
  /**
   * find out the current metrics.
   *
   * @param theFont the font to try
   * @return the metrics object
   */
  public final java.awt.FontMetrics getFontMetrics(final java.awt.Font theFont)
  {
    java.awt.FontMetrics res = null;

    if (_theDest != null)
    {
      if (theFont != null)
        res = _theDest.getFontMetrics(theFont);
      else
        res = _theDest.getFontMetrics();
    }

    return res;
  }

  public final int getStringHeight(final java.awt.Font theFont)
  {
    int res = 0;
    final java.awt.FontMetrics fm = getFontMetrics(theFont);
    if (fm != null)
      res = fm.getHeight();

    return res;
  }

  public final int getStringWidth(final java.awt.Font theFont, final String theString)
  {
    int res = 0;
    final java.awt.FontMetrics fm = getFontMetrics(theFont);
    if (fm != null)
      res = fm.stringWidth(theString);

    return res;
  }


  /**
   * ONLY USE THIS FOR NON-PERSISTENT PLOTTING
   */
  public final java.awt.Graphics getGraphicsTemp()
  {
    java.awt.Graphics res = null;
    /** if we are in a paint operation already,
     * return the graphics object, since it may
     * be a double-buffering image
     */
    if (_theDest != null)
    {
      res = _theDest.create();  // return a copy, so the user can dispose it
    }
    else
    {
      if (_dblBuff != null)
      {
        res = _dblBuff.getGraphics();
      }
      else
      {
      }
    }

    return res;
  }

  public final void setFont(final java.awt.Font theFont)
  {
    //super.setFont(theFont);
  }

  public final boolean drawImage(final Image img,
                                 final int x,
                                 final int y,
                                 final int width,
                                 final int height,
                                 final ImageObserver observer)
  {
    if (_theDest == null)
      return true;

    return _theDest.drawImage(img, x, y, width, height, observer);
  }

  public final void drawLine(final int x1, final int y1, final int x2, final int y2)
  {
    if (_theDest == null)
      return;

    // doDecide whether to anti-alias this line
    this.switchAntiAliasOn(SwingCanvas.antiAliasThisLine(this.getLineWidth()));

    // check that the points are vaguely plottable
    if ((Math.abs(x1) > 9000) || (Math.abs(y1) > 9000) ||
      (Math.abs(x2) > 9000) || (Math.abs(y2) > 9000))
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
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public final void fillPolygon(final int[] xPoints,
                                final int[] yPoints,
                                final int nPoints)
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
  public final void drawPolyline(final int[] xPoints,
                                 final int[] yPoints,
                                 final int nPoints)
  {
    if (_theDest == null)
      return;

    // doDecide whether to anti-alias this line
    this.switchAntiAliasOn(SwingCanvas.antiAliasThisLine(this.getLineWidth()));

    _theDest.drawPolyline(xPoints, yPoints, nPoints);
  }
  
  public void drawPolyline(int[] points)
  {
      if (_theDest == null)
          return;
      // convert to normal format
      int[] xP = new int[points.length];
      int[] yP = new int[points.length];
      int len = points.length;
      
      for (int i = 0; i < points.length; i+= 2)
      {
          xP[i] = points[i];
          yP[i] = points[i+1];
      }
      drawPolyline(xP, yP, len);        
  }    

  /**
   * drawPolygon.
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public final void drawPolygon(final int[] xPoints,
                                final int[] yPoints,
                                final int nPoints)
  {
    if (_theDest == null)
      return;

    // doDecide whether to anti-alias this line
    this.switchAntiAliasOn(SwingCanvas.antiAliasThisLine(this.getLineWidth()));

    _theDest.drawPolygon(xPoints, yPoints, nPoints);
  }


  public final void drawOval(final int x, final int y, final int width, final int height)
  {
    if (_theDest != null)
      this.switchAntiAliasOn(SwingCanvas.antiAliasThisLine(this.getLineWidth()));

    if (_theDest != null)
      _theDest.drawOval(x, y, width, height);
  }

  public final void fillOval(final int x, final int y, final int width, final int height)
  {
    if (_theDest != null)
      _theDest.fillOval(x, y, width, height);
    //    else
    //      MWC.Utilities.Errors.Trace.trace("Graphics object not available when painting oval - occasionally happens in first pass", false);
  }

  public final void setColor(final java.awt.Color theCol)
  {
    if (_theDest == null)
      return;

    _theDest.setColor(theCol);
  }

  static public java.awt.BasicStroke getStrokeFor(final int style)
  {
    if (_myLineStyles == null)
    {
      _myLineStyles = new java.util.HashMap<Integer, BasicStroke>(5);
      _myLineStyles.put(new Integer(MWC.GUI.CanvasType.SOLID), new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
                                                                                        java.awt.BasicStroke.JOIN_MITER, 1, new float[]{5, 0}, 0));
      _myLineStyles.put(new Integer(MWC.GUI.CanvasType.DOTTED), new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
                                                                                         java.awt.BasicStroke.JOIN_MITER, 1, new float[]{2, 6}, 0));
      _myLineStyles.put(new Integer(MWC.GUI.CanvasType.DOT_DASH), new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
                                                                                           java.awt.BasicStroke.JOIN_MITER, 1, new float[]{4, 4, 12, 4}, 0));
      _myLineStyles.put(new Integer(MWC.GUI.CanvasType.SHORT_DASHES), new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
                                                                                               java.awt.BasicStroke.JOIN_MITER, 1, new float[]{6, 6}, 0));
      _myLineStyles.put(new Integer(MWC.GUI.CanvasType.LONG_DASHES), new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT,
                                                                                              java.awt.BasicStroke.JOIN_MITER, 1, new float[]{12, 6}, 0));
      _myLineStyles.put(new Integer(MWC.GUI.CanvasType.UNCONNECTED), new java.awt.BasicStroke(1));
    }

    return (java.awt.BasicStroke) _myLineStyles.get(new Integer(style));
  }

  public final void setLineStyle(final int style)
  {
    final java.awt.BasicStroke stk = getStrokeFor(style);
    final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
    g2.setStroke(stk);
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
      final java.awt.BasicStroke stk = new BasicStroke(width);
      final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
      g2.setStroke(stk);
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
      final java.awt.Graphics2D g2 = (java.awt.Graphics2D) _theDest;
      final BasicStroke bs = (BasicStroke) g2.getStroke();
      res = bs.getLineWidth();
    }
    else
    {
      res = _lineWidth;
    }

    return res;
  }

  public final void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
                            final int arcAngle)
  {
    if (_theDest != null)
    {
      // doDecide whether to anti-alias this line
      this.switchAntiAliasOn(SwingCanvas.antiAliasThisLine(this.getLineWidth()));
    }

    if (_theDest != null)
    {
      _theDest.drawArc(x, y, width, height, startAngle, arcAngle);
    }
  }

  public final void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
                            final int arcAngle)
  {
    if (_theDest != null)
      _theDest.fillArc(x, y, width, height, startAngle, arcAngle);
    //    else
    //      MWC.Utilities.Errors.Trace.trace("Graphics object not available when painting oval - occasionally happens in first pass", false);

  }

  public final void startDraw(final Object theVal)
  {
    _theDest = (java.awt.Graphics) theVal;

    // set the thickness
    final BasicStroke bs = new BasicStroke(_lineWidth);
    final Graphics2D g2 = (Graphics2D) _theDest;
    g2.setStroke(bs);
  }

  public final void endDraw(final Object theVal)
  {
    _theDest = null;
  }

  public void drawText(final String theStr, final int x, final int y)
  {
    if (_theDest == null)
      return;

    drawText(_theDest.getFont(), theStr, x, y);
  }


  public void drawText(final java.awt.Font theFont, final String theStr, final int x, final int y)
  {
    if (_theDest == null)
      return;

    // doDecide the anti-alias
    this.switchAntiAliasOn(SwingCanvas.antiAliasThis(theFont));

    if (_theDest == null)
      return;

    _theDest.setFont(theFont);
    _theDest.drawString(theStr, x, y);
  }

  public final void drawRect(final int x1, final int y1, final int wid, final int height)
  {
    if (_theDest == null)
      return;

    // doDecide whether to anti-alias this line
    this.switchAntiAliasOn(SwingCanvas.antiAliasThisLine(this.getLineWidth()));

    if (_theDest == null)
      return;

    _theDest.drawRect(x1, y1, wid, height);
  }

  public final void fillRect(final int x, final int y, final int wid, final int height)
  {
    if (_theDest == null)
      return;

    _theDest.fillRect(x, y, wid, height);
  }

  /**
   * get the current background colour
   */
  public final java.awt.Color getBackgroundColor()
  {
    return getBackground();
  }

  /**
   * set the current background colour, and trigger a screen update
   */
  public final void setBackgroundColor(final java.awt.Color theColor)
  {
    // set the colour in the parent
    setBackground(theColor);
    // invalidate the screen
    updateMe();
  }

  public final BoundedInteger getLineThickness()
  {
    return new BoundedInteger((int) this.getLineWidth(), 0, 4);
  }

  public final void setLineThickness(final BoundedInteger val)
  {
    setLineWidth(val.getCurrent());
  }


  ///////////////////////////////////////////////////////////
  // handle tooltip stuff
  ///////////////////////////////////////////////////////////
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

  ////////////////////////////////////////////////////////////
  // painter handling
  ////////////////////////////////////////////////////////////
  public final void addPainter(final CanvasType.PaintListener listener)
  {
    _thePainters.addElement(listener);
  }

  public final void removePainter(final CanvasType.PaintListener listener)
  {
    _thePainters.removeElement(listener);
  }

  public final Enumeration<CanvasType.PaintListener> getPainters()
  {
    return _thePainters.elements();
  }


  //////////////////////////////////////////////////////
  // screen redraw related
  //////////////////////////////////////////////////////

  public final void paint(final java.awt.Graphics p1)
  {
    // paint code moved to Update function
    update(p1);
  }

  /**
   * screen redraw, just repaint the buffer
   */
  public void update(final java.awt.Graphics p1)
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
  private void paintPlot()
  {
    if (_dblBuff == null)
    {
      // we may need to recreate the image if
      // we have just restored this session
      final java.awt.Dimension sz = this.getSize();

      // check that we are looking at a valid plot (the panel isn't minimised)
      if ((sz.width <= 0) || (sz.height <= 0))
      {
        // don't bother with repaint - there's no plot visible anyway
      }
      else
      {
        _dblBuff = createImage(sz.width,
                               sz.height);
      }

      // see if we have a screen size yet - if not we can't create our buffer
      if (_dblBuff == null)
      {
        return;
      }

    }

    // temporarily set the dblBuff object to null,
    // to stop anybody borrowing it - and write to a
    // temporary buffer
    final java.awt.Image tmpBuff = _dblBuff;
    _dblBuff = null;


    // hey, let's double-buffer it
    final java.awt.Graphics g1 = tmpBuff.getGraphics();

    // prepare the ground (remember the graphics dest for a start)
    startDraw(g1);

    // erase background
    final java.awt.Dimension sz = this.getSize();
    g1.setColor(this.getBackgroundColor());
    g1.fillRect(0, 0, sz.width, sz.height);

    // do the actual paint
    paintIt(this);

    // all finished, close it now
    endDraw(null);

    // and dispose
    g1.dispose();

    // put the image back in our buffer
    _dblBuff = tmpBuff;

  }

  /**
   * the real paint function, called when it's not satisfactory to
   * just paint in our safe double-buffered image.
   */
  public final void paintIt(final CanvasType canvas)
  {
    // go through our painters
    final Enumeration<CanvasType.PaintListener> enumer = _thePainters.elements();
    while (enumer.hasMoreElements())
    {
      final CanvasType.PaintListener thisPainter =
        (CanvasType.PaintListener) enumer.nextElement();

      if (canvas == null)
      {
        System.out.println("Canvas not ready yet");
      }
      else
      {
        // check the screen has been defined
        final Dimension area = this.getProjection().getScreenArea();
        if ((area == null) || (area.getWidth() <= 0) || (area.getHeight() <= 0))
        {
          return;
        }

        // it must be ok
        thisPainter.paintMe(canvas);
      }

    }
  }

  /**
   * first repaint the plot, then
   * trigger a screen update
   */
  public final void updateMe()
  {

    // reproduce the buffer, since something has clearly changed
    paintPlot();

    // ask the operating system to repaint us when it gets a chance
    repaint();
  }


  public final void setSize(final int p1, final int p2)
  {
    super.setSize(p1, p2);

    // reset our double buffer, since we've changed size
    _dblBuff = null;
  }


  //////////////////////////////////////////////////////
  // bean/editable methods
  /////////////////////////////////////////////////////
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

  /**CanvasType.PaintListener
   * provide close method, clear elements.
   */
  public final void close()
  {
    _thePainters.removeAllElements();
    _thePainters = null;
    _theProjection = null;
    _theDest = null;
    _theSize = null;
    _dblBuff = null;
    _tooltipHandler = null;
  }

  /**
   * return our name (used in editing)
   */
  public final String toString()
  {
    return "Appearance";
  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public final class CanvasInfo extends Editable.EditorType
  {

    public CanvasInfo(final SwingCanvas data)
    {
      super(data, data.toString(), "");
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("BackgroundColor", "the background color"),
          prop("LineThickness", "the line thickness"),
        };

        return res;

      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // methods to support anti-alias decisions
  //////////////////////////////////////////////////

  /**
   * do we anti-alias this font.
   *
   * @param theFont the font we are looking at
   * @return yes/no decision
   */
  private static boolean antiAliasThis(final Font theFont)
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
   * @param width the line width setting
   * @return yes/no
   */
  private static boolean antiAliasThisLine(final float width)
  {
    boolean res = false;

    if (width > 1)
      res = true;

    return res;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class CanvasTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public CanvasTest(final String val)
    {
      super(val);
    }

    public final void testMyParams()
    {
      final Editable ed = new SwingCanvas(null);
      Editable.editableTesterSupport.testParams(ed, this);
    }
  }
}
