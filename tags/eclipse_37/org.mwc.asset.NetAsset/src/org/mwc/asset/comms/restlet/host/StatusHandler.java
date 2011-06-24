package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.StatusResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;
import org.restlet.resource.Post;

import ASSET.Participants.Status;

public class StatusHandler extends ASSETResource implements
		StatusResource
{

	@Post
	public void accept(Status status)
	{
		ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newParticipantState(getScenarioId(), getParticipantId(), status);
	}
}