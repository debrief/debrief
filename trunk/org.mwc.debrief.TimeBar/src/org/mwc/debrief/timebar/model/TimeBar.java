package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;

public class TimeBar implements IEventEntry 
{
	/** TimeBar start */
	Calendar _start = Calendar.getInstance();
	/** TimeBar end */
	Calendar _end = Calendar.getInstance();
	/** TimeBar caption */
	String _eventName;	
	Color _color = null;
	boolean _isBold = false;
	
	Object _source;
	List<IEventEntry> _children = new ArrayList<IEventEntry>();
	
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
		_isBold = true;
	}
	
	public TimeBar(NarrativeWrapper narrative)
	{
		_eventName = "Narratives";
		_start.setTime(narrative.getTimePeriod().getStartDTG().getDate());
		_end.setTime(narrative.getTimePeriod().getEndDTG().getDate());
		_source = narrative;
		
		Enumeration<Editable> numer = narrative.elements();
		while (numer.hasMoreElements())
		{
			Editable next = numer.nextElement();
			if (next instanceof NarrativeEntry)
			{
				_children.add(new TimeSpot((NarrativeEntry) next));
			}
		}
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


	public Object getSource() 
	{
		return _source;
	}
	

	@Override
	public Calendar getStart() 
	{
		return _start;
	}

	@Override
	public Calendar getEnd() 
	{
		return _end;
	}

	@Override
	public String getName() 
	{
		return _eventName;
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
	public boolean isBoldText() 
	{
		return _isBold;
	}

	@Override
	public List<IEventEntry> getChildren() 
	{
		return _children;
	}
}
