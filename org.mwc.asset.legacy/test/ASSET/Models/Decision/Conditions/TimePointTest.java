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

import static ASSET.Models.Decision.Conditions.TimePoint.*;
import ASSET.Models.Decision.Conditions.TimePoint.*;
import java.util.Date;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

////////////////////////////////////////////////////
// constructor
////////////////////////////////////////////////////

/**
 * ************************************************* testing
 * *************************************************
 */
public class TimePointTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new TimePoint(300);
	}

	@Test
	public void testIt() {
		// build it
		final TimePoint loc = new TimePoint(300);
		Object res = loc.test(null, null, 0, null);
		// test it
		assertNull(res, "we are before time");

		res = loc.test(null, null, 300, null);
		assertNotNull(res, "we are at time");

		res = loc.test(null, null, 400, null);
		assertNotNull(res, "we are after time");
	}

}
