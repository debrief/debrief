package org.mwc.debrief.dis.listeners.impl;

import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class FixListener implements IDISFixListener
{

  final private DISContext _context;

  public FixListener(DISContext context)
  {
    _context = context;
  }

  @Override
  public void add(long time, long id, double dLat, double dLong, double depth,
      double courseDegs, double speedMS)
  {
    Layers layers = _context.getLayers();
    if (layers != null)
    {
      TrackWrapper track = (TrackWrapper) layers.findLayer("DIS_" + id);
      if (track == null)
      {
        track = new TrackWrapper();
        track.setName("DIS_" + id);
        layers.addThisLayer(track);
      }

      WorldLocation loc = new WorldLocation(dLat, dLong, depth);
      HiResDate date = new HiResDate(time);
      Fix newF = new Fix(date, loc, courseDegs, speedMS);
      FixWrapper fw = new FixWrapper(newF);
      fw.resetName();
      track.add(fw);
      
      layers.fireExtended(fw, track);
    }

  }

}
