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
	/** find out what the current primary track is
	 * 
	 * @return primary track
	 */
	public WatchableList getPrimary();
	
	/** find out what the current set of secondary tracks are
	 * 
	 * @return list of secondaries
	 */
	public WatchableList[] getSecondaries();
}
