/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 25-Sep-2002
 * Time: 13:32:33
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Responses;

import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;


/**
 * class which directs a participant to adopt a particular course, speed, or depth.
 * These are optionally assigned relative to the detected vessel/bearing
 *
 * @see ASSET.Util.XML.Decisions.Responses.ManoeuvreToCourseHandler
 * @see ManoeuvreToLocation
 */
public class ManoeuvreToCourse extends Response.CoreResponse
{
  ////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////

  /**
   * the speed to travel at (kts)
   */
  private WorldSpeed _mySpeed;

  /**
   * whether the speed change is relative
   */
  private boolean _relativeSpeed;

  /**
   * the course to steer to (degs)
   */
  private Float _myCourse;

  /**
   * whether the course change is relative
   */
  private boolean _relativeCourse;

  /**
   * the height to change to (m), always absolute
   */
  private WorldDistance _myHeight;


  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////

  /**
   * @param speed          the speed to adopt (or null) (kts)
   * @param relativeSpeed  whether to make a speed change relative to the target
   * @param course         the course to switch to, or null (degs)
   * @param relativeCourse whether to make a relative course change (relative to the brg to target)
   * @param height         the height to switch to, or null (m)
   */
  public ManoeuvreToCourse(final WorldSpeed speed, final boolean relativeSpeed, final Float course,
                           final boolean relativeCourse, final WorldDistance height)
  {
    this._mySpeed = speed;
    this._relativeSpeed = relativeSpeed;
    this._myCourse = course;
    this._relativeCourse = relativeCourse;
    this._myHeight = height;
  }



  ////////////////////////////////////////////////////
  // response object
  ////////////////////////////////////////////////////




  /**
   * get the description of what we're doing
   */
  public String getActivity()
  {
    return "Move to course";
  }

  /**
   * produce the required response
   *
   * @param conditionResult the result from the condition test
   * @param status          the current status
   * @param detections      the current set of detections
   * @param monitor         the object monitoring us(for add/remove participants, detonations, etc)
   * @param time            the current time
   * @return
   * @see ASSET.Models.Decision.Conditions.Condition
   */

