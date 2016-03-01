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

  /** produce a narrative data type for this event id
   * 
   * @param id
   * @return
   */
  protected String eventTypeFor(final int thisId)
  {
    final String res;
    
    switch(thisId)
    {
    case (int) IDISEventListener.EVENT_COMMS:
      res = "COMMS";
      break;
    case (int) IDISEventListener.EVENT_LAUNCH:
      res = "LAUNCH";
      break;
    case (int) IDISEventListener.EVENT_NEW_TRACK:
      res = "NEW TRACK";
      break;
    case (int) IDISEventListener.EVENT_TACTICS_CHANGE:
      res = "TACTICS_CHANGE";
      break;
    default:
      res = "EVENT (OTHER)";
      break;
    }
    
    return res;
  }
  
  @Override
  public void add(final long time, short eid, long id, String hisName, final int eType, final String message)
  {
    
    final String theName;
    if (hisName != null)
    {
      theName = hisName;
    }
    else
    {
      theName = "DIS_" + id;
    }
    
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
            new NarrativeEntry(theName, eventTypeFor(eType), new HiResDate(time),
                message);
        Color theColor = colorFor(theName);
        newE.setColor(theColor);
        return newE;
      }
    });
  }

}
