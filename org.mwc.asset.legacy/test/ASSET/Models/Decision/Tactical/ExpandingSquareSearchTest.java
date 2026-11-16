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

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.ExpandingSquareSearch.*;
import ASSET.Models.Decision.Tactical.ExpandingSquareSearch.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class ExpandingSquareSearchTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ExpandingSquareSearch(true, 12, null, null, null, "my name");
	}

	@Test
	public final void testCreateRoute() {

		WorldLocation.setModel(new CompletelyFlatEarth());
		final Integer maxLegs = new Integer(10);
		final boolean flyClockwise = true;
		final WorldLocation origin = createLocation(0, 0);
		final WorldDistance trackSpacing = new WorldDistance(1, WorldDistance.DEGS);
		final double initialTrack = 0;

		final String myName = "ssn Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxDepth = 0;
		final double minDepth = -400;
		final double turnCircle = 100;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turnCircle,
						defaultClimbRate, defaultDiveRate, maxDepth, minDepth);
		final double currentSpeed = 10;

		//////////////////////////////////////////////////
		WorldPath wp = createSearchRoute(maxLegs, chars, flyClockwise, origin, trackSpacing, initialTrack,
				currentSpeed);
		//////////////////////////////////////////////////

		// with 10 legs we will have 11 nodes
		assertEquals(11, wp.size(), 0, "correct length");

		// with 10 legs we should end up to the north
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLong() > 0, "last point to east");
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLat() > 0, "last point to north");

		//////////////////////////////////////////////////
		// REPEAT TO ANTI=CLOCKWISE
		//////////////////////////////////////////////////
		wp = createSearchRoute(maxLegs, chars, !flyClockwise, origin, trackSpacing, initialTrack, currentSpeed);

		// with 10 legs we will have 11 nodes
		assertEquals(11, wp.size(), 0, "correct length");

		// with 10 legs we should end up to the north
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLong() < 0, "last point to west");
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLat() > 0, "last point to north");

		//////////////////////////////////////////////////
		// Chuck in another point so we are now going south
		//////////////////////////////////////////////////
		wp = createSearchRoute(new Integer(maxLegs.intValue() + 1), chars, flyClockwise, origin, trackSpacing,
				initialTrack, currentSpeed);

		// with 10 legs we will have 11 nodes
		assertEquals(12, wp.size(), 0, "correct length");

		// with 10 legs we should end up to the north
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLong() > 0, "last point to east");
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLat() < 0, "last point to north");

		//////////////////////////////////////////////////
		// Chuck in another point so we are now going south
		//////////////////////////////////////////////////
		wp = createSearchRoute(new Integer(maxLegs.intValue() + 1), chars, !flyClockwise, origin, trackSpacing,
				initialTrack, currentSpeed);

		// with 10 legs we will have 11 nodes
		assertEquals(12, wp.size(), 0, "correct length");

		// with 10 legs we should end up to the north
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLong() < 0, "last point to west");
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLat() < 0, "last point to north");

		//////////////////////////////////////////////////
		// idiot check for handling only one point
		//////////////////////////////////////////////////
		wp = createSearchRoute(new Integer(1), chars, !flyClockwise, origin, trackSpacing, initialTrack,
				currentSpeed);

		// with 10 legs we will have 11 nodes
		assertEquals(2, wp.size(), 0, "correct length");

		// with 10 legs we should end up to the north
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLong() == 0, "last point to north");
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLat() > 0, "last point to north");

		//////////////////////////////////////////////////
		// idiot check for handling zero points
		//////////////////////////////////////////////////
		wp = createSearchRoute(new Integer(0), chars, !flyClockwise, origin, trackSpacing, initialTrack,
				currentSpeed);

		// with 10 legs we will have 11 nodes
		assertEquals(1, wp.size(), 0, "correct length");

		// with 10 legs we should end up to the north
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLong() == 0, "last point to north");
		assertTrue(wp.getLocationAt(wp.getPoints().size() - 1).getLat() == 0, "last point to north");
	}

	@Test
	public final void testFollowRoute() {
		// WorldLocation.setModel(new CompletelyFlatEarth());
		final Integer maxLegs = new Integer(10);
		final boolean flyClockwise = true;
		final WorldLocation origin = createLocation(0, 0);
		final WorldDistance trackSpacing = new WorldDistance(10, WorldDistance.NM);
		final double initialTrack = 0;
		final ExpandingSquareSearch ess = new ExpandingSquareSearch(flyClockwise, initialTrack, maxLegs, origin,
				trackSpacing, "the square");

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxHeight = 400;
		final double minHeight = 0;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final ParticipantType cp = new Helo(12);
		cp.setName("h_alpha");
		cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		cp.setDecisionModel(ess);
		cp.setMovementChars(chars);
		final Status stat = new Status(12, 1000);
		stat.setLocation(origin.add(new WorldVector(123, 0.3, 0)));
		stat.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));
		stat.setCourse(122);
		cp.setStatus(stat);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, cp);
		cs.setScenarioStepTime(2000);

		// do a first step
		cs.step();

		// check we're working
		assertEquals(cp.getActivity(), ess.getActivity(), "our decision is running");

		// now run through to completion
		while (cs.getTime() < 25000000) {
			cs.step();
			this.recordThis(cp.getStatus(), cp, cs.getTime());
		}

		// check we've finished the turns
		// assertEquals("search now complete", cp.getActivity(),
		// CoreParticipant.INACTIVE_DESCRIPTOR);

		// check we've ended up to the north west
		// assertTrue("ended up further north", cp.getStatus().getLocation().getLat() >
		// 0);
		// assertTrue("ended up further east", cp.getStatus().getLocation().getLong() >
		// 0);
	}

	@Test
	public final void testUnattainablePoints() {
		WorldLocation.setModel(new CompletelyFlatEarth());
		final Integer maxLegs = new Integer(19);
		final boolean flyClockwise = true;
		final WorldLocation origin = createLocation(0, 00);
		final WorldDistance trackSpacing = new WorldDistance(500, WorldDistance.METRES);
		final double initialTrack = 0;
		final ExpandingSquareSearch ess = new ExpandingSquareSearch(flyClockwise, initialTrack, maxLegs, origin,
				trackSpacing, "the square");

		final String myName = "ssn Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxDepth = 0;
		final double minDepth = -400;
		final double turnCircle = 1100;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turnCircle,
						defaultClimbRate, defaultDiveRate, maxDepth, minDepth);

		final ParticipantType cp = new Helo(12);
		cp.setName("h_delta");
		cp.setCategory(new Category(Category.Force.RED, Category.Environment.AIRBORNE, Category.Type.HELO));
		cp.setDecisionModel(ess);
		cp.setMovementChars(chars);
		final Status stat = new Status(12, 1000);
		stat.setLocation(origin.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), 0.03, 0)));
		stat.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));
		stat.setCourse(122);
		cp.setStatus(stat);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, cp);
		cs.setScenarioStepTime(2000);

		// this.startListeningTo(cp, "new_list2", true, true, true);

		// do a first step
		cs.step();

		// check we created the destinations
		assertTrue(ess._myRoute.getDestinations().size() < maxLegs.intValue(), "we didn't create all the destinations");
		assertTrue(ess._myRoute.getDestinations().size() > 0, "but we created some..");

		// this.recordThis(cp.getStatus(), cp);

		// check we're working
		assertEquals(cp.getActivity(), ess.getActivity(), "our decision is running");

		// now run through to completion
		while (cs.getTime() < 2000000) {
			cs.step();
		}

		this.endRecording(cs);

		// check we've finished the turns
		assertEquals(CoreParticipant.INACTIVE_DESCRIPTOR, cp.getActivity(), "search now complete");

		// check we've ended up to the north west
		assertTrue(cp.getStatus().getLocation().getLat() < 0, "ended up further south");
		assertTrue(cp.getStatus().getLocation().getLong() > 0, "ended up further east");

		assertTrue(_listeningList == null, "stopped recording");

	}
}
