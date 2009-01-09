// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TAFrigateSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: TAFrigateSym.java,v $
// Revision 1.2  2004/05/25 15:38:08  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:37  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:10+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:55+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:58+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:09+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:49+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class TAFrigateSym extends PlainSymbol {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
    paint(dest, centre, 90.0);
  }


  public void paint(CanvasType dest, WorldLocation theLocation, double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    java.awt.Point centre = dest.toScreen(theLocation);

    int wid = (int)(4 * getScaleVal());
    int wid_2 = (int)(wid/2d);
    int wid_4 = (int)(wid/4d);

    // start with the cross-over bits
    dest.drawLine(centre.x - wid, centre.y - wid_2, centre.x + wid, centre.y + wid_2);
    dest.drawLine(centre.x - wid, centre.y + wid_2, centre.x + wid, centre.y - wid_2);
    dest.drawLine(centre.x - wid, centre.y + wid_2, centre.x - wid, centre.y - wid_2);
    dest.drawLine(centre.x + wid, centre.y + wid_2, centre.x + wid, centre.y - wid_2);

    // now the dangly bits
    int bottom_y = centre.y + 5 * wid_4;
    dest.drawLine(centre.x - wid, centre.y + wid_2, centre.x, bottom_y);
    dest.drawLine(centre.x + wid, centre.y + wid_2, centre.x, bottom_y);

    // and the tail
    int tail_size = wid_2;
    dest.drawArc(centre.x - tail_size/2, bottom_y , tail_size, tail_size, 90, (int)(180));
    dest.drawArc(centre.x - tail_size/2, bottom_y + tail_size, tail_size, tail_size, 270, (int)(180));

  }

  public String getType()
  {
    return "TA Frigate";
  }

}




