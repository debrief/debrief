package org.mwc.asset.comms.restlet.host;

import java.util.Map;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import ASSET.Participants.DemandedStatus;

public class StatusHandler extends ServerResource implements
		DemandedStatusResource
{

	@Get
	public DemandedStatus retrieve()
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();

		Map<String, Object> attrs = this.getRequestAttributes();
		Object thisS = attrs.get("sceanrio");
		Object thisP = attrs.get("participant");

		return host.getDemandedStatus(Integer.parseInt((String) thisS), Integer
				.parseInt((String) thisP));
		
	}

	@Put
	public void store(DemandedStatus demState)
	{
		Map<String, Object> attrs = this.getRequestAttributes();
		Object thisS = attrs.get("scenario");
		Object thisP = attrs.get("participant");
		int scenario = Integer.parseInt((String) thisS);
		int participant = Integer.parseInt((String) thisP);

		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		host.setDemandedStatus(scenario, participant, demState);
	}
}