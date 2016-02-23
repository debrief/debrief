package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISEventListener;

public class EventFileListener extends CoreFileListener implements
    IDISEventListener
{

  public EventFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "event", "time, id, message");
  }

  @Override
  public void add(long time, short exerciseId, long id, String message)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(exerciseId);
    out.append(", ");
    out.append(message);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}