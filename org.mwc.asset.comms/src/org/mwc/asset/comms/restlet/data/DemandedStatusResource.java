package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Put;

import ASSET.Participants.DemandedStatus;

/**
 * The resource associated to a contact.
 */
public interface DemandedStatusResource
{

	@Put
	public void store(DemandedStatus newState);
}
