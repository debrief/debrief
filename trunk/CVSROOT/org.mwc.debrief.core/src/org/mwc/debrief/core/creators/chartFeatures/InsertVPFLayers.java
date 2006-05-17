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

	
	public InsertVPFLayers()
	{
		// tell our parent that we want to be inserted as a top-level layer
		super(false);
	}	
	
	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return CreateVPFLayers.createMyLibrary(true);
	}

}
