package org.mwc.debrief.track_shift.views;

import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;

import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DatedToolTipGenerator;

public class FrequencyResidualsView extends BaseStackedDotsView
{
	protected String getUnits()
	{
		return "Hz";
	}
	
	protected String getType()
	{
		return "Frequency";
	}
	
	protected void updateData()
	{
		// update the current datasets
		_myHelper.updateFrequencyData(_dotPlot, _linePlot, _theTrackDataListener,
				_onlyVisible.isChecked(), _holder, this);		
		
		// hide the line for the base freq dataset
		DefaultXYItemRenderer lineRend = (DefaultXYItemRenderer) super._linePlot.getRenderer();
		lineRend.setSeriesShapesVisible(3, false);

	}
}
