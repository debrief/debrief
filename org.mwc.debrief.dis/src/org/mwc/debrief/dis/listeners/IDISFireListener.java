package org.mwc.debrief.dis.listeners;

public interface IDISFireListener
{
  void add(long time, short eid, int hisId, double y, double x, double z);
}