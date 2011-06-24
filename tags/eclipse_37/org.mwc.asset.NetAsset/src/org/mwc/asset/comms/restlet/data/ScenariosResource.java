package org.mwc.asset.comms.restlet.data;


import org.mwc.asset.comms.restlet.data.Scenario.ScenarioList;
import org.restlet.resource.Get;

/**
 * The resource associated to a contact.
 */
public interface ScenariosResource
{

	@Get
	public ScenarioList retrieve();
}
