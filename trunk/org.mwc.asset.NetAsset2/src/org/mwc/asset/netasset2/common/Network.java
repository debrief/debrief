package org.mwc.asset.netasset2.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Network {
	public static final int UDP_PORT = 54777;
	public static final int TCP_PORT = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(SomeRequest.class);
		kryo.register(SomeResponse.class);
	}
	
	public static class SomeRequest {
	   public String text;
	}
	public static class SomeResponse {
	   public String text;
	}

}
