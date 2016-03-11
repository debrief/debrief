package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.NarrativeWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class DebriefCollisionListener  extends DebriefCoreListener implements IDISCollisionListener
{

//  final private String MY_LAYER = "Collisions";

  public DebriefCollisionListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, final short eid, int movingId, final String movingName,
      int recipientId, final String recipientName)
  {
    final String message = "Collision between platform:" + movingName + " and " + recipientName;

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
            new NarrativeEntry(movingName, "COLLISION", new HiResDate(time),
                message);
        Color theColor = colorFor(eid, movingName);
        newE.setColor(theColor);
        return newE;
      }
    });
  }

}
