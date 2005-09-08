/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Tools.Palette.CreateTOPO;

/**
 * @author ian.mayo
 *
 */
public class InsertETOPO extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return CreateTOPO.load2MinBathyData();
	}

}
