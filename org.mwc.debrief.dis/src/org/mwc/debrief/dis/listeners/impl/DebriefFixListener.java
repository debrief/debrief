package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;
import java.util.Iterator;

import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class DebriefFixListener implements IDISFixListener
{

  final private IDISContext _context;

  public DebriefFixListener(IDISContext context)
  {
    _context = context;
  }

  @Override
  public void add(long time, short exerciseId, long id, double dLat,
      double dLong, double depth, double courseDegs, double speedMS)
  {
    final String theName = "DIS_" + id;
    TrackWrapper track = (TrackWrapper) _context.findLayer(exerciseId, theName);
    if (track == null)
    {
      track = new TrackWrapper();
      track.setName(theName);

      Color newCol = colorFor(theName);
      // ok, give it some color
      track.setColor(newCol);

      // and store it
      _context.addThisLayer(track);

      // share the news
      Iterator<INewItemListener> iter = _context.getNewItemListeners();
      while (iter.hasNext())
      {
        Layers.INewItemListener newI = (Layers.INewItemListener) iter.next();
        newI.newItem(track, null, null);
      }
    }

    WorldLocation loc = new WorldLocation(dLat, dLong, depth);
    HiResDate date = new HiResDate(time);
    Fix newF = new Fix(date, loc, courseDegs, speedMS);
    FixWrapper fw = new FixWrapper(newF);
    fw.resetName();
    track.add(fw);

    final TrackWrapper finalTrack = track;

    if (_context.getLiveUpdates())
    {
      final Plottable newItem = null;

      _context.fireUpdate(newItem, finalTrack);
    }

    // share the news about the new time
    System.out.println("== setting new time:" + date.getDate());
    _context.setNewTime(date.getDate().getTime());
    

    // should we try any formatting?
    Iterator<INewItemListener> iter = _context.getNewItemListeners();
    while (iter.hasNext())
    {
      Layers.INewItemListener newI = (Layers.INewItemListener) iter.next();
      newI.newItem(finalTrack, fw, null);
    }

  }

  private final java.awt.Color[] defaultColors = new java.awt.Color[]
  {java.awt.Color.red, java.awt.Color.green, java.awt.Color.yellow,
      java.awt.Color.blue, java.awt.Color.cyan, java.awt.Color.magenta,
      java.awt.Color.orange, java.awt.Color.pink};

  private Color colorFor(String name)
  {
    // ok, get the hashmap
    int index = name.hashCode() % defaultColors.length;
    return defaultColors[index];
  }

}
