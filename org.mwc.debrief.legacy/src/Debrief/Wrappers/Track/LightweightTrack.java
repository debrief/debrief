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
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottables;
import MWC.GenericData.WorldLocation;

public final class LightweightTrack extends Plottables
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

}