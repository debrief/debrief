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
package org.mwc.debrief.dis.ui.preferences;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.diagnostics.CustomEspduSender;

public class DisPrefs extends PreferencePage implements
    IWorkbenchPreferencePage
{
  public static final String ID = "org.mwc.debrief.dis.preferences.DisPrefs";
  private Text simulationPathText;
  private Text ipAddressText;
  private Text portText;
  private static final Pattern IP_ADDRESS_PATTERN =
      Pattern
          .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

  public DisPrefs()
  {
    super("DIS Preferences");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench)
  {
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

    createLabel(composite, "Path to executable:");
    simulationPathText =
        createText(composite, DisActivator.PATH_TO_SIMULATION_EXECUTABLE);

    final Button simulationPathBrowse = new Button(composite, SWT.PUSH);
    simulationPathBrowse.setText("Browse...");
    simulationPathBrowse.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
        String value = simulationPathText.getText();
        if (value.trim().length() == 0)
        {
          value = Platform.getLocation().toOSString();
        }
        dialog.setFilterPath(value);

        String result = dialog.open();
        if (result == null || result.trim().length() == 0)
        {
          return;
        }
        simulationPathText.setText(result);

      }

    });

    Composite discoverComposite = new Composite(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    discoverComposite.setLayoutData(gd);
    layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    discoverComposite.setLayout(layout);

    createLabel(discoverComposite, "IP Address");
    createLabel(discoverComposite, "Port");
    createLabel(discoverComposite, "");

    ipAddressText = createText(discoverComposite, DisActivator.IP_ADDRESS);
    portText = createText(discoverComposite, DisActivator.PORT);
    final Button discoverButton = new Button(discoverComposite, SWT.PUSH);
    discoverButton.setText("Discover");
    discoverButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // FIXME discover
      }

    });

    validate();
    return composite;
  }

  private void validate()
  {
    // clear the message, to start with
    setErrorMessage(null);
    
    String path = simulationPathText.getText();
    if (path != null && !path.isEmpty())
    {
      File file = new File(path);
      if (!file.exists())
      {
        setErrorMessage("File '" + file.getAbsolutePath() + "' doesn't exist");
        return;
      }
    }
    String portString = portText.getText();
    if (portString != null && !portString.isEmpty())
    {
      int port = 0;
      try
      {
        port = new Integer(portString);
      }
      catch (NumberFormatException e)
      {
        // ignore; port is 0 (invalid)
      }
      if (port < 1 || port > 65535)
      {
        setErrorMessage("Invalid port");
      }
    }
    String ipAddress = ipAddressText.getText();
    if (ipAddress != null && !ipAddress.isEmpty())
    {
      if (!IP_ADDRESS_PATTERN.matcher(ipAddress).matches())
      {
        setErrorMessage("Invalid IP Address");
      }
    }
  }

  private Text createText(Composite composite, String prefs)
  {
    GridData gd;
    Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    text.setLayoutData(gd);
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    String value = store.getString(prefs);
    text.setText(value == null ? "" : value);
    text.addModifyListener(new ModifyListener()
    {

      @Override
      public void modifyText(ModifyEvent e)
      {
        validate();
      }
    });
    return text;
  }

  private void createLabel(Composite composite, String text)
  {
    Label label = new Label(composite, SWT.NONE);
    GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
    label.setLayoutData(gd);
    label.setText(text);
  }

  @Override
  protected void performDefaults()
  {
    simulationPathText.setText(CustomEspduSender.DEFAULT_MULTICAST_GROUP); //$NON-NLS-1$
    ipAddressText.setText("" + CustomEspduSender.PORT);
    portText.setText("");
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
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    String value = simulationPathText.getText();
    store.setValue(DisActivator.PATH_TO_SIMULATION_EXECUTABLE, value);
    store.setValue(DisActivator.IP_ADDRESS, ipAddressText.getText());
    store.setValue(DisActivator.PORT, portText.getText());
  }

}