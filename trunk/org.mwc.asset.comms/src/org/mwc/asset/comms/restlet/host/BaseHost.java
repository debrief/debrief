package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
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

	public static class ScenarioSteppedList implements ScenarioSteppedListener
	{
		HashMap<Integer, URL> _myURLs = new HashMap<Integer, URL>();
		int ctr = 0;

		public int size()
		{
			return _myURLs.size();
		}

		public int add(URL url)
		{
			_myURLs.put(++ctr, url);

			return ctr;
		}

		public void remove(int id)
		{
			_myURLs.remove(id);
		}

		@Override
		public void restart(ScenarioType scenario)
		{
			fireEvent("Restart", 0);
		}

		@Override
		public void step(ScenarioType scenario, long newTime)
		{
			fireEvent("Step", newTime);
		}

		private void fireEvent(String msg, long newTime)
		{
			for (Iterator<URL> url = _myURLs.values().iterator(); url.hasNext();)
			{
				URL thisURL = url.next();
				// find some data
				ClientResource cr = new ClientResource(thisURL.toString());
				List<Preference<MediaType>> type = cr.getClientInfo().getAcceptedMediaTypes();
				cr.getClientInfo().getAcceptedMediaTypes().add(
						new Preference<MediaType>(MediaType.APPLICATION_XML));

				// does it have a scenario?
				ScenarioStateResource scenR = cr.wrap(ScenarioStateResource.class);
				scenR.accept(newTime, msg);
			}
		}
	}

}