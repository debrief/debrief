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

package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.Wait.*;
import ASSET.Models.Decision.Tactical.Wait.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class WaitTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new Wait(null, "my name");
	}

	@Test
	public void testSimple() {
		final Duration dur = new Duration(12, Duration.SECONDS);
		final Wait wt = new Wait(dur, "do a wait");

		// ok, give it a test.
		final Status stat = new Status(12, 0);
		stat.setTime(0);
		stat.setSpeed(new WorldSpeed(2, WorldSpeed.Kts));
		stat.setLocation(new WorldLocation(0, 0, 0));
		DemandedStatus res = new SimpleDemandedStatus(0, stat);

		stat.setTime(stat.getTime() + 1000);
		res = wt.decide(stat, null, res, null, null, stat.getTime());

		// have we set the correct expiry time?
		assertEquals(13000, wt._expiryTime, 0, "correct expiry time set");

		// are we continuing in state?
		assertNotNull(res, "returned dem status");

		res = wt.decide(stat, null, res, null, null, stat.getTime());
		stat.setTime(stat.getTime() + 1000);

		res = wt.decide(stat, null, res, null, null, stat.getTime());
		stat.setTime(stat.getTime() + 10000);

		res = wt.decide(stat, null, res, null, null, stat.getTime());
		stat.setTime(stat.getTime() + 1000);
		res = wt.decide(stat, null, res, null, null, stat.getTime());
		stat.setTime(stat.getTime() + 1000);
		res = wt.decide(stat, null, res, null, null, stat.getTime());
		assertNull(res, "dropped out");

	}
}
