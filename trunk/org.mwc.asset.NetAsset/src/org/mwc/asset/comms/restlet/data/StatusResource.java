package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

import ASSET.Participants.Status;

/**
 * The resource associated to a contact.
 */
public interface StatusResource
{

	public static class MovedEvent extends AssetEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final public Status _status;

		public MovedEvent(final Status status)
		{
			super(status.getTime());
			_status = status;
		}
	}

	@Post
	public void accept(Status status);
}
