/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.debrief.core.CorePlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.eclipse.core.resources.IFile; 
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;


import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 *
 */
public class ReplayLoader extends IPlotLoader.BaseLoader
{

	/* (non-Javadoc)
	 * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer.editors.PlotEditor, org.eclipse.ui.IEditorInput)
	 */
	public void loadFile(PlotEditor thePlot, IEditorInput input)
	{
		String source = super.getFileName(input);
		
		Object theInput = input;
		
		Class inputClass = theInput.getClass();


		if(input instanceof org.eclipse.ui.part.FileEditorInput)
		{
			org.eclipse.ui.part.FileEditorInput ife = (org.eclipse.ui.part.FileEditorInput) input;
			final IFile _theFile = ife.getFile();
			String theName = _theFile.getName();
			
			String thePath = _theFile.getFullPath().toOSString();
			IPath iPath = _theFile.getFullPath();

			CorePlugin.logError(Status.INFO, "About to load REPLAY file:" + theName, null);
			
			try
			{
				// stick it in a stream
				final InputStream is = _theFile.getContents();
				final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);
				ImportReplay importer = new Debrief.ReaderWriter.Replay.ImportReplay()
				{
					// override the count-lines method.  We may only have a project-relative
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
							CorePlugin.logError(Status.INFO, "Replay loader - counted:" + lines + " lines", null);
						}
						catch (IOException e)
						{
							CorePlugin.logError(Status.ERROR, "Failed to open stream for counting lines:" + fName, null);
							e.printStackTrace();
						}
						catch (CoreException e)
						{
							CorePlugin.logError(Status.ERROR, "Failed to open stream for counting lines:" + fName, null);
							e.printStackTrace();
						}
						return lines;
					}
					
				};

				// and do the import... 
				importer.importThis(thePath, is, theLayers);
			}
			catch (CoreException e)
			{
				CorePlugin.logError(org.eclipse.core.runtime.Status.ERROR,
					"Unable to open REP file for input:" + theName, e	);
			}
		
			
		}
		// ok, load the data...
		CorePlugin.logError(Status.INFO, "Successfully loaded REPLAY file", null);
	}
}
