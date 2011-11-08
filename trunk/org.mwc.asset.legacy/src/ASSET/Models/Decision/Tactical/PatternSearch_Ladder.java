package ASSET.Models.Decision.Tactical;

/**
 * ASSET from PlanetMayo Ltd
 * User: Ian.Mayo
 * Date: $Date$
 * Time: $Time:$
 * Log:
 *  $Log: LadderSearch.java,v $
 *  Revision 1.2  2006/09/11 09:36:04  Ian.Mayo
 *  Fix dodgy accessor
 *
 *  Revision 1.1  2006/08/08 14:21:34  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:43  Ian.Mayo
 *  First versions
 *
 *  Revision 1.16  2004/11/25 14:29:29  Ian.Mayo
 *  Handle switch to hi-res dates
 *
 *  Revision 1.15  2004/11/03 09:54:50  Ian.Mayo
 *  Allow search speed to be set
 *
 *  Revision 1.14  2004/11/01 14:31:44  Ian.Mayo
 *  Do more elaborate processing when creating sample instance for property testing
 *
 *  Revision 1.13  2004/11/01 10:47:47  Ian.Mayo
 *  Provide accessor for series of generated points
 *
 *  Revision 1.12  2004/08/31 09:36:22  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.11  2004/08/26 16:27:03  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.10  2004/08/25 11:20:37  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.9  2004/08/24 10:36:25  Ian.Mayo
 *  Do correct activity management bits (using parent)
 *
 *  Revision 1.8  2004/08/20 13:32:29  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.7  2004/08/17 14:22:06  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.6  2004/08/09 15:50:31  Ian.Mayo
 *  Refactor category types into Force, Environment, Type sub-classes
 *
 *  Revision 1.5  2004/08/06 12:52:03  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.4  2004/08/06 11:14:25  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.3  2004/08/05 14:54:11  Ian.Mayo
 *  Provide accessor to get series of waypoints
 *
 *  Revision 1.2  2004/08/04 10:33:13  Ian.Mayo
 *  Add optional search height, use it
 *
 *  Revision 1.1  2004/05/24 15:56:29  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.4  2004/04/22 21:37:38  ian
 *  Tidying
 *
 *  Revision 1.3  2004/04/13 20:53:45  ian
 *  Implement restart functionality
 *
 *  Revision 1.2  2004/03/25 21:06:14  ian
 *  Correct placing of first point, correct tests
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *

 */

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModel;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Class implementing a square-shaped search pattern around an initial datum
 */
