/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotDragMode;

/**
 * @author ian.mayo
 */
public class Pan extends CoreDragAction
{

	
	public static class PanMode implements SWTChart.PlotDragMode
	{
		
	}
	

	public PlotDragMode getDragMode()
	{
		return new PanMode();
	}
}