package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ParticipantsResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Get;

public class ParticipantsHandler extends ASSETResource implements
		ParticipantsResource
{
	
	@Get
	public ParticipantsList retrieve()
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		
		ParticipantsList list = host.getParticipantsFor(getScenarioId());
		return list;
	}
	
	
}