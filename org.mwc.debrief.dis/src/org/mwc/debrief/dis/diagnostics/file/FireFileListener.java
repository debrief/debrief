package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISFireListener;

public class FireFileListener extends CoreFileListener implements
    IDISFireListener
{

  public FireFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "fire", "time, id, hisId, x, y, z");
  }


  @Override
  public void
      add(long time, short eid, int hisId, double y, double x, double z)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(eid);
    out.append(", ");
    out.append(hisId);
    out.append(", ");
    out.append(x);
    out.append(", ");
    out.append(y);
    out.append(", ");
    out.append(z);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}