package org.mwc.asset.comms.restlet.host;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.mwc.asset.comms.restlet.data.DecisionResource;
import org.mwc.asset.comms.restlet.data.DetectionResource;
import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ScenarioEventResource;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.Sensor;
import org.mwc.asset.comms.restlet.data.StatusResource;
import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;
import org.mwc.asset.comms.restlet.data.ScenarioEventResource.ScenarioEvent;
import org.mwc.asset.comms.restlet.data.StatusResource.MovedEvent;
import org.restlet.resource.ClientResource;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GenericData.WorldDistance;

abstract public class BaseHost implements ASSETHost
{

	/**
	 * people listening to a scenario
	 * 
	 */
	private HashMap<Integer, ScenarioSteppedList> _stepListeners;

	/**
	 * people listening to a particular participant
	 * 
	 */
	private HashMap<Integer, HashMap<Integer, ParticipantList>> _participantListeners;

	@Override
	public void setScenarioStatus(int scenarioId, final String newState)
	{
		ScenarioType scen = getScenario(scenarioId);
		if (newState.equals(ScenarioStateResource.START))
			scen.start();
		else if (newState.equals(ScenarioStateResource.STOP))
			scen.pause();
		else if (newState.equals(ScenarioStateResource.FASTER))
			scen.setStepTime(scen.getStepTime() / 2);
		else if (newState.equals(ScenarioStateResource.SLOWER))
			scen.setStepTime(scen.getStepTime() * 2);
		else
			System.err.println("UNSUPPORTED METHOD");

	}

	@Override
	public void deleteParticipantDetectionListener(int scenarioId,
			int participantId, int listenerId)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		ParticipantDetectedList detector = thisPList.getDetection();
		detector.remove(listenerId);

