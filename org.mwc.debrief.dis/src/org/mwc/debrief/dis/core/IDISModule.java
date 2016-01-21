package org.mwc.debrief.dis.core;

import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

public interface IDISModule
{
	void setProvider(IPDUProvider provider);

	IDISPreferences getPrefs();

	void setPrefs(IDISPreferences preferences);

	// /////////////
	// LISTENERS
	// /////////////

	void addFixListener(IDISFixListener listener);

	void addGeneralPDUListener(IDISGeneralPDUListener listener);

	void addScenarioListener(IDISScenarioListener handler);

}