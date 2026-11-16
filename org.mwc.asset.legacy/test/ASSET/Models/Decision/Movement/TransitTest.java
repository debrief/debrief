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

import static ASSET.Models.Decision.Movement.Transit.*;
import ASSET.Models.Decision.Movement.Transit.*;
import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
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

public class TransitTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final MWC.GUI.Editable ed = new Transit();
		return ed;
	}

	// TODO FIX-TEST
	public void NtestLooping() {

		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());
		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(1, 1, 0);

		// create the paths
		final MWC.GenericData.WorldPath path = new MWC.GenericData.WorldPath();

		final MWC.GenericData.WorldLocation loc2 = origin.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(90), MWC.Algorithms.Conversions.Yds2Degs(2000), 0));
		path.addPoint(loc2);

		final MWC.GenericData.WorldLocation loc3 = origin.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(180), MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

		path.addPoint(loc3);

		final MWC.GenericData.WorldLocation loc4 = origin.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(270), MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

		path.addPoint(loc4);

		final MWC.GenericData.WorldLocation loc5 = origin.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(0), MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

		path.addPoint(loc5);

		// create the objects
		final Transit transit = new Transit(path, new WorldSpeed(12, WorldSpeed.M_sec), true);

		// configure the transit
		transit.setLoop(true);
		transit.setReverse(false);

		// get going
		final ASSET.Participants.Status stat = new ASSET.Participants.Status(1, 1000);
		stat.setLocation(origin);
		stat.setCourse(0);
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		ASSET.Participants.DemandedStatus dem = transit.decide(stat, null, null, null, null, 1000l);

		// where are we heading?
		assertEquals(0, transit._currentDestination, "moving to first destination");
		assertEquals(90, ((SimpleDemandedStatus) dem).getCourse(), 0.001, "on correct course");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed");

		// let's position ourselves somewhere and check we're still heading for it
		stat.setLocation(origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45),
				MWC.Algorithms.Conversions.Yds2Degs(2819), 0)));
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// where are we heading?
		assertEquals(180, ((SimpleDemandedStatus) dem).getCourse(), 0.2, "on correct course");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed");

		// put us just outside the threshold , check we don't move onto the next one
		final MWC.GenericData.WorldLocation loc10 = loc2.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(0),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) + 100), 0));
		stat.setLocation(loc10);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// where are we heading?
		assertEquals(180, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed");

		// put us just inside the threshold , check we move onto the next one
		final MWC.GenericData.WorldLocation loc11 = loc2.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(225),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc11);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(1, transit._currentDestination, "moving to next destination");
		assertEquals(225, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// check still on track
		stat.setLocation(loc11);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we still heading for the next leg?
		assertEquals(1, transit._currentDestination, "moving to next destination");
		assertEquals(225, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// head for the next leg
		final MWC.GenericData.WorldLocation loc12 = loc3.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(315),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc12);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(2, transit._currentDestination, "moving to next destination");
		assertEquals(315, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// and the next leg
		final MWC.GenericData.WorldLocation loc13 = loc4.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(225),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc13);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(3, transit._currentDestination, "moving to next destination");
		assertEquals(45, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// and the last leg
		final MWC.GenericData.WorldLocation loc14 = loc5.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(135),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc14);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(0, transit._currentDestination, "moving to next destination");
		assertEquals(135, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// what if we are running in reverse?
		transit.setReverse(true);

		// put ourselves back one (so we are still heading for the last point)
		transit._currentDestination = 3;

		final MWC.GenericData.WorldLocation loc15 = loc5.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(45),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc15);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(2, transit._currentDestination, "moving to next destination");
		assertEquals(225, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// carry on going backwards
		final MWC.GenericData.WorldLocation loc16 = loc4.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(135),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc16);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(1, transit._currentDestination, "moving to next destination");
		assertEquals(135, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// carry on going backwards
		final MWC.GenericData.WorldLocation loc17 = loc3.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(45),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc17);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(0, transit._currentDestination, "moving to next destination");
		assertEquals(45, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// and what happens when we get to the start?
		final MWC.GenericData.WorldLocation loc18 = loc2.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(45),
				MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
		stat.setLocation(loc18);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(1, transit._currentDestination, "moving to next destination");
		assertEquals(225, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// continue forwards again
		stat.setLocation(loc12);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(2, transit._currentDestination, "moving to next destination");
		assertEquals(315, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		/////////////////////////////////////////////////
		// and again
		stat.setLocation(loc13);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(3, transit._currentDestination, "moving to next destination");
		assertEquals(45, ((SimpleDemandedStatus) dem).getCourse(), 0.1, "on correct course for new location");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed for new destination");

		// tell it to stop looping
		transit.setLoop(false);

		/////////////////////////////////////////////////
		// and the last one
		stat.setLocation(loc14);
		dem = transit.decide(stat, null, null, null, null, 1000l);

		// are we heading for the next leg?
		assertEquals(-1, transit._currentDestination, "moving to next destination");
		assertEquals(null, dem, "drop out of decision");
	}

	// TODO FIX-TEST
	public void NtestModelling1() {
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final WorldLocation first = createLocation(0, 10000);
		final WorldLocation second = createLocation(10000, 10000);
		final WorldLocation third = createLocation(10000, 0);

		final WorldPath wp = new WorldPath();
		wp.addPoint(first);
		wp.addPoint(second);
		wp.addPoint(third);
		final Transit tr = new Transit(wp, new WorldSpeed(12, WorldSpeed.M_sec), false);

		final ParticipantType cp = new SSN(12);
		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxDepth = 200;
		final double minDepth = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxDepth, minDepth);
		cp.setMovementChars(chars);

		final Status stat = new Status(0, 1000);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		stat.setLocation(origin);

		cp.setDecisionModel(tr);
		cp.setStatus(stat);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, cp);
		cs.setScenarioStepTime(5000);
		cs.step();

		while ((cp.getActivity() != CoreParticipant.INACTIVE_DESCRIPTOR) && (cs.getTime() < 5555500)) {
			cs.step();
		}

		assertEquals(CoreParticipant.INACTIVE_DESCRIPTOR, cp.getActivity(), "we've finished going through the series of points");
		assertEquals(2415000, cs.getTime(), 0, "finished on time");
	}

	// TODO FIX-TEST
	public void NtestModelling2() {
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final WorldLocation first = createLocation(0, 10000);
		final WorldLocation second = createLocation(10000, 10000);
		final WorldLocation third = createLocation(10000, 0);

		final WorldPath wp = new WorldPath();
		wp.addPoint(first);
		wp.addPoint(second);
		wp.addPoint(third);
		final Transit tr = new Transit(wp, new WorldSpeed(12, WorldSpeed.M_sec), true);
		tr.setName("Transit");

		final ParticipantType cp = new SSN(12);
		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxDepth = 200;
		final double minDepth = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxDepth, minDepth);
		cp.setMovementChars(chars);

		final Status stat = new Status(0, 1000);
		stat.setLocation(origin);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		cp.setDecisionModel(tr);
		cp.setStatus(stat);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, cp);
		cs.setScenarioStepTime(5000);
		cs.step();

		System.out.println("about to start test 2");

		while ((cp.getActivity() != CoreParticipant.INACTIVE_DESCRIPTOR) && (cs.getTime() < 2415500)) {
			cs.step();
		}

		assertEquals("Transit:Transit heading for waypoint:2", cp.getActivity(), "we're still going through the transit");

		// check we're heading back to the start
		final int nextPoint = 1; // last point would be size-1, but the transit heads back to the one
									// before this
		assertEquals(nextPoint, tr._currentDestination, "heading for first point");
		assertTrue(tr._in_reverse_transit, "it knows we're going backwards");

		// wait until we get back to the start
		while (tr._currentDestination != 0) {
			cs.step();
		}

		// right, now we're heading for the start- wait until we reach it
		while (tr._currentDestination == 0) {
			cs.step();
		}

		// right, see where we're going now
		assertEquals(1, tr._currentDestination, 0, "going to next step");
		assertFalse(tr._in_reverse_transit, "it knows we're going forward through them");

		// now wait until we're getting to the end
		while (tr._currentDestination != 2) {
			cs.step();
		}

		// right, now we're heading for the end - wait until we reach it
		while (tr._currentDestination == 2) {
			cs.step();
		}

		// now see where it's taking us now
		assertEquals(1, tr._currentDestination, "heading for last but one point");
		assertTrue(tr._in_reverse_transit, "it knows we're going backward through them");

	}

	// TODO FIX-TEST
	public void NtestModelling3() {
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final WorldLocation first = createLocation(0, 10000);
		final WorldLocation second = createLocation(10000, 10000);
		final WorldLocation third = createLocation(10000, 0);

		final WorldPath wp = new WorldPath();
		wp.addPoint(first);
		wp.addPoint(second);
		wp.addPoint(third);
		final Transit tr = new Transit(wp, new WorldSpeed(12, WorldSpeed.M_sec), true);
		tr.setReverse(false);
		tr.setName("Transit");

		final ParticipantType cp = new SSN(12);
		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxDepth = 200;
		final double minDepth = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxDepth, minDepth);
		cp.setMovementChars(chars);

		final Status stat = new Status(0, 1000);
		stat.setLocation(origin);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		cp.setDecisionModel(tr);
		cp.setStatus(stat);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, cp);
		cs.setScenarioStepTime(5000);
		cs.step();

		System.out.println("about to start test 3");
		while ((cp.getActivity() != CoreParticipant.INACTIVE_DESCRIPTOR) && (cs.getTime() < 2416000)) {
			cs.step();
		}

		assertEquals("Transit:Transit heading for waypoint:1", cp.getActivity(), "we're still going through the transit");

		// check we're heading back to the start
		int nextPoint = 0; // we head for the first point
		assertEquals(nextPoint, tr._currentDestination, "heading for first point");

		// wait until we've got back to the start
		while ((tr._currentDestination == 0) && (cs.getTime() < 2416000 * 2)) {
			cs.step();
		}

		assertEquals(1, tr._currentDestination, "heading for first point");

		// now run through to the end again, and see where we're going
		while ((tr._currentDestination != 2)) {
			cs.step();
		}

		// right - now we're heading for the last point
		while ((tr._currentDestination == 2)) {
			cs.step();
		}

		assertEquals("Transit:Transit heading for waypoint:1", cp.getActivity(), "we're still going through the transit");

		// check we're heading back to the start
		nextPoint = 0; // we head for the first point
		assertEquals(nextPoint, tr._currentDestination, "heading for first point");

	}

	@Test
	public void testSingleLocation() {

		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());
		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(1, 1, 0);

		// create the paths
		final MWC.GenericData.WorldPath path = new MWC.GenericData.WorldPath();

		final MWC.GenericData.WorldLocation loc2 = origin.add(new MWC.GenericData.WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(90), MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

		path.addPoint(loc2);

		// create the objects
		final Transit transit = new Transit(path, new WorldSpeed(12, WorldSpeed.M_sec), true);

		// configure the transit
		transit.setLoop(true);
		transit.setReverse(false);

		// get going
		final ASSET.Participants.Status stat = new ASSET.Participants.Status(1, 1000);
		stat.setLocation(origin);
		stat.setCourse(0);
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		ASSET.Participants.DemandedStatus dem = transit.decide(stat, null, null, null, null, 1000l);

		// where are we heading?
		assertEquals(0, transit._currentDestination, "moving to first destination");
		assertEquals(90, ((SimpleDemandedStatus) dem).getCourse(), 0.001, "on correct course");
		assertEquals(12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001, "on correct speed");

		// let's position ourselves somewhere and check we're still heading for it
		stat.setLocation(origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
				MWC.Algorithms.Conversions.Yds2Degs(1909), 0)));
		dem = transit.decide(stat, null, null, null, null, 1000l);
		assertEquals(0, transit._currentDestination, "moving to first destination");
		assertEquals(90, ((SimpleDemandedStatus) dem).getCourse(), 0.001, "on correct course");

		// let's position ourselves somewhere and check we're still heading for it
		stat.setLocation(origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
				MWC.Algorithms.Conversions.Yds2Degs(1909), 0)));
		dem = transit.decide(stat, null, null, null, null, 1000l);
		assertEquals(0, transit._currentDestination, "moving to first destination");
		assertEquals(90, ((SimpleDemandedStatus) dem).getCourse(), 0.001, "on correct course");

	}

}
