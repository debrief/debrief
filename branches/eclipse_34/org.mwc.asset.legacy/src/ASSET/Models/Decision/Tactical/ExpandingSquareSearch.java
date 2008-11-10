package ASSET.Models.Decision.Tactical;

/**
 * ASSET from PlanetMayo Ltd
 * User: Ian.Mayo
 * Date: $Date$
 * Time: $Time:$
 * Log:
 *  $Log: ExpandingSquareSearch.java,v $
 *  Revision 1.1  2006/08/08 14:21:33  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:41  Ian.Mayo
 *  First versions
 *
 *  Revision 1.27  2004/08/31 09:36:19  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.26  2004/08/26 16:00:02  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.25  2004/08/25 11:20:33  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.24  2004/08/20 13:32:26  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.23  2004/08/17 14:22:03  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.22  2004/08/16 09:16:15  Ian.Mayo
 *  Respect changed processing of tester recording to file (it needed a valid scenario object)
 *
 *  Revision 1.21  2004/08/12 11:09:21  Ian.Mayo
 *  Respect observer classes refactored into tidy directories
 *
 *  Revision 1.20  2004/08/09 15:50:29  Ian.Mayo
 *  Refactor category types into Force, Environment, Type sub-classes
 *
 *  Revision 1.19  2004/08/06 12:52:00  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.18  2004/08/06 11:14:22  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.17  2004/08/05 14:55:05  Ian.Mayo
 *  Remove nugatory test code
 *
 *  Revision 1.16  2004/05/24 15:57:08  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *
 *  Revision 1.15  2004/02/18 08:48:09  Ian.Mayo
 *  Sync from home
 *
 *  Revision 1.13  2003/11/05 09:19:53  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.12  2003/09/19 13:37:57  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.11  2003/09/18 14:11:49  Ian.Mayo
 *  Make tests work with new World Speed class
 *
 *  Revision 1.10  2003/09/18 12:12:45  Ian.Mayo
 *  Reflect introduction of World Speed    ex
 *
 *  Revision 1.9  2003/09/12 13:15:08  Ian.Mayo
 *  Check we have a valid time, else use the scenario time
 *
 *  Revision 1.8  2003/09/11 10:43:49  Ian.Mayo
 *  Switch to using OnTop behaviour when conducting search
 *
 *  Revision 1.7  2003/09/09 16:26:52  Ian.Mayo
 *  use shifted location for testing/diags
 *
 *  Revision 1.6  2003/09/09 15:56:58  Ian.Mayo
 *  more implementation (including deciding whether to insert new search points)
 *
 *  Revision 1.5  2003/09/09 09:32:43  Ian.Mayo
 *  accept recommended improvements
 *
 *  Revision 1.4  2003/09/09 09:30:26  Ian.Mayo
 *  Include correct log entry
 *
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Editable;
import MWC.GenericData.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

/**
 * Class implementing a square-shaped search pattern around an initial datum
 */
