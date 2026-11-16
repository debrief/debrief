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

package ASSET.Models.Sensor.Cookie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Sensor.Cookie.PlainCookieSensor.*;
import ASSET.Models.Sensor.Cookie.PlainCookieSensor.*;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class PlainCookieSensorTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new PlainCookieSensor(1, new WorldDistance(12, WorldDistance.DEGS));
	}

	@Test
	public void testPlainSensor() {

		// reset the earth model
		WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		// ok, create the sensor
		final PlainCookieSensor ps = new PlainCookieSensor(12, new WorldDistance(12, WorldDistance.DEGS));

		// now the objects
		final WorldLocation l1 = new WorldLocation(0, 0, 0);
		final WorldLocation l2 = l1.add(new WorldVector(0, 10, 0));
		final WorldLocation l3 = l1.add(new WorldVector(0, 13, 0));

		final ASSET.Models.Vessels.Surface su1 = new ASSET.Models.Vessels.Surface(12);
		final Status theStat1 = new Status(12, 0);
		theStat1.setLocation(l1);
		theStat1.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		su1.setStatus(theStat1);
		final SensorList sl = new SensorList();
		sl.add(ps);

		final ASSET.Models.Vessels.Surface su2 = new ASSET.Models.Vessels.Surface(14);
		final Status theStat2 = new Status(12, 0);
		theStat2.setLocation(l2);
		theStat2.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		su2.setStatus(theStat2);

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

		DetectionEvent de = ps.detectThis(env, su1, su2, 1000, null);

		assertNotNull(de, "Made a detection");

		su2.getStatus().setLocation(l3);

		de = ps.detectThis(env, su1, su2, 1000, null);

		assertNull(de, "Not made a detection");

	}
}
