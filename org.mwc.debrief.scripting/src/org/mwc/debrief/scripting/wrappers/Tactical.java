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
  public static TrackWrapper createTrack(String name)
  {
    TrackWrapper res = new TrackWrapper();
    res.setName(name);
    return res;
  }
  
  public static SensorWrapper createSensor(String name)
  {
    SensorWrapper sensor = new SensorWrapper(name);
    return sensor;
  }
  
  public static SensorContactWrapper createSensorContact(HiResDate date)
  {
    SensorContactWrapper res = new SensorContactWrapper();
    res.setDTG(date);
    return res;
  }

  public static FixWrapper createFix(HiResDate time, WorldLocation location,
      double courseRads, double speedYps)
  {
    Fix fix = new Fix(time, location, courseRads, speedYps);
    return new FixWrapper(fix);
  }
}
