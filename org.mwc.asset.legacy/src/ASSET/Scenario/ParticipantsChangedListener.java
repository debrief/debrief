package ASSET.Scenario;

import ASSET.ScenarioType;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 */

  /** changes in participant members of the scenario
   *
   */
  public interface ParticipantsChangedListener extends java.util.EventListener
  {
    /** the indicated participant has been added to the scenario
     *
     */
    public void newParticipant(int index);
    /** the indicated participant has been removed from the scenario
     *
     */
    public void participantRemoved(int index);

    /** the scenario has restarted, reset
     * @param scenario TODO
     *
     */
    public void restart(ScenarioType scenario);
  }