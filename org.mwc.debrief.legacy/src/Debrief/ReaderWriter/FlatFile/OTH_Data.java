/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.FlatFile;

import java.util.List;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;

public class OTH_Data
{
  private final List<TrackWrapper> _tracks;
  private final List<BaseLayer> _ellipseShapes;

  public OTH_Data(final List<TrackWrapper> tracks, final List<BaseLayer> ellipseShapes)
  {
    _tracks = tracks;
    _ellipseShapes = ellipseShapes;
  }

  public List<TrackWrapper> getTracks()
  {
    return _tracks;
  }
  
  public List<BaseLayer> getEllipseLayers()
  {
    return _ellipseShapes;
  }
}
