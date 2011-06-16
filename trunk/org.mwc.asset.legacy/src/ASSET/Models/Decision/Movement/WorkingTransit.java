package ASSET.Models.Decision.Movement;

import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Tactical.Wait;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2004 Time: 11:43:09 To
 * change this template use File | Settings | File Templates.
 */
public class WorkingTransit extends Transit
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * what we do each time we reach a waypoint
	 */
	private Sequence _myActivity;

	/**
	 * whether we are currently doing one of our activities
	 */
	private boolean _busy;

	/**
	 * what we're doing
	 * 
	 */
	private static final String PERFORMING_ACTIVITY = "Performing activity:";

	/**
	 * the number of stops to take along the way
	 * 
	 */
	private int _numStops;

	/****************************************************
	 * constructor
	 ***************************************************/

	/**
	 * constructor = more realistic usage
	 * 
	 * @param myActivity
	 *          what to do each time we stop
	 * @param destinations
	 *          the core path of points to pass through
	 * @param transit_speed
	 *          how fast to travel
	 * @param loop
	 *          whether to loop around again at the end
	 * @param numStops
	 *          how many stops to make
	 */
	public WorkingTransit(Sequence myActivity, final WorldPath destinations,
			final WorldSpeed transit_speed, final boolean loop, final int numStops)
	{
		// ok, store the things we understand
		this._myActivity = myActivity;
		super.setSpeed(transit_speed);
		super.setLoop(loop);
		
		// do we have a  number of stops
		if(numStops > 0)
		{
			// ok, break down the path into the correct number of stops
			WorldPath brokenDownPath = destinations.breakIntoStraightSections(numStops);
			super.setDestinations(brokenDownPath);
		}
		else
		{
			super.setDestinations(destinations);
		}
	}

	/****************************************************
	 * member methods
	 ***************************************************/

	/**
	 * control what we do at each point
	 * 
	 * @return
	 */
	public Sequence getWorkingActivity()
	{
		return _myActivity;
	}

	/**
	 * control what we do at each point
	 * 
	 * @param myActivity
	 *          what we will do
	 */
	public void setWorkingActivity(Sequence myActivity)
	{
		this._myActivity = myActivity;
	}

	@Override
	protected void performReset()
	{
		super.performReset();

		// and reset our activity
		if (_myActivity != null)
			_myActivity.restart();
	}

	/**
	 * control the number of times we stop along the route
	 * 
	 * @return _numStops
	 */
	public int getNumStops()
	{
		return _numStops;
	}

	/**
	 * control the number of times we stop along the route
	 * 
	 * @param _numStops
	 */
	public void setNumStops(int _numStops)
	{
		this._numStops = _numStops;
	}

	/**
	 * ok, have a look at where we are, moving us on to the next destination if
	 * necessary
	 * 
	 * @param status
	 *          current participant location/speed
	 * @return the next target
	 */
	protected boolean determineProgress(Status status)
	{
		boolean passedNewPoint = false;

		// ok, see if we are currently doing one of our activities
		if (!_busy)
		{
			passedNewPoint = super.determineProgress(status);

			// hmm, did we reach the next check point?
		}

		return passedNewPoint;
	}

	protected DemandedStatus getDemandedStatus(
			final ASSET.Participants.Status status,
			ASSET.Models.Movement.MovementCharacteristics chars,
			DemandedStatus demStatus,
			ASSET.Models.Detection.DetectionList detections,
			ASSET.Scenario.ScenarioActivityMonitor monitor, final long time,
			boolean passedNewPoint)
	{
		DemandedStatus res = null;

		// first, check that we haven't finished
		if (!isFinished())
		{

			// have we just passed a new waypoint?
			if (passedNewPoint)
			{
				// yes, mark ourselves as busy
				_busy = true;

				// and reset the sequence - so we start to run through it again.
				_myActivity.restart();
			}

			// ok, are we currently doing our busy processing?
			if (_busy)
			{
				// yes we are. fire it again
				res = _myActivity.decide(status, chars, demStatus, detections, monitor,
						time);

				// check if we have now finished
				if (res == null)
				{
					// yup, we're not busy any more then
					_busy = false;
				}
				else
				{
					super
							.setLastActivity(PERFORMING_ACTIVITY + _myActivity.getActivity());
				}

			}

			// ok, are we busy in our activity
			if (res == null)
			{
				// no, carry on and
				// plot a course to the next destination
				res = super.getDemandedStatus(status, chars, demStatus, detections,
						monitor, time, passedNewPoint);
			}
		}

		return res;
	}

	/**
	 * ********************************************************************** test
	 * this class
	 * **********************************************************************
	 */

	public static class TransitWorkingTest extends SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TransitWorkingTest(final String name)
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
			return getSampleInstance();
		}

		protected WorkingTransit getSampleInstance()
		{
			WorldLocation newLoc1 = new WorldLocation(0, 1, 2);
			WorldLocation newLoc2 = new WorldLocation(0, 2, 2);
			WorldLocation newLoc3 = new WorldLocation(0, 1, 3);
			WorldPath newPath = new WorldPath(new WorldLocation[]
			{ newLoc1, newLoc2, newLoc3 });
			WorldSpeed transitSpeed = new WorldSpeed(12, WorldSpeed.Kts);
			Sequence activity = new Sequence(true, "Sequence");
			return new WorkingTransit(activity, newPath, transitSpeed, false, 2);
		}

		public void testWorking()
		{
			WorldLocation newLoc1 = new WorldLocation(0, 1, 2);
			WorldLocation newLoc2 = new WorldLocation(0, 2, 2);
			WorldLocation newLoc3 = new WorldLocation(0, 1, 3);
			WorldPath newPath = new WorldPath(new WorldLocation[]
			{ newLoc1, newLoc2, newLoc3 });
			WorldSpeed transitSpeed = new WorldSpeed(12, WorldSpeed.Kts);
			Sequence activity = new Sequence(true, "Sequence");
			Wait doWait = new Wait(new Duration(12, Duration.SECONDS), "do a wait");
			activity.insertAtHead(doWait);
			WorkingTransit transit = new WorkingTransit(activity, newPath,
					transitSpeed, false, 2);

			// just double-check that it's using our special model
			transit.setDestinations(newPath);

			assertNotNull("didn't create object", transit);

			// get ready to step forward
			Status theStat = new Status(23, 0);
			WorldLocation theLocation = new WorldLocation(0, 0, 0);
			WorldSpeed mySpeed = new WorldSpeed(12, WorldSpeed.Kts);
			MovementCharacteristics chars = SurfaceMovementCharacteristics
					.getSampleChars();
			theStat.setLocation(theLocation);
			theStat.setSpeed(mySpeed);

			long time = 1000;

			DemandedStatus dem = transit.decide(theStat, chars, null, null, null,
					time += 1000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);

			// ok, run it again
			dem = transit.decide(theStat, chars, null, null, null, time += 1000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);

			// and now move closer to first point
			theLocation = newLoc1.add(new WorldVector(1, new WorldDistance(12,
					WorldDistance.METRES), null));
			theStat.setLocation(theLocation);

			// check that we've executed our behaviour
			// ok, run it again
			dem = transit.decide(theStat, chars, null, null, null, time += 1000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals(
					"correctly reported doing activity",
					"WorkingTransit:Performing activity:Sequence:do a wait:Still waiting",
					transit.getActivity());

			// check that we've executed our behaviour
			dem = transit.decide(theStat, chars, null, null, null, time += 1000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals(
					"correctly reported doing activity",
					"WorkingTransit:Performing activity:Sequence:do a wait:Still waiting",
					transit.getActivity());

			// ok, keep on waiting
			// check that we've executed our behaviour
			dem = transit.decide(theStat, chars, null, null, null, time += 1000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals(
					"correctly reported doing activity",
					"WorkingTransit:Performing activity:Sequence:do a wait:Still waiting",
					transit.getActivity());

			// ok, move us to the end of the wait time
			// check that we've executed our behaviour
			dem = transit.decide(theStat, chars, null, null, null, time += 10000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals("correctly reported doing activity",
					"WorkingTransit:Transit heading for waypoint:2",
					transit.getActivity());
			assertEquals("correctly moved onto next waypoint", 1,
					transit.getCurrentDestinationIndex());

			// hey, move on to the next stop
			dem = transit.decide(theStat, chars, null, null, null, time += 10000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals("correctly reported doing activity",
					"WorkingTransit:Transit heading for waypoint:2",
					transit.getActivity());
			assertEquals("correctly moved onto next waypoint", 1,
					transit.getCurrentDestinationIndex());

			// hey, move on to the next stop
			dem = transit.decide(theStat, chars, null, null, null, time += 10000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals("correctly reported doing activity",
					"WorkingTransit:Transit heading for waypoint:2",
					transit.getActivity());
			assertEquals("correctly moved onto next waypoint", 1,
					transit.getCurrentDestinationIndex());

			// and move us near the next waypoint
			theStat.setLocation(newLoc2.add(new WorldVector(0, new WorldDistance(1,
					WorldDistance.METRES), null)));

			// hey, move on to the next stop
			dem = transit.decide(theStat, chars, null, null, null, time += 10000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals(
					"correctly reported doing activity",
					"WorkingTransit:Performing activity:Sequence:do a wait:Still waiting",
					transit.getActivity());
			assertEquals("correctly moved onto next waypoint", 2,
					transit.getCurrentDestinationIndex());

			// hey, move on to the next stop
			dem = transit.decide(theStat, chars, null, null, null, time += 10000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals(
					"correctly reported doing activity",
					"WorkingTransit:Performing activity:Sequence:do a wait:Still waiting",
					transit.getActivity());
			assertEquals("correctly moved onto next waypoint", 2,
					transit.getCurrentDestinationIndex());

			// hey, move on to the next stop
			dem = transit.decide(theStat, chars, null, null, null, time += 3000);

			// ok, check it worked
			assertNotNull("haven't produced demanded status", dem);
			assertEquals("correctly reported doing activity",
					"WorkingTransit:Transit heading for waypoint:3",
					transit.getActivity());
			assertEquals("correctly moved onto next waypoint", 2,
					transit.getCurrentDestinationIndex());

		}

	}

}
