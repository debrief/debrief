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

package com.esotericsoftware.kryonet.examples.chatrmi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class Network {
	static public final int port = 54777;

	// These IDs are used to register objects in ObjectSpaces.
	static public final short PLAYER = 1;
	static public final short CHAT_FRAME = 2;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		// This must be called in order to use ObjectSpaces.
		ObjectSpace.registerClasses(kryo);
		// The interfaces that will be used as remote objects must be registered.
		kryo.register(IPlayer.class);
		kryo.register(IChatFrame.class);
		// The classes of all method parameters and return values
		// for remote objects must also be registered.
		kryo.register(String[].class);
	}
}
