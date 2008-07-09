// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WorldLocation.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: WorldLocation.java,v $
// Revision 1.7  2006/05/02 13:43:05  Ian.Mayo
// Correct typo
//
// Revision 1.6  2006/04/21 07:44:30  Ian.Mayo
// More useful range-from method
//
// Revision 1.5  2005/03/09 14:57:33  Ian.Mayo
// Add method to calculate the perpendicular distance off track.
//
// Revision 1.4  2004/11/01 11:35:21  Ian.Mayo
// Move the relative location convenience class into here
//
// Revision 1.3  2004/08/31 09:38:29  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/24 16:27:36  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:01  Ian.Mayo
// Initial import
//
// Revision 1.7  2003-03-14 08:35:18+00  ian_mayo
// Use optimised subtract method for WorldLocation
//
// Revision 1.6  2003-01-17 15:11:10+00  ian_mayo
// Handle missing depth data
//
// Revision 1.5  2002-11-13 13:14:43+00  ian_mayo
// added isValid flag
//
// Revision 1.4  2002-10-11 08:34:50+01  ian_mayo
// IntelliJ optimisations
//
// Revision 1.3  2002-09-24 11:01:16+01  ian_mayo
// tidy up doc comments
//
// Revision 1.2  2002-05-28 09:25:33+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:16+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:35+01  ian_mayo
// Initial revision
//
// Revision 1.6  2002-03-12 15:29:19+00  administrator
// Stop toString being final, so that we can over-ride it in our Path editor
//
// Revision 1.5  2001-11-14 19:51:02+00  administrator
// Change to printing style
//
// Revision 1.4  2001-10-22 14:50:43+01  administrator
// Setup the tests correctly (specifying the correct earth model)
//
// Revision 1.3  2001-09-24 10:04:59+01  administrator
// Complete removal of old format Assert methods
//
// Revision 1.2  2001-09-23 09:07:47+01  administrator
// updated JUnit assert code
//
// Revision 1.1  2001-08-21 12:11:07+01  administrator
// Add improved "equals" method, which will over-ride the Object method
//
// Revision 1.0  2001-07-17 08:46:39+01  administrator
// Initial revision
//
// Revision 1.4  2001-06-04 09:29:07+01  novatech
// add accessor method to let us modify the earth model used
//
// Revision 1.3  2001-01-21 21:39:51+00  novatech
// add JUnit testing
//
// Revision 1.2  2001-01-18 13:22:41+00  novatech
// optimisations to reduce object creation
//
// Revision 1.1  2001-01-03 13:43:11+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:40  ianmayo
// initial version
//
// Revision 1.7  2000-09-27 14:32:11+01  ian_mayo
// add new constructor, taking fewer parameters
//
// Revision 1.6  2000-07-07 09:59:33+01  ian_mayo
// check that earth model is declared before we start doing calcs
//
// Revision 1.5  2000-04-19 11:39:50+01  ian_mayo
// make methods final, switch to EarthModel calcs
//
// Revision 1.4  2000-03-07 10:10:40+00  ian_mayo
// Make lat,long,depth package visible, not private
//
// Revision 1.3  1999-12-03 14:37:13+00  ian_mayo
// tidy up location returned
//
// Revision 1.2  1999-11-09 10:16:42+00  ian_mayo
// show units in comment for rangeFrom calculation
//
// Revision 1.1  1999-10-12 15:37:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:53+01  administrator
// Initial revision
//
// Revision 1.5  1999-07-27 09:23:55+01  administrator
// allowed setting of lat/long/depth and created "addToMe" method
//
// Revision 1.4  1999-07-23 14:03:53+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.3  1999-07-19 12:40:33+01  administrator
// added storage of sub-second time data (Switched to storing as Long rather than java.utils.Date)
//
// Revision 1.2  1999-07-12 08:09:20+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:11+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:02+01  sm11td
// Initial revision
//
// Revision 1.3  1999-06-01 16:49:20+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-04 08:02:23+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:32:59+00  sm11td
// Initial revision
//

package MWC.GenericData;

import MWC.Algorithms.EarthModels.CompletelyFlatEarth;

import java.io.Serializable;


/**
 * represents a 3-point in space, using World/Earth coordinates.
 * Others classes also handle/maintain world coordinates.
 *
 * @see WorldArea
 * @see WorldVector
 */
public class WorldLocation implements Serializable, Cloneable
{

  ///////////////////////////////////////////////////////
  // members
  ///////////////////////////////////////////////////////

  /**
   * keep track of our earth model
   */
  static private MWC.Algorithms.EarthModel _model;

  // keep track of versions
  static final long serialVersionUID = 1;

  /**
   * depth in metres
   */
  double _theDepth;
  /**
   * Latitude in Degrees
   */
  double _theLat;
  /**
   * Longitude in Degrees
   */
  double _theLong;

