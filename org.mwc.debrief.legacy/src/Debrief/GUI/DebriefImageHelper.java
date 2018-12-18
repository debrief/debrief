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
package Debrief.GUI;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.DynamicLayer;
import MWC.GUI.Editable;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GUI.Shapes.ChartFolio;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DebriefImageHelper 
{

  public String getImageFor(Editable editable)
  {
    String res = null;
    
    if (editable instanceof SensorWrapper)
      res = "icons/16/sensor.png";
    else if (editable instanceof ChartFolio)
      res = "icons/16/library.png";
    else if (editable instanceof ExternallyManagedDataLayer)
      res = "icons/16/map.png";
    else if (editable instanceof ChartBoundsWrapper)
      res = "icons/16/map.png";
    else if (editable instanceof SensorContactWrapper)
      res = "icons/16/sensor_contact.png";
    else if (editable instanceof NarrativeWrapper)
      res = "icons/16/narrative.png";
    else if (editable instanceof NarrativeEntry)
      res = "icons/16/narrative_entry.png";
    else if (editable instanceof RelativeTMASegment)
      res = "icons/16/tma_segment.png";
    else if (editable instanceof AbsoluteTMASegment)
      res = "icons/16/abs_tma_segment.png";
    else if (editable instanceof CoreTMASegment)
      res = "icons/16/tma_segment.png";
    else if (editable instanceof TrackSegment)
      res = "icons/16/track_segment.png";
    else if (editable instanceof FixWrapper)
      res = "icons/16/fix.png";
    else if (editable instanceof ShapeWrapper)
      res = "icons/16/shape.png";
    else if (editable instanceof PolygonNode)
      res = "icons/16/polygon.png";
    else if (editable instanceof LabelWrapper)
      res = "icons/16/shape.png";
    else if (editable instanceof DynamicLayer)
      res = "icons/16/clock.png";
    else if (editable instanceof TimeDisplayPainter)
    { 
      TimeDisplayPainter tdp = (TimeDisplayPainter) editable;
      if(tdp.isAbsolute())
        res = "icons/16/clock.png";
      else
        res = "icons/16/stopwatch.png";
    }
    else if (editable instanceof TrackWrapper)
    {
      // we're doing fancy testing here, so put it last in the list
      
      // see if it's a relative track
      TrackWrapper tw=  (TrackWrapper) editable;
      if(tw.isTMATrack())
        res = "icons/16/track_relative.png";
      else
        res = "icons/16/track.png";
    }
    else if (editable instanceof LightweightTrackWrapper)
    {
      res = "icons/16/light_track.png";      
    }    
    return res;
  }
}
  