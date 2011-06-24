/**
 * MWC.Algorithms.EarthModels.CompletelyFlatEarth
 *
 * This is a genuine flat earth algorithm which takes no account
 * of the curvature of the earth: thus a degree of longitude is as long at
 * 40 degs north as at the equator.
 *
 * This algorithm has been created for simplicity whilst testing
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
  private WorldLocation _workingLocation = new WorldLocation(0, 0, 0);

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * add a vector to a location: note that the value returned
   * is the same instance of an object each time
   */
  public WorldLocation add(WorldLocation start, WorldVector delta)
  {
    if ((delta.getRange()) == 0 && (delta.getDepth() == 0))
      return start;

    double dLat = delta.getRange() * Math.cos(delta.getBearing());
    double dLong = delta.getRange() * Math.sin(delta.getBearing());
    double dDepth = delta.getDepth();

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
  public double bearingBetween(WorldLocation from, WorldLocation to)
  {
    WorldVector res = from.subtract(to);

    return res.getBearing();
  }

  /**
   * rangeBetween
   *
   * @param from parameter for rangeBetween
   * @return the returned double
   */
  public double rangeBetween(WorldLocation from, WorldLocation to)
  {
    WorldVector res = subtract(from, to);
    return res.getRange();
  }

  public WorldVector subtract(WorldLocation from,
                              WorldLocation to)
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
  public WorldVector subtract(WorldLocation from, WorldLocation to, WorldVector res)
  {

    // the algorithm used here is from Short Sailing Calculations in the
    // admiralty manual of navigation

    //		WorldVector res;

    // perform brief check to ensure that the positions are not identical
    if (from.equals(to))
      return new WorldVector(0, 0, 0);

    // calculate the deltas
    double dLat = to.getLat() - from.getLat();
    double dLong = to.getLong() - from.getLong();
    double dDepth = to.getDepth() - from.getDepth();

    // produce range and bearing from the deltas
    double bearing = Math.atan2(dLong, dLat); // it's ok to keep this value in radians
    double range = Math.sqrt(dLat * dLat + dLong * dLong);

    res = new WorldVector(bearing, range, dDepth);

    return res;
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

    public FlatEarthTest(String val)
    {
      super(val);
    }

    public void testIt()
    {

      CompletelyFlatEarth cf = new CompletelyFlatEarth();
      FlatEarth fe = new FlatEarth();
      WorldLocation w1 = new WorldLocation(5.1, 1, 0);
      WorldLocation w2 = new WorldLocation(5, 0.4, 1);

      WorldLocation.setModel(cf);

      WorldVector res = cf.subtract(w1, w2);
      WorldVector res2 = fe.subtract(w1, w2);
      WorldVector res3 = w2.subtract(w1);
      System.out.println("res is:" + res.toString());
      System.out.println("res2 is:" + res2.toString());
      System.out.println("res3 is:" + res3.toString());

      // try adding them back in
      WorldLocation w4 = cf.add(w1, res);
      WorldLocation w5 = fe.add(w1, res2);
      assertEquals("Completely flat", w4, w2);
      assertEquals("locally flat lat", w5.getLat(), w2.getLat(), 0.0001);
      assertEquals("locally flat long", w5.getLong(), w2.getLong(), 0.0001);


    }

  }

}