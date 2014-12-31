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
package MWC.GUI.Tools.Chart;


// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: HitTester.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: HitTester.java,v $
// Revision 1.2  2005/12/09 14:53:26  Ian.Mayo
// Minor tidying
//
// Revision 1.1  2004/08/31 08:05:09  Ian.Mayo
// Rename/remove old tests, so that we don't have non-testing classes whose named ends with Test (in support of Maven integration)
//
// Revision 1.2  2004/05/25 15:43:46  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:00+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:41+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:56+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-22 19:40:35+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.1  2001-01-03 13:41:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:16  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:20+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-08-17 10:31:13+01  administrator
// improve way dummy world data point is created
//
// Revision 1.2  1999-08-09 13:37:43+01  administrator
// add more versatile tests
//
// Revision 1.1  1999-07-27 10:59:45+01  administrator
// Initial revision
//

import java.awt.Point;
import java.awt.Rectangle;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class HitTester
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected static final int THRESHOLD = 20;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  /**
   * does the screen point represent a valid click on the data point?
   */
  static public boolean doesHit(final Point theHitScreen,
                                final WorldLocation theHitData,
                                final double theDistance,
                                final PlainProjection theProjection)
  {

    // first create a pretend point in data coordinates
    // (make it a point due east of the origin, so that it is still a valid earth location)
    final WorldLocation other = theHitData.add(new MWC.GenericData.WorldVector(1.57, theDistance, 0));

    // and convert it to screen coordinates
    final Point pOther = theProjection.toScreen(other);
    if (pOther == null) {
    	return false;
    }
    final int dx = pOther.x - theHitScreen.x;
    final int dy = pOther.y - theHitScreen.y;
    final int pDist = (int) Math.sqrt(dx * dx + dy * dy);

    return pDist < THRESHOLD;
  }

  static public boolean doesHit(final WorldLocation theHitScreen,
                                final WorldLocation theHitData,
                                final double theDistance,
                                final PlainProjection theProjection)
  {

    // first create a pretend point in data coordinates
    final Point pThis = theProjection.toScreen(theHitScreen);
    final Point pOther = theProjection.toScreen(theHitData);

    final int dx = pOther.x - pThis.x;
    final int dy = pOther.y - pThis.y;
    final int pDist = (int) Math.sqrt(dx * dx + dy * dy);

    return pDist < THRESHOLD;
  }

  static public boolean doesHit(final WorldLocation theHitScreen,
                                final WorldArea theHitData,
                                final double theDistance,
                                final PlainProjection theProjection)
  {

    // first create a pretend point in data coordinates
    final Point pThis = new Point(theProjection.toScreen(theHitScreen));
    final Point pOtherTL = new Point(theProjection.toScreen(theHitData.getTopLeft()));
    final Point pOtherBR = new Point(theProjection.toScreen(theHitData.getBottomRight()));

    // create a screen rectangle from the area
    final Rectangle rt = new Rectangle(pOtherTL);
    rt.add(pOtherBR);

    // grow the area by the threshold
    rt.grow(THRESHOLD, THRESHOLD);

    // are we in the threshold
    return (rt.contains(pThis));
  }

}
