package org.mwc.asset.comms.restlet.host;

import java.util.List;

import org.mwc.asset.comms.restlet.data.Sensor;
import org.mwc.asset.comms.restlet.data.SensorsResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Get;

public class SensorsHandler extends ASSETResource implements
		SensorsResource
{
	
	@Get
	public List<Sensor> retrieve()
	{
		final ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		final ASSETHost host = hostP.getHost();
		
		final List<Sensor> list = host.getSensorsFor(getScenarioId(), getParticipantId());
		return list;
	}
	
	
}