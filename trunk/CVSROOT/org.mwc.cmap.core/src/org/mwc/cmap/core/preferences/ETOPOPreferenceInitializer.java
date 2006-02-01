/**
 * 
 */
package org.mwc.cmap.core.preferences;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;

public class ETOPOPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{

		// hmm, set the default location
		Path coastPath = new Path("data/ETOPO2.raw");

		// and get the relative path compared to the Core Plugin
		URL fileURL = Platform.find(CorePlugin.getDefault().getBundle(), coastPath);

		if (fileURL != null)
		{
			// right, that's the relative location, switch to absolute path
			try
			{
				fileURL = Platform.asLocalURL(fileURL);
			}
			catch (IOException e)
			{
				CorePlugin.logError(Status.ERROR, "Unable to find ETOPO data-file", e);
			}

			// and store the default location
			IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
			store.setDefault(ETOPOPrefsPage.PreferenceConstants.ETOPO_FILE, fileURL.getFile());

		}
	}

}