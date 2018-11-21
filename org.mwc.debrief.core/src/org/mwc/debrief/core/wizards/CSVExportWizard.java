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
package org.mwc.debrief.core.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.debrief.core.ContextOperations.ExportTrackAsCSV;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

/**
 * Wizard to provide metadata used in exporting a track to the CSV export format
 */

public class CSVExportWizard extends Wizard implements INewWizard,
    ExportTrackAsCSV.CSVAttributeProvider
{
  private CSVExportPage1 page1;
  private CSVExportPage2 page2;
  private CSVExportPage3 page3;

  public static final String TITLE = "UK Track Exchange Format - Track Export";

  public static final String DEC =
      "This wizard is used to provide the extra metadata\r\n"
          + "necessary for exporting tracks to other UK agencies.";

  public static final ImageDescriptor WIZ_IMG = AbstractUIPlugin
      .imageDescriptorFromPlugin("org.mwc.debrief.core",
          "images/csvexport_wizard.png");

  public static final List<String> PAGE_NAMES = Arrays.asList(
      CSVExportPage1.PAGE_ID, CSVExportPage2.PAGE_ID, CSVExportPage3.PAGE_ID);

  private final DropdownProvider _dropdowns;
  private final String _unit;
  private final String _provenance;

  /**
   * Constructor for NewPlotWizard.
   *
   * @param reg
   * @param unit
   * @param provenance
   */
  public CSVExportWizard(final DropdownProvider reg, final String unit,
      final String provenance)
  {
    super();

    _dropdowns = reg;
    _unit = unit;
    _provenance = provenance;
  }

  /**
   * Adding the page to the wizard.
   */

  @Override
  public void addPages()
  {
    page1 = new CSVExportPage1(_dropdowns, _unit, _provenance);
    page2 = new CSVExportPage2(_dropdowns);
    page3 = new CSVExportPage3(_dropdowns);

    addPage(page1);
    addPage(page2);
    addPage(page3);
  }

  @Override
  public boolean canFinish()
  {
    final IWizardPage currentPage = getContainer().getCurrentPage();
    return currentPage.equals(page3) && page3.isPageComplete();
  }

  @Override
  public String getCaseNumber()
  {
    return page2.getCaseNumber();
  }

  @Override
  public String getClassification()
  {
    return page2.getClassification();
  }

  @Override
  public String getConfidence()
  {
    return page2.getConfidence();
  }

  @Override
  public String getDistributionStatement()
  {
    return page3.getStatement();
  }

  @Override
  public String getFilePath()
  {
    return page3.getExportFolder();
  }

  @Override
  public String getFlag()
  {
    return page1.getFlag();
  }

  @Override
  public String getLikelihood()
  {
    return page2.getLikelihood();
  }

  @Override
  public String getProvenance()
  {
    return page1.getProvenance();
  }

  @Override
  public String getPurpose()
  {
    return page3.getPurpose();
  }

  @Override
  public String getSemiMajorAxis()
  {
    return page1.getSemiMajorAxis();
  }

  @Override
  public String getSemiMinorAxis()
  {
    return page1.getSemiMinorAxis();
  }

  @Override
  public String getSensor()
  {
    return page1.getSensor();
  }

  @Override
  public String getSuppliedBy()
  {
    return page2.getSuppliedBy();
  }

  @Override
  public String getType()
  {
    return page1.getType();
  }

  @Override
  public String getUnitName()
  {
    return page1.getUnitName();
  }

  /**
   * We will accept the selection in the workbench to see if we can initialize from it.
   *
   * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
   */
  @Override
  public void init(final IWorkbench workbench,
      final IStructuredSelection selection1)
  {
    // this.selection = selection1;
  }

  @Override
  public boolean performFinish()
  {

    page1.readValues();
    page2.readValues();
    page3.readValues();

    return page1.isPageComplete() && page2.isPageComplete() && page3
        .isPageComplete();
  }
  
  public static ComboViewer addCmbField(final Composite contents, final String key,
      final String title, String tooltip, final boolean edit, final String val,
      final WizardPage page, final DropdownProvider provider)
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
    List<String> values = provider.getValuesFor(key);
    if(values==null) {
      values = new ArrayList<String>();
      page.setErrorMessage("No value for "+ title +" in file, may be an invalid file");
      page.setPageComplete(false);
    }
    typeCmb.setInput(values.toArray());
    typeCmb.getCombo().setLayoutData(gridData);
    if (val != null)
      typeCmb.getCombo().setText(val);
    else if (typeCmb.getCombo().getItemCount() > 0)
      typeCmb.getCombo().setText(typeCmb.getCombo().getItem(0));// select default first item

    return typeCmb;
  }


}