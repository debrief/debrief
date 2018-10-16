package org.mwc.debrief.scripting.wrappers;

import Debrief.Wrappers.FixWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class DebriefObjects
{
  public static WorldLocation createLocation(double dLat, double dLong,
      double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }

  public static FixWrapper createFix(HiResDate time, WorldLocation location,
      double courseRads, double speedYps)
  {
    Fix fix = new Fix(time, location, courseRads, speedYps);
    return new FixWrapper(fix);
  }

  public static HiResDate createDate(long date)
  {
    return new HiResDate(date);
  }
}
