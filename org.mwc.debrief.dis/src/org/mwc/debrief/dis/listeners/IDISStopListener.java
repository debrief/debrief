package org.mwc.debrief.dis.listeners;

public interface IDISStopListener
{

  /**
   * the scenario has stopped
   * 
   * @param time
   * @param eid
   * @param reason
   */
  void stop(long time, short eid, short reason);

}
