package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

/**
 * The resource associated to a contact.
 */
public interface ScenarioStateResource {

	public static class ScenarioEvent extends AssetEvent{
		final public String eventName;
		final public String description;
		final public int scenarioId;

		public ScenarioEvent(String eName, String desc, long t, int s)
		{
			super(t);
			eventName = eName;
			description = desc;
			scenarioId = s;
		}
	}
	
//	@Post
//	public void accept(String event, long time, String description);
	@Post
	public void accept(ScenarioEvent event);
	
}
