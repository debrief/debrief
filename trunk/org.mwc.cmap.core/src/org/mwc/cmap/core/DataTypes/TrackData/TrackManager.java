package org.mwc.cmap.core.DataTypes.TrackData;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.WatchableList;

/**
 * embedded class which manages the primary & secondary tracks
 */
public class TrackManager implements TrackDataProvider // ,
// TrackDataProvider.TrackDataListener
{

	WatchableList _thePrimary;

	WatchableList[] _theSecondaries;

	private Vector<TrackDataListener> _myDataListeners;

	Layers _theLayers;

	private Vector<TrackShiftListener> _myShiftListeners;

	/**
	 * set a limit for the maximum number of secondary tracks we will plot
	 */
	private static final int MAX_SECONDARIES = 10;

	/**
	 * and the message to display
	 */
	private static final String MAX_MESSAGE = "Too many tracks.  Only the first "
			+ MAX_SECONDARIES + " secondary tracks have been assigned";

	public TrackManager(Layers parentLayers)
	{
		_theLayers = parentLayers;
	}

	/**
	 * pass through the data, and assign any watchables as primary and secondary
	 * 
	 * @param onlyAssignTracks
	 *          only put TracksWrappers on the tote
	 */
	public void autoAssign(boolean onlyAssignTracks)
	{

		// check we have some data to search
		if (_theLayers != null)
		{

			// pass through the data to find the WatchableLists
			for (int l = 0; l < _theLayers.size(); l++)
			{
				final Layer layer = _theLayers.elementAt(l);

				if (layer instanceof WatchableList)
				{
					if (_theSecondaries != null)
					{
						// have we got our full set of secondarires yet?
						if (_theSecondaries.length >= MAX_SECONDARIES)
						{
							MWC.GUI.Dialogs.DialogFactory.showMessage(
									"Secondary limit reached", MAX_MESSAGE);
							return;
						}
					}

					processWatchableList((WatchableList) layer, onlyAssignTracks);
				} else
				{
					final Enumeration<Editable> iter = layer.elements();
					while (iter.hasMoreElements())
					{
						final Plottable p = (Plottable) iter.nextElement();
						if (p instanceof WatchableList)
						{

							if (_theSecondaries != null)
							{
								// have we got our full set of secondarires yet?
								if (_theSecondaries.length >= MAX_SECONDARIES)
								{
									MWC.GUI.Dialogs.DialogFactory.showMessage(
											"Secondary limit reached", MAX_MESSAGE);
									return;
								}
							}

							processWatchableList((WatchableList) p, onlyAssignTracks);
						}
					}
				}
			}
		}
	}

	/**
	 * @param list
	 *          the list of items to process
	 * @param onlyAssignTracks
	 *          whether only TrackWrapper items should be placed on the list
	 */
	private void processWatchableList(final WatchableList list,
			boolean onlyAssignTracks)
	{
		// check this isn't the primary
		if (list != getPrimaryTrack())
		{
			final WatchableList w = (WatchableList) list;
			// see if we need a primary setting
			if (getPrimaryTrack() == null)
			{
				if (w.getVisible())
					if ((!onlyAssignTracks) || (onlyAssignTracks)
							&& (w instanceof TrackWrapper))
						setPrimary(w);
			} else
			{

				boolean haveAlready = false;

				// check that this isn't one of our secondaries
				if (_theSecondaries != null)
				{
					// right, we've got some secondaries at least. is it one of them?
					for (int i = 0; i < _theSecondaries.length; i++)
					{
						WatchableList secW = _theSecondaries[i];
						if (secW == w)
						{
							// don't bother with it, we've got it already
							haveAlready = true;
							continue;
						}
					}
				}

				if (!haveAlready)
				{
					if (w.getVisible())
						if ((!onlyAssignTracks) || (onlyAssignTracks)
								&& (w instanceof TrackWrapper))
							addSecondary(w);
				}
			}

		}

	}

	public void assignTracks(String primaryTrack, Vector<String> secondaryTracks)
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
			Vector<Layer> secs = new Vector<Layer>(0, 1);
			Iterator<String> iter = secondaryTracks.iterator();
			while (iter.hasNext())
			{
				String thisS = (String) iter.next();
				Layer theS = _theLayers.findLayer(thisS);
				if (theS != null)
					if (theS instanceof WatchableList)
					{
						secs.add(theS);
					}
			}

			if (secs.size() > 0)
			{
				_theSecondaries = new WatchableList[]
				{};
				_theSecondaries = (WatchableList[]) secs.toArray(_theSecondaries);
			}
		}
	}

	public void addTrackDataListener(TrackDataListener listener)
	{
		if (_myDataListeners == null)
			_myDataListeners = new Vector<TrackDataListener>();

		// do we already contain this one?
		if (!_myDataListeners.contains(listener))
			_myDataListeners.add(listener);
	}

	public void addTrackShiftListener(TrackShiftListener listener)
	{
		if (_myShiftListeners == null)
			_myShiftListeners = new Vector<TrackShiftListener>();

		// do we already contain this one?
		if (!_myShiftListeners.contains(listener))
			_myShiftListeners.add(listener);
	}

	public WatchableList getPrimaryTrack()
	{
		return _thePrimary;
	}

	public WatchableList[] getSecondaryTracks()
	{
		return _theSecondaries;
	}

	public void setPrimary(WatchableList primary)
	{
		// ok - set it as the primary
		setPrimaryImpl(primary);

		// now remove it as a secondary
		removeSecondaryImpl(primary);

		fireTracksChanged();
	}

	private void setPrimaryImpl(WatchableList primary)
	{
		_thePrimary = primary;

		// and inform the listeners
		if (_myDataListeners != null)
		{
			Iterator<TrackDataListener> iter = _myDataListeners.iterator();
			while (iter.hasNext())
			{
				TrackDataListener list = (TrackDataListener) iter.next();
				list.tracksUpdated(_thePrimary, _theSecondaries);
			}
		}
	}

	public void secondariesUpdated(WatchableList[] secondaries)
	{
		_theSecondaries = secondaries;

		// and inform the listeners
		fireTracksChanged();
	}

	public void fireTracksChanged()
	{
		// bugger, just double-check that our layers are still in there...

		// do we have a primary?
		if (_thePrimary != null)
		{
			Layer found = _theLayers.findLayer(_thePrimary.getName());

			// did we find it?
			if (found == null)
			{
				// nope, better ditch the primary
				_thePrimary = null;
			}
		}

		// now the secondaries!!!
		if (_theSecondaries != null)
		{
			Vector<WatchableList> secsFound = new Vector<WatchableList>(0, 1);
			for (int i = 0; i < _theSecondaries.length; i++)
			{
				WatchableList thisSec = _theSecondaries[i];
				String thisName = thisSec.getName();
				// is it in our layers?
				if (_theLayers.findLayer(thisName) != null)
				{
					secsFound.add(thisSec);
				}
			}

			// and store the new secs list
			WatchableList[] demo = new WatchableList[]
			{};
			_theSecondaries = (WatchableList[]) secsFound.toArray(demo);
		}

		if (_myDataListeners != null)
		{
			Iterator<TrackDataListener> iter = _myDataListeners.iterator();
			while (iter.hasNext())
			{
				TrackDataListener list =  iter.next();
				list.tracksUpdated(_thePrimary, _theSecondaries);
			}
		}
	}


	public void setSecondary(WatchableList secondary)
	{
		// clear out any existing secondarires
		_theSecondaries = null;
 		
		// right, insert this as a secondary track
		addSecondaryImpl(secondary);

		// was it the primary?
		if (secondary == _thePrimary)
			setPrimaryImpl(null);

		fireTracksChanged();
	}
	
	public void addSecondary(WatchableList secondary)
	{
		// right, insert this as a secondary track
		addSecondaryImpl(secondary);

		// was it the primary?
		if (secondary == _thePrimary)
			setPrimaryImpl(null);

		fireTracksChanged();
	}

	private void addSecondaryImpl(WatchableList secondary)
	{
		// check we don't already hold it
	
		// store the new list
		Vector<WatchableList> newList = new Vector<WatchableList>(0, 1);

		// copy in the old list
		if (_theSecondaries != null)
		{
			for (int i = 0; i < _theSecondaries.length; i++)
			{
				newList.add(_theSecondaries[i]);
				
				if(_theSecondaries[i] == secondary)
				{
					// we've already got it - drop out.
					return;
				}
				
			}
		}

		// and add the new item
		newList.add(secondary);

		WatchableList[] demo = new WatchableList[]
		{};
		_theSecondaries = (WatchableList[]) newList.toArray(demo);
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
			ta.setName("ta");
			TrackWrapper tb = new TrackWrapper();
			tb.setName("tb");
			TrackWrapper tc = new TrackWrapper();
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
			Vector<String> secs = new Vector<String>(0, 1);
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
		if (_myDataListeners != null)
			_myDataListeners.remove(listener);
	}

	public void removeTrackShiftListener(TrackShiftListener listener)
	{
		if (_myShiftListeners != null)
			_myShiftListeners.remove(listener);
	}

	/**
	 * remove the indicated secondary track
	 * 
	 * @param thisSec
	 */
	public void removeSecondary(WatchableList thisSec)
	{
		removeSecondaryImpl(thisSec);

		// and now fire updates
		fireTracksChanged();
	}

	private void removeSecondaryImpl(WatchableList thisSec)
	{
		// store the new list
		Vector<WatchableList> newList = new Vector<WatchableList>(0, 1);

		// copy in the old list
		if (_theSecondaries != null)
		{
			for (int i = 0; i < _theSecondaries.length; i++)
			{
				WatchableList curSec = _theSecondaries[i];
				if (curSec == thisSec)
				{
					// hey, just ignore it
				} else
				{
					newList.add(_theSecondaries[i]);
				}
			}
		}

		if (newList.size() > 0)
		{
			WatchableList[] demo = new WatchableList[]
			{};
			_theSecondaries = (WatchableList[]) newList.toArray(demo);
		} else
			_theSecondaries = new WatchableList[0];
	}

	/**
	 * ok - tell anybody that wants to know that it's moved
	 * 
	 * @param target
	 */
	public void fireTrackShift(TrackWrapper target)
	{
		if (_myShiftListeners != null)
		{
			Iterator<TrackShiftListener> iter = _myShiftListeners.iterator();
			while (iter.hasNext())
			{
				TrackShiftListener list = iter.next();
				list.trackShifted(target);
			}
		}
	}
}