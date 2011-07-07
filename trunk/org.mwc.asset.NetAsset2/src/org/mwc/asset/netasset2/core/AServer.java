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
import org.mwc.asset.netasset2.common.Network.ListenScen;
import org.mwc.asset.netasset2.common.Network.PartUpdate;
import org.mwc.asset.netasset2.common.Network.ReleasePart;
import org.mwc.asset.netasset2.common.Network.ScenControl;
import org.mwc.asset.netasset2.common.Network.ScenUpdate;
import org.mwc.asset.netasset2.common.Network.ScenarioList;
import org.mwc.asset.netasset2.common.Network.StopListenPart;
import org.mwc.asset.netasset2.common.Network.StopListenScen;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.UserControl;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.MultiScenarioLister;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer
{
	public static final String NETWORK_CONTROL = "Network control";
	private MultiScenarioLister _dataProvider;
	private SModel _model;
	protected HashMap<String, PartListener> _partListeners;
	protected HashMap<String, ScenListener> _scenListeners;

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

	protected class ScenListener implements ScenarioSteppedListener
	{
		private final Connection connection;
		private final String scenarioName;
		private ScenarioType scenario;

		public ScenListener(Connection conn, ScenarioType scenario, String name)
		{
			connection = conn;
			scenarioName = name;
			this.scenario = scenario;

			scenario.addScenarioSteppedListener(this);
		}

		@Override
		public void step(ScenarioType scenario, long newTime)
		{
			ScenUpdate su = new ScenUpdate(scenarioName, ScenUpdate.STEPPED, newTime);
			connection.sendTCP(su);
		}

		@Override
		public void restart(ScenarioType scenario)
		{
			// TODO Auto-generated method stub

		}

		public void release()
		{
			scenario.removeScenarioSteppedListener(this);
		}

	}

	protected class PartListener implements ParticipantMovedListener
	{
		private final Connection _conn;
		private final ParticipantType _part;
		private final int _partId;
		private final String _scenario;
		private DecisionType _defaultBehaviour;

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

		public void restoreBehaviour()
		{
			// does it have a default behaviour?
			if(_defaultBehaviour  != null)
			{
				ParticipantType pt = _part;
				pt.setDecisionModel(_defaultBehaviour);
				// and forget it
				_defaultBehaviour = null;
			}
		}

	}

	public AServer() throws IOException
	{
		_model = new SModel();
		_partListeners = new HashMap<String, PartListener>();
		_scenListeners = new HashMap<String, ScenListener>();

		_model.addListener(new ListenScen().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ListenScen ls = (ListenScen) object;

				listenToScenario(connection, ls);
			}
		});
		_model.addListener(new StopListenScen().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				StopListenScen ls = (StopListenScen) object;

				stopListenToScenario(connection, ls);
			}
		});
		_model.addListener(new ScenControl().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ScenControl ls = (ScenControl) object;
				controlScenario(ls);
			}
		});
		_model.addListener(new GetScenarios().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				getScenarios(connection);
			}
		});
		_model.addListener(new ListenPart().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ListenPart cp = (ListenPart) object;
				listenToParticipant(connection, cp);
			}
		});
		_model.addListener(new StopListenPart().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				StopListenPart cp = (StopListenPart) object;
				stopListenToPart(connection, cp.partId);
			}
		});
		_model.addListener(new DemStatus().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				DemStatus cp = (DemStatus) object;
				controlParticipant(connection, cp);
			}
		});
		_model.addListener(new ReleasePart().getClass(), new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ReleasePart cp = (ReleasePart) object;
				releaseParticipant(connection, cp.partId);
			}
		});
		
	}

	protected void stopListenToPart(Connection connection, int partId)
	{
		if (partId != Network.DUFF_INDEX)
		{
			// we can work out the exact index, cool.
			String index = connection.toString() + partId;

			// get the listener
			PartListener pl = _partListeners.get(index);
			
			// restore behaviour, if we're in control
			pl.restoreBehaviour();

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
				if (index.startsWith(connection.toString()))
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
					// restore behaviour, if we're in control
					pl.restoreBehaviour();

					// now stop listening to it
					pl.release();
					
					// and forget about it
					_partListeners.remove(index);

				}
			}
		}
	}

	public Set<String> getPartListeners()
	{
		return _partListeners.keySet();
	}

	public HashMap<String, ScenListener> getScenListeners()
	{
		return _scenListeners;
	}

	private ParticipantType getParticipant(String scenarioName, int partId)
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

	private void listenToScenario(Connection connection, ListenScen ls)
	{
		// get the secnario
		ScenarioType scen = getScenario(ls.name);

		// ok, start listening to this scenario
		ScenListener sl = new ScenListener(connection, scen, ls.name);

		// and remember it
		String index = connection.toString() + ls.name;
		_scenListeners.put(index, sl);
	}

	private void stopListenToScenario(Connection connection, StopListenScen ls)
	{
		// start off with ditching any participant listeners
		stopListenToPart(connection, Network.DUFF_INDEX);

		// and now the server listeners
		String index = connection.toString() + ls.name;
		ScenListener sl = _scenListeners.get(index);
		sl.release();
		_scenListeners.remove(sl);
		
	}

	private void controlScenario(ScenControl ls)
	{
		ScenarioType st = getScenario(ls.scenarioName);
		if (ls.instruction.equals(ScenControl.STEP))
		{
			st.step();
		}
		else if (ls.instruction.equals(ScenControl.PLAY))
		{
			st.start();
		}
		else if (ls.instruction.equals(ScenControl.PAUSE))
		{
			st.pause();
		}
		else if (ls.instruction.equals(ScenControl.TERMINATE))
		{
			st.stop("Client finish");
		}
	}

	private void getScenarios(Connection connection)
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

	private void listenToParticipant(Connection connection, ListenPart cp)
	{
		ParticipantType part = getParticipant(cp.scenarioName, cp.partId);
		PartListener pl = new PartListener(connection, cp.partId, part,
				cp.scenarioName);
		String index = connection.toString() + cp.partId;
		_partListeners.put(index, pl);
	}

	private void controlParticipant(Connection connection, DemStatus cp)
	{
		ParticipantType part = getParticipant(cp.scenario, cp.partId);
		DecisionType dem = part.getDecisionModel();

		if (!(dem instanceof UserControl))
		{
			// ok, take a safe copy of the decision model
			// we can work out the exact index, cool.
			String index = connection.toString() + cp.partId;

			// get the listener
			PartListener pl = _partListeners.get(index);
			
			if(pl == null)
				System.err.println("CAN'T FIND PART LISTENER FOR:" + cp.partId);

			// take a safe copy of the behaviour, so we can later cancel it
			pl._defaultBehaviour = dem;

			// create a user control behaviour
			dem = new UserControl(0, null, null);
			dem.setName(NETWORK_CONTROL);
			
			// and assign it
			part.setDecisionModel(dem);
		}

		UserControl uc = (UserControl) dem;
		uc.setCourse(cp.courseDegs);
		uc.setSpeed(new WorldSpeed(cp.speedKts, WorldSpeed.Kts));
		uc.setDepth(new WorldDistance(cp.depthM, WorldDistance.METRES));
	}

	private void releaseParticipant(Connection connection, int partId)
	{
		// ok, take a safe copy of the decision model
		// we can work out the exact index, cool.
		String index = connection.toString() + partId;

		// get the listener
		PartListener pl = _partListeners.get(index);

		// and put the original behaviour back
		pl.restoreBehaviour();
	}
}