  /**
   * our working version of world vector, to reduce object creation
   */
  private static WorldVector _myWorldVector = new WorldVector(0, 0, 0);

  ////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////
  /**
   * create a new location, in World coordinates
   *
   * @param LatVal   Latitude in degrees
   * @param LongVal  Longitude in degrees
   * @param DepthVal Depth in metres
   */
  public WorldLocation(double LatVal, double LongVal, double DepthVal)
  {
    _theLat = LatVal;
    _theLong = LongVal;
    _theDepth = DepthVal;
  }

  /**
   * copy constructor
   *
   * @param other WorldLocation to copy
   */
  public WorldLocation(WorldLocation other)
  {
    this(other._theLat, other._theLong, other._theDepth);
  }

  /**
   * long winded constructor, taking raw arguments
   */
  public WorldLocation(int latDegs,
                       int latMin,
                       double latSec,
                       char latHem,
                       int longDegs,
                       int longMin,
                       double longSec,
                       char longHem,
                       double theDepth)
  {
    this(latDegs, latMin + latSec / 60d, latHem,
         longDegs, longMin + longSec / 60d, longHem, theDepth);

  }

  /**
   * long winded constructor, taking raw arguments
   */
  public WorldLocation(int latDegs,
                       double latMin,
                       char latHem,
                       int longDegs,
                       double longMin,
                       char longHem,
                       double theDepth)
  {

    double latVal = latDegs + latMin / 60.0;
    if ((latHem == 'S') || (latHem == 's'))
    {
      latVal = -1.0 * latVal;
    }

    double longVal = longDegs + longMin / 60;
    if ((longHem == 'W') || (longHem == 'w'))
    {
      longVal = -1.0 * longVal;
    }

    // now set the values
    _theLat = latVal;
    _theLong = longVal;
    _theDepth = theDepth;

  }


  ///////////////////////////////////////////////////////
  // getter/setters
  //
  /**
   * @return the latitude in degrees
   */
  final public double getLat()
  {
    return _theLat;
  }

  /**
   * @return the longitude in degrees
   */
  final public double getLong()
  {
    return _theLong;
  }

  /**
   * @return the depth in metres
   */
  final public double getDepth()
  {
    return _theDepth;
  }

  /**
   * @param val - the latitude in degrees
   */
  final public void setLat(double val)
  {
    _theLat = val;
  }

  /**
   * @param val - the longitude in degrees
   */
  final public void setLong(double val)
  {
    _theLong = val;
  }

  /**
   * @param val - the depth in metres
   */
  final public void setDepth(double val)
  {
    _theDepth = val;
  }

  ///////////////////////////////////////////////////////
  // operations
  //////////////////////////////////////////////////////

  /**
   * check for valid depth
   */
  final public boolean hasValidDepth()
  {
    return !Double.isNaN(_theDepth);
  }


  final public WorldVector subtract(WorldLocation other)
  {
    WorldVector res = new WorldVector(0, 0, 0);
    return subtract(other, res);
  }

  /**
   * subtract the two points to produce a vector
   *
   * @param other the offset to add to this point
   * @return a new point
   */
  final public WorldVector subtract(WorldLocation other, WorldVector res)
  {
    // check we have our model
    if (_model == null)
      _model = new MWC.Algorithms.EarthModels.FlatEarth();


    res = _model.subtract(other, this, res);

    return res;
  }


  /**
   * subtract the two points to produce a vector
   *
   * @param other the offset to add to this point
   * @return a new point
   */
  final public WorldDistance rangeFrom(WorldLocation other, WorldDistance res)
  {
    // check we have our model
    if (_model == null)
      _model = new MWC.Algorithms.EarthModels.FlatEarth();

    // ok, how far apart are they?
    WorldVector sep = _model.subtract(other, this);
    
    // update the results object
    res.setValues(sep.getRange(), WorldDistance.DEGS);

    // and return it.
    return res;
  }
  
  /**
   * calculate the range from the other point (Degs)
   *
   * @return the range (in degrees)
   */
  final public double rangeFrom(WorldLocation other)
  {
    WorldVector res = subtract(other, _myWorldVector);

    return res.getRange();
  }

  /** calculate the range from the nearest point on the suppled line
   * @return the range
   */
  final public WorldDistance rangeFrom(WorldLocation lineStart, WorldLocation lineEnd)
  {
	  return perpendicularDistanceBetween(lineStart, lineEnd);
  }
  
  /**
   * calculate the bearing from the other point (rads)
   *
   * @return the bearing (rads)
   */
  final public double bearingFrom(WorldLocation other)
  {
    WorldVector res = subtract(other, _myWorldVector);

    return res.getBearing();
  }


