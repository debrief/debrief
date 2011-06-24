/**
 * 
 */
package org.mwc.asset.comms.restlet.host;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * class that maintains a list of REST listeners URIS, ditching entries from the
 * list if/when they fail to respond
 * 
 * @author ianmayo
 * 
 */
abstract public class BaseListenerList<EventType extends AssetEvent>
{
	private HashMap<Integer, URI> _myURIs = new HashMap<Integer, URI>();
	int ctr = 0;

	/**
	 * how many listeners are there? (mostly for debug)
	 * 
	 * @return
	 */
	public int size()
	{
		return _myURIs.size();
	}

	/**
	 * store this listener
	 * 
	 * @param url
	 * @return the unique index provided to this listener
	 */
	protected int add(URI url)
	{
		_myURIs.put(++ctr, url);

		return ctr;
	}

	/**
	 * ditch this listener
	 * 
	 * @param id
	 */
	protected void remove(int id)
	{
		_myURIs.remove(id);
	}

	/**
	 * fire an event to the specified URI
	 * 
	 * @param client
	 * @param event
	 */
	abstract protected void fireThisEvent(ClientResource client, EventType event);

	/**
	 * fire the supplied event to my listeners
	 * 
	 * @param event
	 */
	protected void fireEvent(EventType event)
	{
		Vector<URI> toDitch = null;

		for (Iterator<URI> url = _myURIs.values().iterator(); url.hasNext();)
		{
			URI thisURI = url.next();

			try
			{
				System.out.println("about to send to " + thisURI.toString());
				ClientResource client = new ClientResource(thisURI.toString());
				fireThisEvent(client, event);
				client.release();
			}
			catch (ResourceException re)
			{
				if (re.getStatus().getCode() == 1001)
				{
					if (toDitch == null)
						toDitch = new Vector<URI>();
					toDitch.add(thisURI);
				}
				else
					re.printStackTrace();
			}
			catch (Exception e)
			{
				System.out.println("failed trying to sent to " + thisURI.toString());
				e.printStackTrace();
			}
		}

		// ok, are we ditching any?
		if (toDitch != null)
		{
			// yup, work through them
			for (Iterator<URI> iterator = toDitch.iterator(); iterator.hasNext();)
			{
				URI thisURI = (URI) iterator.next();

				Set<Integer> mine = _myURIs.keySet();
				for (Iterator<Integer> iterator2 = mine.iterator(); iterator2.hasNext();)
				{
					Integer thisId = (Integer) iterator2.next();
					if (_myURIs.get(thisId).equals(thisURI))
					{
						_myURIs.remove(thisId);
					}
				}
			}

			// and close.
			toDitch.removeAllElements();
		}
	}
}