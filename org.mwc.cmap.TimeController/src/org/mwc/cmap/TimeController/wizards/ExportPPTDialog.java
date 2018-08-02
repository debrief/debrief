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
package org.mwc.cmap.TimeController.wizards;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class ExportPPTDialog extends Dialog
{

  public static final String PREF_PPT_EXPORT_LOCATION = "pptExportLocation";
  public static final String PREF_PPT_EXPORT_FILENAME = "pptExportFilename";
  public static final String PREF_PPT_EXPORT_FILEFORMAT = "pptExportFormat";
  public static final String PREF_PPT_EXPORT_OPEN_FILE = "pptExportOpenFile";

  private static final String[] supportedFormats =
  {"PPTX"};

  private static String getFileNameStem(final String fileName)
  {
    final String newName;
    if (fileName.indexOf("-") != -1)
    {
      newName = fileName.substring(0, fileName.lastIndexOf("-"));
    }
    else
    {
      newName = fileName;
    }
    return newName;
  }

  private Text txtExportLocation;
  private Text txtFilename;

  private Combo cmbFileFormats;
  private String fileFormat;
  private String fileName;

  private String exportLocation;
  private boolean viewOnComplete = true;

  private Button viewOnCompleteBtn;

  public ExportPPTDialog(final Shell parentShell)
  {
    super(parentShell);

    // load prefs
    exportLocation = PlatformUI.getPreferenceStore().getString(
        PREF_PPT_EXPORT_LOCATION);
    fileName = PlatformUI.getPreferenceStore().getString(
        PREF_PPT_EXPORT_FILENAME);
    fileFormat = PlatformUI.getPreferenceStore().getString(
        PREF_PPT_EXPORT_FILEFORMAT);
    viewOnComplete = PlatformUI.getPreferenceStore().getBoolean(
        PREF_PPT_EXPORT_OPEN_FILE);
  }

  @Override
  protected void configureShell(final Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Debrief export");
    setShellStyle(SWT.RESIZE);
    newShell.setSize(550, 300);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent)
  {
    ((GridLayout) parent.getLayout()).numColumns++;
    final Link link = new Link(parent, SWT.NONE);
    link.setText("<a>Click to view PPT template</a>");
    link.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
    link.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        final String prefId = "org.mwc.debrief.core.preferences.PrefsPage";
        final PreferenceDialog dialog = PreferencesUtil
            .createPreferenceDialogOn(link.getShell(), prefId, null, null);
        dialog.open();
      }
    });

    // create OK and Cancel buttons by default
    createButton(parent, IDialogConstants.OK_ID, "Export", true);
    createButton(parent, IDialogConstants.CANCEL_ID,
        IDialogConstants.CANCEL_LABEL, false);

  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final Composite dialogParent = (Composite) super.createDialogArea(parent);
    final Composite composite = new Composite(dialogParent, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    composite.setLayout(new GridLayout(3, false));
    final Label lblExportLocation = new Label(composite, SWT.NONE);
    lblExportLocation.setText("Export Location");
    txtExportLocation = new Text(composite, SWT.BORDER);
    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = SWT.FILL;
    txtExportLocation.setLayoutData(data);
    final Button btnBrowse = new Button(composite, SWT.PUSH);
    btnBrowse.setText("Browse..");
    btnBrowse.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        final DirectoryDialog fd = new DirectoryDialog(getParentShell(),
            SWT.OPEN);
        final String selectedFile = fd.open();
        if (selectedFile != null)
        {
          txtExportLocation.setText(selectedFile);
          exportLocation = txtExportLocation.getText();
        }
      }
    });
    final Label lblFilename = new Label(composite, SWT.NONE);
    lblFilename.setText("File name");
    txtFilename = new Text(composite, SWT.BORDER);
    txtFilename.setLayoutData(data);
    cmbFileFormats = new Combo(composite, SWT.DROP_DOWN);
    cmbFileFormats.setItems(supportedFormats);

    // ok, and the "view on complete" toggle
    viewOnCompleteBtn = new Button(composite, SWT.CHECK);
    viewOnCompleteBtn.setText("Open exported PPTX");
    viewOnCompleteBtn.setSelection(viewOnComplete);

    initUI();
    return dialogParent;
  }

  public String getExportLocation()
  {
    return exportLocation;
  }

  public String getFileFormat()
  {
    return fileFormat;
  }

  public String getFileName()
  {
    return fileName;
  }

  public String getFileToExport(final String filenameOverride)
  {
    final String fName = filenameOverride != null ? filenameOverride : fileName;
    return exportLocation + File.separator + fName + "." + fileFormat;
  }

  public boolean getOpenOncomplete()
  {
    return viewOnComplete;
  }

  private void initUI()
  {
    if (exportLocation != null)
    {
      txtExportLocation.setText(exportLocation);
    }
    if (fileFormat != null)
    {
      cmbFileFormats.setText(fileFormat);
    }
    else
    {
      cmbFileFormats.setText("PPTX");
    }
    if (fileName != null)
    {
      txtFilename.setText(fileName);
    }
    viewOnCompleteBtn.setSelection(viewOnComplete);
  }

  private boolean isNullOrEmpty(final String text)
  {
    return text == null || "".equals(text.trim());
  }

  @Override
  protected boolean isResizable()
  {
    return true;
  }

  @Override
  protected void okPressed()
  {
    // check the export path and format
    if (isNullOrEmpty(txtExportLocation.getText()))
    {
      MessageDialog.openError(getParentShell(), "Error!",
          "Specify the location to export the PPT file");
      txtExportLocation.setFocus();
    }
    else if (isNullOrEmpty(txtFilename.getText()))
    {
      MessageDialog.openError(getParentShell(), "Error!",
          "Specify the name of the file to export");
      txtFilename.setFocus();
    }
    else
    {
      // ok, we've got to store the values in the controls,
      // since they're about to get disposed
      this.exportLocation = txtExportLocation.getText();
      this.fileName = txtFilename.getText();
      final String stemmedName = getFileNameStem(fileName);
      this.fileFormat = cmbFileFormats.getText();
      this.viewOnComplete = viewOnCompleteBtn.getSelection();

      // and store the prefs
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_LOCATION,
          exportLocation);
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_FILENAME,
          stemmedName);
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_FILEFORMAT,
          fileFormat);
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_OPEN_FILE,
          viewOnComplete);

      // let parent do it's business
      super.okPressed();
    }
  }

  public void setExportLocation(final String exportLocation)
  {
    this.exportLocation = exportLocation;
  }

  public void setFileFormat(final String fileFormat)
  {
    this.fileFormat = fileFormat;
  }

  public void setFileName(final String fileName)
  {
    this.fileName = fileName;
  }

  public void setOpenOnComplete(final Boolean openFile)
  {
    viewOnComplete = openFile;
  }
}
