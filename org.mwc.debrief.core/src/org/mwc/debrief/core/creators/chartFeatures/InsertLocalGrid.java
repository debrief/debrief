/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.LocalGridPainter;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 *
 */
public class InsertLocalGrid extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
	  final WorldLocation theOrigin = new WorldLocation(theChart.getCanvas().getProjection().getVisibleDataArea().getCentre());
    final LocalGridPainter res = new LocalGridPainter();
    res.setOrigin(theOrigin);
    return res;
	}

}
