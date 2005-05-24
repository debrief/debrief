/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 *
 */
public class ZoomOut extends CoreEditorAction
{
	protected void run()
	{
		PlainChart theChart = getChart();
		PlainProjection proj = theChart.getCanvas().getProjection();
		proj.zoom(2.0);
		redrawChart();

	}
}