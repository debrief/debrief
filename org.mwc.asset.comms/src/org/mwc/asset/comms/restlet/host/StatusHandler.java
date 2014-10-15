/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.StatusResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;
import org.restlet.resource.Post;

import ASSET.Participants.Status;

public class StatusHandler extends ASSETResource implements
		StatusResource
{

	@Post
	public void accept(final Status status)
	{
		final ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newParticipantState(getScenarioId(), getParticipantId(), status);
	}
}