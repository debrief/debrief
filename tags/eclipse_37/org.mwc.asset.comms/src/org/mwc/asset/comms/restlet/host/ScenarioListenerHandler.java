package org.mwc.asset.comms.restlet.host;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ListenerResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.data.Status;

public class ScenarioListenerHandler extends ASSETResource implements
		ListenerResource
{


	public int accept(String listenerTxt)
	{
		URI listener;
		int res = 0;
		try
		{
			listener = new URI(listenerTxt);
			ASSETHost.HostProvider host = (HostProvider) getApplication();
			res = host.getHost().newScenarioListener(getScenarioId(), listener);

		}
		catch (URISyntaxException e)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
		return res;
	}

	public void remove()
	{
		Map<String, Object> attrs = this.getRequestAttributes();
		Object thisP = attrs.get("listener");
		int theId = Integer.parseInt((String) thisP);
		

		ASSETHost.HostProvider host = (HostProvider) getApplication();
		host.getHost().deleteScenarioListener(getScenarioId(), theId);
	}

}