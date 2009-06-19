package org.mwc.cmap.gridharness;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = new DefaultScope().getNode(Activator.PLUGIN_ID);
		preferences.putBoolean(Activator.PREF_BASE60_FORMAT_NO_SECONDS, true);
	}
}
