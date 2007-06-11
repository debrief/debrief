// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: KingpinSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: KingpinSym.java,v $
// Revision 1.2  2004/05/25 15:37:39  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:35  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:22+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:21+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:56+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 14:01:04+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:41+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Buoys;

import MWC.GUI.Shapes.*;
import MWC.GUI.Shapes.Symbols.*;
import MWC.GenericData.*;
import MWC.GUI.*;

public class KingpinSym extends PlainSymbol {

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


  public void paint(CanvasType dest, WorldLocation theLocation, double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    java.awt.Point centre = dest.toScreen(theLocation);

    int wid = (int)(6 * getScaleVal() * 1.5);
    int tinyWid = (int) (getScaleVal() * 1.5);
    int tinyWid_2 = (int)(tinyWid/2d);
    int wid_2 = (int)(wid/2d);

    // start with the centre object
    dest.fillOval(centre.x - tinyWid_2, centre.y - tinyWid_2, tinyWid, tinyWid);

    // now the outer circle
    dest.drawOval(centre.x - wid_2, centre.y - wid_2, wid, wid);

    // now the direction of the stalk
    double theta = MWC.Algorithms.Conversions.Degs2Rads(45.0);
    double dX = Math.sin(theta) * wid_2;
    double dY = Math.cos(theta) * wid_2;

    dest.drawLine(centre.x + (int)dX, centre.y - (int)dY,
                  centre.x + wid, centre.y - wid);

    dest.drawLine(centre.x + wid, centre.y - wid,
                  centre.x + wid, centre.y - wid + 2 * tinyWid);

    dest.drawLine(centre.x + wid, centre.y - wid + 2 * tinyWid,
                  centre.x + wid + wid_2 , centre.y - wid + 2 * tinyWid - wid_2);




  }

  public String getType()
  {
    return "Kingpin";
  }

}




