package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.ControlPart;
import org.mwc.asset.netasset2.common.Network.GetScenarios;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.common.Network.PartUpdate;
import org.mwc.asset.netasset2.common.Network.ReleasePart;
import org.mwc.asset.netasset2.common.Network.ScenarioList;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.MultiScenarioLister;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer
{
	@SuppressWarnings("unused")
	private MultiScenarioLister _dataProvider;
	private SModel _model;
	protected HashMap<String, PartListener> _partListeners;
	protected ParticipantType _mockPart;

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
					System.err.println("SERVER:" + object.getClass());
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

		public PartListener(Connection conn, int partId, ParticipantType part)
		{
			_conn = conn;
			_partId = partId;
			_part = part;
			
			_part.addParticipantMovedListener(this);
		}

		@Override
		public void moved(Status newStatus)
		{
			// ok, send out the movement details
			PartUpdate pu = new PartUpdate(_partId, newStatus.getTime());
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
				res.list = new Vector<LightScenario>();
				res.list.add(new LightScenario("zaa"));
				res.list.add(new LightScenario("bbb"));
				res.list.add(new LightScenario("ccc"));
				connection.sendTCP(res);
			}
		};
		_model.addListener(new GetScenarios().getClass(), getS);

		Listener controlP = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ControlPart cp = (ControlPart) object;
				ParticipantType part = getParticipant(cp.scenarioName, cp.partId);
				PartListener pl = new PartListener(connection, cp.partId, part);
				String index = connection.toString() + cp.partId;
				_partListeners.put(index, pl);
			}
		};
		_model.addListener(new ControlPart().getClass(), controlP);
		Listener releaseP = new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ReleasePart cp = (ReleasePart) object;
				String index = connection.toString() + cp.partId;
				_partListeners.remove(index);
			}
		};
		_model.addListener(new ReleasePart().getClass(), releaseP);

	}

	public Set<String> getPartListeners()
	{
		return _partListeners.keySet();
	}
	
	protected ParticipantType getParticipant(String scenarioName, int partId)
	{
		if(_mockPart == null)
			_mockPart = new Surface(23);
		return _mockPart;
	}

	public void setDataProvider(MultiScenarioLister lister)
	{
		_dataProvider = lister;
	}

	public void stop()
	{
		_model.stop();
	}
}
