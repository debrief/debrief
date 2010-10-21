package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 *  $Log: Wait.java,v $
 *  Revision 1.1  2006/08/08 14:21:38  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:47  Ian.Mayo
 *  First versions
 *
 *  Revision 1.17  2004/10/28 14:52:43  ian
 *  Correct how we test for correct elapsed time
 *
 *  Revision 1.16  2004/10/28 13:52:44  ian
 *  Correct how we check for passage of elapsed time
 *
 *  Revision 1.15  2004/08/31 09:36:30  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.14  2004/08/26 16:27:11  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.13  2004/08/25 11:20:48  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.12  2004/08/20 13:32:37  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.11  2004/08/17 14:22:13  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.10  2004/08/12 10:46:43  Ian.Mayo
 *  Pass parameters in constructor
 *
 *  Revision 1.9  2004/08/06 12:52:10  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.8  2004/08/06 11:14:32  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.7  2004/05/24 15:57:20  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *
 *  Revision 1.6  2003/11/05 09:19:58  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.5  2003/09/18 14:11:50  Ian.Mayo
 *  Make tests work with new World Speed class
 *
 *  Revision 1.4  2003/09/15 10:17:08  Ian.Mayo
 *  Continue working towards demanded status while we are waiting
 *
 *  Revision 1.3  2003/09/09 15:55:47  Ian.Mayo
 *  Change signature of decision model
 *
 *  Revision 1.2  2003/09/03 14:06:27  Ian.Mayo
 *  Initial implementation
 *
 */

/**
 * Just hang around for the designated period
 */
public class Wait extends CoreDecision implements Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the time to wait for
   */
  private Duration _myDuration;

  /**
   * the time at which we finish waiting
   */
  protected long _expiryTime = -1;

  
  /** the previous status for this platform (so we can return to it on completion)
   * 
   */
	private DemandedStatus _originalStatus;
  

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * simple constructor to define essential items
   *
   * @param myDuration how long to wait for
   * @param myName     our name
   */
  public Wait(Duration myDuration, String myName)
  {
    super(myName);
    this._myDuration = myDuration;
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * decide the course of action to take, or return null to no be used
   *
   * @param status     the current status of the participant
   * @param detections the current list of detections for this participant
   * @param monitor    the object which handles weapons release/detonation
   * @param newTime    the time this decision is to be made
   */
  public DemandedStatus decide(Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars,
                               DemandedStatus demStatus,
                               DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               long newTime)
  {
    DemandedStatus res = null;

    String activity = "";

    // have we been called yet
    if (_expiryTime == -1)
    {
      // no, initialise ourseves
      _expiryTime = newTime + _myDuration.getMillis();
      _originalStatus = demStatus;
    }

    // now check whether we have any remaining wait
    if (newTime >= _expiryTime)
    {
      // all done!
    	// restore the prior status
    	res = _originalStatus;
    	_originalStatus = null;
    }
    else
    {

      // are we working towards a demanded status?
      if (demStatus instanceof SimpleDemandedStatus)
      {
				// keep us heading towards the current dem status
    //    res = new SimpleDemandedStatus(newTime, (SimpleDemandedStatus) demStatus);
      	// CHANGE: we're introducing stationery change
        Status beStationery = new Status(status);
        beStationery.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
      	res = new SimpleDemandedStatus(newTime, beStationery);
      }
      else
      {
        // no, still not at depth - make another request
        res = new SimpleDemandedStatus(newTime, status);
      }

      activity = "Still waiting";

    }

    super.setLastActivity(activity);

    return res;
  }


  /**
   * reset this decision model
   */
  public void restart()
  {
    _expiryTime = -1;
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


  public Duration getDuration()
  {
    return _myDuration;
  }

  public void setDuration(Duration myDuration)
  {
    this._myDuration = myDuration;
  }

  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  private EditorType _myEditor;

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
      _myEditor = new WaitInfo(this);

    return _myEditor;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class WaitInfo extends EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public WaitInfo(final Wait data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this decision model"),
          prop("Duration", "the period to wait for"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Wait.java,v $
   * Revision 1.1  2006/08/08 14:21:38  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:47  Ian.Mayo
   * First versions
   *
   * Revision 1.17  2004/10/28 14:52:43  ian
   * Correct how we test for correct elapsed time
   *
   * Revision 1.16  2004/10/28 13:52:44  ian
   * Correct how we check for passage of elapsed time
   * <p/>
   * Revision 1.15  2004/08/31 09:36:30  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.14  2004/08/26 16:27:11  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.13  2004/08/25 11:20:48  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.12  2004/08/20 13:32:37  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.11  2004/08/17 14:22:13  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.10  2004/08/12 10:46:43  Ian.Mayo
   * Pass parameters in constructor
   * <p/>
   * Revision 1.9  2004/08/06 12:52:10  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.8  2004/08/06 11:14:32  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.7  2004/05/24 15:57:20  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.6  2003/11/05 09:19:58  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
  static public class WaitTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public WaitTest(final String val)
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
      return new Wait(null, "my name");
    }

    public void testSimple()
    {
      Duration dur = new Duration(12, Duration.SECONDS);
      Wait wt = new Wait(dur, "do a wait");

      // ok, give it a test.
      Status stat = new Status(12, 0);
      stat.setTime(0);
      stat.setSpeed(new WorldSpeed(2, WorldSpeed.Kts));
      stat.setLocation(new WorldLocation(0, 0, 0));
      DemandedStatus res = new SimpleDemandedStatus(0, stat);

      stat.setTime(stat.getTime() + 1000);
      res = wt.decide(stat, null, res, null, null, stat.getTime());

      // have we set the correct expiry time?
      assertEquals("correct expiry time set", 13000, wt._expiryTime, 0);

      // are we continuing in state?
      assertNotNull("returned dem status", res);

      res = wt.decide(stat, null, res, null, null, stat.getTime());
      stat.setTime(stat.getTime() + 1000);

      res = wt.decide(stat, null, res, null, null, stat.getTime());
      stat.setTime(stat.getTime() + 10000);

      res = wt.decide(stat, null, res, null, null, stat.getTime());
      stat.setTime(stat.getTime() + 1000);
      res = wt.decide(stat, null, res, null, null, stat.getTime());
      stat.setTime(stat.getTime() + 1000);
      res = wt.decide(stat, null, res, null, null, stat.getTime());
      assertNull("dropped out", res);

    }
  }
}
