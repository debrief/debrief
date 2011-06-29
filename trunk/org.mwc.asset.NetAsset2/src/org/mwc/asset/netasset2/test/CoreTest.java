package org.mwc.asset.netasset2.test;

import java.io.IOException;

import org.mwc.asset.netasset2.common.Network.SomeRequest;
import org.mwc.asset.netasset2.core.AClient;
import org.mwc.asset.netasset2.core.AServer;

public class CoreTest
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		AServer server = new AServer();
		
		AClient client = new AClient();
		
		SomeRequest request = new SomeRequest();
		request.text = "Here is the request 6!";
		client.send(request);
		
		System.out.println("pausing");
		System.in.read();
		
		server.stop();
		client.stop();
	}

}

