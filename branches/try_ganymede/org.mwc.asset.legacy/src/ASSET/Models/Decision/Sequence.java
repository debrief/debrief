package ASSET.Models.Decision;

import ASSET.Models.DecisionType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
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

public class Sequence extends Waterfall
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
  protected int _currentDecision = 0;

  /**
   * whether or not we should stay alive on completion
   */
  private boolean _stayAlive;

  /**
   * whether this status is currently alive
   */
  private boolean _isAlive = true;

  /**
   * the original is alive setting
   */
  private boolean _originalIsAlive;

  /**
   * our behaviour name
   */
  private static final String MY_NAME = "Sequence";

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Sequence(boolean isAlive, String name)
  {
    super(name);
    _myDecisions = new Vector<ASSET.Models.DecisionType>(0, 1);

    _originalIsAlive = isAlive;
    _isAlive = isAlive;
  }

  public Sequence()
  {
    this(true, MY_NAME);
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
  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars,
                                                  DemandedStatus demStatus,
                                                  final ASSET.Models.Detection.DetectionList detections,
                                                  final ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {
    ASSET.Participants.DemandedStatus res = null;

    String activity = null;

    // just check we're still alive
    if (!this.getIsAlive())
    {

    }
    else
    {

      // step through our list
      int index;

      for (index = _currentDecision; index < _myDecisions.size(); index++)
      {
        final DecisionType dt = (DecisionType) _myDecisions.elementAt(index);

        // make a decision
        res = dt.decide(status, chars, demStatus, detections, monitor, time);

        // did this work?
        if (res != null)
        {
          activity = dt.getActivity();

          fireUpdate(UPDATED, dt);

          // yes, drop out
          break;
        } // whether it worked

      } // looping through the behaviours

      // did we find a valid behaviour?
      if (res != null)
      {
        // yes, all is well - remember the current decision model
        _currentDecision = index;
      }
      else
      {
        // bugger, we've finished - reset the pointer to the first item
        _currentDecision = 0;

        // do we stay alive
        if (_stayAlive)
        {
          // yes, we do stay alive.
          // reset the behaviours (so they run again)
          this.restart();
        }
        else
        {
          // no, we don't want to, just lie down and die
          _isAlive = false;
        }
      }
    }

    super.setLastActivity(activity);

    return res;
  }


  /**
   * reset this decision model
   */
  public void restart()
  {
    super.restart();
    
    // go back to the first step in the sequence
    _currentDecision = 0;

    _isAlive = _originalIsAlive;
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
   * find out if this sequence of events should restart once complete.  If not, IsAlive is
   * set to false on completion, and it will not run again
   *
   * @return yes/no for whether to re-run
   */
  public boolean getStayAlive()
  {
    return _stayAlive;
  }

  /**
   * set if this sequence of events should restart once complete.  If not, IsAlive is
   * set to false on completion, and it will not run again
   *
   * @param stayAlive yes/no for whether to re-run
   */
  public void setStayAlive(boolean stayAlive)
  {
    this._stayAlive = stayAlive;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public MWC.GUI.Editable.EditorType getInfo()
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
   * $Log: Sequence.java,v $
   * Revision 1.2  2006/11/02 10:33:19  Ian.Mayo
   * Restart sequence properly
   *
   * Revision 1.1  2006/08/08 14:21:40  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:48  Ian.Mayo
   * First versions
   *
   * Revision 1.18  2004/10/28 14:52:30  ian
   * Correct the restart implementation
   *
   * Revision 1.17  2004/08/31 09:36:33  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.16  2004/08/26 16:27:12  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.15  2004/08/26 14:09:52  Ian.Mayo
   * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
   * <p/>
   * Revision 1.14  2004/08/25 11:20:52  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.13  2004/08/20 13:32:40  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.12  2004/08/17 14:22:16  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.11  2004/08/06 12:55:41  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.10  2004/08/06 11:14:34  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.9  2004/05/24 15:59:42  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.8  2003/11/05 09:19:39  Ian.Mayo
   * Include MWC Model support
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

  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the object
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class SequenceInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public SequenceInfo(final Sequence data)
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
          prop("StayAlive", "whether this behaviour should restart on completion"),
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
      final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(Sequence.class,
        ASSET.GUI.Editors.Decisions.WaterfallEditor.class);
      bp.setDisplayName(MY_NAME);
      return bp;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class SequenceTest extends SupportTesting.EditableTesting
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
      return new Sequence(true, "test behaviour");
    }

    private static class MyDecision extends CoreDecision
    {
      public MyDecision()
      {
        super("My Decision");
      }

      public DemandedStatus myRes = null;
      public boolean hasRestarted = false;

      public DemandedStatus decide(Status status,
                                   ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, DetectionList detections,
                                   ScenarioActivityMonitor monitor,
                                   long time)
      {
        hasRestarted = false;
        return myRes;
      }

      public Editable.EditorType getInfo()
      {
        return null;
      }

      public boolean hasEditor()
      {
        return false;
      }

      public void restart()
      {
        hasRestarted = true;
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

      public String getVersion()
      {
        return "$Date$";
      }


    }

    public void testTheSequence()
    {
      // ok, let's get going
      Sequence seq = new Sequence(true, MY_NAME);

      // create a valid demanded status to test against
      SimpleDemandedStatus validA = new SimpleDemandedStatus(1, 12000);
      SimpleDemandedStatus validB = new SimpleDemandedStatus(1, 12000);
      SimpleDemandedStatus validD = new SimpleDemandedStatus(1, 12000);

      // initialise the settings
      seq.setIsAlive(true);
      seq.setStayAlive(false);

      // give it a trial run (without data)
      Status initialStat = new Status(12, 12000);
      DemandedStatus ds = seq.decide(initialStat, null, null, null, null, 1200);

      assertTrue("no demanded stat with empty behaviours", ds == null);
      assertEquals("is now dead", seq.getIsAlive(), false);

      // create a few  behaviours
      MyDecision da = new MyDecision();
      MyDecision db = new MyDecision();
      MyDecision dc = new MyDecision();
      MyDecision dd = new MyDecision();

      // and insert them
      seq.insertAtHead(dc);
      seq.insertAtHead(db);

      // check they're in the correct order
      assertEquals("b at head", seq.getModels().get(0), db);
      assertEquals("c at foot", seq.getModels().get(1), dc);

      // and a couple more
      seq.insertAtHead(da);
      seq.insertAtFoot(dd);

      // check they're in the correct order
      assertEquals("a at head", seq.getModels().get(0), da);
      assertEquals("d at foot", seq.getModels().get(3), dd);

      // ok, start testing the decisions themselves
      assertEquals("the current decision is correctly set", seq._currentDecision, 0);
      // bring it back to life
      seq.setIsAlive(true);
      assertEquals("is already alive", seq.getIsAlive(), true);
      ds = seq.decide(null, null, null, null, null, 100);

      // hey, they're all returning null - check we've died
      assertEquals("we returned null", ds, null);
      assertEquals("we've now died", seq.getIsAlive(), false);

      // revive us, and set stay alive to true
      seq.setStayAlive(true);
      seq.setIsAlive(true);

      ds = seq.decide(null, null, null, null, null, 100);
      // hey, they're all returning null - check we've died
      assertEquals("we returned null", ds, null);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);

      // ok, give a behaviour and run it
      da.myRes = validA;

      ds = seq.decide(null, null, null, null, null, 100);

      // hey, a returns valid
      assertEquals("we returned correct result", ds, validA);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);
      assertEquals("we've got the correct behaviour", seq._currentDecision, 0);

      // now give b a behaviour and try again
      db.myRes = validB;

      ds = seq.decide(null, null, null, null, null, 100);

      // hey, a returns valid
      assertEquals("we returned correct result", ds, validA);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);
      assertEquals("we've got the correct behaviour", seq._currentDecision, 0);

      // ditch valid behaviour for A, to switch to B
      da.myRes = null;

      ds = seq.decide(null, null, null, null, null, 100);

      // hey, a returns valid
      assertEquals("we returned correct result", ds, validB);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);
      assertEquals("we've got the correct behaviour", seq._currentDecision, 1);

      // give A it's behaviour back, to check it gets ignored
      da.myRes = validA;

      ds = seq.decide(null, null, null, null, null, 100);

      // hey, a returns valid
      assertEquals("we returned correct result", ds, validB);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);
      assertEquals("we've got the correct behaviour", seq._currentDecision, 1);

      // check behaviours cascade down to D
      db.myRes = null;
      dd.myRes = validD;

      ds = seq.decide(null, null, null, null, null, 100);

      // hey, a returns valid
      assertEquals("we returned correct result", ds, validD);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);
      assertEquals("we've got the correct behaviour", seq._currentDecision, 3);

      // and check we can cascade back to the start
      dd.myRes = null;

      // ensure we're set to stay alive
      seq.setStayAlive(true);

      ds = seq.decide(null, null, null, null, null, 100);

      // hey, a returns valid
      assertEquals("we returned correct result", ds, null);
      assertEquals("we've stayed alive", seq.getIsAlive(), true);
      assertEquals("we've got the correct behaviour", seq._currentDecision, 0);

      // check they restarted
      assertTrue("a has restarted", da.hasRestarted);
      assertTrue("b has restarted", db.hasRestarted);
      assertTrue("c has restarted", dc.hasRestarted);

      // put some data back int
      da.myRes = validA;
      db.myRes = validB;
      dd.myRes = validD;

      // move forward a single step (to cancel the has restarted flags)
      ds = seq.decide(null, null, null, null, null, 100);

      assertEquals("a has restarted", da.hasRestarted, false);

      // finally, try the reset
      seq.restart();
      assertTrue("a has restarted", da.hasRestarted);
      assertTrue("b has restarted", db.hasRestarted);
      assertTrue("c has restarted", dc.hasRestarted);

    }


  }


}