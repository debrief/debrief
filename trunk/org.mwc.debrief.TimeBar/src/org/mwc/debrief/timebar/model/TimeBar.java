package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.BaseLayer;
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
	
	Object _source;
	List<IEventEntry> _children = new ArrayList<IEventEntry>();
	
	public TimeBar(WatchableList bar)
	{
		_start.setTime(bar.getStartDTG().getDate());
		_end.setTime(bar.getEndDTG().getDate());
		_eventName = bar.getName();
		_source = bar;
		_color = bar.getColor();
		System.out.println(bar);
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
	
	public TimeBar(TrackWrapper track)
	{
		_source = track;
		SegmentList segments = track.getSegments();		
		_eventName = track.getName();
		HiResDate startDate = segments.getWrapper().getStartDTG(); 
		if( startDate != null)
			_start.setTime(startDate.getDate());
		HiResDate endDate = segments.getWrapper().getEndDTG(); 
		if( endDate != null)
			_end.setTime(endDate.getDate());
		_color = track.getColor();
		
		BaseLayer sensors = track.getSensors();
		Enumeration<Editable> enumer = sensors.elements();
		while(enumer.hasMoreElements())
		{
			Editable sensor = enumer.nextElement();
			if (sensor instanceof TacticalDataWrapper)
				_children.add(new TimeBar((TacticalDataWrapper) sensor));
		}
		
		BaseLayer solutions = track.getSolutions();
		enumer = solutions.elements();
		while(enumer.hasMoreElements())
		{
			Editable solution = enumer.nextElement();
			if (solution instanceof TacticalDataWrapper)
				_children.add(new TimeBar((TacticalDataWrapper) solution));
		}
	}
	
//	public TimeBar(SegmentList segments)
//	{
//		_source = segments;
//		_eventName = segments.getName();
//		HiResDate startDate = segments.getWrapper().getStartDTG(); 
//		if( startDate != null)
//			_start.setTime(startDate.getDate());
//		HiResDate endDate = segments.getWrapper().getEndDTG(); 
//		if( endDate != null)
//			_end.setTime(endDate.getDate());
//		_color = segments.getWrapper().getColor();
//	}


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
	public List<IEventEntry> getChildren() 
	{
		return _children;
	}

	@Override
	public String getToolTipText() 
	{
		return "";
	}
}
