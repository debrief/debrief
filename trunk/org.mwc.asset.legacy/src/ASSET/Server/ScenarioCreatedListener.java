package ASSET.Server;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 */

/** message to indicate that a new scenario has been created
*
*/
public interface ScenarioCreatedListener extends java.util.EventListener
{
  /** pass on details of the creation of a new scenario
   *
   */
  public void scenarioCreated(int index);
  /** pass on details of the destruction of a scenario
   *
   */
  public void scenarioDestroyed(int index);
}