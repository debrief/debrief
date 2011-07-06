package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.GetScenarios;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.common.Network.ListenPart;
import org.mwc.asset.netasset2.common.Network.ListenScen;
import org.mwc.asset.netasset2.common.Network.PartUpdate;
import org.mwc.asset.netasset2.common.Network.ReleasePart;
import org.mwc.asset.netasset2.common.Network.ScenControl;
import org.mwc.asset.netasset2.common.Network.ScenUpdate;
import org.mwc.asset.netasset2.common.Network.ScenarioList;
import org.mwc.asset.netasset2.common.Network.StopListenPart;
import org.mwc.asset.netasset2.common.Network.StopListenScen;

import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioSteppedListener;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class MClient implements IMClient
{
	private static class CModel
	{
		private Client _client;
		private HashMap<Class<?>, Listener> _listeners;

		public CModel() throws IOException
		{
			_client = new Client();
			Network.register(_client);
			_client.start();
			_listeners = new HashMap<Class<?>, Listener>();

			// sort out our handler
			_client.addListener(new Listener()
			{

				@Override
				public void received(Connection connection, Object object)
				{
					// ok, see if we have a handler
					Listener match = _listeners.get(object.getClass());
					if (match != null)
					{
						match.received(connection, object);
					}
					else
					{
						System.err.println("CLIENT HANDLER NOT FOUND FOR:" + object);
					}
				}
			});
		}

		public java.util.List<java.net.InetAddress> discoverHosts()
		{
			return _client.discoverHosts(Network.UDP_PORT, 3000);
		}
		
		public void connect(String target) throws IOException
		{
			if (target == null)
			{
				InetAddress address = _client.discoverHost(Network.UDP_PORT, 1000);
				if (address != null)
					target = address.getHostAddress();

			}

			_client.connect(5000, target, Network.TCP_PORT, Network.UDP_PORT);
		}

		public void addListener(Class<?> objectType, Listener listener)
		{
			_listeners.put(objectType, listener);
		}

		public void removeListener(final Class<?> objectType)
		{
			_listeners.remove(objectType);
		}

		public void stop()
		{
			_client.stop();
		}

		public void send(Object data)
		{
			_client.sendTCP(data);
		}

	}

	private static class ScenListener
	{
		final ScenarioSteppedListener _stepper;

		public ScenListener(ScenarioSteppedListener stepper)
		{
			_stepper = stepper;
		}
	}

	private static class PartListener
	{
		final ParticipantMovedListener _mover;
		@SuppressWarnings("unused")
		final ParticipantDecidedListener _decider;
		@SuppressWarnings("unused")
		final ParticipantDetectedListener _detector;

		public PartListener(ParticipantMovedListener mover,
				ParticipantDecidedListener decider, ParticipantDetectedListener detector)
		{
			_mover = mover;
			_decider = decider;
			_detector = detector;
		}
	}

	private CModel _model;
	private HashMap<String, PartListener> _partListeners;
	private HashMap<String, ScenListener> _scenListeners;

	public MClient() throws IOException
	{
		_model = new CModel();
		_partListeners = new HashMap<String, PartListener>();
		_scenListeners = new HashMap<String, ScenListener>();

		// setup the step listener
		Listener mover = new Listener()
		{
			@Override
			public void received(Connection connection, Object object)
			{
				PartUpdate pu = (PartUpdate) object;

				String index = pu.scenario + pu.id;
				PartListener pl = _partListeners.get(index);
				if (pl != null)
				{
					Status newStat = pu.lStatus;
					pl._mover.moved(newStat);
				}
				else
				{
					System.err.println("LISTENER NOT FOUND FOR:" + index);
				}
			}
		};
		_model.addListener(new PartUpdate().getClass(), mover);

		Listener stepL = new Listener()
		{

			@Override
			public void received(Connection connection, Object object)
			{
				ScenUpdate su = (ScenUpdate) object;
				String index = su.scenarioName;
				ScenListener sl = _scenListeners.get(index);

				// have a look at the event
				if (su.event.equals(ScenUpdate.STEPPED))
				{
					sl._stepper.step(null, su.newTime);
				}
			}
		};
		_model.addListener(new ScenUpdate().getClass(), stepL);

	}
	
	@Override
	public java.util.List<java.net.InetAddress> discoverHosts()
	{
		return _model.discoverHosts();
	}


	@Override
	public void connect(String target) throws IOException
	{
		_model.connect(target);
	}

	@Override
	public void stop()
	{
		_model.stop();
	}

	/**
	 * user wants to listen to this participant
	 * 
	 * @param scenarioName
	 * @param participantId
	 */
	@Override
	public void listenPart(String scenarioName, int participantId,
			ParticipantMovedListener moveL, ParticipantDecidedListener decider,
			ParticipantDetectedListener detector)
	{
		ListenPart cp = new ListenPart();
		cp.scenarioName = scenarioName;
		cp.partId = participantId;

		// ok, register the listener
		String index = scenarioName + participantId;
		PartListener pl = new PartListener(moveL, decider, detector);
		_partListeners.put(index, pl);

		_model.send(cp);

	}

	@Override
	public void listenScen(String scenarioName,
			ScenarioSteppedListener listener)
	{
		// get ready to rx events
		ScenListener sl = new ScenListener(listener);
		_scenListeners.put(scenarioName, sl);

		// register an interest
		ListenScen ls = new ListenScen();
		ls.name = scenarioName;
		_model.send(ls);
	}

	@Override
	public void stopListenScen(String scenarioName)
	{
		// tell it we're not bothered
		StopListenScen ls = new StopListenScen();
		ls.name = scenarioName;
		_model.send(ls);

		// ok, done. now stop listening
		ScenListener sl = _scenListeners.get(scenarioName);
		_scenListeners.remove(sl);
	}
	
	@Override
	public void step(String scenarioName)
	{
		ScenControl sc = new ScenControl(scenarioName, ScenControl.STEP);
		_model.send(sc);
	}

	/**
	 * user wants to release this participant
	 * 
	 * @param scenarioName
	 * @param participantId
	 */
	@Override
	public void stopListenPart(String scenarioName, int participantId)
	{
		StopListenPart cp = new StopListenPart();
		cp.scenarioName = scenarioName;
		cp.partId = participantId;

		String index = scenarioName + participantId;
		_partListeners.remove(index);

		_model.send(cp);
	}

	@Override
	public void getScenarioList(
			final Network.AHandler<Vector<LightScenario>> handler)
	{
		final Class<?> theType = new GetScenarios().getClass();
		final Listener listener = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ScenarioList sl = (ScenarioList) object;
				handler.onSuccess(sl.list);
				// and forget about ourselves
				_model.removeListener(theType);
			}
		};
		_model.addListener(new ScenarioList().getClass(), listener);
		_model.send(new GetScenarios());
	}

	@Override
	public void controlPart(String scenario, int id, double courseDegs,
			double speedKts, double depthM)
	{
		Network.DemStatus dem = new Network.DemStatus();
		dem.scenario = scenario;
		dem.partId = id;
		dem.courseDegs = courseDegs;
		dem.speedKts = speedKts;
		dem.depthM = depthM;
		_model.send(dem);
	}
	
	@Override
	public void releasePart(String scenario, int partId)
	{
		ReleasePart rp = new ReleasePart();
		rp.scenarioName = scenario;
		rp.partId = partId;
		_model.send(rp);
	}

	@Override
	public void controlScen(ScenControl sc)
	{
		_model.send(sc);
	}

}
