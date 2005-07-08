/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Tools.Palette.CreateVPFLayers;

/**
 * @author ian.mayo
 *
 */
public class InsertVPFLayers extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return CreateVPFLayers.createMyLibrary(true);
	}

}
