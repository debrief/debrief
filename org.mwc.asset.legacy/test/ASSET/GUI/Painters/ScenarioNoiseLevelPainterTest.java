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

package ASSET.GUI.Painters;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.GUI.Painters.ScenarioNoiseLevelPainter.*;
import ASSET.GUI.Painters.ScenarioNoiseLevelPainter.*;
import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;
import ASSET.ScenarioType;
import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.BroadbandRadNoise;
import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Util.SupportTesting;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

///////////////////////////////////////////////////
// member variables
///////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class ScenarioNoiseLevelPainterTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final StatusProvider sp = new StatusProvider() {
			@Override
			public void addScenarioChangeListener(final ScenarioSteppedListener listener) {
			}

			@Override
			public Vector<ParticipantStatus> getStatuses(final int theMedium) {
				return null;
			}

			@Override
			public void removeScenarioChangeListener(final ScenarioSteppedListener listener) {
			}
		};

		return new ScenarioNoiseLevelPainter(null, sp, 0, null);
	}

	@Test
	public void testQuick() {
		final SSN ssn = new SSN(12);
		final Status stat = new Status(12, 0);
		stat.setLocation(new WorldLocation(0, 0, 0));
		stat.setCourse(0);
		stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
		ssn.setStatus(stat);

		final StatusProvider sp = new StatusProvider() {
			@Override
			public void addScenarioChangeListener(final ScenarioSteppedListener listener) {
			}

			/**
			 * return a list of ParticipantStatus objects
			 */
			@Override
			public Vector<ParticipantStatus> getStatuses(final int theMedium) {
				final Vector<ParticipantStatus> res = new Vector<ParticipantStatus>(0, 1);
				final ParticipantStatus ps = new ParticipantStatus();
				ps.location = ssn.getStatus().getLocation();
				ps.sourceLevel = ssn.getRadiatedNoiseFor(EnvironmentType.BROADBAND_PASSIVE, 0);
				res.add(ps);
				return res;
			}

			@Override
			public void removeScenarioChangeListener(final ScenarioSteppedListener listener) {
			}
		};

		final CoreEnvironment cs = new SimpleEnvironment(1, 1, 1);

		final ScenarioNoiseLevelPainter snp = new ScenarioNoiseLevelPainter(cs, sp,
				EnvironmentType.BROADBAND_PASSIVE, null);

		// over-ride the radiated noise levels
		ssn.getRadiatedChars().add(ASSET.Models.Environment.EnvironmentType.BROADBAND_PASSIVE,
				new ASSET.Models.Mediums.BroadbandRadNoise(SSN.DEFAULT_BB_NOISE));
		ssn.getRadiatedChars().add(ASSET.Models.Environment.EnvironmentType.VISUAL,
				new ASSET.Models.Mediums.Optic(2, new WorldDistance(2, WorldDistance.METRES)));

		// check that the noise is as expected
		double thisNoise = snp.getValueAt(ssn.getStatus().getLocation());
		assertEquals(SSN.DEFAULT_BB_NOISE, thisNoise, 0.001, "Default noise at zero speed");

		// over-ride the rad-noise and re-check
		final double newRadNoise = 150;
		final BroadbandRadNoise bRad = (BroadbandRadNoise) ssn.getRadiatedChars()
				.getMedium(new Integer(EnvironmentType.BROADBAND_PASSIVE));
		bRad.setBaseNoiseLevel(newRadNoise);
		thisNoise = snp.getValueAt(ssn.getStatus().getLocation());
		assertEquals(newRadNoise, thisNoise, 0.001, "Default noise at zero speed");

	}
}
