package ASSET.Models.Decision.Tactical;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: August 2003
 * Log:
 *  $Log: MarkDip.java,v $
 *  Revision 1.1  2006/08/08 14:21:36  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:45  Ian.Mayo
 *  First versions
 *
 *  Revision 1.24  2004/11/01 15:54:55  Ian.Mayo
 *  Reflect new signature of Track Plot Observer
 *
 *  Revision 1.23  2004/08/31 09:36:26  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.22  2004/08/26 16:27:07  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.21  2004/08/25 11:20:42  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.20  2004/08/20 13:32:33  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.19  2004/08/17 14:22:09  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.18  2004/08/12 11:09:25  Ian.Mayo
 *  Respect observer classes refactored into tidy directories
 *
 *  Revision 1.17  2004/08/09 15:50:34  Ian.Mayo
 *  Refactor category types into Force, Environment, Type sub-classes
 *
 *  Revision 1.16  2004/08/06 12:52:06  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.15  2004/08/06 11:14:28  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.14  2004/05/24 15:57:14  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.2  2004/04/08 20:27:17  ian
 *  Restructured contructor for CoreObserver
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *
 *  Revision 1.13  2004/02/18 08:48:11  Ian.Mayo
 *  Sync from home
 *
 *  Revision 1.11  2003/11/05 09:19:55  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.10  2003/09/19 13:37:54  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.9  2003/09/18 14:11:51  Ian.Mayo
 *  Make tests work with new World Speed class
 *
 *  Revision 1.8  2003/09/11 13:40:42  Ian.Mayo
 *  Classes moved around
 *
 *  Revision 1.7  2003/09/09 15:55:46  Ian.Mayo
 *  Change signature of decision model
 *
 *  Revision 1.6  2003/09/04 15:17:59  Ian.Mayo
 *  Switch to optional parameter values for Move
 *
 *
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Movement.Move;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

/**
 * If we are in the hover, and the dipping sensor is housed, deploy it.
 */
public class MarkDip extends CoreDecision implements Serializable
{

  /**
   * the depth to lower the body to (metres)
   */
  private double _bodyDepth = 0;

  /**
   * constructor
   *
   * @param bodyDepth
   */
  public MarkDip(WorldDistance bodyDepth)
  {
    super("Mark Dip");
    this._bodyDepth = bodyDepth.getValueIn(WorldDistance.METRES);
  }

  /**
   * decide the course of action to take, or return null to no be used
   *
   * @param status     the current status of the participant
   * @param detections the current list of detections for this participant
   * @param monitor    the object which handles weapons release/detonation
   * @param newTime    the time this decision is to be made
   */
  public DemandedStatus decide(Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               long newTime)
  {
    DemandedStatus res = null;

    String activity = "";

    // check we're in the hover
    if (status.is(Helo.HoverStates.IN_HOVER))
    {
      // is the body deployed yet?
      if (!status.is(DippingActiveBroadbandSensor.ADS_AT_DEPTH))
      {
        // no, still not at depth - make another request
        res = new SimpleDemandedStatus(newTime, status);
        res.set(DippingActiveBroadbandSensor.ADS_DEPLOYED);
        DemandedSensorStatus dss = new DemandedSensorStatus(EnvironmentType.BROADBAND_ACTIVE, true);
        dss.setDeployDepth(new Double(_bodyDepth));
        res.add(dss);
        activity = "Still lowering";
      }
      else
      {
        // ok - we've finished - just let us return null

      }
    }
    else
    {
      // we're still not in hover - request that we do
      SimpleDemandedStatus sds = new SimpleDemandedStatus(newTime, status);
      sds.setSpeed(0);
      res = sds;
      res.set(Helo.HoverStates.IN_HOVER);
      activity = "Move to hover";
    }

    super.setLastActivity(activity);

    return res;
  }

  public WorldDistance getBodyDepth()
  {
    return new WorldDistance(_bodyDepth, WorldDistance.METRES);
  }

