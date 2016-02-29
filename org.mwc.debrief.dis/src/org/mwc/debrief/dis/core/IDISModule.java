package org.mwc.debrief.dis.core;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;
import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFireListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

public interface IDISModule
{
  void setProvider(IPDUProvider provider);

  // /////////////
  // LISTENERS
  // /////////////

  void addFixListener(IDISFixListener listener);

  void addDetonationListener(IDISDetonationListener listener);

  void addGeneralPDUListener(IDISGeneralPDUListener listener);

  void addScenarioListener(IDISScenarioListener handler);

  void addEventListener(IDISEventListener handler);

  /** register an event listener who wants to know about
   * a particular type of event
   * @param handler
   * @param eType type of event (or null for all)
   */
  void addEventListener(IDISEventListener handler, Integer eType);

  void addStopListener(IDISStopListener idisStopListener);

  void addFireListener(IDISFireListener handler);

  void addCollisionListener(IDISCollisionListener handler);

  long convertTime(long timestamp);

}