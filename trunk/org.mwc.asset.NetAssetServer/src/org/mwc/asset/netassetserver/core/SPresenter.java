package org.mwc.asset.netassetserver.core;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.mwc.asset.comms.kryo.common.ASpecs;
import org.mwc.asset.core.ASSETPlugin;

import ASSET.NetworkScenario;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

public abstract class SPresenter implements ASpecs
{
	private SModel _model;
	private SView _view;

	public SPresenter(SView view)
	{
		_view = view;
		
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
					Vector<NetworkScenario> res = getScenarios();
					ScenarioList response = new ScenarioList();
					response.scenarios = res;
					connection.sendTCP(response);
				}
			});
			
			Logger MyLogger = new Logger()
			{
				@Override
				public void log(int arg0, String arg1, String arg2, Throwable arg3)
				{
					_view.showMessage(new Date(), arg2);
				}
			};
			Log.setLogger(MyLogger);
			
		}
		catch (IOException e)
		{
			ASSETPlugin.logError(Status.ERROR, "Initialising NetAsset", e);
		}
		
	}
	
	public void finish()
	{
		_model.stop();
	}

	public abstract Vector<NetworkScenario> getScenarios();
}
