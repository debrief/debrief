package org.mwc.debrief.dis.listeners;

public interface IDISFixListener
{
  void add(long time, short exerciseId, long id, short force, double dLat,
      double dLong, double depth, double courseDegs, double speedMS, int damage);
}