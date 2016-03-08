package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISDetonationListener;

public class DetonateFileListener extends CoreFileListener implements
    IDISDetonationListener
{

  public DetonateFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "detonate", "time, id, hisID, hisName, Lat, Lon, depth");
  }

  @Override
  public void add(long time, short eid, int hisId, String hisName, double dLat,
      double dLon, double depth)
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