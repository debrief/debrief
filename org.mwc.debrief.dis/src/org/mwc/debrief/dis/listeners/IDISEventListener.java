package org.mwc.debrief.dis.listeners;

public interface IDISEventListener
{

  public final int EVENT_COMMS = 10001;
  public final int EVENT_LAUNCH = 10002;
  public final int EVENT_NEW_TRACK = 10003;
  public final int EVENT_TACTICS_CHANGE = 10004;
  public final int EVENT_NEW_TARGET_TRACK = 10005;

  /**
   * 
   * @param time
   *          time message sent
   * @param exerciseId
   *          unique id for this exercise
   * @param senderId
   *          the id of the entity sending the message
   * @param hisName TODO
   * @param eventType
   *          the type of event (or -1 if unknown)
   * @param message
   *          the string message content
   */
  void add(long time, short exerciseId, long senderId, String hisName,
      int eventType, String message);
}