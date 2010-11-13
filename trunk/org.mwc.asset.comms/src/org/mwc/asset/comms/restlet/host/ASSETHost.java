package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Scenario;

import ASSET.Participants.DemandedStatus;

public interface ASSETHost
{
	public interface HostProvider
	{
		public ASSETHost getHost();
	}
	
	/** somebody new wants to listen to us
	 * 
	 * @param scenario
	 * @param url
	 * @return
	 */
	public int newScenarioListener(int scenario, URL url);
	
	/** somebody wants to stop listening to us
	 * 
	 * @param listenerId
	 */
	public void deleteListener(int listenerId);

	/** get a list of scenarios we know about
	 * 
	 * @return
	 */
	public Vector<Scenario> getScenarios();

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
}
