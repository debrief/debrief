
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.DynamicLayer;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;

/**
 * @author snpe
 *
 */
public class InsertTimeDisplayAbsolute extends CoreInsertChartFeature
{

	public InsertTimeDisplayAbsolute()
	{
		super();
		setMultiple(true);
	}

	@Override
	public Layer getLayer()
	{
		return new DynamicLayer();
	}

	@Override
	protected String getLayerName()
	{
		return Layers.DYNAMIC_FEATURES;
	}

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
		return new TimeDisplayPainter();
	}
}
