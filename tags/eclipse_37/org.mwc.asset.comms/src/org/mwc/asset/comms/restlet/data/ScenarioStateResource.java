package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Put;

/**
 * The resource associated to a contact.
 */
public interface ScenarioStateResource
{

	public final static String START = "Start";
	public final static String STOP = "Stop";
	public final static String FASTER = "Faster";
	public final static String SLOWER = "Slower";
	
	@Put
	public void store(String newState);
}
