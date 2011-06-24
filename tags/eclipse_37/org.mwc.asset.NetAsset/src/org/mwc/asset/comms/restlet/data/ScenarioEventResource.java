package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

/**
 * The resource associated to a contact.
 */
public interface ScenarioEventResource
{

	public static class ScenarioEvent extends AssetEvent 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final public String description;
		final public String eventName;
		final public int scenarioId;

		public ScenarioEvent(final String eName, final String desc, final long t,
				final int s)
		{
			super(t);
			eventName = eName;
			description = desc;
			scenarioId = s;
		}
	}

	// @Post
	// public void accept(String event, long time, String description);
	@Post
	public void accept(ScenarioEvent event);

}
