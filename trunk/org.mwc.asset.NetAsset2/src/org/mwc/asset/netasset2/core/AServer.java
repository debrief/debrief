package org.mwc.asset.netasset2.core;

import java.io.IOException;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.GetScenarios;
import org.mwc.asset.netasset2.common.Network.ScenarioList;

import ASSET.Scenario.MultiScenarioLister;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer
{
	private MultiScenarioLister _dataProvider;
	private SModel _model;

	public static class SModel
	{
		private Server _server;

		public SModel() throws IOException
		{
			_server = new Server();
			Network.register(_server);
			_server.start();
			_server.bind(Network.TCP_PORT, Network.UDP_PORT);
		}

		public void addListener(Listener listener)
		{
			_server.addListener(listener);
		}

		public void stop()
		{
			_server.stop();
		}

	}

	public AServer() throws IOException
	{
		_model = new SModel();

		_model.addListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				if (object instanceof GetScenarios)
				{
					ScenarioList res = new ScenarioList();
					res.list = _dataProvider.getScenarios();
					connection.sendTCP(res);
				}
			}
		});

	}

//	public static void main(String[] args) throws IOException
//	{
//		AServer server = new AServer();
//
//		System.out.println("pausing");
//		System.in.read();
//
//		server.stop();
//	}

	public void setDataProvider(MultiScenarioLister lister)
	{
		_dataProvider = lister;
	}

	public void stop()
	{
		_model.stop();
	}
}
