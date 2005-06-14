package org.mwc.cmap.core.DataTypes.TrackData;

import java.util.*;

import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;


import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.TacticalData.Track;

/**
 * embedded class which manages the primary & secondary tracks
 */
public class TrackManager implements TrackDataProvider,
		TrackDataProvider.TrackDataListener
{

	private WatchableList _thePrimary;

	private WatchableList[] _theSecondaries;

	private Vector _myListeners;

	private Layers _theLayers;

	public TrackManager(Layers parentLayers)
	{
		_theLayers = parentLayers;
	}

	public void assignTracks(String primaryTrack, Vector secondaryTracks)
	{
		// ok - find the matching tracks/
		Object theP = _theLayers.findLayer(primaryTrack);
		if (theP != null)
		{
			if (theP instanceof WatchableList)
			{
				_thePrimary = (WatchableList) theP;
			}
		}

		// do we have secondaries?
		if (secondaryTracks != null)
		{
			// and now the secs
			Vector secs = new Vector(0, 1);
			Iterator iter = secondaryTracks.iterator();
			while (iter.hasNext())
			{
				String thisS = (String) iter.next();
				Object theS = _theLayers.findLayer(thisS);
				if (theS != null)
					if (theS instanceof WatchableList)
					{
						secs.add(theS);
					}
			}

			if (secs.size() > 0)
			{
				_theSecondaries = new WatchableList[] { null };
				_theSecondaries = (WatchableList[]) secs.toArray(_theSecondaries);
			}
		}
	}

	public void addTrackDataListener(TrackDataListener listener)
	{
		if (_myListeners == null)
			_myListeners = new Vector();

		_myListeners.add(listener);
	}

	public WatchableList getPrimaryTrack()
	{
		return _thePrimary;
	}

	public WatchableList[] getSecondaryTracks()
	{
		return _theSecondaries;
	}

	public void primaryUpdated(WatchableList primary)
	{
		_thePrimary = primary;
		
		// and inform the listeners
		if(_myListeners != null)
		{
			Iterator iter = _myListeners.iterator();
			while(iter.hasNext())
			{
				TrackDataListener list = (TrackDataListener) iter.next();
				list.primaryUpdated(primary);
			}
		}
	}

	public void secondariesUpdated(WatchableList[] secondaries)
	{
		_theSecondaries = secondaries;
		
		// and inform the listeners
		fireSecondariesChanged();
	}


	private void fireSecondariesChanged()
	{
		if(_myListeners != null)
		{
			Iterator iter = _myListeners.iterator();
			while(iter.hasNext())
			{
				TrackDataListener list = (TrackDataListener) iter.next();
				list.secondariesUpdated(_theSecondaries);
			}
		}
	}
	
	public void addSecondary(WatchableList secondary)
	{
		// store the new list
		Vector newList = new Vector(1,1);
		
		// copy in the old list
		if(_theSecondaries != null)
		{
			for(int i=0;i<_theSecondaries.length;i++)
			{
				newList.add(_theSecondaries[i]);
			}
		}
		
		// and add the new item
		newList.add(secondary);
		
		WatchableList[] demo = new WatchableList[]{null};
		_theSecondaries = (WatchableList[]) newList.toArray(demo);
		
		// and inform the listeners
		fireSecondariesChanged();
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testTrackManager extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testTrackManager(final String val)
		{
			super(val);
		}

		public void testLists()
		{
			TrackWrapper ta = new TrackWrapper();
			ta.setTrack(new Track());
			ta.setName("ta");
			TrackWrapper tb = new TrackWrapper();
			tb.setTrack(new Track());
			tb.setName("tb");
			TrackWrapper tc = new TrackWrapper();
			tc.setTrack(new Track());
			tc.setName("tc");
			Layers theLayers = new Layers();
			theLayers.addThisLayer(ta);
			theLayers.addThisLayer(tb);
			theLayers.addThisLayer(tc);

			String pri_a = "ta";
			String pri_b = "tz";
			String sec_b = "tb";
			String sec_c = "tc";
			String sec_d = "tz";
			Vector secs = new Vector(0, 1);
			secs.add(sec_b);
			secs.add(sec_c);

			// create the mgr
			TrackManager tm = new TrackManager(theLayers);

			// do some checks
			assertNull("pri empty", tm._thePrimary);
			assertNull("secs empty", tm._theSecondaries);
			assertNotNull("layers assigned", tm._theLayers);

			// now get going
			tm.assignTracks(pri_a, secs);

			// and do the tests
			assertNotNull("pri assigned", tm._thePrimary);
			assertEquals("pri matches", tm._thePrimary, ta);

			// and the secs
			assertNotNull("sec assigned", tm._theSecondaries);
			assertEquals("correct num", 2, tm._theSecondaries.length);

			// setup duff data
			secs.clear();
			secs.add(sec_b);
			secs.add(sec_d);

			// assign duff data
			tm.assignTracks(pri_b, secs);

			// and test duff data
			assertNotNull("pri still assigned", tm._thePrimary);
			assertEquals("pri matches", tm._thePrimary, ta);
			assertNotNull("sec assigned", tm._theSecondaries);
			assertEquals("correct num", 1, tm._theSecondaries.length);

			// assign more real data
			tm.assignTracks(sec_c, secs);

			// and test duff data
			assertNotNull("pri still assigned", tm._thePrimary);
			assertEquals("pri matches", tm._thePrimary, tc);
			assertNotNull("sec assigned", tm._theSecondaries);
			assertEquals("correct num", 1, tm._theSecondaries.length);
		}
	}

	public void removeTrackDataListener(TrackDataListener listener)
	{
		_myListeners.remove(listener);
	}
}