package ASSET.Scenario;

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
     *
     */
    public void step(long newTime);

    /** the scenario has restarted, reset
     *
     */
    public void restart();

  }