  public void setBodyDepth(WorldDistance bodyDepth)
  {
    this._bodyDepth = bodyDepth.getValueIn(WorldDistance.METRES);
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
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
      _myEditor = new MarkDipInfo(this);

    return _myEditor;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class MarkDipInfo extends EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public MarkDipInfo(final MarkDip data)
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
          prop("BodyDepth", "the depth to lower the buoy to"),
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
   * $Log: MarkDip.java,v $
   * Revision 1.1  2006/08/08 14:21:36  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:45  Ian.Mayo
   * First versions
   *
   * Revision 1.24  2004/11/01 15:54:55  Ian.Mayo
   * Reflect new signature of Track Plot Observer
   *
   * Revision 1.23  2004/08/31 09:36:26  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   *
   * Revision 1.22  2004/08/26 16:27:07  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.21  2004/08/25 11:20:42  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.20  2004/08/20 13:32:33  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.19  2004/08/17 14:22:09  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.18  2004/08/12 11:09:25  Ian.Mayo
   * Respect observer classes refactored into tidy directories
   * <p/>
   * Revision 1.17  2004/08/09 15:50:34  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.16  2004/08/06 12:52:06  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.15  2004/08/06 11:14:28  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.14  2004/05/24 15:57:14  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.2  2004/04/08 20:27:17  ian
   * Restructured contructor for CoreObserver
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.13  2004/02/18 08:48:11  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.11  2003/11/05 09:19:55  Ian.Mayo
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
  static public class MarkDipTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public MarkDipTest(final String val)
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
      MarkDip md = new MarkDip(new WorldDistance(430, WorldDistance.METRES));
      return md;
    }

    public void testLower()
    {
      MarkDip md = new MarkDip(new WorldDistance(430, WorldDistance.METRES));
      md.setName("Dip at 430m");

      Helo hel = new Helo(12);

      DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
      Status st = new Status(11, 0);
      st.setLocation(createLocation(0, 0));
      st.getLocation().setDepth(-100);
      st.setSpeed(new WorldSpeed(1, WorldSpeed.M_sec));
      double waterLowerRate = 4;
      double airLowerRate = 6;
      double waterRaiseRate = 1;
      double airRaiseRate = 3;
      double lowerPause = 3;
      double raisePause = 5;

      DippingActiveBroadbandSensor dip =
        new DippingActiveBroadbandSensor(12,
                                         new WorldSpeed(airLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
                                         new Duration(lowerPause, Duration.SECONDS),
                                         new Duration(raisePause, Duration.SECONDS),
                                         new WorldSpeed(waterLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

      hel.addSensor(dip);
      hel.setDemandedStatus(ds);
      hel.setStatus(st);
      hel.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

      String myName = "Merlin Trial";
      double accelRate = 10;
      double decelRate = 25;
      double fuel_usage_rate = 0;
      double maxSpeed = 100;
      double minSpeed = -5;
      double defaultClimbRate = 15;
      double defaultDiveRate = 15;
      double maxHeight = 400;
      double minHeight = 0;
      double myTurnRate = 3;
      double defaultClimbSpeed = 15;
      double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.HeloMovementCharacteristics(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);
      hel.setMovementChars(chars);

      CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, hel);

      Sequence sq = new Sequence();
      sq.insertAtHead(md);
      Move mv = new Move();
      mv.setCourse(new Double(0));
      mv.setSpeed(new WorldSpeed(100, WorldSpeed.M_sec));
      mv.setDistance(new WorldDistance(12, WorldDistance.KM));
      mv.setName("travel to location");

      Move mv2 = new Move();
      mv2.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
      mv2.setCourse(new Double(12));
      mv2.setName("go into hover");

      sq.insertAtHead(mv2);
      sq.insertAtHead(mv);

      hel.setDecisionModel(sq);
      cs.setScenarioStepTime(2000);

      TrackPlotObserver tpo = new TrackPlotObserver("./", 400, 400, "mark_dip.png", null, true, true, false, "test observer", true);
      tpo.setup(cs);
      DebriefReplayObserver dbo = new DebriefReplayObserver("./", "mark_dip.rep", false, "test observer", true);
      dbo.setup(cs);

      for (int i = 0; i < 190; i++)
      {
        cs.step();
        System.out.println("status:" + hel.getActivity() + " len:" + dip.getCableLength());
      }

      tpo.tearDown(cs);
      dbo.tearDown(cs);

      assertEquals("sensor correctly deployed", 530, dip.getCableLength(), 0.0d);


    }
  }
}
