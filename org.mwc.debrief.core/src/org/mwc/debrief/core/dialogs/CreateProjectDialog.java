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
package org.mwc.debrief.core.dialogs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.preferences.PrefsPage;

/**
 * Create a project
 */
public class CreateProjectDialog extends TitleAreaDialog
{

	private static final String PROJECT_NAME_IS_REQUIRED = "The project name is required";

	private IOverwriteQuery overwriteQuery = new IOverwriteQuery()
	{
		public String queryOverwrite(String file)
		{
			return ALL;
		}
	};
    
	private String projectName = "DebriefProject";

	private Text projectNameText;

	private Button okButton;

	private Button addDebriefSamplesButton;

	private boolean showAskMeButton;

	public CreateProjectDialog(Shell parentShell, boolean showAskMeButton)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.SHEET);
		this.showAskMeButton = showAskMeButton;
	}

	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Create Project");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite contents = new Composite(parentComposite, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setTitle("Create a project");
		setMessage("Debrief requires a project to store your data in, which will stored be in the .DebriefNG folder in\nyour home directory." +
				"Debrief can also provide you with some sample data, for use in the tutorials.");

		new Label(contents, SWT.LEFT).setText("Project name:");

		projectNameText = new Text(contents, SWT.SINGLE | SWT.BORDER);
		projectName = "DebriefProject";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project != null && project.exists()) {
			projectName = "";
		}
		projectNameText.setText(projectName);
		projectNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				if (event.widget == projectNameText)
				{
					projectName = projectNameText.getText().trim();
					okButton.setEnabled(validate());
				}
			}

		});
		
		addDebriefSamplesButton = new Button(contents, SWT.CHECK);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		addDebriefSamplesButton.setLayoutData(gd);
		
		addDebriefSamplesButton.setText("Add Debrief Samples (required for self-teach tutorials)");
		addDebriefSamplesButton.setSelection(true);
	
		if (showAskMeButton)
		{
			final Button askMeNextTime = new Button(contents, SWT.CHECK);
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 2;
			askMeNextTime.setLayoutData(gd);

			askMeNextTime.setText("Always check at startup");
			askMeNextTime.setSelection(true);
			askMeNextTime.addSelectionListener(new SelectionAdapter()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					Boolean askMe = askMeNextTime.getSelection();
					CorePlugin.getDefault().getPreferenceStore()
							.putValue(PrefsPage.PreferenceConstants.ASK_ABOUT_PROJECT, askMe.toString());
				}

			});
		}
		projectNameText.setFocus();

		Dialog.applyDialogFont(parentComposite);

		Point defaultMargins = LayoutConstants.getMargins();
		GridLayoutFactory.fillDefaults().numColumns(2)
				.margins(defaultMargins.x, defaultMargins.y).generateLayout(contents);

		return contents;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(projectName != null && !projectName.isEmpty());
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	private boolean validate()
	{
		if (projectName == null || projectName.isEmpty()) {
			setErrorMessage(PROJECT_NAME_IS_REQUIRED);
			return false;
		}
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project:projects) {
			if (projectName.equals(project.getName())) {
				setErrorMessage("There is such project.");
				return false;
			}
		}
		return true;
	}
	

	@Override
	protected boolean isResizable()
	{
		return true;
	}

	@Override
	protected void okPressed()
	{
		try
		{
			createProject();
		}
		catch (Exception e)
		{
			MessageDialog.openError(getShell(), "Error", "Can not create project. Error:" + e.getMessage());
			DebriefPlugin.logError(Status.ERROR, "Whilst create a project", e);
		}
		super.okPressed();
	}

	private IProject createProject() throws CoreException, InvocationTargetException, InterruptedException
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);
		if (addDebriefSamplesButton.getSelection())
		{
			importSamples(project);
		}
		return project;
	}
	
	private void importSamples(IProject project)
			throws InvocationTargetException, InterruptedException, CoreException
	{
		Location installLocation = Platform.getInstallLocation();
		if (installLocation == null)
		{
			return;
		}
		String installFileStr = installLocation.getURL().getFile();
		File installFile = new File(installFileStr, "sample_data");
		if (installFile.isDirectory())
		{
			IFolder sampleData = project.getFolder("sample_data");
			sampleData.create(true, true, new NullProgressMonitor());
			ImportOperation importOperation = new ImportOperation(
					sampleData.getFullPath(), installFile,
					FileSystemStructureProvider.INSTANCE, overwriteQuery);
			importOperation.setCreateContainerStructure(false);
			importOperation.run(new NullProgressMonitor());
		}
	}
}

