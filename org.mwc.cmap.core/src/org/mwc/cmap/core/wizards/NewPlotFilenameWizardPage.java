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
package org.mwc.cmap.core.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dpf).
 */

public class NewPlotFilenameWizardPage extends WizardPage
{
	private Text containerText;

	private Text fileText;

	private final ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewPlotFilenameWizardPage(final ISelection selection)
	{
		super("wizardPage");
		setTitle("Create new Debrief Plot");
		setDescription("This wizard creates a new Debrief Plot-file, please provide a name for the file.");
		this.selection = selection;
		super.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.mwc.debrief.core", "images/newplot_wizard.gif"));
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(final Composite parent)
	{
		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				dialogChanged();
			}
		});

		final Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Ensures that both text fields are set.
	 */

	void dialogChanged()
	{
		final IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		final String fileName = getFileName();

		if (getContainerName().length() == 0)
		{
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0)
		{
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible())
		{
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0)
		{
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0)
		{
			updateStatus("File name must be valid");
			return;
		}
		final int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1)
		{
			final String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("dpf") == false)
			{
				updateStatus("File extension must be \"dpf\"");
				return;
			}
		}
		updateStatus(null);
	}

	public String getContainerName()
	{
		return containerText.getText();
	}

	public String getFileName()
	{
		return fileText.getText();
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	void handleBrowse()
	{
		final ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == Window.OK)
		{
			final Object[] result = dialog.getResult();
			if (result.length == 1)
			{
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize()
	{
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection)
		{
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			final Object obj = ssel.getFirstElement();
			if (obj instanceof IResource)
			{
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("new_plot.dpf");
	}

	private void updateStatus(final String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}