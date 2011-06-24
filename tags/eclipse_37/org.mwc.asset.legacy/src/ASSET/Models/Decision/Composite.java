package ASSET.Models.Decision;

import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Decision.Conditions.TimePoint;
import ASSET.Models.Decision.Responses.ManoeuvreToLocation;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

/**
 * Our implementation of evasion behaviour.  Our vessel will attempt to evade any contacts
 * with the specified behaviour
 */

public class Composite extends CoreDecision implements java.io.Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the condition object we manage
   */
  private Condition _myCondition = null;

  /**
   * the response object we manage
   */
  private Response _myResponse = null;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;


  public Composite(final Condition theCondition, final Response theResponse)
  {
    super("Composite Behaviour");
    _myCondition = theCondition;
    _myResponse = theResponse;
  }

  /**
   * decide
   *
   * @param status parameter for decide
   * @param time   parameter for decide
   * @return the returned ASSET.Participants.DemandedStatus
   */
  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
                                                  final ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {

    DemandedStatus res = null;

    Object test = null;

    // check we have a condition
    if (_myCondition != null)
    {
      test = _myCondition.test(status, detections, time, null);
    }
    else
    {
      // if we haven't got a condition just produce a Long containing the time
      test = new Boolean(true);
    }

    // ok, continue if everything was ok
    if (test != null)
    {
      res = _myResponse.direct(test, status, demStatus, detections, monitor, time);
    }

    super.setLastActivity(_myResponse.getActivity());

    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    // no detections, reset our variables
    _myCondition.restart();
    _myResponse.restart();
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

  public Condition getCondition()
  {
    return _myCondition;
  }

  public Response getResponse()
  {
    return _myResponse;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Composite.java,v $
   * Revision 1.1  2006/08/08 14:21:39  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:47  Ian.Mayo
   * First versions
   *
   * Revision 1.13  2004/09/02 13:17:39  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   *
   * Revision 1.12  2004/08/31 09:36:31  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.11  2004/08/26 14:09:51  Ian.Mayo
   * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
   * <p/>
   * Revision 1.10  2004/08/25 11:20:50  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.9  2004/08/20 13:32:38  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.8  2004/08/17 14:22:15  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.7  2004/08/06 12:55:31  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.6  2004/08/06 11:14:33  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.5  2004/05/24 15:59:41  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.4  2003/11/05 09:19:39  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  //////////////////////////////////////////////////////////////////////
  // editable data
  //////////////////////////////////////////////////////////////////////
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
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new CompositeInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////
  // editor info class
  ////////////////////////////////////////////////////

  static public class CompositeInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public CompositeInfo(final Composite data)
    {
      super(data, data.getName(), "Edit");
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
          prop("Name", "the name of this composite behavior"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }


    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
      final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(Composite.class,
                                                                         ASSET.GUI.Editors.Decisions.CompositeEditor.class);
      bp.setDisplayName("Composite");
      return bp;
    }
  }

  ////////////////////////////////////////////////////
  // testing support
  ////////////////////////////////////////////////////
  static public class CompositeTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new Composite(null, null);
    }

    public void testIt()
    {
      // create the condition
      final TimePoint tp = new TimePoint(1200);

      // create the response
      final ManoeuvreToLocation gl = new ManoeuvreToLocation(new MWC.GenericData.WorldLocation(0, 1, 2), null);

      final Composite cs = new Composite(tp, gl);
      final ASSET.Participants.Status st = new ASSET.Participants.Status(12, 200);
      st.setLocation(new MWC.GenericData.WorldLocation(0, 0, 0));
      st.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      DemandedStatus ds = cs.decide(null, null, null, null, null, 800);

      // check nothing returned
      assertNull("nothing returned", ds);

      // move forward in time
      ds = cs.decide(st, null, null, null, null, 1400);
      assertNotNull("something returned", ds);


    }
  }


}