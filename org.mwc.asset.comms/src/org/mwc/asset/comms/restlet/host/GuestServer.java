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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;
import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import ASSET.Participants.Status;


abstract public class GuestServer extends Application implements GuestProvider
{

	abstract public ASSETGuest getGuest();


	public static Component go(final Restlet host) throws Exception
	{
		final Component component = new Component();
		component.getClients().add(Protocol.FILE);
		component.getServers().add(Protocol.HTTP, 8081);
		component.getDefaultHost().attach(host);
		component.start();
		
		return component;
	}

	public static void finish(final Component component) throws Exception
	{
		component.stop();
	}
	
	/**
	 * When launched as a standalone application.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception
	{
		final Restlet guestS = new GuestServer()
		{

			ASSETGuest host = new ASSETGuest(){
				public void newParticipantDecision(final int scenarioId, final int participantId,
						final DecidedEvent event)
				{
				}
				public void newParticipantDetection(final int scenarioId, final int participantId,
						final DetectionEvent event)
				{
				}
				public void newParticipantState(final int scenarioId, final int participantId,
						final Status newState)
				{
				}
				public void newScenarioEvent(final long time, final String eventName,
						final String description)
				{
				}};

			@Override
			public ASSETGuest getGuest()
			{
				return host;
			}
		};
		GuestServer.go(guestS);
	}
	

	@Override
	public Restlet createInboundRoot()
	{
		final Router router = new Router(getContext());
		getConnectorService().getClientProtocols().add(Protocol.FILE);
		router.attach("/v1/scenario/{scenario}/event",
				ScenarioEventHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/status",
				StatusHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/decision",
				DecisionHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/detection",
				DetectionHandler.class);
		return router;
	}

}
