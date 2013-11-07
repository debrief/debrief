package org.mwc.debrief.satc_interface.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.wizards.CoastWizardPage;
import org.mwc.cmap.core.wizards.ETOPOWizardPage;
import org.mwc.cmap.core.wizards.GridWizardPage;
import org.mwc.cmap.core.wizards.NewPlotFilenameWizardPage;
import org.mwc.cmap.core.wizards.ScaleWizardPage;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottables;
import MWC.GUI.Chart.Painters.CoastPainter;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "xml". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class NewStraightLegWizard extends Wizard implements INewWizard
{
	private CourseConstraintsWizardPage _courseWizard;
	private SpeedConstraintsWizardPage _speedWizard;
	private LegNameWizardPage _nameWizard;

	private ISelection selection;

	/**
	 * Constructor for NewPlotWizard.
	 */
	public NewStraightLegWizard()
	{
		super();
	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages()
	{
		_nameWizard = new LegNameWizardPage(selection);
		_courseWizard = new CourseConstraintsWizardPage(selection);
		_speedWizard = new SpeedConstraintsWizardPage(selection);

		addPage(_nameWizard);
		addPage(_speedWizard);
		addPage(_courseWizard);
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

//	void doFinish(final String containerName, final String fileName,
//			final IProgressMonitor monitor) throws CoreException
//	{
//		// create a sample file
//		monitor.beginTask("Creating " + fileName, 2);
//		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		final IResource resource = root.findMember(new Path(containerName));
//		if (!resource.exists() || !(resource instanceof IContainer))
//		{
//			throwCoreException("Container \"" + containerName + "\" does not exist.");
//		}
//		final IContainer container = (IContainer) resource;
//		final IFile file = container.getFile(new Path(fileName));
//		try
//		{
//			final InputStream stream = openContentStream();
//			if (file.exists())
//			{
//				file.setContents(stream, true, true, monitor);
//			}
//			else
//			{
//				file.create(stream, true, monitor);
//			}
//			stream.close();
//		}
//		catch (final IOException e)
//		{
//		}
//		monitor.worked(1);
//		monitor.setTaskName("Opening file for editing...");
//		getShell().getDisplay().asyncExec(new Runnable()
//		{
//			public void run()
//			{
//				final IWorkbenchPage page = PlatformUI.getWorkbench()
//						.getActiveWorkbenchWindow().getActivePage();
//				try
//				{
//					IDE.openEditor(page, file, DebriefPlugin.DEBRIEF_EDITOR);
//				}
//				catch (final PartInitException e)
//				{
//					CorePlugin.logError(Status.ERROR, "Whilst opening new file", e);
//				}
//			}
//		});
//		monitor.worked(1);
//	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(final IWorkbench workbench,
			final IStructuredSelection selection1)
	{
		this.selection = selection1;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish()
	{
		// ok, geneate the new straight leg constraint
		return true;
	}
	
	public String getName()
	{
		return _nameWizard.getEditable().getName();
	}

	public LegNameWizardPage getNameWizard()
	{
		return _nameWizard;
	}
	
	public CourseConstraintsWizardPage getCourseWizard()
	{
		return _courseWizard;
	}
	public SpeedConstraintsWizardPage getSpeedWizard()
	{
		return _speedWizard;
	}

}