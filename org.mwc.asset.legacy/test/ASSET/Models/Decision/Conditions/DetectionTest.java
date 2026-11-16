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

import static ASSET.Models.Decision.Conditions.Detection.*;
import ASSET.Models.Decision.Conditions.Detection.*;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

/**
 * ************************************************* testing
 * *************************************************
 */
public class DetectionTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final Detection theDet = new Detection(null, null);
		return theDet;
	}

	@Test
	public void testIt() {
		// build it

		final Category firstTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.CARRIER);
		final Category secondTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.AV_MISSILE);
		final Category thirdTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.AV_MISSILE);

		final ASSET.ParticipantType participant = new ASSET.Models.Vessels.Surface(12);
		participant.setName("un-named");
		final CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(12);
		final DetectionEvent da = new DetectionEvent(110, participant.getId(), null, sensor,
				new WorldDistance(4, WorldDistance.YARDS), new WorldDistance(4, WorldDistance.YARDS), null, null,
				new Float(50), firstTarget, null, null, participant);
		final DetectionEvent db = new DetectionEvent(120, participant.getId(), null, sensor,
				new WorldDistance(1, WorldDistance.YARDS), new WorldDistance(4, WorldDistance.YARDS), null, null,
				new Float(60), secondTarget, null, null, participant);
		final DetectionEvent dc = new DetectionEvent(130, participant.getId(), null, sensor,
				new WorldDistance(4, WorldDistance.YARDS), new WorldDistance(4, WorldDistance.YARDS), null, null,
				new Float(70), thirdTarget, null, null, participant);

		// build up the list
		DetectionList dl = new DetectionList();

		// try with no detections

		final TargetType tt = new TargetType();
		tt.addTargetType(Category.Type.AV_MISSILE);

		Detection detection = new Detection(tt, null);
		DetectionEvent de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNull(de, "no success with empty list");

		// add the first (invalid item)
		dl.add(da);
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNull(de, "no success with invalid contact");

		// add the second (valid item)
		dl.add(db);
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNotNull(de, "success with valid contact");

		// add the third (valid, and closer item)
		dl.add(dc);
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNotNull(de, "success with valid contact");
		assertEquals(de.getStrength().floatValue(), 70, 0.02, "strongest target found");

		//////////////////////////////////////////////////////////////
		// now include a range threshold
		//////////////////////////////////////////////////////////////
		detection = new Detection(tt, new WorldDistance(2, WorldDistance.DEGS));
		dl = new DetectionList();
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNull(de, "no success with empty list");

		// add the first (invalid item)
		dl.add(da);
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNull(de, "no success with invalid contact");

		// add the second (valid item)
		dl.add(db);
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNotNull(de, "success with valid contact");

		// add the third (valid, and closer item)
		dl.add(dc);
		de = (DetectionEvent) detection.test(null, dl, 0, null);
		assertNotNull(de, "success with valid contact");
		assertEquals(de.getStrength().floatValue(), 70, 0.02, "target with detection in range");

	}
}
