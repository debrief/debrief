package org.mwc.debrief.core.wizards.s2r;

import org.eclipse.jface.wizard.Wizard;

public class TMAFromSensorWizard extends Wizard
{

  SelectOffsetPage personalInfoPage;
  EnterSolutionPage addressInfoPage;

  public void addPages() {
           personalInfoPage = new SelectOffsetPage("Personal Information Page");
           addPage(personalInfoPage);
           addressInfoPage = new EnterSolutionPage(null);
           addPage(addressInfoPage);
  }
  public boolean performFinish() {
           return true;
  }

}
