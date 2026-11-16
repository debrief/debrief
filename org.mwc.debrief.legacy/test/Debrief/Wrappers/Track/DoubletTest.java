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

package Debrief.Wrappers.Track;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.Wrappers.Track.Doublet.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.Wrappers.Track.Doublet.*;
import java.awt.Color;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.Algorithms.Conversions;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class DoubletTest {

	private double convertAndTest(final double myCrseDegs, final double bearingDegs, final double mySpeedKts,
			final double observedFreq) {
		final double myCrseRads = Conversions.Degs2Rads(myCrseDegs);
		final double bearingRads = Conversions.Degs2Rads(bearingDegs);
		return FrequencyCalcs.calcDopplerComponent(bearingRads, myCrseRads, mySpeedKts, observedFreq);
	}

	@Test
	public void testCorrected() {
		double res = convertAndTest(320, 28, 8, 300);
		assertEquals(0.304, res, 0.1, "right freq");

		res = convertAndTest(320, 328, 8, 300);
		assertEquals(0.805, res, 0.1, "right freq");

		res = convertAndTest(320, 158, 8, 300);
		assertEquals(-0.7734, res, 0.01, "right freq");

		res = convertAndTest(320, 158, 9, 300);
		assertEquals(-0.870, res, 0.01, "right freq");

		res = convertAndTest(150, 158, 9, 300);
		assertEquals(0.906, res, 0.01, "right freq");
	}

	@Test
	public void testTwoWay() {
		final SensorWrapper rxSensor = new SensorWrapper("Receiver_Sensor");
		final SensorContactWrapper receiver = new SensorContactWrapper("host", new HiResDate(1000), null, 90d, null,
				Color.red, "label", 0, "Receiver");
		receiver.setOrigin(new WorldLocation(0.1, 0.1, 0d));
		receiver.setSensor(rxSensor);
		final double targetCourse = 120;
		final double targetSpeed = 15;
		final Fix targetFix = new Fix(new HiResDate(2000), new WorldLocation(2, 2, 2d), targetCourse, targetSpeed);
		final FixWrapper targetFixW = new FixWrapper(targetFix);
		final WorldLocation targetLocation = new WorldLocation(0, 0, 0d);
		final HiResDate startTime = new HiResDate(1000);
		final HiResDate endTime = new HiResDate(1000000);
		final TrackSegment parent = new AbsoluteTMASegment(34, new WorldSpeed(12, WorldSpeed.Kts), targetLocation,
				startTime, endTime);
		final double hostCourse = 90d;
		final double hostSpeed = 10d;
		final Fix hostFix = new Fix(new HiResDate(4000), new WorldLocation(1, 1, 1d), hostCourse, hostSpeed);
		final FixWrapper hostFixW = new FixWrapper(hostFix);
		final double speed_of_sound = FrequencyCalcs.SpeedOfSoundKts;
		final SensorWrapper transmitter = new SensorWrapper("Transmitter");
		transmitter.setBaseFrequency(300d);
		final Doublet dub = new Doublet(receiver, targetFixW, parent, hostFixW) {

			@Override
			protected FixWrapper getTransmitterFix(final SensorWrapper source) {
				return targetFixW;
			}

		};
		final double freq = dub.getPredictedMultistaticFrequency(speed_of_sound, transmitter);
		assertEquals(297.914, freq, 0.01, "correct freq");
	}
}
