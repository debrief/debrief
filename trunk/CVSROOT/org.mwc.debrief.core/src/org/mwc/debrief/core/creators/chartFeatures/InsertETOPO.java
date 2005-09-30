/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Palette.*;

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

	

	public Action getData(PlainChart theChart)
	{
		Action res = null;

		// create the shape, based on the centre
		Layer myLayer = (Layer) getPlottable(theChart);

		// lastly, get the data
		Layers theData = theChart.getLayers();

		// and put it into an action (so we can undo it)
		res = new PlainCreate.CreateLayerAction(null, myLayer, theData);

		return res;
	}	
}
