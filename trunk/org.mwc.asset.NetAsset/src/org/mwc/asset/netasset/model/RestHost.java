package org.mwc.asset.netasset.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mwc.asset.comms.restlet.host.ASSETHost;
import org.mwc.asset.comms.restlet.host.HostServer;
import org.mwc.asset.comms.restlet.test.MockHost;
import org.restlet.Component;
import org.restlet.Restlet;

public class RestHost
{

	private Component _hosting;

	public void startHosting(final ASSETHost theHost)
	{

		// do the host
		Restlet host = new HostServer()
		{
			@Override
			public ASSETHost getHost()
			{
				return theHost;
			}
		};
		Logger logger = host.getLogger();
		logger.setLevel(Level.WARNING);

		try
		{
			_hosting = HostServer.go(host);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public void stopHosting()
	{
		try
		{
			HostServer.finish(_hosting);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
