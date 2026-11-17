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

import static ASSET.Models.Decision.Conditions.DetectionForPeriod.*;
import ASSET.Models.Decision.Conditions.DetectionForPeriod.*;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;

public class DetectionForPeriodTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final TargetType tt = new TargetType();
		tt.addTargetType(Category.Type.AV_MISSILE);

		final Detection detection = new DetectionForPeriod(tt, null, null, false);
		return detection;
	}

	@Test
	public void testIt() {
		// build it
		final Category secondTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.AV_MISSILE);

		final ASSET.ParticipantType participant = new ASSET.Models.Vessels.Surface(12);
		participant.setName("un-named");
		final CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(12);
		final DetectionEvent db = new DetectionEvent(120, participant.getId(), null, sensor,
				new WorldDistance(1, WorldDistance.YARDS), new WorldDistance(1, WorldDistance.YARDS), null, null,
				new Float(60), secondTarget, null, null, participant);

		// build up the list
		DetectionList dl = new DetectionList();

		// try with no detections

		final TargetType tt = new TargetType();
		tt.addTargetType(Category.Type.AV_MISSILE);

		final Detection detection = new DetectionForPeriod(tt, null, null, false);
		DetectionEvent de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNull(de, "no success with empty list");

		// add the second (valid item)
		final DetectionForPeriod d2 = new DetectionForPeriod(tt, null, new Duration(20, Duration.MILLISECONDS),
				false);
		dl.add(db);
		de = (DetectionEvent) d2.test(null, dl, 0, null);
		assertNotNull(de, "success with valid contact");

		// try it a few minutes later (even with no detections)
		dl = new DetectionList();
		de = (DetectionEvent) d2.test(null, dl, 15, null);
		assertNotNull(de, "we got detection when still in period");

		// check we can expire
		de = (DetectionEvent) d2.test(null, dl, 25, null);
		assertNull(de, "we didn't get detection when still out of period");

		// create empty

	}
}
