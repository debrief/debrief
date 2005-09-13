/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotDragMode;


/**
 * @author ian.mayo
 */
public class ZoomIn extends CoreDragAction
{

	public static class ZoomInMode implements SWTChart.PlotDragMode
	{
		
	}
	

	public PlotDragMode getDragMode()
	{
		// TODO Auto-generated method stub
		return new ZoomInMode();
	}
}