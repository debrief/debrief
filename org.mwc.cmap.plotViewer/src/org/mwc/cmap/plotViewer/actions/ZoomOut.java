
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
		final PlainChart theChart = getChart();

		// get the currently visible data area (which we will restore on undo
		final WorldArea oldArea = theChart.getCanvas().getProjection().getVisibleDataArea();

		// create the zoom out action
		final Action theAction = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAction(theChart,
				oldArea, 2.0);

		// and wrap it
		final DebriefActionWrapper daw = new DebriefActionWrapper(theAction, theChart.getLayers(), null);

		// and add it to the clipboard
		CorePlugin.run(daw);
	}
}