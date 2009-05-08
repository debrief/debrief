package org.mwc.debrief.core.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.ui.ide.IDE;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.CoastPainter;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "xml". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class NewPlotWizard extends Wizard implements INewWizard
{
	private FilenameWizardPage _fileWizard;
	private ScaleWizardPage _scaleWizard;
	private CoastWizardPage _coastWizard;
	private GridWizardPage _gridWizard;
	private ETOPOWizardPage _etopoWizard;

	private ISelection selection;

	private final Layers _myNewLayers;

	/**
	 * Constructor for NewPlotWizard.
	 */
	public NewPlotWizard()
	{
		super();
		setNeedsProgressMonitor(true);

		_myNewLayers = new Layers();
	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages()
	{
		_fileWizard = new FilenameWizardPage(selection);
		_scaleWizard = new ScaleWizardPage(selection);
		_coastWizard = new CoastWizardPage(selection);
		_gridWizard = new GridWizardPage(selection);
		_etopoWizard = new ETOPOWizardPage(selection);
		addPage(_fileWizard);
		addPage(_scaleWizard);
		addPage(_coastWizard);
		addPage(_gridWizard);
		addPage(_etopoWizard);
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	void doFinish(String containerName, String fileName, IProgressMonitor monitor)
			throws CoreException
	{
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer))
		{
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		final IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try
		{
			final InputStream stream = openContentStream();
			if (file.exists())
			{
				file.setContents(stream, true, true, monitor);
			}
			else
			{
				file.create(stream, true, monitor);
			}
			stream.close();
		}
		catch (final IOException e)
		{
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				final IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try
				{
					IDE.openEditor(page, file, "org.mwc.debrief.core.editors.PlotEditor");
				}
				catch (final PartInitException e)
				{
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection1)
	{
		this.selection = selection1;
	}

	/**
	 * Put our layers object into a file
	 */
	private InputStream openContentStream()
	{
		// ok, where do we dump our layers to?
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		// hmm, now actually output the layers
		DebriefEclipseXMLReaderWriter.exportThis(_myNewLayers, bos);

		// and return our OS as an IS
		return new ByteArrayInputStream(bos.toByteArray());
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish()
	{

		// get the chart features layer.
		Layer chartFeatures = _myNewLayers.findLayer(Layers.CHART_FEATURES);
		if (chartFeatures == null)
		{
			final BaseLayer baseFeatures = new BaseLayer();
			baseFeatures.setName(Layers.CHART_FEATURES);

			// make the chart-features layer double-buffered, it won't hold time
			// related data
			// NOTE : we stopped it being double-buffered, since the text was
			// plotted blurred.
			baseFeatures.setBuffered(false);

			// ok, now use the chart-features reference
			chartFeatures = baseFeatures;

			// and store it.
			_myNewLayers.addThisLayer(chartFeatures);
		}

		// ok, add our new layers. The wizards return null if one isn't wanted,
		// the layers object manages that ok.
		chartFeatures.add(_scaleWizard.getEditable());
		chartFeatures.add(_gridWizard.getEditable());

		final CoastPainter coast = (CoastPainter) _coastWizard.getEditable();
		if (coast != null)
		{
			// complete the laze instantiation of the coastline - we're only going to
			// load the data if/when we need it
			coast.initData();

			// cool add it to our plot
			chartFeatures.add(coast);
		}

		// also add in the ETOPO layer if it worked
		final Layer etopoLayer = (Layer) _etopoWizard.getEditable();
		if (etopoLayer != null)
			_myNewLayers.addThisLayer(etopoLayer);

		final String containerName = _fileWizard.getContainerName();
		final String fileName = _fileWizard.getFileName();
		final IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException
			{
				try
				{
					doFinish(containerName, fileName, monitor);
				}
				catch (final CoreException e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					monitor.done();
				}
			}
		};
		try
		{
			getContainer().run(true, false, op);
		}
		catch (final InterruptedException e)
		{
			return false;
		}
		catch (final InvocationTargetException e)
		{
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	private void throwCoreException(String message) throws CoreException
	{
		final IStatus status = new Status(IStatus.ERROR, "org.mwc.debrief.core",
				IStatus.OK, message, null);
		throw new CoreException(status);
	}
}