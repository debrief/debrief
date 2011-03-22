package ASSET.Participants;

/**
 * Title:        ASSET Simulator
 * Description:  Advanced Scenario Simulator for Evaluation of Tactics
 * Copyright:    Copyright (c) 2001
 * Company:      PlanetMayo Ltd
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.*;

import ASSET.*;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.*;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import MWC.GenericData.*;

public class CoreParticipant implements ParticipantType, java.io.Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// //////////////////////////////////////////////////////
	// member variables
	// /////////////////////////////////////////////////////
	/**
	 * our current status (pos, course, speed)
	 */
	private Status _myStatus;

	/**
	 * our current demanded status (course, speed, depth)
	 */
	private DemandedStatus _myDemandedStatus;

	/**
	 * our initial status (pos, course, speed)
	 */
	private Status _myInitialStatus;

	/**
	 * our initial demanded status (course, speed, depth)
	 */
	private DemandedStatus _myInitialDemandedStatus;

	/**
	 * the current set of detections
	 */
	private DetectionList _myNewDetections;

	/**
	 * our list of third party detections (such as those from weapon in the water,
	 * sonar buoy)
	 */
	private ASSET.Models.Detection.DetectionList _myRemoteDetections = new DetectionList();

	/**
	 * our id number
	 */
	private int _myId;

	/**
	 * our name
	 */
	private String _myName;

	/**
	 * my movement model
	 */
	private ASSET.Models.MovementType _movement;

	/**
	 * my decision model
	 */
	private ASSET.Models.DecisionType _decisionModel;

	/**
	 * my radiation characteristics
	 */
	protected ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _radiatedChars = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();

	/**
	 * my self characteristics
	 */
	private ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _selfNoise = null;

	/**
	 * my movement characteristics
	 */
	private ASSET.Models.Movement.MovementCharacteristics _movementChars = null;

	/**
	 * my list of sensors
	 */
	private ASSET.Models.Sensor.SensorList _mySensors = new ASSET.Models.Sensor.SensorList();

	/**
	 * the last thing we did
	 */
	private String _myActivity = INACTIVE_DESCRIPTOR;

	/**
	 * the previous last thing we did. We keep track of the previous activity so
	 * that we can fire a new decision message if the the activitiy wihtin the
	 * behaviour has changed but the last behaviour hasn't
	 */
	private String _myLastActivity = INACTIVE_DESCRIPTOR;

	/**
	 * our category
	 */
	private Category _myCategory = new Category();

	/**
	 * list of people listening out for us moving
	 */
	private Vector<ParticipantMovedListener> _participantMovedListeners;

	/**
	 * list of people listening out for any decisions
	 */
	private Vector<ParticipantDecidedListener> _participantDecidedListeners;

	/**
	 * list of people listening out for any detections
	 */
	private Vector<ParticipantDetectedListener> _participantDetectedListeners;

	/**
	 * the message we use when there's F/A happending
	 */
	public static final String INACTIVE_DESCRIPTOR = "inactive";

	// //////////////////////////////////////////////////////
	// constructor
	// /////////////////////////////////////////////////////

	/**
	 * null constructor, used for reading in data from file?
	 */
	public CoreParticipant(final int id)
	{
		this(id, null, null, new java.util.Date().toString());
	}

	/**
	 * normal constructor
	 */
	protected CoreParticipant(final int id, final Status status,
			final DemandedStatus demStatus, final String name)
	{
		_myId = id;
		setStatus(status);
		setDemandedStatus(demStatus);

		_movement = new ASSET.Models.Movement.CoreMovement();
		_myName = name;
		_myNewDetections = new DetectionList();
	}

	// //////////////////////////////////////////////////////
	// constructor
	// /////////////////////////////////////////////////////

	public String toString()
	{
		return getName();
	}

	/**
	 * the name of this participant
	 */
	public String getName()
	{
		return _myName;
	}

	/**
	 * the name of this participant
	 */
	public void setName(final String val)
	{
		_myName = val;
	}

	/**
	 * the status of this participant
	 */
	public Status getStatus()
	{
		return _myStatus;
	}

	/**
	 * the demanded status of this participant
	 */
	public DemandedStatus getDemandedStatus()
	{
		return _myDemandedStatus;
	}

	/**
	 * perform the decision portion of the step
	 */
	public void doDecision(long oldTime, long newTime, ScenarioType scenario)
	{
		// ////////////////////////////////////////////////
		// pre-processing for special situation on first step
		// ////////////////////////////////////////////////
		// just check if we have a time value for our status
		if (_myStatus.getTime() == -1)
		{
			_myStatus.setTime(scenario.getTime());
		}

		// do we have a demanded status? We may not do in the first step
		if (_myDemandedStatus == null)
			_myDemandedStatus = new SimpleDemandedStatus(_myStatus.getTime(),
					_myStatus);

		final DemandedStatus oldDemStatus = _myDemandedStatus;

		// do decision cycle
		if (_decisionModel != null)
		{
			// take a copy of the demanded status for the decision models to use

			// check if this is our first step (& we don't have a dem status)
			DemandedStatus copyDemStatus = null;
			if (_myDemandedStatus != null)
				copyDemStatus = DemandedStatus.copy(newTime, _myDemandedStatus);

			_myDemandedStatus = _decisionModel.decide(_myStatus,
					this.getMovementChars(), copyDemStatus, _myNewDetections, scenario,
					newTime);
		}

		// if we haven't got a way ahead, continue in steady state
		if (_myDemandedStatus == null)
		{
			_myDemandedStatus = new SimpleDemandedStatus(_myStatus.getTime(),
					_myStatus);
			_myActivity = INACTIVE_DESCRIPTOR;
		}
		else
		{
			if (_decisionModel != null)
				_myActivity = _decisionModel.getActivity();
			else
				_myActivity = "Decision model missing";
		}

		// and fire the update
		if ((oldDemStatus != _myDemandedStatus)
				|| (!_myActivity.equals(_myLastActivity)))
			fireDecided(_myActivity, _myDemandedStatus);

		_myLastActivity = _myActivity;
	}

	/**
	 * perform the detection portion of the step
	 */
	public void doDetection(long oldtime, long newTime, ScenarioType scenario)
	{
		// do detection cycle
		if (_mySensors != null)
			_mySensors.detects(scenario.getEnvironment(), _myNewDetections, this,
					scenario, newTime);

		// get detections from any remote sensors (sonar buoy, weapon in the water,
		// ..)
		_myNewDetections.extend(_myRemoteDetections);

		// and clear the list of remote detections
		_myRemoteDetections.clear();

		// @@ is this right? we fire a detections event whether there were
		// any or not? This does allow the listeners to find out
		// that contact has been lost
		fireDetected(_myNewDetections);
	}

	/**
	 * perform the movement portion of the step
	 */
	public void doMovement(long oldtime, long newTime, ScenarioType scenario)
	{

		// remember the old status
		final Status oldStatus = _myStatus;

		// check we have movement
		if (_movement == null)
		{
			_movement = new ASSET.Models.Movement.CoreMovement();
		}

		// do the movement step
		doMovement(newTime);

		// send updates as necessary
		if (oldStatus != _myStatus)
			fireMoved(_myStatus);

	}

	/**
	 * do the movement portion of the step
	 * 
	 * @param newTime
	 *          the new model time we're stepping to
	 */
	private void doMovement(final long newTime)
	{

		// see if there are any sensor lineup changes
		if (getSensorFit() != null)
		{
			Iterator<SensorType> theSensors = getSensorFit().getSensors().iterator();

			// do we have any demanded sensor states?
			if (_myDemandedStatus != null)
			{
				Vector<DemandedSensorStatus> theStates = _myDemandedStatus
						.getSensorStates();
				if (theStates != null)
				{
					if (theStates.size() > 0)
					{

						// yes, work through them
						for (int i = 0; i < theStates.size(); i++)
						{
							DemandedSensorStatus sensorStatus = theStates.elementAt(i);

							// and inform each sensor
							while (theSensors.hasNext())
							{
								SensorType thisSensor = theSensors.next();
								if (thisSensor.getMedium() == sensorStatus.getMedium())
								{
									thisSensor.inform(sensorStatus);
								}
							}
						}
					}
				}
			}

			// let the sensors update themselves
			theSensors = getSensorFit().getSensors().iterator();
			while (theSensors.hasNext())
			{
				SensorType sensor = theSensors.next();
				sensor.update(_myDemandedStatus, _myStatus, newTime);
			}
		}

		// if we have a set of movement chars, do the move.
		if (_movementChars != null)
		{
			// do movement itself
			_myStatus = _movement.step(newTime, _myStatus, _myDemandedStatus,
					_movementChars);
		}
		
		// do the state update cycle
		updateStates(newTime);

	}

	/**
	 * use our state models to update the current set of vessel states
	 * 
	 * @param newTime
	 *          the time we are stepping to
	 */
	protected void updateStates(final long newTime)
	{
		// nothing to do in core class
	}

	/**
	 * get the id of this participant
	 */
	public int getId()
	{
		return _myId;
	}

	/**
	 * set the id of this participant
	 */
	public void setId(final int val)
	{
		_myId = val;

		// update the id in the status
		_myStatus.setId(_myId);
	}

	/**
	 * set the maximum speed of this participant (kts)
	 */
	public ASSET.Models.Movement.MovementCharacteristics getMovementChars()
	{
		return _movementChars;
	}

	/**
	 * find the list of detections for this participant
	 */
	public ASSET.Models.Detection.DetectionList getAllDetections()
	{
		DetectionList res = null;
		if (_mySensors != null)
			res = _mySensors.getAllDetections();

		return res;
	}

	/**
	 * find the list of current detections
	 */
	public DetectionList getNewDetections()
	{
		return _myNewDetections;
	}

	/**
	 * pass on the list of new detections
	 */
	public void newDetections(DetectionList detections)
	{
		// extend our list of third party detections with this one
		_myRemoteDetections.extend(detections);
	}

	/**
	 * set the get the movement model used by this vessel
	 */
	public ASSET.Models.MovementType getMovementModel()
	{
		return _movement;
	}

	/**
	 * the energy radiation characteristics for this participant
	 */
	public ASSET.Models.Vessels.Radiated.RadiatedCharacteristics getRadiatedChars()
	{
		return _radiatedChars;
	}

	/**
	 * the self-noise characteristics for this participant
	 */
	public ASSET.Models.Vessels.Radiated.RadiatedCharacteristics getSelfNoise()
	{
		RadiatedCharacteristics res = null;
		// do we have a self noise?
		if (_selfNoise == null)
			res = _radiatedChars;
		else
			res = _selfNoise;

		return res;
	}

	/**
	 * get the radiated noise of this participant in this bearing in this medium
	 */
	public double getRadiatedNoiseFor(final int medium, final double brg_degs)
	{
		double vesselRadiated = _radiatedChars.radiatedEnergyFor(medium,
				getStatus(), brg_degs);
		double sensorRadiated = _mySensors.getRadiatedNoiseFor(medium);
		double res = vesselRadiated + sensorRadiated;
		return res;
	}

	/**
	 * get the self noise of this participant in this bearing in this medium
	 */
	public double getSelfNoiseFor(final int medium, final double brg_degs)
	{
		double res = getSelfNoise()
				.radiatedEnergyFor(medium, getStatus(), brg_degs);
		return res;
	}

	/**
	 * find out if this participant radiates this type of noise
	 * 
	 * @param medium
	 *          the medium we're looking for
	 * @return yes/no
	 */
	public boolean radiatesThisNoise(final int medium)
	{
		// does the vessel radiate this?
		boolean vesselRadiates = _radiatedChars.radiatesThis(medium);

		// do any of the sensors radiate this?
		boolean sensorRadiates = _mySensors.radiatesThisMedium(medium);

		// return the combination of the two
		return (vesselRadiates || sensorRadiates);
	}

	/**
	 * reset, to go back to the initial state
	 */
	@SuppressWarnings("unchecked")
	public void restart(ScenarioType scenario)
	{
		// reset the statuses
		_myStatus = new Status(_myInitialStatus);
		_myDemandedStatus = DemandedStatus.copy(_myStatus.getTime(),
				_myInitialDemandedStatus);

		// reset the other data
		_myActivity = "inactive";

		// reset the behaviours
		if (_decisionModel != null)
			_decisionModel.restart();

		// reset the sensors
		if (_mySensors != null)
			_mySensors.restart();

		// inform the listeners
		if (_participantDecidedListeners != null)
		{
			// work on a copy of the list, in case it gets modified
			Vector<ParticipantDecidedListener> tmpListeners = (Vector<ParticipantDecidedListener>) _participantDetectedListeners
					.clone();
			final Iterator<ParticipantDecidedListener> it = tmpListeners.iterator();
			while (it.hasNext())
			{
				final ParticipantDecidedListener pdl = it.next();
				pdl.restart(scenario);
			}
		}

		if (_participantDetectedListeners != null)
		{
			// work on a copy of the list, in case it gets modified
			Vector<ParticipantDetectedListener> tmpListeners = (Vector<ParticipantDetectedListener>) _participantDetectedListeners
					.clone();
			final Iterator<ParticipantDetectedListener> it = tmpListeners.iterator();
			while (it.hasNext())
			{
				final ParticipantDetectedListener ptl = it.next();
				ptl.restart(scenario);
			}
		}

		if (_participantMovedListeners != null)
		{
			// work on a copy of the list, in case it gets modified
			Vector<ParticipantMovedListener> tmpListeners = (Vector<ParticipantMovedListener>) _participantMovedListeners
					.clone();

			final Iterator<ParticipantMovedListener> it = tmpListeners.iterator();
			while (it.hasNext())
			{
				final ParticipantMovedListener pml = it.next();
				pml.restart(scenario);
			}
		}

	}

	/**
	 * set the decision model for this participant
	 */
	public void setDecisionModel(final ASSET.Models.DecisionType decision)
	{
		_decisionModel = decision;
	}

	/**
	 * get the decision model for this participant
	 */
	public ASSET.Models.DecisionType getDecisionModel()
	{
		// give us a chain model if we need it
		if (_decisionModel == null)
			_decisionModel = new ASSET.Models.Decision.Waterfall();

		return _decisionModel;
	}

	/**
	 * get the number of sensors we hold
	 */
	public int getNumSensors()
	{
		int res = 0;
		if (_mySensors != null)
			res = _mySensors.getNumSensors();

		return res;
	}

	/**
	 * get the sensors for this participant
	 * 
	 * @return
	 */
	public SensorList getSensorFit()
	{
		return _mySensors;
	}

	/**
	 * get the indicated sensro
	 * 
	 * @param index
	 * @return
	 */
	public ASSET.Models.SensorType getSensorAt(final int index)
	{
		return _mySensors.getSensor(index);
	}

	/**
	 * set the movement model for this participant
	 */
	public void setMovementModel(final ASSET.Models.MovementType movement)
	{
		_movement = movement;
	}

	@Override
	public WorldDistance rangeFrom(WorldLocation point)
	{
		double dist = getStatus().getLocation().rangeFrom(point);
		WorldDistance res = new WorldDistance(dist, WorldDistance.DEGS);
		return res;
	}

	/**
	 * add a sensor to this participant
	 */
	public void addSensor(final ASSET.Models.SensorType sensor)
	{
		_mySensors.add(sensor);
	}

	/**
	 * return what this participant is currently doing
	 */
	public String getActivity()
	{
		return _myActivity;
	}

	/**
	 * return the category of the target
	 */
	public Category getCategory()
	{
		return _myCategory;
	}

	/**
	 * set the category
	 */
	public void setCategory(final Category val)
	{
		_myCategory = val;
	}

	/**
	 * set the initial status for this vessel
	 */
	public void setInitialStatus(final Status val)
	{
		_myInitialStatus = val;
	}

	/**
	 * set the status
	 */
	public void setStatus(final Status val)
	{
		// just check we have the right id for this status
		if (val != null)
		{
			val.setId(this.getId());

			// and store it
			_myStatus = val;

			// copy it to the initial status, if we haven't already got one
			if (_myInitialStatus == null)
			{
				_myInitialStatus = new Status(val);
			}
			else
			{
				// just do a quick check to ensure that we're not working with a dummy
				// status
				if (_myInitialStatus.getLocation() == null)
					_myInitialStatus = new Status(val);
			}
		}

	}

	/**
	 * set the demanded status for this participant
	 */
	public void setDemandedStatus(final DemandedStatus val)
	{
		_myDemandedStatus = val;
		if (_myDemandedStatus != null)
			_myInitialDemandedStatus = DemandedStatus.copy(
					_myDemandedStatus.getTime(), _myDemandedStatus);
	}

	/**
	 * set the sensor fit
	 */
	public void setSensorFit(final ASSET.Models.Sensor.SensorList sensorList)
	{
		_mySensors = sensorList;
	}

	/**
	 * set the movement characteristics
	 */
	public void setMovementChars(
			final ASSET.Models.Movement.MovementCharacteristics moveChars)
	{
		_movementChars = moveChars;
	}

	/**
	 * set the radiated noise characteristics
	 */
	public void setRadiatedChars(
			final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics radChars)
	{
		_radiatedChars = radChars;
	}

	/**
	 * set the self noise characteristics
	 */
	public void setSelfNoise(
			final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics selfChars)
	{
		_selfNoise = selfChars;
	}

	// ///////////////////////////////////////////////////
	// manage our listeners
	// ///////////////////////////////////////////////////
	public void addParticipantMovedListener(final ParticipantMovedListener list)
	{
		if (_participantMovedListeners == null)
			_participantMovedListeners = new Vector<ParticipantMovedListener>(1, 2);

		_participantMovedListeners.add(list);
	}

	public void removeParticipantMovedListener(final ParticipantMovedListener list)
	{
		if (_participantMovedListeners != null)
			_participantMovedListeners.remove(list);
	}

	public void addParticipantDecidedListener(
			final ParticipantDecidedListener list)
	{
		if (_participantDecidedListeners == null)
			_participantDecidedListeners = new Vector<ParticipantDecidedListener>(1,
					2);

		_participantDecidedListeners.add(list);
	}

	public void removeParticipantDecidedListener(
			final ParticipantDecidedListener list)
	{
		if (_participantDecidedListeners != null)
			_participantDecidedListeners.remove(list);
	}

	public void addParticipantDetectedListener(
			final ParticipantDetectedListener list)
	{
		if (_participantDetectedListeners == null)
			_participantDetectedListeners = new Vector<ParticipantDetectedListener>(
					1, 2);

		_participantDetectedListeners.add(list);
	}

	public void removeParticipantDetectedListener(
			final ParticipantDetectedListener list)
	{
		if (_participantDetectedListeners != null)
			_participantDetectedListeners.remove(list);
	}

	private void fireMoved(final Status newStatus)
	{
		if (_participantMovedListeners != null)
		{
			List<ParticipantMovedListener> syncL = Collections
					.synchronizedList(_participantMovedListeners);
			final Iterator<ParticipantMovedListener> it = syncL.iterator();
			while (it.hasNext())
			{
				final ParticipantMovedListener pml = it.next();
				pml.moved(newStatus);
			}
		}
	}

	protected void fireDetected(final ASSET.Models.Detection.DetectionList list)
	{
		if (_participantDetectedListeners != null)
		{
			final Iterator<ParticipantDetectedListener> it = _participantDetectedListeners
					.iterator();
			while (it.hasNext())
			{
				final ParticipantDetectedListener pml = it.next();
				pml.newDetections(list);
			}
		}
	}

	private void fireDecided(final String decision,
			final DemandedStatus dem_status)
	{
		if (_participantDecidedListeners != null)
		{
			final Iterator<ParticipantDecidedListener> it = _participantDecidedListeners
					.iterator();
			while (it.hasNext())
			{
				final ParticipantDecidedListener pml = it.next();
				pml.newDecision(decision, dem_status);
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ParticipantTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public ParticipantTest(final String val)
		{
			super(val);
		}

		ASSET.Participants.Status newStat = null;

		String dem_state = null;

		ASSET.Participants.DemandedStatus dem_status = null;

		ASSET.Models.Detection.DetectionList newDetectionList = null;

		protected class DecidedListener implements ParticipantDecidedListener
		{
			public void newDecision(final String state,
					final ASSET.Participants.DemandedStatus dem_stat)
			{
				dem_state = state;
				dem_status = dem_stat;
			}

			public void restart(ScenarioType scenario)
			{

			}
		}

		public class DetectedListener implements ParticipantDetectedListener
		{
			public void newDetections(final ASSET.Models.Detection.DetectionList list)
			{
				newDetectionList = list;
			}

			public void restart(ScenarioType scenario)
			{

			}
		}

		protected class MovedListener implements ParticipantMovedListener
		{
			public void moved(final ASSET.Participants.Status newStatus)
			{
				newStat = newStatus;
			}

			public void restart(ScenarioType scenario)
			{

			}
		}

		public void testParticipantStepping()
		{
			// setup the world model
			MWC.GenericData.WorldLocation
					.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

			final ParticipantType part = new CoreParticipant(12);
			part.setMovementModel(new ASSET.Models.Movement.CoreMovement());
			part.setMovementChars(new SSMovementCharacteristics("here",
					new WorldAcceleration(1, WorldAcceleration.M_sec_sec),
					new WorldAcceleration(1, WorldAcceleration.M_sec_sec), 0,
					new WorldSpeed(20, WorldSpeed.M_sec), new WorldSpeed(1,
							WorldSpeed.M_sec), new WorldDistance(200, WorldDistance.METRES),
					new WorldSpeed(1, WorldSpeed.M_sec), new WorldSpeed(1,
							WorldSpeed.M_sec), new WorldDistance(100, WorldDistance.METRES),
					new WorldDistance(1, WorldDistance.METRES)));

			// initialise the participant
			final Status newStat1 = new Status(part.getId(), 0);
			newStat1.setLocation(new MWC.GenericData.WorldLocation(0.0, 0.0, 0));
			newStat1.setCourse(45);
			newStat1.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
			part.setStatus(newStat1);

			// create the dummy decision model
			final ASSET.Models.Decision.Movement.Transit transit = new ASSET.Models.Decision.Movement.Transit();
			transit.setLoop(false);
			final MWC.GenericData.WorldPath path = new MWC.GenericData.WorldPath();
			path.addPoint(new MWC.GenericData.WorldLocation(1, 1, 0));
			path.addPoint(new MWC.GenericData.WorldLocation(1.1, 0.9, 0));
			transit.setDestinations(path);
			part.setDecisionModel(transit);

			// setup the listeners
			final MovedListener mover = new MovedListener();
			part.addParticipantMovedListener(mover);

			final DecidedListener decider = new DecidedListener();
			part.addParticipantDecidedListener(decider);

			// give him some sensors

			// make first step
			final ASSET.Scenario.CoreScenario theScenario = new ASSET.Scenario.CoreScenario();
			part.doDecision(0, 10000, theScenario);
			part.doMovement(0, 10000, theScenario);
			part.doDetection(0, 10000, theScenario);

			assertEquals("moved forward correct time", 10000, this.newStat.getTime());
			assertEquals("new decision activity is correct", transit.getActivity(),
					this.dem_state);
			assertEquals("new dem state is correct", 45d,
					((SimpleDemandedStatus) this.dem_status).getCourse(), 0.001);
			assertEquals("no detections made (no sensors)", null,
					this.newDetectionList);

			// make second step
			part.doDecision(10000, 20000, theScenario);
			part.doMovement(10000, 20000, theScenario);
			part.doDetection(10000, 20000, theScenario);
			assertEquals("moved forward correct time", 20000, this.newStat.getTime());
			assertEquals("new decision activity is correct", transit.getActivity(),
					this.dem_state);
			assertEquals("new dem state is correct", 45d,
					((SimpleDemandedStatus) this.dem_status).getCourse(), 0.001);
			assertEquals("no detections made (no sensors)", null,
					this.newDetectionList);

		}
	}

}