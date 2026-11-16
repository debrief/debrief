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

package ASSET.Models.Decision.Movement;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Models.Decision.Movement.WorkingTransit.*;
import ASSET.Models.Decision.Movement.WorkingTransit.*;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Tactical.Wait;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * ********************************************************************** test
 * this public class
 * **********************************************************************
 */

public class WorkingTransitTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return getSampleInstance();
	}

	protected WorkingTransit getSampleInstance() {
		final WorldLocation newLoc1 = new WorldLocation(0, 1, 2);
		final WorldLocation newLoc2 = new WorldLocation(0, 2, 2);
		final WorldLocation newLoc3 = new WorldLocation(0, 1, 3);
		final WorldPath newPath = new WorldPath(new WorldLocation[] { newLoc1, newLoc2, newLoc3 });
		final WorldSpeed transitSpeed = new WorldSpeed(12, WorldSpeed.Kts);
		final Sequence activity = new Sequence(true, "Sequence");
		return new WorkingTransit(activity, newPath, transitSpeed, false, 2);
	}

	// TODO FIX-TEST
	public void NtestWorking() {
		final WorldLocation newLoc1 = new WorldLocation(0, 1, 2);
		final WorldLocation newLoc2 = new WorldLocation(0, 2, 2);
		final WorldLocation newLoc3 = new WorldLocation(0, 1, 3);
		final WorldPath newPath = new WorldPath(new WorldLocation[] { newLoc1, newLoc2, newLoc3 });
		final WorldSpeed transitSpeed = new WorldSpeed(12, WorldSpeed.Kts);
		final Sequence activity = new Sequence(true, "Sequence");
		final Wait doWait = new Wait(new Duration(12, Duration.SECONDS), "do a wait");
		activity.insertAtHead(doWait);
		final WorkingTransit transit = new WorkingTransit(activity, newPath, transitSpeed, false, 2);

		// just double-check that it's using our special model
		transit.setDestinations(newPath);

		assertNotNull(transit, "didn't create object");

		// get ready to step forward
		final Status theStat = new Status(23, 0);
		WorldLocation theLocation = new WorldLocation(0, 0, 0);
		final WorldSpeed mySpeed = new WorldSpeed(12, WorldSpeed.Kts);
		final MovementCharacteristics chars = SurfaceMovementCharacteristics.getSampleChars();
		theStat.setLocation(theLocation);
		theStat.setSpeed(mySpeed);

		long time = 1000;

		DemandedStatus dem = transit.decide(theStat, chars, null, null, null, time += 1000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");

		// ok, run it again
		dem = transit.decide(theStat, chars, null, null, null, time += 1000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");

		// and now move closer to first point
		theLocation = newLoc1.add(new WorldVector(1, new WorldDistance(12, WorldDistance.METRES), null));
		theStat.setLocation(theLocation);

		// check that we've executed our behaviour
		// ok, run it again
		dem = transit.decide(theStat, chars, null, null, null, time += 1000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Performing activity:Sequence:do a wait:Still waiting", transit.getActivity(), "correctly reported doing activity");

		// check that we've executed our behaviour
		dem = transit.decide(theStat, chars, null, null, null, time += 1000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Performing activity:Sequence:do a wait:Still waiting", transit.getActivity(), "correctly reported doing activity");

		// ok, keep on waiting
		// check that we've executed our behaviour
		dem = transit.decide(theStat, chars, null, null, null, time += 1000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Performing activity:Sequence:do a wait:Still waiting", transit.getActivity(), "correctly reported doing activity");

		// ok, move us to the end of the wait time
		// check that we've executed our behaviour
		dem = transit.decide(theStat, chars, null, null, null, time += 10000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Transit heading for waypoint:2", transit.getActivity(), "correctly reported doing activity");
		assertEquals(1, transit.getCurrentDestinationIndex(), "correctly moved onto next waypoint");

		// hey, move on to the next stop
		dem = transit.decide(theStat, chars, null, null, null, time += 10000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Transit heading for waypoint:2", transit.getActivity(), "correctly reported doing activity");
		assertEquals(1, transit.getCurrentDestinationIndex(), "correctly moved onto next waypoint");

		// hey, move on to the next stop
		dem = transit.decide(theStat, chars, null, null, null, time += 10000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Transit heading for waypoint:2", transit.getActivity(), "correctly reported doing activity");
		assertEquals(1, transit.getCurrentDestinationIndex(), "correctly moved onto next waypoint");

		// and move us near the next waypoint
		theStat.setLocation(newLoc2.add(new WorldVector(0, new WorldDistance(1, WorldDistance.METRES), null)));

		// hey, move on to the next stop
		dem = transit.decide(theStat, chars, null, null, null, time += 10000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Performing activity:Sequence:do a wait:Still waiting", transit.getActivity(), "correctly reported doing activity");
		assertEquals(2, transit.getCurrentDestinationIndex(), "correctly moved onto next waypoint");

		// hey, move on to the next stop
		dem = transit.decide(theStat, chars, null, null, null, time += 10000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Performing activity:Sequence:do a wait:Still waiting", transit.getActivity(), "correctly reported doing activity");
		assertEquals(2, transit.getCurrentDestinationIndex(), "correctly moved onto next waypoint");

		// hey, move on to the next stop
		dem = transit.decide(theStat, chars, null, null, null, time += 3000);

		// ok, check it worked
		assertNotNull(dem, "haven't produced demanded status");
		assertEquals("WorkingTransit:Transit heading for waypoint:3", transit.getActivity(), "correctly reported doing activity");
		assertEquals(2, transit.getCurrentDestinationIndex(), "correctly moved onto next waypoint");

	}

}
