package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISStartResumeListener;

public class StartFileListener extends CoreFileListener implements
    IDISStartResumeListener
{

  public StartFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "start resume", "time, ex id, replication");
  }

  @Override
  public void add(long time, short eid, long replication)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(eid);
    out.append(", ");
    out.append(replication);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}