package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;

public class ScenarioStateHandler extends ASSETResource implements
		ScenarioStateResource
{
	@Override
	public void accept(ScenarioEvent event)
	{
		ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newScenarioStatus(event.time, event.eventName, event.description);
	}
}