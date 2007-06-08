/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;

import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldArea;

/**
 * @author ian.mayo
 */
public class ZoomOut extends CoreEditorAction
{
	protected void execute()
	{
		PlainChart theChart = getChart();

		WorldArea oldArea = theChart.getDataArea();
		Action theAction = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAction(theChart,
				oldArea, 2.0);

		// and wrap it
		DebriefActionWrapper daw = new DebriefActionWrapper(theAction, theChart.getLayers());

		// and add it to the clipboard
		CorePlugin.run(daw);
	}
}