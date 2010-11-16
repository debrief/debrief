package org.mwc.asset.comms.restlet.data;

import java.util.List;

import org.restlet.resource.Get;

/**
 * The resource associated to a contact.
 */
public interface ParticipantsResource
{

	@Get
	public List<Participant> retrieve();
}
