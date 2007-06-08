package ASSET.Participants;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 */

public interface ParticipantDecidedListener extends java.util.EventListener
{
  /** inform listeners that we have made a decision!
   *
   */
  public void newDecision(String description,
                           ASSET.Participants.DemandedStatus dem_status);

  /** the scenario has restarted
   *
   */
  public void restart();
}