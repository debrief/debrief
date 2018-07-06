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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
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
public class CreateProjectPage extends WizardPage
{

  private static final String CREATE_PROJECT_TITLE = "Create Project";
  private static final String IMPORT_PROJECT_TITLE = "Import Project";
  private static final String CREATE_A_PROJECT = "Create a project";
  private static final String IMPORT_PROJECT = "Import project";

  private static final String PROJECT_NAME_IS_REQUIRED =
      "The project name is required";
  private static final String WRITEABLE_FOLDER_IS_REQUIRED =
      "You must have write access to the project folder:[%s]";
  private static final String PROJECT_IMPORT_FOLDER_INVALID =
      "A project with this name is already loaded in Debrief:[%s]. Please select another";

  private IOverwriteQuery overwriteQuery = new IOverwriteQuery()
  {
    public String queryOverwrite(String file)
    {
      return ALL;
    }
  };

  private String projectName = "";

  private Text projectNameText;

  private Button locationButton;
  private Button addDebriefSamplesButton;

  private Wizard wizard;
  private boolean showAskMeButton;

  public CreateProjectPage(boolean showAskMeButton)
  {
    super("project.create");
    this.showAskMeButton = showAskMeButton;
    setPageComplete(false);
  }

  public void configureShell(Wizard wizard)
  {
    this.wizard = wizard;
    wizard.setWindowTitle(CREATE_PROJECT_TITLE);
  }

  boolean validate()
  {
    setErrorMessage(null);// clear errors

    if (projectName == null || projectName.isEmpty())
    {
      setErrorMessage(PROJECT_NAME_IS_REQUIRED);
      return false;
    }
    IProject[] projects =
        ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (IProject project : projects)
    {
      if (projectName.equals(project.getName()))
      {
        setErrorMessage(String.format(PROJECT_IMPORT_FOLDER_INVALID,
            projectName));
        return false;
      }
    }
    
    // check user has write access to project folder
    try
    {
      File sample =
          new File(projectNameText.getText(), "test_" + System.currentTimeMillis() + ".txt");
      sample.createNewFile();
      sample.delete();
    }
    catch(IOException ef)
    {
      setErrorMessage(String.format(WRITEABLE_FOLDER_IS_REQUIRED,
          projectName));
      return false;
    }
    
    // folder is not empty and found a project
    java.io.File projectPath = new java.io.File(projectNameText.getText());
    if (projectPath.list().length > 0
        && new java.io.File(projectPath, ".project").exists())
    {
      wizard.setWindowTitle(IMPORT_PROJECT);
      setTitle(IMPORT_PROJECT);
      getShell().setText(IMPORT_PROJECT_TITLE);
    }
    else
    {
      wizard.setWindowTitle(CREATE_A_PROJECT);
      setTitle(CREATE_A_PROJECT);
      getShell().setText(CREATE_PROJECT_TITLE);
    }

    return true;
  }

  boolean okPressed()
  {
    try
    {
      createProject();
      return true;
    }
    catch (Exception e)
    {
      MessageDialog.openError(getShell(), "Error",
          "Can't create new project.\nError:" + e.getMessage());
      DebriefPlugin.logError(Status.ERROR, "Whilst creating a project", e);
    }
    return false;
  }

  private IProject createProject() throws CoreException,
      InvocationTargetException, InterruptedException
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IProject project = root.getProject(projectName);

    // set the path to system level
    IProjectDescription desc =
        project.getWorkspace().newProjectDescription(project.getName());
    desc.setLocationURI(URIUtil.toURI(Path.fromOSString(projectNameText
        .getText().trim())));
    project.create(desc, null);
    project.refreshLocal(IResource.DEPTH_INFINITE, null);
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
      ImportOperation importOperation =
          new ImportOperation(sampleData.getFullPath(), installFile,
              FileSystemStructureProvider.INSTANCE, overwriteQuery);
      importOperation.setCreateContainerStructure(false);
      importOperation.run(new NullProgressMonitor());
    }
  }

  @Override
  public void createControl(Composite parent)
  {

    Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    contents.setLayout(new GridLayout(3, false));
    setTitle(CREATE_A_PROJECT);
    setMessage("Debrief requires a project to store your data in, which will be stored in \nyour file system. "
        + "Debrief can also provide you with some sample data, for use in the tutorials.");

    new Label(contents, SWT.LEFT).setText("Project location:");

    projectNameText = new Text(contents, SWT.SINGLE | SWT.BORDER);
    locationButton = new Button(contents, SWT.PUSH);

    projectNameText.setEditable(false);
    projectNameText.setText(projectName);

    projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false));

    locationButton.setText("Browse");
    locationButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        if (!projectNameText.getText().isEmpty())
          dialog.setFilterPath(projectNameText.getText());

        String selection = dialog.open();
        if (selection != null)
        {
          projectNameText.setText(selection);
          projectName = new java.io.File(selection).getName();
          setPageComplete(validate());
        }
      }
    });

    addDebriefSamplesButton = new Button(contents, SWT.CHECK);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    addDebriefSamplesButton.setLayoutData(gd);

    addDebriefSamplesButton
        .setText("Add Debrief Samples (required for self-teach tutorials)");
    addDebriefSamplesButton.setSelection(true);
    if (showAskMeButton)
    {
      final Button askMeNextTime = new Button(contents, SWT.CHECK);
      gd = new GridData(SWT.FILL, SWT.FILL, true, false);
      gd.horizontalSpan = 3;
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
              .putValue(PrefsPage.PreferenceConstants.ASK_ABOUT_PROJECT,
                  askMe.toString());
        }

      });
    }
    locationButton.setFocus();
    setControl(contents);

  }

  @Override
  public boolean canFlipToNextPage()
  {
    return false;
  }
}
