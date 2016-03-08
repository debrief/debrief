package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISEventListener;

public class EventFileListener extends CoreFileListener implements
    IDISEventListener
{

  public EventFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "event", "time, eid, id, name, eventType, message");
  }

  @Override
  public void add(long time, short exerciseId, long id, String hisName, int eventType, String message)
  {
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
    out.append(message);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}