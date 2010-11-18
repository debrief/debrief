package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Put;

public class ScenarioStateHandler extends ASSETResource implements
		ScenarioStateResource
{

	@Put
	public void store(String newState)
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		host.setScenarioStatus(getScenarioId(),newState);
	}
}