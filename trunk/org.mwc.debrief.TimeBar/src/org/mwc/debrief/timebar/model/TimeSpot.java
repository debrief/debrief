package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;

import MWC.GenericData.Watchable;

public class TimeSpot implements IChartItemDrawable
{
	Object _source;
	String _name;
	Calendar _time = Calendar.getInstance();
	GanttCheckpoint _presentation;
	Color _color = null;
	
	public TimeSpot(Watchable spot)
	{
		_source = spot;
		_name = spot.getName();
		_time.setTime(spot.getTime().getDate());
		_color = spot.getColor();
	}

	@Override
	public void draw(GanttChart chart) 
	{
		_presentation = new GanttCheckpoint(chart, _name, _time);	
		if(_color != null)
			_presentation.setStatusColor(ColorUtils.convertAWTtoSWTColor(_color));
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
