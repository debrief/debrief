package org.mwc.asset.comms.restlet.host;

import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.mwc.asset.comms.restlet.test.MockHost;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import ASSET.ScenarioType;
import ASSET.Participants.DemandedStatus;

abstract public class HostServer extends Application implements HostProvider
{

	/** provide an interface to the actual data
	 * 
	 */
	abstract public ASSETHost getHost();

	public static Component go(Restlet host) throws Exception
	{
		Component component = new Component();
		component.getClients().add(Protocol.FILE);
		component.getServers().add(Protocol.HTTP, 8080);
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
		Restlet host = new HostServer()
		{

			ASSETHost host = new MockHost();

			@Override
			public ASSETHost getHost()
			{
				return host;
			}
		};
		HostServer.go(host);
	}

	@Override
	public Restlet createInboundRoot()
	{
		Router router = new Router(getContext());
		getConnectorService().getClientProtocols().add(Protocol.FILE);

		router.attach("/v1/scenario", ScenariosHandler.class);
		router.attach("/v1/scenario/{scenario}/state",
				ScenarioStateHandler.class);
		router.attach("/v1/scenario/{scenario}/listener",
				ScenarioListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/listener/{listener}",
				ScenarioListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant",
				ParticipantsHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/demState",
				DemStatusHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/listener",
				ParticipantListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/listener/{listener}",
				ParticipantListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/decisionListener",
				DecisionListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/decisionListener/{listener}",
				DecisionListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/sensor",
				SensorsHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/detectionListener",
				DetectionListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/detectionListener/{listener}",
				DetectionListenerHandler.class);
		return router;
	}

}
