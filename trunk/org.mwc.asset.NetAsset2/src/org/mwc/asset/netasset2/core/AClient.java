package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.SomeResponse;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class AClient
{

	private Client _client;

	public AClient() throws IOException
	{
		_client = new Client();
		Network.register(_client);
		_client.start();

		InetAddress address = _client.discoverHost(Network.UDP_PORT, 5000);
		if (address != null)
			_client.connect(5000, "LOCALHOST", Network.TCP_PORT, Network.UDP_PORT);

		_client.addListener(new Listener()
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
	
	

	public void stop()
	{
		_client.stop();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		AClient client = new AClient();

		System.out.println("pausing");
		System.in.read();

		client.stop();
	}



	public void send(Object data)
	{
		_client.sendTCP(data);
	}

}
