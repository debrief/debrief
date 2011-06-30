package org.mwc.asset.netasset2.common;

import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Network
{
	public static final int UDP_PORT = 54777;
	public static final int TCP_PORT = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint)
	{
		Kryo kryo = endPoint.getKryo();
		// sample ones
		kryo.register(SomeRequest.class);
		kryo.register(SomeResponse.class);
		// real ones
		kryo.register(GetScenarios.class);
		kryo.register(ScenarioList.class);
		kryo.register(LightScenario.class);
		kryo.register(LightParticipant.class);
		kryo.register(Vector.class, new CollectionSerializer(kryo));
		kryo.register(Category.class);
		kryo.register(ControlPart.class);
		kryo.register(ReleasePart.class);
		kryo.register(PartUpdate.class);
		kryo.register(LightStatus.class);
	}

	public static class SomeRequest
	{
		public String text;
	}

	public static class SomeResponse
	{
		public String text;
	}

	public static class GetScenarios
	{
	}

	public static class ScenarioList
	{
		public Vector<LightScenario> list;
		// public Vector list;
	}
	
	public static class LightStatus
	{
		public LightStatus(){};
		public LightStatus(Status status)
		{
			dtg = status.getTime();
			id = status.getId();
			_lat = status.getLocation().getLat();
			_long = status.getLocation().getLong();
			_depth = status.getLocation().getDepth();
			spdKts = status.getSpeed().getValueIn(WorldSpeed.Kts);
			courseDegs = status.getCourse();
		}
		long dtg;
		int id;
		double _lat, _long, _depth;
		double spdKts;
		double courseDegs;
		public Status asStatus()
		{
			Status res = new Status(id, dtg);
			res.setLocation(new WorldLocation(_lat, _long, _depth));
			res.setSpeed(new WorldSpeed(spdKts, WorldSpeed.Kts));
			res.setCourse(courseDegs);
			return res;
		}
	}

	public static class LightScenario
	{
		public LightScenario()
		{
		};

		public LightScenario(ScenarioType scenario)
		{
			listOfParticipants = new Vector<LightParticipant>();
			name = scenario.getName();
			Integer[] list = scenario.getListOfParticipants();
			for (int i = 0; i < list.length; i++)
			{
				Integer integer = list[i];
				ParticipantType pt = scenario.getThisParticipant(integer);
				listOfParticipants.add(new LightParticipant(pt));
			}
		};

		public String name;
		public Vector<LightParticipant> listOfParticipants;
	}

	public static class ControlPart
	{
		public String scenarioName;
		public int partId;
	}

	public static class ReleasePart
	{
		public String scenarioName;
		public int partId;
	}

	public static class PartUpdate
	{
		public int id;
		public LightStatus lStatus;
		public String scenario;

		public PartUpdate()
		{
		};

		public PartUpdate(int id, Status status, String scenario)
		{
			this.id = id;
			this.scenario = scenario;
			lStatus = new LightStatus(status);
		}

	}

	public static class LightParticipant
	{
		public LightParticipant()
		{
		};

		public LightParticipant(int Id, String string)
		{
			this.id = Id;
			name = string;
			category = new Category(Category.Force.RED, Category.Environment.SURFACE,
					Category.Type.SONAR_BUOY);
			activity = "some activity";
		}

		public LightParticipant(ParticipantType pt)
		{
			id = pt.getId();
			name = pt.getName();
			category = pt.getCategory();
			activity = pt.getActivity();
		}

		public int id;
		public String name;
		public Category category;
		public String activity;
	}

	public static class LightNetworkParticipant
	{

	}

	/**
	 * and our event handler
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
