// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DestroyerSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: DestroyerSym.java,v $
// Revision 1.2  2004/05/25 15:37:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:24+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:56+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:52+01  ian_mayo
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

public class DestroyerSym extends PlainSymbol {

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

    int wid = (int)(6 * getScaleVal());
    int wid_2 = (int)(wid/2d);
    int wid_3 = (int)(wid/3d);

    // first the side pieces
    dest.drawLine(centre.x - wid_3, centre.y - wid_2, centre.x - wid_3, centre.y + wid_2);
    dest.drawLine(centre.x + wid_3, centre.y - wid_2, centre.x + wid_3, centre.y + wid_2);

    // join the size pieces at the bottom
    dest.drawLine(centre.x - wid_3, centre.y + wid_2, centre.x, centre.y + (int)(wid * 0.85));
    dest.drawLine(centre.x + wid_3, centre.y + wid_2, centre.x, centre.y + (int)(wid * 0.85));

    // now the top pieces
    dest.drawLine(centre.x, centre.y - (int)(1.2 * wid), centre.x - (int)(0.5 * wid), centre.y - (int)(wid*0.33) );
    dest.drawLine(centre.x, centre.y - (int)(1.2 * wid), centre.x + (int)(0.5 * wid), centre.y - (int)(wid*0.37) );

  }

  public String getType()
  {
    return "Destroyer";
  }

}