  /**
   * note that ADD returns a new object, this
   * remains constant
   *
   * @param delta the offset to add to this point
   * @return a new point
   */
  final public WorldLocation add(WorldVector delta)
  {
    // check we have our model
    if (_model == null)
      _model = new MWC.Algorithms.EarthModels.FlatEarth();

    // do the calculation with our current model
    WorldLocation res = new WorldLocation(_model.add(this, delta));

    // and return the resutls
    return res;
  }

  /**
   * provide setter method to allow us to override the
   * earth model used for calculating separations
   * of locations
   */
  static final public void setModel(MWC.Algorithms.EarthModel theModel)
  {
    _model = theModel;
  }

  /**
   * note that addToMe changes this object
   *
   * @param delta the offset to add to this point
   */
  public void addToMe(WorldVector delta)
  {
    // check we have our model
    if (_model == null)
    {
      _model = new MWC.Algorithms.EarthModels.FlatEarth();
    }

    // do the calculation with our model
    WorldLocation res = _model.add(this, delta);

    // update ourselves to the result
    setLat(res.getLat());
    setLong(res.getLong());
    setDepth(res.getDepth());

  }

  public String toString()
  {
    String res = "";
    res += MWC.Utilities.TextFormatting.BriefFormatLocation.toString(this);
    res += " ";
    return res;
  }

  final public boolean equals(Object other)
  {
    boolean res = false;

    if (other instanceof WorldLocation)
    {
      res = this.equals((WorldLocation) other);
    }

    return res;
  }

  final public boolean equals(WorldLocation o)
  {

    boolean res = true;
    if (o._theDepth != _theDepth)
      res = false;
    if (o._theLat != _theLat)
      res = false;
    if (o._theLong != _theLong)
      res = false;

    return res;
  }

  // make this location a copy of the indicated one
  final public void copy(WorldLocation other)
  {
    _theLat = other._theLat;
    _theLong = other._theLong;
    _theDepth = other._theDepth;
  }

  public boolean isValid()
  {
    boolean res = true;

    // first check lat
    double lat = getLat();
    if ((lat > 90) || (lat < -90))
    {
      res = false;
    }
    else
    {
      // now check long
      double lon = getLong();
      if ((lon > 180) || (lon < -180))
      {
        res = false;
      }
      // ignore the depth
    }


    return res;
  }


  /** work out the perpendicular distance between me and the supplied line segment
   *
   * @param lineStart start point of the line
   * @param lineEnd end point of the line
   * @return perpendicular distance off track.
   */
  protected WorldDistance perpendicularDistanceBetween(WorldLocation lineStart, WorldLocation lineEnd)
  {
    // sort out known angles
    double thetaOne =lineEnd.bearingFrom(lineStart);
    double thetaTwo =  Math.PI - lineStart.bearingFrom(this);
    double thetaThree = thetaOne + thetaTwo;

    // and the single known distance
    double rangeToP1 = lineStart.rangeFrom(this);

    // now do our trig.
    double sinThetaThree = Math.abs(Math.sin(thetaThree));
    WorldDistance distance = new WorldDistance(rangeToP1 * sinThetaThree, WorldDistance.DEGS);

    // sorted.
    return distance;
  }



  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class LocationTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";
    private WorldLocation w1;
    private WorldLocation w2;
    private WorldVector wv1;
    private WorldLocation w4;
    private WorldLocation w5;

    public LocationTest(String val)
    {
      super(val);
    }

    public final void setUp()
    {
      // set the earth model we are expecting
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

      w1 = new WorldLocation(12.3, 12.4, 12.5);
      w2 = new WorldLocation(12.3, 12.4, 12.5);
      wv1 = new WorldVector(0, 1, 0);
      w4 = new WorldLocation(13.3, 12.4, 12.5);
      w5 = new WorldLocation(12.225, 12.275, 12.5);
    }

    public final void tearDown()
    {
      w1 = null;
      w2 = null;
      wv1 = null;
      w4 = null;
      w5 = null;
    }

    public final void testPerpDistanceFrom()
    {
      WorldLocation.setModel(new CompletelyFlatEarth());

      WorldLocation me = new WorldLocation(7, 0, 0);
      WorldLocation p1 = new WorldLocation(4, 4 ,0);
      WorldLocation p2 = new WorldLocation(12,4,0);

      WorldDistance res = me.perpendicularDistanceBetween(p1, p2);
      assertEquals("off-track error is correct", 4.0, res.getValueIn(WorldDistance.DEGS), 0.001);

      p2 = new WorldLocation(9, 2, 0);
      res = me.perpendicularDistanceBetween(p1, p2);
      assertEquals("off-track error is correct", 2.5997, res.getValueIn(WorldDistance.DEGS), 0.001);

      p2 = new WorldLocation(-4, -4, 0);
      res = me.perpendicularDistanceBetween(p1, p2);
      assertEquals("off-track error is correct", 4.9497, res.getValueIn(WorldDistance.DEGS), 0.001);

      res = me.rangeFrom(p1, p2);
      assertEquals("off-track error is correct (using range from operator)", 4.9497, res.getValueIn(WorldDistance.DEGS), 0.001);
      
    }


