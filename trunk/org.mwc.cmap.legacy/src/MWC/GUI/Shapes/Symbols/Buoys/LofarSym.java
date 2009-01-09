// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: LofarSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: LofarSym.java,v $
// Revision 1.2  2004/05/25 15:37:41  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:35  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:13+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:21+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:04+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:11+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-18 09:27:40+00  novatech
// Don't pass NULL parameter to getStringWidth
//
// Revision 1.1  2001-01-16 19:29:32+00  novatech
// Initial revision
//
// Revision 1.2  2001-01-11 15:27:34+00  novatech
// lined up text label, and only show text label at regular and large sizes
//
// Revision 1.1  2001-01-03 13:42:18+00  novatech
// Initial revision
//


package MWC.GUI.Shapes.Symbols.Buoys;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class LofarSym extends PlainSymbol {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** the font we use for the D DifarSymbols */
  private static java.awt.Font _myFont = new java.awt.Font("Arial",
                                                            java.awt.Font.PLAIN,
                                                            12);

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

    int wid = (int)(4 * getScaleVal());

    // draw our box, line by line
    java.awt.Point tl = new java.awt.Point(centre.x - wid, centre.y - wid);
    java.awt.Point br = new java.awt.Point(centre.x + wid, centre.y + wid);

    if(showSimplifiedSymbol())
    {
      // ignore the letter
    }
    else
    {
      // do the central character
      // what's the width?
      String str = "L";
      int strW = dest.getStringWidth(_myFont,str);
      int strH = dest.getStringHeight(_myFont);
      dest.drawText(_myFont, str, centre.x - strW/2, centre.y + strH/4);
    }

    dest.drawLine(tl.x, br.y, br.x, br.y);
    dest.drawLine(br.x, br.y, br.x, centre.y - wid/2);
    dest.drawLine(br.x, centre.y - wid/2, centre.x + wid/2, tl.y);
    dest.drawLine(centre.x + wid/2, tl.y, tl.x, tl.y);
    dest.drawLine(tl.x, tl.y, tl.x, br.y);
  }

  public String getType()
  {
    return "Lofar";
  }

}




