package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.DemStatus;
import org.mwc.asset.netasset2.common.Network.GetScenarios;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.common.Network.ListenPart;
import org.mwc.asset.netasset2.common.Network.PartUpdate;
import org.mwc.asset.netasset2.common.Network.ScenarioList;
import org.mwc.asset.netasset2.common.Network.StopListenPart;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.UserControl;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.MultiScenarioLister;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer
{
	private MultiScenarioLister _dataProvider;
	private SModel _model;
	protected HashMap<String, PartListener> _partListeners;

	public static class SModel
	{
		private Server _server;
		private HashMap<Class<?>, Listener> _listeners;

		public SModel() throws IOException
		{
			_server = new Server();
			Network.register(_server);
			_server.start();
			_server.bind(Network.TCP_PORT, Network.UDP_PORT);

			_listeners = new HashMap<Class<?>, Listener>();

			// sort out our handler
			_server.addListener(new Listener()
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
						System.err.println("HANDLER NOT FOUND FOR:" + object);
					}
				}
			});
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
			_server.stop();
		}

	}

	protected class PartListener implements ParticipantMovedListener
	{
		private final Connection _conn;
		private final ParticipantType _part;
		private final int _partId;
		private final String _scenario;

		public PartListener(Connection conn, int partId, ParticipantType part,
				String scenario)
		{
			_conn = conn;
			_partId = partId;
			_part = part;
			_scenario = scenario;

			_part.addParticipantMovedListener(this);
		}

		@Override
		public void moved(Status newStatus)
		{
			// ok, send out the movement details
			PartUpdate pu = new PartUpdate(_partId, newStatus, _scenario);
			_conn.sendTCP(pu);
		}

		@Override
		public void restart(ScenarioType scenario)
		{
			// TODO Auto-generated method stub

		}

		public void release()
		{
			_part.removeParticipantMovedListener(this);
		}

	}

	public AServer() throws IOException
	{
		_model = new SModel();
		_partListeners = new HashMap<String, PartListener>();

		Listener getS = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ScenarioList res = new ScenarioList();
				Vector<LightScenario> list = new Vector<LightScenario>();
				Iterator<ScenarioType> iter = _dataProvider.getScenarios().iterator();
				while (iter.hasNext())
				{
					ScenarioType scen = (ScenarioType) iter.next();
					list.add(new LightScenario(scen));
				}

				res.list = list;
				connection.sendTCP(res);
			}
		};
		_model.addListener(new GetScenarios().getClass(), getS);

		Listener listenP = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ListenPart cp = (ListenPart) object;
				ParticipantType part = getParticipant(cp.scenarioName, cp.partId);
				PartListener pl = new PartListener(connection, cp.partId, part,
						cp.scenarioName);
				String index = connection.toString() + cp.partId;
				_partListeners.put(index, pl);
			}
		};
		_model.addListener(new ListenPart().getClass(), listenP);
		Listener releaseP = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				StopListenPart cp = (StopListenPart) object;
				dropListener(connection.toString(), cp.partId);
			}
		};
		_model.addListener(new StopListenPart().getClass(), releaseP);
		Listener demS = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				DemStatus cp = (DemStatus) object;
				ParticipantType part = getParticipant(cp.scenario, cp.partId);
				DecisionType dem = part.getDecisionModel();

				if (!(dem instanceof UserControl))
				{
					UserControl uc2 = new UserControl(0, null, null);
					part.setDecisionModel(uc2);
				}

				dem = part.getDecisionModel();
				if (dem instanceof UserControl)
				{
					UserControl uc = (UserControl) dem;
					uc.setCourse(cp.courseDegs);
					uc.setSpeed(new WorldSpeed(cp.speedKts, WorldSpeed.Kts));
					uc.setDepth(new WorldDistance(cp.depthM, WorldDistance.METRES));
				}
				else
				{
					System.err.println("PARTICIPANT IS NOT CONTROLLABLE!");
				}
			}
		};
		_model.addListener(new DemStatus().getClass(), demS);

	}

	protected void dropListener(String connStr, int partId)
	{
		if (partId != -1)
		{
			// we can work out the exact index, cool.
			String index = connStr + partId;

			// get the listener
			PartListener pl = _partListeners.get(index);

			// tell it to stop listening to hte part
			pl.release();

			// and forget about it.
			_partListeners.remove(index);
		}
		else
		{
			// we have to loop thorugh to find the right connection(s)
			Iterator<String> iter = _partListeners.keySet().iterator();
			Vector<String> toDrop = new Vector<String>();
			while (iter.hasNext())
			{
				String index = (String) iter.next();
				if (index.startsWith(connStr))
				{
					// ok, ditch it
					toDrop.add(index);
				}
			}

			// did we find any?
			if (toDrop.size() > 0)
			{
				Iterator<String> drops = toDrop.iterator();
				while (drops.hasNext())
				{
					String index = (String) drops.next();
					PartListener pl = _partListeners.get(index);
					pl.release();
					_partListeners.remove(index);

				}
			}
		}
	}

	public Set<String> getPartListeners()
	{
		return _partListeners.keySet();
	}

	protected ParticipantType getParticipant(String scenarioName, int partId)
	{
		ParticipantType part = null;
		ScenarioType thisS = getScenario(scenarioName);

		if (thisS != null)
			part = thisS.getThisParticipant(partId);

		return part;
	}

	private ScenarioType getScenario(String scenarioName)
	{
		ScenarioType res = null;
		Vector<ScenarioType> list = _dataProvider.getScenarios();
		Iterator<ScenarioType> iter = list.iterator();
		while (iter.hasNext())
		{
			ScenarioType ns = (ScenarioType) iter.next();
			if (ns.getName().equals(scenarioName))
			{
				res = ns;
				break;
			}
		}
		return res;
	}

	public void setDataProvider(MultiScenarioLister lister)
	{
		_dataProvider = lister;
	}

	public void stop()
	{
		_model.stop();
	}

	public void step(String scenarioName)
	{
		ScenarioType scen = getScenario(scenarioName);
		scen.step();
	}
}
