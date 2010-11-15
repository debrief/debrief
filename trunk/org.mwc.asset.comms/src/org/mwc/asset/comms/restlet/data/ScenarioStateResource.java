package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

/**
 * The resource associated to a contact.
 */
public interface ScenarioStateResource {

//	@Post
//	public void accept(String event, long time, String description);
	@Post
	public void accept(long event, String val2);
}
