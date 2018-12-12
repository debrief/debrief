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
import junit.framework.TestCase;

public class Tactical
{
  public static class TaticalTest extends TestCase
  {
    static final String trackName = "trackName";
    static final String type = "type";
    static final HiResDate dtg = new HiResDate(2000000);
    static final String entryString = "entry";
    static final String narrativeTitle = "narrativeTitle";
    static final String sensorName = "sensorName";
    static final HiResDate time = new HiResDate(2500000);
    static final WorldLocation location = new WorldLocation(10, 23.5, 1.5);
    static final double speedYps = 3.6;
    static final double courseRads = 1.3;

    public void testCreateFix()
    {
      final FixWrapper fix = createFix(time, location, courseRads, speedYps);
      assertEquals("Same time for Fix", time, fix.getTime());
      assertEquals("Same location for Fix", location, fix.getLocation());
      assertEquals("Same speedYps for Fix", speedYps, fix.getFix().getSpeed(),
          1e-5);
      assertEquals("Same courseRads for Fix", courseRads, fix.getCourse(),
          1e-5);
    }

    public void testCreateNarrative()
    {
      final NarrativeEntry entry = createNarrativeEntry(trackName, type, dtg,
          entryString);
      assertEquals("Same value for track name", trackName, entry
          .getTrackName());
      assertEquals("Same value for type", type, entry.getType());
      assertEquals("Same value for DTG", dtg, entry.getDTG());
      assertEquals("Same value for entry", entryString, entry.getEntry());

      final NarrativeWrapper narrativeWrapper = createNarrative(narrativeTitle);
      narrativeWrapper.add(entry);

      assertEquals("Same name for Narrative Wrapper", narrativeTitle,
          narrativeWrapper.getName());
      assertEquals("Entry is contained in narrative wrapper", entry,
          narrativeWrapper.elements().nextElement());
    }

    public void testCreateNarrativeEntry()
    {
      final NarrativeEntry entry = createNarrativeEntry(trackName, type, dtg,
          entryString);
      assertEquals("Same value for track name", trackName, entry
          .getTrackName());
      assertEquals("Same value for type", type, entry.getType());
      assertEquals("Same value for DTG", dtg, entry.getDTG());
      assertEquals("Same value for entry", entryString, entry.getEntry());
    }

    public void testCreateSensor()
    {
      final SensorWrapper sensor = createSensor(sensorName);
      assertEquals("Same name for Sensor", sensorName, sensor.getName());
    }

    public void testCreateTrack()
    {
      final TrackWrapper trackWrapper = createTrack(trackName);
      assertEquals("Same name for Track Wrapper", trackName, trackWrapper
          .getName());
    }
  }

  /**
   * Create a fix given the time, location, course in radians and speed in yards per seconds.
   * @param time Time of the new fix
   * @param location Location of the new fix
   * @param courseRads Course in radians of the new fix
   * @param speedYps Speed in yards per second.
   * @return New fix object.
   */
  public static FixWrapper createFix(final HiResDate time,
      final WorldLocation location, final double courseRads,
      final double speedYps)
  {
    final Fix fix = new Fix(time, location, courseRads, speedYps);
    return new FixWrapper(fix);
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

  /**
   * 
   * @param track
   * @param type
   * @param DTG
   * @param entry
   * @return
   */
  public static NarrativeEntry createNarrativeEntry(final String track,
      final String type, final HiResDate DTG, final String entry)
  {
    return new NarrativeEntry(track, type, DTG, entry);
  }

  /**
   * Create a new sensor with the given name.
   * @param name Name of the new Sensor
   * @return New sensor object with the name given.
   */
  public static SensorWrapper createSensor(final String name)
  {
    final SensorWrapper sensor = new SensorWrapper(name);
    return sensor;
  }

  /**
   * Creates a new sensor with the date given as DTG.
   * 
   * @see Debrief.Wrappers.SensorContactWrapper.SensorContactWrapper()
   * @param date Date of the new sensor to be created.
   * @return New sensor object.
   */
  public static SensorContactWrapper createSensorContact(final HiResDate date)
  {
    final SensorContactWrapper res = new SensorContactWrapper();
    res.setDTG(date);
    return res;
  }

  /**
   * Creates a track with the given name.
   * 
   * @see Debrief.Wrappers.TrackWrapper
   * @param name
   *          Name of the new track created.
   * @return New Track created.
   */
  public static TrackWrapper createTrack(final String name)
  {
    final TrackWrapper res = new TrackWrapper();
    res.setName(name);
    return res;
  }
}