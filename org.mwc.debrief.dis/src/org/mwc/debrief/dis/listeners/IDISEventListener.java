package org.mwc.debrief.dis.listeners;

public interface IDISEventListener
{

  public final long EVENT_COMMS = 10001;
  public final long EVENT_LAUNCH = 10002;
  public final long EVENT_NEW_TRACK = 10003;
  public final long EVENT_TACTICS_CHANGE = 10004;

  /**
   * 
   * @param time
   *          time message sent
   * @param exerciseId
   *          unique id for this exercise
   * @param senderId
   *          the id of the entity sending the message
   * @param eventType
   *          the type of event (or -1 if unknown)
   * @param message
   *          the string message content
   */
  void add(long time, short exerciseId, long senderId, int eventType,
      String message);
}