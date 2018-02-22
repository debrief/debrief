/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.gridharness;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;

import Debrief.Wrappers.SensorContactWrapper;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final
		IPreferenceStore preferences = CorePlugin.getDefault().getPreferenceStore();
		preferences.setDefault(CorePlugin.PREF_BASE60_FORMAT_NO_SECONDS, false);
    preferences.setDefault(CorePlugin.DEFAULT_FONT, "Arial-regular-12");
    preferences.setDefault(SensorContactWrapper.TRANSPARENCY, 100);
	}
}
