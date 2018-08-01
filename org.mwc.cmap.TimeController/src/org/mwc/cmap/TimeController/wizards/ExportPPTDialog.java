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

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class ExportPPTDialog extends Dialog
{

  
  private static final String[] supportedFormats = {"PPTX"};
  
  private Text txtExportLocation;
  private Text txtFilename;
  private Combo cmbFileFormats;
  
  private String fileFormat;
  private String fileName;
  private String exportLocation;
  
  public ExportPPTDialog(Shell parentShell)
  {
    super(parentShell);
  }
  
  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Debrief");
    setShellStyle(SWT.RESIZE);
    newShell.setSize(550, 300);
  }
  
  @Override
  protected boolean isResizable()
  {
    return true;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    //setTitle("Dynamic Exporter");
    //setMessage("Export your recording");
    Composite dialogParent = (Composite)super.createDialogArea(parent);
    Composite composite = new Composite(dialogParent,SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    composite.setLayout(new GridLayout(3,false));
    Label lblExportLocation = new Label(composite,SWT.NONE);
    lblExportLocation.setText("Export Location");
    txtExportLocation = new Text(composite,SWT.BORDER);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.grabExcessHorizontalSpace=true;
    data.horizontalAlignment=SWT.FILL;
    txtExportLocation.setLayoutData(data);
    Button btnBrowse = new Button(composite,SWT.PUSH);
    btnBrowse.setText("Browse..");
    btnBrowse.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        DirectoryDialog fd = new DirectoryDialog(getParentShell(),SWT.OPEN);
        String selectedFile = fd.open();
        if(selectedFile != null) {
          txtExportLocation.setText(selectedFile);
          exportLocation = txtExportLocation.getText();
        }
      }
    }
    );
    Label lblFilename = new Label(composite,SWT.NONE);
    lblFilename.setText("File name");
    txtFilename = new Text(composite,SWT.BORDER);
    txtFilename.setLayoutData(data);
    cmbFileFormats = new Combo(composite,SWT.DROP_DOWN);
    cmbFileFormats.setItems(supportedFormats);
    initUI();
    return dialogParent;
  }
  
  private void initUI() {
    if(exportLocation!=null) {
      txtExportLocation.setText(exportLocation);
    }
    if(fileFormat!=null) {
      cmbFileFormats.setText(fileFormat);
    }
    else {
      cmbFileFormats.setText("PPTX");
    }
    if(fileName!=null) {
      txtFilename.setText(fileName);
    }
    
  }
  
  @Override
  protected void okPressed()
  {
    //set the export path and format here
    if(isNullOrEmpty(txtExportLocation.getText())) {
      MessageDialog.openError(getParentShell(), "Error!", "Specify the location to export the PPT file");
      txtExportLocation.setFocus();
    }
    else if(isNullOrEmpty(txtFilename.getText())) {
      MessageDialog.openError(getParentShell(), "Error!", "Specify the name of the file to export");
      txtFilename.setFocus();
    }
    else {
      this.exportLocation = txtExportLocation.getText();
      this.fileName = txtFilename.getText();
      this.fileFormat = cmbFileFormats.getText();
      super.okPressed();
    }
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public String getExportLocation()
  {
    return exportLocation;
  }
  public void setExportLocation(String exportLocation)
  {
    this.exportLocation = exportLocation;
  }
  public String getFileFormat() {
    return fileFormat;
  }
  public void setFileFormat(String fileFormat)
  {
    this.fileFormat = fileFormat;
  }
  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    ((GridLayout) parent.getLayout()).numColumns++;
    final Link link = new Link(parent,SWT.NONE);
    link.setText("<a>Click to view PPT template</a>");
    link.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false));
    link.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        final String prefId = "org.mwc.debrief.core.preferences.PrefsPage";
        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(link.getShell(), prefId, null, null);
        dialog.open();
      }
    });
    super.createButtonsForButtonBar(parent);
    
  }
  private boolean isNullOrEmpty(String text) {
    return text==null || "".equals(text.trim());
  }
}
