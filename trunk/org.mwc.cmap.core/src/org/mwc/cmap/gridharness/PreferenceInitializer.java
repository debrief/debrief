package org.mwc.cmap.gridharness;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.mwc.cmap.core.CorePlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = new DefaultScope().getNode(Activator.PLUGIN_ID);
		preferences.putBoolean(CorePlugin.PREF_BASE60_FORMAT_NO_SECONDS, false);
	}
}
