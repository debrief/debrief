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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

public class CSVExportPage1 extends WizardPage
{

  private static final String TITLE = "UK Track Exchange Format - Track Export";
  private static final String DEC = "TODO";

  private final DropdownProvider provider;

  // Data Fields ---- TODO: change default values
  private String classification;
  private String type;
  private String sensor;
  private String majorAxis = "12.2";
  private String semiMajorAxis = "4.3";
  private String semiMinorAxis = "3.2";
  private String flag;
  private String likelihood;
  private String confidence;
  private Date infoCutoffDate = new Date();
  private String caseNumber = "D-112/12";
  private String suppliedBy;
  private String unitName;
  private String provenance;
  // --------

  // UI- Fields -------

  private Text provenanceTxt;
  private ComboViewer typeCmb;
  private Text unitNameTxt;
  private ComboViewer flagCmb;
  private ComboViewer sensorCmb;
  private ComboViewer classificationCmb;
  private ComboViewer likelihoodCmb;
  private ComboViewer confidenceCmb;
  private ComboViewer suppliedByCmb;
  private Text caseNumbertxt;
  private DateTime infoCutoffDateComp;

  // -----------

  public CSVExportPage1(final DropdownProvider provider, String unit,
      String provenance)
  {
    super("page1");
    setTitle(TITLE);
    setDescription(DEC);
    this.provider = provider;
    
    this.provenance = provenance;
    unitName = unit;
    
  }

  @Override
  public void createControl(Composite parent)
  {

    Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    contents.setLayout(new GridLayout(4, false));

    // line 1
    provenanceTxt = addCaseNumberField(contents, "Provenance:", provenance);
    typeCmb = addCmbField(contents, "TYPE", "Type:", false, type);
    // line 2
    unitNameTxt = addCaseNumberField(contents, "Unit Name:", unitName);
    flagCmb = addCmbField(contents, "FLAG", "Flag:", false, flag);
    // line 3
    caseNumbertxt = addCaseNumberField(contents, "Case Number:", caseNumber);
    sensorCmb = addCmbField(contents, "SENSOR", "Sensor:", true, sensor);
    // line 4
    classificationCmb = addCmbField(contents, "CLASSIFICATION",
        "Classification:", false, classification);
    likelihoodCmb = addCmbField(contents, "LIKELIHOOD", "Likelihood:", false,
        likelihood);
    // line 5
    addInfoCutoffDateField(contents);
    confidenceCmb = addCmbField(contents, "CONFIDENCE", "Confidence:", false,
        confidence);
    // line 6
    suppliedByCmb = addCmbField(contents, "SUPPLIED_BY", "Supplied by:", false,
        flag);

    setControl(contents);

  }

  private Text addCaseNumberField(Composite contents, String label,
      final String initialValue)
  {

    Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(label);
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    Text textControl = new Text(contents, SWT.BORDER);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    textControl.setLayoutData(gridData);
    if (initialValue != null)
      textControl.setText(initialValue);

    return textControl;

  }

  private void addInfoCutoffDateField(Composite contents)
  {

    Label lbl = new Label(contents, SWT.NONE);
    lbl.setText("Info Cut-off Date:");
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    infoCutoffDateComp = new DateTime(contents, SWT.BORDER | SWT.DROP_DOWN
        | SWT.DATE | SWT.LONG);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    infoCutoffDateComp.setLayoutData(gridData);
    if (caseNumber != null)
    {
      Calendar date = Calendar.getInstance();
      date.setTime(infoCutoffDate);
      infoCutoffDateComp.setDate(date.get(Calendar.YEAR), date.get(
          Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

    }

  }

  public void readValues()
  {

    type = getCmbVal(typeCmb, type);
    flag = getCmbVal(flagCmb, flag);
    sensor = getCmbVal(sensorCmb, sensor);
    classification = getCmbVal(classificationCmb, classification);
    likelihood = getCmbVal(likelihoodCmb, likelihood);
    confidence = getCmbVal(confidenceCmb, confidence);
    suppliedBy = getCmbVal(suppliedByCmb, suppliedBy);

    provenance = getTxtVal(provenanceTxt, provenance);
    unitName = getTxtVal(unitNameTxt, unitName);
    caseNumber = getTxtVal(caseNumbertxt, caseNumber);

    if (infoCutoffDateComp != null && infoCutoffDateComp.isDisposed())
    {
      Calendar date = Calendar.getInstance();
      date.set(Calendar.YEAR, infoCutoffDateComp.getYear());
      date.set(Calendar.MONTH, infoCutoffDateComp.getMonth());
      date.set(Calendar.DAY_OF_MONTH, infoCutoffDateComp.getDay());
      date.set(Calendar.HOUR_OF_DAY, 0);
      date.set(Calendar.MINUTE, 0);
      date.set(Calendar.SECOND, 0);
      date.set(Calendar.MILLISECOND, 0);
      infoCutoffDate = date.getTime();
    }

  }

  private String getTxtVal(Text control, String val)
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

  private static String getCmbVal(ComboViewer comboViewer, String val)
  {
    if (comboViewer != null && !comboViewer.getCombo().isDisposed())
    {
      StructuredSelection selection = (StructuredSelection) comboViewer
          .getSelection();
      val = (String) (selection.isEmpty() ? val : selection.getFirstElement());
    }

    return val;
  }

  private ComboViewer addCmbField(Composite contents, String key, String title,
      boolean edit, String val)
  {

    Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(title);
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    ComboViewer typeCmb = new ComboViewer(contents, (edit ? SWT.BORDER
        : SWT.READ_ONLY | SWT.BORDER));
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    typeCmb.setContentProvider(new ArrayContentProvider());
    typeCmb.setInput(provider.getValuesFor(key).toArray());
    typeCmb.getCombo().setLayoutData(gridData);
    if (val != null)
      typeCmb.getCombo().setText(val);
    else if (typeCmb.getCombo().getItemCount() > 0)
      typeCmb.getCombo().setText(typeCmb.getCombo().getItem(0));// select default first item

    return typeCmb;

  }

  public String getProvenance()
  {
    return provenance;
  }

  public String getUnitName()
  {

    return unitName;
  }

  public String getCaseNumber()
  {
    return caseNumber;
  }

  @SuppressWarnings("deprecation")
  public String getInfoCutoffDate()
  {
    return infoCutoffDate.toGMTString();
  }

  public String getSuppliedBy()
  {
    return suppliedBy;
  }

  public String getClassification()
  {
    return classification;
  }

  public String getType()
  {
    return type;
  }

  public String getFlag()
  {
    return flag;
  }

  public String getSensor()
  {
    return sensor;
  }

  public String getMajorAxis()
  {
    return majorAxis;
  }

  public String getSemiMajorAxis()
  {
    return semiMajorAxis;
  }

  public String getSemiMinorAxis()
  {
    return semiMinorAxis;
  }

  public String getLikelihood()
  {
    return likelihood;
  }

  public String getConfidence()
  {
    return confidence;
  }
}