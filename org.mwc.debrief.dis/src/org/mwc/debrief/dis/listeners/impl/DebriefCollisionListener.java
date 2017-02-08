package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;

public class DebriefCollisionListener  extends DebriefCoreListener implements IDISCollisionListener
{

  final private String COLLISIONS_LAYER = "Collisions";

  public DebriefCollisionListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, final short eid, int movingId, final String rawMovingName,
      int recipientId, final String rawRecipientName, final double dLat, final double dLong, final double depthM)
  {
    final String recipientName;
    
    // special handling - and ID of -1 means the environment
    if(recipientId == -1)
    {
      recipientName = "Environment";
    }
    else
    {
      recipientName = rawRecipientName;
    }
    
    final String movingName;
    if(movingId == -1)
    {
      movingName = "Environment";
    }
    else
    {
      movingName = rawMovingName;
    }
    
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
    
    // create the text marker
    addNewItem(eid, COLLISIONS_LAYER, new ListenerHelper()
    {

      @Override
      public Layer createLayer()
      {
        Layer newB = new BaseLayer();
        newB.setName(COLLISIONS_LAYER);
        return newB;
      }

      @Override
      public Plottable createItem()
      {
        WorldLocation newLoc = new WorldLocation(dLat, dLong, depthM);
        Color theColor = colorFor(eid, movingName);
        return new LabelWrapper(message, newLoc, theColor);
      }
    });

    
    
  }

}
