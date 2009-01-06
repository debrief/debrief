package ASSET.Models.Decision;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

import java.util.Vector;


/**
 * Represents a sequence of ordered behaviours.
 * <p/>
 * When called, the sequence first examines its "Alive" status.  If the sequence is not
 * "Alive", it returns a blank null.
 * <p/>
 * While a behaviour returns a non-null demanded status, that behaviour continues to be active.
 * Once a behaviour returns a null status, processing switches to the next behaviour, and so on.
 * <p/>
 * Once all of the behaviours are complete, the "StayAlive" setting is examined.  If this behaviour
 * should stay alive, then it remains "Alive" for the next time it's required.  If not, it dies
 * and is not triggered again.
 * <p/>
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public class Switch extends Waterfall
{

  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the index of the current behaviour
   */
  private int _selectedActivity = 0;

  /**
   * whether this status is currently alive
   */
  private boolean _isAlive = true;
  private static final String MY_NAME = "Switch";

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * constructor - ensures we know which is the selected activity
   *
   */
  public Switch()
  {
    super(MY_NAME);
    _myDecisions = new Vector<ASSET.Models.DecisionType>(0, 1);
  }

  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////

  public String getBehaviourName()
  {
    return MY_NAME;
  }

  /**
   * performed the waterfalled decision, if a model does not return
   * a demanded status, we move on to the next one
   */
  public DemandedStatus decide(final Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars,
                               DemandedStatus demStatus,
                               final DetectionList detections,
                               final ScenarioActivityMonitor monitor,
                               final long time)
  {
    DemandedStatus res = null;

    if (this.getIsAlive())
    {
      Waterfall ourDecision = (Waterfall) _myDecisions.elementAt(getIndex() -1);
      res = ourDecision.decide(status, chars, demStatus, detections, monitor, time);


      // did this work?
      if (res != null)
      {
        String activity = ourDecision.getActivity();

        fireUpdate(UPDATED, ourDecision);

        super.setLastActivity(activity);
      } // whether it worked
    }

    return res;
  }

  /**
   * find out if this behaviour is currently available (it's not alive if it has
   * completed and is not set to stay alive)
   *
   * @return yes/no
   */
  public boolean getIsAlive()
  {
    return _isAlive;
  }

  /**
   * set if this behaviour is currently available (it's not alive if it has
   * completed and is not set to stay alive)
   *
   * @param isAlive yes/no
   */
  public void setIsAlive(boolean isAlive)
  {
    this._isAlive = isAlive;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new SequenceInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Switch.java,v $
   * Revision 1.1  2006/08/08 14:21:40  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:48  Ian.Mayo
   * First versions
   *
   * Revision 1.1  2004/10/21 13:35:23  Ian.Mayo
   * First version
   *
   * Revision 1.17  2004/08/31 09:36:33  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

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
   * get the activity we are going to use
   *
   * @return
   */
  public int getIndex()
  {
    return _selectedActivity;
  }

  /**
   * set the activity we are going to use
   *
   * @param index the index
   */
  public void setIndex(int index)
  {
    this._selectedActivity = index;
  }

  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the object
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class SequenceInfo extends EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public SequenceInfo(final Switch data)
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
          prop("Name", "the name of this condition"),
          prop("IsAlive", "whether this behaviour is currently operating"),
          prop("Index", "the index of the child waterfall we should use"),
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
      final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(Switch.class,
                                                                         ASSET.GUI.Editors.Decisions.WaterfallEditor.class);
      bp.setDisplayName("Sequence");
      return bp;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class SequenceTest extends ASSET.Util.SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public SequenceTest(final String val)
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
      return new Switch();
    }

  }


}