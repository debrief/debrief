package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;
import java.util.Iterator;

import org.mwc.debrief.dis.listeners.IDISDetonationListener;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.NarrativeWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class DebriefDetonationListener implements IDISDetonationListener
{

  final private IDISContext _context;

  public DebriefDetonationListener(IDISContext context)
  {
    _context = context;
  }

  @Override
  public void add(long time, short eid, int hisId, double dLat, double dLon,
      double depth)
  {

    final String theName = "DIS_" + hisId;

    // find the narratives layer
    
    Layer nLayer =  _context.findLayer(eid, ImportReplay.NARRATIVE_LAYER);
    if (nLayer == null)
    {
      
      nLayer = new NarrativeWrapper(ImportReplay.NARRATIVE_LAYER);
      
      // and store it
      _context.addThisLayer(nLayer);

      // share the news
      Iterator<INewItemListener> iter = _context.getNewItemListeners();
      while (iter.hasNext())
      {
        Layers.INewItemListener newI = (Layers.INewItemListener) iter.next();
        newI.newItem(nLayer, null, null);
      }
    }

    NarrativeEntry newE = new NarrativeEntry(theName, "DETONATION", new HiResDate(time), "new narrative");
    newE.setColor(Color.black);
    nLayer.add(newE);

    final Layer finalLayer = nLayer;

    if (_context.getLiveUpdates())
    {
      final Plottable newItem = null;
      _context.fireUpdate(newItem, finalLayer);
    }

    // share the news about the new time
//    System.out.println("== setting new time:" + date.getDate());
    _context.setNewTime(time);

    // should we try any formatting?
    Iterator<INewItemListener> iter = _context.getNewItemListeners();
    while (iter.hasNext())
    {
      Layers.INewItemListener newI = (Layers.INewItemListener) iter.next();
      newI.newItem(finalLayer, newE, null);
    }
  }

}
