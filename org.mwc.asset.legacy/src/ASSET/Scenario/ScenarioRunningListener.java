
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
 * a change in the state of the scenario running on auto
 */
public interface ScenarioRunningListener extends java.util.EventListener {
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
	 * the scenario has stopped running on auto
	 */
	public void paused();

	/**
	 * the scenario has restarted, reset
	 *
	 * @param scenario TODO
	 */
	public void restart(ScenarioType scenario);

	/**
	 * the scenario has started running on auto
	 */
	public void started();

}
