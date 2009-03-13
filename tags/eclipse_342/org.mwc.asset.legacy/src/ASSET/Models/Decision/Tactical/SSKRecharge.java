package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public class SSKRecharge extends CoreDecision implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////////////////////


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the maximum level we let the battery reach before we recharge
   */
  private double _minLevel = 20;

  /**
   * the minimum we let the battery charge to before we stop
   */
  private double _safeLevel = 100;

  /**
   * whether we are currently charging or not
   */
  private boolean _recharging = false;

  /**
   * the speed we snort at (kts)
   */
  private double _snortSpeed = 4.0;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * types of target we avoid when snorting
   */
  private TargetType _evadeThese = null;

  //////////////////////////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////////////////////////
  public SSKRecharge()
  {
    super("SSK Recharge");
  }


  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
                                                  ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {
    SimpleDemandedStatus res = null;

    // is our battery in trouble?
    final double fuelLevel = status.getFuelLevel();

    // what's our depth?
    final double height = -status.getLocation().getDepth();
//    final double spd = status.getSpeed().getValueIn(WorldSpeed.M_sec);

    // yes, we're charging, decide if we are in trouble
    boolean _inTrouble = false;

    // first see if there are any hostile contacts
    if (_evadeThese != null)
    {
      // look through detections for threat contact
      for (int i = 0; i < detections.size(); i++)
      {
        final ASSET.Models.Detection.DetectionEvent det = detections.getDetection(i);

        final ASSET.Participants.Category target = det.getTargetType();

        if (_evadeThese.matches(target))
        {
          _inTrouble = true;
          break; // drop out, we've seen enough
        } // whether this target is a hostile one
      } // looping through the detections
    } // whether we have a hostile contact list

    // so, are we in trouble?
    if (_inTrouble)
    {
      res = null;
      _recharging = false;
    }
    else
    {
      // are we currently charging?
      if ((height >= ASSET.Models.Vessels.SSK.CHARGE_HEIGHT))
      {
        // do we need to continue snorting?
        if (fuelLevel >= _safeLevel)
        {
          // box is full, drop out
          res = null;
          _recharging = false;
        }
        else
        {
          // we still need to snort
          res = new SimpleDemandedStatus(time, status);
          res.setSpeed(_snortSpeed);

          _recharging = true;
        }
      }
      else
      {
        // we are not currently charging, look at our box level
        if (fuelLevel <= _minLevel)
        {
          // we need to charge up, head for charge depth
          res = new SimpleDemandedStatus(time, status);
          res.setHeight(ASSET.Models.Vessels.SSK.CHARGE_HEIGHT);
          res.setSpeed(_snortSpeed);
          _recharging = false;
        } // whether we still need to snort
      } // whether we are already snorting
    } // whether we are in trouble

    // and do the name
    String activity = "";
    if (_recharging)
      activity = getName();
    else
      activity = "Heading to PD";

    super.setLastActivity(activity);

    return res;
  }

  public void restart()
  {
    //
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
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new SSKRechargeInfo(this);

    return _myEditor;
  }

  /**
   * the level we decide to do a recharge at
   */
  public void setMinLevel(final double newMinLevel)
  {
    _minLevel = newMinLevel;
  }

  /**
   * the level we decide to do a recharge at
   */
  public double getMinLevel()
  {
    return _minLevel;
  }

  /**
   * the types of target we avoid when snorting
   */
  public void setTargetToEvade(final ASSET.Models.Decision.TargetType target)
  {
    _evadeThese = target;
  }

  /**
   * the types of target we avoid when snorting
   */
  public ASSET.Models.Decision.TargetType getTargetToEvade()
  {
    if (_evadeThese == null)
      _evadeThese = new TargetType();

    return _evadeThese;
  }

  /**
   * the level we decide we are ok to continue
   */
  public void setSafeLevel(final double newSafeLevel)
  {
    _safeLevel = newSafeLevel;
  }

  /**
   * the level we decide we are ok to continue
   */
  public double getSafeLevel()
  {
    return _safeLevel;
  }

  /**
   * the speed we travel at when snorting (kts)
   */
  public void setSnortSpeed(final WorldSpeed newSnortSpeed)
  {
    _snortSpeed = newSnortSpeed.getValueIn(WorldSpeed.M_sec);
  }

  /**
   * the speed we travel at when snorting (kts)
   */
  public WorldSpeed getSnortSpeed()
  {
    return new WorldSpeed(_snortSpeed, WorldSpeed.M_sec);
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: SSKRecharge.java,v $
   * Revision 1.1  2006/08/08 14:21:37  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:46  Ian.Mayo
   * First versions
   *
   * Revision 1.17  2004/11/12 16:21:10  Ian.Mayo
   * Remove StayAlive parameter for Waterfall
   *
   * Revision 1.16  2004/09/02 13:17:37  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.15  2004/08/26 14:09:50  Ian.Mayo
   * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
   * <p/>
   * Revision 1.14  2004/08/25 11:20:46  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.13  2004/08/20 13:32:35  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.12  2004/08/17 14:22:11  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.11  2004/08/09 15:50:36  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.10  2004/08/06 12:52:08  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.9  2004/08/06 11:14:30  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.8  2004/05/24 15:57:17  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.7  2004/02/18 08:48:11  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.5  2003/11/05 09:19:57  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  static public class SSKRechargeInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public SSKRechargeInfo(final SSKRecharge data)
    {
      super(data, data.getName(), "Recharge");
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
          prop("TargetToEvade", "the types of target we avoid when snorting"),
          prop("SafeLevel", "the battery level at which we stop recharging"),
          prop("MinLevel", "the battery level at which we start recharging"),
          prop("SnortSpeed", "the speed at which this vessel snorts"),
          prop("Name", "the name of this SSK Recharge model"),

        };
        //     res[0].setPropertyEditorClass(ASSET.GUI.Editors.TargetTypeEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }

  /**
   * *************************************************
   * testing
   * *************************************************
   */
  static public class testMe extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
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
      final SSKRecharge recharge = new SSKRecharge();
      return recharge;
    }

    /**
     * test we decide to snort correctly
     */
    public void testSnorting()
    {
      // setup our scenario
      final SSKRecharge recharge = new SSKRecharge();
      recharge.setMinLevel(20);
      recharge.setSafeLevel(80);
      recharge.setSnortSpeed(new WorldSpeed(6, WorldSpeed.M_sec));
      recharge.setTargetToEvade(null);

      // check when we've no target to avoid
      final Status theStat = new Status(12, 0);
      theStat.setCourse(12);
      theStat.setFuelLevel(100);
      theStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      theStat.setLocation(new WorldLocation(12, 12, 33));

      final Category friendly = new Category(Category.Force.RED, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE);

      // see what the plan is
      DemandedStatus de = recharge.decide(theStat, null, null, null, null, 12);

      // check we don't need to recharge
      assertEquals("Check we don't need to recharge with full box", de, null);

      // check we need to recharge at limit
      theStat.setFuelLevel(20);
      de = recharge.decide(theStat, null, null, null, null, 12);
      assertNotNull("Check we recharge at limit", de);

      // check we need to recharge under limit
      theStat.setFuelLevel(18);
      de = recharge.decide(theStat, null, null, null, null, 12);
      assertNotNull("Check we recharge under limit", de);

      // check we recharge with friendly contacts
      final DetectionList dl = new DetectionList();
      final ASSET.Models.Sensor.Initial.OpticSensor optic = new ASSET.Models.Sensor.Initial.OpticSensor(12);
      final CoreParticipant cp = new CoreParticipant(12);
      final DetectionEvent dEvent = new DetectionEvent(0,
                                                       cp.getId(), null,
                                                       optic,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       friendly,
                                                       null,
                                                       null,
                                                       cp);
      dl.add(dEvent);
      // set the avoid category to blue vessels
      recharge.setTargetToEvade(new TargetType(Category.Force.BLUE));

      // see what happens now
      de = recharge.decide(theStat, null, null, dl, null, 12);
      assertNotNull("Check don't avoid friendly", de);

      // see about avoiding hostile vessels
      // change it so we want to avoid friendly vessels
      recharge.setTargetToEvade(new TargetType(Category.Force.RED));
      de = recharge.decide(theStat, null, null, dl, null, 12);
      assertNull("Check don't snort with hostile", de);

      theStat.setFuelLevel(80);
      recharge.setTargetToEvade(new TargetType(Category.Force.BLUE));
      de = recharge.decide(theStat, null, null, dl, null, 12);
      assertNull("Check don't snort when not needed", de);
    }


  }
}