package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

import ASSET.Models.Detection.DetectionList;

/**
 * The resource associated to a contact.
 */
public interface DetectionResource
{

	public static class DetectionEvent extends AssetEvent
	{
		final public DetectionList _list;

		public DetectionEvent(final DetectionList list)
		{
			super(-1);
			_list = list;
		}
	}

	@Post
	public void accept(DetectionEvent event);
}
