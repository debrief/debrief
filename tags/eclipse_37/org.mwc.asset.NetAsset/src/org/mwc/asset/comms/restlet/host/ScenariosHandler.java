package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.mwc.asset.comms.restlet.data.Scenario.ScenarioList;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Get;

public class ScenariosHandler extends ASSETResource implements
		ScenariosResource
{
	
	@Get
	public ScenarioList retrieve()
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		
		ScenarioList res = host.getScenarios();
		
		return res;
	}
}