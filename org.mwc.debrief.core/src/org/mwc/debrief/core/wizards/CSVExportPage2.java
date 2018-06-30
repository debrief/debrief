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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

public class CSVExportPage2 extends CustomWizardPage
{

  private static final String TITLE = "UK Track Exchange Format - Track Export";
  private static final String DEC = "This wizard is used to provide the extra metadata\r\n" + 
      "necessary for exporting tracks to other UK agencies.";

  
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
  private String caseNumber = "D-112/12";

  private String classification;
  private String likelihood;
  private String confidence;
  private Date infoCutoffDate = new Date();
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
    super(CSVExportWizard.PAGE_NAMES.get(1));
    setTitle(TITLE);
    setDescription(DEC);
    this.provider = provider;

    readFormPref();

    super.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
        "org.mwc.debrief.core", "images/csvexport_wizard.png"));

  }

  private Text addCaseNumberField(final Composite contents, final String label, String tooltip,
      final String initialValue)
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

  private ComboViewer addCmbField(final Composite contents, final String key,
      final String title, String tooltip, final boolean edit, final String val)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(title);
    lbl.setToolTipText(tooltip);
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    final ComboViewer typeCmb = new ComboViewer(contents, (edit ? SWT.BORDER
        : SWT.READ_ONLY | SWT.BORDER));
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
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


  
  
  @Override
  protected Composite createDataSection(Composite parent)
  {
    final Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayout(new GridLayout(2, false));

    caseNumbertxt = addCaseNumberField(contents, "Case Number:","Case number", caseNumber);

    classificationCmb = addCmbField(contents, "CLASSIFICATION",
        "Classification:","Protective marking for this data", false, classification);

    suppliedByCmb = addCmbField(contents, "SUPPLIED_BY", "Supplied by:","Supplier organisation", false,
        suppliedBy);
    
    likelihoodCmb = addCmbField(contents, "LIKELIHOOD", "Likelihood:","Likelihood of subject identification", false,
        likelihood);
    
    confidenceCmb = addCmbField(contents, "CONFIDENCE", "Confidence:","Confidence in subject track", false,
        confidence);
    


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



  public String getSuppliedBy()
  {
    return suppliedBy;
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



  public void readFormPref()
  {

    
    classification = getPrefValue("CSV_EXPORT_classification", classification);
    likelihood = getPrefValue("CSV_EXPORT_likelihood", likelihood);
    confidence = getPrefValue("CSV_EXPORT_confidence", confidence);
    suppliedBy = getPrefValue("CSV_EXPORT_suppliedBy", suppliedBy);
    caseNumber = getPrefValue("CSV_EXPORT_caseNumber", caseNumber);
  }

  public void writeToPref()
  {


    classification = setPrefValue("CSV_EXPORT_classification", classification);
    likelihood = setPrefValue("CSV_EXPORT_likelihood", likelihood);
    confidence = setPrefValue("CSV_EXPORT_confidence", confidence);
    suppliedBy = setPrefValue("CSV_EXPORT_suppliedBy", suppliedBy);
    caseNumber = setPrefValue("CSV_EXPORT_caseNumber", caseNumber);
  }

  String getPrefValue(String key, String currentVal)
  {
    IPreferenceStore preferenceStore = CorePlugin.getDefault()
        .getPreferenceStore();
    String newVal = preferenceStore.getString(key);

    return (newVal == null || newVal.isEmpty()) ? currentVal : newVal;
  }

  String setPrefValue(String key, String currentVal)
  {
    IPreferenceStore preferenceStore = CorePlugin.getDefault()
        .getPreferenceStore();

    preferenceStore.setValue(key, currentVal);
    return currentVal;
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
}