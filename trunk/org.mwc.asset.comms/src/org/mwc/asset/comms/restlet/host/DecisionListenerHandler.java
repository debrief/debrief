package org.mwc.asset.comms.restlet.host;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ListenerResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.data.Status;

public class DecisionListenerHandler extends ASSETResource implements
		ListenerResource
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
			res = host.getHost().newParticipantDecisionListener(getScenarioId(),
					getParticipantId(), listener);

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
		Object thisP = attrs.get("listener");
		int theId = Integer.parseInt((String) thisP);

		ASSETHost.HostProvider host = (HostProvider) getApplication();
		host.getHost().deleteParticipantDecisionListener(getScenarioId(),
				getParticipantId(), theId);
	}

}