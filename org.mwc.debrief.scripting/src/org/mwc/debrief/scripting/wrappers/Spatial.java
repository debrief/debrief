package org.mwc.debrief.scripting.wrappers;

import org.eclipse.ease.modules.WrapToScript;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class Spatial
{

  /**
   * the units for distances. Note to provide these to ease, we have to add a @WrapToScript
   * annotation. Once we've added this annotation to one public field/method, we have to apply it
   * for all methods
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
  
  /**
   * Units for world speed.
   */
  @WrapToScript
  static public final int M_SEC = 0;
  @WrapToScript
  static public final int KTS = 1;
  @WrapToScript
  static public final int FT_SEC = 2;
  @WrapToScript
  static public final int FT_MIN = 3;

  @WrapToScript
  public static WorldLocation createLocation(final double dLat,
      final double dLong, final double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }

  @WrapToScript
  /** produce an area object using the two corners
   * 
   * @param tl top-left corner
   * @param br bottom-right corner
   * @return MWC.GenericData.WorldArea
   */
  public static WorldArea createArea(final WorldLocation tl, 
      final WorldLocation br)
  {
    return new WorldArea(tl, br);
  }
  
  @WrapToScript
  public static WorldVector createVector(final double distM,
      final double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distM, WorldDistance.METRES), null);
  }

  @WrapToScript
  public static WorldVector createVectorKm(final double distKm,
      final double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distKm, WorldDistance.KM), null);
  }

  @WrapToScript
  public WorldDistance createDistance(final double value, final int units)
  {
    return new WorldDistance(value, units);
  }

  @WrapToScript
  public WorldSpeed createWorldSpeed(final double value, final int units)
  {
    return new WorldSpeed(value, units);
  }
}
