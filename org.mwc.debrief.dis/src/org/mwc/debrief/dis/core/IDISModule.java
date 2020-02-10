package org.mwc.debrief.dis.core;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;
import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFireListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.listeners.IDISStartResumeListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

public interface IDISModule {
	void addCollisionListener(IDISCollisionListener handler);

	// /////////////
	// LISTENERS
	// /////////////

	void addDetonationListener(IDISDetonationListener listener);

	void addEventListener(IDISEventListener handler);

	/**
	 * register an event listener who wants to know about a particular type of event
	 *
	 * @param handler
	 * @param eType   type of event (or null for all)
	 */
	void addEventListener(IDISEventListener handler, Integer eType);

	void addFireListener(IDISFireListener handler);

	void addFixListener(IDISFixListener listener);

	void addGeneralPDUListener(IDISGeneralPDUListener listener);

	void addScenarioListener(IDISScenarioListener handler);

	void addStartResumeListener(IDISStartResumeListener handler);

	void addStopListener(IDISStopListener idisStopListener);

	long convertTime(long timestamp);

	void setProvider(IPDUProvider provider);

}