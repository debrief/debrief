package org.mwc.debrief.dis.ui.preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.dis.ui.preferences.DebriefDISNetPrefs.*;
import org.mwc.debrief.dis.ui.preferences.DebriefDISNetPrefs.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;
import org.junit.jupiter.api.Test;

public class DebriefDISNetPrefsTest {
	@Test
	public void testArgs() {
		final DebriefDISNetPrefs prefs = new DebriefDISNetPrefs();
		assertNull(prefs.getArg(new String[] { "aa", "bb" }, "val"), "didn't find it");
		assertNotNull(prefs.getArg(new String[] { "aa", "-Dval=23" }, "val"), "found it");
		assertEquals("23", prefs.getArg(new String[] { "aa", "-Dval=23" }, "val"), "found it");
	}
}
