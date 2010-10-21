package ASSET.Models.Decision.Movement;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Models.Movement.WaypointVisitor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * behaviour to travel through a series of points at an indicated speed. Optionally the journey
 * may be repeated, in forward or reverse direction.
 */
public class TransitWaypoint extends CoreDecision implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // Member Variables
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * loop through destinations
   */
  private boolean _loop = true;

  /**
   * when we are looping, whether we go back to start, or
   * pass through them in reverse;
   */
  private boolean _inReverse = true;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * whether this represents a single transit, and is now complete
   */
  private boolean _transit_complete = false;

  /**
   * the route we travel through
   */
  private HighLevelDemandedStatus _demStatus = null;

  /**
   * our name.
   */
  private static final String WAYPOINT_NAME = "Transit Waypoint";

  //////////////////////////////////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////////////////////////////////


  public TransitWaypoint(final WorldPath destinations,
                         final WorldSpeed transit_speed,
                         final boolean loop,
                         final WaypointVisitor visitor)
  {
    super(WAYPOINT_NAME);
    _loop = loop;

    // collate the status object
    _demStatus = new HighLevelDemandedStatus(-1, -1, 0,
                                             destinations,
                                             visitor,
                                             transit_speed);
  }


  public TransitWaypoint()
  {
    super(WAYPOINT_NAME);
    _demStatus = null;
    _loop = true;
    _demStatus = new HighLevelDemandedStatus(-1, -1);
  }


  //////////////////////////////////////////////////////////////////////
  // Member methods
  //////////////////////////////////////////////////////////////////////

  public DemandedStatus decide(final Status status,
                               MovementCharacteristics chars,
                               DemandedStatus demStatus,
                               ASSET.Models.Detection.DetectionList detections,
                               ASSET.Scenario.ScenarioActivityMonitor monitor,
                               final long newTime)
  {

    DemandedStatus res = null;

    String thisActivity;

    if (_transit_complete)
    {
      thisActivity = "Transit complete";
      return res;
    }

    // are we already up and running?
    if (_demStatus.getTime() != -1)
    {
      // yes, have we finished
      if (_demStatus.getCurrentTarget() == null)
      {
        // yes, do we want to go around again?
        if (_loop)
        {
          // yes, do we want to go in reverse?
          if (_inReverse)
          {
            // are we already running in reverse?
            if (_demStatus.getRunInReverse())
            {
              // ok, set to run forwards
              _demStatus.setRunInReverse(false);
              _demStatus.setCurrentTargetIndex(1);
              thisActivity = "Starting to run forwards again from start";
            }
            else
            {
              // ok. we've finished the route, and are going to run back in reverse. set
              // the current target to be the last but one.
              _demStatus.setRunInReverse(true);
              _demStatus.setCurrentTargetIndex(_demStatus.size() - 2);
              thisActivity = "Starting to run backwards from end";
            }
          }
          else
          {
            // no, we want to run forward
            // ok, set to run forwards
            _demStatus.setRunInReverse(false);
            _demStatus.setCurrentTargetIndex(1);
            thisActivity = "Starting to run forwards again from start";

          }

          // ok, just return our dem status object
          res = _demStatus;
          res.setTime(newTime);
          //         thisActivity = "Starting to run forwards again from start";

        }
        else
        {
          // no, we don't want to go around again.
          res = null;
          _transit_complete = true;
          thisActivity = "Transit complete";
        }
      }
      else
      {
        // no, our route isn't marked as finished - just carry on
        // ok, just return our dem status object

        res = _demStatus;
        res.setTime(newTime);
        thisActivity = " Continuing to waypoint:" + (_demStatus.getCurrentTargetIndex() + 1);
      }
    } // whether our demanded status object is already in use
    else
    {
      // configure and use our demanded status objet
      // ok, just return our dem status object

      _demStatus.setCurrentTargetIndex(0);
      _demStatus.setRunInReverse(false);
      thisActivity = "Starting to run towards first waypoint";

      res = _demStatus;
      res.setTime(newTime);

    }

    super.setLastActivity(thisActivity);

    return res;

  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    _transit_complete = false;
    _demStatus.setCurrentTargetIndex(0);
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
      _myEditor = new TransitInfo(this);

    return _myEditor;
  }

  /**
   * whether we loop through the destinations, switching
   * to the first from the last
   */
  public void setLoop(final boolean newLoop)
  {
    _loop = newLoop;
  }

  /**
   * whether we loop through the destinations, switching
   * to the first from the last
   */
  public boolean getLoop()
  {
    return _loop;
  }

  /**
   * when we are looping, whether we go back to the start, or if we
   * pass through the destinations in reverse
   */
  public void setReverse(final boolean val)
  {
    _inReverse = val;
  }


  /**
   * when we are looping, whether we go back to the start, or if we
   * pass through the destinations in reverse
   */
  public boolean getReverse()
  {
    return _inReverse;
  }

  /**
   * the speed we transit at (m_sec)
   */
  public void setSpeed(final WorldSpeed newSpeed)
  {
    _demStatus.setSpeed(newSpeed);

  }

  /**
   * the speed we transit at (kts)
   */
  public WorldSpeed getSpeed()
  {
    return _demStatus.getSpeed();
  }

  /**
   * the set of destinations we follow
   */
  public void setDestinations(final WorldPath newDestinations)
  {
    _demStatus.setPath(newDestinations);
  }

  /**
   * the set of destinations we follow
   */
  public WorldPath getDestinations()
  {
    return _demStatus.getPath();
  }


  public WaypointVisitor getVisitor()
  {
    return _demStatus.getVisitType();
  }

  public boolean isFinished()
  {
    return _transit_complete;
  }


  public int getCurrentDestinationIndex()
  {
    return _demStatus.getCurrentTargetIndex();
  }

  static public class TransitInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public TransitInfo(final TransitWaypoint data)
    {
      super(data, data.getName(), "Transit");
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
          prop("Destinations", "the list of destinations we follow"),
          prop("Loop", "whether we loop back to the first point"),
          prop("Reverse", "when we loop, whether we follow the points in reverse order"),
          prop("Speed", "the speed we transit at (kts)"),
          prop("Name", "the name of this transit model"),
        };
        res[0].setPropertyEditorClass(MWC.GUI.Properties.Swing.SwingWorldPathPropertyEditor.class);

        return res;
      }
      catch (java.beans.IntrospectionException e)
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
   * $Log: TransitWaypoint.java,v $
   * Revision 1.1  2006/08/08 14:21:29  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:37  Ian.Mayo
   * First versions
   *
   * Revision 1.29  2005/04/15 14:11:57  Ian.Mayo
   * Update tests to reflect new scenario step cycle
   *
   * Revision 1.28  2004/11/01 15:54:53  Ian.Mayo
   * Reflect new signature of Track Plot Observer
   * <p/>
   * Revision 1.27  2004/09/02 13:17:28  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.26  2004/08/31 09:36:08  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.25  2004/08/26 16:26:54  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.24  2004/08/25 11:20:22  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.23  2004/08/20 13:32:21  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.22  2004/08/17 14:21:59  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.21  2004/08/16 09:16:13  Ian.Mayo
   * Respect changed processing of tester recording to file (it needed a valid scenario object)
   * <p/>
   * Revision 1.20  2004/08/12 11:09:19  Ian.Mayo
   * Respect observer classes refactored into tidy directories
   * <p/>
   * Revision 1.19  2004/08/09 15:50:27  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.18  2004/08/06 12:51:56  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.17  2004/08/06 11:14:18  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.16  2004/05/24 15:46:39  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.2  2004/04/08 20:27:17  ian
   * Restructured contructor for CoreObserver
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.15  2004/02/18 08:47:13  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.13  2003/12/10 16:15:40  Ian.Mayo
   * More tests
   * <p/>
   * Revision 1.12  2003/12/08 13:17:50  Ian.Mayo
   * Update testing for completed OnTop alg
   * <p/>
   * Revision 1.11  2003/11/19 15:48:47  Ian.Mayo
   * minor tidying
   * <p/>
   * Revision 1.10  2003/11/19 10:10:12  Ian.Mayo
   * Clear demanded status at end of route
   * <p/>
   * Revision 1.9  2003/11/05 09:20:11  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  /**
   * **********************************************************************
   * test this class
   * **********************************************************************
   */

  public static class TransitWaypointTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public TransitWaypointTest(final String name)
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
      MWC.GUI.Editable ed = new TransitWaypoint();
      return ed;
    }

    public void testNonLoopRoute()
    {

      WorldPath wp = new WorldPath();
      wp.addPoint(createLocation(000, 600));
      wp.addPoint(createLocation(400, 600));
      wp.addPoint(createLocation(600, 200));
      wp.addPoint(createLocation(1000, 200));
      wp.addPoint(createLocation(400, 300));
      wp.addPoint(createLocation(600, 800));

      OnTopWaypoint otw = new OnTopWaypoint();

      TransitWaypoint t2 = new TransitWaypoint(wp, null, false, otw);


      String myName = "Merlin Trial";
      double accelRate = 10;
      double decelRate = 25;
      double fuel_usage_rate = 0;
      double maxSpeed = 100;
      double minSpeed = -5;
      double defaultClimbRate = 15;
      double defaultDiveRate = 15;
      double maxHeight = 0;
      double minHeight = -400;
      double myTurnRate = 3;
      double defaultClimbSpeed = 15;
      double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
         ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      Status current = new Status(1, 100);
      current.setLocation(new WorldLocation(0.00, 0.00, 0));
      current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
      current.setCourse(0);

      Helo helo = new Helo(12);
      helo.setName("Merlin_Test");
      helo.setMovementChars(chars);
      helo.setStatus(current);
      helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      helo.setDecisionModel(t2);


      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(1000);
      cs.addParticipant(12, helo);

      super.startListeningTo(helo, "testNonLoopRoute", true, true, true, cs);

      for (long newTime = 0; newTime < 600000; newTime += 1000)
      {
        cs.step();
        newTime = cs.getTime();
      }

      super.endRecording(cs);

      super.outputTheseToRep("testNonLoopRoute_pts.rep", wp);

      // ok - check where we are
      WorldLocation finalLocation = helo.getStatus().getLocation();
      WorldLocation targetLocation = createLocation(2319, 1515);
      double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
      outputLocation(finalLocation);
      assertTrue("near target - error: " + (int) range_offset, range_offset <= 10);

    }

    public void testReverseLoopRoute()
    {

      WorldPath wp = new WorldPath();
      wp.addPoint(createLocation(000, 6000));
      wp.addPoint(createLocation(4000, 6000));
      wp.addPoint(createLocation(6000, 2000));
      wp.addPoint(createLocation(10000, 2000));
      wp.addPoint(createLocation(4000, 3000));
      wp.addPoint(createLocation(6000, 8000));

      OnTopWaypoint otw = new OnTopWaypoint();

      TransitWaypoint t2 = new TransitWaypoint(wp, null, true, otw);


      String myName = "Merlin Trial";
      double accelRate = 10;
      double decelRate = 25;
      double fuel_usage_rate = 0;
      double maxSpeed = 100;
      double minSpeed = -5;
      double defaultClimbRate = 15;
      double defaultDiveRate = 15;
      double maxDepth = 400;
      double minDepth = 0;
      double myTurnRate = 3;
      double defaultClimbSpeed = 15;
      double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
      	ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxDepth,
                                                              minDepth, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      Status current = new Status(1, 100);
      current.setLocation(new WorldLocation(0.00, 0.00, 0));
      current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
      current.setCourse(0);


      Helo helo = new Helo(12);
      helo.setName("Merlin_Test");
      helo.setMovementChars(chars);
      helo.setStatus(current);
      helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      helo.setDecisionModel(t2);


      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(1000);
      cs.addParticipant(12, helo);

      super.startListeningTo(helo, "testReverseLoopRoute", true, true, true, cs);

      for (int newTime = 0; newTime < 11000000; newTime += 1000)
      {
        cs.step();
      }

      super.endRecording(cs);
      super.outputTheseToRep("testReverseLoopRoute_pts.rep", wp);

      // ok - check where we are
      WorldLocation finalLocation = helo.getStatus().getLocation();
      WorldLocation targetLocation = createLocation(4207, 5860);
      double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
      outputLocation(finalLocation);

      //  todo  find out why we're looping around again at point 6'
      assertTrue("near target - range:" + range_offset, range_offset < 12);

    }

    public void testShortLoopRoute()
    {

      WorldPath wp = new WorldPath();
      wp.addPoint(createLocation(10000, 2000));
      wp.addPoint(createLocation(4000, 3000));
      wp.addPoint(createLocation(6000, 8000));

      OnTopWaypoint otw = new OnTopWaypoint();

      TransitWaypoint t2 = new TransitWaypoint(wp, null, true, otw);


      String myName = "Merlin Trial";
      double accelRate = 10;
      double decelRate = 25;
      double fuel_usage_rate = 0;
      double maxSpeed = 100;
      double minSpeed = -5;
      double defaultClimbRate = 15;
      double defaultDiveRate = 15;
      double maxDepth = 400;
      double minDepth = 0;
      double myTurnRate = 3;
      double defaultClimbSpeed = 15;
      double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
      	ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxDepth,
                                                              minDepth, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      Status current = new Status(1, 100);
      current.setLocation(new WorldLocation(0.00, 0.00, 0));
      current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
      current.setCourse(0);


      Helo helo = new Helo(12);
      helo.setName("Merlin_Test");
      helo.setMovementChars(chars);
      helo.setStatus(current);
      helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      helo.setDecisionModel(t2);


      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(1000);
      cs.addParticipant(12, helo);

      super.startListeningTo(helo, "testShortLoopRoute", true, true, true, cs);

      for (int newTime = 0; newTime < 2600000; newTime += 1000)
      {
        cs.step();
        if (newTime == 2202000)
          System.out.println("here");
      }

      super.endRecording(cs);
      super.outputTheseToRep("testShortLoopRoute_pts.rep", wp);

      // ok - check where we are
      WorldLocation finalLocation = helo.getStatus().getLocation();
      WorldLocation targetLocation = createLocation(4861, 4772);
      double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
      outputLocation(finalLocation);

      //  todo  find out why we're looping around again at point 6'
      assertTrue("near target - range:" + range_offset, range_offset < 11);

    }


    public void testForwardLoopRoute()
    {

      WorldPath wp = new WorldPath();
      wp.addPoint(createLocation(000, 6000));
      wp.addPoint(createLocation(4000, 6000));
      wp.addPoint(createLocation(6000, 2000));
      wp.addPoint(createLocation(10000, 2000));
      wp.addPoint(createLocation(4000, 3000));
      wp.addPoint(createLocation(6000, 8000));

      OnTopWaypoint otw = new OnTopWaypoint();

      TransitWaypoint t2 = new TransitWaypoint(wp, null, true, otw);
      t2.setReverse(false);


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
      	ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      Status current = new Status(1, 100);
      current.setLocation(new WorldLocation(0.00, 0.00, 0));
      current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
      current.setCourse(0);

      TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400, "transit2b.png",
                                                    new WorldDistance(100, WorldDistance.METRES),
                                                    false, true, false, "test observer", true);
      CoreParticipant cp = OnTopWaypoint.createTestHelo();
      cp.setDecisionModel(t2);
      cp.setMovementChars(chars);
      cp.setStatus(current);

      ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver("./test_reports", null, false, "test observer", true);

      CoreScenario cs = new CoreScenario();
      cs.addParticipant(cp.getId(), cp);
      cs.setScenarioStepTime(1000);

      tpo.setup(cs);
      dr.setup(cs);


      for (int newTime = 0; newTime < 11000000; newTime += 1000)
      {
        cs.step();
      }


      super.outputTheseToRep("testForwardLoopRoute_pts.rep", wp);

      tpo.tearDown(cs);
      dr.tearDown(cs);

      // ok - check where we are
      WorldLocation finalLocation = cp.getStatus().getLocation();
      outputLocation(finalLocation);
      WorldLocation targetLocation = createLocation(6948, 1915);
      double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
      assertEquals("near target", 0, range_offset, 10d);

    }

    public void testAcceleratingForwardLoopRoute()
    {

      WorldPath wp = new WorldPath();
      wp.addPoint(createLocation(000, 6000));
      wp.addPoint(createLocation(4000, 6000));
      wp.addPoint(createLocation(6000, 2000));
      wp.addPoint(createLocation(10000, 2000));
      wp.addPoint(createLocation(4000, 3000));
      wp.addPoint(createLocation(6000, 8000));

      OnTopWaypoint otw = new OnTopWaypoint();

      TransitWaypoint t2 = new TransitWaypoint(wp, new WorldSpeed(62, WorldSpeed.M_sec), true, otw);
      t2.setReverse(false);


      String myName = "Merlin Trial";
      double accelRate = 0.6;
      double decelRate = 0.5;
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
      	ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      Status current = new Status(1, 100);
      current.setLocation(new WorldLocation(0.00, 0.00, 0));
      current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
      current.setCourse(0);

      TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400, "testAcceleratingForwardLoopRoute.png",
                                                    new WorldDistance(100, WorldDistance.METRES),
                                                    false, true, false, "test observer", true);
      CoreParticipant cp = OnTopWaypoint.createTestHelo();
      cp.setDecisionModel(t2);
      cp.setMovementChars(chars);
      cp.setStatus(current);

      ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver("./test_reports", "testAcceleratingForwardLoopRoute.rep", false, "test observer", true);
      ASSET.Scenario.Observers.Recording.CSVTrackObserver csv = new CSVTrackObserver("./test_reports", "testAcceleratingForwardLoopRoute.csv", false, "test observer", true);

      CoreScenario cs = new CoreScenario();
      cs.addParticipant(cp.getId(), cp);
      cs.setScenarioStepTime(1000);

      tpo.setup(cs);
      dr.setup(cs);

      csv.setup(cs);


      for (int newTime = 0; newTime < 1200000; newTime += 1000)
      {
        cs.step();
      }

      tpo.tearDown(cs);
      dr.tearDown(cs);
      csv.tearDown(cs);

      // ok, now output the points
      super.outputTheseToRep("testAcceleratingForwardLoopRoute_points.rep", wp);

      // ok - check where we are
      WorldLocation finalLocation = cp.getStatus().getLocation();
      outputLocation(finalLocation);
      WorldLocation targetLocation = createLocation(6518, 8322);
      double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
      assertEquals("near target - range error:" + range_offset + " pos:" + toXYString(finalLocation), 0, range_offset, 10);

    }

  }

}