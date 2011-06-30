package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.GetScenarios;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.common.Network.ScenarioList;

import ASSET.Scenario.MultiScenarioLister;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class AServer
{
	@SuppressWarnings("unused")
	private MultiScenarioLister _dataProvider;
	private SModel _model;

	public static class SModel
	{
		private Server _server;
		private HashMap<Class<?>, Listener> _listeners;

		public SModel() throws IOException
		{
			_server = new Server();
			Network.register(_server);
			_server.start();
			_server.bind(Network.TCP_PORT, Network.UDP_PORT);
			
			_listeners = new HashMap<Class<?>, Listener>();

			// sort out our handler
			_server.addListener(new Listener()
			{

				@Override
				public void received(Connection connection, Object object)
				{
					// ok, see if we have a handler
					Listener match = _listeners.get(object.getClass());
					if (match != null)
					{
						match.received(connection, object);
					}
					else
					{
						System.err.println("HANDLER NOT FOUND FOR:" + object);
					}
				}
			});
		}
		

		public void addListener(Class<?> objectType, Listener listener)
		{
			_listeners.put(objectType, listener);
		}

		public void removeListener(final Class<?> objectType)
		{
			_listeners.remove(objectType);
		}

		public void stop()
		{
			_server.stop();
		}

	}

	public AServer() throws IOException
	{
		_model = new SModel();

		Listener getS = new Listener()
		{
			public void received(Connection connection, Object object)
			{
					System.err.println("getS received");
					Log.info("GetScenarios received");
					ScenarioList res = new ScenarioList();
					res.list = new Vector<LightScenario>();
					res.list.add(new LightScenario("zaa"));
					res.list.add(new LightScenario("bbb"));
					res.list.add(new LightScenario("ccc"));

					// res.list = _dataProvider.getScenarios();
					System.err.println("about to send list");
					connection.sendTCP(res);
					System.err.println("sent list");
			}
		};
		_model.addListener(new GetScenarios().getClass(), getS);

	}

	public void setDataProvider(MultiScenarioLister lister)
	{
		_dataProvider = lister;
	}

	public void stop()
	{
		_model.stop();
	}
}
