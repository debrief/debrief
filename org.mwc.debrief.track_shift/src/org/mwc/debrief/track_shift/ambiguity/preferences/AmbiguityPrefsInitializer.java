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
    store.setDefault(PreferenceConstants.MAX_STEADY, 0.2d);
    store.setDefault(PreferenceConstants.DIAGNOSTICS, false);
    store.setDefault(PreferenceConstants.DISPLAY, false);
	}

}
