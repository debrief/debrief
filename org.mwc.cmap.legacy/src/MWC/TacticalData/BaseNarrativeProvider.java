/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.TacticalData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class BaseNarrativeProvider implements IRollingNarrativeProvider
{
	/** our list of listeners
	 * 
	 */
	public HashMap<String, Vector<INarrativeListener>> _myListeners = new HashMap<String, Vector<INarrativeListener>>();

	
	public void addNarrativeListener(final String category, final INarrativeListener listener)
	{
		// do we hold a list of this type?
		Vector<INarrativeListener> theList =  _myListeners.get(category);
		
		// do we hold one?
		if(theList == null)
		{
			theList = new Vector<INarrativeListener>(1,1);
			_myListeners.put(category, theList);
		}
		
		// and add it.
		theList.add(listener);
	}


	public void removeNarrativeListener(final String category, final INarrativeListener listener)
	{
		// do we hold a list of this type?
		final Vector<INarrativeListener> theList = _myListeners.get(category);
//		
//		if(theList == null)
//		{
//			NarrativePlugin.logError(Status.ERROR, "category of listeners not found for:" + category, null);
//		}
//
//		// and remove it.
//		if(!theList.contains(listener))
//		{
//			NarrativePlugin.logError(Status.ERROR, "this listener wasn't even in the list:" + listener, null);
//		}
//		
		// ok, ditch it
		theList.remove(listener);
	}

	public NarrativeEntry[] getNarrativeHistory(final String[] categories)
	{
		return null;
	}
	
	public void fireEntry(final NarrativeEntry newEntry)
	{
		// right, first fire it the the "all" category
		Vector<INarrativeListener> theListeners =  _myListeners.get(IRollingNarrativeProvider.ALL_CATS);
		
		// and fire it to them
		fireThisEntry(newEntry, theListeners);
		
		// find any type-specific listeners
		final String theType = newEntry.getType();
		
		if(theType != null)
		{
			theListeners =  _myListeners.get(theType);
			fireThisEntry(newEntry, theListeners);
		}
	}
	
	/** ok, distribute the entry to this list of listeners
	 * 
	 * @param newEntry what we're firing off
	 * @param theListeners the people who're interested.
	 */
	private void fireThisEntry(final NarrativeEntry newEntry, final Vector<INarrativeListener> theListeners)
	{
		// do we have any listeners?
		if(theListeners != null)
		{
			// ok, cycle through them
			for (final Iterator<INarrativeListener> iter = theListeners.iterator(); iter.hasNext();)
			{
				final INarrativeListener element = (INarrativeListener) iter.next();
				// fire away...
				element.newEntry(newEntry);
			}
		}
	}
	
}