    public final void testConstructor()
    {
      WorldLocation v1 = new WorldLocation(12.225, 12.275, 12.5);
      WorldLocation v2 = new WorldLocation(12, 13.5, 'N', 12, 16.5, 'E', 12.5);
      WorldLocation v3 = new WorldLocation(12, 13, 30, 'N', 12, 16, 30, 'E', 12.5);
      WorldLocation v4 = new WorldLocation(w5);

      assertTrue("v1 (d,d,d)", v1.equals(w5));
      assertTrue("v2 (i,d,c,i,d,c,d)", v2.equals(w5));
      assertTrue("v3 (i,i,d,c,i,i,d,c,d)", v3.equals(w5));
      assertTrue("v4 (WorldLoc)", v4.equals(w5));
    }

    public final void testEquals()
    {
      assertTrue(w1.equals(w2));
      assertTrue(!w1.equals(w5));
    }

    public final void testCopy()
    {
      WorldLocation w3 = new WorldLocation(0, 0, 0);
      w3.copy(w2);
      assertTrue(w2.equals(w3));
    }

    public final void testAdd()
    {
      WorldLocation ww = w1.add(wv1);
      assertTrue(w4.equals(ww));
    }

    public final void testAddToMe()
    {
      w1.addToMe(wv1);
      assertTrue(w1.equals(w4));
    }

    public final void testBearingFrom()
    {
      double brg = w4.bearingFrom(w1);
      assertTrue(brg == 0d);
    }

    public final void testGetDepth()
    {
      assertEquals(w1.getDepth(), 12.5, 0d);
    }

    public final void testGetLat()
    {
      assertEquals(w1.getLat(), 12.3, 0d);
    }

    public final void testGetLong()
    {
      assertEquals(w1.getLong(), 12.4, 0d);
    }

    public final void testRangeFrom()
    {
      double rng = w4.rangeFrom(w1);
      assertEquals(rng, 1.0, 0.0001d);
    }

    public final void setXXX()
    {
      double dep = w4.getDepth();
      double dLat = w4.getLat();
      double dLong = w4.getLong();
      w1.setDepth(dep);
      w1.setLat(dLat);
      w1.setLong(dLong);
      assertTrue(w1.equals(w4));
    }

    public final void setSubtract()
    {
      WorldVector wvv = w4.subtract(w1);
      assertTrue(wvv.equals(wv1));
    }


  }


  /**
   * *******************************************************************
   * convenience class which allows a WorldLocation to be created using
   * a world distance instead of a double for depth/heught
   * *******************************************************************
   */
  public static class LocalLocation extends WorldLocation
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
     * @param lat     the latitude
     * @param longVal the longitude
     * @param height  the height
     */
    public LocalLocation(double lat, double longVal, WorldDistance height)
    {
      super(lat, longVal, -height.getValueIn(WorldDistance.METRES));
    }

    /**
     * @param north  - how far north we are
     * @param east   - how far east we are
     * @param height the height
     */
    public LocalLocation(WorldDistance north, WorldDistance east, WorldDistance height)
    {
      super(north.getValueIn(WorldDistance.DEGS),
          east.getValueIn(WorldDistance.DEGS), -
          height.getValueIn(WorldDistance.METRES));
    }

    /**
     * @param north  - how far north we are
     * @param east   - how far east we are
     * @param height the height
     */
    public LocalLocation(WorldDistance north, WorldDistance east, double height)
    {
      super(north.getValueIn(WorldDistance.DEGS),
          east.getValueIn(WorldDistance.DEGS), -
          height);
    }

    /**
     * @param lat     the latitude
     * @param longVal the longitude
     */
    public LocalLocation(double lat, double longVal)
    {
      super(lat, longVal, 0);
    }

    /**
     * long winded constructor, taking raw arguments
     */
    public LocalLocation(int latDegs, int latMin, double latSec, char latHem, int longDegs, int longMin, double longSec, char longHem, WorldDistance height)
    {
      super(latDegs, latMin, latSec, latHem, longDegs, longMin, longSec, longHem, -height.getValueIn(WorldDistance.METRES));
    }

    /**
     * long winded constructor, taking raw arguments
     */
    public LocalLocation(int latDegs, int latMin, double latSec, char latHem, int longDegs, int longMin, double longSec, char longHem)
    {
      super(latDegs, latMin, latSec, latHem, longDegs, longMin, longSec, longHem, 0);
    }
  }

  public static void main(String[] args)
  {
    LocationTest lt = new LocationTest("here");
    lt.testPerpDistanceFrom();
  }


}




