
package ASSET.Participants;

import ASSET.ScenarioType;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public interface ParticipantDecidedListener extends java.util.EventListener {
	/**
	 * inform listeners that we have made a decision!
	 *
	 */
	public void newDecision(String description, ASSET.Participants.DemandedStatus dem_status);

	/**
	 * the scenario has restarted
	 *
	 */
	public void restart(ScenarioType scenario);
}