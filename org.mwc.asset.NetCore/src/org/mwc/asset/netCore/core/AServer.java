/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netCore.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.mwc.asset.netCore.common.Network;
import org.mwc.asset.netCore.common.Network.DemStatus;
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

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.MultiScenarioLister;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer
{
	public static final String NETWORK_CONTROL = "Network control";
	private MultiScenarioLister _dataProvider;
	private final SModel _model;
	protected HashMap<String, PartListener> _partListeners;
	protected HashMap<String, ScenListener> _scenListeners;

	public static class SModel
	{
		private final Server _server;
		private final HashMap<Class<?>, Listener> _listeners;

		public SModel() throws IOException
		{
			_server = new Server();
			Network.register(_server);
			_listeners = new HashMap<Class<?>, Listener>();

			// sort out our handler
			_server.addListener(new Listener()
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
						System.err.println("HANDLER NOT FOUND FOR:" + object);
					}
				}
			});
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
			_server.stop();
		}
		
		public void start() throws IOException
		{
			_server.start();
			_server.bind(Network.TCP_PORT, Network.UDP_PORT);

		}

	}

	protected class ScenListener implements ScenarioSteppedListener
	{
		private final Connection connection;
		private final String scenarioName;
		private final ScenarioType scenario;

		public ScenListener(final Connection conn, final ScenarioType scenario, final String name)
		{
			connection = conn;
			scenarioName = name;
			this.scenario = scenario;

			scenario.addScenarioSteppedListener(this);
		}

		@Override
		public void step(final ScenarioType scenario, final long newTime)
		{
			final ScenUpdate su = new ScenUpdate(scenarioName, ScenUpdate.STEPPED, newTime);
			connection.sendTCP(su);
		}

		@Override
		public void restart(final ScenarioType scenario)
		{

		}

		public void release()
		{
			scenario.removeScenarioSteppedListener(this);
		}

	}

	protected class PartListener implements ParticipantMovedListener, ParticipantDetectedListener
	{
		private final Connection _conn;
		private final ParticipantType _part;
		private final int _partId;
		private final String _scenario;
		private DecisionType _defaultBehaviour;

		public PartListener(final Connection conn, final int partId, final ParticipantType part,
				final String scenario)
		{
			_conn = conn;
			_partId = partId;
			_part = part;
			_scenario = scenario;
			_part.addParticipantMovedListener(this);
			_part.addParticipantDetectedListener(this);
		}

		@Override
		public void moved(final Status newStatus)
		{
			// ok, send out the movement details
			final PartMovement pu = new PartMovement(_partId, _scenario, newStatus);
			_conn.sendTCP(pu);
		}
		
		@Override
		public void newDetections(final DetectionList detections)
		{
			final PartDetection pd = new PartDetection(_partId, _scenario, detections);
			_conn.sendTCP(pd);
		}



		@Override
		public void restart(final ScenarioType scenario)
		{

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
				final ParticipantType pt = _part;
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
		
		_model.addListener(new FrameworkMessage.KeepAlive().getClass(), new Listener(){});

		_model.addListener(new ListenScen().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final ListenScen ls = (ListenScen) object;

				listenToScenario(connection, ls);
			}
		});
		_model.addListener(new StopListenScen().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final StopListenScen ls = (StopListenScen) object;

				stopListenToScenario(connection, ls);
			}
		});
		_model.addListener(new ScenControl().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final ScenControl ls = (ScenControl) object;
				controlScenario(ls);
			}
		});
		_model.addListener(new GetScenarios().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				getScenarios(connection);
			}
		});
		_model.addListener(new ListenPart().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final ListenPart cp = (ListenPart) object;
				listenToParticipant(connection, cp);
			}
		});
		_model.addListener(new StopListenPart().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final StopListenPart cp = (StopListenPart) object;
				stopListenToPart(connection, cp.partId);
			}
		});
		_model.addListener(new DemStatus().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final DemStatus cp = (DemStatus) object;
				controlParticipant(connection, cp);
			}
		});
		_model.addListener(new ReleasePart().getClass(), new Listener()
		{
			public void received(final Connection connection, final Object object)
			{
				final ReleasePart cp = (ReleasePart) object;
				releaseParticipant(connection, cp.partId);
			}
		});
		
	}

	protected void stopListenToPart(final Connection connection, final int partId)
	{
		if (partId != Network.DUFF_INDEX)
		{
			// we can work out the exact index, cool.
			final String index = connection.toString() + partId;

			// get the listener
			final PartListener pl = _partListeners.get(index);
			
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
			final Iterator<String> iter = _partListeners.keySet().iterator();
			final Vector<String> toDrop = new Vector<String>();
			while (iter.hasNext())
			{
				final String index = (String) iter.next();
				if (index.startsWith(connection.toString()))
				{
					// ok, ditch it
					toDrop.add(index);
				}
			}

			// did we find any?
			if (toDrop.size() > 0)
			{
				final Iterator<String> drops = toDrop.iterator();
				while (drops.hasNext())
				{
					final String index = (String) drops.next();
					final PartListener pl = _partListeners.get(index);
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

	private ParticipantType getParticipant(final String scenarioName, final int partId)
	{
		ParticipantType part = null;
		final ScenarioType thisS = getScenario(scenarioName);

		if (thisS != null)
			part = thisS.getThisParticipant(partId);

		return part;
	}

	private ScenarioType getScenario(final String scenarioName)
	{
		ScenarioType res = null;
		final Vector<ScenarioType> list = _dataProvider.getScenarios();
		final Iterator<ScenarioType> iter = list.iterator();
		while (iter.hasNext())
		{
			final ScenarioType ns = (ScenarioType) iter.next();
			if (ns.getName().equals(scenarioName))
			{
				res = ns;
				break;
			}
		}
		return res;
	}

	public void setDataProvider(final MultiScenarioLister lister)
	{
		_dataProvider = lister;
	}

	public void start() throws IOException
	{
		_model.start();
	}
	
	public void stop()
	{
		_model.stop();
	}

	public void step(final String scenarioName)
	{
		final ScenarioType scen = getScenario(scenarioName);
		scen.step();
	}

	private void listenToScenario(final Connection connection, final ListenScen ls)
	{
		// get the secnario
		final ScenarioType scen = getScenario(ls.name);

		// ok, start listening to this scenario
		final ScenListener sl = new ScenListener(connection, scen, ls.name);

		// and remember it
		final String index = connection.toString() + ls.name;
		
		System.err.println("adding" + index);
		_scenListeners.put(index, sl);
	}

	private void stopListenToScenario(final Connection connection, final StopListenScen ls)
	{
		// start off with ditching any participant listeners
		stopListenToPart(connection, Network.DUFF_INDEX);

		// and now the server listeners
		final String index = connection.toString() + ls.name;
		final ScenListener sl = _scenListeners.get(index);
		System.err.println("removing:" + index);
		sl.release();
		_scenListeners.remove(sl);
		
	}

	private void controlScenario(final ScenControl ls)
	{
		final ScenarioType st = getScenario(ls.scenarioName);
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
		else if (ls.instruction.equals(ScenControl.FASTER))
		{
			final int curTime = st.getStepTime();
			st.setStepTime(curTime / 2);
		}
		else if (ls.instruction.equals(ScenControl.SLOWER))
		{
			int curTime = st.getStepTime();
			
			// just double-check if we've got zero step time
			if(curTime == 0)
				curTime = 500;
			
			st.setStepTime(curTime * 2);
		}
	}

	private void getScenarios(final Connection connection)
	{
		final ScenarioList res = new ScenarioList();
		final Vector<LightScenario> list = new Vector<LightScenario>();
		final Iterator<ScenarioType> iter = _dataProvider.getScenarios().iterator();
		while (iter.hasNext())
		{
			final ScenarioType scen = (ScenarioType) iter.next();
			list.add(new LightScenario(scen));
		}

		res.list = list;
		connection.sendTCP(res);
	}

	private void listenToParticipant(final Connection connection, final ListenPart cp)
	{
		final ParticipantType part = getParticipant(cp.scenarioName, cp.partId);
		final PartListener pl = new PartListener(connection, cp.partId, part,
				cp.scenarioName);
		final String index = connection.toString() + cp.partId;
		_partListeners.put(index, pl);
	}

	private void controlParticipant(final Connection connection, final DemStatus cp)
	{
		final ParticipantType part = getParticipant(cp.scenario, cp.partId);
		DecisionType dem = part.getDecisionModel();

		if (!(dem instanceof UserControl))
		{
			// ok, take a safe copy of the decision model
			// we can work out the exact index, cool.
			final String index = connection.toString() + cp.partId;

			// get the listener
			final PartListener pl = _partListeners.get(index);
			
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

		final UserControl uc = (UserControl) dem;
		uc.setCourse(cp.courseDegs);
		uc.setSpeed(new WorldSpeed(cp.speedKts, WorldSpeed.Kts));
		uc.setDepth(new WorldDistance(cp.depthM, WorldDistance.METRES));
	}

	private void releaseParticipant(final Connection connection, final int partId)
	{
		// ok, take a safe copy of the decision model
		// we can work out the exact index, cool.
		final String index = connection.toString() + partId;

		// get the listener
		final PartListener pl = _partListeners.get(index);

		// and put the original behaviour back
		pl.restoreBehaviour();
	}
}
