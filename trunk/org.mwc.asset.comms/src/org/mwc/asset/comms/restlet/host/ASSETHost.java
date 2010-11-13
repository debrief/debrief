package org.mwc.asset.comms.restlet.host;

import java.net.URL;

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
}
