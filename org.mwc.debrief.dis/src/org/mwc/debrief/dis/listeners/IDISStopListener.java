package org.mwc.debrief.dis.listeners;

public interface IDISStopListener
{

  public static final short PDU_FREEZE = 1;
  public static final short PDU_STOP = 2;

  /**
   * the scenario has stopped
   * 
   * @param time
   * @param appId TODO
   * @param eid
   * @param reason
   */
  void stop(long time, int appId, short eid, short reason);

}
