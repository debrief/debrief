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

import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

public class CSVExportPage1 extends CustomWizardPage
{

  public static final String PAGE_ID = "1. Subject";
  private static final String CSV_EXPORT_SENSOR = "CSV_EXPORT_sensor";
  private static final String CSV_EXPORT_FLAG = "CSV_EXPORT_flag";
  private static final String CSV_EXPORT_TYPE = "CSV_EXPORT_type";
  private static final String CSV_EXPORT_UNIT = "CSV_EXPORT_unit";
  private static final String CSV_EXPORT_PROVENANCE = "CSV_EXPORT_provenance";

  @Override
  protected List<String> getPageNames()
  {
    return CSVExportWizard.PAGE_NAMES;
  }

  protected static String getCmbVal(final ComboViewer comboViewer, String val)
  {
    final String res;
    if (comboViewer != null && !comboViewer.getCombo().isDisposed())
    {
      final StructuredSelection selection = (StructuredSelection) comboViewer
          .getSelection();
      if (selection.isEmpty())
      {
        // ah, it's not one of the drop downs, so
        // get the value from the combo
        final String comboText = comboViewer.getCombo().getText();
        res = comboText == null ? val : comboText;
      }
      else
      {
        // just get the selected item
        res = (String) selection.getFirstElement();
      }
    }
    else
    {
      res = val;
    }

    return res;
  }

  private final DropdownProvider provider;
  // Data Fields ---- TODO: change default values

  private String type;
  private String sensor;
  private String flag;
  private String unitName;
  private String semiMajorAxis = "0.5";
  private String semiMinorAxis = "0.5";

  // UI- Fields -------

  private String provenance;
  // --------
  private Text provenanceTxt;
  private Text typeTxt;
  private Text unitNameTxt;
  private Text semiMajorAxisTxt;
  private Text semiMinorAxisTxt;
  private ComboViewer flagCmb;
  private ComboViewer sensorCmb;

  public CSVExportPage1(final DropdownProvider provider, final String unit,
      final String provenance)
  {
    super(PAGE_ID);
    setTitle(CSVExportWizard.TITLE);
    setDescription(CSVExportWizard.DEC);
    this.provider = provider;

    this.provenance = provenance;
    unitName = unit;
    readFormPref();

    super.setImageDescriptor(CSVExportWizard.WIZ_IMG);

  }

  private Text addTxtField(final Composite contents, final String label,
      String tooltip, final String initialValue)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(label);
    lbl.setToolTipText(tooltip);
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    final Text textControl = new Text(contents, SWT.BORDER);
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    textControl.setLayoutData(gridData);
    if (initialValue != null)
      textControl.setText(initialValue);

    return textControl;

  }

 
  @Override
  protected Composite createDataSection(Composite parent)
  {
    final Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayout(new GridLayout(2, false));

    provenanceTxt = addTxtField(contents, "Provenance:", "Source platform, \n"
        + "Eg: HMS Nelson", provenance);
    sensorCmb = CSVExportWizard.addCmbField(contents, "SENSOR", "Sensor:", "Source sensor",
        true, sensor, this, provider);

    unitNameTxt = addTxtField(contents, "Unit Name:", "Subject platform",
        unitName);

    flagCmb = CSVExportWizard.addCmbField(contents, "FLAG", "Flag:", "Subject nationality",
        true, flag, this, provider);
    typeTxt = addTxtField(contents, "Type:", "Subject platform type", type);

    semiMajorAxisTxt = addTxtField(contents, "Semi-Major Axis (Nm):",
        "½ ellipse length", semiMajorAxis);

    semiMinorAxisTxt = addTxtField(contents, "Semi-Minor Axis (Nm):",
        "½ ellipse length", semiMinorAxis);

    return contents;
  }

  public String getFlag()
  {
    return flag;
  }

  public String getProvenance()
  {
    return provenance;
  }

  public String getSensor()
  {
    return sensor;
  }

  private String getTxtVal(final Text control, final String val)
  {
    if (control != null && !control.isDisposed())
    {
      return control.getText().trim();
    }
    else
    {
      return val;
    }
  }

  public String getType()
  {
    return type;
  }

  public String getUnitName()
  {

    return unitName;
  }

  public String getSemiMajorAxis()
  {
    return semiMajorAxis;
  }

  public String getSemiMinorAxis()
  {
    return semiMinorAxis;
  }

  public void readFormPref()
  {

    if (provenance == null || provenance.isEmpty())
    {
      provenance = getPrefValue("CSV_EXPORT_provenance", provenance);
    }
    if (unitName == null || unitName.isEmpty())
    {
      unitName = getPrefValue(CSV_EXPORT_UNIT, unitName);
    }
    type = getPrefValue(CSV_EXPORT_TYPE, type);
    flag = getPrefValue(CSV_EXPORT_FLAG, flag);
    sensor = getPrefValue(CSV_EXPORT_SENSOR, sensor);

  }

  public void writeToPref()
  {

    provenance = setPrefValue(CSV_EXPORT_PROVENANCE, provenance);

    unitName = setPrefValue(CSV_EXPORT_UNIT, unitName);

    type = setPrefValue(CSV_EXPORT_TYPE, type);
    flag = setPrefValue(CSV_EXPORT_FLAG, flag);
    sensor = setPrefValue(CSV_EXPORT_SENSOR, sensor);
    semiMajorAxis = getTxtVal(semiMajorAxisTxt, semiMajorAxis);
    semiMinorAxis = getTxtVal(semiMinorAxisTxt, semiMinorAxis);

  }

  public void readValues()
  {

    type = getTxtVal(typeTxt, type);
    flag = getCmbVal(flagCmb, flag);
    sensor = getCmbVal(sensorCmb, sensor);

    provenance = getTxtVal(provenanceTxt, provenance);
    unitName = getTxtVal(unitNameTxt, unitName);

    writeToPref();

  }
}