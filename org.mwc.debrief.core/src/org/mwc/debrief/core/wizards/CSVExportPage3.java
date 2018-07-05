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
import java.util.List;

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

public class CSVExportPage3 extends CustomWizardPage
{

  private static final String CSV_EXPORT_STATEMENT = "CSV_EXPORT_statement";

  private static final String CSV_EXPORT_PURPOSE = "CSV_EXPORT_purpose";

  private static final String CSV_EXPORT_EXPORT_FOLDER = "CSV_EXPORT_exportFolder";

  public static final String PAGE_ID = "3. Release";

  private final DropdownProvider provider;

  // Data Fields ---- TODO: change default values
  private String purpose = "For operational planning";
  private String statement;
  private String exportFolder = new File(System.getProperty("user.home"))
      .getAbsolutePath();
  // ------

  // UI - Fields -----
  private Text purposeTxt;
  private Text statementTxt;
  private Text folderTxt;

  // ------

  public CSVExportPage3(final DropdownProvider provider)
  {
    super(PAGE_ID);
    setTitle(CSVExportWizard.TITLE);
    setDescription(CSVExportWizard.DEC);
    this.provider = provider;
    readFormPref();
    super.setImageDescriptor(CSVExportWizard.WIZ_IMG);

  }

  @Override
  protected List<String> getPageNames()
  {
    return CSVExportWizard.PAGE_NAMES;
  }

  private void addFolderField(final Composite contents)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Destination:");
    lbl.setToolTipText("Where the data-file is to be stored");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    folderTxt = new Text(contents, SWT.BORDER);
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL
        | GridData.GRAB_HORIZONTAL);
    folderTxt.setLayoutData(gridData);
    if (exportFolder != null)
      folderTxt.setText(exportFolder);
    folderTxt.setEditable(false);
    final Button browse = new Button(contents, SWT.PUSH);
    browse.setText("Browse");
    browse.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        final DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
        directoryDialog.setFilterPath(folderTxt.getText());
        directoryDialog.setText("Destination");
        final String path = directoryDialog.open();
        if (path != null)
        {
          folderTxt.setText(path);
          setPageComplete(true);

        }

      }
    });

  }

  private void addPurposeField(final Composite contents)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Purpose:");
    lbl.setToolTipText("Acceptable uses for this information");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    purposeTxt = new Text(contents, SWT.BORDER);
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 200;
    purposeTxt.setLayoutData(gridData);
    if (purpose != null)
      purposeTxt.setText(purpose);

    new Label(contents, SWT.NONE);// empty for 3rd col

  }

  private void addStatementField(final Composite contents)
  {

    if (statement == null && !provider.getValuesFor("DISTRIBUTION").isEmpty())
      statement = provider.getValuesFor("DISTRIBUTION").get(0);
    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Distribution Statement:");
    lbl.setToolTipText("Details on how this information can be used");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END
        | GridData.VERTICAL_ALIGN_BEGINNING));

    statementTxt = new Text(contents, SWT.BORDER | SWT.MULTI | SWT.WRAP);
    final GridData gridData = new GridData(GridData.GRAB_VERTICAL
        | GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    gridData.widthHint = 200;
    statementTxt.setLayoutData(gridData);
    if (statement != null)
      statementTxt.setText(statement);

  }

  @Override
  protected Composite createDataSection(Composite parent)
  {
    final Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayout(new GridLayout(3, false));

    addPurposeField(contents);
    addStatementField(contents);
    addFolderField(contents);

    return (contents);
  }

  public void readFormPref()
  {

    exportFolder = getPrefValue(CSV_EXPORT_EXPORT_FOLDER, exportFolder);
    purpose = getPrefValue(CSV_EXPORT_PURPOSE, purpose);
    statement = getPrefValue(CSV_EXPORT_STATEMENT, statement);
  }

  public void writeToPref()
  {

    exportFolder = setPrefValue(CSV_EXPORT_EXPORT_FOLDER, exportFolder);
    purpose = setPrefValue(CSV_EXPORT_PURPOSE, purpose);
    statement = setPrefValue(CSV_EXPORT_STATEMENT, statement);
  }

  public String getExportFolder()
  {
    return exportFolder;
  }

  public String getPurpose()
  {
    return purpose;
  }

  public String getStatement()
  {
    return statement;
  }

  public void readValues()
  {
    if (purposeTxt != null && !purposeTxt.isDisposed())
      purpose = purposeTxt.getText().trim();

    if (statementTxt != null && !statementTxt.isDisposed())
      statement = statementTxt.getText().trim();

    if (folderTxt != null && !folderTxt.isDisposed())
      exportFolder = folderTxt.getText().trim();

    if (exportFolder == null || exportFolder.isEmpty())
    {
      setErrorMessage("Please select valid Destination folder.");

      return;
    }
    validate();
  }

  private void validate()
  {
    if (exportFolder == null || exportFolder.isEmpty())
    {
      setErrorMessage("Please select valid Destination folder.");

      setPageComplete(false);

      return;
    }
    else if (!new File(exportFolder).exists() || !new File(exportFolder)
        .isDirectory() || !new File(exportFolder).canWrite())
    {
      setErrorMessage(
          "Please select valid Destination folder with write access.");

      setPageComplete(false);

      return;
    }
    writeToPref();
    setErrorMessage(null);
    setPageComplete(true);
  }

}