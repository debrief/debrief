package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISDetonationListener;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;

public class DebriefDetonationListener  extends DebriefCoreListener implements IDISDetonationListener
{

  final private String DETONATIONS_LAYER = "Detonations";

  public DebriefDetonationListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, short eid, int hisId, final double dLat,
      final double dLon, final double depth)
  {

    final String firingName = "DIS_" + hisId;
    final String message = "Detonation fired from:" + firingName;

    // create the text marker
    addNewItem(eid, DETONATIONS_LAYER, new ListenerHelper()
    {

      @Override
      public Layer createLayer()
      {
        Layer newB = new BaseLayer();
        newB.setName(DETONATIONS_LAYER);
        return newB;
      }

      @Override
      public Plottable createItem()
      {
        WorldLocation newLoc = new WorldLocation(dLat, dLon, depth);
        Color theColor = colorFor(firingName);
        return new LabelWrapper(message, newLoc, theColor);
      }
    });

    // and the narrative entry
    addNewItem(eid, ImportReplay.NARRATIVE_LAYER, new ListenerHelper()
    {

      @Override
      public Layer createLayer()
      {
        return new NarrativeWrapper(ImportReplay.NARRATIVE_LAYER);
      }

      @Override
      public Plottable createItem()
      {
        NarrativeEntry newE =
            new NarrativeEntry(firingName, "DETONATION", new HiResDate(time),
                message);
        Color theColor = colorFor(firingName);
        newE.setColor(theColor);
        return newE;
      }
    });

    // share the news about the new time
    _context.setNewTime(time);
  }

}
