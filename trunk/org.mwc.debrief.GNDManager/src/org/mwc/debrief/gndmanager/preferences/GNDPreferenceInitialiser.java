package org.mwc.debrief.gndmanager.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.gndmanager.Activator;
import org.mwc.debrief.gndmanager.preferences.GNDPrefsPage.PreferenceConstants;


public class GNDPreferenceInitialiser extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.DB_URL, "http://127.0.0.1:5984/tracks");
		store.setDefault(PreferenceConstants.INDEX_URL, "http://127.0.0.1:9200/gnd");
	}

}