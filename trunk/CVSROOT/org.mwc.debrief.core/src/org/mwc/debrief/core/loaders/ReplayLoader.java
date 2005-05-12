/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.io.IOException;
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
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.debrief.core.CorePlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class ReplayLoader extends IPlotLoader.BaseLoader
{

	public void doTheLoad(Layers destination, InputStream source, String fileName)
	{

	}

	/**
	 * @param _theFile
	 * @param thePath
	 * @param theLayers
	 * @param is
	 */
	private void doTheLoad(final IFile _theFile, final String thePath,
			final Layers theLayers, final InputStream is)
	{
		final ImportReplay importer = new Debrief.ReaderWriter.Replay.ImportReplay()
		{
			// override the count-lines method. We may only have a project-relative
			// to the data-file - and the legacy code won't be able to find the file.
			// we do, however have a stream for the input file - just count the
			// lines in this.
			public int countLinesFor(String fName)
			{
				int lines = 0;
				try
				{
					// create ourselves a fresh stream. we create some fresh streams
					// based on this one which get closed in processing
					final InputStream lineCounterStream = _theFile.getContents();
					lines = super.countLinesInStream(lineCounterStream);
					lineCounterStream.close();
					CorePlugin.logError(Status.INFO, "Replay loader - counted:" + lines
							+ " lines", null);
				}
				catch (IOException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Failed to open stream for counting lines:" + fName, null);
					e.printStackTrace();
				}
				catch (CoreException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Failed to open stream for counting lines:" + fName, null);
					e.printStackTrace();
				}
				return lines;
			}

		};
		// and do the import...
		importer.importThis(thePath, is, theLayers);
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

			CorePlugin.logError(Status.INFO, "About to load REPLAY file:" + theName,
					null);
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
							// ok - get loading going
							doTheLoad(_theFile, thePath, theLayers, is);
							
							// and inform the plot editor
							thePlot.loadingComplete(this);
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
						"Unable to open REP file for input:" + theName, e);
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
		CorePlugin.logError(Status.INFO, "Successfully loaded REPLAY file", null);
	}
}
