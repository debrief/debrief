// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DatumSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: DatumSym.java,v $
// Revision 1.2  2004/05/25 15:37:47  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-02-07 09:49:24+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.3  2003-01-31 11:31:22+00  ian_mayo
// Provide methods to return shape as set of lines
//
// Revision 1.2  2002-05-28 09:25:54+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:00+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:13+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:40+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Geog;

import MWC.GUI.Shapes.Symbols.*;
import MWC.GenericData.*;
import MWC.GUI.*;

import java.util.Collection;
import java.util.Vector;
import java.awt.*;
import java.awt.geom.Point2D;

public class DatumSym extends PlainSymbol {

  public void getMetafile()
  {
  }

  public java.awt.Dimension getBounds(){
    // sort out the size of the symbol at the current scale factor
    java.awt.Dimension res = new java.awt.Dimension((int)(6 * getScaleVal()),(int)(6 * getScaleVal()));
    return res;
  }

  public void paint(CanvasType dest, WorldLocation centre)
  {
    paint(dest, centre, 0.0);
  }

  /** get this symbol as a sequence of lines.
   * The
   *
   * @return a collection of paths.  Each path is a collection of java.awt.Point objects.
   */
  public Collection getCoordinates() {
    Vector res = new Vector(0,1);

    // first do the cross

    Vector line1 = new Vector(0,1);
    Vector line2 = new Vector(0,1);

    int wid = (int)(2 * getScaleVal());
    line1.add(new Point(-wid, 0));
    line1.add(new Point(wid, 0));
    line2.add(new Point(0, -wid));
    line2.add(new Point(0, wid));

    res.add(line1);
    res.add(line2);

    // now the circle
    Collection circle = new Vector(0,1);

    // work our way around the circle, adding the pts

    int NUM_SEGMENTS = 30;
    for (int i=0; i<=NUM_SEGMENTS; i++)
    {
      // produce the current bearing
      double this_brg = (360.0 / NUM_SEGMENTS * i) / 180.0 * Math.PI;

      Point2D newP = new Point2D.Double(Math.sin(this_brg) * wid, Math.cos(this_brg) * wid);

      circle.add(newP);
    }

    res.add(circle);


    return res;
  }

  public void paint(CanvasType dest, WorldLocation theLocation, double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    java.awt.Point centre = dest.toScreen(theLocation);

    int wid = (int)(6 * getScaleVal());
    int tinyWid = (int) getScaleVal();
    int tinyWid_2 = (int)(tinyWid/2d);
    int wid_2 = (int)(wid/2d);

    // now the outer circle
    dest.drawOval(centre.x - wid_2, centre.y - wid_2, wid, wid);

    // try to sort out the are
    dest.fillArc(centre.x - wid_2, centre.y - wid_2, wid, wid, 180, 90);
    dest.fillArc(centre.x - wid_2, centre.y - wid_2, wid, wid, 0, 90);

  }

  public String getType()
  {
    return "Datum";
  }

}




