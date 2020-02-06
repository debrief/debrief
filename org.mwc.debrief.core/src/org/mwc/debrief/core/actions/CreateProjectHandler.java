
package org.mwc.debrief.core.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mwc.debrief.core.wizards.NewProjectWizard;

public class CreateProjectHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
	  Shell shell = PlatformUI.getWorkbench().getModalDialogShellProvider()
        .getShell();
    
    NewProjectWizard wizard = new NewProjectWizard(false);
      wizard.init(PlatformUI.getWorkbench(), null);

      WizardDialog dialog = new WizardDialog(shell, wizard)
      {
        @Override
        public void setShellStyle(int newShellStyle)
        {
          super.setShellStyle(newShellStyle| SWT.SHEET);
        }
      };
      dialog.create();
      dialog.open();
		return null;
	}

}
