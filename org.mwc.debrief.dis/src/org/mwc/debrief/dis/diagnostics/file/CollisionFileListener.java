package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;

public class CollisionFileListener extends CoreFileListener implements
    IDISCollisionListener
{

  public CollisionFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "collision",
        "time, exerciseId, movingId, name, recipientId, recipientName");
  }

  @Override
  public void add(long time, short eid, int movingId, String movingName,
      int recipientId, String recipientName)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(eid);
    out.append(", ");
    out.append(movingId);
    out.append(", ");
    out.append(movingName);
    out.append(", ");
    out.append(recipientId);
    out.append(", ");
    out.append(recipientName);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}