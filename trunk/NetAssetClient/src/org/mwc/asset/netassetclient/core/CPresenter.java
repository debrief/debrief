package org.mwc.asset.netassetclient.core;

import java.io.IOException;

import org.mwc.asset.comms.kryo.common.ACallback;
import org.mwc.asset.comms.kryo.common.ASpecs.AScenario;
import org.mwc.asset.comms.kryo.common.ASpecs.GetScenarios;
import org.mwc.asset.comms.kryo.common.ASpecs.GetThisScenario;
import org.mwc.asset.comms.kryo.common.ASpecs.ScenarioList;

import ASSET.NetworkScenario;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class CPresenter
{
	private CView _view;
	private CModel _model;

	public CPresenter(CView view) throws IOException
	{
		_view = view;

		// create the client
		_model = new CModel();

		// auto connect
		_model.autoConnect();
		
		_view.showMessage("Connected");

		// and register our listeners

	}

	public void getScenarioList(final ACallback<ScenarioList> listener)
	{
		Listener lister = new Listener()
		{
			@Override
			public void received(Connection connection, Object object)
			{
				ScenarioList res = (ScenarioList) object;
				listener.onSuccess(res);
				_model.unregisterHandler(new ScenarioList());
			}
		};

		// get the handler ready
		_model.registerHandler(new ScenarioList(), lister);

		// send the call
		_model.sendTCP(new GetScenarios());
	}

	public void getScenario(String name, final ACallback<NetworkScenario> listener)
	{
		Listener lister = new Listener()
		{
			@Override
			public void received(Connection connection, Object object)
			{
				NetworkScenario res = (NetworkScenario) object;
				listener.onSuccess(res);
				_model.unregisterHandler(new AScenario());
			}
		};

		// get the handler ready
		_model.registerHandler(new AScenario(), lister);

		// send the call
		_model.sendTCP(new GetThisScenario(name));
	}
}
