/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
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