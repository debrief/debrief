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

import static ASSET.Models.Decision.Conditions.ElapsedTime.*;
import ASSET.Models.Decision.Conditions.ElapsedTime.*;
import java.util.Date;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.TimePeriod;

/**
 * ************************************************* testing
 * *************************************************
 */
public class ElapsedTimeTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ElapsedTime(new Duration(500, Duration.MILLISECONDS));
	}

	@Test
	public void testIt() {
		// build it
		final ElapsedTime loc = new ElapsedTime(new Duration(500, Duration.MILLISECONDS));
		Object res = loc.test(null, null, 200, null);

		// test it
		assertNotNull(res, "we are alive");
		assertEquals(loc._endTime, 700, "correct expiry time set");

		res = loc.test(null, null, 300, null);
		assertNotNull(res, "still going");

		res = loc.test(null, null, 100, null);
		assertNotNull(res, "still going");

		res = loc.test(null, null, 700, null);
		assertNull(res, "we have expired");

		// check the "single-shot" mode
		loc.setDuration(new Duration(0, Duration.MILLISECONDS));
		// do a reset
		loc.restart();
		res = loc.test(null, null, 300, null);
		assertNotNull(res, "single true value");
		res = loc.test(null, null, 400, null);
		assertNull(res, "now returns false");
	}

}
