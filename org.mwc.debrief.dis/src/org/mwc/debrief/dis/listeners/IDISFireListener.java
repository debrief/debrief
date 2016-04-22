package org.mwc.debrief.dis.listeners;

public interface IDISFireListener
{
  void add(long time, short eid, int hisId, String hisName, int tgtId, String tgtName,  double y, double x, double z);
}