package org.mwc.debrief.scripting.wrappers;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class Tactical
{
  public static FixWrapper createFix(final HiResDate time,
      final WorldLocation location, final double courseRads,
      final double speedYps)
  {
    final Fix fix = new Fix(time, location, courseRads, speedYps);
    return new FixWrapper(fix);
  }

  public static SensorWrapper createSensor(final String name)
  {
    final SensorWrapper sensor = new SensorWrapper(name);
    return sensor;
  }

  public static SensorContactWrapper createSensorContact(final HiResDate date)
  {
    final SensorContactWrapper res = new SensorContactWrapper();
    res.setDTG(date);
    return res;
  }

  public static TrackWrapper createTrack(final String name)
  {
    final TrackWrapper res = new TrackWrapper();
    res.setName(name);
    return res;
  }
}
