package org.mwc.debrief.core.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class OpenPrefs extends AbstractHandler
{
  protected void execute()
  {
    PreferenceDialog pref =
        PreferencesUtil.createPreferenceDialogOn(null,
            "org.mwc.debrief.core.preferences.PrefsPage", null, null);
    if (pref != null)
      pref.open();
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    execute();
    return null;
  }
}
