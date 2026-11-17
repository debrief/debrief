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

import static ASSET.Models.Movement.TurnAlgorithm.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Models.Movement.TurnAlgorithm.*;
import ASSET.ParticipantType;
import ASSET.Models.MWCModel;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.SSK;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.Conversions;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TurnAlgorithmTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testAlmostOnCourseTurn() {
		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

		final double demCourse = 307.2185479616559;
		final double curCourse = 307.2185479359266;

		// ok, set up our input data
		Status stat = new Status(0, 0);
		stat.setCourse(curCourse);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 0);
		dem_stat.setCourse(demCourse);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxHeight = 200;
		final double minHeight = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxHeight, minHeight);

		// so, how far should we turn in a second?
		// final double sec_turn =
		// chars.calculateTurnRate(stat.getSpeed().getValueIn(WorldSpeed.M_sec));

		stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);

		assertEquals(demCourse, stat.getCourse(), 0, "not on demanded course");

	}

	@Test
	public void testHeloHeightChange() {

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 40;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 10;
		final double defaultDiveRate = 15;
		final double maxHeight = 400;
		final double minHeight = 0;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 20;
		final double defaultDiveSpeed = 30;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Helo bravo = new Helo(12);
		bravo.setMovementChars(chars);
		bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

		final Status stat = new Status(12, 0);
		final WorldLocation origin = createLocation(0, 0);
		stat.setLocation(origin);
		stat.setCourse(0);
		stat.setTime(0);
		stat.setSpeed(new WorldSpeed(110, WorldSpeed.M_sec));
		origin.setDepth(-140);

		final SimpleDemandedStatus sds = new SimpleDemandedStatus(1000, stat);
		sds.setHeight(80);
		sds.setSpeed(110);

		final TurnAlgorithm turner = new TurnAlgorithm();

		// do the first step
		Status res = turner.doTurn(stat, sds, chars, 1000);

		// see how it looks
		assertEquals(70, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-125, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 2000);

		// see how it looks
		assertEquals(30, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-110, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 3000);

		// see how it looks
		assertEquals(30, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-95, res.getLocation().getDepth(), 0, "dived");

		// // ok, now move forward a little more
		// res = turner.doTurn(res, sds, chars, 4000);
		//
		// // see how it looks
		// assertEquals("decelerated", 30, res.getSpeed(), 0);
		// assertEquals("dived", -80, res.getLocation().getHeight(), 0);

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 5000);

		// see how it looks
		assertEquals(40, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-80, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 6000);

		// see how it looks
		assertEquals(50, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-80, res.getLocation().getDepth(), 0, "dived");

		// hey, let's ask to climb
		sds.setHeight(120);

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 6250);

		// see how it looks
		assertEquals(52.5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-82.5, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 6750);

		// see how it looks
		assertEquals(32.5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-87.5, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 7000);

		// see how it looks
		assertEquals(22.5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-90, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 8000);

		// see how it looks
		assertEquals(20, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-100, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 9000);

		// see how it looks
		assertEquals(20, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-110, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 10000);

		// see how it looks
		assertEquals(20, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-120, res.getLocation().getDepth(), 0, "dived");

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 11000);

		// see how it looks
		assertEquals(30, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-120, res.getLocation().getDepth(), 0, "dived");

		// ////////////////////////////////////////////////
		// lets repeat it all using just three steps
		// ////////////////////////////////////////////////

		stat.setTime(0);
		stat.getLocation().setDepth(-140);
		stat.setSpeed(new WorldSpeed(110, WorldSpeed.M_sec));

		sds.setSpeed(110);
		sds.setHeight(80);

		// ok, now move forward a little more
		res = turner.doTurn(stat, sds, chars, 6000);

		// see how it looks
		assertEquals(100, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-80, res.getLocation().getDepth(), 0, "dived");

		sds.setHeight(120);

		// ok, now move forward a little more
		res = turner.doTurn(res, sds, chars, 11000);

		// see how it looks
		assertEquals(100, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0, "decelerated");
		assertEquals(-120, res.getLocation().getDepth(), 0, "dived");

	}

	@Test
	public void testHeloStraight() {

		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		final MWC.Algorithms.EarthModels.CompletelyFlatEarth flatModel = new MWC.Algorithms.EarthModels.CompletelyFlatEarth();
		MWC.GenericData.WorldLocation.setModel(flatModel);
		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

		// ok, set up our input data
		final Status stat = new Status(0, 5);
		stat.setCourse(0);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 5);
		dem_stat.setCourse(0);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		dem_stat.setTime(0);

		ASSET.Models.Movement.SimpleDemandedStatus dem = new ASSET.Models.Movement.SimpleDemandedStatus(0,
				dem_stat);

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

		ASSET.Participants.Status res = null;

		// start with straight line
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		WorldVector wv = res.getLocation().subtract(origin);
		Assertions.assertEquals(MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 40, 0.001, "Straight, const spd");

		// now do decelerating (fitting in to single time step)
		dem_stat.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));
		dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(35, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		wv = res.getLocation().subtract(origin);

		// so, how far should we have travelled: change of 25 m/s takes 1
		// secs at 25 m/sec/sec.
		// so we travel at 22.5 m/sec for 1 secs then 10 m/sec for remaining
		// 9 secs
		// total distance is thus 105m
		final double t1_expected_range = 112.5;
		Assertions.assertEquals(t1_expected_range, MWC.Algorithms.Conversions.Degs2m(wv.getRange()),
				0.001, "Straight, decelerating");
		Assertions.assertEquals(res.getSpeed().getValueIn(WorldSpeed.M_sec), 10, 0.001, "Straight, decelerating");
		Assertions.assertEquals(res.getCourse(), stat.getCourse(), 0.001, "Straight, decelerating");

		// now do decelerating (not fitting in single time step)
		dem_stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
		dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(100, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 2000);
		wv = res.getLocation().subtract(origin);
		// so, how far should we have traveled? It will take 4 secs to slow
		// from 100 to 0 at 25 m/sec/sec
		// the step is only 1/2 this, so we will slow down to 50 kts -
		// travelling at an average of 75 m_sec through
		// the time step. So, we will travel at 75 m_sec for 2 secs for
		// total of 375m.
		final int t2_expected_range = 150;
		Assertions.assertEquals(t2_expected_range, MWC.Algorithms.Conversions.Degs2m(wv.getRange()),
				0.001, "Straight, decelerating");
		Assertions.assertEquals(50, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001, "Straight, decelerating");

		// now do decelerating (limited by minimum speed)
		dem_stat.setSpeed(new WorldSpeed(-10, WorldSpeed.M_sec));
		dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(20, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		wv = res.getLocation().subtract(origin);
		// so, how far should we have traveled? It will take 1 second to
		// slow from
		// 20 kts to -5, leaving 9 seconds at -5 m/sec. So 1 at 2.5 (12.5
		// m), 9 at
		// -5 (-45 m), total is -37.5m
		final double t3_expected_range = 37.5;
		Assertions.assertEquals(t3_expected_range, MWC.Algorithms.Conversions.Degs2m(wv.getRange()),
				0.001, "Straight, decelerating - travelled correct distance");
		Assertions.assertEquals(180, MWC.Algorithms.Conversions.Rads2Degs(wv.getBearing()),
				0.001, "Straight, decelerating - travelled backwards");
		Assertions.assertEquals(-5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001, "Straight, decelerating");

		// now do accelerating (not fitting in single time step)
		dem_stat.setSpeed(new WorldSpeed(100, WorldSpeed.M_sec));
		dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 5000);
		wv = res.getLocation().subtract(origin);
		// how far will we go?
		// it would take 10 secs to get to 100 from zero. We only have 5 -
		// so we will only get up to 50 m_sec.
		// it we accelerate linearly, we will travel at average of 25 m_secs
		// for 5 secs = 125m
		Assertions.assertEquals(125, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, accelerating");
		Assertions.assertEquals(50, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001, "Straight, accelerating");

		// now do accelerating (limited by max spd)
		dem_stat.setSpeed(new WorldSpeed(150, WorldSpeed.M_sec));
		dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(90, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		wv = res.getLocation().subtract(origin);
		// how far? We will take 1 sec to get to 100 m_sec, so 95 m_sec for
		// 1 sec plus 100 m_sec for 9 secs, total
		// dist of 995m
		Assertions.assertEquals(995, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, accelerating");
	}

	@Test
	public void testHeloTurnShape() {
		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0, 0, 0);

		// ok, set up our input data
		final Status stat = new Status(0, 5);
		stat.setCourse(0);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 5);
		dem_stat.setCourse(180);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));

		final ASSET.Models.Movement.SimpleDemandedStatus dem = new ASSET.Models.Movement.SimpleDemandedStatus(0,
				dem_stat);

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

		ASSET.Participants.Status res = null;

		final double turn_diam = 381.97186342054886;
		// check we've calculated it correctly
		assertEquals(turn_diam, chars.getTurningCircleDiameter(10), 0.01, "we're using correct turn diameter");

		// clockwise at steady speed, fit in one cycle
		res = new Status(stat);

		final ParticipantType cp = new SSK(12);
		cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		cp.setName("test SSK");

		// move forward the first couple of steps
		res = turner.doTurn(res, dem, chars, res.getTime() + 1000);

		// check we've changed course by the correct amount
		assertEquals(3, res.getCourse(), 0.001, "changed course by correct amount");
		assertEquals(10, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001, "maintained speed");

		// and again
		res = turner.doTurn(res, dem, chars, res.getTime() + 1000);
		// check we've changed course by the correct amount
		assertEquals(6, res.getCourse(), 0.001, "changed course by correct amount");
		assertEquals(10, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001, "maintained speed");

		// move forward another 58 steps (to get around to 180 degs)
		for (int j = 0; j < 58; j++) {
			res = turner.doTurn(res, dem, chars, res.getTime() + 1000);
		}

		// are we at correct location?
		final WorldLocation thisEndPoint = stat.getLocation().add(new WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(90), MWC.Algorithms.Conversions.m2Degs(turn_diam), 0));
		double range = thisEndPoint.subtract(res.getLocation()).getRange();
		range = MWC.Algorithms.Conversions.Degs2m(range);
		assertEquals(0, range, 0.001, "we're following the correct circle");

		// now see what happens when we slow down
		res.setLocation(new WorldLocation(origin));
		res.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		res.setCourse(0);
		res.setTime(0);

		dem.setSpeed(0);
		dem.setCourse(0);

		res = turner.doTurn(res, dem, chars, res.getTime() + 1000);

	}

	@Test
	public void testSSNDelayedTurn() {

		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0, 0, 0);

		// ok, set up our input data
		Status stat = new Status(0, 0);
		stat.setCourse(0);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 0);
		dem_stat.setCourse(0);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxHeight = 200;
		final double minHeight = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxHeight, minHeight);

		final ParticipantType cp = new CoreParticipant(23);
		cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		cp.setName("SSK");

		// create a scenario object just to please the track recorder
		final CoreScenario dummyScenario = new CoreScenario();

		startRecording("delayed_turn", true, true, false, dummyScenario);
		recordThis(stat, cp, stat.getTime());

		// more forwards, then turn
		for (int i = 0; i < 4; i++) {
			stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);
			recordThis(stat, cp, stat.getTime());
		}

		// check we've travelled the correct distance
		final WorldVector vec = stat.getLocation().subtract(origin);
		assertEquals(16, MWC.Algorithms.Conversions.Degs2m(vec.getRange()), 0.001, "travelled correct dist");

		dem.setCourse(180);

		// more forwards, then turn
		for (int i = 0; i < 158; i++) {
			stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);
			recordThis(stat, cp, stat.getTime());
		}

		assertEquals(180, stat.getCourse(), 0.001, "now on course");

		// and down a bit
		for (int i = 0; i < 4; i++) {
			stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);
			recordThis(stat, cp, stat.getTime());
		}

		endRecording(dummyScenario);

		assertEquals(180, stat.getCourse(), 0.001, "still on course");

	}

	@Test
	public void testSSNSimpleTurn() {

		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

		// ok, set up our input data
		Status stat = new Status(0, 0);
		stat.setCourse(0);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 0);
		dem_stat.setCourse(90);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxHeight = 200;
		final double minHeight = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxHeight, minHeight);

		// so, how far should we turn in a second?
		final double sec_turn = chars.calculateTurnRate(stat.getSpeed().getValueIn(WorldSpeed.M_sec));

		for (int i = 1; i < 30; i++) {
			stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);

			// check we've turned the correct amount
			assertEquals(i * sec_turn, stat.getCourse(), 0.001, "turned correct amount");
		}

	}

	@Test
	public void testSSNStraight() {
		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		final MWC.Algorithms.EarthModels.CompletelyFlatEarth flatModel = new MWC.Algorithms.EarthModels.CompletelyFlatEarth();
		MWC.GenericData.WorldLocation.setModel(flatModel);
		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

		// ok, set up our input data
		final Status stat = new Status(0, 0);
		stat.setCourse(0);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 0);
		dem_stat.setCourse(0);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		dem_stat.setTime(0);

		SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

		final String myName = "SSK Trial";
		final double accelRate = 0.5;
		final double decelRate = 0.2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxHeight = 200;
		final double minHeight = 10;
		final double turningCircle = 200;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, turningCircle);

		ASSET.Participants.Status res = null;

		// start with straight line
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		WorldVector wv = res.getLocation().subtract(origin);
		Assertions.assertEquals(40, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, const spd");

		// now do decelerating (not fitting in single time step)
		dem_stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		wv = res.getLocation().subtract(origin);
		// so, how far should we go?
		// if we decelerate at 0.2 m/sec/sec and we want to decelerate 7
		// m/s. This will take 35 secs. We've only
		// got 10 = so it's a uniform deceleration. After 10 secs will have
		// dec by 2 m/sec. Therefore mean speed is
		// (78 + 19)/2 = 18. So, 10 secs at 18 (180)
		Assertions.assertEquals(180, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, decelerating");
		Assertions.assertEquals(17, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001, "Straight, decelerating");
		Assertions.assertEquals(res.getCourse(), stat.getCourse(), 0.001, "Straight, decelerating");

		// now do decelerating (fitting in single time step)
		dem_stat.setSpeed(new WorldSpeed(15, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
		// how far should we be? Drop to 17 m/sec will take 20 secs. Mean
		// speed through this
		// period will be 17 m/sec (340m). Leaving 20 secs at 15 m/sec
		// (300m).
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 40000);
		wv = res.getLocation().subtract(origin);
		Assertions.assertEquals(640, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, decelerating");

		// now do accelerating (fitting in single time step)
		dem_stat.setSpeed(new WorldSpeed(14, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		// how far? Accelerate will take 4 secs - average is 13 (52m),
		// leaving 6 secs at 14 m/sec (84)
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		wv = res.getLocation().subtract(origin);
		Assertions.assertEquals(136, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, accelerating");

		// now do accelerating (not fitting in single time step)
		dem_stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		// how far? We need to accelerate by 7 m/sec. This will take 14 secs
		// at 0.5 m/sec/sec. We will accelerate
		// for 10 secs, taking us to 17 m/sec - average is 14.5 m/sec for 10
		// secs. Distance is: 145m
		wv = res.getLocation().subtract(origin);
		Assertions.assertEquals(145, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, accelerating");

		// now do accelerating (limited by max spd)
		dem_stat.setSpeed(new WorldSpeed(31, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
		// so, how far? Our max speed is 21. It will take 4 secs to do this
		// 2m/sec change (4 * 20 = 80), leaving
		// 6 secs at 21 m/sec (126).
		wv = res.getLocation().subtract(origin);
		Assertions.assertEquals(206, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001, "Straight, accelerating");

	}

	@Test
	public void testSSNTurn() {

		final TurnAlgorithm turner = new TurnAlgorithm();

		// put the earth model into flat mode
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

		// ok, set up our input data
		final Status stat = new Status(0, 0);
		stat.setCourse(0);
		stat.setFuelLevel(100);
		stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status dem_stat = new Status(0, 0);
		dem_stat.setCourse(90);
		dem_stat.setFuelLevel(100);
		dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

		final String myName = "SSK Trial";
		final double accelRate = 2;
		final double decelRate = 2;
		final double fuel_usage_rate = 2;
		final double maxSpeed = 21;
		final double minSpeed = -5;
		final double defaultClimbRate = 2;
		final double defaultDiveRate = 2;
		final double maxHeight = 200;
		final double minHeight = 10;
		final double turningCircle = 400;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.SSMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, turningCircle,
						defaultClimbRate, defaultDiveRate, maxHeight, minHeight);

		ASSET.Participants.Status res = null;

		// clockwise at steady speed, fit in one cycle
		// how long to travel through turn?
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
		assertEquals(200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()),
				0.01, "Turn clock, steady speed");
		assertEquals(200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()),
				0.01, "Turn clock, steady speed");

		// clockwise at steady speed, not fit in one cycle
		dem_stat.setCourse(95);
		dem = new SimpleDemandedStatus(0, dem_stat);
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
		assertEquals(200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()),
				0.01, "Turn anti-clock, steady speed");
		assertEquals(200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()),
				0.01, "Turn clock, steady speed");

		// anti-clockwise at steady speed, fit in one cycle
		dem_stat.setCourse(-90);
		dem = new SimpleDemandedStatus(0, dem_stat);
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
		assertEquals(200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()),
				0.01, "Turn anti-clock, steady speed");
		assertEquals(-200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()),
				0.01, "Turn anti-clock, steady speed");

		// anticlockwise at steady speed, not fit in one cycle
		dem_stat.setCourse(-95);
		dem = new SimpleDemandedStatus(0, dem_stat);
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
		assertEquals(200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()),
				0.01, "Turn anti-clock, steady speed");
		assertEquals(-200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()),
				0.01, "Turn anti-clock, steady speed");

		// clockwise accelerating
		dem_stat.setCourse(179.9999);
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 157079);
		assertEquals(res.getSpeed().getValueIn(WorldSpeed.M_sec), 4, 0.001, "long turn, final speed");
		assertEquals(res.getCourse(), 179.999, 0.001, "Long turn, final head");
		assertEquals(0.000, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()),
				0.01, "Long turn vert travel");
		assertEquals(400, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()),
				0.01, "Long turn horiz travel");

		// clockwise accelerating
		dem_stat.setCourse(-179.9999);
		dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
		dem = new SimpleDemandedStatus(0, dem_stat);
		res = turner.doTurn(stat, dem, chars, stat.getTime() + 157079);
		assertEquals(res.getSpeed().getValueIn(WorldSpeed.M_sec), 4, 0.001, "long turn, final speed");
		assertEquals(res.getCourse(), 180.000, 0.001, "Long turn, final head");
		assertEquals(0.0000, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()),
				0.01, "Long turn vert travel");
		assertEquals(-400, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()),
				0.01, "Long turn horiz travel");

	}

	@Test
	public void testZeroStepTime() {
		final TurnAlgorithm turner = new TurnAlgorithm();
		final Status oldStat = new Status(12, 12000);
		final SimpleDemandedStatus oldDem = null;
		final MovementCharacteristics chars = null;
		final Status res = turner.doTurn2(oldStat, oldDem, chars, oldStat.getTime());

		// check we're still at the old val
		// assertEquals("kept old value", res, oldStat);
		// assertEquals("time kept same", res.getTime(), oldStat.getTime(), 0);
	}

}
