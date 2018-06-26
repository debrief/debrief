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
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.mwc.cmap.core.wizards.CoastWizardPage;
import org.mwc.cmap.core.wizards.ETOPOWizardPage;
import org.mwc.cmap.core.wizards.GridWizardPage;
import org.mwc.cmap.core.wizards.NaturalEarthWizardPage;
import org.mwc.cmap.core.wizards.NewPlotFilenameWizardPage;
import org.mwc.cmap.core.wizards.ScaleWizardPage;
import org.mwc.debrief.core.ContextOperations.ExportTrackAsCSV;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

/**
 * Wizard to provide metadata used in exporting a track to the CSV export format
 */

public class CSVExportWizard extends Wizard implements INewWizard, ExportTrackAsCSV.CSVAttributeProvider
{
  private NewPlotFilenameWizardPage _fileWizard;
  private ScaleWizardPage _scaleWizard;
  private CoastWizardPage _coastWizard;
  private GridWizardPage _gridWizard;
  private ETOPOWizardPage _etopoWizard;

  private ISelection selection;

  private NaturalEarthWizardPage _naturalEarthWizard;
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
    _fileWizard = new NewPlotFilenameWizardPage(selection);
    _scaleWizard = new ScaleWizardPage(selection);
    _coastWizard = new CoastWizardPage(selection);
    _gridWizard = new GridWizardPage(selection);
    // _etopoWizard = new ETOPOWizardPage(selection);

    _naturalEarthWizard = new NaturalEarthWizardPage(selection);

    addPage(_fileWizard);
    addPage(_naturalEarthWizard);
    addPage(_scaleWizard);
    if (_coastWizard != null)
      addPage(_coastWizard);
    addPage(_gridWizard);
    if (_etopoWizard != null && _etopoWizard.isAvailable())
      addPage(_etopoWizard);
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
  public boolean performFinish()
  {
    return false;
  }

  @Override
  public String getCountry()
  {
    return "NARNIA";
  }

  @Override
  public String getType()
  {
    return "SHIP";
  }

  @Override
  public String getFilePath()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getProvenance()
  {
    return "HMS NONSUCH";
  }

}