/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.lite.util;

import java.util.Enumeration;

import org.mwc.debrief.lite.DebriefLiteApp;

import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

/**
 * What the loaded data means for Lite's time controls and dirty flag. Worked
 * out from the layers whenever they change; kept free of any UI so that it can
 * be tested headless.
 */
public final class PlotState {

	/**
	 * look through the layers for tracks, narratives and time-bounded data
	 */
	public static PlotState of(final Layers layers) {
		boolean hasTracks = false;
		boolean hasNarratives = false;
		boolean hasStart = false;
		boolean hasEnd = false;

		final Enumeration<Editable> lIter = layers.elements();
		while (lIter.hasMoreElements()) {
			final Editable next = lIter.nextElement();
			if (next instanceof TrackWrapper) {
				hasTracks = true;
				break;
			} else if (next instanceof BaseLayer) {
				// check the children, to see if they're like a track
				final BaseLayer baseL = (BaseLayer) next;
				final Enumeration<Editable> ele = baseL.elements();
				while (ele.hasMoreElements() && !hasTracks) {
					final Editable nextE = ele.nextElement();
					hasTracks |= nextE instanceof LightweightTrackWrapper
							|| nextE instanceof DynamicTrackShapeSetWrapper;
					if (!hasTracks && nextE instanceof WatchableList) {
						hasStart |= ((WatchableList) nextE).getStartDTG() != null;
						hasEnd |= ((WatchableList) nextE).getEndDTG() != null;
					}
				}
			} else if (next instanceof NarrativeWrapper) {
				// check if we have any narrative item inside.
				final NarrativeWrapper narrativeWrapper = (NarrativeWrapper) next;
				final Enumeration<Editable> elements = narrativeWrapper.elements();
				while (elements.hasMoreElements() && !hasNarratives) {
					final Editable nextE = elements.nextElement();
					hasNarratives |= nextE instanceof NarrativeEntry;
				}
			} else if (next instanceof WatchableList) {
				// look at the date
				final WatchableList wl = (WatchableList) next;
				hasStart |= wl.getStartDTG() != null;
				hasEnd |= wl.getEndDTG() != null;
			}
		}
		return new PlotState(hasTracks, hasNarratives, hasStart && hasEnd);
	}

	private final boolean _hasTracks;
	private final boolean _hasNarratives;
	private final boolean _hasTimePeriod;

	private PlotState(final boolean hasTracks, final boolean hasNarratives, final boolean hasTimePeriod) {
		_hasTracks = hasTracks;
		_hasNarratives = hasNarratives;
		_hasTimePeriod = hasTimePeriod;
	}

	/**
	 * @return one of {@link DebriefLiteApp#ACTIVE_STATE} (tracks loaded: full time
	 *         controls), {@link DebriefLiteApp#TIME_ENABLED_STATE} (narratives, or
	 *         other data with a start and end time) or
	 *         {@link DebriefLiteApp#INACTIVE_STATE}
	 */
	public String getState() {
		if (_hasTracks) {
			return DebriefLiteApp.ACTIVE_STATE;
		} else if (_hasNarratives || _hasTimePeriod) {
			return DebriefLiteApp.TIME_ENABLED_STATE;
		}
		return DebriefLiteApp.INACTIVE_STATE;
	}

	/**
	 * Lite treats a plot holding tracks or narratives as having data worth saving,
	 * so it asks before discarding it
	 */
	public boolean isDirty() {
		return _hasTracks || _hasNarratives;
	}
}
