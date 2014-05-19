package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;

import ASSET.Participants.Status;

/**
 * methods exposed by object capable of acting as ASSET Host in networked
 * simulation
 * 
 * @author ianmayo
 * 
 */
public interface ASSETGuest
{
	interface GuestProvider
	{
		/**
		 * get the host object
		 * 
		 * @return
		 */
		public ASSETGuest getGuest();
	}

	/**
	 * something has changed in the scenario
	 * 
	 * @param description2
	 * @param msg
	 */
	public void newScenarioEvent(long time, String eventName, String description);

	/**
	 * someone we are listening to has moved
	 * 
	 * @param scenarioId
	 *          the scenario
	 * @param participantId
	 *          the participant
	 * @param newState
	 */
	public void newParticipantState(int scenarioId, int participantId,
			Status newState);

	/**
	 * someone we are listening to has detected something
	 * 
	 * @param scenarioId
	 *          the scenario
	 * @param participantId
	 *          the participant
	 * @param newState
	 */
	public void newParticipantDetection(int scenarioId, int participantId,
			DetectionEvent event);

	/**
	 * someone has made a decision
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param event
	 */

	public void newParticipantDecision(int scenarioId, int participantId,
	DecidedEvent event);

}
