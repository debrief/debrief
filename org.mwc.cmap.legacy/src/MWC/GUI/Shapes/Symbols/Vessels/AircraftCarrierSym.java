/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: AircraftCarrierSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: AircraftCarrierSym.java,v $
// Revision 1.3  2004/05/25 15:37:53  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.2  2003/11/21 08:55:22  Ian.Mayo
// Correct the name
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:18+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:57+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:49+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:08+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:50+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class AircraftCarrierSym extends PlainSymbol {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

    final int wid = (int)(6 * getScaleVal());
    final int tinyWid = (int) getScaleVal();

    // start with the centre object
    dest.fillOval(centre.x - tinyWid/2, centre.y - tinyWid/2, tinyWid, tinyWid);

    // now the outer circle
    dest.drawOval(centre.x - wid/2, centre.y - wid/2, wid, wid);

    // now the slash
    final double theta = MWC.Algorithms.Conversions.Degs2Rads(45);
    final double dX = Math.sin(theta) * wid * 1.3;
    final double dY = Math.sin(theta) * wid * 1.3;

    dest.drawLine(centre.x - (int)dX, centre.y + (int)dY,
                  centre.x + (int)dX, centre.y - (int)dY);

  }

  public String getType()
  {
    return "Carrier";
  }


  @Override
  public PlainSymbol create()
  {
    return new AircraftCarrierSym();
  }
}




