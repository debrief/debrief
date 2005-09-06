/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;

import MWC.GUI.*;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Tools.Chart.WriteMetafile;

/**
 * @author ian.mayo
 *
 */
public class ExportWMF extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/** ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_theParent = theParent;
	}
	
	
	/** and execute..
	 * 
	 *
	 */
	protected void run()
	{
		final PlainChart theChart = getChart();
		
		if(_theParent == null)
		{
			CorePlugin.logError(Status.ERROR, "Tool parent missing for Write Metafile", null);
			return;
		}
		
		WriteMetafile write = new WriteMetafile(_theParent, theChart, theChart.getLayers())
		{

			/**
			 * @param mf
			 */
			protected void paintToMetafile(MetafileCanvas mf)
			{
				SWTCanvas sc = (SWTCanvas) theChart.getCanvas();
		    sc.paintPlot(mf);
		  }
			
		};
		write.execute();
	}
	
}