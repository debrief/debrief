package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

import ASSET.Participants.DemandedStatus;

/**
 * The resource associated to a contact.
 */
public interface DecisionResource
{

	public static class DecidedEvent extends AssetEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final public String _activity;
		final public DemandedStatus _status;

		public DecidedEvent(final DemandedStatus status, final String activity)
		{
			super(status.getTime());
			_status = status;
			_activity = activity;
		}
	}

	@Post
	public void accept(DecidedEvent event);
}
