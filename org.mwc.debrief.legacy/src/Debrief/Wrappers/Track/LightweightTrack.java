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
import java.awt.Font;
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
import MWC.GUI.FireReformatted;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public final class LightweightTrack extends Plottables implements
    WatchableList, Plottable, FormattedTrack
{

  public class LightweightTrackInfo extends Editable.EditorType
  {
    public LightweightTrackInfo(final LightweightTrack data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Visible", "the Layer visibility", VISIBILITY), prop("Name",
            "the name of the track", FORMAT), prop("NameVisible",
                "show the name of the track", FORMAT), prop("Color",
                    "color of the track", FORMAT),
          displayExpertLongProp("LineStyle", "Line style",
              "the line style used to join track points", TEMPORAL,
              MWC.GUI.Properties.LineStylePropertyEditor.class)};

        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  private Color _color = null;

  @Override
  public EditorType getInfo()
  {
    return new LightweightTrackInfo(this);
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

 
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String _name;

  private boolean _visible;

  private TimePeriod _cachedPeriod;

  private long _timeCachedPeriodCalculated;

  private Font _trackFont;

  private boolean _nameVisible;

  private int _lineStyle;
  
  private WorldArea _bounds = null;

  public LightweightTrack(String name, boolean visible, boolean nameVisible, Color color, int lineStyle)
  {
    setName(name);
    
    setVisible(visible);
    setNameVisible(nameVisible);
    setColor(color);
    setLineStyle(lineStyle);    
  }

  public void setName(String name)
  {
    _name = name;
  }

  public Color getCustomColor()
  {
    return _color;
  }

  /**
   * Determine the time period for which we have visible locations
   * 
   * @return
   */
  public TimePeriod getVisiblePeriod()
  {
    // ok, have we determined the visible period recently?
    final long tNow = System.currentTimeMillis();

    // how long does the cached value remain valid for?
    final long ALLOWABLE_PERIOD = 500;
    if (_cachedPeriod != null && tNow
        - _timeCachedPeriodCalculated < ALLOWABLE_PERIOD)
    {
      // still in date, use the last calculated period
    }
    else
    {
      // ok, calculate a new one
      TimePeriod res = null;
      final Iterator<FixWrapper> pos = iterator();
      while (pos.hasNext())
      {
        final FixWrapper editable = (FixWrapper) pos.next();
        if (editable.getVisible())
        {
          final HiResDate thisT = editable.getTime();
          if (res == null)
          {
            res = new TimePeriod.BaseTimePeriod(thisT, thisT);
          }
          else
          {
            res.extend(thisT);
          }
        }
      }
      // ok, store the new time period
      _cachedPeriod = res;

      // remember when it was calculated
      _timeCachedPeriodCalculated = tNow;
    }

    return _cachedPeriod;
  }

  public Iterator<FixWrapper> iterator()
  {
    final Enumeration<Editable> ele = elements();
    return new Iterator<FixWrapper>() {

      @Override
      public boolean hasNext()
      {
        return ele.hasMoreElements();
      }

      @Override
      public FixWrapper next()
      {
        return (FixWrapper) ele.nextElement();
      }};
  }

  @FireReformatted
  public void setColor(Color color)
  {
    this._color = color;
  }

  public synchronized void paint(CanvasType dest)
  {
    if (!getVisible())
    {
      return;
    }

    final Color myColor = getColor();

    dest.setColor(myColor);

    final int len = super.size();
    Iterator<FixWrapper> iter = iterator();
    int ctr = 0;
    final int[] xPoints = new int[len];
    final int[] yPoints = new int[len];

    WorldLocation firstLoc = null;

    // build up polyline
    while (iter.hasNext())
    {
      FixWrapper fw = (FixWrapper) iter.next();
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
    if (getNameVisible() && firstLoc != null)
    {
      Point loc = dest.toScreen(firstLoc);
      dest.drawText(getName(), loc.x + 5, loc.y);
    }
  }

  @Override
  public Color getColor()
  {
    return getCustomColor();
  }

  @Override
  public HiResDate getStartDTG()
  {
    return ((FixWrapper) first()).getTime();
  }


  @Override
  public HiResDate getEndDTG()
  {
    return ((FixWrapper) last()).getTime();
  }

  @Override
  public Watchable[] getNearestTo(HiResDate DTG)
  {
    final long dtg = DTG.getDate().getTime();
    FixWrapper nearest = null;

    FixWrapper myFirst = (FixWrapper) first();
    FixWrapper myLast = (FixWrapper) last();

    if (DTG.lessThan(myFirst.getTime()) || DTG.greaterThan(myLast.getTime()))
    {
      nearest = null;
    }
    else
    {
      long distance = Long.MAX_VALUE;
      Iterator<FixWrapper> fIter = iterator();
      while (fIter.hasNext())
      {
        FixWrapper fix = fIter.next();
        final long dist = Math.abs(fix.getDateTimeGroup().getDate().getTime()
            - dtg);
        if (dist < distance)
        {
          nearest = fix;
          distance = dist;
        }
      }
    }

    return new Watchable[]
    {nearest};
  }

  @Override
  public void filterListTo(HiResDate start, HiResDate end)
  {
    TimePeriod period = new TimePeriod.BaseTimePeriod(start, end);
    Iterator<FixWrapper> fIter = iterator();
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
    Iterator<FixWrapper> iter = iterator();
    while (iter.hasNext())
    {
      FixWrapper fix = (FixWrapper) iter.next();
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

  public void add(FixWrapper e)
  {
    // forget the bounds
    _bounds = null;
    
     super.add(e);
  }

  @Override
  public String getName()
  {
    return _name;
  }
  
  public String toString()
  {
    return getName();
  }

  @Override
  public boolean getVisible()
  {
    return _visible;
  }

  @Override
  public WorldArea getBounds()
  {
    if(_bounds == null)
    {
      Iterator<FixWrapper> itera = iterator();
      while(itera.hasNext())
      {
        WorldLocation loc = itera.next().getLocation();
        if(_bounds == null)
        {
          _bounds = new WorldArea(loc, loc);
        }
        else
        {
          _bounds.extend(loc);
        }
      }
    }
    
    return _bounds;
  }

  @Override
  public int compareTo(Plottable o)
  {
    return this.getName().compareTo(o.getName());
  }

  @Override
  public void setVisible(boolean val)
  {
    _visible = val;
  }

  @Override
  public double rangeFrom(WorldLocation other)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getLineStyle()
  {
    return _lineStyle;
  }

  @FireReformatted
  public void setLineStyle(int style)
  {
    _lineStyle = style;
  }

  @Override
  public boolean getNameVisible()
  {
    return _nameVisible;
  }

  @FireReformatted
  public void setNameVisible(boolean visible)
  {
    _nameVisible = visible;
  }

  @Override
  public Font getTrackFont()
  {
    return _trackFont;
  }

  @FireReformatted
  public void setTrackFont(Font font)
  {
    _trackFont = font;
  }

  @Override
  public Enumeration<Editable> getPositionIterator()
  {
    return elements();
  }

  /**
   * get the set of fixes contained within this time period which haven't been filtered, and which
   * have valid depths. The isVisible flag indicates whether a track has been filtered or not. We
   * also have the getVisibleFixesBetween method (below) which decides if a fix is visible if it is
   * set to Visible, and it's label or symbol are visible.
   * <p/>
   * We don't have to worry about a valid depth, since 3d doesn't show points with invalid depth
   * values
   * 
   * @param start
   *          start DTG
   * @param end
   *          end DTG
   * @return series of fixes
   */
  public final Collection<Editable> getUnfilteredItems(final HiResDate start,
      final HiResDate end)
  {

    // see if we have _any_ points in range
    if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
    {
      return null;
    }

    if (this.getVisible() == false)
    {
      return null;
    }

    // get ready for the output
    final Vector<Editable> res = new Vector<Editable>(0, 1);

    // put the data into a period
    final TimePeriod thePeriod = new TimePeriod.BaseTimePeriod(start, end);

    // step through our fixes
    final Enumeration<Editable> iter = getPositionIterator();
    while (iter.hasMoreElements())
    {
      final FixWrapper fw = (FixWrapper) iter.nextElement();
      if (fw.getVisible())
      {
        // is it visible?
        if (thePeriod.contains(fw.getTime()))
        {
          // hey, it's valid - continue
          res.add(fw);
        }
      }
    }
    return res;
  }

}