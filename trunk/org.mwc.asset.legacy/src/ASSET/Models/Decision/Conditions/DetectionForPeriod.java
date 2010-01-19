/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 23-Oct-2002
 * Time: 11:30:27
 */
package ASSET.Models.Decision.Conditions;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;

public class DetectionForPeriod extends Detection
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
   * how long should we remain "true" for?
   */
  private Duration _validPeriod;

  /**
   * the detection which activated us
   */
  private DetectionEvent _validDetection;

  /**
   * the time at which we are due to expire (after a valid detection)
   */
  private long _expiryTime = -1;

  /**
   * whether we should continue listening for valid detections (each time putting
   * back our expiry time
   */
  private boolean _continueMonitoring = false;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////


  /**
   * constructor for behaviour which returns true for a indicated
   * period after a particular detection
   *
   * @param theTargetType      the type of target we're looking for
   * @param theThreshold       the threshold range at which detections are valid (or null for all detections)
   * @param validPeriod        the period we should remain "true" for after a valid detection
   * @param continueMonitoring whether we continue listening out for further valid contacts
   */
  public DetectionForPeriod(final TargetType theTargetType,
                            final WorldDistance theThreshold,
                            final Duration validPeriod,
                            final boolean continueMonitoring)
  {
    super(theTargetType, theThreshold);
    _validPeriod = validPeriod;
    _continueMonitoring = continueMonitoring;
  }

  ////////////////////////////////////////////////////
  // condition fields
  ////////////////////////////////////////////////////

  /**
   * see if we do have any detections of interest.  If not, are we still in the valid time period
   *
   * @param status
   * @param detections
   * @param time
   * @param monitor
   * @return
   */
  public Object test(Status status,
                     DetectionList detections,
                     long time,
                     ScenarioActivityMonitor monitor)
  {
    DetectionEvent res = null;

    // see if we can still detect any valid targets
    DetectionEvent theDet = (DetectionEvent) super.test(status, detections, time, monitor);

    // are we still in our valid period?
    if (_expiryTime != -1)
    {

      // are we continuing to listen?
      if (_continueMonitoring)
      {
        // do we have a detection?
        if (theDet != null)
        {
          // if we are still in contact, put back the expiry time
          _expiryTime = time + (long) _validPeriod.getValueIn(Duration.MILLISECONDS);
        }
      }

      // we're still processing. is this still a valid time?
      if (time < _expiryTime)
      {
        // yes, just return our current detection
        res = _validDetection;
      }
      else
      {
        // time expired, clear it
        _validDetection = null;
        _expiryTime = -1;
      }
    }
    else
    {
      if (theDet != null)
      {
        // do we have an activation period?
        if (_validPeriod != null)
        {
          // ok, we've been "activated", set our expiry time
          _expiryTime = time + (long) _validPeriod.getValueIn(Duration.MILLISECONDS);
          // and remember it
          _validDetection = theDet;
        }

        // and set the return value
        res = theDet;

      }
    }

    return res;
  }


  ////////////////////////////////////////////////////
  // accessors
  ////////////////////////////////////////////////////
  public Duration getValidPeriod()
  {
    return _validPeriod;
  }

  public void setValidPeriod(Duration validPeriod)
  {
    _validPeriod = validPeriod;
  }

  public boolean getContinueMonitoring()
  {
    return _continueMonitoring;
  }

  public void setContinueMonitoring(boolean continueMonitoring)
  {
    _continueMonitoring = continueMonitoring;
  }


  ////////////////////////////////////////////////////
  // editor support
  ////////////////////////////////////////////////////

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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new DetectionPeriodInfo(this);

    return _myEditor;
  }


  static public class DetectionPeriodInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public DetectionPeriodInfo(final DetectionForPeriod data)
    {
      super(data, data.getName(), "Detection Condition");
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
          prop("Name", "the name of this condition"),
          prop("TargetType", "the type of target we're looking for"),
          prop("ValidPeriod", "the period for which we should continue after first detection"),
          prop("ContinueMonitoring", "whether to continue monitoring for further detections"),
          prop("RangeThreshold", "the minimum range at which to trigger detection")
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
  //
  ////////////////////////////////////////////////////

  static public class DetPeriodTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public DetPeriodTest(final String name)
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
      final TargetType tt = new TargetType();
      tt.addTargetType(Category.Type.AV_MISSILE);

      Detection detection = new DetectionForPeriod(tt, null, null, false);
      return detection;
    }

    public void testIt()
    {
      // build it
      final Category secondTarget = new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.AV_MISSILE);

      final ASSET.ParticipantType participant = new ASSET.Models.Vessels.Surface(12);
      participant.setName("un-named");
      final CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(12);
      final DetectionEvent db = new DetectionEvent(120, participant.getId(), null, sensor,
                                                   new WorldDistance(1, WorldDistance.YARDS),
                                                   new WorldDistance(1, WorldDistance.YARDS),
                                                   null, null,
                                                   new Float(60), secondTarget, null, null, participant);

      // build up the list
      DetectionList dl = new DetectionList();

      // try with no detections

      final TargetType tt = new TargetType();
      tt.addTargetType(Category.Type.AV_MISSILE);

      Detection detection = new DetectionForPeriod(tt, null, null, false);
      DetectionEvent de = (DetectionEvent) detection.test(null, dl, 0, null);
      assertNull("no success with empty list", de);

      // add the second (valid item)
      DetectionForPeriod d2 = new DetectionForPeriod(tt, null, new Duration(20, Duration.MILLISECONDS), false);
      dl.add(db);
      de = (DetectionEvent) d2.test(null, dl, 0, null);
      assertNotNull("success with valid contact", de);

      // try it a few minutes later (even with no detections)
      dl = new DetectionList();
      de = (DetectionEvent) d2.test(null, dl, 15, null);
      assertNotNull("we got detection when still in period", de);

      // check we can expire
      de = (DetectionEvent) d2.test(null, dl, 25, null);
      assertNull("we didn't get detection when still out of period", de);


      // create empty

    }
  }


}
