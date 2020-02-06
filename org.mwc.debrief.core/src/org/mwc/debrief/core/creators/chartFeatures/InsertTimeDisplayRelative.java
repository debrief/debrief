
package org.mwc.debrief.core.creators.chartFeatures;

import org.eclipse.core.runtime.IAdaptable;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.DynamicLayer;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.TimeProvider;

/**
 * @author snpe
 *
 */
public class InsertTimeDisplayRelative extends CoreInsertChartFeature
{

	public InsertTimeDisplayRelative()
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
		TimeDisplayPainter plottable = new TimeDisplayPainter();
		plottable.setAbsolute(false);
		plottable.setName(TimeDisplayPainter.TIME_DISPLAY_RELATIVE);
		plottable.setFormat(TimeDisplayPainter.RELATIVE_DEFAULT_FORMAT);
		
		// can we find the current time?
		IChartBasedEditor chartObject = getEditor();
		
		// is the chart an editable object? (hopefully it's our plot)
		if(chartObject instanceof IAdaptable)
		{
			IAdaptable adapt = (IAdaptable) chartObject;
			TimeProvider prov = (TimeProvider) adapt.getAdapter(TimeProvider.class);
			
			// does it have a time provider?
			if(prov != null)
			{
				//yes - retrieve the time
				HiResDate tNow = prov.getTime();
				plottable.setOrigin(tNow);
			}
		}
		
		return plottable;
	}
}
