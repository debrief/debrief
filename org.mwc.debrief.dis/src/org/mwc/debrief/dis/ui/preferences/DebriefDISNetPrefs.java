/**
 *
 */
package org.mwc.debrief.dis.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;

import junit.framework.TestCase;

/**
 * @author ian
 *
 */
public class DebriefDISNetPrefs implements IDISNetworkPrefs {

	public static class TestMe extends TestCase {
		public void testArgs() {
			final DebriefDISNetPrefs prefs = new DebriefDISNetPrefs();
			assertNull("didn't find it", prefs.getArg(new String[] { "aa", "bb" }, "val"));
			assertNotNull("found it", prefs.getArg(new String[] { "aa", "-Dval=23" }, "val"));
			assertEquals("found it", "23", prefs.getArg(new String[] { "aa", "-Dval=23" }, "val"));
		}
	}

	private String getArg(final String[] args, final String name) {
		for (int i = 0; i < args.length; i++) {
			final String thisArg = args[i];
			if (thisArg.contains(name)) {
				final int startI = thisArg.indexOf(name) + name.length() + 1;
				final String endStr = thisArg.substring(startI).trim();
				return endStr;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mwc.debrief.dis.providers.network.IDISNetworkPrefs#getIPAddress()
	 */
	@Override
	public String getIPAddress() {
		return getThisArg(DisActivator.IP_ADDRESS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mwc.debrief.dis.providers.network.IDISNetworkPrefs#getPort()
	 */
	@Override
	public int getPort() {
		return Integer.parseInt(getThisArg(DisActivator.PORT));
	}

	private String getThisArg(final String name) {
		// do a command-line check, just in case it was supplied in the command line
		// args
		String address = getArg(Platform.getCommandLineArgs(), name);

		// was there one?
		if (address == null) {
			final IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();

			// try for system property, use preference as fallback
			address = System.getProperty(name, store.getString(name));
		}
		return address;
	}

}
