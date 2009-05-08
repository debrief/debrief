package org.mwc.debrief.core.wizards.s2r;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class TMAFromSensorWizard extends Wizard
{
  SelectOffsetPage personalInfoPage;
  EnterSolutionPage addressInfoPage;

  public void addPages() {
           personalInfoPage = new SelectOffsetPage(null);
           addPage(personalInfoPage);
           addressInfoPage = new EnterSolutionPage(null);
           addPage(addressInfoPage);
  }
  public boolean performFinish() {
           return true;
  }
  
	@Override
	public IWizardPage getPage(String name)
	{
		return super.getPage(name);
	}
  
  
}
