package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.host.ASSETGuest.GuestProvider;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

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

			MockGuest host = new MockGuest();

			@Override
			public ASSETGuest getGuest()
			{
				return host;
			}
		};
		GuestServer.go(guestS);
	}
	
	public String getRootPath()
	{
		return super.getRoot().toString();
	}

	@Override
	public Restlet createInboundRoot()
	{
		Router router = new Router(getContext());
		getConnectorService().getClientProtocols().add(Protocol.FILE);
		router.attach("/v1/scenario/{scenario}/event",
				ScenarioStateHandler.class);
//		router.attach("/v1/scenario/{scenario}/participant/{participant}/state",
//				StatusHandler.class);
		return router;
	}

}
