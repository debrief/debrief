/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.core.preferences;

import java.awt.Color;
import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.datalocation.Location;
import org.mwc.cmap.core.CorePlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private String getDefaultTemplateLocation() {
		final Location installLocation = Platform.getInstallLocation();
		final String path = installLocation.getURL().getFile()
				+ "root_installs/sample_data/other_formats/master_template.pptx";
		final File f = new File(path);
		if (f.exists()) {
			return f.getAbsolutePath();
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences ()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
		store.setDefault(PrefsPage.PreferenceConstants.AUTO_SELECT, true);
		store.setDefault(PrefsPage.PreferenceConstants.CALC_SLANT_RANGE, false);
		store.setDefault(PrefsPage.PreferenceConstants.DONT_SHOW_DRAG_IN_PROPS, true);
		store.setDefault(PrefsPage.PreferenceConstants.USE_IMPORT_SENSOR_WIZARD, true);
		store.setDefault(PrefsPage.PreferenceConstants.ASK_ABOUT_PROJECT, true);
		store.setDefault(PrefsPage.PreferenceConstants.DEFAULT_PLOT_COLOR, Color.white.getRGB());
		store.setDefault(PrefsPage.PreferenceConstants.REUSE_TRIM_NARRATIVES_DIALOG_CHOICE, false);
		store.setDefault(PrefsPage.PreferenceConstants.PPT_TEMPLATE, getDefaultTemplateLocation());
		store.setDefault(PrefsPage.PreferenceConstants.PEPYS_USE_STORED_FUNCTIONS, true);
	}
}