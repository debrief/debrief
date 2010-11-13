package org.mwc.asset.comms.restlet.host;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ScenarioListenerResource;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;

public class ScenarioListenerHandler extends ServerResource implements
		ScenarioListenerResource
{

	static HashMap<Integer, URL> _myListeners;
	static int ctr = 0;

	public static void main(String[] args)
	{
		URL res;
		try
		{
			res = new URL("http://google.com");
			System.out.println("url is: " + res.toString());
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public int accept(String listenerTxt)
	{
		if (_myListeners == null)
			_myListeners = new HashMap<Integer, URL>();

		URL listener;
		try
		{
			listener = new URL(listenerTxt);
			_myListeners.put(++ctr, listener);

		}
		catch (MalformedURLException e)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
		
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