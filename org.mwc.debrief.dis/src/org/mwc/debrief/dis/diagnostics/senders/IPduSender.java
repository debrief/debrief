package org.mwc.debrief.dis.diagnostics.senders;

import edu.nps.moves.dis.Pdu;

/** interface for classes that are able to send out our data messages
 * 
 * @author ian
 *
 */
public interface IPduSender
{
  /** pass on this pdu
   * 
   * @param pdu
   */
  void sendPdu(final Pdu pdu);
  
  /** the simulation is complete, do any necessary tidying
   * 
   */
  void close();
}