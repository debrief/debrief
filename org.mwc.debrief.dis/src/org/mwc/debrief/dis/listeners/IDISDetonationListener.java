package org.mwc.debrief.dis.listeners;


public interface IDISDetonationListener
{

  void add(long time, short eid, int hisId, double dLat, double dLon,
      double depth);
}