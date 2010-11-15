package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import ASSET.Participants.DemandedStatus;

public class DemStatusHandler extends ASSETResource implements
		DemandedStatusResource
{

	@Get
	public DemandedStatus retrieve()
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();

		return host.getDemandedStatus(getScenarioId(), getParticipantId());
		
	}

	@Put
	public void store(DemandedStatus demState)
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		host.setDemandedStatus(getScenarioId(), getParticipantId(), demState);
	}
}