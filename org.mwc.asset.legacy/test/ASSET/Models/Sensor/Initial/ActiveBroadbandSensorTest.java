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

import static ASSET.Models.Sensor.Initial.ActiveBroadbandSensor.*;
import ASSET.Models.Sensor.Initial.ActiveBroadbandSensor.*;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.SensorType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class ActiveBroadbandSensorTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ActiveBroadbandSensor(12);
	}

	@Test
	public void testHeloDetection() {

		// set up the Ssk
		final ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
		final ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(5), 40)));
		sskStat.setSpeed(new WorldSpeed(18, WorldSpeed.M_sec));
		ssk.setStatus(sskStat);

		// ok, setup the ssk radiation
		final ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(134);
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		rc.add(EnvironmentType.BROADBAND_PASSIVE, brn);
		ssk.setRadiatedChars(rc);

		// now setup the helo
		final ASSET.Models.Vessels.Helo merlin = new ASSET.Models.Vessels.Helo(33);
		final ASSET.Participants.Status merlinStat = new ASSET.Participants.Status(33, 0);
		merlinStat.setLocation(origin);
		merlinStat.setSpeed(new WorldSpeed(1, WorldSpeed.Kts));
		merlin.setStatus(merlinStat);

		// and it's sensor
		final ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
		final ActiveBroadbandSensor bs = new ActiveBroadbandSensor(34);
		bs.setSourceLevel(210);
		fit.add(bs);
		merlin.setSensorFit(fit);

		// now setup the su
		final ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
		final ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
		final WorldLocation sskLocation = ssk.getStatus().getLocation();
		ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(1), -40)));
		ffStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		ff.setStatus(ffStat);
		ff.setSensorFit(fit);
		final ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(15);
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		ff_rc.add(EnvironmentType.BROADBAND_PASSIVE, ff_brn);
		ff.setSelfNoise(ff_rc);
		ff.setRadiatedChars(ff_rc);

		// try a detection
		final ASSET.Models.Environment.CoreEnvironment env = new ASSET.Models.Environment.SimpleEnvironment(1, 1,
				1);
		ASSET.Models.Detection.DetectionEvent dt;
		dt = bs.detectThis(env, merlin, ssk, 0, null);
		assertTrue(dt != null, "helo able to detect SSK");

		dt = bs.detectThis(env, ff, ssk, 0, null);
		assertTrue(dt != null, "frigate able to detect SSK");

	}

}
