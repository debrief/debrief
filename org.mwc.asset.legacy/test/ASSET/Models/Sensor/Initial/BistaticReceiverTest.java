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

package ASSET.Models.Sensor.Initial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Sensor.Initial.BistaticReceiver.*;
import ASSET.Models.Sensor.Initial.BistaticReceiver.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.NarrowbandRadNoise;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Buoy;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class BistaticReceiverTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final BistaticReceiver theDet = new BistaticReceiver(12);
		return theDet;
	}

	private void showDetections(final DetectionList detections) {
		final Iterator<DetectionEvent> iter = detections.iterator();
		while (iter.hasNext()) {
			final DetectionEvent de = iter.next();
			System.out.println(de.getTargetType().getType() + " bearing:" + de.getBearing());
		}

	}

	@Test
	public void testBistatics() {
		final CoreScenario scenario = new CoreScenario();
		scenario.setScenarioStepTime(10000);

		// reset the earth model
		WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		// now the objects
		final WorldLocation l1 = new WorldLocation(0, 0, 0);
		final WorldLocation l2 = l1.add(new WorldVector(0, MWC.Algorithms.Conversions.m2Degs(2000), 0));

		final Status theStat = new Status(12, 0);
		theStat.setLocation(l1);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		final ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(14);
		ssn.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
		final Status otherStat = new Status(theStat);
		otherStat.setLocation(l2);
		otherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final RadiatedCharacteristics rc = new RadiatedCharacteristics();
		final Optic opticRadNoise = new Optic(2, new WorldDistance(2, WorldDistance.METRES));
		rc.add(EnvironmentType.VISUAL, opticRadNoise);
		ssn.setMovementChars(MovementCharacteristics.getSampleChars());
		ssn.setRadiatedChars(rc);
		ssn.setStatus(otherStat);

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

		// ok, generate the transmitter
		final Buoy tx = new Buoy(2);
		tx.setName("TX");
		final Status txStat = new Status(theStat);
		tx.setCategory(new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.BUOY));

		txStat.setLocation(txStat.getLocation().add(new WorldVector(3, 0.004, 0)));
		txStat.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
		final NarrowbandSensor noiseSource = new NarrowbandSensor(55);
		final NarrowbandRadNoise nrn = new NarrowbandRadNoise(150, 150);
		final RadiatedCharacteristics txRadChars = new RadiatedCharacteristics();
		txRadChars.add(EnvironmentType.NARROWBAND, nrn);
		tx.setRadiatedChars(txRadChars);
		tx.getSensorFit().add(noiseSource);
		tx.setStatus(txStat);

		final Buoy rx = new Buoy(3);
		rx.setName("RX");
		rx.setCategory(new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.BUOY));
		final Status rxStat = new Status(theStat);
		rxStat.setLocation(rxStat.getLocation().add(new WorldVector(1, 0.004, 0)));
		rxStat.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
		final BistaticReceiver receiver = new BistaticReceiver(44);
		rx.getSensorFit().add(receiver);
		rx.setStatus(rxStat);

		scenario.addParticipant(ssn.getId(), ssn);
		scenario.addParticipant(tx.getId(), tx);
		scenario.addParticipant(rx.getId(), rx);

		DetectionList detections = new DetectionList();
		receiver.detects(env, detections, rx, scenario, 20000);

		assertEquals(1, detections.size(), "got some detections");

		showDetections(detections);

		detections.clear();
		receiver.detects(env, detections, rx, scenario, 40000);

		assertEquals(1, detections.size(), "got some detections");

		// ok, do some steps
		for (int i = 0; i < 10; i++) {
			detections = receiver.getAllDetections();
			detections.clear();

			scenario.step();
			showDetections(detections);
		}

	}
}
