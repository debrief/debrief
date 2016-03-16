package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISFireListener;

public class FireFileListener extends CoreFileListener implements
    IDISFireListener
{

  public FireFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "fire", "time, exerciseId,  entityId, entityName, tgtId, tgtName, x, y, z");
  }


  @Override
  public void
      add(long time, short eid, int hisId, String hisName, int tgtId, String tgtName, final double dLat, final double dLon, final double depth)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(eid);
    out.append(", ");
    out.append(hisId);
    out.append(", ");
    out.append(hisName);
    out.append(", ");
    out.append(tgtId);
    out.append(", ");
    out.append(tgtName);
    out.append(", ");
    out.append(dLat);
    out.append(", ");
    out.append(dLon);
    out.append(", ");
    out.append(depth);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}