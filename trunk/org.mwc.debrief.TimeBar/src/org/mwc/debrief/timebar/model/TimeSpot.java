package org.mwc.debrief.timebar.model;

import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;

import MWC.GenericData.Watchable;

//TODO: color
public class TimeSpot implements IChartItemDrawable
{
	Object _source;
	String _name;
	Calendar _time = Calendar.getInstance();
	GanttCheckpoint _presentation;
	
	
	public TimeSpot(Watchable spot)
	{
		_source = spot;
		_name = spot.getName();
		_time.setTime(spot.getTime().getDate());
	}

	@Override
	public void draw(GanttChart chart) 
	{
		_presentation = new GanttCheckpoint(chart, _name, _time);		
	}

	@Override
	public Object getSource() 
	{
		return _source;
	}

	@Override
	public GanttEvent getPresentation() 
	{
		return _presentation;
	}
}
