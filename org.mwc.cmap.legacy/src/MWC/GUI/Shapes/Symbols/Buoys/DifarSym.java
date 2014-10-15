/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: DifarSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: DifarSym.java,v $
// Revision 1.2  2004/05/25 15:37:37  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:35  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:14+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:21+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:03+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:11+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-18 09:27:41+00  novatech
// Don't pass NULL parameter to getStringWidth
//
// Revision 1.1  2001-01-16 19:29:33+00  novatech
// Initial revision
//
// Revision 1.2  2001-01-11 15:27:53+00  novatech
// lined up text label, and only show text label at regular and large sizes
//
// Revision 1.1  2001-01-03 13:42:17+00  novatech
// Initial revision
//


package MWC.GUI.Shapes.Symbols.Buoys;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class DifarSym extends PlainSymbol {

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
    final java.awt.Dimension res = new java.awt.Dimension((int)(2 * 4 * getScaleVal()),(int)( 2 * 4 * getScaleVal()));
    return res;
  }

  public void paint(final CanvasType dest, final WorldLocation centre)
  {
    paint(dest, centre, 0.0);
  }


  public void paint(final CanvasType dest, final WorldLocation theLocation, final double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    final java.awt.Point centre = dest.toScreen(theLocation);

    final int wid = (int)(4 * getScaleVal());

    // draw our box, line by line
    final java.awt.Point tl = new java.awt.Point(centre.x - wid, centre.y - wid);
    final java.awt.Point br = new java.awt.Point(centre.x + wid, centre.y + wid);

    if(showSimplifiedSymbol())
    {
      // ignore the letter
    }
    else
    {
      // do the central character
      // what's the width?
      final String str = "d";
      final int strW = dest.getStringWidth(_myFont,str);
      final int strH = dest.getStringHeight(_myFont);
      dest.drawText(_myFont, str, centre.x - strW/2, centre.y + strH/4);
    }

    dest.drawLine(tl.x, br.y, br.x, br.y);
    dest.drawLine(br.x, br.y, br.x, tl.y);
    dest.drawLine(br.x, tl.y, tl.x + wid/2, tl.y);
    dest.drawLine(tl.x + wid /2, tl.y, tl.x, tl.y + wid / 2);
    dest.drawLine(tl.x, tl.y + wid / 2, tl.x, br.y);
  }

  public String getType()
  {
    return "Difar";
  }

}




