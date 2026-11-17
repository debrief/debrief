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

package MWC.Algorithms;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Algorithms.FrequencyCalcs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.Algorithms.FrequencyCalcs.*;
import MWC.GenericData.WorldSpeed;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class FrequencyCalcsTest {

	/**
	 * test taken from here:
	 * http://physics.bu.edu/~redner/211-sp06/class19/class19_doppler.html
	 */
	@Test
	public void testBatExample1() {
		final double Cms = 340d;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI / 4;
		final double osMS = 20;
		final double tgtMS = 8;
		final double brgRads = Math.PI / 4;
		final double f0 = 58.1;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(60.1, res, 0.01, "correct result");
	}

	/**
	 * test taken from here:
	 * http://physics.bu.edu/~redner/211-sp06/class19/class19_doppler.html
	 */
	@Test
	public void testBatExample2() {
		final double Cms = 340d;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI / 4;
		final double osMS = 8;
		final double tgtMS = 20;
		final double brgRads = Math.PI + Math.PI / 4;
		final double f0 = 56;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(58.1, res, 0.01, "correct result");
	}

	@Test
	public void testDemoFigures() {
		// (figures as provided by Iain)
		final double osKts = 2.5;
		final double osDegs = 208.2;
		final double tgtKts = 9;
		final double tgtDegs = 350.17;
		final double bearing = 260.47;
		final double Ckts = 2951;
		final double f0 = 150;

		final double osMS = new WorldSpeed(osKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
		final double tgtMS = new WorldSpeed(tgtKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
		final double osRads = Math.toRadians(osDegs);
		final double tgtRads = Math.toRadians(tgtDegs);
		final double brgRads = Math.toRadians(bearing);
		final double Cms = new WorldSpeed(Ckts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);

		final double relBrg = bearing - osDegs;
		final double OSL = osKts * Math.cos(Math.toRadians(-relBrg));
		final double ATB = tgtDegs - bearing;
		final double TSL = tgtKts * Math.cos(Math.toRadians(ATB));

		assertEquals(1.5298, OSL, 0.0001, "correct OSL");
		assertEquals(0.04712, TSL, 0.0001, "correct TSL");

		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		// calcObservedFreqCollate(f0, Cms, brgRads,
		// osRads, osMS, tgtRads, tgtMS);

		assertEquals(150.0750, res, 0.001, "correct obs");

	}

	/**
	 * test taken from here:
	 * http://formulas.tutorvista.com/physics/doppler-shift-formula.html
	 */
	@Test
	public void testDopplerExample1() {
		final double Cms = 330;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI + Math.PI / 4;
		final double osMS = 15;
		final double tgtMS = 15;
		final double brgRads = Math.PI / 4;
		final double f0 = 500;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(547.62, res, 0.01, "correct result");
	}

	/**
	 * test taken from here:
	 * http://formulas.tutorvista.com/physics/doppler-shift-formula.html
	 */
	@Test
	public void testDopplerExample2() {
		final double Cms = 340;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI + Math.PI / 4;
		final double osMS = 20;
		final double tgtMS = 0;
		final double brgRads = Math.PI / 4;
		final double f0 = 1000;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(1058.82, res, 0.01, "correct result");
	}

	/**
	 * test taken from here:
	 * http://formulas.tutorvista.com/physics/doppler-shift-formula.html
	 */
	@Test
	public void testDopplerExample3() {
		final double Cms = 340;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI / 4;
		final double osMS = 20;
		final double tgtMS = 0;
		final double brgRads = Math.PI + Math.PI / 4;
		final double f0 = 1000;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(941d, res, 1d, "correct result");
	}

	/**
	 * test taken from here:
	 * https://www.thermaxxjackets.com/doppler-effect-frequency-change/
	 */
	@Test
	public void testDopplerExample4() {
		final double Cms = 340.29;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI / 4;
		final double osMS = 0;
		final double tgtMS = 15.64;
		final double brgRads = Math.PI + Math.PI / 4;
		final double f0 = 700;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(734d, res, 1d, "correct result");
	}

	/**
	 * test taken from here:
	 * https://www.thermaxxjackets.com/doppler-effect-frequency-change/
	 */
	@Test
	public void testDopplerExample5() {
		final double Cms = 340.29;
		final double osRads = Math.PI / 4;
		final double tgtRads = Math.PI / 4;
		final double osMS = 0;
		final double tgtMS = 15.64;
		final double brgRads = Math.PI / 4;
		final double f0 = 700;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(669d, res, 1d, "correct result");
	}

	/**
	 * test taken from here:
	 * https://www.thermaxxjackets.com/doppler-effect-frequency-change/
	 */
	@Test
	public void testDopplerExample6() {
		final double Cms = 340.29;
		final double osRads = Math.PI;
		final double tgtRads = 0;
		final double osMS = 13.4112;
		final double tgtMS = 12.517;
		final double brgRads = Math.PI;
		final double f0 = 700;
		final double res = FrequencyCalcs.calcPredictedFreqSI(Cms, osRads, tgtRads, osMS, tgtMS, brgRads, f0);
		assertEquals(755d, res, 1d, "correct result");
	}

	@Test
	public void testTowards() {
		assertEquals(1d, travellingTowards(Math.toRadians(33)));
		assertEquals(1d, travellingTowards(Math.toRadians(-33)));
		assertEquals(1d, travellingTowards(Math.toRadians(333)));
		assertEquals(1d, travellingTowards(Math.toRadians(-333)));

		assertEquals(-1d, travellingTowards(Math.toRadians(133)));
		assertEquals(-1d, travellingTowards(Math.toRadians(-133)));
		assertEquals(-1d, travellingTowards(Math.toRadians(473)));
		assertEquals(-1d, travellingTowards(Math.toRadians(-473)));
	}

	// public void testDopplerShiftLowLevel()
	// {
	// final double SPEED_OF_SOUND = 1500;
	// final double F_NOUGHT = 150;
	//
	// final WorldLocation hostLoc = new WorldLocation(4, 4, 0);
	// final WorldLocation tgtLoc = new WorldLocation(4, 7, 0);
	// double hostCourse = MWC.Algorithms.Conversions.Degs2Rads(60);
	// double hostSpeed = 8d; // MWC.Algorithms.Conversions.Kts2Yps(8);
	// double tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(300);
	// double tgtSpeed = 4; // MWC.Algorithms.Conversions.Kts2Yps(4);
	//
	// double dLat = hostLoc.getLat() - tgtLoc.getLat();
	// double dLong = hostLoc.getLong() - tgtLoc.getLong();
	//
	// double dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 1.00133, dShift, 0.00001d);
	//
	// // and another permutation
	// hostCourse = MWC.Algorithms.Conversions.Degs2Rads(12d);
	// tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(333d);
	// hostSpeed = 3;
	// tgtSpeed = 4;
	//
	// dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 0.9995840, dShift, 0.00001d);
	//
	// // move to southern hemi
	// hostLoc.setLat(-4);
	// tgtLoc.setLat(-5);
	// hostCourse = 0;
	// tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(180);
	//
	// dLat = hostLoc.getLat() - tgtLoc.getLat();
	// dLong = hostLoc.getLong() - tgtLoc.getLong();
	//
	// dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 1.004427, dShift, 0.00001d);
	//
	// hostCourse = MWC.Algorithms.Conversions.Degs2Rads(12d);
	// tgtCourse = MWC.Algorithms.Conversions.Degs2Rads(333d);
	// hostSpeed = 3;
	//
	// dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 0.99908, dShift, 0.00001d);
	//
	// hostLoc.setLong(-2);
	// tgtLoc.setLong(-3);
	// dLat = hostLoc.getLat() - tgtLoc.getLat();
	// dLong = hostLoc.getLong() - tgtLoc.getLong();
	//
	// dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 0.99914, dShift, 0.00001d);
	//
	// // slow down target, so they're divering more slowly
	// hostSpeed = 0.1;
	//
	// dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 1.000768, dShift, 0.000001d);
	//
	// // stop them both
	// hostSpeed = 0d;
	// tgtSpeed = 0d;
	//
	// dShift = calcDopplerShift(SPEED_OF_SOUND, hostCourse, tgtCourse,
	// hostSpeed, tgtSpeed, dLat, dLong, F_NOUGHT);
	//
	// assertEquals("correct doppler shift", 1.000, dShift, 0.000001d);
	// }
}
