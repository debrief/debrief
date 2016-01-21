package org.mwc.debrief.dis.providers;

import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;

public interface IPDUProvider
{

	/**
	 * register as a listener for new data
	 * 
	 * @param listener
	 */
	void addListener(IDISGeneralPDUListener listener);

	/**
	 * start providing data
	 * 
	 */
	void start();

	/**
	 * stop providing data
	 * 
	 */
	void disconnect();
}