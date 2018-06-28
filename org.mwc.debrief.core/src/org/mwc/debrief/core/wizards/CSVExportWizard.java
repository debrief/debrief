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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
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

  @SuppressWarnings("unused")
  private ISelection selection;

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
  public CSVExportWizard(final DropdownProvider reg, String unit, final String provenance)
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

    addPage(page1);
    addPage(page2);
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
    this.selection = selection1;
  }

  @Override
  public boolean canFinish()
  {
    IWizardPage currentPage = getContainer().getCurrentPage();
    return currentPage != page1 && page2.isPageComplete();
  }

  @Override
  public boolean performFinish()
  {

    page1.readValues();
    page2.readValues();

    
    return page1.isPageComplete() && page2.isPageComplete();
  }


  
  
  public String getProvenance()
  {
    return page1.getProvenance();
  }

  public String getUnitName()
  {
    return page1.getUnitName();
  }

  public String getCaseNumber()
  {
    return page1.getCaseNumber();
  }

  public String getInfoCutoffDate()
  {
    return page1.getInfoCutoffDate();
  }

  public String getSuppliedBy()
  {
    return page1.getSuppliedBy();
  }

  public String getClassification()
  {
    return page1.getClassification();
  }

  public String getType()
  {
    return page1.getType();
  }

  public String getFlag()
  {
    return page1.getFlag();
  }

  public String getSensor()
  {
    return page1.getSensor();
  }

  public String getMajorAxis()
  {
    return page1.getMajorAxis();
  }

  public String getSemiMajorAxis()
  {
    return page1.getSemiMajorAxis();
  }

  public String getSemiMinorAxis()
  {
    return page1.getSemiMinorAxis();
  }

  public String getLikelihood()
  {
    return page1.getLikelihood();
  }

  public String getConfidence()
  {
    return page1.getConfidence();
  }

 
  @Override
  public String getPurpose()
  {
    return page2.getPurpose();
  }
  
  @Override
  public String getDistributionStatement()
  {
    return page2.getStatement();
  }
  
  @Override
  public String getFilePath()
  {
    return page2.getExportFolder();
  }

}