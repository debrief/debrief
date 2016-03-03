package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;

public class CollisionFileListener extends CoreFileListener implements
    IDISCollisionListener
{

  public CollisionFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "collision",
        "time, id, movingId, name, recipientId");
  }

  @Override
  public void add(long time, short eid, int movingId, String hisName,
      int recipientId)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(eid);
    out.append(", ");
    out.append(movingId);
    out.append(", ");
    out.append(hisName);
    out.append(", ");
    out.append(recipientId);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}