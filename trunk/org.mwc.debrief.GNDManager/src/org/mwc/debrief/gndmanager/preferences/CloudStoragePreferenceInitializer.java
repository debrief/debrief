/**
 * 
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
			IPreferenceStore store = DebriefPlugin.getDefault().getPreferenceStore();
			store.setDefault(TrackStoreWrapper.COUCHDB_LOCATION,DEFAULT_COUCH);
			store.setDefault(TrackStoreWrapper.ES_LOCATION,DEFAULT_ES);
		
	}

}