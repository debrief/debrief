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

import org.mwc.asset.comms.restlet.data.ScenarioEventResource;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;

public class ScenarioEventHandler extends ASSETResource implements
		ScenarioEventResource
{
	public void accept(final ScenarioEvent event)
	{
		final ASSETGuest.GuestProvider host = (GuestProvider) getApplication();
		host.getGuest().newScenarioEvent(event.time, event.eventName, event.description);
	}
}