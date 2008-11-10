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
import org.osgi.framework.Bundle;

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
		Path etopoPath = new Path("data/ETOPO2.raw");

		URL fileURL = null;
		Bundle staticBundle = Platform.getBundle("org.mwc.cmap.static_resources");
		if (staticBundle != null)
		{
			// and get the relative path compared to the Core Plugin
			fileURL = FileLocator.find(staticBundle, etopoPath, null);
		}

		if (fileURL != null)
		{
			// right, that's the relative location, switch to absolute path
			try
			{
				fileURL = FileLocator.toFileURL(fileURL);
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