/**
 * 
 */
package org.mwc.debrief.dis.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.providers.network.IDISSimulatorPrefs;

/**
 * @author ian
 * 
 */
public class DebriefDISSimulatorPrefs implements IDISSimulatorPrefs
{

  @Override
  public String getExePath()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    return store.getString(DisActivator.PATH_TO_SIMULATION_EXECUTABLE);
  }

  @Override
  public String getInputFile()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    return store.getString(DisActivator.PATH_TO_INPUT_FILE);
  }

}
