/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.ContextOperations.ExportCSVPrefs;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;

/**
 * There are two ways to load data from a file on the preference page:
 * <ul>
 * <li>directly by "Update now" button
 * <li>on storing file name to preference store
 * </ul>
 *
 */
public class ExportCSVPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

  /**
   * Constant definitions for plug-in preferences
   */
  public static class PreferenceConstants
  {
    public static final String INCLUDE_COMMAND = "INCLUDE_COMMAND";
    public static final String PATH_TO_CSV = "CSV_LOCATION";
  }

  private static final String CONTEXT_ID =
      "org.mwc.debrief.help.ExportCSVsPrefs";

  /**
   * extension filters for file selection dialog
   */
  private static final String[] availableExtensions = new String[]
  {"*.csv"};//$NON-NLS-1$

  private FileFieldEditor myFileEditor;

  public ExportCSVPreferencesPage()
  {
    super(GRID);
    setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
    setDescription("Preferences related to exporting Tracks to in UK Track Exchange format (CSV)");
  }

  private void addEnabledButton()
  {
    final Composite parent = getFieldEditorParent();
    final BooleanFieldEditor enabledEditor = new BooleanFieldEditor(PreferenceConstants.INCLUDE_COMMAND,
        "Include 'Export track to CSV Text format' in Track drop-down menu", parent);
    addField(enabledEditor);
  }

  private void addFileEditor()
  {
    final Composite parent = getFieldEditorParent();
    myFileEditor = new FileFieldEditor(PreferenceConstants.PATH_TO_CSV,
        Messages.ExportCSVLookupPreferencesPage_FileLabel, true,
        StringFieldEditor.VALIDATE_ON_KEY_STROKE, parent)
    {

      @Override
      protected void doStore()
      {
        super.doStore();
        reloadDataFromFile();
      }
    };
    myFileEditor.setErrorMessage(
        Messages.ExportCSVLookupPreferencesPage_InvalidFileName);
    myFileEditor.setFileExtensions(availableExtensions);
    addField(myFileEditor);
  }

  private void addOpenFileHyperlink()
  {
    final Composite parent = getFieldEditorParent();

    final Link link = new Link(parent, SWT.NONE);
    link.setText(Messages.ExportCSVLookupPreferencesPage_OpenFileLabel);
    link.addListener(SWT.Selection, new Listener()
    {

      @Override
      public void handleEvent(final Event event)
      {
        openSystemEditor();
      }
    });

    int numColumns = ((GridLayout) parent.getLayout()).numColumns;
    // skip last cell for 'reload' button
    if (numColumns > 1)
    {
      numColumns--;
    }
    GridDataFactory.fillDefaults().span(numColumns, 1).align(SWT.BEGINNING,
        SWT.CENTER).applyTo(link);
  }

  private void addReloadButton()
  {
    final Composite parent = getFieldEditorParent();
    final Button reloadButton = new Button(parent, SWT.PUSH);
    reloadButton.setText(Messages.ExportCSVLookupPreferencesPage_UpdateNow);
    reloadButton.addSelectionListener(new SelectionListener()
    {

      @Override
      public void widgetDefaultSelected(final SelectionEvent e)
      {
        // nothing
      }

      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        reloadDataFromFile();
      }
    });

    int widthHint = convertHorizontalDLUsToPixels(
        IDialogConstants.BUTTON_WIDTH);
    widthHint = Math.max(widthHint, reloadButton.computeSize(SWT.DEFAULT,
        SWT.DEFAULT, true).x);

    GridDataFactory.fillDefaults().hint(widthHint, SWT.DEFAULT).align(SWT.FILL,
        SWT.CENTER).applyTo(reloadButton);
  }

  @Override
  protected void createFieldEditors()
  {
    addEnabledButton();
    
    final Composite parent = getFieldEditorParent();
    new Label(parent, SWT.NONE);
    Label lbl = new Label(parent, SWT.NONE);
    new Label(parent, SWT.NONE);
    lbl.setText("Specify location of spreadsheet containing drop-down menu items:");
    
    addFileEditor();
    addOpenFileHyperlink();
    addReloadButton();

    // and the context-sensitive help
    PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), CONTEXT_ID);
  }

  private String getFileName()
  {
    return myFileEditor.getStringValue();
  }

  /**
   * if we have a file specified, open it - else throw error
   *
   */
  private void openSystemEditor()
  {
    final String fileName = getFileName();
    if ((fileName == null) || (fileName.length() == 0))
    {
      MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
          "Edit CSV Dropdowns file",
          "CSV Dropdowns file not assigned, \nplease create file as descried in Debrief help (link button below),"
              + "\nand select it using the above Browse button");
    }
    else
    {
      final File file = new File(fileName);
      try
      {
        Desktop.getDesktop().open(file);
      }
      catch (final IOException e)
      {
        CorePlugin.logError(IStatus.ERROR,
            Messages.ExportCSVLookupPreferencesPage_ErrorOnOpenFileEditor, e);
      }
    }
  }

  private void reloadDataFromFile()
  {
    CSVExportDropdownRegistry.getRegistry().setFileName(getFileName());
    CSVExportDropdownRegistry.getRegistry().reload();
  }

  @Override
  public void init(IWorkbench workbench)
  {
    // TODO Auto-generated method stub
  }
}
