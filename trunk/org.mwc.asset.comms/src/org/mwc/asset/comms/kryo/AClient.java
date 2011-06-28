package org.mwc.asset.comms.kryo;

import java.io.IOException;

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
		Client client = new Client();
		client.start();
		client.connect(5000, "127.0.0.1", 54555, 54777);
		
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
		
		
		
		
	}
}
