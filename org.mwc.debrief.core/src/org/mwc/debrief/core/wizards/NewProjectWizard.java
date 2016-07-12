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
