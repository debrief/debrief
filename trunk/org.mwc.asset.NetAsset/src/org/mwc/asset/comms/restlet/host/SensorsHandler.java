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
		ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		ASSETHost host = hostP.getHost();
		
		List<Sensor> list = host.getSensorsFor(getScenarioId(), getParticipantId());
		return list;
	}
	
	
}