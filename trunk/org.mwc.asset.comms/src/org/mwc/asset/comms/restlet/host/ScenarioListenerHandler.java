package org.mwc.asset.comms.restlet.host;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ScenarioListenerResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;

public class ScenarioListenerHandler extends ServerResource implements
		ScenarioListenerResource
{

	@Override
	public int accept(String listenerTxt)
	{
		URL listener;
		int res = 0;
		try
		{
			listener = new URL(listenerTxt);
			ASSETHost.HostProvider host = (HostProvider) getApplication();
			String scen = (String) getRequest().getAttributes().get("scenario");
			int scenario = Integer.parseInt(scen);
			res = host.getHost().newScenarioListener(scenario, listener);

		}
		catch (MalformedURLException e)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
		return res;
	}

	@Override
	public void remove()
	{
		Map<String, Object> attrs = this.getRequestAttributes();
		Object thisS = attrs.get("scenario");
		Object thisP = attrs.get("listener");
		int theScenario = Integer.parseInt((String) thisS);
		int theId = Integer.parseInt((String) thisP);
		

		ASSETHost.HostProvider host = (HostProvider) getApplication();
		host.getHost().deleteScenarioListener(theScenario, theId);
	}

}