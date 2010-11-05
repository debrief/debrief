package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * The server side implementation of the Restlet resource.
 */
public class ScenarioServerResource extends ServerResource implements
		ScenarioResource
{
	
	private static volatile Scenario _scenario = new Scenario("Scott");

	@Get
	public Scenario retrieve()
	{
		return _scenario;
	}
}