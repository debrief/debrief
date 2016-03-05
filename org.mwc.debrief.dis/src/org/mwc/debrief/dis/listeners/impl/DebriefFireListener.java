package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISFireListener;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;

public class DebriefFireListener extends DebriefCoreListener implements
    IDISFireListener
{

  final private String MY_LAYER = "WPN RELEASE";

  public DebriefFireListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, short eid, int hisId, String hisName, int tgtId, String tgtName,
      final double dLat, final double dLon, final double depth)
  {
    final String firingName;
    if(hisName != null)
    {
      firingName = hisName;
    }
    else
    {
      firingName = "DIS_" + hisId;
    }
    final String message = "Launch of new weapon from:" + firingName + " at:" + tgtName;

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
            new NarrativeEntry(firingName, MY_LAYER, new HiResDate(time),
                message);
        Color theColor = colorFor(firingName);
        newE.setColor(theColor);
        return newE;
      }
    });
  }

}
