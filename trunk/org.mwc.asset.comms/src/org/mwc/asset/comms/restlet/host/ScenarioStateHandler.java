package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;

public class ScenarioStateHandler extends ASSETResource implements
		ScenarioStateResource
{

	// @Override
	// public void accept(String event, long time, String description)
	// {
	// ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
	// String scen = (String) getRequest().getAttributes().get("scenario");
	// int scenario = Integer.parseInt(scen);
	// host.getGuest().newScenarioStatus(scenario, event, time, description);
	// }

	@Override
	public void accept(ScenarioEvent event)
	{
		// this may just work: {'int':'2','String':"bark"}
		
		ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newScenarioStatus(event.time, event.eventName, event.description);
	}
}