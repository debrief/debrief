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


  /** the scenario stepping forward
   *
   */
  public interface ScenarioSteppedListener extends java.util.EventListener
  {
    /** the scenario has stepped forward
     * @param scenario the scenario that has stepped
     * @param the new time
     *
     */
    public void step(final ScenarioType scenario, final long newTime);

    /** the scenario has restarted, reset
     * @param scenario the scenario that has restarted
     *
     */
    public void restart(final ScenarioType scenario);

  }