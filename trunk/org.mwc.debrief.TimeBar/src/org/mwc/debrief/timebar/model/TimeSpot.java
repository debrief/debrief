package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;

import MWC.GUI.Plottable;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;

public class TimeSpot implements IEventEntry
{
	Object _source;
	String _name;
	Calendar _time = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.UK);
	GanttCheckpoint _presentation;
	Color _color = null;
	
	public TimeSpot(WatchableList spot)
	{
		_source = spot;
		_name = spot.getName();
		_time.setTime(spot.getStartDTG().getDate());
		_color = spot.getColor();
	}
	
	public TimeSpot(Watchable spot)
	{
		_source = spot;
		_name = spot.getName();
		_time.setTime(spot.getTime().getDate());
		_color = spot.getColor();
	}
	
	public TimeSpot(NarrativeEntry entry)
	{
		_source = entry;
		_name = entry.getName();
		_time.setTime(entry.getDTG().getDate());
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
	
	@Override
	public boolean isVisible() 
	{
		if (getSource() instanceof Plottable)
		{
			return ((Plottable) getSource()).getVisible();			
		}
		return true;
	}

	@Override
	public List<IEventEntry> getChildren() 
	{
		return null;
	}
	
	@Override
	public String getToolTipText() 
	{
		if (_source instanceof NarrativeEntry)
		{
			StringBuffer res = new StringBuffer();
			NarrativeEntry entry = (NarrativeEntry) _source;
			res.append(entry.getDTGString());
			res.append("\n");
			if (entry.getSource() != null)
			{
				res.append(entry.getSource());
				res.append("\n");
			}
			if (entry.getType() != null)
			{
				res.append(entry.getType());
				res.append("\n");
			}
			res.append(entry.getEntry());
			return res.toString();
		}
		return "";
	}

}
