package org.mwc.asset.comms.kryo;

import java.io.IOException;

import org.mwc.asset.comms.kryo.Specs.SomeRequest;
import org.mwc.asset.comms.kryo.Specs.SomeResponse;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer
{


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
		server.start();
		server.bind(54555, 54777);
		
		Specs.Init(server.getKryo());
		
		server.addListener(new Listener() {
		   public void received (Connection connection, Object object) {
		      if (object instanceof SomeRequest) {
		         SomeRequest request = (SomeRequest)object;

		         SomeResponse response = new SomeResponse();
		         response.text = "Thanks:" + request.text;
		         connection.sendTCP(response);
		      }
		   }
		});
	}

}
