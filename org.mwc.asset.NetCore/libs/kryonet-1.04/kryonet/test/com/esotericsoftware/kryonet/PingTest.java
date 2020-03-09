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
import java.util.Timer;

import com.esotericsoftware.kryonet.FrameworkMessage.Ping;

public class PingTest extends KryoNetTestCase {
	public void testPing () throws IOException {
		final Server server = new Server();
		startEndPoint(server);
		server.bind(tcpPort);

		// ----

		final Client client = new Client();
		startEndPoint(client);
		client.addListener(new Listener() {
			public void connected (Connection connection) {
				client.updateReturnTripTime();
			}

			public void received (Connection connection, Object object) {
				if (object instanceof Ping) {
					Ping ping = (Ping)object;
					if (ping.isReply) System.out.println("Ping: " + connection.getReturnTripTime());
					client.updateReturnTripTime();
				}
			}
		});
		client.connect(5000, host, tcpPort);

		waitForThreads(5000);
	}
}
