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

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

public class CSVExportPage2 extends CustomWizardPage
{

  private static final String CSV_EXPORT_CASE_NUMBER = "CSV_EXPORT_caseNumber";
  private static final String CSV_EXPORT_SUPPLIED_BY = "CSV_EXPORT_suppliedBy";
  private static final String CSV_EXPORT_CONFIDENCE = "CSV_EXPORT_confidence";
  private static final String CSV_EXPORT_LIKELIHOOD = "CSV_EXPORT_likelihood";
  private static final String CSV_EXPORT_CLASSIFICATION =
      "CSV_EXPORT_classification";
  public static final String PAGE_ID = "2. Background";

  private static Text addCaseNumberField(final Composite contents,
      final String label, final String tooltip, final String initialValue)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(label);
    lbl.setText(tooltip);
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

  private final DropdownProvider provider;
  // Data Fields ---- TODO: change default values
  private String caseNumber = "D-112/12";

  private String classification;
  private String likelihood;
  private String confidence;
  private final Date infoCutoffDate = new Date();
  private String suppliedBy;

  // UI- Fields -------

  // --------
  private ComboViewer classificationCmb;
  private ComboViewer likelihoodCmb;
  private ComboViewer confidenceCmb;
  private ComboViewer suppliedByCmb;
  private Text caseNumbertxt;

  public CSVExportPage2(final DropdownProvider provider)
  {
    super(PAGE_ID);
    setTitle(CSVExportWizard.TITLE);
    setDescription(CSVExportWizard.DEC);
    this.provider = provider;

    readFromPref();

    super.setImageDescriptor(CSVExportWizard.WIZ_IMG);

  }

  @Override
  protected Composite createDataSection(final Composite parent)
  {
    final Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayout(new GridLayout(2, false));

    caseNumbertxt = addCaseNumberField(contents, "Case Number:", "Case number",
        caseNumber);

    classificationCmb = addCmbField(contents, "CLASSIFICATION",
        "Classification:", "Protective marking for this data", true,
        classification, this, provider);

    suppliedByCmb = addCmbField(contents, "SUPPLIED_BY", "Supplied by:",
        "Supplier organisation", false, suppliedBy, this, provider);

    likelihoodCmb = addCmbField(contents, "LIKELIHOOD", "Likelihood:",
        "Likelihood of subject identification", false, likelihood, this,
        provider);

    confidenceCmb = addCmbField(contents, "CONFIDENCE", "Confidence:",
        "Confidence in subject track", false, confidence, this, provider);

    return contents;
  }

  public String getCaseNumber()
  {
    return caseNumber;
  }

  public String getClassification()
  {
    return classification;
  }

  public String getConfidence()
  {
    return confidence;
  }

  @SuppressWarnings("deprecation")
  public String getInfoCutoffDate()
  {
    return infoCutoffDate.toGMTString();
  }

  public String getLikelihood()
  {
    return likelihood;
  }

  @Override
  protected List<String> getPageNames()
  {
    return CSVExportWizard.PAGE_NAMES;
  }

  public String getSuppliedBy()
  {
    return suppliedBy;
  }

  public void readFromPref()
  {
    classification = getPrefValue(CSV_EXPORT_CLASSIFICATION, classification);
    likelihood = getPrefValue(CSV_EXPORT_LIKELIHOOD, likelihood);
    confidence = getPrefValue(CSV_EXPORT_CONFIDENCE, confidence);
    suppliedBy = getPrefValue(CSV_EXPORT_SUPPLIED_BY, suppliedBy);
    caseNumber = getPrefValue(CSV_EXPORT_CASE_NUMBER, caseNumber);
  }

  public void readValues()
  {
    classification = getCmbVal(classificationCmb, classification);
    likelihood = getCmbVal(likelihoodCmb, likelihood);
    confidence = getCmbVal(confidenceCmb, confidence);
    suppliedBy = getCmbVal(suppliedByCmb, suppliedBy);
    caseNumber = getTxtVal(caseNumbertxt, caseNumber);

    writeToPref();

  }

  public void writeToPref()
  {
    classification = setPrefValue(CSV_EXPORT_CLASSIFICATION, classification);
    likelihood = setPrefValue(CSV_EXPORT_LIKELIHOOD, likelihood);
    confidence = setPrefValue(CSV_EXPORT_CONFIDENCE, confidence);
    suppliedBy = setPrefValue(CSV_EXPORT_SUPPLIED_BY, suppliedBy);
    caseNumber = setPrefValue(CSV_EXPORT_CASE_NUMBER, caseNumber);
  }
}