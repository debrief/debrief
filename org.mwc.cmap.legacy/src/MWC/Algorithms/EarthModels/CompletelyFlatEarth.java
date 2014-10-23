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
package MWC.Algorithms.EarthModels;

/**
 * Title:        Debrief 2000
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      PlanetMayo
 * @author Ian Mayo
 * @version 1.0
 */

// Copyright MWC 1999
// $RCSfile: CompletelyFlatEarth.java,v $
// $Author: Ian.Mayo $
// $Log: CompletelyFlatEarth.java,v $
// Revision 1.4  2004/08/31 09:38:00  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.3  2004/08/25 11:22:07  Ian.Mayo
// Remove main methods which just run junit tests
//
// Revision 1.2  2004/05/24 16:28:15  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:13  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:06:59  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-03-14 08:35:00+00  ian_mayo
// provide subtract methods which re-use single WorldVector object, to reduce object creation
//
// Revision 1.3  2003-02-14 10:17:42+00  ian_mayo
// Allow "add" operation when zero lat/long delta, but with non-zero depth change
//
// Revision 1.2  2002-05-28 09:25:33+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-09-19 09:15:25+01  administrator
// Fix bug
//
// Revision 1.0  2001-07-17 08:47:01+01  administrator
// Initial revision
//
// Revision 1.1  2001-06-04 09:28:34+01  novatech
// Initial revision
//

import MWC.Algorithms.EarthModel;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class CompletelyFlatEarth implements EarthModel
{


  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  /**
   * working world location, used to reduce amount of object creation
   */
  private final WorldLocation _workingLocation = new WorldLocation(0, 0, 0);

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * add a vector to a location: note that the value returned
   * is the same instance of an object each time
   */
  public WorldLocation add(final WorldLocation start, final WorldVector delta)
  {
    if ((delta.getRange()) == 0 && (delta.getDepth() == 0))
      return start;

    final double dLat = delta.getRange() * Math.cos(delta.getBearing());
    final double dLong = delta.getRange() * Math.sin(delta.getBearing());
    final double dDepth = delta.getDepth();

    // use our internal object for calculation, to reduce object creation
    _workingLocation.setLat(start.getLat() + dLat);
    _workingLocation.setLong(start.getLong() + dLong);
    _workingLocation.setDepth(start.getDepth() + dDepth);

    // 6. Hooray, now produce the result
    return _workingLocation;
  }


  /**
   * bearingBetween
   *
   * @param from parameter for bearingBetween
   * @return the returned double
   */
  public double bearingBetween(final WorldLocation from, final WorldLocation to)
  {
    final WorldVector res = from.subtract(to);

    return res.getBearing();
  }

  /**
   * rangeBetween
   *
   * @param from parameter for rangeBetween
   * @return the returned double
   */
  public double rangeBetween(final WorldLocation from, final WorldLocation to)
  {
    final WorldVector res = subtract(from, to);
    return res.getRange();
  }

  public WorldVector subtract(final WorldLocation from,
                              final WorldLocation to)
  {
    WorldVector res = new WorldVector(0, 0, 0);
    res = subtract(from, to, res);
    return res;
  }

  /**
   * subtract
   *
   * @param from parameter for subtract
   * @return the returned WorldVector
   */
  public WorldVector subtract(final WorldLocation from, final WorldLocation to, final WorldVector res)
  {
	 
    // the algorithm used here is from Short Sailing Calculations in the
    // admiralty manual of navigation

    //		WorldVector res;

    // perform brief check to ensure that the positions are not identical
    if (from.equals(to))
      return new WorldVector(0, 0, 0);

    // calculate the deltas
    final double dLat = to.getLat() - from.getLat();
    final double dLong = to.getLong() - from.getLong();
    final double dDepth = to.getDepth() - from.getDepth();
    WorldVector result = res;

    // produce range and bearing from the deltas
    final double bearing = Math.atan2(dLong, dLat); // it's ok to keep this value in radians
    final double range = Math.sqrt(dLat * dLat + dLong * dLong);

    result = new WorldVector(bearing, range, dDepth);

    return result;
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class FlatEarthTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public FlatEarthTest(final String val)
    {
      super(val);
    }

    public void testIt()
    {

      final CompletelyFlatEarth cf = new CompletelyFlatEarth();
      final FlatEarth fe = new FlatEarth();
      final WorldLocation w1 = new WorldLocation(5.1, 1, 0);
      final WorldLocation w2 = new WorldLocation(5, 0.4, 1);

      WorldLocation.setModel(cf);

      final WorldVector res = cf.subtract(w1, w2);
      final WorldVector res2 = fe.subtract(w1, w2);
      final WorldVector res3 = w2.subtract(w1);
      System.out.println("res is:" + res.toString());
      System.out.println("res2 is:" + res2.toString());
      System.out.println("res3 is:" + res3.toString());

      // try adding them back in
      final WorldLocation w4 = cf.add(w1, res);
      final WorldLocation w5 = fe.add(w1, res2);
      assertEquals("Completely flat", w4, w2);
      assertEquals("locally flat lat", w5.getLat(), w2.getLat(), 0.0001);
      assertEquals("locally flat long", w5.getLong(), w2.getLong(), 0.0001);


    }

  }

}