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

import static ASSET.Models.Decision.Tactical.DeferredBirth.*;
import ASSET.Models.Decision.Tactical.DeferredBirth.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import ASSET.ParticipantType;
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

// ////////////////////////////////////////////////
// testing
// ////////////////////////////////////////////////
public class DeferredBirthTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new DeferredBirth(null, "my name");
	}

	// TODO FIX-TEST
	public void NtestSimple() {
		final Duration dur = new Duration(12, Duration.SECONDS);
		final DeferredBirth wt = new DeferredBirth(dur, "do a wait");

		// ok, give it a test.
		final Status stat = new Status(12, 0);
		stat.setTime(0);
		stat.setSpeed(new WorldSpeed(2, WorldSpeed.Kts));
		stat.setLocation(new WorldLocation(0, 0, 0));
		DemandedStatus res = new SimpleDemandedStatus(0, stat);

		stat.setTime(stat.getTime() + 1000);
		final ScenarioActivityMonitor monitor = new ScenarioActivityMonitor() {

			@Override
			public void createParticipant(final ParticipantType newPart) {
				// TODO Auto-generated method stub

			}

			@Override
			public void detonationAt(final int id, final WorldLocation loc, final double power) {
				// TODO Auto-generated method stub

			}

			@Override
			public Integer[] getListOfParticipants() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ParticipantType getThisParticipant(final int id) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		res = wt.decide(stat, null, res, null, monitor, stat.getTime());

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
