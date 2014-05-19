package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Get;

/**
 * The resource associated to a contact.
 */
public interface ParticipantResource
{

	@Get
	public Participant retrieve();
}
