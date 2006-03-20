/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;

import Debrief.Tools.Operations.View3dPlot;
import MWC.GUI.*;

/**
 * @author ian.mayo
 */
public class View3d extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * and execute..
	 */
	protected void execute()
	{
		
		try{
		final PlainChart theChart = getChart();
		Layers theLayers = theChart.getLayers();
		
		View3dPlot plotter = new View3dPlot(_theParent, null, theLayers, null);
		
		plotter.execute();
		}
		catch(NoClassDefFoundError err)
		{
			CorePlugin.showMessage("View 3d", "Debrief NGs 3d implementation invalid.  This is a known problem");
			CorePlugin.logError(Status.ERROR, "3d libraries not found", err);
		}
		
	}

}