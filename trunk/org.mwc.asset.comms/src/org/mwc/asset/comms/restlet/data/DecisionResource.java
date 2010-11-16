package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

import ASSET.Participants.DemandedStatus;

/**
 * The resource associated to a contact.
 */
public interface DecisionResource {
	
	public static class DecidedEvent extends AssetEvent{
		final public DemandedStatus _status;
		final public String _activity;

		public DecidedEvent(DemandedStatus status, String activity)
		{
			super(status.getTime());
			_status = status;
			_activity = activity;
		}
	}
    @Post
    public void accept(DecidedEvent event);
}
