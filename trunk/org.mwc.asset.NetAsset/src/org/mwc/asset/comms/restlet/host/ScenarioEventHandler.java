package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ScenarioEventResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;

public class ScenarioEventHandler extends ASSETResource implements
		ScenarioEventResource
{
	@Override
	public void accept(ScenarioEvent event)
	{
		ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newScenarioEvent(event.time, event.eventName, event.description);
	}
}