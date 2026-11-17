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

import static ASSET.Models.Decision.Tactical.MarkDip.*;
import ASSET.Models.Decision.Tactical.MarkDip.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Movement.Move;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class MarkDipTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final MarkDip md = new MarkDip(new WorldDistance(430, WorldDistance.METRES));
		return md;
	}

	// TODO FIX-TEST
	public void NtestLower() {
		final MarkDip md = new MarkDip(new WorldDistance(430, WorldDistance.METRES));
		md.setName("Dip at 430m");

		final Helo hel = new Helo(12);

		final DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
		final Status st = new Status(11, 0);
		st.setLocation(createLocation(0, 0));
		st.getLocation().setDepth(-100);
		st.setSpeed(new WorldSpeed(1, WorldSpeed.M_sec));
		final double waterLowerRate = 4;
		final double airLowerRate = 6;
		final double waterRaiseRate = 1;
		final double airRaiseRate = 3;
		final double lowerPause = 3;
		final double raisePause = 5;

		final DippingActiveBroadbandSensor dip = new DippingActiveBroadbandSensor(12,
				new WorldSpeed(airLowerRate, WorldSpeed.M_sec), new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
				new Duration(lowerPause, Duration.SECONDS), new Duration(raisePause, Duration.SECONDS),
				new WorldSpeed(waterLowerRate, WorldSpeed.M_sec), new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

		hel.addSensor(dip);
		hel.setDemandedStatus(ds);
		hel.setStatus(st);
		hel.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

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
		hel.setMovementChars(chars);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(12, hel);

		final Sequence sq = new Sequence();
		sq.insertAtHead(md);
		final Move mv = new Move();
		mv.setCourse(new Double(0));
		mv.setSpeed(new WorldSpeed(100, WorldSpeed.M_sec));
		mv.setDistance(new WorldDistance(12, WorldDistance.KM));
		mv.setName("travel to location");

		final Move mv2 = new Move();
		mv2.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
		mv2.setCourse(new Double(12));
		mv2.setName("go into hover");

		sq.insertAtHead(mv2);
		sq.insertAtHead(mv);

		hel.setDecisionModel(sq);
		cs.setScenarioStepTime(2000);

		final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400, "mark_dip.png", null, true,
				true, false, "test observer", true);
		tpo.setup(cs);
		final DebriefReplayObserver dbo = new DebriefReplayObserver("./test_reports", "mark_dip.rep", false,
				"test observer", true);
		dbo.setup(cs);

		for (int i = 0; i < 190; i++) {
			cs.step();
			System.out.println("status:" + hel.getActivity() + " len:" + dip.getCableLength());
		}

		tpo.tearDown(cs);
		dbo.tearDown(cs);

		assertEquals(530, dip.getCableLength(), 0.0d, "sensor correctly deployed");

	}
}
