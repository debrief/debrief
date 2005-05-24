/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 *
 */
public class Redraw extends CoreEditorAction
{
	protected void run()
	{
		PlainChart theChart = getChart();
		PlainProjection proj = theChart.getCanvas().getProjection();
		redrawChart();

	}
}