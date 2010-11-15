package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource.ScenarioEvent;
import org.restlet.resource.ClientResource;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioSteppedListener;

abstract public class BaseHost implements ASSETHost
{
	private HashMap<Integer, ScenarioSteppedList> _stepListeners;

	protected HashMap<Integer, URL> _participantListeners = new HashMap<Integer, URL>();
	public int _participantCounter = 0;

	public ScenarioSteppedList getSteppedListFor(int scenarioId)
	{
		// are we already listening to this scenario?
		if (_stepListeners == null)
		{
			_stepListeners = new HashMap<Integer, ScenarioSteppedList>();
		}

		ScenarioSteppedList thisList = _stepListeners.get(scenarioId);

		if (thisList == null)
		{
			thisList = new ScenarioSteppedList()
			{
			};
			_stepListeners.put(scenarioId, thisList);
		}

		return thisList;
	}

	public void deleteScenarioListener(int scenarioId, int listenerId)
	{
		// are we already listening to this scenario?
		ScenarioSteppedList thisList = getSteppedListFor(scenarioId);
		if (thisList != null)
		{
			thisList.remove(listenerId);
		}
	}

	@Override
	public int newScenarioListener(int scenarioId, URL url)
	{
		ScenarioSteppedList thisList = getSteppedListFor(scenarioId);

		return thisList.add(url);
	}
	
	public static class ScenarioSteppedList extends BaseListenerList implements ScenarioSteppedListener
	{

		@Override
		public void restart(ScenarioType scenario)
		{

			AssetEvent event = new ScenarioEvent("Restart", "unknown", 0, 0);
			fireEvent(event);
		}

		@Override
		public void step(ScenarioType scenario, long newTime)
		{
			AssetEvent event = new ScenarioEvent("Step", "unknown", newTime, 0);
			fireEvent(event);
		}

		protected void fireThisEvent(URL dest, AssetEvent event)
		{
			// fire some data
			ClientResource cr = new ClientResource(dest.toString());

			// does it have a scenario?
			ScenarioStateResource scenR = cr.wrap(ScenarioStateResource.class);
			scenR.accept((ScenarioEvent) event);
		}

	}
}