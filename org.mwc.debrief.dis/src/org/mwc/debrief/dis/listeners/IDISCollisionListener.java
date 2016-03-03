package org.mwc.debrief.dis.listeners;

public interface IDISCollisionListener
{
  void add(final long time, final short eid, final int movingId,
      String hisName, final int recipientId);
}