package org.mwc.asset.comms.kryo;

import com.esotericsoftware.kryo.Kryo;

public class Specs
{
	public static class SomeRequest {
	   public String text;
	}
	public static class SomeResponse {
	   public String text;
	}
	
	static public void Init(Kryo kryo)
	{
		kryo.register(SomeRequest.class);
		kryo.register(SomeResponse.class);
	}
}
