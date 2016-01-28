package org.mwc.debrief.dis.core;

import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

public interface IDISModule
{
  void setProvider(IPDUProvider provider);

  IDISPreferences getPrefs();

  // /////////////
  // LISTENERS
  // /////////////

  void addFixListener(IDISFixListener listener);

  void addDetonationListener(IDISDetonationListener listener);

  void addGeneralPDUListener(IDISGeneralPDUListener listener);

  void addScenarioListener(IDISScenarioListener handler);

  void addEventListener(IDISEventListener handler);

}