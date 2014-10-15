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