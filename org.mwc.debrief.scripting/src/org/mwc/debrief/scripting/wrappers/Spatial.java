package org.mwc.debrief.scripting.wrappers;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class Spatial
{

  public static WorldLocation createLocation(double dLat, double dLong,
      double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }

  public WorldDistance createDistance(double km)
  {
    return new WorldDistance(km, WorldDistance.KM);
  }

  public static WorldVector createVector(double distM, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distM, WorldDistance.METRES), null);
  }

  public static WorldVector createVectorKm(double distKm, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distKm, WorldDistance.KM), null);
  }

}
