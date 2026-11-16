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

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Movement.RectangleWander.*;
import ASSET.Models.Decision.Movement.RectangleWander.*;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public final class RectangleWanderTest extends ASSET.Util.EditableTesting {

	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
		final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
		final WorldArea theArea = new WorldArea(topLeft, bottomRight);
		final RectangleWander tw = new RectangleWander(theArea, "rect wander");
		return tw;
	}

	@Test
	public final void testNoDemCourseSpeed() {
		final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
		final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
		final WorldArea theArea = new WorldArea(topLeft, bottomRight);
		final RectangleWander tw = new RectangleWander(theArea, "rect wander");

		final int seed = (int) (RandomGenerator.nextRandom() * 1000d);
		System.out.println("seed:" + seed);

		final Status stat = new Status(1, 0);
		stat.setLocation(SupportTesting.createLocation(3000, 3000));
		stat.setCourse(RandomGenerator.nextRandom() * 180);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		final DemandedStatus dem = null;

		final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

		assertNotNull(ds, "dem returned");
		// assertEquals("still on old course", 22, ds.getCourse(), 0);
		// assertEquals("still on old speed", MWC.Algorithms.Conversions.Kts2Mps(12),
		// ds.getSpeed(),
		// 0);

		final Surface fisher = new Surface(23);
		fisher.setName("Fisher");
		fisher.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		fisher.setStatus(stat);
		fisher.setDecisionModel(tw);
		fisher.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(10000);
		cs.addParticipant(fisher.getId(), fisher);

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "rect_wander_search.rep",
		// false);
		// TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300,
		// "rect_wander_search.png", null, false, true);
		// dro.setup(cs);
		// tpo.setup(cs);
		//
		// dro.outputThisArea(theArea);

		// now run through to completion
		while (cs.getTime() < 8000000) {
			cs.step();
		}

		// dro.tearDown(cs);
		// tpo.tearDown(cs);

	}

	@Test
	public final void testWithDemCourseSpeed() {
		final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
		final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
		final WorldArea theArea = new WorldArea(topLeft, bottomRight);
		final RectangleWander tw = new RectangleWander(theArea, "rect wander");
		tw.setSpeed(new WorldSpeed(44, WorldSpeed.M_sec));
		tw.setHeight(new WorldDistance(45, WorldDistance.METRES));

		final Status stat = new Status(1, 0);
		stat.setLocation(theArea.getCentre().add(new WorldVector(100, WorldDistance.METRES, 0)));
		stat.setCourse(22);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final DemandedStatus dem = null;

		final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

		assertNotNull(ds, "dem returned");
		assertEquals(45, ds.getHeight(), 0, "new demanded Height");
		assertEquals(44, ds.getSpeed(), 0, "new demanded speed");
	}
}
