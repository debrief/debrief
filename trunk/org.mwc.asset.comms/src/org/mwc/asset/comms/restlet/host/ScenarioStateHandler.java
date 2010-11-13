package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;
import org.restlet.resource.ServerResource;

public class ScenarioStateHandler extends ServerResource implements
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
	public void accept(String event)
	{
		ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newScenarioStatus(0, "svent type", 0, event);
	}

}