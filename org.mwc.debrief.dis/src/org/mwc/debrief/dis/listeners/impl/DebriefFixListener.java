package org.mwc.debrief.dis.listeners.impl;

import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class DebriefFixListener implements IDISFixListener
{

  final private DISContext _context;

  public DebriefFixListener(DISContext context)
  {
    _context = context;
  }

  @Override
  public void add(long time, long exerciseId, long id, double dLat, double dLong,
      double depth, double courseDegs, double speedMS)
  {
    final Layers layers = _context.getLayers();
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
      
      final TrackWrapper finalTrack = track;
      
      if(_context.getLiveUpdates())
      {
        final Plottable newItem = null;
        
        // pass the new item to the extended method in order to display it in the layer manager
        // newItem = fw;
        Display.getDefault().asyncExec(new Runnable(){

          @Override
          public void run()
          {
            layers.fireExtended(newItem, finalTrack);
          }});
      }
    }

  }

}
