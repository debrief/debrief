package org.mwc.debrief.track_shift.views;

import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;

public class FrequencyResidualsView extends BaseStackedDotsView
{
	public FrequencyResidualsView()
	{
		super(false, true);
	}

	protected String getUnits()
	{
		return "Hz";
	}
	
	protected String getType()
	{
		return "Frequency";
	}
	
	protected void updateData(boolean updateDoublets)
	{
		// update the current datasets
		_myHelper.updateFrequencyData(_dotPlot, _linePlot, _theTrackDataListener,
				_onlyVisible.isChecked(), _holder, this, updateDoublets);		
		
		// hide the line for the base freq dataset
		DefaultXYItemRenderer lineRend = (DefaultXYItemRenderer) super._linePlot.getRenderer();
		lineRend.setSeriesShapesVisible(3, false);

	}
}
