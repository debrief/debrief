package org.mwc.debrief.core.editors;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mwc.debrief.core.gpx.ImportGPX;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.SteppingListener;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class CoordinateRecorder implements PropertyChangeListener,
    SteppingListener
{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, TrackWrapper> _tracks =
      new HashMap<String, TrackWrapper>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;

  public CoordinateRecorder(final Layers _myLayers,
      final PlainProjection plainProjection)
  {
    this._myLayers = _myLayers;
    _projection = plainProjection;
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

          TrackWrapper match = _tracks.get(track.getName());
          if (match == null)
          {
            match = new TrackWrapper();
            match.setName(track.getName());
            _tracks.put(track.getName(), match);
          }

          Point point = _projection.toScreen(fix.getLocation());
          WorldLocation newLoc = new WorldLocation(point.getY(), point.getX(),
              fix.getLocation().getDepth());
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix.getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(),
          WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec)/3;
          Fix fix2 = new Fix(timeNow, newLoc,
              courseRads,
              speedYps);
          FixWrapper fw2 = new FixWrapper(fix2);
          match.addFix(fw2);
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

    StringWriter writer = new StringWriter();
    
    List<TrackWrapper> list = new ArrayList<TrackWrapper>();
    list.addAll(_tracks.values());
    
    // output tracks object.
    ImportGPX.doExport(list, writer);
    
    System.out.println(writer.toString());
    
  }
}
