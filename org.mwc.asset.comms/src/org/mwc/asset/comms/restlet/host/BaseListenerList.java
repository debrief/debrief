/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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

/** class that maintains a list of REST listeners URIS, ditching entries from the list if/when
 * they fail to respond
 * @author ianmayo
 *
 */
abstract public class BaseListenerList<EventType extends AssetEvent>
{
	private final HashMap<Integer, URI> _myURIs = new HashMap<Integer, URI>();
	int ctr = 0;

	/** how many listeners are there? (mostly for debug)
	 * 
	 * @return
	 */
	public int size()
	{
		return _myURIs.size();
	}

	/** store this listener
	 * 
	 * @param url
	 * @return the unique index provided to this listener
	 */
	protected int add(final URI url)
	{
		_myURIs.put(++ctr, url);

		return ctr;
	}

	/** ditch this listener
	 * 
	 * @param id
	 */
	protected void remove(final int id)
	{
		_myURIs.remove(id);
	}
	
	/** fire an event to the specified URI
	 * 
	 * @param client
	 * @param event
	 */
	abstract protected void fireThisEvent(ClientResource client, EventType event);


	/** fire the supplied event to my listeners
	 * 
	 * @param event
	 */
	protected void fireEvent(final EventType event)
	{
		Vector<URI> toDitch = null;

		for (final Iterator<URI> url = _myURIs.values().iterator(); url.hasNext();)
		{
			final URI thisURI = url.next();

			try
			{
				final ClientResource client = new ClientResource(thisURI.toString());
				fireThisEvent(client, event);
			}
			catch (final ResourceException re)
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
		}

		// ok, are we ditching any?
		if (toDitch != null)
		{
			// yup, work through them
			for (final Iterator<URI> iterator = toDitch.iterator(); iterator.hasNext();)
			{
				final URI thisURI = (URI) iterator.next();

				final Set<Integer> mine = _myURIs.keySet();
				for (final Iterator<Integer> iterator2 = mine.iterator(); iterator2
						.hasNext();)
				{
					final Integer thisId = (Integer) iterator2.next();
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