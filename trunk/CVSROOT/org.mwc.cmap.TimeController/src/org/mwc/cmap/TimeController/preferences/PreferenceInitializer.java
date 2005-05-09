package org.mwc.cmap.TimeController.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.mwc.cmap.TimeController.TimeControllerPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = TimeControllerPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_STRING,"hh:mm:ss");
	}

}
