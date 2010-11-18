package org.mwc.asset.netasset.model;

import java.util.List;

import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest;
import org.mwc.asset.comms.restlet.test.MockHost;
import org.restlet.resource.ClientResource;

public class RestSupport
{

	final private ASSETGuest _myGuest;

	public RestSupport(ASSETGuest guest)
	{
		_myGuest = guest;
	}

	/**
	 * connect to a server
	 * 
	 */
	public boolean doConnect()
	{
		// find some data
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario");

		// does it have a scenario?
		ScenariosResource scenR = cr.wrap(ScenariosResource.class);
		List<Scenario> sList = scenR.retrieve();
		boolean res = (sList != null);

		if (res)
			_myGuest.newScenarioEvent(0, "Setup", "scenarios found:" + sList.size());
		else
			_myGuest.newScenarioEvent(0, "Setup", "scenarios not found");

		return res;
	}

	public void play(boolean play)
	{
		// find some data
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario/" + MockHost.SCENARIO_ID +"/state");

		// does it have a scenario?
		ScenarioStateResource scenR = cr.wrap(ScenarioStateResource.class);
		String theState;
		if(play)
			theState = ScenarioStateResource.START;
		else
			theState = ScenarioStateResource.STOP;
		
		scenR.store(theState);
	}

}
