/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.ScalePainter;

/**
 * @author ian.mayo
 *
 */
public class InsertScale extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return new ScalePainter();
	}

}
