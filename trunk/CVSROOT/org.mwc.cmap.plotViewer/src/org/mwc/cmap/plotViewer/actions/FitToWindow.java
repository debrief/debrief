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
public class FitToWindow extends CoreEditorAction
{
	protected void run()
	{
		PlainChart theChart = getChart();
		theChart.rescale();
		redrawChart();
	}
}
