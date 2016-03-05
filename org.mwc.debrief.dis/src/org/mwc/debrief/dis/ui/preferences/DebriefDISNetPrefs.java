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
  
  
  /* (non-Javadoc)
   * @see org.mwc.debrief.dis.providers.network.IDISNetworkPrefs#getIPAddress()
   */
  @Override
  public String getIPAddress()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    return store.getString(DisActivator.IP_ADDRESS);
  }

  /* (non-Javadoc)
   * @see org.mwc.debrief.dis.providers.network.IDISNetworkPrefs#getPort()
   */
  @Override
  public int getPort()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    return store.getInt(DisActivator.PORT);
  }

}
