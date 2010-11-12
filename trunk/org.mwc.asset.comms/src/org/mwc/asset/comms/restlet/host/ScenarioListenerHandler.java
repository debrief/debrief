package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ScenarioListenerResource;
import org.restlet.resource.ServerResource;

public class ScenarioListenerHandler extends ServerResource implements
		ScenarioListenerResource
{

	static HashMap<Integer, URL> _myListeners;
	static int ctr = 0;

	@Override
	public int accept(URL listener)
	{
		if (_myListeners == null)
			_myListeners = new HashMap<Integer, URL>();

		_myListeners.put(++ctr, listener);

		System.out.println("-----------");
		if (_myListeners != null)
		{
			Iterator<URL> urls = _myListeners.values().iterator();
			while (urls.hasNext())
			{
				URL next = urls.next();
				if(next != null)
					System.out.println(next.toString());
				else
					System.err.println("no next...");
			}
		}

		return ctr;
	}

	@Override
	public void remove()
	{
		Map<String, Object> attrs = this.getRequestAttributes();
		// Object thisS = attrs.get("scenario");
		Object thisP = attrs.get("listener");
		int theId = Integer.parseInt((String) thisP);

		_myListeners.remove(theId);
	}

}