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

package ASSET.Models.Movement;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Models.Movement.HighLevelDemandedStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Models.Movement.HighLevelDemandedStatus.*;
import ASSET.Participants.DemandedStatus;
import ASSET.Util.SupportTesting;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import org.junit.jupiter.api.Test;

/**********************************************************************
 * member variables
 *********************************************************************/

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class HighLevelDemandedStatusTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testNextTarget() {
		final WorldLocation wa = new WorldLocation(1, 0, 0);
		final WorldLocation wb = new WorldLocation(2, 0, 0);
		final WorldLocation wc = new WorldLocation(3, 0, 0);
		final WorldLocation wd = new WorldLocation(4, 0, 0);
		final WorldLocation we = new WorldLocation(5, 0, 0);
		final WorldLocation wf = new WorldLocation(6, 0, 0);
		final WorldLocation wg = new WorldLocation(7, 0, 0);

		final WorldPath wp = new WorldPath();
		wp.addPoint(wa);
		wp.addPoint(wb);
		wp.addPoint(wc);
		wp.addPoint(wd);
		wp.addPoint(we);
		wp.addPoint(wf);
		wp.addPoint(wg);

		final HighLevelDemandedStatus hl = new HighLevelDemandedStatus(12, 0, 2, wp, null, null);
		hl.setRunInReverse(false);

		WorldLocation res = hl.getCurrentTarget();
		assertEquals(wc, res, "returned current target");

		res = hl.getNextTarget();
		assertEquals(wd, res, "returned next target");

		// switch us into reverse
		hl.setRunInReverse(true);
		res = hl.getNextTarget();
		assertEquals(wb, res, "returned next target");

		// and forwards again
		hl.setRunInReverse(false);

		// do the next waypoint
		hl.nextWaypointVisited();

		res = hl.getCurrentTarget();
		assertEquals(wd, res, "returned current target");

		res = hl.getNextTarget();
		assertEquals(we, res, "returned next target");

		// do the next few waypoint
		hl.nextWaypointVisited();
		hl.nextWaypointVisited();
		hl.nextWaypointVisited();

		res = hl.getCurrentTarget();
		assertEquals(wg, res, "returned current target");

		res = hl.getNextTarget();
		assertEquals(null, res, "returned next target");

		// and fall of fthe plot
		hl.nextWaypointVisited();

		res = hl.getCurrentTarget();
		assertEquals(null, res, "returned current target");
		res = hl.getNextTarget();
		assertEquals(null, res, "returned next target");

		// now go into reverse
		hl.setCurrentTargetIndex(3);
		hl.setRunInReverse(true);

		res = hl.getCurrentTarget();
		assertEquals(wd, res, "returned current target");
		res = hl.getNextTarget();
		assertEquals(wc, res, "returned next target");

		// do the next few waypoint
		hl.nextWaypointVisited();
		hl.nextWaypointVisited();
		hl.nextWaypointVisited();

		res = hl.getCurrentTarget();
		assertEquals(wa, res, "returned current target");
		res = hl.getNextTarget();
		assertEquals(null, res, "returned next target");

		// and fall of fthe plot
		hl.nextWaypointVisited();

		res = hl.getCurrentTarget();
		assertEquals(null, res, "returned current target");
		res = hl.getNextTarget();
		assertEquals(null, res, "returned next target");

	}
}
