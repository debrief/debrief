/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.preferences.CMAPPrefsPage.PreferenceConstants;

import Debrief.ReaderWriter.Replay.ImportReplay;
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
		final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.REL_BEARING_FORMAT, relBearingCalc.UK_REL_BEARING_FORMAT);
		store.setDefault(PreferenceConstants.RNG_UNITS, MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS);
		store.setDefault(PreferenceConstants.IMPORT_MODE, ImportReplay.ASK_THE_AUDIENCE);
	}

}