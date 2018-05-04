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
package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottables;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;

public final class LightweightTrack extends Plottables implements WatchableList
{
  private Color _customColor = null;
  private LightweightTrackFolder _parent;

  @Override
  public EditorType getInfo()
  {
    return new LightweightInfo(this);
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  public class LightweightInfo extends Editable.EditorType
  {

    public LightweightInfo(final LightweightTrack data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
            {prop("Visible", "the Layer visibility", VISIBILITY),
                prop("CustomColor", "a custom color for this track", FORMAT),
                prop("Name", "the name of the track", FORMAT)};

        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public LightweightTrack(String name)
  {
    setName(name);
  }

  public void add(FixWrapper fix)
  {
    super.add(fix);
  }

  @Override
  public synchronized void paint(CanvasType dest)
  {
    // should not be called, we get called by parent object, which controls
    // formatting
    throw new IllegalArgumentException(
        "Should not be called, track folder  should control paint");
  }

  public static interface PaintOptions
  {
    public Color getColor();

    public boolean showName();
  }

  public Color getCustomColor()
  {
    return _customColor;
  }

  public void setCustomColor(Color _customColor)
  {
    this._customColor = _customColor;
  }

  public synchronized void paint(CanvasType dest, PaintOptions options)
  {
    if (!getVisible())
    {
      return;
    }

    final Color myColor =
        _customColor == null ? options.getColor() : _customColor;

    dest.setColor(myColor);

    final int len = super.size();
    Enumeration<Editable> iter = super.elements();
    int ctr = 0;
    final int[] xPoints = new int[len];
    final int[] yPoints = new int[len];

    WorldLocation firstLoc = null;

    // build up polyline
    while (iter.hasMoreElements())
    {
      FixWrapper fw = (FixWrapper) iter.nextElement();
      if (firstLoc == null)
      {
        firstLoc = fw.getLocation();
      }
      Point loc = dest.toScreen(fw.getLocation());
      xPoints[ctr] = (int) loc.getX();
      yPoints[ctr] = (int) loc.getY();
      ctr++;
    }

    // draw the line
    dest.drawPolyline(xPoints, yPoints, len);

    // and the track name?
    if (options.showName() && firstLoc != null)
    {
      Point loc = dest.toScreen(firstLoc);
      dest.drawText(getName(), loc.x + 5, loc.y);
    }
  }

  public void setParent(LightweightTrackFolder parent)
  {
    _parent = parent;
  }

  public LightweightTrackFolder getParent()
  {
    return _parent;
  }

  @Override
  public Color getColor()
  {
    return getCustomColor();
  }

  @Override
  public HiResDate getStartDTG()
  {
    return ((FixWrapper)first()).getDTG();
  }

  @Override
  public HiResDate getEndDTG()
  {
    return ((FixWrapper)last()).getDTG();
  }

  @Override
  public Watchable[] getNearestTo(HiResDate DTG)
  {
    final long dtg = DTG.getDate().getTime();
    Iterator<FixWrapper> fIter = getIter();
    FixWrapper nearest = null;

    FixWrapper myFirst = (FixWrapper) first();
    FixWrapper myLast = (FixWrapper) last();

    if (DTG.lessThan(myFirst.getDTG()) || DTG.greaterThan(myLast.getDTG()))
    {
      nearest = null;
    }
    else
    {
      long distance = Long.MAX_VALUE;
      while (fIter.hasNext())
      {
        FixWrapper fix = fIter.next();
        final long dist =
            Math.abs(fix.getDateTimeGroup().getDate().getTime() - dtg);
        if (dist < distance)
        {
          nearest = fix;
          distance = dist;
        }
      }
    }

    return new Watchable[]{nearest};
  }

  private Iterator<FixWrapper> getIter()
  {
    final Enumeration<Editable> enumer = elements();
    return new Iterator<FixWrapper>()
    {

      @Override
      public boolean hasNext()
      {
        return enumer.hasMoreElements();
      }

      @Override
      public FixWrapper next()
      {
        return (FixWrapper) enumer.nextElement();
      }

      @Override
      public void remove()
      {
      }
    };
  }

  @Override
  public void filterListTo(HiResDate start, HiResDate end)
  {
    TimePeriod period = new TimePeriod.BaseTimePeriod(start, end);
    Iterator<FixWrapper> fIter = getIter();
    while (fIter.hasNext())
    {
      FixWrapper fix = fIter.next();
      if (period.contains(fix.getDateTimeGroup()))
      {
        fix.setVisible(true);
      }
      else
      {
        fix.setVisible(false);
      }
    }
  }

  @Override
  public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
  {
    TimePeriod period = new TimePeriod.BaseTimePeriod(start, end);
    Collection<Editable> items = new Vector<Editable>();
    Enumeration<Editable> iter = elements();
    while (iter.hasMoreElements())
    {
      FixWrapper fix = (FixWrapper) iter.nextElement();
      if (period.contains(fix.getDateTimeGroup()))
      {
        items.add(fix);
      }
    }
    return items;
  }

  @Override
  public PlainSymbol getSnailShape()
  {
    return null;
  }

}