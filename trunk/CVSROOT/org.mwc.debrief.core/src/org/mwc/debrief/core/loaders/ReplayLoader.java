/**
 * 
 */
package org.mwc.debrief.core.loaders;

import org.eclipse.ui.IEditorInput;
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.debrief.core.interfaces.IPlotLoader;

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
		
		// ok, load the data...
		System.out.println(getName() + " LOADER: loading data");
		
	}
}
