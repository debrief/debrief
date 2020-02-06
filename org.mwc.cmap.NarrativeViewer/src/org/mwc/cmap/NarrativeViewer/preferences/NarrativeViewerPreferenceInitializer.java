
package org.mwc.cmap.NarrativeViewer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;

public class NarrativeViewerPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
	  final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
    store.setDefault(NarrativeViewerPrefsPage.PreferenceConstants.HIGHLIGHT_PHRASES,
        "important,urgent,track");
	}

}