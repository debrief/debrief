package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;

import MWC.GenericData.Watchable;

public class TimeSpot implements IEventEntry
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
	public Object getSource() 
	{
		return _source;
	}
	
	@Override
	public Calendar getStart() 
	{
		return _time;
	}

	@Override
	public Calendar getEnd() 
	{
		return _time;
	}

	@Override
	public String getName() 
	{
		return _name;
	}

	@Override
	public org.eclipse.swt.graphics.Color getColor() 
	{
		if(_color != null)
			return ColorUtils.convertAWTtoSWTColor(_color);
		return null;
	}

}
