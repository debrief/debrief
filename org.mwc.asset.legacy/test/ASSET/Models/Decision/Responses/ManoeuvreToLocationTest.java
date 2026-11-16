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

package ASSET.Models.Decision.Responses;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Responses.ManoeuvreToLocation.*;
import ASSET.Models.Decision.Responses.ManoeuvreToLocation.*;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

////////////////////////////////////////////////////
// testing support
////////////////////////////////////////////////////
public class ManoeuvreToLocationTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final WorldLocation newLoc = new WorldLocation(0.4, 0.4, 40);
		final WorldLocation origin = new WorldLocation(0, 0, 40);
		final Status currentStat = new Status(12, 200);
		currentStat.setLocation(origin);
		currentStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final ManoeuvreToLocation ml = new ManoeuvreToLocation(newLoc, null);
		return ml;
	}

	@Test
	public void testIt() {
		final WorldLocation newLoc = new WorldLocation(0.4, 0.4, 40);
		final WorldLocation origin = new WorldLocation(0, 0, 40);
		final Status currentStat = new Status(12, 200);
		currentStat.setLocation(origin);
		currentStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final ManoeuvreToLocation ml = new ManoeuvreToLocation(newLoc, null);
		DemandedStatus ds = ml.direct(null, currentStat, null, null, null, -1);
		// check we are at the same speed
		assertEquals(12, ((SimpleDemandedStatus) ds).getSpeed(), 0.001, "maintain speed");
		assertEquals(45, ((SimpleDemandedStatus) ds).getCourse(), 1, "on correct course");

		// now provide a speed value
		ml.setSpeed(new WorldSpeed(20, WorldSpeed.M_sec));
		ds = ml.direct(null, currentStat, null, null, null, -1);
		// check we are at the same speed
		assertEquals(20, ((SimpleDemandedStatus) ds).getSpeed(), 0.001, "maintain speed");
		assertEquals(45, ((SimpleDemandedStatus) ds).getCourse(), 1, "on correct course");

	}

}
