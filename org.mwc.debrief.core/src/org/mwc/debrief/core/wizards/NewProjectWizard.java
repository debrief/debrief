/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.core.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewProjectWizard extends Wizard implements INewWizard
{
  private CreateProjectPage page;
  boolean showAskMeButton;
  public NewProjectWizard(boolean showAskMeButton)
  {
    this.showAskMeButton = showAskMeButton;
  }
  public NewProjectWizard()
  {
    this(false);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {

  }

  @Override
  public void addPages()
  {
    page = new CreateProjectPage(showAskMeButton);
    page.configureShell(this);
    addPage(page);
    super.addPages();
  }

  @Override
  public boolean performFinish()
  {
    return page.okPressed();
  }

  @Override
  public boolean canFinish()
  {
    return page != null && page.isPageComplete();
  }
}
