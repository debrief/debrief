package ASSET.Participants;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public interface ParticipantDetectedListener extends java.util.EventListener
{
  /**
   * pass on the list of new detections
   */
  public void newDetections(ASSET.Models.Detection.DetectionList detections);

  /**
   * the scenario has restarted
   */
  public void restart();


  public class Helper implements ParticipantDetectedListener
  {
    /**
     * the list of helpers we support
     */
    private java.util.Vector _myListeners;

    /**
     * add the new listener
     */
    public void addListener(final ParticipantDetectedListener listener)
    {
      if (_myListeners == null)
        _myListeners = new java.util.Vector(1, 1);

      _myListeners.add(listener);
    }

    /**
     * remove this listener
     */
    public void removeListener(final ParticipantDetectedListener listener)
    {
      _myListeners.remove(listener);
    }

    /**
     * handle the new event
     */
    public void newDetections(final ASSET.Models.Detection.DetectionList detections)
    {
      if (_myListeners != null)
      {
        final java.util.Iterator it = _myListeners.iterator();
        while (it.hasNext())
        {
          final ParticipantDetectedListener list = (ParticipantDetectedListener) it.next();
          list.newDetections(detections);
        }
      }
    }


    /**
     * the scenario has restarted
     */
    public void restart()
    {

    }


  }

}