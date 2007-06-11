// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CruiserSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CruiserSym.java,v $
// Revision 1.2  2004/05/25 15:37:56  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:15+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:56+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:51+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:08+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:47+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import MWC.GUI.Shapes.*;
import MWC.GUI.Shapes.Symbols.*;
import MWC.GenericData.*;
import MWC.GUI.*;

public class CruiserSym extends PlainSymbol {

  public void getMetafile()
  {
  }

  public java.awt.Dimension getBounds(){
    // sort out the size of the symbol at the current scale factor
    java.awt.Dimension res = new java.awt.Dimension((int)(2 * 4 * getScaleVal()),(int)( 2 * 4 * getScaleVal()));
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

    int wid = (int)(getScaleVal() * 2);

    // start with the centre object
    dest.fillOval(centre.x - wid/4, centre.y - wid/4, wid/2, wid/2);

    double theta = MWC.Algorithms.Conversions.Degs2Rads(120.0);

    int deltaX = (int)(Math.sin(theta) * wid * 3.0);
    int deltaY =(int)(Math.cos(theta) * wid * 3.0);

    // first travel from above the centre down to the points
    dest.drawLine(centre.x, centre.y - wid * 3, centre.x + deltaX, centre.y - deltaY);
    dest.drawLine(centre.x, centre.y - wid * 3, centre.x - deltaX, centre.y - deltaY);
    dest.drawLine(centre.x - deltaX, centre.y - deltaY, centre.x + deltaX, centre.y - deltaY);

    // join these points up
    dest.drawLine(centre.x - deltaX, centre.y - deltaY, centre.x, centre.y - (int)(2.2 * deltaY));
    dest.drawLine(centre.x + deltaX, centre.y - deltaY, centre.x, centre.y - (int)(2.2 * deltaY));


  }

  public String getType()
  {
    return "Cruiser";
  }

}




