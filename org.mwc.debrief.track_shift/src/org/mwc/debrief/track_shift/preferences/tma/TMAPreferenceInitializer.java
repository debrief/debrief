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
package org.mwc.debrief.track_shift.preferences.tma;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.track_shift.TrackShiftActivator;

import Debrief.Wrappers.Track.DynamicInfillSegment;

/**
 * Class used to initialize default preference values.
 */
public class TMAPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = TrackShiftActivator.getDefault().getPreferenceStore();
		store.setDefault(TMAPreferencePage.PreferenceConstants.INFILL_COLOR_STRATEGY,
				DynamicInfillSegment.RANDOM_INFILL);
		store.setDefault(TMAPreferencePage.PreferenceConstants.MERGED_INFILL_COLOR, "255, 150, 0");
		store.setDefault(TMAPreferencePage.PreferenceConstants.MERGED_TRACK_COLOR, "255, 77, 255");
		store.setDefault(TMAPreferencePage.PreferenceConstants.USE_CUT_COLOR, true);
		store.setDefault(TMAPreferencePage.PreferenceConstants.CUT_OFF_VALUE_DEGS, 3);
		store.setDefault(TMAPreferencePage.PreferenceConstants.CUT_OFF_VALUE_HZ, 0.2);
		store.setDefault(TMAPreferencePage.PreferenceConstants.SPEED_OF_SOUND_KTS, "2951");
		store.setDefault(TMAPreferencePage.PreferenceConstants.HOLISTIC_LEG_SLICER, true);
	}

}
