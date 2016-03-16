package org.mwc.debrief.dis.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.dis.DisActivator;

public class DisPrefsInitialiser extends AbstractPreferenceInitializer
{

  public DisPrefsInitialiser()
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void initializeDefaultPreferences()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();

    store.setDefault(DisActivator.SITE_ID, 777);
    store.setDefault(DisActivator.APP_ID, 778);
    store.setDefault(DisActivator.IP_ADDRESS, "239.1.2.3");
    store.setDefault(DisActivator.PORT, 62040);
    store.setDefault(DisActivator.FIT_TO_DATA, true);
  }

}
