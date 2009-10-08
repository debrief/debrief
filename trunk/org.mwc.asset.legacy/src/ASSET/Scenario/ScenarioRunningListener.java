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

/**
 * a change in the state of the scenario running on auto
 */
public interface ScenarioRunningListener extends java.util.EventListener
{
  /**
   * the scenario has started running on auto
   */
  public void started();

  /**
   * the scenario has stopped running on auto
   */
  public void paused();

  /**
   * the scenario is now complete
   *
   * @param elapsedTime
   * @param reason
   */
  public void finished(long elapsedTime, String reason);

  /**
   * the scenario step time has changed
   */
  public void newScenarioStepTime(int val);

  /**
   * the GUI step time has changed
   */
  public void newStepTime(int val);

  /**
   * the scenario has restarted, reset
   * @param scenario TODO
   */
  public void restart(ScenarioType scenario);

}
