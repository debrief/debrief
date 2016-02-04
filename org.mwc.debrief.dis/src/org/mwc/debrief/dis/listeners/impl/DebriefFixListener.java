package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class DebriefFixListener  extends DebriefCoreListener implements IDISFixListener
{

  public DebriefFixListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, short exerciseId, long id, final double dLat,
      final double dLong, final double depth, final double courseDegs, final double speedMS)
  {
    final String theName = "DIS_" + id;
    
    super.addNewItem(exerciseId, theName, new ListenerHelper(){

      @Override
      public Layer createLayer()
      {
        TrackWrapper track = new TrackWrapper();
        track.setName(theName);

        Color newCol = colorFor(theName);
        // ok, give it some color
        track.setColor(newCol);

        return track;
      }

      @Override
      public Plottable createItem()
      {
        WorldLocation loc = new WorldLocation(dLat, dLong, depth);
        HiResDate date = new HiResDate(time);
        Fix newF = new Fix(date, loc, courseDegs, speedMS);
        FixWrapper fw = new FixWrapper(newF);
        fw.resetName();
        return fw;
      }});
  }
}
