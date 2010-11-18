package org.mwc.asset.comms.restlet.host;

import java.net.URI;
import java.util.List;

import org.mwc.asset.comms.restlet.data.Sensor;
import org.mwc.asset.comms.restlet.data.ParticipantsResource.ParticipantsList;
import org.mwc.asset.comms.restlet.data.Scenario.ScenarioList;

import ASSET.ScenarioType;
import ASSET.Participants.DemandedStatus;

/** methods exposed by object capable of acting as ASSET Host in networked simulation
 * 
 * @author ianmayo
 *
 */
public interface ASSETHost
{
	/** how to get at the host object
	 * 
	 * @author ianmayo
	 *
	 */
	interface HostProvider
	{
		/** get the host object
		 * 
		 * @return
		 */
		public ASSETHost getHost();
	}
	
	
	/** get hold of the specified scenario
	 * 
	 * @param scenarioId
	 * @return
	 */
	public ScenarioType getScenario(int scenarioId);

	/** somebody new wants to listen to us
	 * 
	 * @param scenario
	 * @param url
	 * @return
	 */
	public int newScenarioListener(int scenario, URI url);
	
	/** somebody wants to stop listening to us
	 * @param scenario subject scenario
	 * @param listenerId
	 */
	public void deleteScenarioListener(int scenario, int listenerId);

	/** get a list of scenarios we know about
	 * 
	 * @return
	 */
	public ScenarioList getScenarios();
	
	public ParticipantsList getParticipantsFor(int scenarioId);
	
	/** somebody new wants to listen to us
	 * 
	 * @param scenario
	 * @param url
	 * @return
	 */
	public int newParticipantListener(int scenarioId, int participantId, URI url);
	
	/** somebody wants to stop listening to us
	 * @param scenarioId TODO
	 * @param listenerId
	 */
	public void deleteParticipantListener(int scenarioId, int participantId, int listenerId);


	/** find out the current status of this participant
	 * 
	 * @param parseInt
	 * @param parseInt2
	 * @return
	 */
	public DemandedStatus getDemandedStatus(int scenario, int participant);

	/** record a new demanded status for the supplied participant
	 * 
	 * @param scenario
	 * @param participant
	 * @param demState
	 */
	public void setDemandedStatus(int scenario, int participant,
			DemandedStatus demState);

	/** someone wants to listen to new decisions
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param listener
	 * @return
	 */
	public int newParticipantDecisionListener(int scenarioId, int participantId,
			URI listener);

	/** someone wants to stop listening to new decisions
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param theId
	 */
	public void deleteParticipantDecisionListener(int scenarioId,
			int participantId, int theId);

	/** retrive the sensors for this participant
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @return the list
	 */
	public List<Sensor> getSensorsFor(int scenarioId, int participantId);

	/** a new detection listener for this sensor
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param listener
	 * @return
	 */
	public int newParticipantDetectionListener(int scenarioId, int participantId,
			URI listener);

	/** ditch this detection listener
	 *  
	 * @param scenarioId
	 * @param participantId
	 * @param sensorId
	 */
	public void deleteParticipantDetectionListener(int scenarioId,
			int participantId, int sensorId);

	/** somebody wants to change how this scenario is running
	 * 
	 * @param scenarioId
	 * @param newState
	 */
	public void setScenarioStatus(int scenarioId, String newState);
}
