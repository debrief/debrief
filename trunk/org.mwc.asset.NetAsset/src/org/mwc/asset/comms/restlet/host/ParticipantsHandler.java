package org.mwc.asset.comms.restlet.host;

import java.util.List;

import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ParticipantsResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Get;

public class ParticipantsHandler extends ASSETResource implements
		ParticipantsResource
{
	
	@Get
	public List<Participant> retrieve()
	{
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		
		List<Participant> list = host.getParticipantsFor(getScenarioId());
		return list;
	}
	
	
}