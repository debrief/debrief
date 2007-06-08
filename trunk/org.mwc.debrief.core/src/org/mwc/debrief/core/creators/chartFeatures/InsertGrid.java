/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.GridPainter;

/**
 * @author ian.mayo
 *
 */
public class InsertGrid extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return new GridPainter();
	}

}
