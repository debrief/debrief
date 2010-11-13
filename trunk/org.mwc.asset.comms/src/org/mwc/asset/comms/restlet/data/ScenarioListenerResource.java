package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Delete;
import org.restlet.resource.Post;

/**
 * The resource associated to a contact.
 */
public interface ScenarioListenerResource
{
	@Post
	public int accept(String listener);

	@Delete
	public void remove();
}
