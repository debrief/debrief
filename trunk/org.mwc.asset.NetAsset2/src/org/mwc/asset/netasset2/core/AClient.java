package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.GetScenarios;
import org.mwc.asset.netasset2.common.Network.SomeResponse;

import ASSET.NetworkScenario;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class AClient
{
	private static class CModel
	{
		private Client _client;
		private HashMap<Class<?>, Listener> _listeners;

		public CModel() throws IOException
		{
			_client = new Client();
			Network.register(_client);
			_client.start();
			_listeners = new HashMap<Class<?>, Listener>();

			// sort out our handler
			_client.addListener(new Listener()
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
				}
			});
		}

		public void connect(String target) throws IOException
		{
			if (target == null)
			{
				InetAddress address = _client.discoverHost(Network.UDP_PORT, 3000);
				if (address != null)
					target = address.getHostAddress();

			}

			_client.connect(5000, target, Network.TCP_PORT, Network.UDP_PORT);
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
			_client.stop();
		}

		public void send(Object data)
		{
			_client.sendTCP(data);
		}

	}

	private CModel _model;

	public AClient() throws IOException
	{
		_model = new CModel();
	}

	public void connect(String target) throws IOException
	{
		_model.connect(target);
	}

	public void stop()
	{
		_model.stop();
	}

	public void send(Object data)
	{
		_model.send(data);
	}

	public void getScenarioList(
			final Network.AHandler<Vector<NetworkScenario>> handler)
	{
		final Class<?> theType = new GetScenarios().getClass();
		final Listener listener = new Listener()
		{
			@SuppressWarnings("unchecked")
			public void received(Connection connection, Object object)
			{
				handler.onSuccess((Vector<NetworkScenario>) object);
				// and forget about ourselves
				_model.removeListener(theType);
			}
		};
		_model.addListener(new GetScenarios().getClass(), listener);
		_model.send(theType);
		// don't bother waiting, the handler will remove itself
	}

}
