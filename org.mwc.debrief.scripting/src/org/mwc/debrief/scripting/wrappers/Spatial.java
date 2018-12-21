package org.mwc.debrief.scripting.wrappers;

import org.eclipse.ease.modules.WrapToScript;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import junit.framework.TestCase;

/** generic position related elements
 * 
 * @author ian
 *
 */
public class Spatial
{

  public static class TestSpatial extends TestCase
  {

    static final double testDouble = 4.5;

    static final double testDouble2 = 7.3;

    static final double testDouble3 = 2.0;

    static final int testInt = WorldSpeed.Kts;

    static final WorldLocation w1 = new WorldLocation(12, 12, 0);

    static final WorldLocation w2 = new WorldLocation(10, 10, 100);
    static final WorldArea wa1 = new WorldArea(w1, w2);
    
    public void testCreateArea()
    {
      final WorldArea ww1 = new WorldArea(w1, w2);
      final WorldArea ww2 = new WorldArea(wa1);
      assertEquals("constructor worked", ww1.equals(wa1), true);
      assertEquals("constructor worked", ww2.equals(wa1), true);
    }

    public void testCreateDistance()
    {
      final WorldDistance distance = createDistance(testDouble, testInt);
      assertEquals("Same units for WorldDistance", testInt, distance
          .getUnits());
      assertEquals("Same value for WorldDistance", testDouble, distance
          .getValueIn(testInt), 1e-5);
    }

    public void testCreateLocation()
    {
      final WorldLocation location = createLocation(testDouble, testDouble2,
          testDouble3);
      assertEquals("Same value for lattitude", testDouble, location.getLat(),
          1e-5);
      assertEquals("Same value for longitud", testDouble2, location.getLong(),
          1e-5);
      assertEquals("Same value for depth", testDouble3, location.getDepth(),
          1e-5);
    }

    public void testCreateVector()
    {
      // TODO
    }

    public void testCreateVectorKm()
    {
      /*
       * WorldVector vectorKM = createVectorKm(testDouble, testDouble2);
       * assertEquals("Same value for vectorKM", testDouble, vectorKM.getRange(), 1e-5);
       * assertEquals("Same value for vectorKM", testDouble2, vectorKM .getBearing(), 1e-5);
       */
      // TODO
    }

    public void testCreateWorldSpeed()
    {
      final WorldSpeed worldSpeed = createWorldSpeed(testDouble, testInt);
      assertEquals("Same units for worldSpeed", testInt, worldSpeed.getUnits());
      assertEquals("Same value for worldSpeed", testDouble, worldSpeed
          .getValueIn(testInt), 1e-5);
    }
  }

  /**
   * the units for distances. Note to provide these to ease, we have to add a @WrapToScript
   * annotation. Once we've added this annotation to one public field/method, we have to apply it
   * for all methods
   */
  /** Metres type
   * 
   */
  @WrapToScript
  static public final int METRES = 0;
  /** Yards type
   * 
   */
  @WrapToScript
  static public final int YARDS = 1;
  /** Kilometres type
   * 
   */
  @WrapToScript
  static public final int KM = 2;
  /** Nautical Miles type
   * 
   */
  @WrapToScript
  static public final int NM = 3;
  /** Arc Minutes type
   * 
   */
  @WrapToScript
  static public final int MINUTES = 4;
  /** Arc Degrees type
   * 
   */
  @WrapToScript
  static public final int DEGS = 5;
  /** Kiloyards type
   * 
   */
  @WrapToScript
  static public final int KYDS = 6;

  /** Feet type
   * 
   */
  @WrapToScript
  static public final int FT = 7;
  /**
   * Units for world speed.
   */
  /**
   * Metres/sec for speed object.
   */
  @WrapToScript
  static public final int M_SEC = 0;
  /**
   * Knots for speed object.
   */
  @WrapToScript
  static public final int KTS = 1;
  /**
   * Feet/sec for speed object.
   */
  @WrapToScript
  static public final int FT_SEC = 2;

  /**
   * Feet/Min for speed object.
   */
  @WrapToScript
  static public final int FT_MIN = 3;

  /**
   * produce an area object using the two corners
   *
   * @param tl
   *          top-left corner
   * @param br
   *          bottom-right corner
   * @return MWC.GenericData.WorldArea
   * 		<br />
   * 		// @type MWC.GenericData.WorldArea
   * 
   */
  @WrapToScript
  public static WorldArea createArea(final WorldLocation tl,
      final WorldLocation br)
  {
    return new WorldArea(tl, br);
  }

  /** create a distance object
   * 
   * @param value how many units of distance
   * @param units the units for the measurement
   * @return a distance object
   * 		<br />
   * 		// @type MWC.GenericData.WorldDistance
   * 
   */
  @WrapToScript
  public static WorldDistance createDistance(final double value,
      final int units)
  {
    return new WorldDistance(value, units);
  }

  /** create a location
   * 
   * @param dLat degrees of latitude
   * @param dLong degrees of longitude
   * @param depth depth in metres
   * @return a location object
   * 		<br />
   * 		// @type MWC.GenericData.WorldLocation
   * 
   */
  @WrapToScript
  public static WorldLocation createLocation(final double dLat,
      final double dLong, final double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }

  /** create a measured vector
   * 
   * @param distM distance in metres
   * @param bearingDegs bearing in degrees
   * @return a vector object
   * 		<br />
   * 		// @type MWC.GenericData.WorldVector
   * 
   */
  @WrapToScript
  public static WorldVector createVector(final double distM,
      final double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distM, WorldDistance.METRES), null);
  }

  /** create a vector object using km
   * 
   * @param distKm distance in Km
   * @param bearingDegs bearing in degrees
   * @return a vector object
   * 		<br />
   * 		// @type MWC.GenericData.WorldVector
   * 
   */
  @WrapToScript
  public static WorldVector createVectorKm(final double distKm,
      final double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distKm, WorldDistance.KM), null);
  }

  /** create a speed object
   * 
   * @param value how many units of speed to use
   * @param units the units for this measurement
   * @return a speed object
   * 		<br />
   * 		// @type MWC.GenericData.WorldSpeed
   * 
   */
  @WrapToScript
  public static WorldSpeed createWorldSpeed(final double value, final int units)
  {
    return new WorldSpeed(value, units);
  }
}
