package org.mwc.asset.netCore.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Vector;

import org.mwc.asset.netCore.common.Network;
import org.mwc.asset.netCore.common.Network.GetScenarios;
import org.mwc.asset.netCore.common.Network.LightScenario;
import org.mwc.asset.netCore.common.Network.ListenPart;
import org.mwc.asset.netCore.common.Network.ListenScen;
import org.mwc.asset.netCore.common.Network.PartDetection;
import org.mwc.asset.netCore.common.Network.PartMovement;
import org.mwc.asset.netCore.common.Network.ReleasePart;
import org.mwc.asset.netCore.common.Network.ScenControl;
import org.mwc.asset.netCore.common.Network.ScenUpdate;
import org.mwc.asset.netCore.common.Network.ScenarioList;
import org.mwc.asset.netCore.common.Network.StopListenPart;
import org.mwc.asset.netCore.common.Network.StopListenScen;

import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioSteppedListener;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

public class MClient implements IMClient
{
	private static class NetClient
	{
		private final Client _client;
		private final HashMap<Class<?>, Listener> _listeners;

		public NetClient() throws IOException
		{
			_client = new Client();
			Network.register(_client);
			_client.start();
			_listeners = new HashMap<Class<?>, Listener>();

			// sort out our handler
			_client.addListener(new Listener()
			{

				@Override
				public void received(final Connection connection, final Object object)
				{
					// ok, see if we have a handler
					final Listener match = _listeners.get(object.getClass());
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
			return _client.discoverHosts(Network.UDP_PORT, 1000);
		}

		public void connect(String target) throws IOException
		{
			if (target == null)
			{
				final InetAddress address = _client.discoverHost(Network.UDP_PORT, 1000);
				if (address != null)
					target = address.getHostAddress();

			}

			_client.connect(5000, target, Network.TCP_PORT, Network.UDP_PORT);
		}

		public void addListener(final Class<?> objectType, final Listener listener)
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

		public void send(final Object data)
		{
			_client.sendTCP(data);
		}

	}

	private static class ScenListener
	{
		final ScenarioSteppedListener _stepper;

		public ScenListener(final ScenarioSteppedListener stepper)
		{
			_stepper = stepper;
		}
	}

	private static class PartListener
	{
		final ParticipantMovedListener _mover;
		@SuppressWarnings("unused")
		final ParticipantDecidedListener _decider;
		final ParticipantDetectedListener _detector;

		public PartListener(final ParticipantMovedListener mover,
				final ParticipantDecidedListener decider, final ParticipantDetectedListener detector)
		{
			_mover = mover;
			_decider = decider;
			_detector = detector;
		}
	}

	private final NetClient _model;
	private final HashMap<String, PartListener> _partListeners;
	private final HashMap<String, ScenListener> _scenListeners;

	public MClient() throws IOException
	{
		_model = new NetClient();
		_partListeners = new HashMap<String, PartListener>();
		_scenListeners = new HashMap<String, ScenListener>();

		// get ready to ignore i'm alive messages...
		_model.addListener(new FrameworkMessage.KeepAlive().getClass(),
				new Listener()
				{
				});

		// setup the step listener
		final Listener mover = new Listener()
		{
			@Override
			public void received(final Connection connection, final Object object)
			{
				final PartMovement pu = (PartMovement) object;

				final String index = pu.scenario + pu.id;
				final PartListener pl = _partListeners.get(index);
				if (pl != null)
				{
					final Status newStat = pu.lStatus;
					if (pl._mover != null)
						pl._mover.moved(newStat);
				}
				else
				{
					System.err.println("LISTENER NOT FOUND FOR:" + index);
				}
			}
		};
		_model.addListener(new PartMovement().getClass(), mover);
		final Listener detector = new Listener()
		{
			@Override
			public void received(final Connection connection, final Object object)
			{
				final PartDetection pu = (PartDetection) object;

				final String index = pu.scenario + pu.id;
				final PartListener pl = _partListeners.get(index);
				if (pl != null)
				{
					if (pl._detector != null)
						pl._detector.newDetections(pu.detections);
				}
				else
				{
					System.err.println("LISTENER NOT FOUND FOR:" + index);
				}
			}
		};
		_model.addListener(new PartDetection().getClass(), detector);

		final Listener stepL = new Listener()
		{

			@Override
			public void received(final Connection connection, final Object object)
			{
				final ScenUpdate su = (ScenUpdate) object;
				final String index = su.scenarioName;
				final ScenListener sl = _scenListeners.get(index);

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
	public void connect(final String target) throws IOException
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
	public void listenPart(final String scenarioName, final int participantId,
			final ParticipantMovedListener moveL, final ParticipantDecidedListener decider,
			final ParticipantDetectedListener detector)
	{
		final ListenPart cp = new ListenPart();
		cp.scenarioName = scenarioName;
		cp.partId = participantId;

		// ok, register the listener
		final String index = scenarioName + participantId;
		final PartListener pl = new PartListener(moveL, decider, detector);
		_partListeners.put(index, pl);

		_model.send(cp);

	}

	@Override
	public void listenScen(final String scenarioName, final ScenarioSteppedListener listener)
	{
		// get ready to rx events
		final ScenListener sl = new ScenListener(listener);
		_scenListeners.put(scenarioName, sl);

		// register an interest
		final ListenScen ls = new ListenScen();
		ls.name = scenarioName;
		_model.send(ls);
	}

	@Override
	public void stopListenScen(final String scenarioName)
	{
		// tell it we're not bothered
		final StopListenScen ls = new StopListenScen();
		ls.name = scenarioName;
		_model.send(ls);

		// ok, done. now stop listening
		final ScenListener sl = _scenListeners.get(scenarioName);
		_scenListeners.remove(sl);
	}

	@Override
	public void step(final String scenarioName)
	{
		final ScenControl sc = new ScenControl(scenarioName, ScenControl.STEP);
		_model.send(sc);
	}

	@Override
	public void stop(final String scenarioName)
	{
		final ScenControl sc = new ScenControl(scenarioName, ScenControl.TERMINATE);
		_model.send(sc);
	}

	/**
	 * user wants to release this participant
	 * 
	 * @param scenarioName
	 * @param participantId
	 */
	@Override
	public void stopListenPart(final String scenarioName, final int participantId)
	{
		final StopListenPart cp = new StopListenPart();
		cp.scenarioName = scenarioName;
		cp.partId = participantId;

		// stop listening
		_model.send(cp);

		// and forget our listener
		final String index = scenarioName + participantId;
		_partListeners.remove(index);
	}

	@Override
	public void getScenarioList(
			final Network.AHandler<Vector<LightScenario>> handler)
	{
		final Class<?> theType = new GetScenarios().getClass();
		final Listener listener = new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final ScenarioList sl = (ScenarioList) object;
				handler.onSuccess(sl.list);
				// and forget about ourselves
				_model.removeListener(theType);
			}
		};
		_model.addListener(new ScenarioList().getClass(), listener);
		_model.send(new GetScenarios());
	}

	@Override
	public void controlPart(final String scenario, final int id, final double courseDegs,
			final double speedKts, final double depthM)
	{
		final Network.DemStatus dem = new Network.DemStatus();
		dem.scenario = scenario;
		dem.partId = id;
		dem.courseDegs = courseDegs;
		dem.speedKts = speedKts;
		dem.depthM = depthM;
		_model.send(dem);
	}

	@Override
	public void releasePart(final String scenario, final int partId)
	{
		final ReleasePart rp = new ReleasePart();
		rp.scenarioName = scenario;
		rp.partId = partId;
		_model.send(rp);
	}

	@Override
	public void controlScen(final ScenControl sc)
	{
		_model.send(sc);
	}

}
