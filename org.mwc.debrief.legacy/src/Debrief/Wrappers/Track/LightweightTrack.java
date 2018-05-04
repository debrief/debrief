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
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottables;
import MWC.GUI.Properties.DebriefColors;

public final class LightweightTrack extends Plottables
{
  private Color _customColor = null;

  public class LightweightInfo extends Editable.EditorType
  {

    public LightweightInfo(final BaseLayer data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
            {prop("Visible", "the Layer visibility", VISIBILITY),
                prop("Name", "the name of the track", FORMAT)};

        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

    @SuppressWarnings("rawtypes")
    public MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class c = BaseLayer.class;
      final MethodDescriptor mds[] =
          {method(c, "exportShape", null, "Export Shape"),
              method(c, "hideChildren", null, "Hide all children"),
              method(c, "revealChildren", null, "Reveal all children")};
      return mds;
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

  public static class PaintOptions
  {
    private Color _color = DebriefColors.RED;

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
    final Color myColor = _customColor == null ? options._color : _customColor;
    
    dest.setColor(myColor);
    
    final int len = super.size();
    Enumeration<Editable> iter = super.elements();
    int ctr = 0;
    final int[] xPoints = new int[len];
    final int[] yPoints = new int[len];
    
    // build up polyline
    while(iter.hasMoreElements())
    {
      FixWrapper fw = (FixWrapper) iter.nextElement();
      Point loc = dest.toScreen(fw.getLocation());
      xPoints[ctr] = (int) loc.getX();
      yPoints[ctr] = (int) loc.getY();
    }
    
    // draw the line
    dest.drawPolyline(xPoints, yPoints, len);
  }

}