package org.mwc.debrief.dis.listeners;


public interface IDISDetonationListener
{

  void add(long time, short eid, int hisId, String hisName, double dLat,
      double dLon, double depth);
}