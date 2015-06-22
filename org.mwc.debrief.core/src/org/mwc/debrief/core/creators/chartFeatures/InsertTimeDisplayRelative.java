/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import org.eclipse.core.runtime.IAdaptable;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.DynamicLayer;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;
import MWC.GenericData.HiResDate;

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
