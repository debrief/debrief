package ASSET.Participants;

import ASSET.ScenarioType;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 */

public interface ParticipantMovedListener extends java.util.EventListener
{
  /** this participant has moved
   *
   */
  public void moved(ASSET.Participants.Status newStatus);

  /** the scenario has restarted
   * @param scenario TODO
   *
   */
  public void restart(ScenarioType scenario);


  public class Helper implements ParticipantMovedListener
  {
    /** the list of helpers we support
     *
     */
    private java.util.Vector<ParticipantMovedListener> _myListeners;

    /** add the new listener
     *
     */
    public void addListener(final ParticipantMovedListener listener)
    {
      if(_myListeners == null)
        _myListeners = new java.util.Vector<ParticipantMovedListener>(1,1);

      _myListeners.add(listener);
    }

    /** remove this listener
     *
     */
    public void removeListener(final ParticipantMovedListener listener)
    {
      _myListeners.remove(listener);
    }

    /** handle the new event
     *
     */
    public void moved(final ASSET.Participants.Status newStatus)
    {
      if(_myListeners != null)
      {
        final java.util.Iterator<ParticipantMovedListener> it = _myListeners.iterator();
        while(it.hasNext())
        {
          final ParticipantMovedListener list = (ParticipantMovedListener) it.next();
          list.moved(newStatus);
        }
      }
    }


    /** the scenario has restarted
     *
     */
    public void restart(ScenarioType scenario)
    {
      //
    }
  }


}