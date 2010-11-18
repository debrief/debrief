package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Delete;
import org.restlet.resource.Post;

/**
 * The resource associated to a contact.
 */
public interface ListenerResource
{
	@Post("text/plain")
	public Integer accept(String listener);

	@Delete
	public void remove();
}