  public DemandedStatus direct(final Object conditionResult,
                               final Status status,
                               DemandedStatus demStat, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               final long time)
  {
    final SimpleDemandedStatus ds = new SimpleDemandedStatus(time, status);

    // check if we have a valid detection
    if (conditionResult instanceof ASSET.Models.Detection.DetectionEvent)
    {
      final ASSET.Models.Detection.DetectionEvent de = (ASSET.Models.Detection.DetectionEvent) conditionResult;

      // do we have a course value?
      if (_myCourse != null)
      {
        // yes, we have a detection. see if we have any relative parameters
        if (_relativeCourse)
        {
          // do we know brg to target?
          if (de.getBearing() != null)
          {
            double theBearing = de.getBearing().doubleValue();

            // steer a course relative to the bearing to the contact
            ds.setCourse(theBearing + _myCourse.floatValue());
          }
          else
          {
            // no, we don't know his course - don't make a course change
          }
        }
        else
        {
          // no we are not trying to set a relative course, set absolute
          ds.setCourse(_myCourse.floatValue());
        }
      }


      // do we have a Speed value?
      if (_mySpeed != null)
      {
        // yes, we have a detection. see if we have any relative parameters
        if (_relativeSpeed)
        {
          // do we know his Speed
          if (de.getSpeed() != null)
          {
            // steer a relative Speed
            ds.setSpeed(de.getSpeed().floatValue() + _mySpeed.getValueIn(WorldSpeed.M_sec));
          }
          else
          {
            // no, we don't know his Speed - don't make a Speed change
          }
        }
        else
        {
          // no we are not trying to set a relative Speed, set absolute
          ds.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));
        }
      }
    }
    else
    {
      // we don't have a detection, if there are any absolute changes, make them so
      if (!_relativeCourse)
      {
        if (_myCourse != null)
          ds.setCourse(_myCourse.floatValue());
      }

      if (!_relativeSpeed)
      {
        if (_mySpeed != null)
          ds.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));
      }
    }


    // now handle the depth (which can't be relative

    // do we have a depth value?
    if (_myHeight != null)
    {
      // no we are not trying to set a relative Depth, set absolute
      ds.setHeight(_myHeight.getValueIn(WorldDistance.METRES));
    }

    return ds;
  }

  public void restart()
  {
    // don't bother, we don't react to this
  }

  public WorldSpeed getSpeed()
  {
    return _mySpeed;
  }

  public void setSpeed(final WorldSpeed spd_kts)
  {
    this._mySpeed = spd_kts;
  }

  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new GotoLocationInfo(this);

    return _myEditor;
  }

  public boolean isRelativeSpeed()
  {
    return _relativeSpeed;
  }

  public void setRelativeSpeed(final boolean relativeSpeed)
  {
    this._relativeSpeed = relativeSpeed;
  }

  public Float getCourse()
  {
    return _myCourse;
  }

  public void setCourse(final Float course)
  {
    this._myCourse = course;
  }

  public boolean isRelativeCourse()
  {
    return _relativeCourse;
  }

  public void setRelativeCourse(final boolean RelativeCourse)
  {
    this._relativeCourse = RelativeCourse;
  }

  public WorldDistance getHeight()
  {
    return _myHeight;
  }

  public void setHeight(final WorldDistance height)
  {
    this._myHeight = height;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class GotoLocationInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public GotoLocationInfo(final ManoeuvreToCourse data)
    {
      super(data, data.getName(), "Location Response");
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
          prop("Name", "the name of this response"),
          prop("Course", "the course to turn to (degs)"),
          prop("Speed", "the speed to travel at (kts)"),
          //         prop("Depth", "the depth to travel at (m)"),
          prop("RelativeSpeed", "whether to change course relative to detection bearing"),
          prop("RelativeCourse", "whether to change speed relative to target speed"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  ////////////////////////////////////////////////////
  // testing support
  ////////////////////////////////////////////////////
  public static class ManoeuvreToCourseTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ManoeuvreToCourseTest(final String name)
    {
      super(name);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new ManoeuvreToCourse(null, false, null, false, null);
    }

    public void testIt()
    {
      ManoeuvreToCourse mc = new ManoeuvreToCourse(new WorldSpeed(10, WorldSpeed.M_sec), true,
                                                   new Float(20), true,
                                                   new WorldDistance(40, WorldDistance.METRES));

      final CoreParticipant cp = new ASSET.Models.Vessels.Surface(12);
      final ASSET.Models.Sensor.Initial.InitialSensor cs = new ASSET.Models.Sensor.Initial.BroadbandSensor(22);
      ASSET.Models.Detection.DetectionEvent de =
        new ASSET.Models.Detection.DetectionEvent(1200, cp.getId(), null, cs,
                                                  new WorldDistance(33, WorldDistance.YARDS), // range
                                                  new WorldDistance(33, WorldDistance.YARDS), // estimated range
                                                  new Float(3), // course
                                                  null, null, null, new Float(22), null, cp);

      final Status status = new Status(10, 10);
      status.setLocation(new WorldLocation(2, 2, 2));
      status.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      status.setCourse(45);
      DemandedStatus res = mc.direct(de, status, null, null, null, 1200);
      SimpleDemandedStatus sds = (SimpleDemandedStatus) res;

      // check we got something returned
      assertNotNull("demanded status returned", res);

      // check we correctly handled the relative aspects
      assertEquals("correct relative course", 23, sds.getCourse(), 0.001);
      assertEquals("correct relative speed", 32, sds.getSpeed(), 0.001);
      assertEquals("correct absolute depth", 40, sds.getHeight(), 0.001);

      // hey, let's not provide course or speed!
      de = new ASSET.Models.Detection.DetectionEvent(1200, cp.getId(), null, cs, null, null, null,
                                                     null, null, null, null, null, cp);

      res = mc.direct(de, status, null, null, null, 1200);
      sds = (SimpleDemandedStatus) res;

      // check we got something returned
      assertNotNull("demanded status returned", res);

      // check we correctly handled the relative aspects
      assertEquals("correct relative course ignored", 45, sds.getCourse(), 0.001);
      assertEquals("correct relative speed ignored", 12, sds.getSpeed(), 0.001);
      assertEquals("correct absolute depth", 40, sds.getHeight(), 0.001);

      // handle when there's missing data
      de = new ASSET.Models.Detection.DetectionEvent(1200, cp.getId(), null, cs, null, null, new Float(33),
                                                     null, null, null, new Float(22), null, cp);

      mc = new ManoeuvreToCourse(new WorldSpeed(10, WorldSpeed.M_sec), false,
                                 new Float(20), false,
                                 new WorldDistance(40, WorldDistance.METRES));

      res = mc.direct(de, status, null, null, null, 1200);
      sds = (SimpleDemandedStatus) res;

      // check we got something returned
      assertNotNull("demanded status returned", res);

      // check we correctly handled the relative aspects
      assertEquals("correct absolute course", 20, sds.getCourse(), 0.001);
      assertEquals("correct absolute speed", 10, sds.getSpeed(), 0.001);
      assertEquals("correct absolute depth", 40, sds.getHeight(), 0.001);

      mc = new ManoeuvreToCourse(null, false, null, false, null);

      res = mc.direct(de, status, null, null, null, 1200);
      sds = (SimpleDemandedStatus) res;

      // check we got something returned
      assertNotNull("demanded status returned", res);

      // check we correctly handled the relative aspects
      assertEquals("empty course", 45, sds.getCourse(), 0.001);
      assertEquals("empty speed", 12, sds.getSpeed(), 0.001);
      assertEquals("empty depth", -2, sds.getHeight(), 0.001);
    }

  }
}
