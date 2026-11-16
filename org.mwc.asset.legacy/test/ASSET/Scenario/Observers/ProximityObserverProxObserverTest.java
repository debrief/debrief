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

package ASSET.Scenario.Observers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Scenario.Observers.ProximityObserver.*;
import ASSET.Scenario.Observers.ProximityObserver.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

// ////////////////////////////////////////////////
// and now general test
// ////////////////////////////////////////////////
public final class ProximityObserverProxObserverTest extends ASSET.Util.EditableTesting {
	boolean hasStopped = false;


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ProximityObserver(null, null, "here", true);
	}

	@Test
	public final void testNoDemCourseSpeed() {
		final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
		final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
		final WorldArea theArea = new WorldArea(topLeft, bottomRight);
		final RectangleWander tw = new RectangleWander(theArea, "rect wander");
		final RectangleWander tw2 = new RectangleWander(theArea, "rect wander 2");

		final Status stat = new Status(1, 0);
		stat.setLocation(SupportTesting.createLocation(2000, 4000));
		stat.setCourse(270);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		final Status stat2 = new Status(1, 0);
		stat2.setLocation(SupportTesting.createLocation(4000, 2000));
		stat2.setCourse(155);
		stat2.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		final DemandedStatus dem = null;

		final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

		assertNotNull(ds, "dem returned");
		// assertEquals("still on old course", 22, ds.getCourse(), 0);
		// assertEquals("still on old speed",
		// MWC.Algorithms.Conversions.Kts2Mps(12), ds.getSpeed(), 0);

		final Surface fisher = new Surface(23);
		fisher.setName("Fisher");
		fisher.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		fisher.setStatus(stat);
		fisher.setDecisionModel(tw);
		fisher.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		final Surface fisher2 = new Surface(25);
		fisher2.setName("Fisher2");
		fisher2.setCategory(
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		fisher2.setStatus(stat2);
		fisher2.setDecisionModel(tw2);
		fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(10000);
		cs.addParticipant(fisher.getId(), fisher);
		cs.addParticipant(fisher2.getId(), fisher2);

		hasStopped = false;

		// and the stop on proximity observer
		final StopOnProximityObserver stopper = new StopOnProximityObserver(new TargetType(Category.Force.RED),
				new TargetType(Category.Force.BLUE), new WorldDistance(0.6, WorldDistance.KM), "stop on proxim",
				true) {
			@Override
			protected void stopScenario() {
				hasStopped = true;
				super.stopScenario();
				System.out.println("stopped at:" + super._myScenario.getTime());
			}
		};

		stopper.setup(cs);
		System.out.println("started at:" + cs.getTime());

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "rect_wander_search.rep", false, "plotter", true);
		// TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300,
		// "rect_wander_search.png", null, false, true, "tester", true);
		// dro.setup(cs);
		// tpo.setup(cs);
		//
		// dro.outputThisArea(theArea);

		// now run through to completion
		while ((cs.getTime() < 120000000) && (!hasStopped)) {
			cs.step();
		}

		stopper.tearDown(cs);

		// dro.tearDown(cs);
		// tpo.tearDown(cs);

		// check that we stopped
		assertTrue(hasStopped, "Proximity sensor failed to notice vessels getting too close to each other");

	}

}
