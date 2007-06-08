/**
 * 
 */
package org.mwc.cmap.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.preferences.CMAPPrefsPage.PreferenceConstants;

import Debrief.Tools.Tote.Calculations.relBearingCalc;

public class CMAPPreferenceInitializer extends
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
		store.setDefault(PreferenceConstants.REL_BEARING_FORMAT, relBearingCalc.UK_REL_BEARING_FORMAT);
		store.setDefault(PreferenceConstants.RNG_UNITS, MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS);
	}

}