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

package org.mwc.asset.netCore.test;

import java.util.Vector;
import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * A few small sample scenarios, for the NetAsset client and for CoreTest.
 */
public final class SampleScenarios {

	public static class MyPart extends Surface {
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;

		public MyPart(final int id, final String force) {
			super(id);
			setName("p" + id);
			this.setCategory(new Category(force, Category.Environment.SURFACE, Category.Type.FRIGATE));
			final WorldLocation centre = new WorldLocation(12, 12, 2);
			final WorldDistance area = new WorldDistance(12, WorldDistance.NM);
			final DecisionType wander = new Wander(centre, area);
			wander.setName("DefaultWander");
			this.setDecisionModel(wander);
			final Status newStat = new Status(12, 0);
			newStat.setLocation(new WorldLocation(11 + id / 100d, 11 - id / 100d, 11));
			newStat.setCourse(12);
			newStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
			this.setStatus(newStat);

			final Vector<TypedRangeDoublet> rangeDoublets = new Vector<TypedRangeDoublet>();
			rangeDoublets.add(new TypedRangeDoublet(null, new WorldDistance(10, WorldDistance.NM)));
			// and a sensor
			final TypedCookieSensor sensor = new TypedCookieSensor(id * 30, rangeDoublets);
			this.addSensor(sensor);

		}
	}

	public static class MyScen extends CoreScenario {
		public MyScen() {
			this.setScenarioStepTime(new Duration(1, Duration.MINUTES));
			this.setStepTime(new Duration(1, Duration.SECONDS));
		}
	}

	public static MyPart testParticipant;

	private static Vector<ScenarioType> _myScenarios;

	public static Vector<ScenarioType> getScenarioList() {
		if (_myScenarios == null) {
			_myScenarios = new Vector<ScenarioType>();

			final CoreScenario scen = new MyScen();
			scen.setName("aaa");
			testParticipant = new MyPart(12, Category.Force.RED);
			final CoreParticipant cp2 = new MyPart(13, Category.Force.BLUE);
			final CoreParticipant cp3 = new MyPart(14, Category.Force.BLUE);
			final CoreParticipant cp4 = new MyPart(22, Category.Force.RED);
			final CoreParticipant cp5 = new MyPart(23, Category.Force.BLUE);
			final CoreParticipant cp6 = new MyPart(24, Category.Force.BLUE);
			final CoreParticipant cp7 = new MyPart(32, Category.Force.BLUE);
			final CoreParticipant cp8 = new MyPart(33, Category.Force.RED);
			final CoreParticipant cp9 = new MyPart(34, Category.Force.BLUE);
			scen.addParticipant(testParticipant.getId(), testParticipant);
			scen.addParticipant(cp2.getId(), cp2);
			scen.addParticipant(cp3.getId(), cp3);
			final CoreScenario scen2 = new MyScen();
			scen2.addParticipant(cp4.getId(), cp4);
			scen2.addParticipant(cp5.getId(), cp5);
			scen2.addParticipant(cp6.getId(), cp6);
			scen2.setName("aab");
			final CoreScenario scen3 = new MyScen();
			scen3.addParticipant(cp7.getId(), cp7);
			scen3.addParticipant(cp8.getId(), cp8);
			scen3.addParticipant(cp9.getId(), cp9);
			scen3.setName("aac");

			_myScenarios.add(scen);
			_myScenarios.add(scen2);
			_myScenarios.add(scen3);
		}

		return _myScenarios;
	}

	private SampleScenarios() {
	}
}
