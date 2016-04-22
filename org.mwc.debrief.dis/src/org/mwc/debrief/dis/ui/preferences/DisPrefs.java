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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.diagnostics.senders.NetworkPduSender;
import org.mwc.debrief.dis.ui.views.DisListenerView;

public class DisPrefs extends PreferencePage implements
    IWorkbenchPreferencePage
{
  public static final String ID = "org.mwc.debrief.dis.preferences.DisPrefs";
  private Text simulationPathText;
  private Text ipAddressText;
  private Text portText;
  private IWorkbench _myBench;
  private Text siteFilterText;
  private Text appFilterText;
  private Text exText;
  private Text siteText;
  private Text appText;
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
    _myBench = workbench;
  }

  @Override
  protected Control createContents(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    composite.setLayoutData(gd);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    composite.setLayout(layout);

    // put a DOS button at the top-left
    Label iconLbl = new Label(composite, SWT.NONE);
    iconLbl.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(
        "org.mwc.debrief.dis", "icons/50px/dis_icon.png").createImage());
    GridData gd3 = new GridData(SWT.LEFT, SWT.CENTER, false, false);
    iconLbl.setLayoutData(gd3);
    createLabel(composite, " ");
    final Action act =
        CorePlugin.createOpenHelpAction(DisListenerView.CONFIG_CONTEXT, null,
            _myBench.getHelpSystem());
    Button helpBtn = new Button(composite, SWT.PUSH);
    helpBtn.addSelectionListener(new SelectionListener()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        act.run();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
    });
    helpBtn.setImage(CorePlugin.getImageDescriptor("icons/16/help.png")
        .createImage());
    GridData gd2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
    helpBtn.setLayoutData(gd2);

    // now the rest of the prefs
    Group localSettings = new Group(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    localSettings.setLayoutData(gd);
    localSettings.setText("Local executable");
    layout = new GridLayout(3, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    localSettings.setLayout(layout);

    createLabel(localSettings, "Path to executable:");
    simulationPathText =
        createText(localSettings, DisActivator.PATH_TO_SIMULATION_EXECUTABLE,
            150, true);

    final Button simulationPathBrowse = new Button(localSettings, SWT.PUSH);
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

    // ////////////////////////////
    // SERVER
    // /////////////////////////

    Group serverSettings = new Group(composite, SWT.NONE);
    serverSettings.setText("Server settings");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 2;
    serverSettings.setLayoutData(gd);
    layout = new GridLayout(3, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    serverSettings.setLayout(layout);

    Label tip = new Label(serverSettings, SWT.WRAP);
    gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
    gd.verticalSpan = 2;
    gd.widthHint = 250;
    tip.setLayoutData(gd);
    tip.setText("Provide the multicast connection parameters for the DIS provider");
    getShell().layout(true, true);

    createLabel(serverSettings, "IP Address");
    createLabel(serverSettings, "Port");

    ipAddressText =
        createText(serverSettings, DisActivator.IP_ADDRESS, 100, true);
    portText = createText(serverSettings, DisActivator.PORT, 100, true);

    // //////////////////////////////
    // IDENTITY
    // /////////////////////////////

    Group identitySettings = new Group(composite, SWT.NONE);
    identitySettings.setText("Identity settings");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    identitySettings.setLayoutData(gd);
    layout = new GridLayout(3, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    identitySettings.setLayout(layout);

    Label lbl2 = new Label(identitySettings, SWT.WRAP);
    gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
    gd.verticalSpan = 2;
    gd.widthHint = 250;
    lbl2.setLayoutData(gd);
    lbl2.setText("Use the following boxes to specify our ID for DIS sending");
    getShell().layout(true, true);

    createLabel(identitySettings, "Site");
    createLabel(identitySettings, "Application");

    siteText =
        createText(identitySettings, DisActivator.SITE_ID, 80, false);
    appText = createText(identitySettings, DisActivator.APP_ID, 80, false);

    // //////////////////////////////
    // FILTER
    // /////////////////////////////

    Group filterSettings = new Group(composite, SWT.NONE);
    filterSettings.setText("Filter settings");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    filterSettings.setLayoutData(gd);
    layout = new GridLayout(4, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    filterSettings.setLayout(layout);

    Label lbl = new Label(filterSettings, SWT.WRAP);
    gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
    gd.verticalSpan = 2;
    gd.widthHint = 250;
    lbl.setLayoutData(gd);
    lbl.setText("Use the following boxes to (optionally) control which DIS scenario to listen to");
    getShell().layout(true, true);

    createLabel(filterSettings, "Site");
    createLabel(filterSettings, "Application");
    createLabel(filterSettings, "Exercise");

    siteFilterText =
        createText(filterSettings, DisActivator.SITE_FILTER, 80, false);
    appFilterText =
        createText(filterSettings, DisActivator.APP_FILTER, 80, false);
    exText =
        createText(filterSettings, DisActivator.EXERCISE_FILTER, 80, false);

    // ////////////////////////////////////////////
    // DONE
    // ///////////////////////////////////////////
    simulationPathText.setFocus();

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
    checkInt(portString, "Invalid port", true, false, true);
    String ipAddress = ipAddressText.getText();
    if (ipAddress != null && !ipAddress.isEmpty())
    {
      if (!IP_ADDRESS_PATTERN.matcher(ipAddress).matches())
      {
        setErrorMessage("Invalid IP Address");
        return;
      }
    }
    String siteVal = siteFilterText.getText();
    if (!checkInt(siteVal, "Invalid site filter", false, true, true))
    {
      return;
    }
    String appVal = appFilterText.getText();
    if (!checkInt(appVal, "Invalid application filter", false, true, true))
    {
      return;
    }
    String exVal = exText.getText();
    if (!checkInt(exVal, "Invalid exercise filter", false, true, true))
    {
      return;
    }
    String siteId = siteText.getText();
    if (!checkInt(siteId, "Invalid site id", false, true, false))
    {
      return;
    }
    String appId = appText.getText();
    if (!checkInt(appId, "Invalid application id", false, true, false))
    {
      return;
    }

  }

  private boolean checkInt(String string, String errorMsg, boolean checkPort,
      boolean checkShort, boolean allowEmpty)
  {
    if (!allowEmpty && (string == null || string.length() == 0))
    {
      setErrorMessage(errorMsg + " (value required)");
      return false;
    }
    if(allowEmpty && (string == null || string.length() == 0))
    {
      return true;
    }

    if (string != null)
    {
      int val = 0;
      try
      {
        val = new Integer(string);
      }
      catch (NumberFormatException e)
      {
        // ignore; port is 0 (invalid)
        setErrorMessage(errorMsg + " (should be number)");
        return false;
      }
      if (checkPort)
      {
        if (val < 1 || val > 65535)
        {
          setErrorMessage(errorMsg + " (invalid port address)");
          return false;
        }
      }
      if (checkShort)
      {
        if (val < Short.MIN_VALUE || val > Short.MAX_VALUE)
        {
          setErrorMessage(errorMsg + " (out of range)");
          return false;
        }
      }
    }
    return true;
  }

  private Text createText(Composite composite, String prefs, Integer widthHint,
      boolean fillHoriz)
  {
    GridData gd;
    Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, fillHoriz, false);
    if (widthHint != null)
    {
      gd.widthHint = widthHint;
    }
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
    createAlignedLabel(composite, text, SWT.FILL);
  }

  private void createAlignedLabel(Composite composite, String text,
      int alignment)
  {
    Label label = new Label(composite, SWT.NONE);
    GridData gd = new GridData(alignment, SWT.CENTER, false, false);
    label.setLayoutData(gd);
    label.setText(text);
  }

  @Override
  protected void performDefaults()
  {
    simulationPathText.setText(NetworkPduSender.DEFAULT_MULTICAST_GROUP); //$NON-NLS-1$
    ipAddressText.setText("" + NetworkPduSender.PORT);
    portText.setText("");
    appFilterText.setText("");
    siteFilterText.setText("");
    exText.setText("");
    appText.setText("901");
    siteText.setText("902");
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
    store.setValue(DisActivator.APP_FILTER, appFilterText.getText());
    store.setValue(DisActivator.SITE_FILTER, siteFilterText.getText());
    store.setValue(DisActivator.EXERCISE_FILTER, exText.getText());
    store.setValue(DisActivator.SITE_ID, siteText.getText());
    store.setValue(DisActivator.APP_ID, appText.getText());
  }

}