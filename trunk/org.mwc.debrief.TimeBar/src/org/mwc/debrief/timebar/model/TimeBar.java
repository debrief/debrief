package org.mwc.debrief.timebar.model;

import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;

import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.BaseLayer;
import MWC.GenericData.WatchableList;

//TODO: color
public class TimeBar implements IChartItemDrawable 
{
	/** TimeBar start */
	Calendar _start = Calendar.getInstance();
	/** TimeBar end */
	Calendar _end = Calendar.getInstance();
	/** TimeBar caption */
	String _eventName;
	
	Object _source;
	//TODO: create Painter class?
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
	}
	
	public TimeBar(BaseLayer solutions)
	{
		//TODO: implement
	}
	
	public TimeBar(SegmentList segements)
	{
		//TODO: implement
	}

	@Override
	public void draw(GanttChart chart) 
	{
		_presentation = new GanttEvent(chart, _eventName, 
				_start, _end, 0 /* percentage complete */);		
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
