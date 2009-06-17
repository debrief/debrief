package org.mwc.debrief.track_shift.views;

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
	}
}
