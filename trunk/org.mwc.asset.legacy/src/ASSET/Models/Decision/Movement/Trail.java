package ASSET.Models.Decision.Movement;

import ASSET.NetworkParticipant;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class Trail extends CoreDecision implements java.io.Serializable
{

	// ////////////////////////////////////////////////////////////////////
	// Member Variables
	// ////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the range to trail at (yds)
	 */
	WorldDistance _trailRange;

	/**
	 * the allowable error in the trail (yds)
	 */
	protected WorldDistance _allowable_error = new WorldDistance(600,
			WorldDistance.YARDS);

	/**
	 * the type of target we are hunting
	 */
	private TargetType _myTarget;

	/**
	 * a local copy of our editable object
	 */
	protected MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the time period we should stay on course for (following a valid detection)
	 */
	private Duration _stayOnBearing = null;
	
	/** whether to allow the platform to change speed to hit the ideal trail range
	 * 
	 */
	private boolean _allowSpeedChange = true;

	/**
	 * the time at which we will stop staying on bearing
	 */
	transient protected long _endStayOnBearing = -1;

	/**
	 * remember the last valid detection (in case we do intend to stay on bearing)
	 */
	transient private DemandedStatus _courseForLastValidDetection = null;

	/**
	 * an (optional) value to represent the Height to trail at
	 */
	private WorldDistance _trailHeight = null;

	// ////////////////////////////////////////////////////////////////////
	// Constructor
	// ////////////////////////////////////////////////////////////////////

	public Trail(final WorldDistance trailRange)
	{
		super("Trail");
		_trailRange = trailRange;

		_stayOnBearing = new Duration(5, Duration.MINUTES);
	}

	// ////////////////////////////////////////////////////////////////////
	// Member methods
	// ////////////////////////////////////////////////////////////////////

	public void setTargetType(final TargetType target)
	{
		_myTarget = target;
	}

	public TargetType getTargetType()
	{
		return _myTarget;
	}

	public Duration getStayOnBearingTime()
	{
		return _stayOnBearing;
	}

	public WorldDistance getTrailHeight()
	{
		return _trailHeight;
	}

	public void setTrailHeight(WorldDistance trailHeight)
	{
		this._trailHeight = trailHeight;
	}

	public void setStayOnBearingTime(Duration stayOnBearing)
	{
		_stayOnBearing = stayOnBearing;
	}

	private static boolean isThisValid(DetectionEvent detection,
			TargetType myTarget, Integer requiredHost)
	{
		boolean res = false;
		final Float brg = detection.getBearing();

		// are we fussy about the host?
		if (requiredHost != null)
		{
			if (detection.getHost() != requiredHost.intValue())
			{
				return res;
			}
		}

		// else, lets check we have a bearing to the target
		if (brg != null)
		{
			// do we have a target type?
			if (myTarget != null)
			{
				// is this of our target type
				final ASSET.Participants.Category thisTarget = detection
						.getTargetType();
				if (myTarget.matches(thisTarget))
				{
					// yes, continue with this loop
					res = true;
				}
				else
				{
				}
			}

		} // if we know the bearing
		return res;
	}

	/**
	 * decide the course of action to take, or return null to no be used
	 * 
	 * @param status
	 *          the current status of the participant
	 * @param chars
	 *          the movement chars of this vehicle
	 * @param detections
	 *          the current list of detections for this participant
	 * @param monitor
	 *          the object which handles weapons release/detonation
	 * @param time
	 *          the time this decision is to be made
	 */
	public ASSET.Participants.DemandedStatus decide(
			final ASSET.Participants.Status status,
			ASSET.Models.Movement.MovementCharacteristics chars,
			DemandedStatus demStatus,
			final ASSET.Models.Detection.DetectionList detections,
			ASSET.Scenario.ScenarioActivityMonitor monitor, final long time)
	{
		ASSET.Participants.DemandedStatus res = null;

		DetectionEvent validDetection = null;

		// clear the activity flag
		String activity = "Not in trail";

		// do we have any detections?
		if (detections != null)
		{
			// get bearing to first detection
			final int len = detections.size();

			if (len > 0)
			{

				// first pass through our organic sensors
				for (int i = 0; i < len; i++)
				{
					final ASSET.Models.Detection.DetectionEvent de = detections
							.getDetection(i);
					if (isThisValid(de, _myTarget, new Integer(status.getId())))
					{
						validDetection = de;
						break;
					}
				} // looping through the detections

				// did we have a valid organic deteciton?
				if (validDetection == null)
				{
					// no, go through the remote senors
					// first pass through our organic sensors
					for (int i = 0; i < len; i++)
					{
						final ASSET.Models.Detection.DetectionEvent de = detections
								.getDetection(i);
						if (isThisValid(de, _myTarget, null))
						{
							validDetection = de;
							break;
						}
					} // looping through the detections
				}
			} // if we have any detections
		} // if the detections object was received

		if (validDetection == null)
		{
			// no, we've not found anything new

			// should we be continuing on our existing track
			if (_endStayOnBearing > time)
			{
				// yes, produce a new dem status (updating the time)
				_courseForLastValidDetection.setTime(time);

				// and return
				res = _courseForLastValidDetection;

				// create a short description
				activity = "Chasing lost contact";
			}
			else
			{
				// it must have expired, forget it
				_courseForLastValidDetection = null;

				// and forget about the expiry time
				_endStayOnBearing = -1;
			}
		}
		else
		{
			// yes, we've got a valid detection - produce a demanded course towards it

			// do we need to close on the target?
			WorldDistance rangeToTarget = validDetection.getRange();
			if (rangeToTarget.greaterThan(_allowable_error))
			{

				// store the description of the target
				activity = validDetection.toString();

				// base the demanded status on the current status
				res = new SimpleDemandedStatus(time, status);

				// and calculate what we need to do
				res = getDemanded(time, status, validDetection);

				// do we want to remember valid detections?
				if (_stayOnBearing != null)
				{
					// and remember it
					_courseForLastValidDetection = res;

					// update the expiry time
					_endStayOnBearing = time
							+ (long) _stayOnBearing.getValueIn(Duration.MILLISECONDS);
				}
			}
		}

		super.setLastActivity(activity);

		// ok, done
		return res;
	}

	/**
	 * calculate the course (in degs) from our location to the target, allowing
	 * for a third party sensor
	 * 
	 * @param myId
	 * @param myLocation
	 * @param detection
	 * @return
	 */
	protected double courseToTarget(int myId, WorldLocation myLocation,
			DetectionEvent detection)
	{
		double res = 0;

		// is this my sensor
		if (detection.getHost() == myId)
		{
			res = detection.getBearing().doubleValue();
		}
		else
		{
			// work out distance from us to the target, not from the sensor to the
			// target
			WorldLocation sensorLocation = detection.getSensorLocation();

			// Work out the estimated target location
			WorldVector sensorToTarget = new WorldVector(MWC.Algorithms.Conversions
					.Degs2Rads(detection.getBearing().doubleValue()), detection
					.getRange().getValueIn(WorldDistance.DEGS), 0);

			WorldLocation targetLocation = sensorLocation.add(sensorToTarget);

			// what's the bearing to the target location?
			WorldVector meToTarget = targetLocation.subtract(myLocation);
			res = MWC.Algorithms.Conversions.Rads2Degs(meToTarget.getBearing());
		}

		return res;
	}

	/**
	 * calculate the course (in degs) from our location to the target, allowing
	 * for a third party sensor
	 * 
	 * @param myId
	 * @param myLocation
	 * @param detection
	 * @return
	 */
	protected WorldDistance rangeToTarget(int myId, WorldLocation myLocation,
			DetectionEvent detection)
	{
		WorldDistance res = null;

		// is this my sensor
		if (detection.getHost() == myId)
		{
			res = detection.getRange();
		}
		else
		{
			// work out distance from us to the target, not from the sensor to the
			// target
			WorldLocation sensorLocation = detection.getSensorLocation();

			// Work out the estimated target location
			WorldVector sensorToTarget = new WorldVector(MWC.Algorithms.Conversions
					.Degs2Rads(detection.getBearing().doubleValue()), detection
					.getRange().getValueIn(WorldDistance.DEGS), 0);

			WorldLocation targetLocation = sensorLocation.add(sensorToTarget);

			// what's the bearing to the target location?
			WorldVector meToTarget = targetLocation.subtract(myLocation);
			res = new WorldDistance(meToTarget.getRange(), WorldDistance.DEGS);
		}

		return res;
	}

	/**
	 * get the course and speed we need to get back on track
	 */
	protected DemandedStatus getDemanded(long time,
			ASSET.Participants.Status status,
			final ASSET.Models.Detection.DetectionEvent detection)
	{
		SimpleDemandedStatus res = new SimpleDemandedStatus(time, status);

		final Float Brg = detection.getBearing();
		// check we have a value
		if (Brg != null)
		{
			final double brg = courseToTarget(status.getId(), status.getLocation(),
					detection);

			// set the course for starters
			res.setCourse(brg);

			// do we have a Height?
			if (_trailHeight != null)
			{
				res.setHeight(_trailHeight.getValueIn(WorldDistance.METRES));
			}

			// now work out the range
			WorldDistance rng = detection.getRange();

			// do we want to fall back to the estimated range?
			if (rng == null)
				rng = detection.getEstimatedRange();

			// do we have range data?
			if (rng != null)
			{
				// are we at trail range?
				final double rngYds = rng.getValueIn(WorldDistance.YARDS);

				// work out the minimum change in speed requested
				double spdChange = 0;
				if(isAllowSpeedChange())
					spdChange = Math.max(0.4, res.getSpeed() * 0.05);

				// work our our distance from the estimated target location
				WorldVector sensorToTarget = new WorldVector(MWC.Algorithms.Conversions
						.Degs2Rads(brg), rng.getValueIn(WorldDistance.DEGS), 0);

				WorldLocation tgtLocation = detection.getSensorLocation().add(
						sensorToTarget);

				// are we very far from this location?
				WorldVector hostToTarget = tgtLocation.subtract(status.getLocation());

				// modify our course so we are heading for the target location
				double brg_degs = MWC.Algorithms.Conversions.Rads2Degs(hostToTarget
						.getBearing());
				res.setCourse(brg_degs);

				// and what's this range in yards?
				double rangeToTargetYds = MWC.Algorithms.Conversions
						.Degs2Yds(hostToTarget.getRange());

				// are we very far from our required range?
				double rangeYds = _trailRange.getValueIn(WorldDistance.YARDS);
				final double distError = Math.abs(rangeToTargetYds - rangeYds);

				// is this outside our allowable error
				if (distError > _allowable_error.getValueIn(WorldDistance.YARDS))
				{
					// yes the errors too great
					if (rngYds > rangeYds)
					{
						// too far, speed up
						res.setSpeed(res.getSpeed() + spdChange);
					}
					else
					{
						// too close, slow down
						res.setSpeed(res.getSpeed() - spdChange);
					} // whether we are too fast or too slow
				} // whether we have fallen out of our 'envelope'
			} // if we know the range

		}

		return res;
	}

	/**
	 * reset this decision model
	 */
	public void restart()
	{
		//
		_endStayOnBearing = -1;

	}

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 * 
	 * @param currentStatus
	 */
	public void interrupted(Status currentStatus)
	{
		// ignore.
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
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
			_myEditor = new TrailInfo(this);

		return _myEditor;
	}

	public WorldDistance getAllowableError()
	{
		return _allowable_error;
	}

	public void setAllowableError(final WorldDistance val)
	{
		_allowable_error = val;
	}

	public WorldDistance getTrailRange()
	{
		return _trailRange;
	}

	public void setTrailRange(final WorldDistance val)
	{
		_trailRange = val;
	}
	
	public boolean isAllowSpeedChange()
	{
		return _allowSpeedChange;
	}

	public void setAllowSpeedChange(boolean allowSpeedChange)
	{
		_allowSpeedChange = allowSpeedChange;
	}



	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * get the version details for this model.
	 * 
	 * <pre>
	 * $Log: Trail.java,v $
	 * Revision 1.1  2006/08/08 14:21:28  Ian.Mayo
	 * Second import
	 * 
	 * Revision 1.1  2006/08/07 12:25:36  Ian.Mayo
	 * First versions
	 * 
	 * Revision 1.18  2004/09/02 13:17:26  Ian.Mayo
	 * Reflect CoreDecision handling the toString method
	 * 
	 * Revision 1.17  2004/09/01 10:42:24  Ian.Mayo
	 * Better javadoc
	 * <p/>
	 * Revision 1.16  2004/08/31 09:36:05  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.15  2004/08/26 13:22:21  Ian.Mayo
	 * Correct properties description, add property editing
	 * <p/>
	 * Revision 1.14  2004/08/25 11:20:17  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.13  2004/08/20 13:32:18  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.12  2004/08/17 14:21:57  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.11  2004/08/09 15:50:26  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.10  2004/08/06 12:51:54  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.9  2004/08/06 11:14:16  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.8  2004/05/24 15:46:35  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:51  ian
	 * no message
	 * <p/>
	 * Revision 1.7  2004/02/18 08:47:12  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.5  2003/11/05 09:20:10  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	public String getVersion()
	{
		return "$Date$";
	}

	static public class TrailInfo extends MWC.GUI.Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public TrailInfo(final Trail data)
		{
			super(data, data.getName(), "Trail");
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
				final java.beans.PropertyDescriptor[] res =
				{
						prop("TargetType", "the type of vessel we are trailing"),
						prop("TrailRange", "the range at which we trail"),
						prop("AllowableError",
								"the envelope allowed around the trail range"),
						prop("Name", "the name of this trail model"),
						prop("StayOnBearingTime",
								"the to continue on track after losing target"), };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static class TrailTest extends SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TrailTest(final String val)
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
			final Trail followBlue = new Trail(new WorldDistance(2000,
					WorldDistance.YARDS));
			return followBlue;
		}

		public void testTheTrail()
		{
			// setup the scenario
			final Trail followBlue = new Trail(new WorldDistance(2000,
					WorldDistance.YARDS));

			// setup the target
			final TargetType tgtType = new TargetType();
			tgtType.addTargetType(ASSET.Participants.Category.Type.SUBMARINE);
			followBlue.setTargetType(tgtType);

			// setup the status
			final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(
					0, 0, 0);
			final ASSET.Participants.Status stat = new ASSET.Participants.Status(0,
					1000);
			stat.setLocation(origin);
			stat.setCourse(0);
			stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

			// create the host
			final NetworkParticipant host = new ASSET.Models.Vessels.Surface(12);
			final CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(
					22);

			// and the detection list
			ASSET.Models.Detection.DetectionList list = new ASSET.Models.Detection.DetectionList();

			// /////////////////////////////////////////////////////////////
			// what do we do when there are no detections?

			// null detections
			DemandedStatus dem = followBlue
					.decide(stat, null, null, null, null, 2000);
			assertEquals("return null when no detections", null, dem);

			// zero detections
			dem = followBlue.decide(stat, null, null, list, null, 2000);
			assertEquals("return null when zero detections", null, dem);

			// //////////////////////////////////////////////////////////////
			// handle detections, but not of valid target
			final ASSET.Participants.Category tgt = new ASSET.Participants.Category();
			tgt.setForce(ASSET.Participants.Category.Force.RED);
			tgt.setType(ASSET.Participants.Category.Type.CARRIER);
			tgt.setEnvironment(ASSET.Participants.Category.Environment.SURFACE);

			final ASSET.Models.Detection.DetectionEvent det1 = new DetectionEvent(
					2000, host.getId(), null, sensor, null, null, null, null, null, tgt,
					null, null, new CoreParticipant(12));

			list.add(det1);

			dem = followBlue.decide(stat, null, null, list, null, 2000);
			assertEquals("return null when invalid detections", null, dem);

			// //////////////////////////////////////////////////////////////
			// handle detections including valid target

			final ASSET.Participants.Category tgt2 = new ASSET.Participants.Category();
			tgt2.setForce(ASSET.Participants.Category.Force.RED);
			tgt2.setType(ASSET.Participants.Category.Type.SUBMARINE);
			tgt2.setEnvironment(ASSET.Participants.Category.Environment.SUBSURFACE);
			final ASSET.Models.Detection.DetectionEvent det2 = new ASSET.Models.Detection.DetectionEvent(
					2000, host.getId(), stat.getLocation(), sensor, new WorldDistance(
							2000, WorldDistance.YARDS), new WorldDistance(2000,
							WorldDistance.YARDS), new Float(33), new Float(44), null, tgt2,
					null, null, new CoreParticipant(12));

			list.add(det2);

			dem = followBlue.decide(stat, null, null, list, null, 2000);
			SimpleDemandedStatus sds = (SimpleDemandedStatus) dem;
			assertTrue("returned demanded status", (dem != null));
			assertEquals("returned valid course to target", 33, sds.getCourse(),
					0.001);

			// //////////////////////////////////////////////////////////////
			// handle loss of valid target
			list = new ASSET.Models.Detection.DetectionList();
			list.add(det1);

			dem = followBlue.decide(stat, null, null, list, null, 2100);
			assertNotNull("return null when lost contact", dem);

			dem = followBlue.decide(stat, null, null, list, null,
					2000 + followBlue._endStayOnBearing + 500);
			assertEquals("return null when time expired", null, dem);

			// //////////////////////////////////////////////////////////////
			// regain target
			list.add(det2);

			dem = followBlue.decide(stat, null, null, list, null, 2000);
			sds = (SimpleDemandedStatus) dem;
			assertTrue("returned demanded status", (dem != null));
			assertEquals("returned valid course to target", 33, sds.getCourse(),
					0.001);

		}

		public void testGettingDemanded()
		{
			// //////////////////////////////////////////////////////////////
			// handle missing bearing

			// //////////////////////////////////////////////////////////////
			// handle missing range

		}
	}

}