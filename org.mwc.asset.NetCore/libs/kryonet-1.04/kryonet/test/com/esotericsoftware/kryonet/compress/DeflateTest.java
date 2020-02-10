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

package com.esotericsoftware.kryonet.compress;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.compress.DeflateCompressor;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoNetTestCase;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class DeflateTest extends KryoNetTestCase {
	public void testDeflate () throws IOException {
		final Server server = new Server();
		register(server.getKryo());

		final SomeData data = new SomeData();
		data.text = "some text here aaaaaaaaaabbbbbbbbbbbcccccccccc";
		data.stuff = new short[] {1, 2, 3, 4, 5, 6, 7, 8};

		final ArrayList a = new ArrayList();
		a.add(12);
		a.add(null);
		a.add(34);

		startEndPoint(server);
		server.bind(tcpPort, udpPort);
		server.addListener(new Listener() {
			public void connected (Connection connection) {
				server.sendToAllTCP(data);
				connection.sendTCP(data);
				connection.sendTCP(a);
			}
		});

		// ----

		final Client client = new Client();
		register(client.getKryo());
		startEndPoint(client);
		client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof SomeData) {
					SomeData data = (SomeData)object;
					System.out.println(data.stuff[3]);
				} else if (object instanceof ArrayList) {
					stopEndPoints();
				}
			}
		});
		client.connect(5000, host, tcpPort, udpPort);

		waitForThreads();
	}

	static public void register (Kryo kryo) {
		kryo.register(short[].class);
		kryo.register(SomeData.class, new DeflateCompressor(new FieldSerializer(kryo, SomeData.class)));
		kryo.register(ArrayList.class, new CollectionSerializer(kryo));
	}

	static public class SomeData {
		public String text;
		public short[] stuff;
	}
}
