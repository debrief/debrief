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
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;

import Debrief.Wrappers.Track.LightweightTrack.PaintOptions;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Properties.DebriefColors;

public final class LightweightTrackFolder extends BaseLayer implements
    PaintOptions
{

  // ////////////////////////////////////////////////////
  // bean info for this class
  // ///////////////////////////////////////////////////
  public class LightweightTrackInfo extends Editable.EditorType
  {

    public LightweightTrackInfo(final LightweightTrackFolder data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
            {prop("Visible", "the Layer visibility", VISIBILITY),
                prop("Name", "the name of the track", FORMAT),
                prop("ShowName", "show the name of the track", FORMAT),
                prop("Color", "color of the track", FORMAT)};

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
  
  

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  @Override
  public EditorType getInfo()
  {
    return new LightweightTrackInfo(this);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Color _color = DebriefColors.RED;
  private boolean _showName = true;

  public LightweightTrackFolder(String name)
  {
    setName(name);
  }

  public void add(LightweightTrack track)
  {
    super.add(track);
    
    track.setParent(this);
  }

  @Override
  public synchronized void paint(CanvasType dest)
  {
    Enumeration<Editable> ele = super.elements();
    while (ele.hasMoreElements())
    {
      LightweightTrack track = (LightweightTrack) ele.nextElement();
      track.paint(dest, this);
    }
  }

  public LightweightTrack find(String theTrackName)
  {
    Enumeration<Editable> iter = elements();
    while (iter.hasMoreElements())
    {
      Editable next = iter.nextElement();
      final String name = next.getName();
      if (name != null && name.equals(theTrackName))
      {
        return (LightweightTrack) next;
      }
    }
    return null;
  }

  @Override
  public Color getColor()
  {
    return _color;
  }

  @Override
  public boolean showName()
  {
    return _showName;
  }

  public void setColor(Color _color)
  {
    this._color = _color;
  }

  public boolean isShowName()
  {
    return _showName;
  }

  public void setShowName(boolean _showName)
  {
    this._showName = _showName;
  }
}