package org.mwc.asset.netasset2.core;

import java.io.IOException;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.SomeRequest;
import org.mwc.asset.netasset2.common.Network.SomeResponse;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer 
{

	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
		Network.register(server);
		server.start();
		server.bind(Network.TCP_PORT, Network.UDP_PORT);
		
		server.addListener(new Listener() {
		   public void received (Connection connection, Object object) {
		      if (object instanceof SomeRequest) {
		         SomeRequest request = (SomeRequest)object;
		         System.out.println(request.text);

		         SomeResponse response = new SomeResponse();
		         response.text = "Thanks:" + request.text;
		         connection.sendTCP(response);
		      }
		   }
		   
		});
		
		
		System.out.println("pausing");
		System.in.read();
	}
}
