package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Put;

import ASSET.Participants.DemandedStatus;

public class DemStatusHandler extends ASSETResource implements
		DemandedStatusResource
{

	@Put
	public void store(final DemandedStatus demState)
	{
		final ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		final ASSETHost host = hostP.getHost();
		host.setDemandedStatus(getScenarioId(), getParticipantId(), demState);
	}
}