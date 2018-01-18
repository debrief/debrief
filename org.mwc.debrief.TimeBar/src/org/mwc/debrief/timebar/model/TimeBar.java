/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;

public class TimeBar implements IEventEntry
{
  /** TimeBar start */
  Calendar _start = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"),
      Locale.UK);
  /** TimeBar end */
  Calendar _end = Calendar
      .getInstance(TimeZone.getTimeZone("GMT+0"), Locale.UK);
  /** TimeBar caption */
  String _eventName;
  Color _color = null;

  Object _source;
  List<IEventEntry> _children = new ArrayList<IEventEntry>();

  protected TimeBar(final BaseLayer tacticalItems, boolean collapseChildren)
  {
    // _start.setTime(bar.getStartDTG().getDate());
    _eventName = tacticalItems.getName();
    _source = tacticalItems;

    final Enumeration<Editable> enumer = tacticalItems.elements();
    TimePeriod coverage = null;
    while (enumer.hasMoreElements())
    {
      final Editable sensor = enumer.nextElement();
      if (sensor instanceof TacticalDataWrapper)
      {
        final TacticalDataWrapper item = (TacticalDataWrapper) sensor;

        if (item.getStartDTG() != null && item.getEndDTG() != null)
        {
          if(!collapseChildren)
          {
            _children.add(new TimeBar((TacticalDataWrapper) sensor));
          }
          final TimePeriod thisP =
              new TimePeriod.BaseTimePeriod(item.getStartDTG(), item
                  .getEndDTG());

          if (coverage == null)
          {
            coverage = thisP;
          }
          else
          {
            coverage.extend(thisP.getStartDTG());
            coverage.extend(thisP.getEndDTG());
          }
        }
      }
    }

    _start.setTimeInMillis(coverage.getStartDTG().getDate().getTime());
    _end.setTimeInMillis(coverage.getEndDTG().getDate().getTime());

  }

  public TimeBar(final ContributionWrapper contribution)
  {
    _eventName = contribution.getName();
    _start.setTime(contribution.get_Start().getDate());
    _end.setTime(contribution.getEnd().getDate());
    _source = contribution;
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

  public TimeBar(final SATC_Solution solution)
  {
    _eventName = "Solution";
    _start.setTime(solution.getStartDTG().getDate());
    _end.setTime(solution.getEndDTG().getDate());
    _source = solution;

    final Enumeration<Editable> numer = solution.elements();
    while (numer.hasMoreElements())
    {
      final Editable next = numer.nextElement();
      if (next instanceof ContributionWrapper)
      {
        // does it have a date component?
        final ContributionWrapper cw = (ContributionWrapper) next;
        if (cw.get_Start() != null)
        {
          _children.add(new TimeBar(cw));
        }
      }
    }
  }

  public TimeBar(final SegmentList segments, final TimeBarPrefs prefs)
  {
    _source = segments;
    _eventName = segments.getName();
    final HiResDate startDate = segments.getWrapper().getStartDTG();
    if (startDate != null)
    {
      _start.setTime(startDate.getDate());
    }
    final HiResDate endDate = segments.getWrapper().getEndDTG();
    if (endDate != null)
    {
      _end.setTime(endDate.getDate());
    }
    _color = segments.getWrapper().getColor();

    // also work through the segments, if there's more than one
    if (segments.size() > 1)
    {
      if (!prefs.collapseSegments())
      {
        final Enumeration<Editable> elems = segments.elements();
        while (elems.hasMoreElements())
        {
          final TrackSegment thisE = (TrackSegment) elems.nextElement();
          _children.add(new TimeBar(thisE));
        }
      }
    }
  }

  public TimeBar(final TacticalDataWrapper sensorOrSolution)
  {
    _source = sensorOrSolution;
    _eventName = sensorOrSolution.getName();
    _color = sensorOrSolution.getColor();
    final HiResDate startDate = sensorOrSolution.getStartDTG();
    if (startDate != null)
    {
      _start.setTime(startDate.getDate());
    }
    final HiResDate endDate = sensorOrSolution.getEndDTG();
    if (endDate != null)
    {
      _end.setTime(endDate.getDate());
    }
  }

  public TimeBar(final TrackSegment thisE)
  {
    _source = thisE;
    _eventName = thisE.getName();
    final HiResDate startDate = thisE.startDTG();
    if (startDate != null)
    {
      _start.setTime(startDate.getDate());
    }
    final HiResDate endDate = thisE.endDTG();
    if (endDate != null)
    {
      _end.setTime(endDate.getDate());
    }

    if (thisE.elements().hasMoreElements())
    {
      final FixWrapper firstF = (FixWrapper) thisE.elements().nextElement();
      _color = firstF.getColor();
    }
    else
    {
      _color = Color.green;
    }
  }

  public TimeBar(final TrackWrapper track, TimeBarPrefs prefs)
  {
    this((WatchableList) track);
    final SegmentList segments = track.getSegments();
    _children.add(new TimeBar(segments, prefs));

    final BaseLayer theSensors = track.getSensors();
    if (theSensors != null && theSensors.size() > 0)
    {
      _children.add(new TimeBar(theSensors, prefs.collapseSensors()));
    }

    final BaseLayer theSolutions = track.getSolutions();
    if (theSolutions != null && theSolutions.size() > 0)
    {
      _children.add(new TimeBar(theSolutions, false));
    }
  }

  public TimeBar(final WatchableList bar)
  {
    // _start.setTime(bar.getStartDTG().getDate());
    _start.setTime(bar.getStartDTG().getDate());
    _end.setTime(bar.getEndDTG().getDate());
    _eventName = bar.getName();
    _source = bar;
    _color = bar.getColor();
  }

  @Override
  public List<IEventEntry> getChildren()
  {
    return _children;
  }

  @Override
  public org.eclipse.swt.graphics.Color getColor()
  {
    if (_color != null)
    {
      return ColorUtils.convertAWTtoSWTColor(_color);
    }
    return null;
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
  public String getToolTipText()
  {
    return "";
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
}
