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

import java.util.Date;

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

  private ISelection selection;

  @SuppressWarnings("unused")
  private final DropdownProvider _dropdowns;

  /**
   * Constructor for NewPlotWizard.
   * 
   * @param reg
   */
  public CSVExportWizard(final DropdownProvider reg)
  {
    super();

    _dropdowns = reg;
  }

  /**
   * Adding the page to the wizard.
   */

  @Override
  public void addPages()
  {
    page1 = new CSVExportPage1(_dropdowns);
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

  @Override
  public String getProvenance()
  {
    return "HMS NONSUCH";
  }

  @Override
  public String getUnitName()
  {
    return "USS ALLIANCE";
  }

  @Override
  public String getCaseNumber()
  {
    return "D-112/12";
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getInfoCutoffDate()
  {
    return new Date().toGMTString();
  }

  @Override
  public String getSuppliedBy()
  {
    return "DEEP BLUE";
  }

  @Override
  public String getPurpose()
  {
    return "For operational planning";
  }

  @Override
  public String getClassification()
  {
    return "Private";
  }

  @Override
  public String getDistributionStatement()
  {
    return page2.getStatement();
  }

  @Override
  public String getType()
  {
    return "FISHER";
  }

  @Override
  public String getFlag()
  {
    return "AMERICA";
  }

  @Override
  public String getSensor()
  {
    return "RADAR";
  }

  @Override
  public String getMajorAxis()
  {
    return "12.2";
  }

  @Override
  public String getSemiMajorAxis()
  {
    return "4.3";
  }

  @Override
  public String getSemiMinorAxis()
  {
    return "3.2";
  }

  @Override
  public String getLikelihood()
  {
    return "Remote";
  }

  @Override
  public String getConfidence()
  {
    return "Med";
  }

  @Override
  public String getFilePath()
  {
    return page2.getExportFolder();
  }

}