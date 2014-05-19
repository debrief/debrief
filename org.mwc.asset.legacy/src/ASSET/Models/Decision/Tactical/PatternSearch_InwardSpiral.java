package ASSET.Models.Decision.Tactical;

/**
 * ASSET from PlanetMayo Ltd
 * User: Ian.Mayo
 * Date: $Date: 2011-09-20 11:29:31 +0100 (Tue, 20 Sep 2011) $
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

/**
 * Class implementing a ladder-shaped search pattern around an initial datum.
 * The search is conducted in a defined box
 */
public final class PatternSearch_InwardSpiral extends PatternSearch_Core
		implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	/**
	 * the name of this behaviour
	 */
	static final String INWARD_SPIRAL = "Performing inward spiral";

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * conduct a ladder search
	 * 
	 * @param myOrigin
	 *          the starting point
	 * @param trackSpacing
	 *          how far apart to make the legs
	 * @param searchHeight
	 *          the (optional) height/depth to conduct ladder at
	 * @param searchSpeed
	 *          the (optional) height/depth to conduct ladder at
	 * @param name
	 *          the name for this ladder search behaviour
	 * @param height
	 *          the height of the search area
	 * @param width
	 *          the width of the search area
	 */
	public PatternSearch_InwardSpiral(final WorldLocation myOrigin,
			final WorldDistance trackSpacing, final WorldDistance searchHeight,
			final WorldSpeed searchSpeed, final String name, WorldDistance height,
			WorldDistance width)
	{
		super(name, myOrigin, searchHeight, searchSpeed, trackSpacing, height,
				width);
		_finished = false;
	}

	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SpiralInInfo(this);

		return _myEditor;
	}

	// ////////////////////////////////////////////////
	// decision options
	// ////////////////////////////////////////////////

	/**
	 * create the area search path as a route
	 * 
	 * @param currentLocation
	 *         the current location of the platform
	 * @param origin
	 *          the start datum
	 * @param trackSpacing
	 *          the spacing between loops of the spiral
	 * @return a path containing the route to fly
	 */
	protected WorldPath createSearchRoute(WorldLocation currentLocation)
	{
		final WorldPath route = new WorldPath();

		double _heightDegs = _height.getValueIn(WorldDistance.DEGS);
		double _widthDegs = _width.getValueIn(WorldDistance.DEGS);

		double t, b, l, r, s;
		b = getOrigin().getLat();
		t = b + _heightDegs;
		l = getOrigin().getLong();
		r = l + _widthDegs;
		s = _trackSpacing.getValueIn(WorldDistance.DEGS);

		WorldLocation newP;

		// right, are we at the start point?
		if((currentLocation != null) && !getOrigin().equals(currentLocation))
		{
			// take us to the first point
			newP = new WorldLocation(getOrigin());
			addPointToRoute(_searchHeight, newP, route);
		}
		
		
		// do the first, slightly dodgy loop
		newP = new WorldLocation(b, r, 0);
		addPointToRoute(_searchHeight, newP, route);
		newP = new WorldLocation(t, r, 0);
		addPointToRoute(_searchHeight, newP, route);
		newP = new WorldLocation(t, l, 0);
		addPointToRoute(_searchHeight, newP, route);
		b = b + s;
		newP = new WorldLocation(b, l, 0);
		addPointToRoute(_searchHeight, newP, route);
		
		while (t > (b + s * 1.1))
		{
			r = r - s;
			newP = new WorldLocation(b, r, 0);
			addPointToRoute(_searchHeight, newP, route);
			t = t - s;
			newP = new WorldLocation(t, r, 0);
			addPointToRoute(_searchHeight, newP, route);
			l = l + s;
			newP = new WorldLocation(t, l, 0);
			addPointToRoute(_searchHeight, newP, route);
			b = b + s;
			newP = new WorldLocation(b, l, 0);
			addPointToRoute(_searchHeight, newP, route);
		}

		r = r - s;
		newP = new WorldLocation(b, r, 0);
		addPointToRoute(_searchHeight, newP, route);

		return route;
	}

	// ////////////////////////////////////////////////
	// property editing
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// editable properties
	// ////////////////////////////////////////////////
	static public class SpiralInInfo extends EditorType
	{
		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public SpiralInInfo(final PatternSearch_Core data)
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
				final PropertyDescriptor[] res =
				{ prop("Name", "the name of this search behaviour"),
						prop("Origin", "the origin for this search"),
						prop("Route", "the series of points comprising the ladder search"),
						prop("Width", "the width of the area to search"),
						prop("Height", "the height of the area to search"),
						prop("SearchHeight", "the height to search at"),
						prop("SearchSpeed", "the speed to search at"),
						prop("TrackSpacing", "how far apart to make each leg"), };
				return res;
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class SpiralInTest extends
			SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		private EarthModel _oldModel;

		public SpiralInTest(final String s)
		{
			super(s);

		}

		@Override
		protected void setUp() throws Exception
		{
			super.setUp();

			_oldModel = WorldLocation.getModel();

			// store the model we're expecting
			MWC.GenericData.WorldLocation
					.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

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
			PatternSearch_Core ladder = new PatternSearch_InwardSpiral(
					new WorldLocation(1, 1, 1), new WorldDistance(20, WorldDistance.KM),
					null, null, "test search", new WorldDistance(160, WorldDistance.KM),
					new WorldDistance(160, WorldDistance.KM));
			final WorldPath route = ladder.createSearchRoute(null);

			// and put it into a route
			ladder._myRoute = new TransitWaypoint(route, null, false,
					new OnTopWaypoint());

			// we need t
			return ladder;
		}

		public final void testCreateRoute()
		{
			PatternSearch_Core ladder = new PatternSearch_InwardSpiral(null,
					new WorldDistance(20, WorldDistance.KM), null, null, "test search",
					new WorldDistance(160, WorldDistance.KM), new WorldDistance(160,
							WorldDistance.KM));

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

			assertEquals("path of correct length", 17, path.size());

			assertEquals("first point right", " x,159999,y,0",
					SupportTesting.toXYString(path.getLocationAt(0)));
			assertEquals("second point right", " x,159999,y,159999",
					SupportTesting.toXYString(path.getLocationAt(1)));
			assertEquals("11th point right", " x,39999,y,119999",
					SupportTesting.toXYString(path.getLocationAt(10)));
		}

		public static void outputPoints(WorldPath path)
		{
			Collection<WorldLocation> pts = path.getPoints();
			Iterator<WorldLocation> iter = pts.iterator();
			while (iter.hasNext())
			{
				WorldLocation pt = iter.next();
				System.err.println(pt.getLong() + "," + pt.getLat());
			}
		}

		public final void testSpeed()
		{
			WorldLocation originLoc = new WorldLocation(75, 0, 0);

			PatternSearch_Core ladder = new PatternSearch_InwardSpiral(null,
					new WorldDistance(20, WorldDistance.KM), new WorldDistance(200,
							WorldDistance.METRES), new WorldSpeed(120, WorldSpeed.Kts),
					"test search", new WorldDistance(160, WorldDistance.KM),
					new WorldDistance(160, WorldDistance.KM));

			// and now the helo
			Helo merlin = new Helo(12);
			merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
			merlin.setCategory(new Category(Category.Force.BLUE,
					Category.Environment.AIRBORNE, Category.Type.HELO));
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

			// now run through to completion
			while (cs.getTime() < 5400000)
			{
				cs.step();
			}

			// dro.tearDown(cs);
			// tpo.tearDown(cs);
			assertEquals("at demanded speed", 120, merlin.getStatus().getSpeed()
					.getValueIn(WorldSpeed.Kts), 0.001);

		}

		public final void testCreateRouteWithOrigin()
		{
			PatternSearch_Core ladder = new PatternSearch_InwardSpiral(new WorldLocation(2,2,0),
					new WorldDistance(20, WorldDistance.KM), null, null, "test search",
					new WorldDistance(160, WorldDistance.KM), new WorldDistance(160,
							WorldDistance.KM));
		
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
		
			assertEquals("path of correct length", 18, path.size());
		
			assertEquals("first point right", " x,222240,y,222240",
					SupportTesting.toXYString(path.getLocationAt(0)));
			assertEquals("second point right", " x,382240,y,222240",
					SupportTesting.toXYString(path.getLocationAt(1)));
			assertEquals("11th point right", " x,342240,y,342240",
					SupportTesting.toXYString(path.getLocationAt(10)));
		}
	}

	@Override
	protected String getDescriptor()
	{
		return INWARD_SPIRAL;
	}

}
