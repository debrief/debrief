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

import static ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor.*;
import ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.DeployableSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class DippingActiveBroadbandSensorTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new DippingActiveBroadbandSensor(12);
	}

	@Test
	public void testBig() {
		final DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
		final DemandedSensorStatus dss = new DemandedSensorStatus(EnvironmentType.BROADBAND_ACTIVE, true);
		dss.setDeployDepth(new Double(300));
		ds.set(DippingActiveBroadbandSensor.ADS_HOUSED);
		ds.add(dss);

		final Status st = new Status(11, 0);
		st.setLocation(createLocation(0, 0));

		dss.setDeployDepth(new Double(300));
		st.getLocation().setDepth(-100);
		final double waterLowerRate = 4;
		final double airLowerRate = 6;
		final double waterRaiseRate = 1;
		final double airRaiseRate = 3;
		final double lowerPause = 3;
		final double raisePause = 5;

		final DippingActiveBroadbandSensor dip = new DippingActiveBroadbandSensor(12,
				new WorldSpeed(airLowerRate, WorldSpeed.M_sec), new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
				new Duration(lowerPause, Duration.SECONDS), new Duration(raisePause, Duration.SECONDS),
				new WorldSpeed(waterLowerRate, WorldSpeed.M_sec), new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

		// tell the dipper that we want it to deploy
		dip.inform(dss);

		// let's see what happens
		dip.update(ds, st, 30000);

		// start by checking what happens when we try to re-house it
		assertTrue(dip.isHoused());
		assertFalse(dip.isAtDepth(st.getLocation().getDepth()));
		assertEquals(dip._remainingPausePeriod, 0, 0.001, "no pause required");

		ds.unset(DippingActiveBroadbandSensor.ADS_HOUSED);
		ds.set(DippingActiveBroadbandSensor.ADS_DEPLOYED);

		// let's see what happens when we're not in hover
		dip.update(ds, st, 30000);

		// has it started to deploy?
		assertTrue(dip.isHoused(), "not started to deploy when not in hover");
		assertFalse(dip.isAtDepth(st.getLocation().getDepth()), "not started to deploy when not in hover");
		assertEquals(dip._remainingPausePeriod, 0, 0.001, "no pause inserted");
		assertEquals(dip._cableLength, 0, 0.001, "no cable extended");

		// let's see what happens when we are in hover
		st.set(Helo.HoverStates.IN_HOVER);
		dip.update(ds, st, 30000);

		// has it started to deploy?
		assertFalse(dip.isHoused(), "not housed any more");
		assertFalse(dip.isAtDepth(st.getLocation().getDepth()), "still not deployed");

		// move the state forward
		st.setTime(30000);

		dip.update(ds, st, 3000000);

		// has it started to deploy?
		assertFalse(dip.isHoused(), "not housed any more");
		assertTrue(dip.isAtDepth(st.getLocation().getDepth()), "deploy deployed");

	}

	@Test
	public void testLower() {
		final double waterLowerRate = 4;
		final double airLowerRate = 6;
		final double waterRaiseRate = 1;
		final double airRaiseRate = 3;
		final double lowerPause = 3;
		final double raisePause = 5;

		final DippingActiveBroadbandSensor dip = new DippingActiveBroadbandSensor(12,
				new WorldSpeed(airLowerRate, WorldSpeed.M_sec), new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
				new Duration(lowerPause, Duration.SECONDS), new Duration(raisePause, Duration.SECONDS),
				new WorldSpeed(waterLowerRate, WorldSpeed.M_sec), new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

		// check we're up and running
		assertNotNull(dip, "object created");

		// check we've stored the objects correctly
		assertEquals(dip.getAirLowerRate().getValueIn(WorldSpeed.M_sec), airLowerRate, 0.1, "it");
		assertEquals(dip.getWaterLowerRate().getValueIn(WorldSpeed.M_sec), waterLowerRate, 0.1, "it");
		assertEquals(dip.getAirRaiseRate().getValueIn(WorldSpeed.M_sec), airRaiseRate, 0.1, "it");
		assertEquals(dip.getWaterRaiseRate().getValueIn(WorldSpeed.M_sec), waterRaiseRate, 0.1, "it");
		assertEquals(dip.getRaisePause().getValueIn(Duration.SECONDS), raisePause, 0.1, "it");
		assertEquals(dip.getLowerPause().getValueIn(Duration.SECONDS), lowerPause, 0.1, "it");

		// and the cable length
		assertEquals(dip._cableLength, 0, 0.0, "still inside");

		// start the lower sequence
		dip._remainingPausePeriod = dip._lowerPause;

		dip.setDemandedDeployDepth(300);

		dip.lower(2000, -60);

		//
		assertEquals(0, dip._cableLength, 0.0, "still pausing");

		dip.lower(3000, -60);
		//
		assertEquals(12, dip._cableLength, 0.0, "started dropping through air");

		// and some more
		dip.lower(2000, -60);

		//
		assertEquals(24, dip._cableLength, 0.0, "still dropping through air");

		// and some more
		dip.lower(5000, -60);

		//
		assertEquals(54, dip._cableLength, 0.0, "still dropping through air");

		// span the two mediums
		dip.lower(2000, -60);

		//
		assertEquals(64, dip._cableLength, 0.0, "still dropping through air");

		// carry on diving
		dip.lower(76000, -60);

		//
		assertEquals(360, dip._cableLength, 0.0, "reached bottom");
	}

	@Test
	public void testRaise() {
		final double waterLowerRate = 4;
		final double airLowerRate = 6;
		final double waterRaiseRate = 1;
		final double airRaiseRate = 3;
		final double lowerPause = 3;
		final double raisePause = 5;

		final DippingActiveBroadbandSensor dip = new DippingActiveBroadbandSensor(12,
				new WorldSpeed(airLowerRate, WorldSpeed.M_sec), new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
				new Duration(lowerPause, Duration.SECONDS), new Duration(raisePause, Duration.SECONDS),
				new WorldSpeed(waterLowerRate, WorldSpeed.M_sec), new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

		// initialise the pause period
		dip._remainingPausePeriod = dip._raisePause;

		dip._cableLength = 150;
		dip.raise(2000, -30);

		//
		assertEquals(148, dip._cableLength, 0.0, "starting to come up");

		//
		dip.raise(117000, -30);
		//
		assertEquals(31, dip._cableLength, 0.0, "near the surface");

		//
		//
		dip.raise(2000, -30);
		// answer should be 27 - from 31m under we travel a 1m/s for 1 sec then 3m/sec
		// for 1 sec - 4
		// m/sec total
		assertEquals(27, dip._cableLength, 0.0, "in air");

		//
		dip.raise(8000, -30);
		assertEquals(3, dip._cableLength, 0.0, "in air");

		//
		dip.raise(4000, -30);
		assertEquals(0, dip._cableLength, 0.0, "in air");

		//
		assertFalse(dip.isHoused(), "pausing before house");

		//
		dip.raise(2000, -30);
		assertTrue(dip.isHoused(), "now housed");

		assertFalse(dip.isAtDepth(-30));

	}

	@Test
	public void testTimedLowerTest() {

		final Status st = new Status(11, 0);
		st.setLocation(createLocation(0, 0));
		st.getLocation().setDepth(-100);
		st.set(Helo.HoverStates.IN_HOVER);

		final DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
		final DemandedSensorStatus dss = new DemandedSensorStatus(EnvironmentType.BROADBAND_ACTIVE, true);
		dss.setDeployDepth(new Double(300));
		ds.set(DippingActiveBroadbandSensor.ADS_DEPLOYED);

		final double waterLowerRate = 4;
		final double airLowerRate = 6;
		final double waterRaiseRate = 1;
		final double airRaiseRate = 3;
		final double lowerPause = 3;
		final double raisePause = 5;

		final DippingActiveBroadbandSensor dip = new DippingActiveBroadbandSensor(12,
				new WorldSpeed(airLowerRate, WorldSpeed.M_sec), new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
				new Duration(lowerPause, Duration.SECONDS), new Duration(raisePause, Duration.SECONDS),
				new WorldSpeed(waterLowerRate, WorldSpeed.M_sec), new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));
		// check we're up and running
		assertNotNull(dip, "object created");

		dip.inform(dss);

		final long timeStep = 100;
		long newTime = 0;

		// ok, now loop through second by second and see how we get on...
		while (!dip.isAtDepth(st.getLocation().getDepth())) {
			dip.update(ds, st, newTime += timeStep);
			st.setTime(newTime);
		}
		assertEquals(94700, newTime, 0, "took correct time");

	}

	@Test
	public void testTimedRaiseTest() {

		final Status st = new Status(11, 0);
		st.setLocation(createLocation(0, 0));
		st.getLocation().setDepth(-100);
		st.set(Helo.HoverStates.IN_HOVER);

		final DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
		ds.set(DippingActiveBroadbandSensor.ADS_HOUSED);

		final double waterLowerRate = 4;
		final double airLowerRate = 6;
		final double waterRaiseRate = 1;
		final double airRaiseRate = 3;
		final double lowerPause = 3;
		final double raisePause = 5;

		final DippingActiveBroadbandSensor dip = new DippingActiveBroadbandSensor(12,
				new WorldSpeed(airLowerRate, WorldSpeed.M_sec), new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
				new Duration(lowerPause, Duration.SECONDS), new Duration(raisePause, Duration.SECONDS),
				new WorldSpeed(waterLowerRate, WorldSpeed.M_sec), new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

		// check we're up and running
		assertNotNull(dip, "object created");

		dip._cableLength = 400;

		final long timeStep = 100;
		long newTime = 0;

		// ok, now loop through second by second and see how we get on...
		while (!dip.isHoused()) {
			dip.update(ds, st, newTime += timeStep);
			st.setTime(newTime);
		}
		assertEquals(338400, newTime, 0, "took correct time");
	}

}
