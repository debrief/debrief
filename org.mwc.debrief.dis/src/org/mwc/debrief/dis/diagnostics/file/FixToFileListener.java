package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISFixListener;

public class FixToFileListener extends CoreFileListener implements
    IDISFixListener
{

  public FixToFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "fix",
        "time, id, name, dLat, dLong, depth, courseDegs, speedMS, damage");
  }

  @Override
  public void add(long time, short exerciseId, long id, String eName,
      short force, double dLat, double dLong, double depth, double courseDegs, double speedMS, int damage)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(id);
    out.append(", ");
    out.append(eName);
    out.append(", ");
    out.append(dLat);
    out.append(", ");
    out.append(dLong);
    out.append(", ");
    out.append(depth);
    out.append(", ");
    out.append(courseDegs);
    out.append(", ");
    out.append(speedMS);
    out.append(", ");
    out.append(damage);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}