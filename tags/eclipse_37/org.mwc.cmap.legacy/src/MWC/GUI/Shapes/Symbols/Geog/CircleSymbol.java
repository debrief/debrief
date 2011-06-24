
/**
 * MWC.GUI.Shapes.Symbols.SquareSymbol
 */// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CircleSymbol.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CircleSymbol.java,v $
// Revision 1.2  2004/05/25 15:37:43  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-01-31 11:31:21+00  ian_mayo
// Provide methods to return shape as set of lines
//
// Revision 1.2  2002-05-28 09:25:55+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:59+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:41+00  novatech
// Initial revision
//


package MWC.GUI.Shapes.Symbols.Geog;

import java.awt.geom.Point2D;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class CircleSymbol extends PlainSymbol
{
  /**
 * // keep track of versions
 */
  static final long serialVersionUID = 1;

  /**
   * the size of the symbol
   */
  protected final int wid = 4;

  /**
   * <init>
   *
   */
  public CircleSymbol(){
    super();
     // construct the symbol from a sequence of metafile commands,
     // if we think it is really necessary...
  }

  /**
   * getBounds
   *
   * @return the returned java.awt.Dimension
   */
  public java.awt.Dimension getBounds()
  {
    int sWid = (int)(wid * getScaleVal());
    return new java.awt.Dimension(2 * sWid, 2 * sWid);
  }

  /**
   * getType
   *
   * @return the returned String
   */
  public String getType()
  {
    return "Circle";
  }

  /** get this symbol as a sequence of lines.
   * The
   *
   * @return a collection of paths.  Each path is a collection of java.awt.Point objects.
   */
  public Vector<Vector<Point2D>> getCoordinates() {
  	Vector<Vector<Point2D>> res = new Vector<Vector<Point2D>>(0,1);

    // now the circle
    Vector<Point2D> circle = new Vector<Point2D>(0,1);

    // work our way around the circle, adding the pts
    int NUM_SEGMENTS = 30;
    for (int i=0; i<=NUM_SEGMENTS; i++)
    {
      // produce the current bearing
      double this_brg = (360.0 / NUM_SEGMENTS * i) / 180.0 * Math.PI;

      Point2D newP = new Point2D.Double(Math.sin(this_brg) * wid/2, Math.cos(this_brg) * wid/2);

      circle.add(newP);
    }

    // store the circle
    res.add(circle);

    return res;
  }

  /**
   * getMetafile
   *
   */
  public void getMetafile()
  {
    // return the metafile
  }

  public void paint(CanvasType dest, WorldLocation centre)
  {
    paint(dest, centre, 0.0);
  }


  /**
   * paint
   *
   * @param dest parameter for paint
   * @param theLocation the place where we paint it
   */
  public void paint(CanvasType dest, WorldLocation theLocation, double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    java.awt.Point centre = dest.toScreen(theLocation);

    // calculate the scaled width
    int sWid = (int)(wid * getScaleVal());

    // draw our square at the set radius around the centre
    if(getFillSymbol())
      dest.fillOval(centre.x - sWid, centre.y - sWid,  sWid * 2,  sWid * 2);
    else
      dest.drawOval(centre.x - sWid, centre.y - sWid,  sWid * 2,  sWid * 2);
  }


}



