package org.mwc.debrief.dis.listeners;

public interface IDISFixListener
{
  void add(long id, long time, double dLat, double dLong, double depth,
      double courseDegs, double speedMS);
}