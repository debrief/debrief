package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.StatusResource;
import org.restlet.resource.Post;

import ASSET.Participants.Status;

public class StatusHandler extends ASSETResource implements
		StatusResource
{

	@Post
	public void accept(Status status)
	{
	}
}