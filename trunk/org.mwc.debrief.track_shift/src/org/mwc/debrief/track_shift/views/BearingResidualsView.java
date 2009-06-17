package org.mwc.debrief.track_shift.views;

public class BearingResidualsView extends BaseStackedDotsView
{
	protected String getUnits()
	{
		return "degs";
	}
	
	protected String getType()
	{
		return "Bearing";
	}
	
	protected void updateData()
	{
		// update the current datasets
		_myHelper.updateBearingData(_dotPlot, _linePlot, _theTrackDataListener,
				_onlyVisible.isChecked(), _holder, this);		
	}
}
