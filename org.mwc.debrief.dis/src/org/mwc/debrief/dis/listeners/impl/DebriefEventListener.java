package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISEventListener;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.NarrativeWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class DebriefEventListener extends DebriefCoreListener implements
    IDISEventListener
{

  public DebriefEventListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, short eid, long id)
  {
    final String theName = "DIS_" + id;
    final String message = "Message from " + theName;
    
    System.out.println("NEW EVENT!");

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
            new NarrativeEntry(theName, "EVENT", new HiResDate(time),
                message);
        Color theColor = colorFor(theName);
        newE.setColor(theColor);
        return newE;
      }
    });
  }

}
