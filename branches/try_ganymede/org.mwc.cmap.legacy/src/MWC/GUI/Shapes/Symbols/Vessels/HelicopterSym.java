// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: HelicopterSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: HelicopterSym.java,v $
// Revision 1.2  2004/05/25 15:38:02  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:37  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:27+00  ian_mayo
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
// Revision 1.0  2001-07-17 08:43:07+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:48+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class HelicopterSym extends PlainSymbol {

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
    paint(dest, centre, 0.0);
  }


  public void paint(CanvasType dest, WorldLocation theLocation, double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    java.awt.Point centre = dest.toScreen(theLocation);

    int wid = (int)(4 * getScaleVal());

    // start with the centre object
    dest.drawOval(centre.x - wid/2, centre.y - wid/2, wid, wid);

    double theta = MWC.Algorithms.Conversions.Degs2Rads(120.0);
    double thisAng = MWC.Algorithms.Conversions.Degs2Rads(180);

    // now for the arcs
    for(int i=0; i<3;i++)
    {
      // the inner end of the arc

      int deltaX = (int)(Math.sin(thisAng) * wid / 2.0);
      int deltaY =(int)(Math.cos(thisAng) * wid / 2.0);
      int endX = (int)(Math.sin(thisAng) * wid * 1.5);
      int endY = (int)(Math.cos(thisAng) * wid * 1.5);

      // step to the next angle
      thisAng += theta;

      // and the line
      dest.drawLine(centre.x + deltaX, centre.y - deltaY, centre.x + endX, centre.y - endY);

    }

  }

  public String getType()
  {
    return "Helicopter";
  }

}




