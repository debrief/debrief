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

import static ASSET.Models.Decision.Tactical.PatternSearch_Ladder.*;
import ASSET.Models.Decision.Tactical.PatternSearch_Ladder.*;
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
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////
// member variables
//////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class PatternSearch_LadderTest extends ASSET.Util.EditableTesting {
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
		final PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(5),
				SupportTesting.createLocation(0, 0), new WorldDistance(1, WorldDistance.KM),
				new WorldDistance(4, WorldDistance.KM), null, null, "home ladder");

		final WorldPath route = ladder.createSearchRoute(null);

		// and put it into a route
		ladder._myRoute = new TransitWaypoint(route, null, false, new OnTopWaypoint());

		// we need t
		return ladder;
	}

	// TODO FIX-TEST
	public final void NtestCreateRoute() {
		final PatternSearch_Core ladder = new PatternSearch_Ladder(0, new Integer(9),
				SupportTesting.createLocation(0, 0), new WorldDistance(20, WorldDistance.KM),
				new WorldDistance(160, WorldDistance.KM), null, null, "home ladder");

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

		assertEquals(11, path.size(), "path of correct length");

		assertEquals(" x,0,y,0", SupportTesting.toXYString(path.getLocationAt(0)), "first point right");
		assertEquals(" x,500,y,0", SupportTesting.toXYString(path.getLocationAt(1)), "second point right");
		assertEquals(" x,4500,y,4000", SupportTesting.toXYString(path.getLocationAt(10)), "11th point right");
	}

	// TODO FIX-TEST
	public final void NtestUnlimitedFlySearch() {
		final PatternSearch_Core ladder = new PatternSearch_Ladder(90, null, SupportTesting.createLocation(0, 0),
				new WorldDistance(10, WorldDistance.KM), new WorldDistance(40, WorldDistance.KM), null, null,
				"home ladder");

		// ok. now go for it
		final Status stat = new Status(1, TimePeriod.INVALID_TIME);
		stat.setLocation(SupportTesting.createLocation(0, 0));
		stat.setCourse(90);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		// and now the helo
		final Helo merlin = new Helo(12);
		merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		final Status start = new Status(12, TimePeriod.INVALID_TIME);
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
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

		// check the correct num of points created
		assertEquals(1 + PatternSearch_Ladder.DEFAULT_ROUTE_LENGTH * 2, ladder._myRoute.getDestinations().size(), 0, "right number of points created");

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "unlimited_fly_search.rep", false);
		// DebriefReplayObserver.outputTheseLocations("c:/temp/unlimited_pts2",
		// ladder._myRoute.getDestinations());
		//
		// TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300,
		// "unlimited_fly_search.png", null, false, true);
		// tpo.addPoints(ladder._myRoute.getDestinations());
		// dro.setup(cs);
		// tpo.setup(cs);

		// now run through to completion
		while (cs.getTime() < 20000000) {
			cs.step();
		}

		// dro.tearDown(cs);
		// tpo.tearDown(cs);

		final WorldLocation endPoint = merlin.getStatus().getLocation();
		final WorldLocation correctEnd = SupportTesting.createLocation(265423, 25605);
		SupportTesting.outputLocation(endPoint);
		final WorldVector error = correctEnd.subtract(endPoint);
		assertEquals(0, MWC.Algorithms.Conversions.Degs2m(error.getRange()), 400, "near to correct end point");

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
	public final void testCreateRouteWithDepth() {
		final PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(5),
				SupportTesting.createLocation(0, 0), new WorldDistance(1, WorldDistance.KM),
				new WorldDistance(4, WorldDistance.KM), new WorldDistance(500, WorldDistance.METRES), null,
				"home ladder");

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
		assertNotNull(path, "waypoints created");

		assertEquals(11, path.size(), "path of correct length");

		assertEquals(" x,0,y,0", SupportTesting.toXYString(path.getLocationAt(0)), "first point right");
		assertEquals(" x,500,y,0", SupportTesting.toXYString(path.getLocationAt(1)), "second point right");
		assertEquals(" x,4500,y,4000", SupportTesting.toXYString(path.getLocationAt(10)), "11th point right");

		assertEquals(-500, path.getLocationAt(0).getDepth(), 0.01, "correct height set");
		assertEquals(-500, path.getLocationAt(1).getDepth(), 0.01, "correct height set");
		assertEquals(-500, path.getLocationAt(10).getDepth(), 0.01, "correct height set");
	}

	@Test
	public final void testFlySearch() {
		final WorldLocation originLoc = SupportTesting.createLocation(
				MWC.Algorithms.Conversions.Degs2m(MWC.Algorithms.Conversions.Nm2Degs(-5)),
				MWC.Algorithms.Conversions.Degs2m(MWC.Algorithms.Conversions.Nm2Degs(5)));

		final PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(4), originLoc,
				new WorldDistance(10, WorldDistance.NM), new WorldDistance(40, WorldDistance.NM), null, null,
				"home ladder");

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

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "fly_search.rep", false);
		// DebriefReplayObserver.outputTheseLocations("c:/temp/pts2",
		// ladder._myRoute.getDestinations());
		// TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300,
		// "fly_search.png", null,
		// false, true);
		// tpo.addPoints(ladder._myRoute.getDestinations());
		// dro.setup(cs);
		// tpo.setup(cs);

		// now run through to completion
		while (cs.getTime() < 5400000) {
			cs.step();
		}

		// dro.tearDown(cs);
		// tpo.tearDown(cs);

		final WorldLocation endPoint = merlin.getStatus().getLocation();
		final WorldLocation correctEnd = SupportTesting.createLocation(55201, -11739);
		// SupportTesting.outputLocation(endPoint);
		final WorldVector error = correctEnd.subtract(endPoint);
		assertEquals(0, MWC.Algorithms.Conversions.Degs2m(error.getRange()), 400, "near to correct end point");

	}

	@Test
	public final void testFlySearchFurtherNorth() {
		final WorldLocation originLoc = new WorldLocation(75, 0, 0);

		final PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(4), originLoc,
				new WorldDistance(10, WorldDistance.NM), new WorldDistance(40, WorldDistance.NM), null, null,
				"home ladder");

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

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "fly_search_north.rep",
		// false, "debrief_track", true);
		// DebriefReplayObserver.outputTheseLocations("c:/temp/fly_search_north_pts",
		// ladder._myRoute.getDestinations());
		// TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300,
		// "fly_search_north.png",
		// null, false, true, "track plot", true);
		// tpo.addPoints(ladder._myRoute.getDestinations());
		// dro.setup(cs);
		// tpo.setup(cs);

		// now run through to completion
		while (cs.getTime() < 5400000) {
			cs.step();
		}

		// dro.tearDown(cs);
		// tpo.tearDown(cs);

		final WorldLocation endPoint = merlin.getStatus().getLocation();
		final WorldLocation correctEnd = SupportTesting.createLocation(64456, 8312860);
		SupportTesting.outputLocation(endPoint);
		final WorldVector error = correctEnd.subtract(endPoint);
		assertEquals(0, MWC.Algorithms.Conversions.Degs2m(error.getRange()), 400, "near to correct end point:" + endPoint);

	}

	@Test
	public final void testSpeed() {
		final WorldLocation originLoc = new WorldLocation(75, 0, 0);

		final PatternSearch_Core ladder = new PatternSearch_Ladder(90, new Integer(4), originLoc,
				new WorldDistance(10, WorldDistance.NM), new WorldDistance(40, WorldDistance.NM), null,
				new WorldSpeed(120, WorldSpeed.Kts), "home ladder");

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

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "fly_search_north.rep",
		// false, "debrief_track", true);
		// DebriefReplayObserver.outputTheseLocations("c:/temp/fly_search_north_pts",
		// ladder._myRoute.getDestinations());
		// TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300,
		// "fly_search_north.png",
		// null, false, true, "track plot", true);
		// tpo.addPoints(ladder._myRoute.getDestinations());
		// dro.setup(cs);
		// tpo.setup(cs);

		// now run through to completion
		while (cs.getTime() < 5400000) {
			cs.step();
		}

		// dro.tearDown(cs);
		// tpo.tearDown(cs);
		assertEquals(120, merlin.getStatus().getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "at demanded speed");

	}

}
