
/**
 * MWC.GUI.Shapes.Symbols.SquareSymbol
 */// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SquareSymbol.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SquareSymbol.java,v $
// Revision 1.2  2004/05/25 15:37:50  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-01-31 11:31:23+00  ian_mayo
// Provide methods to return shape as set of lines
//
// Revision 1.3  2003-01-30 16:09:09+00  ian_mayo
// Static method with shape name
//
// Revision 1.2  2002-05-28 09:25:54+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:02+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:40+00  novatech
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:14+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:16  ianmayo
// initial version
//
// Revision 1.3  2000-11-17 09:07:10+00  ian_mayo
// move fill status to parent
//
// Revision 1.2  2000-05-23 13:38:04+01  ian_mayo
// allow configurable "fill" option for symbol
//
// Revision 1.1  1999-10-12 15:36:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:38+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-23 14:03:48+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.1  1999-07-07 11:10:05+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:58+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 16:08:47+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:03+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Geog;

import MWC.GenericData.*;
import MWC.GUI.*;
import java.io.*;
import java.util.Collection;
import java.util.Vector;
import java.awt.*;

import MWC.GUI.Shapes.Symbols.*;

public class SquareSymbol extends PlainSymbol implements Serializable
{
  /**
 * // keep track of versions
 */
  static final long serialVersionUID = 1;

  /** the name of our shape
   *
   */
  static public final String SQUARE_SYMBOL = "Square";

  /**
   * the size of the symbol
   */
  protected final int wid = 4;

  /**
   * java.util.Vector _theMetafile
   */
  private java.util.Vector _theMetafile;

  /**
   * <init>
   *
   */
  public SquareSymbol(){
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
    return SQUARE_SYMBOL;
  }

  /**
   * getMetafile
   *
   */
  public void getMetafile()
  {
    // return the metafile
  }

  /** get this symbol as a sequence of lines.
   * The
   *
   * @return a collection of paths.  Each path is a collection of java.awt.Point objects.
   */
  public Collection getCoordinates() {
    Vector res = new Vector(0,1);

    Vector line1 = new Vector(0,1);

    int wid = (int)(2 * getScaleVal());
    line1.add(new Point(-wid, -wid));
    line1.add(new Point( wid, -wid));
    line1.add(new Point( wid,  wid));
    line1.add(new Point( -wid, wid));
    line1.add(new Point(-wid, -wid));

    res.add(line1);

    return res;
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
      dest.fillRect(centre.x - sWid, centre.y - sWid,  sWid * 2,  sWid * 2);
    else
      dest.drawRect(centre.x - sWid, centre.y - sWid,  sWid * 2,  sWid * 2);
  }


}



