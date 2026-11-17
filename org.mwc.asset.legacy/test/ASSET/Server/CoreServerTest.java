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

package ASSET.Server;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Server.CoreServer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Server.CoreServer.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import ASSET.ScenarioType;
import ASSET.ServerType;
import org.junit.jupiter.api.Test;

////////////////////////////////////////////////////////////////////////
// member objects
////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class CoreServerTest {
	protected class createList implements ScenarioCreatedListener {
		@Override
		public void scenarioCreated(final int index) {
			createdCounter++;
		}

		@Override
		public void scenarioDestroyed(final int index) {
			destroyedCounter++;
		}

	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";
	protected int createdCounter = 0;

	protected int destroyedCounter = 0;


	@Test
	public void testScenarios() {
		// create server
		final ServerType srv = new CoreServer();

		// add as listener
		final createList cl = new createList();
		srv.addScenarioCreatedListener(cl);

		// create new scenarios
		int s2 = srv.createNewScenario("scrap A");
		s2 = srv.createNewScenario("scrap");

		// now stop listening for these events
		srv.removeScenarioCreatedListener(cl);

		// add another, which we shouldn't hear about
		final int s3 = srv.createNewScenario("scrap");

		// check events got fired
		assertEquals(createdCounter, 2, "count of created events (one ignored)");
		assertEquals(destroyedCounter, 0, "count of destroyed events");

		// get list of scenarios
		final Integer[] res = srv.getListOfScenarios();

		assertEquals(res.length, 3, "Number of scenarios");

		// get specific scenarios
		final ScenarioType sc2 = srv.getThisScenario(s2);
		assertTrue(sc2 != null, "scenario returned");

		if (sc2 == null)
			return;

		// make edits
		sc2.setName("scenario 2");
		sc2.setStepTime(1000);
		final ScenarioType sc3 = srv.getThisScenario(s3);
		sc3.setName("scenario 3");

		// re-retrieve this scenario to check we're getting the correct one
		final ScenarioType sc2a = srv.getThisScenario(s2);
		assertEquals(sc2a.getName(), "scenario 2", "correct name");
		assertEquals(sc2a.getStepTime(), 1000, "correct step");

		// check invalid scenario indices
		ScenarioType dd = srv.getThisScenario(1000);
		assertEquals(dd, null, "invalid index");
		dd = srv.getThisScenario(0);
		assertEquals(dd, null, "invalid index");

		// listen to the events again
		srv.addScenarioCreatedListener(cl);

		// try to destroy a scenario (valid)
		srv.closeScenario(s2);

		assertEquals(destroyedCounter, 1, "after destruction");

		// is it still there?
		dd = srv.getThisScenario(s2);
		assertEquals(null, dd, "destroyed scenario");

		dd = srv.getThisScenario(0);
		assertEquals(null, dd, "destroyed scenario");

		dd = srv.getThisScenario(10000);
		assertEquals(null, dd, "destroyed scenario");

	}
}
