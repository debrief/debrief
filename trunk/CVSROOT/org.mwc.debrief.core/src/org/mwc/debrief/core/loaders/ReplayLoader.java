/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.debrief.core.interfaces.IPlotLoader;

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

//		IFileEditorInput ife = null;
		final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

		Object importer = new Debrief.ReaderWriter.Replay.ImportReplay();		

		
		// ok, load the data...
		System.out.println(getName() + " LOADER: loading data");
		
	}
}
