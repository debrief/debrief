package org.mwc.cmap.core.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;

public class MessageWizardPage extends WizardPage
{
	private String _message;

	public MessageWizardPage(String pageName, String pageTitle,
			String pageDescription, String message, String imagePath)
	{
		super(pageName);
		setTitle(pageTitle);
		setDescription(pageDescription);
		_message = message;
		
		// ok, now try to set the image
		if (imagePath != null)
		{
			final ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(
					"org.mwc.debrief.core", imagePath);
			if (id != null)
				super.setImageDescriptor(id);
			else
				CorePlugin.logError(IStatus.WARNING, "Wizard image file not found for:"
						+ imagePath, null);
		}

	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		Label txt = new Label(composite, SWT.WRAP);
		txt.setText(_message);
		txt.setSize(600, 200);
	}
}