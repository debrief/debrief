/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 14-Jun-02
 * Time: 14:16:42
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Vessels.SonarBuoy;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

/**
 * behaviour relating to the launch of a weapon
 * The weapon to be launched is stored as a long text string.  The text string may contain
 * the following keywords which will be replaced with the correct value when the weapon
 * is launched (if known)
 * <ul>
 * <li>$BRG$  current bearing to the target (in degrees)</li>
 * <li>$RNG$  current range to the target (in yards)</li>
 * <ul>
 */

public class LaunchSensor extends CoreDecision implements Response, java.io.Serializable
{

  /****************************************************
   * member variables
   ***************************************************/

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * have we finished our processing?
   */
  private boolean _isAlive = true;

  /**
   * whether we should stay alive after we've launched
   */
  private boolean _stayAlive = false;

  /**
   * the type of sensor to launch
   */
  private String _launchThis = "blank";

  /**
   * a local copy of our editable object
   */
  private Editable.EditorType _myEditor = null;

  /**
   * *************************************************
   * constructor
   * *************************************************
   */
  public LaunchSensor()
  {
    super("Launch Sensor");
  }

  /****************************************************
   * member methods
   ***************************************************/

  /**
   * get the string describing what to launch
   */
  public String getLaunchType()
  {
    return _launchThis;
  }

  public boolean getAlive()
  {
    return _isAlive;
  }

  public void setAlive(boolean isAlive)
  {
    this._isAlive = isAlive;
  }

  public boolean getStayAlive()
  {
    return _stayAlive;
  }

  public void setStayAlive(boolean stayAlive)
  {
    this._stayAlive = stayAlive;
  }

  /**
   * set the XML string describing what to launch
   */
  public void setLaunchType(final String launchThis)
  {
    this._launchThis = launchThis;
  }


  /**
   * decide the course of action to take, or return null to no be used
   *
   * @param status     the current status of the participant
   * @param detections the current list of detections for this participant
   * @param monitor    the object which handles weapons release/detonation
   * @param time       the time this decision was made
   */
  public DemandedStatus decide(Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               long time)
  {
    // just do the RESPONSE part
    DemandedStatus res = null;

    String activity = "";

    if (_isAlive)
    {
      res = this.direct(null, status, demStatus, detections, monitor, time);


      // should we stay alive?
      if (!_stayAlive)
      {
        _isAlive = false;
        activity = "Launched Sensor. Now inactive";
      }
      else
        activity = "Launched Sensor. Still Active";
    }

    super.setLastActivity(activity);

    return res;
  }

  /**
   * @param conditionResult the object returned from the condition object
   * @param status          the current status of the participant
   * @param detections      the current set of detections
   * @param monitor         the monitor object listening out for significant activity
   * @param time            the current time step
   * @return the DemandedStatus for this vessel
   */
  public DemandedStatus direct(Object conditionResult,
                               Status status,
                               DemandedStatus demStat, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               long time)
  {
    SimpleDemandedStatus res = null;

    // create a status to use as the return value,
    // indicating that we should carry on in our current state
    res = new SimpleDemandedStatus(time, status);

    // create the status for the buoy
    Status buoyStat = new Status(status);

    // and over-ride the speed & depth
    buoyStat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
    buoyStat.getLocation().setDepth(40);

    // and the buoy itself
    int theId = ASSET.Util.IdNumber.generateInt();
    SonarBuoy buoy = new SonarBuoy(theId, buoyStat, "Sonar_Buoy_" + theId);

    // create the sensor for the sonar buoy
    BroadbandSensor bb = new BroadbandSensor(0);
    bb.setDetectionAperture(180d);
    bb.setName("BB on buoy:" + theId);
    // stick it into the buoy
    buoy.getSensorFit().add(bb);


    // get the host
    int id = status.getId();
    ParticipantType thePart = monitor.getThisParticipant(id);

    // let our helo listen to the sensor
    buoy.addParticipantDetectedListener(thePart);

    // update the category
    Category buoyCat = new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SONAR_BUOY);
    buoy.setCategory(buoyCat);

    // put the monitor into the parent
    monitor.createParticipant(buoy);

    return res;

  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    _isAlive = true;
  }


  /**
   * indicate to this model that its execution has been interrupted by another (prob higher priority) model
   *
   * @param currentStatus
   */
  public void interrupted(Status currentStatus)
  {
    // ignore.
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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new LaunchInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: LaunchSensor.java,v $
   * Revision 1.1  2006/08/08 14:21:35  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:44  Ian.Mayo
   * First versions
   *
   * Revision 1.16  2004/09/02 13:17:35  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   *
   * Revision 1.15  2004/08/31 09:36:24  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.14  2004/08/26 16:47:21  Ian.Mayo
   * Implement more editable properties, add Acceleration property editor
   * <p/>
   * Revision 1.13  2004/08/26 16:27:05  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.12  2004/08/25 11:20:39  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.11  2004/08/20 13:32:30  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.10  2004/08/17 14:22:07  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.9  2004/08/09 15:50:32  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.8  2004/08/06 12:52:04  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.7  2004/08/06 11:14:26  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.6  2004/05/24 15:57:10  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.5  2004/02/18 08:48:10  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.3  2003/11/05 09:19:54  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class LaunchInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public LaunchInfo(final LaunchSensor data)
    {
      super(data, data.getName(), "LaunchWeapon");
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
          prop("Name", "the name of this launch behaviour"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // embedded class which passes detections through to a
  //////////////////////////////////////////////////



  /**
   * *************************************************
   * testing
   * *************************************************
   */
  public static class LaunchSensorTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public LaunchSensorTest(final String val)
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
      return new LaunchSensor();
    }

    public void testCreate()
    {
      // create the weapon

      // todo: IMPLEMENT TESTS FOR THIS
    }
  }
}
