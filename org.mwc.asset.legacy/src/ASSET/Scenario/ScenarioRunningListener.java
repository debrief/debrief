/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
