// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: MerchantSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: MerchantSym.java,v $
// Revision 1.2  2004/05/25 15:38:03  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:37  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:07+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:55+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:57+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:09+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:49+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import MWC.GUI.Shapes.*;
import MWC.GUI.Shapes.Symbols.*;
import MWC.GenericData.*;
import MWC.GUI.*;

public class MerchantSym extends PlainSymbol {

  public void getMetafile()
  {
  }

  public java.awt.Dimension getBounds(){
    // sort out the size of the symbol at the current scale factor
    java.awt.Dimension res = new java.awt.Dimension((int)(6 * getScaleVal()),(int)( 6 * getScaleVal()));
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
    int tinyWid = (int) getScaleVal();

    // start with the centre object
    dest.fillOval(centre.x - tinyWid/2, centre.y - tinyWid/2, tinyWid, tinyWid);

    // now the outer circle
    dest.drawOval(centre.x - wid/2, centre.y - wid/2, wid, wid);

  }

  public String getType()
  {
    return "Merchant";
  }

}




