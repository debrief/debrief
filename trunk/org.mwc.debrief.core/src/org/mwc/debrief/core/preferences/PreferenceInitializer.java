/**
 * 
 */
package org.mwc.debrief.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;

public class PreferenceInitializer extends
		AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
		store.setDefault(PrefsPage.PreferenceConstants.AUTO_SELECT, true);
		store.setDefault(PrefsPage.PreferenceConstants.CALC_SLANT_RANGE, false);
		store.setDefault(PrefsPage.PreferenceConstants.SHOW_DRAG_IN_PROPS, true);
	}

}