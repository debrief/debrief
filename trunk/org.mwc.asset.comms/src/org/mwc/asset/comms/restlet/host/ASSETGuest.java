package org.mwc.asset.comms.restlet.host;

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
		 * 
		 * @param newState
		 */
		public void newParticipantState(Status newState);

		
}
