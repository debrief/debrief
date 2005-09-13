/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreDragAction.SwitchModeAction;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotDragMode;

import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldArea;

/**
 * @author ian.mayo
 */
public class RangeBearing extends CoreDragAction
{

	
	public static class RangeBearingMode implements SWTChart.PlotDragMode
	{
		
	}

	public PlotDragMode getDragMode()
	{
		return new RangeBearingMode();
	}
}