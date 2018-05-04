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

import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;

import Debrief.Wrappers.Track.LightweightTrack.PaintOptions;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;

public final class LightweightTrackFolder extends BaseLayer
{

  // ////////////////////////////////////////////////////
  // bean info for this class
  // ///////////////////////////////////////////////////
  public class LightweightTrackInfo extends Editable.EditorType
  {

    public LightweightTrackInfo(final BaseLayer data)
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
  private final PaintOptions _options = new PaintOptions();

  public LightweightTrackFolder(String name)
  {
    setName(name);
  }

  public void add(LightweightTrack track)
  {
    super.add(track);
  }
  
  @Override
  public synchronized void paint(CanvasType dest)
  {
    Enumeration<Editable> ele = super.elements();
    while(ele.hasMoreElements())
    {
      LightweightTrack track = (LightweightTrack) ele.nextElement();
      track.paint(dest, _options);
    }
  }

  public LightweightTrack find(String theTrackName)
  {
    
    LightweightTrack res = null;
    Enumeration<Editable> iter = elements();
    while(iter.hasMoreElements())
    {
      
    }
    return res ;
  }
}