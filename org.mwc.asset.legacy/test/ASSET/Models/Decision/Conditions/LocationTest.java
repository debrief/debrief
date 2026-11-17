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

package ASSET.Models.Decision.Conditions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Conditions.Location.*;
import ASSET.Models.Decision.Conditions.Location.*;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * ************************************************* testing
 * *************************************************
 */
public class LocationTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final Location loc = new Location(null, new WorldDistance(1, WorldDistance.DEGS));
		return loc;
	}

	@Test
	public void testIt() {
		// build it
		final WorldLocation wl = new WorldLocation(1, 1, 1);
		Status newStat = new Status(12, 200);
		newStat.setLocation(wl.add(new WorldVector(0, 2, 0)));
		final Location loc = new Location(wl, new WorldDistance(1, WorldDistance.DEGS));
		Object res = loc.test(newStat, null, 0, null);
		// test it
		assertNull(res, "we are not in range");

		newStat = new Status(12, 200);
		newStat.setLocation(wl.add(new WorldVector(0, 1, 0)));
		res = loc.test(newStat, null, 0, null);
		assertNotNull(res, "we are at range");

		// try inside range
		newStat = new Status(12, 200);
		newStat.setLocation(wl.add(new WorldVector(0, 0.5, 0)));
		res = loc.test(newStat, null, 0, null);
		assertNotNull(res, "we are in range");

		// switch around when we succeed
		loc.setSucceedIfCloser(false);

		newStat.setLocation(wl.add(new WorldVector(0, 0.5, 0)));
		res = loc.test(newStat, null, 0, null);
		// test it
		assertNull(res, "we are not in range");

		newStat = new Status(12, 200);
		newStat.setLocation(wl.add(new WorldVector(0, 1, 0)));
		res = loc.test(newStat, null, 0, null);
		assertNotNull(res, "we are at range");

		// try inside range
		newStat = new Status(12, 200);
		newStat.setLocation(wl.add(new WorldVector(0, 2, 0)));
		res = loc.test(newStat, null, 0, null);
		assertNotNull(res, "we are in range");

	}

}
