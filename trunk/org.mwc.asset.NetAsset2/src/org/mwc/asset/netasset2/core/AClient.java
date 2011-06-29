package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.SomeRequest;
import org.mwc.asset.netasset2.common.Network.SomeResponse;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class AClient
{
	private static class CModel
	{
		private Client _client;

		public CModel() throws IOException
		{
			_client = new Client();
			Network.register(_client);
			_client.start();

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

		public void addListener(Listener listener)
		{
			_client.addListener(listener);
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
		_model.addListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				if (object instanceof SomeResponse)
				{
					SomeResponse response = (SomeResponse) object;
					System.out.println(response.text);
				}
			}
		});
	}

	public void connect(String target) throws IOException
	{
		_model.connect(target);
	}

	public void stop()
	{
		_model.stop();
	}

	public void testSend()
	{
		SomeRequest request = new SomeRequest();
		request.text = "Here is the request 6!";
		_model.send(request);
	}

}
