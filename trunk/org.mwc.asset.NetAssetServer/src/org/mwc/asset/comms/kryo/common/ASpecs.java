package org.mwc.asset.comms.kryo.common;

import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.NetworkScenario;

import com.esotericsoftware.kryo.Kryo;

public interface ASpecs
{
	/**
	 * the ports we use across the LAN
	 * 
	 */
	public final int UDP_PORT = 54777;
	public final int TCP_PORT = 54555;

	/**
	 * marker interface for comms that cross the wire
	 * 
	 * @author ianmayo
	 * 
	 */
	public static interface DataPacket
	{

	}

	public static class ScenarioItem implements DataPacket
	{
		public ScenarioItem()
		{
		};

		public ScenarioItem(String string)
		{
			this();

			name = string;
			id = 1;
			description = name + ":" + id;
		}

		public String name;
		public int id;
		public String description;
	}

	public static class GetScenarios implements DataPacket
	{
	}

	public static class ScenarioList implements DataPacket
	{
		public Vector<NetworkScenario> scenarios;
	}

	public static class GetThisScenario implements DataPacket
	{
		public int id;
	}
	
	public static class GetThisParticipant implements DataPacket
	{
		public int id;
	}
	

	public static class Config
	{
		public static void init(Kryo kryo)
		{
			kryo.register(Vector.class);
			kryo.register(GetScenarios.class);
			kryo.register(NetworkScenario.class);
			kryo.register(ScenarioList.class);
			kryo.register(ScenarioItem.class);
			kryo.register(GetThisScenario.class);
			kryo.register(NetworkParticipant.class);
		}
	}
}
