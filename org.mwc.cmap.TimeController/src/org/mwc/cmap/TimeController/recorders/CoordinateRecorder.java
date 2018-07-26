package org.mwc.cmap.TimeController.recorders;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class CoordinateRecorder 

{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, TrackWrapper> _tracks =
      new HashMap<String, TrackWrapper>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  private TimeControlPreferences _timePrefs;

  public CoordinateRecorder(final Layers _myLayers,
      final PlainProjection plainProjection,TimeControlPreferences timePreferences)
  {
    this._myLayers = _myLayers;
    _projection = plainProjection;
    _timePrefs = timePreferences;
  }

  public void newTime(final HiResDate timeNow)
  {
    if (!_running)
      return;

    // get the new time.
    
    _times.add(FormatRNDateTime.toMediumString(timeNow.getDate().getTime()));

    OperateFunction outputIt = new OperateFunction()
    {

      @Override
      public void operateOn(final Editable item)
      {
        final TrackWrapper track = (TrackWrapper) item;
        final Watchable[] items = track.getNearestTo(timeNow);
        if (items != null && items.length > 0)
        {
          final FixWrapper fix = (FixWrapper) items[0];

          TrackWrapper match = _tracks.get(track.getName());
          if (match == null)
          {
            match = new TrackWrapper();
            match.setName(track.getName());
            match.setColor(track.getColor());
            _tracks.put(track.getName(), match);
          }
          final Point point = _projection.toScreen(fix.getLocation());
          final WorldLocation newLoc = new WorldLocation(point.getY(), point.getX(),
              fix.getLocation().getDepth());
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix.getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(),
          WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec)/3;
          final Fix fix2 = new Fix(timeNow, newLoc,
              courseRads,
              speedYps);
          final FixWrapper fw2 = new FixWrapper(fix2);
          match.addFix(fw2);
        }
      }
    };
    _myLayers.walkVisibleItems(TrackWrapper.class, outputIt);
  }

  public void startStepping(final HiResDate now)
  {
    _tracks.clear();
    _times.clear();
    _running = true;
  }

  public void stopStepping(final HiResDate now)
  {
    _running = false;
    
    // output timestamps
    for(String time:_times)
    {
      System.out.println(time);
    }
    
    List<TrackWrapper> list = new ArrayList<TrackWrapper>();
    list.addAll(_tracks.values());
    // output tracks object.
    
  }
}
