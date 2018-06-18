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
package org.mwc.debrief.core.ContextOperations.ExportCSVPrefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.ExportCSVPreferencesPage.PreferenceConstants;

public class ExportCSVPreferenceInitializer extends
    AbstractPreferenceInitializer
{

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
   * initializeDefaultPreferences()
   */
  public void initializeDefaultPreferences()
  {
    // and store the default location
    final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.INCLUDE_COMMAND, false);
    store.setDefault(PreferenceConstants.PATH_TO_CSV, "");
  }

}