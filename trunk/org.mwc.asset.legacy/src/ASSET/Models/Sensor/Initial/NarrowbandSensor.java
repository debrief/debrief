package ASSET.Models.Sensor.Initial;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.NarrowbandRadNoise;
import ASSET.Models.Vessels.SSN;
import ASSET.Models.Vessels.Surface;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.ScenarioType;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class NarrowbandSensor extends InitialSensor
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * time it takes this sensor to steady
   */
  private Duration _mySteadyTime;

  /**
   * the course we were on in the last step
   */
  private double _oldCourse = INVALID_COURSE;

  /**
   * the time at which our array should be steady.
   */
  protected long _timeArraySteady;

  /**
   * constant used to indicate that course has not yet been assigned
   */
  private static final double INVALID_COURSE = -999d;

  /**
   * string used to indicate that array is steady
   */
  private static final String ARRAY_STEADY = "Array Steady";

  /**
   * string marker used when firing reports to indicate that the array is unsteady
   */
  private static final String ARRAY_UNSTEADY = "Array unsteady";

  ///////////////////////////////////
  // member variables
  //////////////////////////////////


  /**
   * *************************************************
   * constructor
   * *************************************************
   */
  public NarrowbandSensor(final int id)
  {
    super(id, "NB");
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * find out which medium this sensor operates under
   *
   * @return
   */
  public int getMedium()
  {
    return EnvironmentType.NARROWBAND;
  }

  /**
   * find out how long it takes this sensor to steady
   *
   * @return
   */
  public Duration getSteadyTime()
  {
    return _mySteadyTime;
  }

  /**
   * set how long it takes this sensor to steady
   *
   * @param val
   */
  public void setSteadyTime(final Duration val)
  {
    _mySteadyTime = val;
  }


  //////////////////////////////////////////////////
  // general sensor-type stuff
  //////////////////////////////////////////////////

  /**
   * restart this sensors
   */
  public void restart()
  {
    super.restart();

    // and clear the time array expected to steady
    _timeArraySteady = -01;

    // and forget the old course
    _oldCourse = INVALID_COURSE;
  }


  // what is the detection strength for this target?
  protected DetectionEvent detectThis(EnvironmentType environment, ParticipantType host, ParticipantType target,
                                      long time, ScenarioType scenario)
  {
    DetectionEvent res = null;
    String msg = null;

    // ok, before we let our parent do the real calculation, handle the array unsteady bits
    Status curStatus = host.getStatus();
    double course = curStatus.getCourse();

    // is this the first time we've examined the course?
    if (_oldCourse == INVALID_COURSE)
    {
      // yes, just initialise it
      _oldCourse = course;
    }

    // right see if we are currently changing course?
    if (_oldCourse != course)
    {
      // yes, update our calculated time when the array will be steady
      _timeArraySteady = time + getSteadyTime().getMillis();
    }

    // have we passed the steady time?
    if (time >= _timeArraySteady)
    {

      msg = ARRAY_STEADY;

      // yup, do our calc
      res = super.detectThis(environment, host, target, time, scenario);
    }
    else
    {
      // no, still waiting to steady.  return null...
      msg = ARRAY_UNSTEADY;
    }

    if (msg != null)
    {
      if (_myEditor != null)
      {
        if (_myEditor.hasReportListeners())
          _myEditor.fireReport(this, msg);
      }
    }

    // and remember the old course
    _oldCourse = course;

    return res;
  }

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new NarrowbandInfo(this);

    return _myEditor;
  }

  /**
   * Whether this sensor cannot be used to positively identify a target
   */
  public boolean canIdentifyTarget()
  {
    return false;
  }

  // allow an 'overview' test, just to check if it is worth all of the above processing
  protected boolean canDetectThisType(NetworkParticipant ownship,
                                      final ASSET.ParticipantType other, EnvironmentType env)
  {
    return other.radiatesThisNoise(getMedium());
  }


  protected double getDI(final double courseDegs,
                         final double absBearingDegs)
  {
    //  double res = 74;
    double res = 34; // URICK page 43, for cylindrical sensor 60 inches diameter @ 15Khz

    return res;
  }

  protected double getLoss(EnvironmentType environment,
                           final WorldLocation target,
                           final WorldLocation host)
  {
    double res = 0;

    // use the environment to determine the loss
    res = environment.getLossBetween(getMedium(), target, host);

    return res;
  }

  protected double getOSNoise(final ASSET.ParticipantType ownship, final double absBearingDegs)
  {
    return ownship.getSelfNoiseFor(getMedium(), absBearingDegs);
  }

  /**
   * the estimated range for a detection of this type (where applicable)
   */
  public WorldDistance getEstimatedRange()
  {
    return new WorldDistance(1000, WorldDistance.YARDS);
  }

  protected double getRD(NetworkParticipant host, NetworkParticipant target)
  {
    return 2;
  }

  protected double getTgtNoise(final ASSET.ParticipantType target, final double absBearingDegs)
  {
    return target.getRadiatedNoiseFor(getMedium(), absBearingDegs);
  }

  protected double getBkgndNoise(EnvironmentType environment,
                                 WorldLocation host, double absBearingDegs)
  {

    double res;

    // use the environment to determine the loss
    res = environment.getBkgndNoise(EnvironmentType.BROADBAND_PASSIVE, host, absBearingDegs);

    return res;
  }

  /**
   * does this sensor return the course of the target?
   */
  public boolean hasTgtCourse()
  {
    return true;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: NarrowbandSensor.java,v $
   * Revision 1.3  2006/09/21 12:20:42  Ian.Mayo
   * Reflect introduction of default names
   *
   * Revision 1.2  2006/08/31 14:34:08  Ian.Mayo
   * Undeprecate old models = we'll no longer rely on lookup sensors
   *
   * Revision 1.1  2006/08/08 14:21:55  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:03  Ian.Mayo
   * First versions
   *
   * Revision 1.5  2004/11/03 15:42:08  Ian.Mayo
   * More support for MAD sensors, better use of canDetectThis method
   *
   * Revision 1.4  2004/10/18 19:06:27  ian
   * Fire array steady messages
   * <p/>
   * Revision 1.3  2004/10/18 15:09:57  Ian.Mayo
   * Handle array steadying
   * <p/>
   * Revision 1.2  2004/10/18 15:08:48  Ian.Mayo
   * Test steady time stuff
   * <p/>
   * Revision 1.1  2004/10/18 14:58:22  Ian.Mayo
   * Implementation complete.  In testing
   * <p/>
   * Revision 1.7  2004/10/14 13:38:51  Ian.Mayo
   * Refactor listening to sensors - so that we can listen to a sensor & it's detections in the same way that we can listen to a participant
   * <p/>
   * Revision 1.6  2004/09/06 14:20:05  Ian.Mayo
   * Provide default icons & properties for sensors
   * <p/>
   * Revision 1.5  2004/08/31 09:36:55  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.4  2004/08/26 17:05:35  Ian.Mayo
   * Implement more editable properties
   * <p/>
   * Revision 1.3  2004/08/25 11:21:08  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.2  2004/05/24 15:06:19  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.2  2004/03/25 22:46:55  ian
   * Reflect new simple environment constructor
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.1  2004/02/16 13:41:39  Ian.Mayo
   * Renamed class structure
   * <p/>
   * Revision 1.4  2003/11/05 09:19:07  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  ////////////////////////////////////////////////////
  // the editor object
  ////////////////////////////////////////////////////
  static public class NarrowbandInfo extends BaseSensorInfo
  {
    /**
     * @param data the Layers themselves
     */
    public NarrowbandInfo(final NarrowbandSensor data)
    {
      super(data, true);
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("Name", "the name of this narrowband sensor"),
          prop("SteadyTime", "the size of the aperture of this sensor"),
          prop("Working", "whether this sensor is in use"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }


  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class NBSensorTest extends ASSET.Util.SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public NBSensorTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new NarrowbandSensor(12);
    }

    public void testSteadyTime()
    {
      NarrowbandSensor ns = new NarrowbandSensor(22);
      ns.setName("ian test");
      ns.setSteadyTime(new Duration(2, Duration.MINUTES));

      // create valid locations
      WorldLocation loca = SupportTesting.createLocation(100, 1000);
      WorldLocation locb = SupportTesting.createLocation(200, 1000);

      long time = 0;

      EnvironmentType env = new SimpleEnvironment(1, 1, 1);
      DetectionList dets = new DetectionList();
      SSN me = new SSN(12);
      Status stat = new Status(12, 0);
      stat.setCourse(11);
      stat.setLocation(loca);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      me.setStatus(stat);

      Surface him = new Surface(122);
      Status statb = new Status(32, 0);
      statb.setCourse(13);
      statb.setLocation(locb);
      statb.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      him.setStatus(statb);
      him.getRadiatedChars().add(EnvironmentType.NARROWBAND, new NarrowbandRadNoise(222));

      CoreScenario theScenario = new CoreScenario();
      theScenario.addParticipant(him.getId(), him);

      // check dets is empty
      assertEquals("dets starts empty", 0, dets.size());

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should have gained at least one detection", 1, dets.size());

      // ok, move time forward.  check we get another
      time += 1000;

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should have gained at least one detection", 1, dets.size());

      // ok, move time forward.  check we get another
      time += 1000;

      // and change our course
      me.getStatus().setCourse(22);

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 0, dets.size());
      assertEquals("correct steady time set", 122000, ns._timeArraySteady, 0);

      // ok try another cycle
      time += 1000;

      // and change our course
      me.getStatus().setCourse(23);

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 0, dets.size());
      assertEquals("correct steady extended time set", 123000, ns._timeArraySteady, 0);
      // ok try another cycle
      time += 10000;

      // and change our course
      me.getStatus().setCourse(24);

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 0, dets.size());
      assertEquals("correct steady extended time set", 133000, ns._timeArraySteady, 0);

      // AND BACK ON COURSE
      time += 10000;

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 0, dets.size());
      assertEquals("correct steady extended time set", 133000, ns._timeArraySteady, 0);

      // STAY ON COURSE
      time += 10000;

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 0, dets.size());
      assertEquals("correct steady extended time set", 133000, ns._timeArraySteady, 0);


      // MAKE TIME ELAPSED VALID
      time = 133000;

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 1, dets.size());
      assertEquals("correct steady extended time set", 133000, ns._timeArraySteady, 0);

      // MAKE TIME ELAPSED VALID
      time = 134000;

      ns.detects(env, dets, me, theScenario, time);

      assertEquals("should not have gained any more detections", 1, dets.size());
      assertEquals("correct steady extended time set", 133000, ns._timeArraySteady, 0);


    }

  }
}