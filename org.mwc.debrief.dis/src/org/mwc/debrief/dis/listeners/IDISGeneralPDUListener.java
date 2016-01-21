package org.mwc.debrief.dis.listeners;

import edu.nps.moves.dis.Pdu;

/**
 * listener that's interested in all PDU activity, and gets raw PDU data
 * 
 */
public interface IDISGeneralPDUListener
{
	/**
	 * here is some data
	 * 
	 * @param pdu
	 */
	public void logPDU(Pdu pdu);

	/**
	 * now more data is going to arrive
	 * 
	 */
	public void complete(String reason);
}