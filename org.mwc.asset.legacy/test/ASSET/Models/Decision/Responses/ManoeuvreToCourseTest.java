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

import static ASSET.Models.Decision.Responses.ManoeuvreToCourse.*;
import ASSET.Models.Decision.Responses.ManoeuvreToCourse.*;
import ASSET.ParticipantType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

////////////////////////////////////////////////////
// testing support
////////////////////////////////////////////////////
public class ManoeuvreToCourseTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ManoeuvreToCourse(null, false, null, false, null);
	}

	@Test
	public void testIt() {
		ManoeuvreToCourse mc = new ManoeuvreToCourse(new WorldSpeed(10, WorldSpeed.M_sec), true, new Float(20),
				true, new WorldDistance(40, WorldDistance.METRES));

		final ParticipantType cp = new ASSET.Models.Vessels.Surface(12);
		final CoreSensor cs = new ASSET.Models.Sensor.Initial.BroadbandSensor(22);
		ASSET.Models.Detection.DetectionEvent de = new ASSET.Models.Detection.DetectionEvent(1200, cp.getId(), null,
				cs, new WorldDistance(33, WorldDistance.YARDS), // range
				new WorldDistance(33, WorldDistance.YARDS), // estimated range
				new Float(3), // course
				null, null, null, new Float(22), null, cp);

		final Status status = new Status(10, 10);
		status.setLocation(new WorldLocation(2, 2, 2));
		status.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		status.setCourse(45);
		DemandedStatus res = mc.direct(de, status, null, null, null, 1200);
		SimpleDemandedStatus sds = (SimpleDemandedStatus) res;

		// check we got something returned
		assertNotNull(res, "demanded status returned");

		// check we correctly handled the relative aspects
		assertEquals(23, sds.getCourse(), 0.001, "correct relative course");
		assertEquals(32, sds.getSpeed(), 0.001, "correct relative speed");
		assertEquals(40, sds.getHeight(), 0.001, "correct absolute depth");

		// hey, let's not provide course or speed!
		de = new ASSET.Models.Detection.DetectionEvent(1200, cp.getId(), null, cs, null, null, null, null, null,
				null, null, null, cp);

		res = mc.direct(de, status, null, null, null, 1200);
		sds = (SimpleDemandedStatus) res;

		// check we got something returned
		assertNotNull(res, "demanded status returned");

		// check we correctly handled the relative aspects
		assertEquals(45, sds.getCourse(), 0.001, "correct relative course ignored");
		assertEquals(12, sds.getSpeed(), 0.001, "correct relative speed ignored");
		assertEquals(40, sds.getHeight(), 0.001, "correct absolute depth");

		// handle when there's missing data
		de = new ASSET.Models.Detection.DetectionEvent(1200, cp.getId(), null, cs, null, null, new Float(33), null,
				null, null, new Float(22), null, cp);

		mc = new ManoeuvreToCourse(new WorldSpeed(10, WorldSpeed.M_sec), false, new Float(20), false,
				new WorldDistance(40, WorldDistance.METRES));

		res = mc.direct(de, status, null, null, null, 1200);
		sds = (SimpleDemandedStatus) res;

		// check we got something returned
		assertNotNull(res, "demanded status returned");

		// check we correctly handled the relative aspects
		assertEquals(20, sds.getCourse(), 0.001, "correct absolute course");
		assertEquals(10, sds.getSpeed(), 0.001, "correct absolute speed");
		assertEquals(40, sds.getHeight(), 0.001, "correct absolute depth");

		mc = new ManoeuvreToCourse(null, false, null, false, null);

		res = mc.direct(de, status, null, null, null, 1200);
		sds = (SimpleDemandedStatus) res;

		// check we got something returned
		assertNotNull(res, "demanded status returned");

		// check we correctly handled the relative aspects
		assertEquals(45, sds.getCourse(), 0.001, "empty course");
		assertEquals(12, sds.getSpeed(), 0.001, "empty speed");
		assertEquals(-2, sds.getHeight(), 0.001, "empty depth");
	}

}
