package org.mwc.debrief.scripting.wrappers;

import org.eclipse.ease.modules.WrapToScript;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class Spatial
{

  /**
   * the units for distances. Note to provide these to ease, we have to add
   * a @WrapToScript annotation.  Once we've added this annotation to one public
   * field/method, we have to apply it for all methods
   */
  @WrapToScript
  static public final int METRES = 0;
  @WrapToScript
  static public final int YARDS = 1;
  @WrapToScript
  static public final int KM = 2;
  @WrapToScript
  static public final int NM = 3;
  @WrapToScript
  static public final int MINUTES = 4;
  @WrapToScript
  static public final int DEGS = 5;
  @WrapToScript
  static public final int KYDS = 6;
  @WrapToScript
  static public final int FT = 7;
  
  @WrapToScript
  public static WorldLocation createLocation(double dLat, double dLong,
      double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }

  @WrapToScript
  public WorldDistance createDistance(double value, int units)
  {
    return new WorldDistance(value, units);
  }

  @WrapToScript
  public static WorldVector createVector(double distM, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distM, WorldDistance.METRES), null);
  }

  @WrapToScript
  public static WorldVector createVectorKm(double distKm, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distKm, WorldDistance.KM), null);
  }

}
