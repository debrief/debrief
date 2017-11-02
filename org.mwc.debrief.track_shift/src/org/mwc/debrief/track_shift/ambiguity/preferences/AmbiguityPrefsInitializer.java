package org.mwc.debrief.track_shift.ambiguity.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.track_shift.TrackShiftActivator;

/**
 * Class used to initialize default preference values.
 */
public class AmbiguityPrefsInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = TrackShiftActivator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.MIN_ZIG, 0.5d);
    store.setDefault(PreferenceConstants.MIN_TURN_RATE, 0.2d);
    store.setDefault(PreferenceConstants.DIAGNOSTICS, false);
    store.setDefault(PreferenceConstants.DISPLAY, false);
    store.setDefault(PreferenceConstants.MIN_LEG_LENGTH, 240d);
    store.setDefault(PreferenceConstants.OS_TURN_MIN_COURSE_CHANGE, 12d);
    store.setDefault(PreferenceConstants.OS_TURN_MIN_TIME_INTERVAL, 180L);    
    store.setDefault(PreferenceConstants.OS_TURN_MAX_LEGS, 8);    
	}

}
