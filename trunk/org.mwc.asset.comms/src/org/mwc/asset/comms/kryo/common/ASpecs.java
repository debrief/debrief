package org.mwc.asset.comms.kryo.common;

import java.util.Vector;

import com.esotericsoftware.kryo.Kryo;

public interface ASpecs
{
	/** the ports we use across the LAN
	 * 
	 */
	public final int UDP_PORT = 54777;
	public final int TCP_PORT = 54555;
	
	
	/** marker interface for comms that cross the wire
	 * 
	 * @author ianmayo
	 *
	 */
	public static interface DataPacket
	{
		
	}
	
	public static class GetScenarios
	{
	}
	
	public static class ScenarioList
	{
		public Vector<String> scenarios;
	}


	
	
	public static class Config
	{
		public static void init(Kryo kryo)
		{
			kryo.register(Vector.class);
			kryo.register(GetScenarios.class);
			kryo.register(ScenarioList.class);
		}
	}
}
