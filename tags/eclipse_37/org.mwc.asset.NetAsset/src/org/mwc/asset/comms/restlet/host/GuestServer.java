package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;
import org.mwc.asset.comms.restlet.data.ParticipantsResource.ParticipantsList;
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


	public static Component go(Restlet host) throws Exception
	{
		Component component = new Component();
		component.getClients().add(Protocol.FILE);
		component.getServers().add(Protocol.HTTP, 8081);
		component.getDefaultHost().attach(host);
		component.start();
		
		return component;
	}

	public static void finish(Component component) throws Exception
	{
		component.stop();
	}
	
	/**
	 * When launched as a standalone application.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Restlet guestS = new GuestServer()
		{

			ASSETGuest host = new ASSETGuest(){
				public void newParticipantDecision(int scenarioId, int participantId,
						DecidedEvent event)
				{
				}
				public void newParticipantDetection(int scenarioId, int participantId,
						DetectionEvent event)
				{
				}
				public void newParticipantState(int scenarioId, int participantId,
						Status newState)
				{
				}
				public void newScenarioEvent(long time, String eventName,
						String description)
				{
				}
				public void setParticipants(int scenarioId, ParticipantsList pList)
				{
					// TODO Auto-generated method stub
					
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
		Router router = new Router(getContext());
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
