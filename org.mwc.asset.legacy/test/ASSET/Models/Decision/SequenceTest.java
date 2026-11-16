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

package ASSET.Models.Decision;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Sequence.*;
import ASSET.Models.Decision.Sequence.*;
import java.util.Vector;
import ASSET.Models.DecisionType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class SequenceTest extends ASSET.Util.EditableTesting {
	private static class MyDecision extends CoreDecision {
		public DemandedStatus myRes = null;

		public boolean hasRestarted = false;

		public MyDecision() {
			super("My Decision");
		}

		@Override
		public DemandedStatus decide(final Status status, final ASSET.Models.Movement.MovementCharacteristics chars,
				final DemandedStatus demStatus, final DetectionList detections,
				final ScenarioActivityMonitor monitor, final long time) {
			hasRestarted = false;
			return myRes;
		}

		@Override
		public Editable.EditorType getInfo() {
			return null;
		}

		@Override
		public String getVersion() {
			return "$Date$";
		}

		@Override
		public boolean hasEditor() {
			return false;
		}

		/**
		 * indicate to this model that its execution has been interrupted by another
		 * (prob higher priority) model
		 *
		 * @param currentStatus
		 */
		@Override
		public void interrupted(final Status currentStatus) {
			// ignore.
		}

		@Override
		public void restart() {
			hasRestarted = true;
		}

	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new Sequence(true, "test behaviour");
	}

	@Test
	public void testTheSequence() {
		// ok, let's get going
		final Sequence seq = new Sequence(true, MY_NAME);

		// create a valid demanded status to test against
		final SimpleDemandedStatus validA = new SimpleDemandedStatus(1, 12000);
		final SimpleDemandedStatus validB = new SimpleDemandedStatus(1, 12000);
		final SimpleDemandedStatus validD = new SimpleDemandedStatus(1, 12000);

		// initialise the settings
		seq.setIsAlive(true);
		seq.setStayAlive(false);

		// give it a trial run (without data)
		final Status initialStat = new Status(12, 12000);
		DemandedStatus ds = seq.decide(initialStat, null, null, null, null, 1200);

		assertTrue(ds == null, "no demanded stat with empty behaviours");
		assertEquals(seq.getIsAlive(), false, "is now dead");

		// create a few behaviours
		final MyDecision da = new MyDecision();
		final MyDecision db = new MyDecision();
		final MyDecision dc = new MyDecision();
		final MyDecision dd = new MyDecision();

		// and insert them
		seq.insertAtHead(dc);
		seq.insertAtHead(db);

		// check they're in the correct order
		assertEquals(seq.getModels().get(0), db, "b at head");
		assertEquals(seq.getModels().get(1), dc, "c at foot");

		// and a couple more
		seq.insertAtHead(da);
		seq.insertAtFoot(dd);

		// check they're in the correct order
		assertEquals(seq.getModels().get(0), da, "a at head");
		assertEquals(seq.getModels().get(3), dd, "d at foot");

		// ok, start testing the decisions themselves
		assertEquals(seq._currentDecision, 0, "the current decision is correctly set");
		// bring it back to life
		seq.setIsAlive(true);
		assertEquals(seq.getIsAlive(), true, "is already alive");
		ds = seq.decide(null, null, null, null, null, 100);

		// hey, they're all returning null - check we've died
		assertEquals(ds, null, "we returned null");
		assertEquals(seq.getIsAlive(), false, "we've now died");

		// revive us, and set stay alive to true
		seq.setStayAlive(true);
		seq.setIsAlive(true);

		ds = seq.decide(null, null, null, null, null, 100);
		// hey, they're all returning null - check we've died
		assertEquals(ds, null, "we returned null");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");

		// ok, give a behaviour and run it
		da.myRes = validA;

		ds = seq.decide(null, null, null, null, null, 100);

		// hey, a returns valid
		assertEquals(ds, validA, "we returned correct result");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");
		assertEquals(seq._currentDecision, 0, "we've got the correct behaviour");

		// now give b a behaviour and try again
		db.myRes = validB;

		ds = seq.decide(null, null, null, null, null, 100);

		// hey, a returns valid
		assertEquals(ds, validA, "we returned correct result");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");
		assertEquals(seq._currentDecision, 0, "we've got the correct behaviour");

		// ditch valid behaviour for A, to switch to B
		da.myRes = null;

		ds = seq.decide(null, null, null, null, null, 100);

		// hey, a returns valid
		assertEquals(ds, validB, "we returned correct result");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");
		assertEquals(seq._currentDecision, 1, "we've got the correct behaviour");

		// give A it's behaviour back, to check it gets ignored
		da.myRes = validA;

		ds = seq.decide(null, null, null, null, null, 100);

		// hey, a returns valid
		assertEquals(ds, validB, "we returned correct result");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");
		assertEquals(seq._currentDecision, 1, "we've got the correct behaviour");

		// check behaviours cascade down to D
		db.myRes = null;
		dd.myRes = validD;

		ds = seq.decide(null, null, null, null, null, 100);

		// hey, a returns valid
		assertEquals(ds, validD, "we returned correct result");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");
		assertEquals(seq._currentDecision, 3, "we've got the correct behaviour");

		// and check we can cascade back to the start
		dd.myRes = null;

		// ensure we're set to stay alive
		seq.setStayAlive(true);

		ds = seq.decide(null, null, null, null, null, 100);

		// hey, a returns valid
		assertEquals(ds, null, "we returned correct result");
		assertEquals(seq.getIsAlive(), true, "we've stayed alive");
		assertEquals(seq._currentDecision, 0, "we've got the correct behaviour");

		// check they restarted
		assertTrue(da.hasRestarted, "a has restarted");
		assertTrue(db.hasRestarted, "b has restarted");
		assertTrue(dc.hasRestarted, "c has restarted");

		// put some data back int
		da.myRes = validA;
		db.myRes = validB;
		dd.myRes = validD;

		// move forward a single step (to cancel the has restarted flags)
		ds = seq.decide(null, null, null, null, null, 100);

		assertEquals(da.hasRestarted, false, "a has restarted");

		// finally, try the reset
		seq.restart();
		assertTrue(da.hasRestarted, "a has restarted");
		assertTrue(db.hasRestarted, "b has restarted");
		assertTrue(dc.hasRestarted, "c has restarted");

	}

}
