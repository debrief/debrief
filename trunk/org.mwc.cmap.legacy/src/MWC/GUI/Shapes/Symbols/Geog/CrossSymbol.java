// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CrossSymbol.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CrossSymbol.java,v $
// Revision 1.2  2004/05/25 15:37:45  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-02-07 09:49:28+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.3  2003-01-30 16:09:38+00  ian_mayo
// Return ourselves as collection of lines
//
// Revision 1.2  2002-05-28 09:25:54+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:00+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:40+00  novatech
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:15+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:07  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:36+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:37+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:04+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:57+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 14:25:00+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:01+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Geog;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class CrossSymbol extends PlainSymbol {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void getMetafile()
  {
  }

  /** get this symbol as a sequence of lines
   *
   * @return
   */
  public Vector<Vector<Point2D>> getCoordinates() {
  	Vector<Vector<Point2D>> res = new Vector<Vector<Point2D>>(0,1);

  	Vector<Point2D> line1 = new Vector<Point2D>(0,1);
  	Vector<Point2D> line2 = new Vector<Point2D>(0,1);

    int wid = (int)(2 * getScaleVal());
    line1.add(new Point(-wid, 0));
    line1.add(new Point(wid, 0));
    line2.add(new Point(0, -wid));
    line2.add(new Point(0, wid));

    res.add(line1);
    res.add(line2);

    return res;
  }

  public java.awt.Dimension getBounds()
  {
    return new java.awt.Dimension((int)(2 * 2 * getScaleVal()),(int)(2 * 2 * getScaleVal()));
  }

  public void paint(CanvasType dest, WorldLocation centre)
  {
    paint(dest, centre, 0.0);
  }


  public void paint(CanvasType dest, WorldLocation theLocation, double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    java.awt.Point centre = dest.toScreen(theLocation);

    int wid = (int)(2 * getScaleVal());

    // draw our cross
    dest.drawLine(centre.x - wid, centre.y,  centre.x + wid,  centre.y);
    dest.drawLine(centre.x, centre.y - wid,  centre.x,  centre.y + wid);
  }

  public String getType()
  {
    return "Cross";
  }

}




