/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.DataTypes.TrackData;

import MWC.GenericData.WatchableList;

/**
 * @author ian.mayo
 */
public interface TrackDataProvider
{
	public static interface TrackDataListener
	{
		/**
		 * find out that the primary has changed
		 * 
		 * @param primary         the primary track
		 */
		public void tracksUpdated(WatchableList primary, WatchableList[] secondaries);
	}
	
	/** people who want to find out about tracks moving
	 * 
	 * @author ian.mayo
	 *
	 */
	public static interface TrackShiftListener
	{
		/** 
		 * 
		 */
		public void trackShifted(WatchableList target);
	}

	/**
	 * declare that we want to be informed about changes in selected tracks
	 */
	public void addTrackDataListener(TrackDataListener listener);

	/**
	 * forget that somebody wants to know about track changes
	 */
	public void removeTrackDataListener(TrackDataListener listener);
	
	/**
	 * declare that we want to be informed about changes in selected tracks
	 */
	public void addTrackShiftListener(TrackShiftListener listener);

	/**
	 * forget that somebody wants to know about track changes
	 */
	public void removeTrackShiftListener(TrackShiftListener listener);
	
	/** ok - tell anybody that wants to know about our movement
	 * 
	 * @param watchableList what's being dragged
	 */
	public void fireTrackShift(final WatchableList watchableList);
	
	/** ok, the tracks have changed. tell the world
	 * 
	 */
	public void fireTracksChanged();
	

	/**
	 * find out what the primary track is
	 */
	public WatchableList getPrimaryTrack();

	/**
	 * find out what the secondary track is
	 */
	public WatchableList[] getSecondaryTracks();

}
