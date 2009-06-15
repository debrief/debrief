// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WorldVector.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: WorldVector.java,v $
// Revision 1.7  2006/05/02 13:43:50  Ian.Mayo
// Correct comment typo, add 'inverse' generator
//
// Revision 1.6  2004/10/28 12:52:18  ian
// Allow use of null object for depth in constructor
//
// Revision 1.5  2004/08/31 15:28:19  Ian.Mayo
// Polish off test refactoring, start Intercept behaviour
//
// Revision 1.4  2004/08/31 09:38:32  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.3  2004/07/08 15:48:38  Ian.Mayo
// Tidy toString operation, accept inspector bits
//
// Revision 1.2  2004/05/24 16:27:38  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:01  Ian.Mayo
// Initial import
//
// Revision 1.4  2002-10-28 09:25:11+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.3  2002-10-11 08:34:12+01  ian_mayo
// IntelliJ optimisations
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
// Revision 1.2  2001-10-22 14:50:50+01  administrator
// Setup the tests correctly (specifying the correct earth model)
//
// Revision 1.1  2001-08-21 12:11:21+01  administrator
// put units in parameter names
//
// Revision 1.0  2001-07-17 08:46:40+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-21 21:39:51+00  novatech
// add JUnit testing
//
// Revision 1.2  2001-01-18 13:21:29+00  novatech
// optimisations to reduce object creation
//
// Revision 1.1  2001-01-03 13:43:11+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:40  ianmayo
// initial version
//
// Revision 1.3  2000-04-19 11:40:00+01  ian_mayo
// make methods final
//
// Revision 1.2  2000-02-02 14:24:06+00  ian_mayo
// made serializable
//
// Revision 1.1  1999-10-12 15:37:14+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:32+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:53+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-23 14:03:53+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.1  1999-07-07 11:10:11+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:02+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:24+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:32:59+00  sm11td
// Initial revision
//

package MWC.GenericData;

import java.io.Serializable;

import junit.framework.TestCase;
import MWC.Algorithms.Conversions;
import MWC.Algorithms.EarthModels.FlatEarth;

/**
 * class which represents a vector offset in 3 dimensions
 */
public final class WorldVector implements Serializable, Cloneable
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  // keep track of versions
  static final long serialVersionUID = 1L;

  private double _brg;
  private double _rng;
  private double _depth;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /**
   * constructor for a vector (separation between 2 points)
   *
   * @param brgRads    the bearing in radians
   * @param rngDegs    the range in degrees
   * @param dpthMetres the depth in metres
   */
  public WorldVector(final double brgRads,
                     final double rngDegs,
                     final double dpthMetres)
  {
    _brg = brgRads;
    _rng = rngDegs;
    _depth = dpthMetres;
  }

  /**
   * convenience constructor, taking higher level constructrs
   *
   * @param brgRads
   * @param dist
   * @param depth
   */
  public WorldVector(final double brgRads,
                     final WorldDistance dist,
                     final WorldDistance depth)
  {
    this(brgRads, dist.getValueIn(WorldDistance.DEGS), 0);

    if (depth != null)
      _depth = depth.getValueIn(WorldDistance.DEGS);
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** provide the reverse of the current vector
   * 
   */
  public WorldVector generateInverse()
  {
  	return new WorldVector(_brg + Math.PI, _rng, _depth);
  }
  
  /**
   * set the internal values (hopefully this is a substitute for repeated creations of world vectors
   *
   * @param theBearing the bearing in radians
   * @param theRange   the range in degrees
   * @param theDepth   the depth in metres
   */
  final public void setValues(final double theBearing, final double theRange, final double theDepth)
  {
    _brg = theBearing;
    _rng = theRange;
    _depth = theDepth;
  }

  /**
   * get the bearing (rads)
   *
   * @return the bearing in radians
   */
  final public double getBearing()
  {
    return _brg;
  }

  /**
   * get the range (degs)
   *
   * @return the range in degrees
   */
  final public double getRange()
  {
    return _rng;
  }

  /**
   * get the depth separation
   *
   * @return the depth in metres
   */
  final public double getDepth()
  {
    return _depth;
  }

  final public String toString()
  {
    String res;
    res = "MWC.GenericData.WorldVector[brg:" + Conversions.Rads2Degs(_brg);
    res = res + " degs, rng:" + Conversions.Degs2Nm(_rng) + " nm";
    return res;
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class VectorTest extends TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";
    private WorldVector wv1 = null;

    public VectorTest(final String val)
    {
      super(val);
    }

    public final void setUp()
    {
      // set the earth model we are expecting
      WorldLocation.setModel(new FlatEarth());
      wv1 = new WorldVector(4.3, 12.0, 45.0);
    }

    public final void tearDown()
    {
      wv1 = null;
    }

    public final void testConstructor()
    {
      assertEquals("check bearing rads", wv1.getBearing(), 4.3, 0.00001);
      assertEquals("check range degs", wv1.getRange(), 12.0, 0.0001);
      assertEquals("check depth m", wv1.getDepth(), 45.0, 0.0001);
    }

    public final void testSetter()
    {
      wv1.setValues(33.0, 44.0, 55.0);
      assertEquals("check bearing rads", wv1.getBearing(), 33.0, 0.00001);
      assertEquals("check range degs", wv1.getRange(), 44.0, 0.0001);
      assertEquals("check depth m", wv1.getDepth(), 55.0, 0.0001);
    }
  }
}
