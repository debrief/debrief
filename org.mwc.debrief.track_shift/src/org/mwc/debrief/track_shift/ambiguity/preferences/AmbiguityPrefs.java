/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2016, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.track_shift.ambiguity.preferences;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.TrackShiftActivator;

public class AmbiguityPrefs extends PreferencePage implements
    IWorkbenchPreferencePage
{

  public static final String ID =
      "org.mwc.debrief.track_shift.ambiguity.preferences.AmbiguityPrefsInitializer";
  private Text cutOffText;

  public AmbiguityPrefs()
  {
    super("Ambiguity Resolution");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench)
  {
    // empty body.
  }

  @Override
  protected Control createContents(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    composite.setLayoutData(gd);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);

    Label blank1 = new Label(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.LEFT, false, false);
    blank1.setLayoutData(gd);
    blank1.setText(" ");

    Label intro = new Label(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    intro.setLayoutData(gd);
    intro.setText("Where the rate of change of the difference between \nPort and Starboard bearings is more than the value below,\nthe platform will be treated as zigging");

    Label blank2 = new Label(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.LEFT, false, false);
    blank2.setLayoutData(gd);
    blank2.setText(" ");

    
    Label dataFolderLabel = new Label(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.LEFT, false, false);
    dataFolderLabel.setLayoutData(gd);
    dataFolderLabel.setText("Cut-off:");

    cutOffText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    cutOffText.setLayoutData(gd);
    IPreferenceStore store =
        TrackShiftActivator.getDefault().getPreferenceStore();
    String dataFolderValue = store.getString(PreferenceConstants.CUT_OFF);
    cutOffText.setText(dataFolderValue == null ? "0.2" : dataFolderValue);
    
    Label unitsLabel = new Label(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.RIGHT, false, false);
    unitsLabel.setLayoutData(gd);
    unitsLabel.setText("Degs/sec");

    Label scrapLabel = new Label(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.RIGHT, false, false);
    scrapLabel.setLayoutData(gd);
    scrapLabel.setText("  ");

    
    return composite;
  }

  @Override
  protected void performDefaults()
  {
    cutOffText.setText(""); //$NON-NLS-1$
    storePreferences();
    super.performDefaults();
  }

  @Override
  public boolean performOk()
  {
    storePreferences();
    return super.performOk();
  }

  private void storePreferences()
  {
    IPreferenceStore store =
        TrackShiftActivator.getDefault().getPreferenceStore();
    String value = cutOffText.getText();
    if (value != null)
    {
      try{
      double val = Double.parseDouble(value);
      store.setValue(PreferenceConstants.CUT_OFF, val);
      }
      catch(NumberFormatException ne)
      {
        CorePlugin.logError(Status.ERROR, "Failed to parse new cut-off value", ne);
      }
    }
  }
}