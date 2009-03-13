package ASSET.Models.Movement;

/**
 * Title:        ASSET Simulator
 * Description:  Advanced Scenario Simulator for Evaluation of Tactics
 * Copyright:    Copyright (c) 2001
 * Company:      PlanetMayo Ltd
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Models.MovementType;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * high level class which converts high level demanded statuses into the detailed once necessary for the
 * turn algorith, where applicable
 */
public class CoreMovement implements MovementType, java.io.Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * the turn algorithm we're using
   */
  private static TurnAlgorithm _turner = new TurnAlgorithm();


  /**
   * move platform forward one time step
   *
   * @param newTime        the current scenario time
   * @param currentStatus  the current platform status
   * @param demandedStatus the current demanded status of the platform
   * @param moves          a set of movement characteristics for this particular platform
   * @return the updated status
   */

  public Status step(final long newTime,
                     final Status currentStatus,
                     final DemandedStatus demandedStatus,
                     final MovementCharacteristics moves)
  {

    Status res = null;

    SimpleDemandedStatus simpleDem = null;

    // see if we have a high level demanded status
    if (demandedStatus instanceof HighLevelDemandedStatus)
    {
      // switch us back to a high level dem status
      HighLevelDemandedStatus hl = (HighLevelDemandedStatus) demandedStatus;

      // ok, see what the simple demanded status would be
      final WaypointVisitor visitType = hl.getVisitType();

      // and determine the path
      res = visitType.step(hl,
                           currentStatus,
                           newTime,
                           moves,
                           _turner);
    }
    else
    {
      // hey, it's just a simple one - go for it!
      simpleDem = (SimpleDemandedStatus) demandedStatus;

      // now pass the dem status to the turn algorithm
      res = _turner.doTurn(currentStatus,
                           simpleDem, moves, newTime);
    }


    return res;
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: CoreMovement.java,v $
   * Revision 1.2  2006/09/14 14:11:10  Ian.Mayo
   * Source tidying
   *
   * Revision 1.1  2006/08/08 14:21:46  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:54  Ian.Mayo
   * First versions
   *
   * Revision 1.19  2004/11/01 15:54:57  Ian.Mayo
   * Reflect new signature of Track Plot Observer
   *
   * Revision 1.18  2004/08/31 09:36:40  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   *
   * Revision 1.17  2004/08/25 11:20:54  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.16  2004/08/16 10:10:36  Ian.Mayo
   * Correctly set dummy scenario for track plot observer
   * <p/>
   * Revision 1.15  2004/08/16 09:32:53  Ian.Mayo
   * Respect changed processing of tester recording to file (it needed a valid scenario object)
   * <p/>
   * Revision 1.14  2004/08/09 15:50:38  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.13  2004/05/24 15:09:00  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.2  2004/04/08 20:27:18  ian
   * Restructured contructor for CoreObserver
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:53  ian
   * no message
   * <p/>
   * Revision 1.12  2003/11/05 09:19:18  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  /**
   * *******************************************************************
   * testing code
   * *******************************************************************
   */
  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class CoreMovementTest extends SupportTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public CoreMovementTest(final String val)
    {
      super(val);
    }


    public void testSimple()
    {
      CoreMovement cm = new CoreMovement();
      Status stat = new Status(0, 100);
      stat.setLocation(new WorldLocation(0, 0, 0));
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      SimpleDemandedStatus simpleStat = new SimpleDemandedStatus(200, stat);
      simpleStat.setCourse(120);
      simpleStat.setSpeed(3);

      String myName = "Merlin Trial";
      double accelRate = 10;
      double decelRate = 25;
      double fuel_usage_rate = 0;
      double maxSpeed = 100;
      double minSpeed = -5;
      double defaultClimbRate = 15;
      double defaultDiveRate = 15;
      double maxHeight = 400;
      double minHeight = 000;
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

      Status res = cm.step(300, stat, simpleStat, chars);

      assertNotNull("status received", res);
      assertEquals("time updated", 300, res.getTime());
    }

    public void testHighLevel()
    {
      CoreMovement cm = new CoreMovement();

      WorldLocation.setModel(new CompletelyFlatEarth());

      OnTopWaypoint myVisitor = new OnTopWaypoint();
      WorldPath myPath = new WorldPath();
      myPath.addPoint(createLocation(000, 600));
      myPath.addPoint(createLocation(400, 600));
      myPath.addPoint(createLocation(600, 200));
      myPath.addPoint(createLocation(1000, 200));
      myPath.addPoint(createLocation(400, 300));
      myPath.addPoint(createLocation(600, 800));
      HighLevelDemandedStatus hls = new HighLevelDemandedStatus(300, 200, 0, myPath, myVisitor, null);

      Status stat = new Status(0, 100);
      stat.setLocation(new WorldLocation(0, 0, 0));
      stat.setSpeed(new WorldSpeed(10.27, WorldSpeed.M_sec));
      stat.setCourse(0);

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


      long newTime = 4000;

      CoreScenario dummyScenario = new CoreScenario();

      TrackPlotObserver tpo = new TrackPlotObserver("./", 400, 400, "tpo.png",
                                                    new WorldDistance(100, WorldDistance.METRES),
                                                    false, true, false,  "test observer", true);
      tpo.setup(dummyScenario);
      CoreParticipant cp = new Helo(100);
      cp.setName("helo alpha");
      cp.setCategory(new Category(Category.Force.RED, Category.Environment.AIRBORNE, Category.Type.HELO));

      for (int i = 0; i < 500000; i += 3000)
      {
        stat = cm.step(newTime + i, stat, hls, chars);
        tpo.processTheseDetails(new WorldLocation(stat.getLocation()), stat, cp);
      }

      tpo.tearDown(dummyScenario);

    }

    public void testAcceleratingHighLevel()
    {
      CoreMovement cm = new CoreMovement();

      WorldLocation.setModel(new CompletelyFlatEarth());

      OnTopWaypoint myVisitor = new OnTopWaypoint();
      WorldPath myPath = new WorldPath();
      myPath.addPoint(createLocation(000, 600));
      myPath.addPoint(createLocation(400, 600));
      myPath.addPoint(createLocation(600, 200));
      myPath.addPoint(createLocation(1000, 200));
      myPath.addPoint(createLocation(400, 300));
      myPath.addPoint(createLocation(600, 800));

      HighLevelDemandedStatus hls = new HighLevelDemandedStatus(300, 200, 0,
                                                                myPath, myVisitor,
                                                                new WorldSpeed(60, WorldSpeed.M_sec));

      Status stat = new Status(0, 100);
      stat.setLocation(new WorldLocation(0, 0, 0));
      stat.setSpeed(new WorldSpeed(10.27, WorldSpeed.M_sec));
      stat.setCourse(0);

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


      long newTime = 4000;


      CoreScenario dummyScenario = new CoreScenario();

      TrackPlotObserver tpo = new TrackPlotObserver("./", 400, 400, "tpo.png",
                                                    new WorldDistance(100, WorldDistance.METRES),
                                                    false, true, false, "test observer", true);
      tpo.setup(dummyScenario);
      CoreParticipant cp = new Helo(100);
      cp.setName("helo alpha");
      cp.setCategory(new Category(Category.Force.RED, Category.Environment.AIRBORNE, Category.Type.HELO));


      for (int i = 0; i < 500000; i += 3000)
      {
        stat = cm.step(newTime + i, stat, hls, chars);
        tpo.processTheseDetails(new WorldLocation(stat.getLocation()), stat, cp);
      }

      tpo.tearDown(dummyScenario);

    }
  }

}