		// do we have any movement listeners?
		if (detector.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantDetectedListener(detector);
		}

	}

	@Override
	public int newParticipantDetectionListener(int scenarioId, int participantId,
			URI listener)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		ParticipantDetectedList detector = thisPList.getDetection();

		// do we have any movement listeners?
		if (detector.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantDetectedListener(detector);
		}

		int listId = detector.add(listener);
		return listId;
	}

	@Override
	public void deleteParticipantDecisionListener(int scenarioId,
			int participantId, int listenerId)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		ParticipantDecidedList decider = thisPList.getDecision();
		decider.remove(listenerId);

		// do we have any movement listeners?
		if (decider.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantDecidedListener(decider);
		}

	}

	@Override
	public int newParticipantDecisionListener(int scenarioId, int participantId,
			URI listener)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		ParticipantDecidedList decider = thisPList.getDecision();

		// do we have any movement listeners?
		if (decider.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantDecidedListener(decider);
		}

		int listId = decider.add(listener);
		return listId;
	}

	@Override
	public List<Participant> getParticipantsFor(int scenarioId)
	{
		Vector<Participant> res = new Vector<Participant>();
		Integer[] parts = getScenario(scenarioId).getListOfParticipants();
		for (int i = 0; i < parts.length; i++)
		{
			ParticipantType thisP = getScenario(scenarioId).getThisParticipant(
					parts[i]);
			Participant newP = new Participant(thisP);
			res.add(newP);
		}
		return res;
	}

	@Override
	public List<Sensor> getSensorsFor(int scenarioId, int participantId)
	{
		ParticipantType thisP = getScenario(scenarioId).getThisParticipant(
				participantId);

		List<Sensor> res = new Vector<Sensor>();
		SensorList sensors = thisP.getSensorFit();
		Collection<SensorType> iter = sensors.getSensors();
		for (Iterator<SensorType> iterator = iter.iterator(); iterator.hasNext();)
		{
			SensorType thisS = (SensorType) iterator.next();
			Sensor newS = new Sensor(thisS);
			res.add(newS);

		}

		return res;
	}

	@Override
	public DemandedStatus getDemandedStatus(int scenario, int participant)
	{
		return getScenario(scenario).getThisParticipant(participant)
				.getDemandedStatus();
	}

	@Override
	public void setDemandedStatus(int scenario, int participant,
			DemandedStatus demState)
	{
		// what's the current model
		ParticipantType thisP = getScenario(scenario).getThisParticipant(
				participant);
		DecisionType curModel = thisP.getDecisionModel();
		UserControl userC = null;
		if (curModel instanceof UserControl)
		{
			userC = (UserControl) curModel;
		}
		else
		{
			userC = new UserControl(0, null, null);
			thisP.setDecisionModel(userC);
		}

		SimpleDemandedStatus sds = (SimpleDemandedStatus) demState;
		userC.setCourse(sds.getCourse());
		userC.setSpeed(sds.getSpeedVal());
		userC.setDepth(new WorldDistance(-sds.getHeight(), WorldDistance.METRES));
	}

	/**
	 * get the specified block of listeners
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @return
	 */
	private ParticipantList getParticipantListFor(int scenarioId,
			int participantId)
	{
		// are we already listening to this scenario?
		if (_participantListeners == null)
		{
			_participantListeners = new HashMap<Integer, HashMap<Integer, ParticipantList>>();
		}

		HashMap<Integer, ParticipantList> thisSList = _participantListeners
				.get(scenarioId);
		if (thisSList == null)
		{
			thisSList = new HashMap<Integer, ParticipantList>();
			_participantListeners.put(scenarioId, thisSList);
		}

		ParticipantList thisPList = thisSList.get(participantId);

		if (thisPList == null)
		{
			thisPList = new ParticipantList();
			thisSList.put(participantId, thisPList);
		}

		return thisPList;

	}

	@Override
	public void deleteParticipantListener(int scenarioId, int participantId,
			int listenerId)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		ParticipantMovedList mover = thisPList.getMovement();
		mover.remove(listenerId);

		// do we have any movement listeners?
		if (mover.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantMovedListener(mover);
		}

	}

	@Override
	public int newParticipantListener(int scenarioId, int participantId, URI url)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		ParticipantMovedList mover = thisPList.getMovement();

		// do we have any movement listeners?
		if (mover.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantMovedListener(mover);
		}

		int listId = mover.add(url);
		return listId;
	}

	public ScenarioSteppedList getSteppedListFor(int scenarioId)
	{
		// are we already listening to this scenario?
		if (_stepListeners == null)
		{
			_stepListeners = new HashMap<Integer, ScenarioSteppedList>();
		}

		ScenarioSteppedList thisList = _stepListeners.get(scenarioId);

		if (thisList == null)
		{
			thisList = new ScenarioSteppedList()
			{
			};
			_stepListeners.put(scenarioId, thisList);
		}

		return thisList;
	}

	public void deleteScenarioListener(int scenarioId, int listenerId)
	{
		// are we already listening to this scenario?
		getSteppedListFor(scenarioId).remove(listenerId);
	}

	@Override
	public int newScenarioListener(int scenarioId, URI url)
	{
		return getSteppedListFor(scenarioId).add(url);
	}

	/**
	 * holder for events of our own special type
	 * 
	 * @author ianmayo
	 * 
	 */
	private static class ParticipantMovedList extends
			BaseListenerList<MovedEvent> implements ParticipantMovedListener
	{

		@Override
		public void restart(ScenarioType scenario)
		{
			// ignore, we learn about this via the scenaro steppers
		}

		protected void fireThisEvent(ClientResource client, MovedEvent event)
		{
			// does it have a scenario?
			StatusResource scenR = client.wrap(StatusResource.class);
			scenR.accept(event._status);
		}

		@Override
		public void moved(Status newStatus)
		{
			fireEvent(new MovedEvent(newStatus));
		}

	}

	/**
	 * holder for events of our own special type
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class ScenarioSteppedList extends
			BaseListenerList<ScenarioEvent> implements ScenarioSteppedListener,
			ParticipantsChangedListener
	{

		@Override
		public void restart(ScenarioType scenario)
		{
			fireEvent(new ScenarioEvent("Restart", "unknown", 0, 0));
		}

		@Override
		public void step(ScenarioType scenario, long newTime)
		{
			fireEvent(new ScenarioEvent("Step", "unknown", newTime, 0));
		}

		protected void fireThisEvent(ClientResource client, ScenarioEvent event)
		{
			// does it have a scenario?
			ScenarioEventResource scenR = client.wrap(ScenarioEventResource.class);
			try
			{
				System.out.println("%%% about to fire at:" + client);
				scenR.accept(event);
				client.release();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void newParticipant(int index)
		{
			fireEvent(new ScenarioEvent(AssetEvent.JOINED, "Participant:" + index
					+ " joined", 0, 0));
		}

		@Override
		public void participantRemoved(int index)
		{
			fireEvent(new ScenarioEvent(AssetEvent.LEFT, "Participant:" + index
					+ " left", 0, 0));
		}

	}

	private static class ParticipantList
	{
		final private ParticipantMovedList _movement;
		final private ParticipantDecidedList _decision;
		final private ParticipantDetectedList _detection;

		public ParticipantList()
		{
			_movement = new ParticipantMovedList();
			_decision = new ParticipantDecidedList();
			_detection = new ParticipantDetectedList();
		}

		public ParticipantDetectedList getDetection()
		{
			return _detection;
		}

		public ParticipantMovedList getMovement()
		{
			return _movement;
		}

		public ParticipantDecidedList getDecision()
		{
			return _decision;
		}

	}

	/**
	 * holder for events of our own special type
	 * 
	 * @author ianmayo
	 * 
	 */
	private static class ParticipantDecidedList extends
			BaseListenerList<DecidedEvent> implements ParticipantDecidedListener
	{

		@Override
		public void restart(ScenarioType scenario)
		{
			// ignore, we learn about this via the scenaro steppers
		}

		protected void fireThisEvent(ClientResource client, DecidedEvent event)
		{
			// does it have a scenario?
			DecisionResource scenR = client.wrap(DecisionResource.class);
			scenR.accept(event);
		}

		@Override
		public void newDecision(String description, DemandedStatus demStatus)
		{
			fireEvent(new DecidedEvent(demStatus, description));
		}

	}

	/**
	 * holder for events of our own special type
	 * 
	 * @author ianmayo
	 * 
	 */
	private static class ParticipantDetectedList extends
			BaseListenerList<DetectionEvent> implements ParticipantDetectedListener
	{

		protected void fireThisEvent(ClientResource client, DetectionEvent event)
		{
			// does it have a scenario?
			DetectionResource scenR = client.wrap(DetectionResource.class);
			scenR.accept(event);
		}

		@Override
		public void newDetections(DetectionList detections)
		{
			fireEvent(new DetectionResource.DetectionEvent(detections));
		}

		@Override
		public void restart(ScenarioType scenario)
		{
		}

	}
}