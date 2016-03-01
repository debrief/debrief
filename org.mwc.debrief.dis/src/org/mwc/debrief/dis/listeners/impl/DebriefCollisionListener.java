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

  final private String MY_LAYER = "Collisions";

  public DebriefCollisionListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, short eid, int movingId, String hisName,
      int recipientId, final double dLat, final double dLon, final double depth)
  {

    final String movingName = "DIS_" + movingId;
    final String recipeintName = "DIS_" + recipientId;
    final String message = "Collision between platform:" + movingName + " and " + recipeintName;

    // create the text marker
    addNewItem(eid, MY_LAYER, new ListenerHelper()
    {

      @Override
      public Layer createLayer()
      {
        Layer newB = new BaseLayer();
        newB.setName(MY_LAYER);
        return newB;
      }

      @Override
      public Plottable createItem()
      {
        WorldLocation newLoc = new WorldLocation(dLat, dLon, depth);
        Color theColor = colorFor(movingName);
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
            new NarrativeEntry(movingName, "COLLISION", new HiResDate(time),
                message);
        Color theColor = colorFor(movingName);
        newE.setColor(theColor);
        return newE;
      }
    });
  }

}
