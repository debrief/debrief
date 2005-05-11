/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.debrief.core.CorePlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.eclipse.core.resources.IFile; 
import org.eclipse.core.runtime.CoreException;


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

	//	if(input instanceof org.eclipse.ui.part.FileEditorInput)
//		{
//			org.eclipse.ui.part.FileEditorInput ife = (org.eclipse.ui.part.FileEditorInput) input;
//			IFile _theFile = ife.getFile();
//			String theName = _theFile.getName();
			
			String theName = "d:/dev/eclipse2/runtime-workspace/test_project/boat_file.rep";
		
			try
			{
				// stick it in a stream
//				InputStream is = _theFile.getContents();
				InputStream is = new FileInputStream(theName);
				Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);
				ImportReplay importer = new Debrief.ReaderWriter.Replay.ImportReplay();

				// and do the import... 
				importer.importThis(theName, is, theLayers);
			}
			catch (FileNotFoundException e)
			{
				CorePlugin.logError(org.eclipse.core.runtime.Status.ERROR,
					"Unable to open REP file for input:" + theName, e	);
			}
		
			
	//	}

		
		// ok, load the data...
		System.out.println(getName() + " LOADER: loading data");
		
	}
}
