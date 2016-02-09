package org.mwc.debrief.dis.listeners;

public interface IDISCollisionListener
{
  void add(final long time, final short eid, final int movingId,
      final int recipientId, final double dLat, final double dLon,
      final double depth);
}