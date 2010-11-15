/**
 * 
 */
package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.restlet.resource.ResourceException;

/** class that maintains a list of REST listeners URLS, ditching entries from the list if/when
 * they fail to respond
 * @author ianmayo
 *
 */
abstract public class BaseListenerList
{
	HashMap<Integer, URL> _myURLs = new HashMap<Integer, URL>();
	int ctr = 0;

	/** how many listeners are there? (mostly for debug)
	 * 
	 * @return
	 */
	public int size()
	{
		return _myURLs.size();
	}

	/** store this listener
	 * 
	 * @param url
	 * @return the unique index provided to this listener
	 */
	public int add(URL url)
	{
		_myURLs.put(++ctr, url);

		return ctr;
	}

	/** ditch this listener
	 * 
	 * @param id
	 */
	public void remove(int id)
	{
		_myURLs.remove(id);
	}
	
	abstract protected void fireThisEvent(URL dest, AssetEvent event);


	protected void fireEvent(AssetEvent event)
	{
		Vector<URL> toDitch = null;

		for (Iterator<URL> url = _myURLs.values().iterator(); url.hasNext();)
		{
			URL thisURL = url.next();

			try
			{
				fireThisEvent(thisURL, event);
			}
			catch (ResourceException re)
			{
				if (re.getStatus().getCode() == 1001)
				{
					if (toDitch == null)
						toDitch = new Vector<URL>();
					toDitch.add(thisURL);
				}
				else
					re.printStackTrace();
			}
		}

		// ok, are we ditching any?
		if (toDitch != null)
		{
			// yup, work through them
			for (Iterator<URL> iterator = toDitch.iterator(); iterator.hasNext();)
			{
				URL thisURL = (URL) iterator.next();

				Set<Integer> mine = _myURLs.keySet();
				for (Iterator<Integer> iterator2 = mine.iterator(); iterator2
						.hasNext();)
				{
					Integer thisId = (Integer) iterator2.next();
					if (_myURLs.get(thisId).equals(thisURL))
					{
						_myURLs.remove(thisId);
					}
				}
			}

			// and close.
			toDitch.removeAllElements();
		}
	}
}