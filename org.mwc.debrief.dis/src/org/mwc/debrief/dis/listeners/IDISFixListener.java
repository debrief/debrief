package org.mwc.debrief.dis.listeners;

public interface IDISFixListener
{
  void add(long time, long exerciseId, long id, double dLat, double dLong,
      double depth, double courseDegs, double speedMS);
}