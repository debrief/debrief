package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISStopListener;

public class StopFileListener extends CoreFileListener implements
    IDISStopListener
{

  public StopFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "stop", "time, id, reason");
  }

  @Override
  public void stop(long time, short eid, short reason)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(eid);
    out.append(", ");
    out.append(reason);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}