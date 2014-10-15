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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.gndmanager.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.gndmanager.Tracks.TrackStoreWrapper;

public class CloudStoragePreferenceInitializer extends AbstractPreferenceInitializer
{

	public static final String DEFAULT_ES = "http://gnd:9200";
	public static final String DEFAULT_COUCH = "http://gnd:5984";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
			// and store the default location
			final IPreferenceStore store = DebriefPlugin.getDefault().getPreferenceStore();
			store.setDefault(TrackStoreWrapper.COUCHDB_LOCATION,DEFAULT_COUCH);
			store.setDefault(TrackStoreWrapper.ES_LOCATION,DEFAULT_ES);
		
	}

}