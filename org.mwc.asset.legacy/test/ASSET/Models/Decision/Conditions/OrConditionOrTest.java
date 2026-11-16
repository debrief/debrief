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

import static ASSET.Models.Decision.Conditions.OrCondition.*;
import ASSET.Models.Decision.Conditions.OrCondition.*;
import java.util.Vector;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * ************************************************* testing
 * *************************************************
 */
public class OrConditionOrTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new OrCondition();
	}

	@Test
	public void testIt() {
		// build it

		// create our real conditions

		// time elapsed first
		final TimePoint tpa = new TimePoint(2000);
		final TimePoint tpb = new TimePoint(3000);

		// now for the location
		final Location theLoc = new Location(new WorldLocation(1, 1, 1), new WorldDistance(4, WorldDistance.DEGS));
		theLoc.setSucceedIfCloser(false);

		// build up the condition
		final OrCondition or = new OrCondition();
		or.addCondition(tpb);
		or.addCondition(theLoc);

		// and now the test
		final Status newStat = new Status(12, 300);
		newStat.setLocation(new WorldLocation(2, 2, 2));

		Object res = or.test(newStat, null, 300, null);
		// check it failed
		assertNull(res, "no tests passed");

		// see if we pass the first time test
		newStat.setTime(3200);
		res = or.test(newStat, null, 3200, null);
		// check it failed
		assertNotNull(res, "passed time test");

		// check the correct time was identified
		java.util.Date val = (java.util.Date) res;
		assertEquals(val.getTime(), 3000, "correct time returned");

		or.addCondition(tpa);
		newStat.setTime(3200);
		res = or.test(newStat, null, 3200, null);
		// check it failed
		assertNotNull(res, "passed time test");

		// check that we didn't continue through the tests
		val = (java.util.Date) res;
		assertEquals(val.getTime(), 3000, "correct time returned");

	}

}
