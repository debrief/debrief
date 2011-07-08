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
	public static final int UDP_PORT = 54778;
	public static final int TCP_PORT = 54555;
	public static final int DUFF_INDEX = -1;

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
		kryo.register(ListenScen.class);
		kryo.register(StopListenScen.class);
		kryo.register(ScenUpdate.class);
		kryo.register(ScenControl.class);
		kryo.register(LightParticipant.class);
		kryo.register(Vector.class, new CollectionSerializer(kryo));
		kryo.register(Category.class);
		kryo.register(ListenPart.class);
		kryo.register(StopListenPart.class);
		kryo.register(PartUpdate.class);
		kryo.register(DemStatus.class);
		kryo.register(ReleasePart.class);
		kryo.register(WorldSpeed.class);
		kryo.register(WorldLocation.class);
		kryo.register(Status.class);
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
	}

	/**
	 * specify a demanded status for this part
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class DemStatus
	{
		public String scenario;
		public int partId;
		public double courseDegs;
		public double speedKts;
		public double depthM;
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

		public String toString()
		{
			return name + " (" + listOfParticipants.size() + ")";
		};

		public String name;
		public Vector<LightParticipant> listOfParticipants;
	}

	/**
	 * we wish to start receiving updates for this participant
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class ListenPart
	{
		public String scenarioName;
		public int partId;
	}

	/**
	 * we no longer wish to receive updates for this participant
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class StopListenPart
	{
		public String scenarioName;
		public int partId;
	}

	/**
	 * we no longer wish to control this part, restore it's original decision
	 * model
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class ReleasePart
	{
		public String scenarioName;
		public int partId;
	}

	public static class ScenUpdate
	{
		public ScenUpdate()
		{
		};

		public ScenUpdate(String scenName, String stepped2, long newTime2)
		{
			scenarioName = scenName;
			event = STEPPED;
			newTime = newTime2;
		}

		public String scenarioName;
		public static final String PLAYING = "Started";
		public static final String STEPPED = "Stepped";
		public static final String PAUSED = "Paused";
		public static final String TERMINATED = "Finished";

		public long newTime;
		public String event;
	}

	public static class ListenScen
	{
		public String name;
	}

	public static class StopListenScen
	{
		public String name;
	}

	public static class ScenControl
	{
		public static final String PLAY = "Start";
		public static final String STEP = "Step";
		public static final String PAUSE = "Pause";
		public static final String TERMINATE = "Finish";
		public static final String FASTER = "FASTER";
		public static final String SLOWER = "SLOWER";

		public String instruction;
		public String scenarioName;

		public ScenControl()
		{
		};

		public ScenControl(String scenarioName, String step2)
		{
			this.scenarioName = scenarioName;
			this.instruction = step2;
		}

	}

	public static class PartUpdate
	{
		public int id;
		public Status lStatus;
		public String scenario;

		public PartUpdate()
		{
		};

		public PartUpdate(int id, Status status, String scenario)
		{
			this.id = id;
			this.scenario = scenario;
			lStatus = status;
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
