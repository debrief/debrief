package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * The server side implementation of the Restlet resource.
 */
public class ParticipantServerResource extends ServerResource implements
		ParticipantResource
{
	
	private static volatile Participant _scenario = new Participant("Scott",5);

	@Get
	public Participant retrieve()
	{
		return _scenario;
	}
}