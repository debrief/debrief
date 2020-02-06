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
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ReconnectTest extends KryoNetTestCase {
	public void testReconnect () throws IOException {
		final Timer timer = new Timer();

		final Server server = new Server();
		startEndPoint(server);
		server.bind(tcpPort);
		server.addListener(new Listener() {
			public void connected (final Connection connection) {
				timer.schedule(new TimerTask() {
					public void run () {
						System.out.println("Disconnecting after 2 seconds.");
						connection.close();
					}
				}, 2000);
			}
		});

		// ----

		final AtomicInteger reconnetCount = new AtomicInteger();
		final Client client = new Client();
		startEndPoint(client);
		client.addListener(new Listener() {
			public void disconnected (Connection connection) {
				if (reconnetCount.getAndIncrement() == 2) {
					stopEndPoints();
					return;
				}
				new Thread() {
					public void run () {
						try {
							System.out.println("Reconnecting: " + reconnetCount.get());
							client.reconnect();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}.start();
			}
		});
		client.connect(5000, host, tcpPort);

		waitForThreads(10000);
		assertEquals(3, reconnetCount.getAndIncrement());
	}
}
