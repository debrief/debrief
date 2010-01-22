/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.PlainImporterBase;

/**
 * @author ian.mayo
 */
public class ReplayLoader extends IPlotLoader.BaseLoader
{

	// public void doTheLoad(Layers destination, InputStream source, String
	// fileName)
	// {
	//
	// }

	/**
	 * @param _theFile
	 * @param thePath
	 * @param theLayers
	 * @param is
	 */
	void doTheLoad(final String thePath, final String theFileName,
			final Layers theLayers, final InputStream is)
	{
		final PlainImporterBase importer = new Debrief.ReaderWriter.Replay.ImportReplay()
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
					final FileInputStream lineCounterStream = new FileInputStream(fName);
					lines = super.countLinesInStream(lineCounterStream);
					lineCounterStream.close();
					DebriefPlugin.logError(Status.INFO, "Replay loader - counted:"
							+ lines + " lines", null);
				}
				catch (FileNotFoundException fe)
				{
					DebriefPlugin
							.logError(
									Status.INFO,
									"Ongoing problem related to counting lines in REP file, the counter isn't receiving sufficient file-path to open the file.",
									fe);
				}
				catch (IOException e)
				{
					DebriefPlugin.logError(Status.ERROR,
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
	 * 
	 * @see
	 * org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
	 * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
	 */
	public void loadFile(final PlotEditor thePlot, final InputStream inputStream,
			final String fileName)
	{

		// org.eclipse.ui.part.FileEditorInput ife =
		// (org.eclipse.ui.part.FileEditorInput) input;
		// final IFile _theFile = ife.getFile();
		// String theName = _theFile.getName();

		// final String thePath = _theFile.getFullPath().toOSString();
		// IPath iPath = _theFile.getFullPath();

		DebriefPlugin.logError(Status.INFO,
				"About to load REPLAY file:" + fileName, null);
		final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

		try
		{
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
						doTheLoad(fileName, fileName, theLayers, inputStream);

						// and inform the plot editor
						thePlot.loadingComplete(this);
					}
					catch (RuntimeException e)
					{
						e.printStackTrace();
						DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
								+ fileName, e);
					}
					finally
					{
						// ok, allow the layers object to inform anybody what's happening
						// again
						theLayers.suspendFiringExtended(false);

						// and close the stream, just to be tidy.
						if (inputStream != null)
						{
							try
							{
								inputStream.close();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}

						// and trigger an update ourselves
						// theLayers.fireExtended();
					}
				}
			});
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
		// ok, load the data...
		DebriefPlugin
				.logError(Status.INFO, "Successfully loaded REPLAY file", null);
	}
}
