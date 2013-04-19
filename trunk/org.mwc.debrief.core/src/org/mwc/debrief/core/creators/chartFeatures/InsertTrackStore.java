/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import Debrief.Wrappers.TrackStoreWrapper;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;

/**
 * @author ian.mayo
 * 
 */
public class InsertTrackStore extends CoreInsertChartFeature
{

	public InsertTrackStore()
	{
		// tell our parent that we want to be inserted as a top-level layer
		super(true);
	}

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		return new TrackStoreWrapper();
	}

}
