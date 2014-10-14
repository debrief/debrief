/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
	Calendar _start = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"), Locale.UK);
	/** TimeBar end */
	Calendar _end = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"), Locale.UK);
	/** TimeBar caption */
	String _eventName;	
	Color _color = null;
	
	Object _source;
	List<IEventEntry> _children = new ArrayList<IEventEntry>();
	
	public TimeBar(final WatchableList bar)
	{
		//_start.setTime(bar.getStartDTG().getDate());
		_start.setTimeInMillis(bar.getStartDTG().getMicros());
		_end.setTime(bar.getEndDTG().getDate());
		_eventName = bar.getName();
		_source = bar;
		_color = bar.getColor();
	}
	
	public TimeBar(final NarrativeWrapper narrative)
	{
		_eventName = "Narratives";
		_start.setTime(narrative.getTimePeriod().getStartDTG().getDate());
		_end.setTime(narrative.getTimePeriod().getEndDTG().getDate());
		_source = narrative;
		
		final Enumeration<Editable> numer = narrative.elements();
		while (numer.hasMoreElements())
		{
			final Editable next = numer.nextElement();
			if (next instanceof NarrativeEntry)
			{
				_children.add(new TimeSpot((NarrativeEntry) next));
			}
		}
	}
	
	public TimeBar(final TacticalDataWrapper sensorOrSolution)
	{
		_source = sensorOrSolution;
		_eventName = sensorOrSolution.getName();
		final HiResDate startDate = sensorOrSolution.getStartDTG(); 
		if( startDate != null)
			_start.setTime(startDate.getDate());
		final HiResDate endDate = sensorOrSolution.getEndDTG(); 
		if( endDate != null)
			_end.setTime(endDate.getDate());
	}	
	
	public TimeBar(final TrackWrapper track)
	{
		this((WatchableList) track);
		final SegmentList segments = track.getSegments();		
		_children.add(new TimeBar(segments));
		
		final BaseLayer sensors = track.getSensors();
		Enumeration<Editable> enumer = sensors.elements();
		while(enumer.hasMoreElements())
		{
			final Editable sensor = enumer.nextElement();
			if (sensor instanceof TacticalDataWrapper)
				_children.add(new TimeBar((TacticalDataWrapper) sensor));
		}
		
		final BaseLayer solutions = track.getSolutions();
		enumer = solutions.elements();
		while(enumer.hasMoreElements())
		{
			final Editable solution = enumer.nextElement();
			if (solution instanceof TacticalDataWrapper)
				_children.add(new TimeBar((TacticalDataWrapper) solution));
		}
	}
	
	public TimeBar(final SegmentList segments)
	{
		_source = segments;
		_eventName = segments.getName();
		final HiResDate startDate = segments.getWrapper().getStartDTG(); 
		if( startDate != null)
			_start.setTime(startDate.getDate());
		final HiResDate endDate = segments.getWrapper().getEndDTG(); 
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
