package org.mwc.debrief.dis.providers;

import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;

import edu.nps.moves.dis.EntityID;

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
   * @param filters
   *          any site,app,ex filters to apply to the messages
   * @param eid 
   *          our originating identity
   */
  void attach(DISFilters filters, EntityID eid);

  /**
   * stop providing data
   * 
   */
  void detach();
}