public final class ExpandingSquareSearch extends CoreDecision implements Serializable
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
   * the start point for the search
   */
  private WorldLocation _myOrigin;

  /**
   * the initial heading for the manoeuvre
   */
  private double _initialTrack;

  /**
   * the track spacing
   */
  private WorldDistance _trackSpacing;

  /**
   * which way around we're going
   */
  private boolean _flyClockwise;

  /**
   * the max num of legs we're flying
   */
  private Integer _maxLegs = null;

  /**
   * whether we've finished yet or not.
   */
  private boolean _finished = false;

  /**
   * the route of points to pass through
   */
  private TransitWaypoint _myRoute;

  /**
   * the default number of legs to produce
   */
  private static final int DEFAULT_ROUTE_LENGTH = 1000;
  private static final String EXP_SQUARE_SEARCH = "Performing expanding square search";


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  public ExpandingSquareSearch(final boolean flyClockwise,
                               final double initialTrack,
                               final Integer maxLegs,
                               final WorldLocation myOrigin,
                               final WorldDistance worldDistance,
                               final String name)
  {
    super(name);
    this._flyClockwise = flyClockwise;
    this._initialTrack = initialTrack;
    this._maxLegs = maxLegs;
    this._myOrigin = myOrigin;
    this._trackSpacing = worldDistance;
    _finished = false;
  }

  //////////////////////////////////////////////////
  // decision options
  //////////////////////////////////////////////////

  /**
   * decide the course of action to take, or return null to no be used
   *
   * @param status     the current status of the participant
   * @param detections the current list of detections for this participant
   * @param monitor    the object which handles weapons release/detonation
   * @param newTime    the time this decision is to be made
   */
  public final DemandedStatus decide(final Status status,
                                     ASSET.Models.Movement.MovementCharacteristics chars,
                                     final DemandedStatus demStatus,
                                     final DetectionList detections,
                                     final ScenarioActivityMonitor monitor,
                                     final long newTime)
  {
    DemandedStatus res = null;

    String activity = null;

    // have we finished yet?
    if (!_finished)
    {
      // no, have we defined our route yet?
      if (_myRoute == null)
      {
        // what's our current speed
        double curSpeed = status.getSpeed().getValueIn(WorldSpeed.M_sec);

        // create the path to follow
        final WorldPath route = createSearchRoute(_maxLegs, chars, _flyClockwise, _myOrigin, _trackSpacing, _initialTrack, curSpeed);

        // and put it into a route
        _myRoute = new TransitWaypoint(route, null, false, new OnTopWaypoint());
      }

      // so, we now have our route. Has it finished yet?
      if (_myRoute.isFinished())
      {
        // done
        res = null;

        // and reset our list of points
        _myRoute.getDestinations().getPoints().clear();
        _myRoute = null;

        // remember the fact that we've now finished
        _finished = true;

        activity = null;

      }
      else
      {
        // ok - still going, ask the transitter what it wants to do
        res = _myRoute.decide(status, chars, demStatus, detections, monitor, newTime);

        // just double-check if we have now finished.
        if (res == null)
        {
          activity = _myRoute.getActivity();
          _finished = true;
        }
        else
          activity = EXP_SQUARE_SEARCH + ":" + _myRoute.getCurrentDestinationIndex();
      }
    }

    super.setLastActivity(activity);

    return res;
  }

  /**
   * create the area search path as a route
   *
   * @param maxLegs      the maximum number of legs to fly
   * @param flyClockwise whether to fly clockwise
   * @param origin       the start datum
   * @param trackSpacing the spacing between loops of the spiral
   * @param initialTrack the initial course to steer
   * @return a path containing the route to fly
   */
  private static WorldPath createSearchRoute(final Integer maxLegs,
                                             final MovementCharacteristics chars,
                                             final boolean flyClockwise,
                                             final WorldLocation origin,
                                             final WorldDistance trackSpacing,
                                             final double initialTrack,
                                             double currentSpeed)
  {
    // no - we'd better do it then.
    final WorldPath route = new WorldPath();

    // and now fill in the positions
    int routeLen = 0;

    if (maxLegs != null)
      routeLen = maxLegs.intValue();
    else
      routeLen = DEFAULT_ROUTE_LENGTH;

    // what's the turning circle
    double turn_circle_rad_degs =
      MWC.Algorithms.Conversions.m2Degs(chars.getTurningCircleDiameter(currentSpeed)) / 1.5;


    // remember the 90 deg turn in rads
    double turn90 = MWC.Algorithms.Conversions.Degs2Rads(90);

    // and turn us left if needed
    if (!flyClockwise)
      turn90 = -turn90;

    // remember the last location
    WorldLocation currentLocation = origin;

    // and the previous location
    WorldLocation lastLocation = currentLocation;

    // remember the last heading
    double lastHeading = MWC.Algorithms.Conversions.Degs2Rads(initialTrack);

    // insert the start datum
    route.addPoint(currentLocation);

    final WorldVector thisLeg = new WorldVector(0, 0, 0);

    // so, let's get going
    for (int i = 1; i <= routeLen; i++)
    {
      // create the vector
      thisLeg.setValues(lastHeading, ((i + 1) / 2) * trackSpacing.getValueIn(WorldDistance.DEGS), 0);

      // create the new position
      currentLocation = currentLocation.add(thisLeg);

      // what's the range from the previous location?
      if (lastLocation != null)
      {
        double rngToLast = currentLocation.rangeFrom(lastLocation);
        if (rngToLast > turn_circle_rad_degs)
        {
          route.addPoint(currentLocation);
          lastLocation = currentLocation;
        }
      }
      else
      {
        // first pass through, just add it
        route.addPoint(currentLocation);
        lastLocation = currentLocation;
      }

      // and turn us to the right
      lastHeading += turn90;
    }
    return route;
  }


  /**
   * reset this decision model
   */
  public final void restart()
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


  public final boolean isflyClockwise()
  {
    return _flyClockwise;
  }

  public final void setflyClockwise(final boolean _flyClockwise)
  {
    this._flyClockwise = _flyClockwise;
  }

  public final double getInitialTrack()
  {
    return _initialTrack;
  }

  public final void setInitialTrack(final double _initialTrack)
  {
    this._initialTrack = _initialTrack;
  }

  public final Integer getMaxLegs()
  {
    return _maxLegs;
  }

  public final void setMaxLegs(final Integer _maxLegs)
  {
    this._maxLegs = _maxLegs;
  }

  public final WorldLocation getOrigin()
  {
    return _myOrigin;
  }

  public final void setOrigin(final WorldLocation _myOrigin)
  {
    this._myOrigin = _myOrigin;
  }

  public final WorldDistance getTrackSpacing()
  {
    return _trackSpacing;
  }

  public final void setTrackSpacing(final WorldDistance _trackSpacing)
  {
    this._trackSpacing = _trackSpacing;
  }

  public final boolean getFinished()
  {
    return _finished;
  }


  public boolean getClockwise()
  {
    return _flyClockwise;
  }

  public void setClockwise(boolean flyClockwise)
  {
    this._flyClockwise = flyClockwise;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: ExpandingSquareSearch.java,v $
   * Revision 1.1  2006/08/08 14:21:33  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:41  Ian.Mayo
   * First versions
   *
   * Revision 1.27  2004/08/31 09:36:19  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   *
   * Revision 1.26  2004/08/26 16:00:02  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.25  2004/08/25 11:20:33  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.24  2004/08/20 13:32:26  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.23  2004/08/17 14:22:03  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.22  2004/08/16 09:16:15  Ian.Mayo
   * Respect changed processing of tester recording to file (it needed a valid scenario object)
   * <p/>
   * Revision 1.21  2004/08/12 11:09:21  Ian.Mayo
   * Respect observer classes refactored into tidy directories
   * <p/>
   * Revision 1.20  2004/08/09 15:50:29  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.19  2004/08/06 12:52:00  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.18  2004/08/06 11:14:22  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.17  2004/08/05 14:55:05  Ian.Mayo
   * Remove nugatory test code
   * <p/>
   * Revision 1.16  2004/05/24 15:57:08  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.15  2004/02/18 08:48:09  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.13  2003/11/05 09:19:53  Ian.Mayo
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
      _myEditor = new ExpandingSquareSearchInfo(this);

    return _myEditor;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class ExpandingSquareSearchInfo extends EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public ExpandingSquareSearchInfo(final ExpandingSquareSearch data)
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
          prop("Name", "the name of this bearing trail model"),
          prop("Clockwise", "whether to search clockwise or not"),
          prop("InitialTrack", "the initial heading for the search"),
          prop("MaxLegs", "the maximum number of legs to conduct"),
          prop("TrackSpacing", "the spacing to use between legs"),
          prop("Origin", "the start point of the search"),
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


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static final class ExpandingSquareSearchTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ExpandingSquareSearchTest(final String s)
    {
      super(s);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new ExpandingSquareSearch(true, 12, null, null, null, "my name");
    }

    public final void testCreateRoute()
    {

      WorldLocation.setModel(new CompletelyFlatEarth());
      final Integer maxLegs = new Integer(10);
      final boolean flyClockwise = true;
      final WorldLocation origin = createLocation(0, 0);
      final WorldDistance trackSpacing = new WorldDistance(1, WorldDistance.DEGS);
      final double initialTrack = 0;

      final String myName = "ssn Trial";
      final double accelRate = 10;
      final double decelRate = 25;
      final double fuel_usage_rate = 0;
      final double maxSpeed = 100;
      final double minSpeed = -5;
      final double defaultClimbRate = 15;
      final double defaultDiveRate = 15;
      final double maxDepth = 0;
      final double minDepth = -400;
      final double turnCircle = 100;

      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.SSMovementCharacteristics(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, turnCircle,
                                                            defaultClimbRate,
                                                            defaultDiveRate, maxDepth,
                                                            minDepth);
      final double currentSpeed = 10;

      //////////////////////////////////////////////////
      WorldPath wp = createSearchRoute(maxLegs, chars, flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);
      //////////////////////////////////////////////////

      // with 10 legs we will have 11 nodes
      assertEquals("correct length", 11, wp.size(), 0);

      // with 10 legs we should end up to the north
      assertTrue("last point to east", wp.getLocationAt(wp.getPoints().size() - 1).getLong() > 0);
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLat() > 0);

      //////////////////////////////////////////////////
      // REPEAT TO ANTI=CLOCKWISE
      //////////////////////////////////////////////////
      wp = createSearchRoute(maxLegs, chars, !flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);

      // with 10 legs we will have 11 nodes
      assertEquals("correct length", 11, wp.size(), 0);

      // with 10 legs we should end up to the north
      assertTrue("last point to west", wp.getLocationAt(wp.getPoints().size() - 1).getLong() < 0);
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLat() > 0);

      //////////////////////////////////////////////////
      // Chuck in another point so we are now going south
      //////////////////////////////////////////////////
      wp = createSearchRoute(new Integer(maxLegs.intValue() + 1), chars, flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);

      // with 10 legs we will have 11 nodes
      assertEquals("correct length", 12, wp.size(), 0);

      // with 10 legs we should end up to the north
      assertTrue("last point to east", wp.getLocationAt(wp.getPoints().size() - 1).getLong() > 0);
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLat() < 0);

      //////////////////////////////////////////////////
      // Chuck in another point so we are now going south
      //////////////////////////////////////////////////
      wp = createSearchRoute(new Integer(maxLegs.intValue() + 1), chars, !flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);

      // with 10 legs we will have 11 nodes
      assertEquals("correct length", 12, wp.size(), 0);

      // with 10 legs we should end up to the north
      assertTrue("last point to west", wp.getLocationAt(wp.getPoints().size() - 1).getLong() < 0);
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLat() < 0);

      //////////////////////////////////////////////////
      // idiot check for handling only one point
      //////////////////////////////////////////////////
      wp = createSearchRoute(new Integer(1), chars, !flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);

      // with 10 legs we will have 11 nodes
      assertEquals("correct length", 2, wp.size(), 0);

      // with 10 legs we should end up to the north
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLong() == 0);
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLat() > 0);

      //////////////////////////////////////////////////
      // idiot check for handling zero points
      //////////////////////////////////////////////////
      wp = createSearchRoute(new Integer(0), chars, !flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);

      // with 10 legs we will have 11 nodes
      assertEquals("correct length", 1, wp.size(), 0);

      // with 10 legs we should end up to the north
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLong() == 0);
      assertTrue("last point to north", wp.getLocationAt(wp.getPoints().size() - 1).getLat() == 0);
    }

    public final void testFollowRoute()
    {
      //     WorldLocation.setModel(new CompletelyFlatEarth());
      final Integer maxLegs = new Integer(10);
      final boolean flyClockwise = true;
      final WorldLocation origin = createLocation(0, 0);
      final WorldDistance trackSpacing = new WorldDistance(10, WorldDistance.NM);
      final double initialTrack = 0;
      final ExpandingSquareSearch ess = new ExpandingSquareSearch(flyClockwise, initialTrack, maxLegs, origin, trackSpacing, "the square");

      final String myName = "Merlin Trial";
      final double accelRate = 10;
      final double decelRate = 25;
      final double fuel_usage_rate = 0;
      final double maxSpeed = 100;
      final double minSpeed = -5;
      final double defaultClimbRate = 15;
      final double defaultDiveRate = 15;
      final double maxHeight = 400;
      final double minHeight = 0;
      final double myTurnRate = 3;
      final double defaultClimbSpeed = 15;
      final double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.HeloMovementCharacteristics(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      final CoreParticipant cp = new Helo(12);
      cp.setName("h_alpha");
      cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      cp.setDecisionModel(ess);
      cp.setMovementChars(chars);
      final Status stat = new Status(12, 1000);
      stat.setLocation(origin.add(new WorldVector(123, 0.3, 0)));
      stat.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));
      stat.setCourse(122);
      cp.setStatus(stat);

      final CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, cp);
      cs.setScenarioStepTime(2000);


      // do a first step
      cs.step();

      // check we're working
      assertEquals("our decision is running", cp.getActivity(), ess.getActivity());

      // now run through to completion
      while (cs.getTime() < 25000000)
      {
        cs.step();
        this.recordThis(cp.getStatus(), cp, cs.getTime());
      }

      // check we've finished the turns
      assertEquals("search now complete", cp.getActivity(), CoreParticipant.INACTIVE_DESCRIPTOR);

      // check we've ended up to the north west
      assertTrue("ended up further north", cp.getStatus().getLocation().getLat() > 0);
      assertTrue("ended up further east", cp.getStatus().getLocation().getLong() > 0);
    }

    public final void testUnattainablePoints()
    {
      WorldLocation.setModel(new CompletelyFlatEarth());
      final Integer maxLegs = new Integer(19);
      final boolean flyClockwise = true;
      final WorldLocation origin = createLocation(0, 00);
      final WorldDistance trackSpacing = new WorldDistance(500, WorldDistance.METRES);
      final double initialTrack = 0;
      final ExpandingSquareSearch ess = new ExpandingSquareSearch(flyClockwise, initialTrack, maxLegs, origin, trackSpacing, "the square");

      final String myName = "ssn Trial";
      final double accelRate = 10;
      final double decelRate = 25;
      final double fuel_usage_rate = 0;
      final double maxSpeed = 100;
      final double minSpeed = -5;
      final double defaultClimbRate = 15;
      final double defaultDiveRate = 15;
      final double maxDepth = 0;
      final double minDepth = -400;
      final double turnCircle = 1100;

      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.SSMovementCharacteristics(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, turnCircle,
                                                            defaultClimbRate,
                                                            defaultDiveRate, maxDepth,
                                                            minDepth);

      final CoreParticipant cp = new Helo(12);
      cp.setName("h_delta");
      cp.setCategory(new Category(Category.Force.RED, Category.Environment.AIRBORNE, Category.Type.HELO));
      cp.setDecisionModel(ess);
      cp.setMovementChars(chars);
      final Status stat = new Status(12, 1000);
      stat.setLocation(origin.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), 0.03, 0)));
      stat.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));
      stat.setCourse(122);
      cp.setStatus(stat);

      final CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, cp);
      cs.setScenarioStepTime(2000);

      //      this.startListeningTo(cp, "new_list2", true, true, true);

      // do a first step
      cs.step();

      // check we created the destinations
      assertTrue("we didn't create all the destinations", ess._myRoute.getDestinations().size() < maxLegs.intValue());
      assertTrue("but we created some..", ess._myRoute.getDestinations().size() > 0);



      //      this.recordThis(cp.getStatus(), cp);

      // check we're working
      assertEquals("our decision is running", cp.getActivity(), ess.getActivity());

      // now run through to completion
      while (cs.getTime() < 2000000)
      {
        cs.step();
      }


      this.endRecording(cs);

      // check we've finished the turns
      assertEquals("search now complete", CoreParticipant.INACTIVE_DESCRIPTOR, cp.getActivity());

      // check we've ended up to the north west
      assertTrue("ended up further south", cp.getStatus().getLocation().getLat() < 0);
      assertTrue("ended up further east", cp.getStatus().getLocation().getLong() > 0);


      assertTrue("stopped recording", _listeningList == null);

    }
  }

}
