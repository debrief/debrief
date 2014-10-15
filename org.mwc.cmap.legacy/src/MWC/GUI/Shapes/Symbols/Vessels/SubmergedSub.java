
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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SubmergedSub.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SubmergedSub.java,v $
// Revision 1.2  2004/05/25 15:38:06  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:37  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:19+00  ian_mayo
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
// Revision 1.0  2001-07-17 08:43:07+01  administrator
// Initial revision
//
// Revision 1.3  2001-02-01 09:31:59+00  novatech
// comments added
//
// Revision 1.2  2001-01-26 11:19:35+00  novatech
// corrected initial direction of vessel
//
// Revision 1.1  2001-01-16 19:29:49+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class SubmergedSub extends PlainSymbol {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * getMetafile
   *
   */
  public void getMetafile()
  {
  }

  /**
   * getBounds
   *
   * @return the returned java.awt.Dimension
   */
  public java.awt.Dimension getBounds(){
    // sort out the size of the symbol at the current scale factor
    final java.awt.Dimension res = new java.awt.Dimension((int)(2 * 4 * getScaleVal()),(int)( 2 * 4 * getScaleVal()));
    return res;
  }

  /**
   * paint
   *
   * @param dest parameter for paint
   *
   */
  public void paint(final CanvasType dest, final WorldLocation centre)
  {
    paint(dest, centre, 90.0 / 180 * Math.PI);
  }


  /**
   * paint
   *
   * @param dest parameter for paint
   * @param theLocation centre for symbol
   * @param direction direction in Radians
   */
  public void paint(final CanvasType dest, final WorldLocation theLocation, final double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    final java.awt.Point centre = dest.toScreen(theLocation);

    final int wid = (int)(2 * getScaleVal());

    // start with the centre object
    dest.fillOval(centre.x - wid/4, centre.y - wid/4, wid/2, wid/2);

    final double _theMaxima = getScaleVal() * 5;
    final double _theMinima = getScaleVal() * 2;

    // number of line segments to draw
    final int NUM_SEGMENTS = 20;

    // the centre of the ellipse
    java.awt.Point lastPoint = null;

    // produce the orientation in radians
    final double orient = direction;

    for (int i=0; i<=NUM_SEGMENTS; i++)
    {
      // produce the current bearing
      final double this_brg = (360.0 / NUM_SEGMENTS * i) / 180.0 * Math.PI;


      // first produce a standard ellipse of the correct size
      final double x1 = Math.sin(this_brg) * _theMaxima;
      double y1 = Math.cos(this_brg) * _theMinima;

      // now produce the range out to the edge of the ellipse at
      // this point
      final double r =  Math.sqrt(Math.pow(x1,2)+Math.pow(y1,2));

      // to prevent div/0 error in atan, make y1 small if zero
      if(y1 == 0)
        y1 = 0.0000001;

      // and the new bearing to the correct point on the ellipse
      final double tr = Math.atan2(y1,x1) + orient;


      // actually add the new rotation
      final double x2 = r * Math.sin(tr);
      final double y2 = r * Math.cos(tr);

      // add offsets to centre point
      final java.awt.Point p2 = new java.awt.Point(centre);
      p2.translate((int)x2, -(int)y2);

      if(lastPoint == null)
      {
        // don't do anything, since this is the first pass
      }
      else
      {
        dest.drawLine(lastPoint.x,
                      lastPoint.y,
                      p2.x,
                      p2.y);
      }

      // store the last point
      lastPoint = new java.awt.Point(p2);

    }

    // also draw the stalk which represents the conning tower
    final double orient_perpendicular = orient + Math.PI/2;
    double stalkX = Math.sin(orient_perpendicular);
    double stalkY = Math.cos(orient_perpendicular);

    // check that the stalk is pointing upwards
    if(stalkY < 0)
    {
      stalkY *= -1.0;
      stalkX *= -1.0;
    }

    // the stalk starts at the edge of the cylinder, and finishes
    dest.drawLine(centre.x + (int)(stalkX * wid), centre.y - (int)(stalkY * wid),
                  centre.x + (int)(stalkX * wid * 2.5), centre.y - (int)(stalkY * wid * 2.5));

  }

  /**
   * getType
   *
   * @return the returned String
   */
  public String getType()
  {
    return "Submarine";
  }

}