public final class PatternSearch_Ladder extends PatternSearch_Core implements Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
   * the initial heading for the manoeuvre
   */
  double _ladderAxis;

  /**
   * the leg length
   */
  WorldDistance _legLength;

  /**
   * the number of legs to run (optional)
   */
  Integer _maxLegs;

  /**
   * the default number of legs to produce
   */
  private static final int DEFAULT_ROUTE_LENGTH = 1000;

  /**
   * the name of this behaviour
   */
  static final String LADDER_SEARCH = "Performing ladder search";

  


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * conduct a ladder search
   *
   * @param ladderAxis   the initial direction
   * @param maxLegs      the number of legs to run (optional)
   * @param myOrigin     the starting point
   * @param trackSpacing the track spacing (separation in ladder axis)
   * @param legLength    the length of each leg
   * @param searchHeight the (optional) height/depth to conduct ladder at
   * @param name         the name for this ladder search behaviour
   */
  public PatternSearch_Ladder(final double ladderAxis,
                      final Integer maxLegs,
                      final WorldLocation myOrigin,
                      final WorldDistance trackSpacing,
                      final WorldDistance legLength,
                      final WorldDistance searchHeight,
                      final WorldSpeed searchSpeed,
                      final String name)
  {
    super(name, myOrigin, searchHeight, searchSpeed, trackSpacing, null, null);
    _ladderAxis = ladderAxis;
    _maxLegs = maxLegs;
    _trackSpacing = trackSpacing;
    _legLength = legLength;
    _finished = false;
  }

	 public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new LadderSearchInfo(this);

		return _myEditor;
	}

  
  //////////////////////////////////////////////////
  // decision options
  //////////////////////////////////////////////////

  /**
   * create the area search path as a route
   *
   * @param maxLegs      the maximum number of legs to fly
   * @param origin       the start datum
   * @param trackSpacing the spacing between loops of the spiral
   * @return a path containing the route to fly
   */
  protected WorldPath createSearchRoute(WorldLocation ignoreLocation)
  {
    // no - we'd better do it then.
    final WorldPath route = new WorldPath();

    // and now fill in the positions
    int routeLen = 0;

    if (_maxLegs != null)
      routeLen = _maxLegs.intValue();
    else
      routeLen = DEFAULT_ROUTE_LENGTH;

    // remember the 90 deg turn in rads
    double turn90 = MWC.Algorithms.Conversions.Degs2Rads(90);

    // remember the ladder axis in radians
    double ladderRads = MWC.Algorithms.Conversions.Degs2Rads(_ladderAxis);

    // remember the last location
    WorldLocation currentLocation = getOrigin();

    // and store it
    addPointToRoute(_searchHeight, currentLocation, route);

    // convert our dimensions to degrees
    double legDist = _legLength.getValueIn(WorldDistance.DEGS);
    double trkSpace = _trackSpacing.getValueIn(WorldDistance.DEGS);

    // keep track of whether we're doing a left or right leg
    float turnDirection = -1;

    // we will use special processing for the first case to only travel
    // down half a track length at the start - so that the areaa
    // of swept water overlaps the edge of the area (assuming that the
    // track spacing is equal to the swept water area)
    boolean first = true;

    // so, let's get going
    for (int i = 1; i <= routeLen; i++)
    {

      // create the two waypoint vectors (as per the modelling guide)
      WorldVector wp1 = null, wp2 = null;

      // ok. start off with wp1 (modifying it so that for WP1 we travel across by the leg length)
      if (first)
      {
        wp1 = new WorldVector(ladderRads, trkSpace / 2, 0);
        first = false;
      }
      else
      {
        wp1 = new WorldVector(ladderRads, trkSpace, 0);
      }

      currentLocation = new WorldLocation(currentLocation.add(wp1));

      // and store it
      addPointToRoute(_searchHeight, currentLocation, route);


      // and now on to wp2
      wp2 = new WorldVector(ladderRads + turnDirection * turn90, legDist, 0);
      currentLocation = new WorldLocation(currentLocation.add(wp2));

      // and store it
      addPointToRoute(_searchHeight, currentLocation, route);


      // ok. we are now at the end of an along, up, across, down manoeuvre, ready to go along again.
      turnDirection *= -1;

    }
    return route;
  }

  public final double getInitialTrack()
  {
    return _ladderAxis;
  }

  public final void setInitialTrack(final double _initialTrack)
  {
    this._ladderAxis = _initialTrack;
  }

  public final Integer getMaxLegs()
  {
    return _maxLegs;
  }

  public final void setMaxLegs(final Integer _maxLegs)
  {
    this._maxLegs = _maxLegs;
  }

  public WorldDistance getLegLength()
  {
    return _legLength;
  }

  public void setLegLength(WorldDistance legLength)
  {
    this._legLength = legLength;
  }

  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class LadderSearchInfo extends EditorType
  {
    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public LadderSearchInfo(final PatternSearch_Core data)
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
          prop("Name", "the name of this search behaviour"),
          prop("LegLength", "how wide each leg of the ladder should be"),
          prop("MaxLegs", "the number of legs to conduct"),
          prop("Origin", "the origin for this search"),
          prop("Route", "the series of points comprising the ladder search"),
          prop("SearchHeight", "the height to search at"),
          prop("TrackSpacing", "how far apart to make each leg"),
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

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static final class LadderSearchTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";
		private EarthModel _oldModel;

    public LadderSearchTest(final String s)
    {
      super(s);
      
    }
    
    

    @Override
		protected void setUp() throws Exception
		{
			super.setUp();

			_oldModel = WorldLocation.getModel();
			
			// store the model we're expecting
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		}



		@Override
		protected void tearDown() throws Exception
		{
			super.tearDown();
			
			// restore the previous model
			WorldLocation.setModel(_oldModel);
		}



		/**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      PatternSearch_Core ladder = new PatternSearch_Ladder(90,
                                             new Integer(5),
                                             SupportTesting.createLocation(0, 0),
                                             new WorldDistance(1, WorldDistance.KM),
                                             new WorldDistance(4, WorldDistance.KM),
                                             null,
                                             null,
                                             "home ladder");

      final WorldPath route = ladder.createSearchRoute(null);

      // and put it into a route
      ladder._myRoute = new TransitWaypoint(route, null, false, new OnTopWaypoint());

      // we need t
      return ladder;
    }

    public final void testCreateRoute()
    {
      PatternSearch_Core ladder = new PatternSearch_Ladder(0,
                                             new Integer(9),
                                             SupportTesting.createLocation(0, 0),
                                             new WorldDistance(20, WorldDistance.KM),
                                             new WorldDistance(160, WorldDistance.KM),
                                             null,
                                             null,
                                             "home ladder");

      // ok. now go for it
      Status origin = new Status(1, 0);
      origin.setLocation(SupportTesting.createLocation(0, 0));
      origin.setCourse(90);
      origin.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      ladder.decide(origin, null, null, null, null, 1000);

      // and look at the resulting ladder
      assertNotNull("ladder created", ladder._myRoute);

      TransitWaypoint route = ladder._myRoute;
      WorldPath path = route.getDestinations();
      outputPoints(path);
      assertNotNull("waypoints created", path);

      assertEquals("path of correct length", 11, path.size());

      assertEquals("first point right", " x,0,y,0", SupportTesting.toXYString(path.getLocationAt(0)));
      assertEquals("second point right", " x,500,y,0", SupportTesting.toXYString(path.getLocationAt(1)));
      assertEquals("11th point right", " x,4500,y,4000", SupportTesting.toXYString(path.getLocationAt(10)));
    }
    
    public static void outputPoints(WorldPath path)
    {
    	Collection<WorldLocation> pts = path.getPoints();
    	Iterator<WorldLocation> iter = pts.iterator();
    	while(iter.hasNext())
    	{
    		WorldLocation pt = iter.next();
    		System.err.println(pt.getLong() + "," + pt.getLat());
    	}
    }

    public final void testCreateRouteWithDepth()
    {
      PatternSearch_Core ladder = new PatternSearch_Ladder(90,
                                             new Integer(5),
                                             SupportTesting.createLocation(0, 0),
                                             new WorldDistance(1, WorldDistance.KM),
                                             new WorldDistance(4, WorldDistance.KM),
                                             new WorldDistance(500, WorldDistance.METRES),
                                             null,
                                             "home ladder");

      // ok. now go for it
      Status origin = new Status(1, 0);
      origin.setLocation(SupportTesting.createLocation(0, 0));
      origin.setCourse(90);
      origin.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      ladder.decide(origin, null, null, null, null, 1000);

      // and look at the resulting ladder
      assertNotNull("ladder created", ladder._myRoute);

      TransitWaypoint route = ladder._myRoute;
      WorldPath path = route.getDestinations();
      assertNotNull("waypoints created", path);

      assertEquals("path of correct length", 11, path.size());

      assertEquals("first point right", " x,0,y,0", SupportTesting.toXYString(path.getLocationAt(0)));
      assertEquals("second point right", " x,500,y,0", SupportTesting.toXYString(path.getLocationAt(1)));
      assertEquals("11th point right", " x,4500,y,4000", SupportTesting.toXYString(path.getLocationAt(10)));

      assertEquals("correct height set", -500, path.getLocationAt(0).getDepth(), 0.01);
      assertEquals("correct height set", -500, path.getLocationAt(1).getDepth(), 0.01);
      assertEquals("correct height set", -500, path.getLocationAt(10).getDepth(), 0.01);
    }

    public final void testFlySearch()
    {
      WorldLocation originLoc = SupportTesting.createLocation(MWC.Algorithms.Conversions.Degs2m(MWC.Algorithms.Conversions.Nm2Degs(-5)),
                                                              MWC.Algorithms.Conversions.Degs2m(MWC.Algorithms.Conversions.Nm2Degs(5)));

      PatternSearch_Core ladder = new PatternSearch_Ladder(90,
                                             new Integer(4),
                                             originLoc,
                                             new WorldDistance(10, WorldDistance.NM),
                                             new WorldDistance(40, WorldDistance.NM),
                                             null,
                                             null,
                                             "home ladder");

      // and now the helo
      Helo merlin = new Helo(12);
      merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
      merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      Status start = new Status(12, TimePeriod.INVALID_TIME);
      WorldLocation origin = originLoc;
      origin.setDepth(-500);
      start.setLocation(origin);
      start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
      merlin.setStatus(start);
      merlin.setDecisionModel(ladder);

      final CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, merlin);
      cs.setScenarioStepTime(2000);

      // do a step - to initialise the points
      cs.step();

      //      DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp", "fly_search.rep", false);
      //      DebriefReplayObserver.outputTheseLocations("c:/temp/pts2", ladder._myRoute.getDestinations());
      //      TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300, "fly_search.png", null, false, true);
      //      tpo.addPoints(ladder._myRoute.getDestinations());
      //      dro.setup(cs);
      //      tpo.setup(cs);

      // now run through to completion
      while (cs.getTime() < 5400000)
      {
        cs.step();
      }

      //      dro.tearDown(cs);
      //      tpo.tearDown(cs);

      WorldLocation endPoint = merlin.getStatus().getLocation();
      WorldLocation correctEnd = SupportTesting.createLocation(55201, -11739);
      //     SupportTesting.outputLocation(endPoint);
      WorldVector error = correctEnd.subtract(endPoint);
      assertEquals("near to correct end point", 0, MWC.Algorithms.Conversions.Degs2m(error.getRange()), 400);

    }

    public final void testFlySearchFurtherNorth()
    {
      WorldLocation originLoc = new WorldLocation(75, 0, 0);

      PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(4),
                                             originLoc,
                                             new WorldDistance(10, WorldDistance.NM),
                                             new WorldDistance(40, WorldDistance.NM),
                                             null,
                                             null,
                                             "home ladder");

      // and now the helo
      Helo merlin = new Helo(12);
      merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
      merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      Status start = new Status(12, TimePeriod.INVALID_TIME);
      WorldLocation origin = originLoc;
      origin.setDepth(-500);
      start.setLocation(origin);
      start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
      merlin.setStatus(start);
      merlin.setDecisionModel(ladder);

      final CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, merlin);
      cs.setScenarioStepTime(2000);

      // do a step - to initialise the points
      cs.step();

      //      DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp", "fly_search_north.rep", false, "debrief_track", true);
      //      DebriefReplayObserver.outputTheseLocations("c:/temp/fly_search_north_pts", ladder._myRoute.getDestinations());
      //      TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300, "fly_search_north.png", null, false, true, "track plot", true);
      //      tpo.addPoints(ladder._myRoute.getDestinations());
      //      dro.setup(cs);
      //      tpo.setup(cs);

      // now run through to completion
      while (cs.getTime() < 5400000)
      {
        cs.step();
      }

      //      dro.tearDown(cs);
      //      tpo.tearDown(cs);

      WorldLocation endPoint = merlin.getStatus().getLocation();
      WorldLocation correctEnd = SupportTesting.createLocation(64456,8312860);
      SupportTesting.outputLocation(endPoint);
      WorldVector error = correctEnd.subtract(endPoint);
      assertEquals("near to correct end point:" + endPoint, 0, MWC.Algorithms.Conversions.Degs2m(error.getRange()), 400);

    }

    public final void testSpeed()
    {
      WorldLocation originLoc = new WorldLocation(75, 0, 0);

      PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(4),
                                             originLoc,
                                             new WorldDistance(10, WorldDistance.NM),
                                             new WorldDistance(40, WorldDistance.NM),
                                             null,
                                             new WorldSpeed(120, WorldSpeed.Kts),
                                             "home ladder");

      // and now the helo
      Helo merlin = new Helo(12);
      merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
      merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      Status start = new Status(12, TimePeriod.INVALID_TIME);
      WorldLocation origin = originLoc;
      origin.setDepth(-500);
      start.setLocation(origin);
      start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
      merlin.setStatus(start);
      merlin.setDecisionModel(ladder);

      final CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, merlin);
      cs.setScenarioStepTime(2000);

      // do a step - to initialise the points
      cs.step();

      //      DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp", "fly_search_north.rep", false, "debrief_track", true);
      //      DebriefReplayObserver.outputTheseLocations("c:/temp/fly_search_north_pts", ladder._myRoute.getDestinations());
      //      TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300, "fly_search_north.png", null, false, true, "track plot", true);
      //      tpo.addPoints(ladder._myRoute.getDestinations());
      //      dro.setup(cs);
      //      tpo.setup(cs);

      // now run through to completion
      while (cs.getTime() < 5400000)
      {
        cs.step();
      }

      //      dro.tearDown(cs);
      //      tpo.tearDown(cs);
      assertEquals("at demanded speed", 120, merlin.getStatus().getSpeed().getValueIn(WorldSpeed.Kts), 0.001);

    }

    public final void testUnlimitedFlySearch()
    {
      PatternSearch_Core ladder = new PatternSearch_Ladder(90, null, SupportTesting.createLocation(0, 0),
                                             new WorldDistance(10, WorldDistance.KM),
                                             new WorldDistance(40, WorldDistance.KM),
                                             null,
                                             null,
                                             "home ladder");

      // ok. now go for it
      Status stat = new Status(1, TimePeriod.INVALID_TIME);
      stat.setLocation(SupportTesting.createLocation(0, 0));
      stat.setCourse(90);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      // and now the helo
      Helo merlin = new Helo(12);
      merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
      merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      Status start = new Status(12, TimePeriod.INVALID_TIME);
      WorldLocation origin = SupportTesting.createLocation(0, 0);
      origin.setDepth(-500);
      start.setLocation(origin);
      start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
      merlin.setStatus(start);
      merlin.setDecisionModel(ladder);

      final CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, merlin);
      cs.setScenarioStepTime(2000);

      // do a step - to initialise the points
      cs.step();

      // check the correct num of points created
      assertEquals("right number of points created", 1 + PatternSearch_Ladder.DEFAULT_ROUTE_LENGTH * 2, ladder._myRoute.getDestinations().size(), 0);

      //      DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp", "unlimited_fly_search.rep", false);
      //      DebriefReplayObserver.outputTheseLocations("c:/temp/unlimited_pts2", ladder._myRoute.getDestinations());
      //
      //      TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300, "unlimited_fly_search.png", null, false, true);
      //      tpo.addPoints(ladder._myRoute.getDestinations());
      //      dro.setup(cs);
      //      tpo.setup(cs);

      // now run through to completion
      while (cs.getTime() < 20000000)
      {
        cs.step();
      }

      //      dro.tearDown(cs);
      //      tpo.tearDown(cs);

      WorldLocation endPoint = merlin.getStatus().getLocation();
      WorldLocation correctEnd = SupportTesting.createLocation(265423,25605);
            SupportTesting.outputLocation(endPoint);
      WorldVector error = correctEnd.subtract(endPoint);
      assertEquals("near to correct end point", 0, MWC.Algorithms.Conversions.Degs2m(error.getRange()), 400);

    }

  }



	@Override
	protected String getDescriptor()
	{
		return LADDER_SEARCH;
	}

}
