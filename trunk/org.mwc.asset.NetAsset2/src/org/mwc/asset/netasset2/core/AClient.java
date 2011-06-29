package org.mwc.asset.netasset2.core;

import java.io.IOException;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.SomeRequest;
import org.mwc.asset.netasset2.common.Network.SomeResponse;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class AClient
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Client client = new Client();
		Network.register(client);
		client.start();
		client.connect(5000, "LOCALHOST", Network.TCP_PORT, Network.UDP_PORT);

		SomeRequest request = new SomeRequest();
		request.text = "Here is the request 2!";
		client.sendTCP(request);
		
		client.addListener(new Listener() {
		   public void received (Connection connection, Object object) {
		      if (object instanceof SomeResponse) {
		         SomeResponse response = (SomeResponse)object;
		         System.out.println(response.text);
		      }
		   }
		});
	}

}
