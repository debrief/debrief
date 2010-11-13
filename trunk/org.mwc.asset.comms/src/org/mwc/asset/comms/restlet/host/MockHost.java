/**
 * 
 */
package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;

class MockHost implements ASSETHost
{
	HashMap<Integer, URL> _scenarioListeners = new HashMap<Integer, URL>();
	int _scenarioCounter = 1;

	@Override
	public void deleteListener(int listenerId)
	{
		_scenarioListeners.remove(listenerId);
	}

	@Override
	public int newScenarioListener(int scenario, URL url)
	{
		_scenarioListeners.put(scenario, url);
		return _scenarioCounter++;
	}
	
}