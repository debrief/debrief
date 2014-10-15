/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
		final PlainChart theChart = getChart();

		final WorldArea oldArea = theChart.getDataArea();
		final Action theAction = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAction(theChart,
				oldArea, 2.0);

		// and wrap it
		final DebriefActionWrapper daw = new DebriefActionWrapper(theAction, theChart.getLayers(), null);

		// and add it to the clipboard
		CorePlugin.run(daw);
	}
}