/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.Grid4WPainter;

/**
 * @author ian.mayo
 *
 */
public class Insert4WGrid extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return new Grid4WPainter(theChart.getDataArea().getCentreAtSurface());
	}

}
