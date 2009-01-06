package ASSET.Models.Decision;

import ASSET.Models.DecisionType;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

import java.util.Iterator;
import java.util.Vector;


/**
 * Represents a Waterfall of prioritised behaviours.  If a higher priority behaviour is not applicable, it will
 * return a NULL demanded status object, and processing will fall to the behaviour with next lower
 * priority, and so on until a "catch-all" behaviour is supplied which will always return a demanded
 * course, or not, in which case the Waterfall returns null.
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public class Waterfall extends CoreDecision implements java.io.Serializable, BehaviourList
{

  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * handle my listeners
   */
  private java.beans.PropertyChangeSupport _pSupport = new java.beans.PropertyChangeSupport(this);

  /**
   * my list of decision models
   */
  protected Vector<DecisionType> _myDecisions;

  /**
   * an instance of our editor
   */
  protected MWC.GUI.Editable.EditorType _myEditor = null;


  /**
   * the index of the last decision model called.  we
   * keep track of the previous model so we can call it's interrupted
   * method after a higher-priority behaviour interrupts it
   *
   * @see ASSET.Models.DecisionType#interrupted(ASSET.Participants.Status)
   */
  protected int _lastDecision = INVALID_DECISION;


  final static protected int INVALID_DECISION = Integer.MAX_VALUE;

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Waterfall()
  {
    this("Waterfall");
  }

  public Waterfall(final String name)
  {
    super(name);
    _myDecisions = new Vector<DecisionType>(0, 1);
  }

  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////

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

  protected void fireUpdate(final String type, final ASSET.Models.DecisionType decider)
  {
    _pSupport.firePropertyChange(type, null, decider);
  }


  /**
   * put the indicated decision model at the head of our list
   */
  public void insertAtHead(final ASSET.Models.DecisionType decision)
  {
    _myDecisions.insertElementAt(decision, 0);
  }

  /**
   * put the indicated decision model at the head of our list
   */
  public void insertAtFoot(final ASSET.Models.DecisionType decision)
  {
    _myDecisions.addElement(decision);
  }

  /**
   * performed the waterfalled decision, if a model does not return
   * a demanded status, we move on to the next one
   */
  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars, ASSET.Participants.DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
                                                  final ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {
    ASSET.Participants.DemandedStatus res = null;

    String activity = "";

    int thisIndex;

    for (thisIndex = 0; thisIndex < _myDecisions.size(); thisIndex++)
    {
      final ASSET.Models.DecisionType dt = (ASSET.Models.DecisionType) _myDecisions.elementAt(thisIndex);

      // make a decision
      res = dt.decide(status, chars, demStatus, detections, monitor, time);

      // did this work?
      if (res != null)
      {
        activity = dt.getActivity();

        fireUpdate(UPDATED, dt);

        // yes, drop out
        break;
      }
    }


    // hey, have we previously made a decision
    if (_lastDecision != INVALID_DECISION)
    {
      // ok. have we change decision model?
      if (thisIndex != _lastDecision)
      {
        // yes.  Is the new one of higher priority?
        if (thisIndex < _lastDecision)
        {
          // yes, fire the last model's interrupted method
          final ASSET.Models.DecisionType dt = (ASSET.Models.DecisionType) _myDecisions.elementAt(_lastDecision);
          dt.interrupted(status);
        }
      }
    }

    // output the activity (even though it will include both the waterfall and activity reports)
    super.setLastActivity(activity);

    // just check if none of our models fired.  If not, we need to set this decision as invalid
    if (thisIndex == _myDecisions.size())
      thisIndex = INVALID_DECISION;

    // ok, now remember the decision model
    _lastDecision = thisIndex;

    return res;
  }

  public String getBehaviourName()
  {
    return "Waterfall";
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    // forget what the last decision made was
    _lastDecision = INVALID_DECISION;

    // pass the restart to the child events
    final Iterator<DecisionType> it = _myDecisions.iterator();
    while (it.hasNext())
    {
      final DecisionType dm = (DecisionType) it.next();
      dm.restart();
    }

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


  public Vector<DecisionType> getModels()
  {
    return _myDecisions;
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Waterfall.java,v $
   * Revision 1.2  2006/09/11 15:15:48  Ian.Mayo
   * Tidy to better reflect schema
   *
   * Revision 1.1  2006/08/08 14:21:41  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:50  Ian.Mayo
   * First versions
   *
   * Revision 1.17  2004/09/06 14:20:02  Ian.Mayo
   * Provide default icons & properties for sensors
   *
   * Revision 1.16  2004/09/02 13:17:42  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.15  2004/09/01 15:42:19  Ian.Mayo
   * Handle remembering the last decision when we didn't actually find a matching decision model
   * <p/>
   * Revision 1.14  2004/09/01 08:48:05  Ian.Mayo
   * Correct initialisation problem.
   * <p/>
   * Revision 1.13  2004/08/31 15:28:06  Ian.Mayo
   * Polish off test refactoring, start Intercept behaviour
   * <p/>
   * Revision 1.12  2004/08/31 09:36:35  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.11  2004/08/26 14:54:23  Ian.Mayo
   * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
   * <p/>
   * Revision 1.10  2004/08/24 10:34:40  Ian.Mayo
   * Better comment
   * <p/>
   * Revision 1.9  2004/08/20 13:32:43  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.8  2004/08/17 14:22:18  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.7  2004/08/06 12:56:05  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.6  2004/08/06 11:14:37  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.5  2004/05/24 15:59:45  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.4  2003/11/05 09:19:41  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
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
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new WaterfallInfo(this);

    return _myEditor;
  }
  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the object
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class WaterfallInfo extends CoreDecisionInfo
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public WaterfallInfo(final Waterfall data)
    {
      super(data);
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
          prop("Name", "the current location of this participant"),
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
      final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(Waterfall.class,
                                                                         ASSET.GUI.Editors.Decisions.WaterfallEditor.class);
      bp.setDisplayName("Waterfall");
      return bp;
    }
  }

  //////////////////////////////////////////////////
  // property testnig
  //////////////////////////////////////////////////
  public static class WaterfallTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new Waterfall("my name");
    }
  }


}