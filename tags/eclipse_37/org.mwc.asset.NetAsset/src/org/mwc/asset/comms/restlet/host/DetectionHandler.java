package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DetectionResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;

public class DetectionHandler extends ASSETResource implements
		DetectionResource
{

	public void accept(DetectionEvent event)
	{
		ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newParticipantDetection(getScenarioId(),
				getParticipantId(), event);
	}
}