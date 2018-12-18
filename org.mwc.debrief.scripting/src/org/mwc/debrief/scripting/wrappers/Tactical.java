package org.mwc.debrief.scripting.wrappers;

import org.eclipse.ease.modules.WrapToScript;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

/** entities related to measured vessel data
 * 
 * @author ian
 *
 */
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
    static final double speedMs = 3.6;
    static final double speedYps = new WorldSpeed(speedMs,
        WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec) /3 ;
    static final double courseDegs = 1.3;

    public void testCreateFix()
    {
      final FixWrapper fix = createFix(time, location, courseDegs, speedMs);
      assertEquals("Same time for Fix", time, fix.getTime());
      assertEquals("Same location for Fix", location, fix.getLocation());
      assertEquals("Same course for Fix", courseDegs, fix.getCourseDegs(),
          1e-5);
      assertEquals("Same speedYps for Fix", speedYps, fix.getFix().getSpeed(),
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
   * @param courseDegs Course in degrees of the new fix
   * @param speedMs Speed in metres per second.
   * @return New fix object.
   */
  @WrapToScript
  public static FixWrapper createFix(final HiResDate time,
      final WorldLocation location, final double courseDegs,
      final double speedMs)
  {
    final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
    final double speedYps = new WorldSpeed(speedMs, WorldSpeed.M_sec)
        .getValueIn(WorldSpeed.ft_sec) / 3d;
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
  @WrapToScript
  public static NarrativeWrapper createNarrative(final String title)
  {
    return new NarrativeWrapper(title);
  }

  /** Create a new narrative entry.
   * 
   * @param track the track this is a child of
   * @param type the type of entry
   * @param DTG when this was recorded
   * @param entry the textual entry
   * @return a new narrative entry
   */
  @WrapToScript
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
  @WrapToScript
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
  @WrapToScript
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
  @WrapToScript
  public static TrackWrapper createTrack(final String name)
  {
    final TrackWrapper res = new TrackWrapper();
    res.setName(name);
    return res;
  }
}