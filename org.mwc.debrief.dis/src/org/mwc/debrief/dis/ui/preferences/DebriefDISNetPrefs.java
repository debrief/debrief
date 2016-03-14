/**
 * 
 */
package org.mwc.debrief.dis.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;

/**
 * @author ian
 * 
 */
public class DebriefDISNetPrefs implements IDISNetworkPrefs
{

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.providers.network.IDISNetworkPrefs#getIPAddress()
   */
  @Override
  public String getIPAddress()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();

    // try for system property, use preference as fallback
    return System.getProperties().getProperty(DisActivator.IP_ADDRESS,
        store.getString(DisActivator.IP_ADDRESS));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.providers.network.IDISNetworkPrefs#getPort()
   */
  @Override
  public int getPort()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();

    // try for system property, use preference as fallback
    String res =
        System.getProperty(DisActivator.PORT, store
            .getString(DisActivator.PORT));
    return Integer.parseInt(res);
  }

}
