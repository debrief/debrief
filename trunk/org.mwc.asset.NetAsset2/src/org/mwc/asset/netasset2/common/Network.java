package org.mwc.asset.netasset2.common;

import java.util.Vector;

import ASSET.NetworkScenario;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Network {
	public static final int UDP_PORT = 54777;
	public static final int TCP_PORT = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		// sample ones
		kryo.register(SomeRequest.class);
		kryo.register(SomeResponse.class);
		// real ones
		kryo.register(GetScenarios.class);
		kryo.register(ScenarioList.class);
		kryo.register(NetworkScenario.class);
		kryo.register(Vector.class, new CollectionSerializer(kryo));
	}
	
	public static class SomeRequest {
	   public String text;
	}
	public static class SomeResponse {
	   public String text;
	}
	public static class GetScenarios
	{
	}
	public static class ScenarioList
	{
		public Vector<NetworkScenario> list;
//		public Vector list;
	}
	
	/** and our event handler
	 * 
	 */
	public abstract static class AHandler<T>
	{
		public void onFailure(Throwable t)
		{
			t.printStackTrace();
		}
		abstract public void onSuccess(T result);
	}
	
}
