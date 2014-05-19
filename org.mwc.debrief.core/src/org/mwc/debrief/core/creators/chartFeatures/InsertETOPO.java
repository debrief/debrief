/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Tools.Palette.CreateTOPO;

/**
 * @author ian.mayo
 *
 */
public class InsertETOPO extends CoreInsertChartFeature
{
	
	public InsertETOPO()
	{
		// tell our parent that we want to be inserted as a top-level layer
		super(true);
	}

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
		return CreateTOPO.load2MinBathyData();
	}

}
