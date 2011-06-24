package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Put;


public class DemStatusHandler extends ASSETResource implements
		DemandedStatusResource
{

	@Put
	public void store(NetDemStatus demState)
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		host.setDemandedStatus(getScenarioId(), getParticipantId(), demState);
	}
}