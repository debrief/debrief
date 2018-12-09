package org.mwc.debrief.scripting.wrappers;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

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

  /**
   * Creates a Narrative Wrapper
   * 
   * @param title
   *          Title of the narrative.
   * @return Narrative Wrapper Object.
   */
  public static NarrativeWrapper createNarrative(final String title)
  {
    return new NarrativeWrapper(title);
  }

  public static NarrativeEntry createNarrativeEntry(final String track,
      final String type, final HiResDate DTG, final String entry)
  {
    return new NarrativeEntry(track, type, DTG, entry);
  }
}
