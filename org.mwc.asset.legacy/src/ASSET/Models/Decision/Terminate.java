package ASSET.Models.Decision;

import ASSET.ScenarioType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

/**
 * Terminate behaviour = used to stop a simulation
 */

public class Terminate extends CoreDecision implements java.io.Serializable
{


  public static final String NAME = "Terminate";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * handle my listeners
   */
  private java.beans.PropertyChangeSupport _pSupport = new java.beans.PropertyChangeSupport(this);


  /**
   */
  public Terminate()
  {
    super(NAME);
  }

  /**
   * somebody wants to know about us
   */
  public void addListener(final String type, final java.beans.PropertyChangeListener listener)
  {
    _pSupport.addPropertyChangeListener(type, listener);
  }

  /**
   * somebody wants to know about us
   */
  public void removeListener(final String type, final java.beans.PropertyChangeListener listener)
  {
    _pSupport.removePropertyChangeListener(type, listener);
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
                                                  ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {
  	
    SimpleDemandedStatus res = null;

    String activity = "";

    if (isActive())
    {
    	// ALL YOUR BASE ARE MINE
    	
    	// get the scenario
    	if(monitor instanceof ScenarioType)
    	{
    		ScenarioType scen = (ScenarioType) monitor;
    		scen.stop("Stopped by TERMINATE behaviour");
    	}
    	
      activity = "under control";
    }

    super.setLastActivity(activity);

    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    // no detections, reset our variables
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
      _myEditor = new UserControlInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: UserControl.java,v $
   * Revision 1.1  2006/08/08 14:21:41  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:49  Ian.Mayo
   * First versions
   *
   * Revision 1.14  2004/09/02 13:17:41  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   *
   * Revision 1.13  2004/08/31 15:28:04  Ian.Mayo
   * Polish off test refactoring, start Intercept behaviour
   * <p/>
   * Revision 1.12  2004/08/31 09:36:34  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.11  2004/08/26 14:54:22  Ian.Mayo
   * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
   * <p/>
   * Revision 1.10  2004/08/20 13:32:41  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.9  2004/08/17 14:22:17  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.8  2004/08/06 12:55:51  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.7  2004/08/06 11:14:36  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.6  2004/05/24 15:59:44  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.5  2003/11/05 09:19:40  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date: 2010-11-17 08:59:11 +0000 (Wed, 17 Nov 2010) $";
  }


  static public class UserControlInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public UserControlInfo(final Terminate data)
    {
      super(data, data.getName(), NAME);
    }

    /**
     * return the custom editor for this object
     */
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
      final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(Terminate.class,
                                                                         ASSET.GUI.Editors.Decisions.UserControlEditor.class);
      bp.setDisplayName(super.getData().toString());
      return bp;
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
          prop("Active", "whether this control is active"),
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
  // property testnig
  //////////////////////////////////////////////////
  public static class ControlTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new Terminate();
    }
  }

}