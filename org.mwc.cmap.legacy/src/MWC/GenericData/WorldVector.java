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
package MWC.GenericData;

import java.io.Serializable;

import MWC.Algorithms.Conversions;
import MWC.Algorithms.EarthModels.FlatEarth;
import junit.framework.TestCase;

/**
 * class which represents a vector offset in 3 dimensions
 */
public final class WorldVector implements Serializable, Cloneable
{

  static public final class VectorTest extends TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";
    private WorldVector wv1 = null;

    public VectorTest(final String val)
    {
      super(val);
    }

    @Override
    public final void setUp()
    {
      // set the earth model we are expecting
      WorldLocation.setModel(new FlatEarth());
      wv1 = new WorldVector(4.3, 12.0, 45.0);
    }

    @Override
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

  // keep track of versions
  static final long serialVersionUID = 1L;
  private double _brg; // rads
  private double _rng; // degs
  private double _depth; // metres
  
  /**
   * constructor for a vector (separation between 2 points)
   *
   * @param brgRads
   *          the bearing in radians
   * @param rngDegs
   *          the range in degrees
   * @param dpthMetres
   *          the depth in metres
   */
  public WorldVector(final double brgRads, final double rngDegs,
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
  public WorldVector(final double brgRads, final WorldDistance dist,
      final WorldDistance depth)
  {
    this(brgRads, dist.getValueIn(WorldDistance.DEGS), 0);

    if (depth != null)
      _depth = depth.getValueIn(WorldDistance.METRES);
  }

  /**
   * copy constructor
   *
   * @param other
   *          world vector to copy
   */
  public WorldVector(final WorldVector other)
  {
    this(other.getBearing(), other.getRange(), other.getDepth());
  }

  /**
   * provide the reverse of the current vector
   *
   */
  public WorldVector generateInverse()
  {
    return new WorldVector(_brg + Math.PI, _rng, _depth);
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
   * get the depth separation
   *
   * @return the depth in metres
   */
  final public double getDepth()
  {
    return _depth;
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
   * set the internal values (hopefully this is a substitute for repeated creations of world vectors
   *
   * @param theBearing
   *          the bearing in radians
   * @param theRange
   *          the range in degrees
   * @param theDepth
   *          the depth in metres
   */
  final public void setValues(final double theBearing, final double theRange,
      final double theDepth)
  {
    _brg = theBearing;
    _rng = theRange;
    _depth = theDepth;
  }

  @Override
  final public String toString()
  {
    String res;
    res = ":" + ((int) Conversions.Rads2Degs(_brg));
    res = res + "\u00b0 " + ((int) Conversions.Degs2m(_rng)) + " m";
    return res;
  }
}
