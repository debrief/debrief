package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.diagnostics.file.EventFileListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;

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
  public void add(final long time, final short eid, long id, final String hisName, final int eType, final String message)
  {
    // and the narrative entry
    addNewItem(eid, NarrativeEntry.NARRATIVE_LAYER, new ListenerHelper()
    {

      @Override
      public Layer createLayer()
      {
        return new NarrativeWrapper(NarrativeEntry.NARRATIVE_LAYER);
      }

      @Override
      public Plottable createItem()
      {
        NarrativeEntry newE =
            new NarrativeEntry(hisName, EventFileListener.eventTypeFor(eType), new HiResDate(time),
                message);
        Color theColor = colorFor(eid, hisName);
        newE.setColor(theColor);
        return newE;
      }
    });
  }

}
