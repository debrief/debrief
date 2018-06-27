/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.wizards;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

public class CSVExportPage2 extends WizardPage
{

  private static final String TITLE = "UK Track Exchange Format - Track Export";
  private static final String DEC = "TODO";

  private final DropdownProvider provider;

  // Data Fields ---- TODO: change default values
  private String purpose = "For operational planning";
  private String statement;
  private String exportFolder = new File(System.getProperty("user.home"),
      "debrief").getAbsolutePath();
  // ------

  // UI - Fields -----
  private Text purposeTxt;
  private Text statementTxt;
  private Text folderTxt;

  // ------

  public CSVExportPage2(final DropdownProvider provider)
  {
    super("page2");
    setTitle(TITLE);
    setDescription(DEC);
    this.provider = provider;

  }

  @Override
  public void createControl(Composite parent)
  {
    Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    contents.setLayout(new GridLayout(3, false));

    addPurposeField(contents);
    addStatementField(contents);
    addFolderField(contents);

    setControl(contents);
  }

  private void addPurposeField(Composite contents)
  {

    Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Purpose:");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    purposeTxt = new Text(contents, SWT.BORDER);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 200;
    purposeTxt.setLayoutData(gridData);
    if (purpose != null)
      purposeTxt.setText(purpose);

    new Label(contents, SWT.NONE);// empty for 3rd col

  }

  private void addStatementField(Composite contents)
  {

    if (statement == null && !provider.getValuesFor("DISTRIBUTION").isEmpty())
      statement = provider.getValuesFor("DISTRIBUTION").get(0);
    Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Distribution Statement:");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END
        | GridData.VERTICAL_ALIGN_BEGINNING));

    statementTxt = new Text(contents, SWT.BORDER | SWT.MULTI|SWT.WRAP);
    GridData gridData = new GridData(GridData.GRAB_VERTICAL
        | GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    gridData.widthHint = 200;
    statementTxt.setLayoutData(gridData);
    if (statement != null)
      statementTxt.setText(statement);

  }

  private void addFolderField(Composite contents)
  {

    Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Destination:");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    folderTxt = new Text(contents, SWT.BORDER);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL
        | GridData.GRAB_HORIZONTAL);
    folderTxt.setLayoutData(gridData);
    if (exportFolder != null)
      folderTxt.setText(exportFolder);
    folderTxt.setEditable(false);
    Button browse = new Button(contents, SWT.PUSH);
    browse.setText("Browse");
    browse.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
        directoryDialog.setFilterPath(folderTxt.getText());
        directoryDialog.setText("Destination");
        String path = directoryDialog.open();
        if (path != null)
        {
          folderTxt.setText(path);
        }

      }
    });

  }

  public String getPurpose()
  {
    return purpose;
  }

  public String getStatement()
  {
    return statement;
  }

  public String getExportFolder()
  {
    return exportFolder;
  }

  void readValues()
  {
    if (purposeTxt != null && !purposeTxt.isDisposed())
      purpose = purposeTxt.getText().trim();

    if (statementTxt != null && !statementTxt.isDisposed())
      statement = statementTxt.getText().trim();

    if (folderTxt != null && !folderTxt.isDisposed())
      exportFolder = folderTxt.getText().trim();
  }
}