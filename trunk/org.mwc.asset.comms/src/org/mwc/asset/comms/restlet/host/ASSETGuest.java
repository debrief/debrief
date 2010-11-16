package org.mwc.asset.comms.restlet.host;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;


/** methods exposed by object capable of acting as ASSET Host in networked simulation
 * 
 * @author ianmayo
 *
 */
public interface ASSETGuest
{
		public interface GuestProvider
		{
			/** get the host object
			 * 
			 * @return
			 */
			public ASSETGuest getGuest();
		}
		
		/** something has changed in the scenario
		 * @param description2 
		 * @param msg
		 */
		public void newScenarioStatus(long time, String eventName, String description);
		
		/** someone we are listening to has moved
		 * @param scenarioId the scenario
		 * @param participantId the participant
		 * @param newState
		 */
		public void newParticipantState(int scenarioId, int participantId, Status newState);

		/** someone we are listening to has decided
		 * @param scenarioId the scenario
		 * @param participantId the participant
		 * @param newState
		 */
		public void newParticipantDecision(int scenarioId, int participantId, DemandedStatus demState);

		/** someone we are listening to has decided
		 * @param scenarioId the scenario
		 * @param participantId the participant
		 * @param newState
		 */
		public void newParticipantDetection(int scenarioId, int participantId, int sensorId,
				DetectionList dList);

}
