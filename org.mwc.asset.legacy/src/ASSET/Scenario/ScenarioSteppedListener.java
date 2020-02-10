
package ASSET.Scenario;

import ASSET.ScenarioType;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

/**
 * the scenario stepping forward
 *
 */
public interface ScenarioSteppedListener extends java.util.EventListener {
	/**
	 * the scenario has restarted, reset
	 *
	 * @param scenario the scenario that has restarted
	 *
	 */
	public void restart(final ScenarioType scenario);

	/**
	 * the scenario has stepped forward
	 *
	 * @param scenario the scenario that has stepped
	 * @param the      new time
	 *
	 */
	public void step(final ScenarioType scenario, final long newTime);

}