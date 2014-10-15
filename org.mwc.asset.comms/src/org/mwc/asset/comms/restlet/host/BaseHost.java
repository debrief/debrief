/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

	public void setScenarioStatus(final int scenarioId, final String newState)
	{
		final ScenarioType scen = getScenario(scenarioId);
		if (newState == ScenarioStateResource.START)
			scen.start();
		else if (newState == ScenarioStateResource.STOP)
			scen.pause();
		else if (newState == ScenarioStateResource.FASTER)
			System.err.println("faster not supported");
		else if (newState == ScenarioStateResource.SLOWER)
			System.err.println("slower not supported");
		else
			System.err.println("UNSUPPORTED METHOD");

	}

	public void deleteParticipantDetectionListener(final int scenarioId,
			final int participantId, final int listenerId)
	{
		final ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		final ParticipantDetectedList detector = thisPList.getDetection();
		detector.remove(listenerId);

		// do we have any movement listeners?
		if (detector.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantDetectedListener(detector);
		}

	}

	public int newParticipantDetectionListener(final int scenarioId, final int participantId,
			final URI listener)
	{
		final ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		final ParticipantDetectedList detector = thisPList.getDetection();

		// do we have any movement listeners?
		if (detector.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantDetectedListener(detector);
		}

		final int listId = detector.add(listener);
		return listId;
	}

	public void deleteParticipantDecisionListener(final int scenarioId,
			final int participantId, final int listenerId)
	{
		final ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		final ParticipantDecidedList decider = thisPList.getDecision();
		decider.remove(listenerId);

		// do we have any movement listeners?
		if (decider.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantDecidedListener(decider);
		}

	}

	public int newParticipantDecisionListener(final int scenarioId, final int participantId,
			final URI listener)
	{
		final ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		final ParticipantDecidedList decider = thisPList.getDecision();

		// do we have any movement listeners?
		if (decider.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantDecidedListener(decider);
		}

		final int listId = decider.add(listener);
		return listId;
	}

	public List<Participant> getParticipantsFor(final int scenarioId)
	{
		final Vector<Participant> res = new Vector<Participant>();
		final Integer[] parts = getScenario(scenarioId).getListOfParticipants();
		for (int i = 0; i < parts.length; i++)
		{
			final ParticipantType thisP = getScenario(scenarioId).getThisParticipant(
					parts[i]);
			final Participant newP = new Participant(thisP);
			res.add(newP);
		}
		return res;
	}

	public List<Sensor> getSensorsFor(final int scenarioId, final int participantId)
	{
		final ParticipantType thisP = getScenario(scenarioId).getThisParticipant(
				participantId);

		final List<Sensor> res = new Vector<Sensor>();
		final SensorList sensors = thisP.getSensorFit();
		final Collection<SensorType> iter = sensors.getSensors();
		for (final Iterator<SensorType> iterator = iter.iterator(); iterator.hasNext();)
		{
			final SensorType thisS = (SensorType) iterator.next();
			final Sensor newS = new Sensor(thisS);
			res.add(newS);

		}

		return res;
	}


	public DemandedStatus getDemandedStatus(final int scenario, final int participant)
	{
		return getScenario(scenario).getThisParticipant(participant)
				.getDemandedStatus();
	}

	public void setDemandedStatus(final int scenario, final int participant,
			final DemandedStatus demState)
	{
		// what's the current model
		final ParticipantType thisP = getScenario(scenario).getThisParticipant(participant);
		final DecisionType curModel = thisP.getDecisionModel();
		UserControl userC = null;
		if(curModel instanceof UserControl)
		{
			userC = (UserControl) curModel;
		}
		else
		{
			userC = new UserControl(0,null,null);
			thisP.setDecisionModel(userC);
		}
		
		final SimpleDemandedStatus sds = (SimpleDemandedStatus) demState;
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
	private ParticipantList getParticipantListFor(final int scenarioId,
			final int participantId)
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

	public void deleteParticipantListener(final int scenarioId, final int participantId,
			final int listenerId)
	{
		final ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		final ParticipantMovedList mover = thisPList.getMovement();
		mover.remove(listenerId);

		// do we have any movement listeners?
		if (mover.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantMovedListener(mover);
		}

	}

	public int newParticipantListener(final int scenarioId, final int participantId, final URI url)
	{
		final ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		final ParticipantMovedList mover = thisPList.getMovement();

		// do we have any movement listeners?
		if (mover.size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantMovedListener(mover);
		}

		final int listId = mover.add(url);
		return listId;
	}

	public ScenarioSteppedList getSteppedListFor(final int scenarioId)
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

	public void deleteScenarioListener(final int scenarioId, final int listenerId)
	{
		// are we already listening to this scenario?
		getSteppedListFor(scenarioId).remove(listenerId);
	}

	public int newScenarioListener(final int scenarioId, final URI url)
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

		public void restart(final ScenarioType scenario)
		{
			// ignore, we learn about this via the scenaro steppers
		}

		protected void fireThisEvent(final ClientResource client, final MovedEvent event)
		{
			// does it have a scenario?
			final StatusResource scenR = client.wrap(StatusResource.class);
			scenR.accept(event._status);
		}

		public void moved(final Status newStatus)
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

		public void restart(final ScenarioType scenario)
		{
			fireEvent(new ScenarioEvent("Restart", "unknown", 0, 0));
		}

		public void step(final ScenarioType scenario, final long newTime)
		{
			fireEvent(new ScenarioEvent("Step", "unknown", newTime, 0));
		}

		protected void fireThisEvent(final ClientResource client, final ScenarioEvent event)
		{
			// does it have a scenario?
			final ScenarioEventResource scenR = client.wrap(ScenarioEventResource.class);
			scenR.accept(event);
		}

		public void newParticipant(final int index)
		{
			fireEvent(new ScenarioEvent(AssetEvent.JOINED, "Participant:" + index
					+ " joined", 0, 0));
		}

		public void participantRemoved(final int index)
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

		public void restart(final ScenarioType scenario)
		{
			// ignore, we learn about this via the scenaro steppers
		}

		protected void fireThisEvent(final ClientResource client, final DecidedEvent event)
		{
			// does it have a scenario?
			final DecisionResource scenR = client.wrap(DecisionResource.class);
			scenR.accept(event);
		}

		public void newDecision(final String description, final DemandedStatus demStatus)
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

		protected void fireThisEvent(final ClientResource client, final DetectionEvent event)
		{
			// does it have a scenario?
			final DetectionResource scenR = client.wrap(DetectionResource.class);
			scenR.accept(event);
		}

		public void newDetections(final DetectionList detections)
		{
			fireEvent(new DetectionResource.DetectionEvent(detections));
		}

		public void restart(final ScenarioType scenario)
		{
		}

	}
}