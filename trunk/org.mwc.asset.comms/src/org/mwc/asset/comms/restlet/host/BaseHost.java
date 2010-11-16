package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.StatusResource;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource.ScenarioEvent;
import org.mwc.asset.comms.restlet.data.StatusResource.MovedEvent;
import org.restlet.resource.ClientResource;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioSteppedListener;

abstract public class BaseHost implements ASSETHost
{

	private HashMap<Integer, ScenarioSteppedList> _stepListeners;
	private HashMap<Integer, HashMap<Integer, ParticipantList>> _participantListeners;

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

	/**
	 * get the specified block of listeners
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @return
	 */
	public ParticipantList getParticipantListFor(int scenarioId, int participantId)
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
			thisSList.put(scenarioId, thisPList);
		}

		return thisPList;

	}

	@Override
	public void deleteParticipantListener(int scenarioId, int participantId,
			int listenerId)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);
		thisPList.getMovement().remove(listenerId);
		
		// do we have any movement listeners?
		if (thisPList.getMovement().size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.removeParticipantMovedListener(thisPList.getMovement());
		}

	}

	@Override
	public int newParticipantListener(int scenarioId, int participantId, URL url)
	{
		ParticipantList thisPList = this.getParticipantListFor(scenarioId,
				participantId);

		// do we have any movement listeners?
		if (thisPList.getMovement().size() == 0)
		{
			// nope, better register
			getScenario(scenarioId).getThisParticipant(participantId)
					.addParticipantMovedListener(thisPList.getMovement());
		}

		int listId = thisPList.getMovement().add(url);
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
	public int newScenarioListener(int scenarioId, URL url)
	{
		return getSteppedListFor(scenarioId).add(url);
	}

	/**
	 * holder for events of our own special type
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class ParticipantMovedList extends BaseListenerList<MovedEvent>
			implements ParticipantMovedListener
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
			ScenarioStateResource scenR = client.wrap(ScenarioStateResource.class);
			scenR.accept(event);
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

	public static class ParticipantList
	{
		final private ParticipantMovedList _movement;

		public ParticipantList()
		{
			_movement = new ParticipantMovedList();
		}

		public ParticipantMovedList getMovement()
		{
			return _movement;
		}

	}

}