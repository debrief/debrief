package org.mwc.asset.netassetserver.views;

import java.io.IOException;
import java.util.Vector;

import org.mwc.asset.comms.kryo.common.ASpecs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public abstract class SPresenter implements ASpecs
{
	private SModel _model;

	public SPresenter()
	{
		// ok, config the model
		try
		{
			_model = new SModel();

			// and sort out the listeners
			_model.registerHandler(new GetScenarios(), new Listener()
			{

				@Override
				public void received(Connection connection, Object object)
				{
					Vector<ScenarioItem> res = getScenarios();
					ScenarioList response = new ScenarioList();
					response.scenarios = res;
					connection.sendTCP(response);
				}
			});
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// also connect up the UI

	}
	
	public void finish()
	{
		_model.stop();
	}

	public abstract Vector<ScenarioItem> getScenarios();
}
