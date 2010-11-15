package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

/**
 * The resource associated to a contact.
 */
public interface ScenarioStateResource {

	public static class ScenarioEvent{
		final public String eventName;
		final public String description;
		final public long time;
		final public int scenarioId;

		public ScenarioEvent(String eName, String desc, long t, int s)
		{
			eventName = eName;
			description = desc;
			time = t;
			scenarioId = s;
		}
	}
	
//	@Post
//	public void accept(String event, long time, String description);
	@Post
	public void accept(ScenarioEvent event);
	
}
