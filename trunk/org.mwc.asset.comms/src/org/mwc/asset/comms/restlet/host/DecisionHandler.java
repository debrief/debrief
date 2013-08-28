package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DecisionResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;

public class DecisionHandler extends ASSETResource implements
		DecisionResource
{

	public void accept(final DecidedEvent event)
	{
		final ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newParticipantDecision(getScenarioId(), getParticipantId(), event);
	}
}