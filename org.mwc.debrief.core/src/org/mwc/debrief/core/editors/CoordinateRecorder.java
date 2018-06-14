package org.mwc.debrief.core.editors;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.SteppingListener;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class CoordinateRecorder implements PropertyChangeListener,
    SteppingListener
{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, TrackListing> _tracks =
      new HashMap<String, TrackListing>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;

  public CoordinateRecorder(final Layers _myLayers,
      final PlainProjection plainProjection)
  {
    this._myLayers = _myLayers;
    _projection = plainProjection;
  }

  final private class TrackListing extends ArrayList<Point>
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    final private String _name;

    public TrackListing(String name)
    {
      _name = name;
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    if (!_running)
      return;

    // get the new time.
    final HiResDate timeNow = (HiResDate) evt.getNewValue();
    
    _times.add(FormatRNDateTime.toMediumString(timeNow.getDate().getTime()));

    OperateFunction outputIt = new OperateFunction()
    {

      @Override
      public void operateOn(Editable item)
      {
        TrackWrapper track = (TrackWrapper) item;
        Watchable[] items = track.getNearestTo(timeNow);
        if (items != null && items.length > 0)
        {
          FixWrapper fix = (FixWrapper) items[0];

          TrackListing match = _tracks.get(track.getName());
          if (match == null)
          {
            match = new TrackListing(track.getName());
            _tracks.put(track.getName(), match);
          }
          match.add(_projection.toScreen(fix.getLocation()));
        }
      }
    };
    _myLayers.walkVisibleItems(TrackWrapper.class, outputIt);
  }

  @Override
  public void startStepping(HiResDate now)
  {
    _tracks.clear();
    _times.clear();
    _running = true;
  }

  @Override
  public void stopStepping(HiResDate now)
  {
    _running = false;
    
    // output timestamps
    for(String time:_times)
    {
      System.out.println(time);
    }
    
    // output tracks object.
    for(String name:_tracks.keySet())
    {
      System.out.println(name);
      TrackListing track = _tracks.get(name);
      for(Point pt: track)
      {
        System.out.println(pt.getX() + ", " + pt.getY());
      }
    }
  }
}
