package org.mwc.asset.netasset.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.Scenario.ScenarioList;
import org.mwc.asset.comms.restlet.host.ASSETHost;
import org.mwc.asset.comms.restlet.host.BaseHost;
import org.mwc.asset.comms.restlet.host.HostServer;
import org.restlet.Component;
import org.restlet.Restlet;

import ASSET.ScenarioType;

public class RestHost extends BaseHost
{

	private static String _localName;
	private static int _myPort;
	private Component _hosting;
	private ScenarioType _myScenario;
	private int _myId;

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
			_myPort = 8080;
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
	public static String getHostName()
	{
		if (_localName == null)
		{
			InetAddress addr = null;
			try
			{
				addr = InetAddress.getLocalHost();
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			_localName = "http://" + addr.getHostAddress() + ":" + _myPort;
		}
		return _localName;
	}

	
	public void setScenario(ScenarioType scenario, int id)
	{
		_myScenario = scenario;
		_myId = id;

		_myScenario.addScenarioSteppedListener(getSteppedListFor(_myId));
		_myScenario.addParticipantsChangedListener(getSteppedListFor(_myId));

	}

	public void clearScenario()
	{
		if (_myScenario != null)
		{
			_myScenario.removeScenarioSteppedListener(getSteppedListFor(_myId));
			_myScenario.removeParticipantsChangedListener(getSteppedListFor(_myId));
		}
	}

	@Override
	public ScenarioType getScenario(int scenarioId)
	{
		return _myScenario;
	}

	@Override
	public ScenarioList getScenarios()
	{
		ScenarioList res = new ScenarioList();
		if(_myScenario != null)
			res.add(new Scenario(_myScenario.getName(), _myId));
		return res;
	}
}
