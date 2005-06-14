/**
 * 
 */
package org.mwc.cmap.core.DataTypes.TrackData;

import Debrief.Tools.Tote.WatchableList;

/**
 * @author ian.mayo
 *
 */
public interface TrackDataProvider
{
	public static interface TrackDataListener
	{
		/** find out that the primary has changed
		 * 
		 * @param primary the primary track
		 */
		public void primaryUpdated(WatchableList primary);
		
		/** find out that the secondaries have changed
		 * 
		 * @param secondaries list of secondary tracks
		 */
		public void secondariesUpdated(WatchableList[] secondaries);
	}

	/** declare that we want to be informed about changes
	 * in selected tracks
	 */
	public void addTrackDataListener(TrackDataListener listener);
	
	/** forget that somebody wants to know about track changes
	 * 
	 */
	public void removeTrackDataListener(TrackDataListener listener);

	/** find out what the primary track is
	 * 
	 */
	public WatchableList getPrimaryTrack();
	
	/** find out what the secondary track is
	 * 
	 */
	public WatchableList[] getSecondaryTracks();
	
}
