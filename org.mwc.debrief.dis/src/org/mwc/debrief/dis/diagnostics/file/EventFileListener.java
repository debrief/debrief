package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISEventListener;

public class EventFileListener extends CoreFileListener implements
    IDISEventListener
{

  public EventFileListener(String root, boolean toFile, boolean toScreen, LoggingFileWriter writer)
  {
    super(
        root,
        toFile,
        toScreen,
        "event",
        "time, exerciseId, entityId, entityName, eventType, eventName, message",
        writer);
  }

  @Override
  public void add(long time, short exerciseId, long id, String hisName, int eventType, String message)
  {
    String eventName = eventTypeFor(eventType);
    
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(exerciseId);
    out.append(", ");
    out.append(id);
    out.append(", ");
    out.append(hisName);
    out.append(", ");
    out.append(eventType);
    out.append(", ");
    out.append(eventName);
    out.append(", ");
    out.append(message);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }


  /** produce a narrative data type for this event id
   * 
   * @param id
   * @return
   */
  public static String eventTypeFor(final int thisId)
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
    case (int) IDISEventListener.EVENT_NEW_TARGET_TRACK:
      res = "NEW TARGET TRACK";
      break;
    default:
      res = "EVENT (" + thisId + ")";
      break;
    }
    
    return res;
  }
  
}