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

package ASSET.Models.Decision;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Composite.*;
import ASSET.Models.Decision.Composite.*;
import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Decision.Conditions.TimePoint;
import ASSET.Models.Decision.Responses.ManoeuvreToLocation;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

////////////////////////////////////////////////////
// testing support
////////////////////////////////////////////////////
public class CompositeTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new Composite(null, null);
	}

	@Test
	public void testIt() {
		// create the condition
		final TimePoint tp = new TimePoint(1200);

		// create the response
		final ManoeuvreToLocation gl = new ManoeuvreToLocation(new MWC.GenericData.WorldLocation(0, 1, 2), null);

		final Composite cs = new Composite(tp, gl);
		final ASSET.Participants.Status st = new ASSET.Participants.Status(12, 200);
		st.setLocation(new MWC.GenericData.WorldLocation(0, 0, 0));
		st.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		DemandedStatus ds = cs.decide(null, null, null, null, null, 800);

		// check nothing returned
		assertNull(ds, "nothing returned");

		// move forward in time
		ds = cs.decide(st, null, null, null, null, 1400);
		assertNotNull(ds, "something returned");

	}
}
