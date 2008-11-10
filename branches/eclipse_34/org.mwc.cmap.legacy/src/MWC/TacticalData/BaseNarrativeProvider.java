/**
 * 
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
	public HashMap _myListeners = new HashMap();

	
	public void addNarrativeListener(String category, INarrativeListener listener)
	{
		// do we hold a list of this type?
		Vector theList = (Vector) _myListeners.get(category);
		
		// do we hold one?
		if(theList == null)
		{
			theList = new Vector(1,1);
			_myListeners.put(category, theList);
		}
		
		// and add it.
		theList.add(listener);
	}


	public void removeNarrativeListener(String category, INarrativeListener listener)
	{
		// do we hold a list of this type?
		Vector theList = (Vector) _myListeners.get(category);
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

	public NarrativeEntry[] getNarrativeHistory(String[] categories)
	{
		return null;
	}
	
	public void fireEntry(NarrativeEntry newEntry)
	{
		// right, first fire it the the "all" category
		Vector theListeners = (Vector) _myListeners.get(IRollingNarrativeProvider.ALL_CATS);
		
		// and fire it to them
		fireThisEntry(newEntry, theListeners);
		
		// find any type-specific listeners
		String theType = newEntry.getType();
		
		if(theType != null)
		{
			theListeners = (Vector) _myListeners.get(theType);
			fireThisEntry(newEntry, theListeners);
		}
	}
	
	/** ok, distribute the entry to this list of listeners
	 * 
	 * @param newEntry what we're firing off
	 * @param theListeners the people who're interested.
	 */
	private void fireThisEntry(NarrativeEntry newEntry, Vector theListeners)
	{
		// do we have any listeners?
		if(theListeners != null)
		{
			// ok, cycle through them
			for (Iterator iter = theListeners.iterator(); iter.hasNext();)
			{
				INarrativeListener element = (INarrativeListener) iter.next();
				// fire away...
				element.newEntry(newEntry);
			}
		}
	}
	
}