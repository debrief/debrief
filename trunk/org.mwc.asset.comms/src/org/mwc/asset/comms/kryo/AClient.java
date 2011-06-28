package org.mwc.asset.comms.kryo;

import java.io.IOException;
import java.net.InetAddress;

import org.mwc.asset.comms.kryo.Specs.SomeRequest;
import org.mwc.asset.comms.kryo.Specs.SomeResponse;

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
		// try to find a server
		Client client = new Client();
		client.start();
		InetAddress address = client.discoverHost(54777, 2000);
		System.out.println(address);

		if(address == null)
			System.exit(1);
		
		// did it work?
		client.connect(5000, address.getHostAddress(), 54555, 54777);
		
		Specs.Init(client.getKryo());

		client.addListener(new Listener() {
		   public void received (Connection connection, Object object) {
		      if (object instanceof SomeResponse) {
		         SomeResponse response = (SomeResponse)object;
		         System.out.println(response.text);
		      }
		   }
		});
		
		for(int i=0;i<500;i++)
		{
			SomeRequest request = new SomeRequest();
			request.text = "Here is the request " + i;
			client.sendTCP(request);
		}
		
		
		client.stop();
		
		
		
	}
}
