package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;

import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;

public class TimeBar implements IChartItemDrawable 
{
	/** TimeBar start */
	Calendar _start = Calendar.getInstance();
	/** TimeBar end */
	Calendar _end = Calendar.getInstance();
	/** TimeBar caption */
	String _eventName;	
	Color _color = null;
	
	Object _source;
	GanttEvent _presentation;
	
	public TimeBar(WatchableList bar)
	{
		//TODO: what if times are not specified?
		if (bar.getStartDTG() != null)
			_start.setTime(bar.getStartDTG().getDate());
		if(bar.getEndDTG() != null)
			_end.setTime(bar.getEndDTG().getDate());
		_eventName = bar.getName();
		_source = bar;
		_color = bar.getColor();
	}
	
	public TimeBar(TacticalDataWrapper sensorOrSolution)
	{
		_source = sensorOrSolution;
		_eventName = sensorOrSolution.getName();
		HiResDate startDate = sensorOrSolution.getStartDTG(); 
		if( startDate != null)
			_start.setTime(startDate.getDate());
		HiResDate endDate = sensorOrSolution.getEndDTG(); 
		if( endDate != null)
			_end.setTime(endDate.getDate());
	}	
	
	public TimeBar(SegmentList segments)
	{
		_source = segments;
		_eventName = segments.getName();
		HiResDate startDate = segments.getWrapper().getStartDTG(); 
		if( startDate != null)
			_start.setTime(startDate.getDate());
		HiResDate endDate = segments.getWrapper().getEndDTG(); 
		if( endDate != null)
			_end.setTime(endDate.getDate());
		_color = segments.getWrapper().getColor();
	}

	@Override
	public void draw(GanttChart chart) 
	{
		_presentation = new GanttEvent(chart, _eventName, 
				_start, _end, 0 /* percentage complete */);
		if(_color != null)
			_presentation.setStatusColor(ColorUtils.convertAWTtoSWTColor(_color));
	}

	public Object getSource() 
	{
		return _source;
	}
	
	public GanttEvent getPresentation()
	{
		return _presentation;
	}
}
