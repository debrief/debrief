/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

package com.esotericsoftware.kryonet;

import java.io.IOException;
import java.net.InetAddress;

public class DiscoverHostTest extends KryoNetTestCase {
	public void testBroadcast () throws IOException {
		// This server exists solely to reply to Client#discoverHost.
		// It wouldn't be needed if the real server was using UDP.
		final Server broadcastServer = new Server();
		startEndPoint(broadcastServer);
		broadcastServer.bind(0, udpPort);

		final Server server = new Server();
		startEndPoint(server);
		server.bind(54555);
		server.addListener(new Listener() {
			public void disconnected (Connection connection) {
				broadcastServer.stop();
				server.stop();
			}
		});

		// ----

		Client client = new Client();
		InetAddress host = client.discoverHost(udpPort, 2000);
		if (host == null) {
			stopEndPoints();
			fail("No servers found.");
			return;
		}

		startEndPoint(client);
		client.connect(2000, host, tcpPort);
		client.stop();

		waitForThreads();
	}
}
