/**
 * 
 */
package org.mwc.debrief.core.loaders;


import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.debrief.core.CorePlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefXMLReaderWriter;

import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class XMLLoader extends IPlotLoader.BaseLoader
{
	
	/** the static object we use for data-file load/open
	 * 
	 */
	private static DebriefXMLReaderWriter _myReader;
	
	
	public XMLLoader()
	{
		if(_myReader == null)
		{
			_myReader = new DebriefXMLReaderWriter();
		}
	}
	
	/** load the data-file
	 * 
	 * @param destination
	 * @param source
	 * @param fileName
	 */
	public void doTheLoad(Layers destination, 
			InputStream source, 
			String fileName,
			IControllableViewport view)
	{			
		_myReader.importThis(fileName, source ,destination, view);
	}

	/*
	 * (non-Javadoc)
	 * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer.editors.PlotEditor,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void loadFile(final PlotEditor thePlot, IEditorInput input)
	{
		String source = super.getFileName(input);

		Object theInput = input;

		Class inputClass = theInput.getClass();

		if (input instanceof org.eclipse.ui.part.FileEditorInput)
		{
			org.eclipse.ui.part.FileEditorInput ife = (org.eclipse.ui.part.FileEditorInput) input;
			final IFile _theFile = ife.getFile();
			String theName = _theFile.getName();

			final String thePath = _theFile.getFullPath().toOSString();
			IPath iPath = _theFile.getFullPath();

			CorePlugin.logError(Status.INFO, "About to load XML file:" + theName,	null);
			final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

			try
			{
				// stick it in a stream
				final InputStream is = _theFile.getContents();

				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress()
				{
					public void run(IProgressMonitor pm)
					{
						// right, better suspend the LayerManager extended updates from
						// firing
						theLayers.suspendFiringExtended(true);

						try
						{
							CorePlugin.logError(Status.INFO, "about to start loading:" + thePath, null);

							// ok - get loading going
							doTheLoad(theLayers, is, thePath, thePlot);
							
							CorePlugin.logError(Status.INFO, "completed loading:" + thePath, null);

							// and inform the plot editor
							thePlot.loadingComplete(this);

							CorePlugin.logError(Status.INFO, "parent plot informed", null);

						}
						catch (RuntimeException e)
						{
							e.printStackTrace();
							CorePlugin.logError(Status.ERROR, "Problem loading datafile:" + thePath, e);
						}
						finally
						{
							// ok, allow the layers object to inform anybody what's happening
							// again
							theLayers.suspendFiringExtended(false);

							// and trigger an update ourselves
							theLayers.fireExtended();
						}
					}
				});

			}

			catch (CoreException e)
			{
				CorePlugin.logError(org.eclipse.core.runtime.Status.ERROR,
						"Unable to open XML file for input:" + theName, e);
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
			}
		}
		// ok, load the data...
		CorePlugin.logError(Status.INFO, "Successfully loaded XML file", null);
	}
}
