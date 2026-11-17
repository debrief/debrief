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

import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.PatternSearch_Saw.*;
import ASSET.Models.Decision.Tactical.PatternSearch_Saw.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModel;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

// ////////////////////////////////////////////////
// member variables
// ////////////////////////////////////////////////

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class PatternSearch_SawTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	public static void outputPoints(final WorldPath path) {
		final Collection<WorldLocation> pts = path.getPoints();
		final Iterator<WorldLocation> iter = pts.iterator();
		while (iter.hasNext()) {
			final WorldLocation pt = iter.next();
			System.err.println(pt.getLong() + "," + pt.getLat());
		}
	}

	private EarthModel _oldModel;


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final PatternSearch_Core ladder = new PatternSearch_Saw(new WorldLocation(1, 1, 1),
				new WorldDistance(20, WorldDistance.KM), null, null, "test search",
				new WorldDistance(160, WorldDistance.KM), new WorldDistance(160, WorldDistance.KM));
		final WorldPath route = ladder.createSearchRoute(null);

		// and put it into a route
		ladder._myRoute = new TransitWaypoint(route, null, false, new OnTopWaypoint());

		// we need t
		return ladder;
	}

	@BeforeEach
	protected void setUp() throws Exception {

		_oldModel = WorldLocation.getModel();

		// store the model we're expecting
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

	}

	@AfterEach
	protected void tearDown() throws Exception {

		// restore the previous model
		WorldLocation.setModel(_oldModel);
	}

	@Test
	public final void testCreateRoute() {
		final PatternSearch_Core ladder = new PatternSearch_Saw(null, new WorldDistance(20, WorldDistance.KM), null,
				null, "test search", new WorldDistance(160, WorldDistance.KM),
				new WorldDistance(160, WorldDistance.KM));

		// ok. now go for it
		final Status origin = new Status(1, 0);
		origin.setLocation(SupportTesting.createLocation(0, 0));
		origin.setCourse(90);
		origin.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		ladder.decide(origin, null, null, null, null, 1000);

		// and look at the resulting ladder
		assertNotNull(ladder._myRoute, "ladder created");

		final TransitWaypoint route = ladder._myRoute;
		final WorldPath path = route.getDestinations();
		outputPoints(path);
		assertNotNull(path, "waypoints created");

		assertEquals(6, path.size(), "path of correct length");

		assertEquals(" x,159999,y,19999", SupportTesting.toXYString(path.getLocationAt(0)), "first point right");
		assertEquals(" x,0,y,39999", SupportTesting.toXYString(path.getLocationAt(1)), "second point right");
		assertEquals(" x,0,y,119999", SupportTesting.toXYString(path.getLocationAt(5)), "5th point right");
	}

	@Test
	public final void testSpeed() {
		final WorldLocation originLoc = new WorldLocation(75, 0, 0);

		final PatternSearch_Core ladder = new PatternSearch_Saw(null, new WorldDistance(20, WorldDistance.KM),
				new WorldDistance(200, WorldDistance.METRES), new WorldSpeed(120, WorldSpeed.Kts), "test search",
				new WorldDistance(160, WorldDistance.KM), new WorldDistance(160, WorldDistance.KM));

		// and now the helo
		final Helo merlin = new Helo(12);
		merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		final Status start = new Status(12, TimePeriod.INVALID_TIME);
		final WorldLocation origin = originLoc;
		origin.setDepth(-500);
		start.setLocation(origin);
		start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
		merlin.setStatus(start);
		merlin.setDecisionModel(ladder);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, merlin);
		cs.setScenarioStepTime(2000);

		// do a step - to initialise the points
		cs.step();

		// now run through to completion
		while (cs.getTime() < 5400000) {
			cs.step();
		}

		// dro.tearDown(cs);
		// tpo.tearDown(cs);
		assertEquals(120, merlin.getStatus().getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "at demanded speed");

	}
}
