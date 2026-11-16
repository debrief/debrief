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

package org.mwc.debrief.multipath2.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.multipath2.model.SVP.*;
import org.mwc.debrief.multipath2.model.SVP.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import org.mwc.debrief.multipath2.model.MultiPathModel.CalculationException;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import flanagan.interpolation.LinearInterpolation;
import org.junit.jupiter.api.Test;

// /////////////////////////////////////////////////
// and the testing goes here
// /////////////////////////////////////////////////
public class SVPTest {
	public static final String SVP_FILE = "src/org/mwc/debrief/multipath2/model/test_svp.csv";
	public static final String SVP_FILE2 = "src/org/mwc/debrief/multipath2/model/test_svp2.csv";
	public static final String SVP_FILE_NO_ZERO = "src/org/mwc/debrief/multipath2/model/test_svp_noZero.csv";

	// TODO FIX-TEST
	public void NtestExtrapolate() {
		double res = SVP.extrapolateZero(3, 4, 6, 5);
		assertEquals(3d, res, "wrong extrapolated value");

		res = SVP.extrapolateZero(3, 4, 6, 6);
		assertEquals(2d, res, "wrong extrapolated value");

		res = SVP.extrapolateZero(3, 6, 6, 4);
		assertEquals(8d, res, "wrong extrapolated value");

		// now try loading it
		final SVP svp = new SVP();

		assertEquals(null, svp._depths, "not got data");

		try {
			svp.load(SVP_FILE_NO_ZERO);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		assertEquals(4, svp._depths.length, "not created extra point");
		assertEquals(4, svp._speeds.length, "not created extra point");

		assertEquals(0d, svp._depths[0], "not got zero depth");
		assertEquals(1503d, svp._speeds[0], "not got surface speed");

	}

	// TODO FIX-TEST
	public void NtestIndex() {
		final SVP svp = new SVP();

		assertEquals(null, svp._depths, "not got data");

		try {
			svp.load(SVP_FILE);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		assertEquals(4, svp._depths.length, "got data");

		int t1 = svp.pointBefore(0);
		assertEquals(0d, svp._depths[t1], "correct depth");

		t1 = svp.pointBefore(5);
		assertEquals(0d, svp._depths[t1], "correct depth");

		t1 = svp.pointBefore(15);
		assertEquals(00d, svp._depths[t1], "correct depth");

		t1 = svp.pointBefore(35);
		assertEquals(30d, svp._depths[t1], "correct depth");

		t1 = svp.pointBefore(50);
		assertEquals(40d, svp._depths[t1], "correct depth");

		t1 = svp.pointAfter(0);
		assertEquals(0d, svp._depths[t1], "correct depth");

		t1 = svp.pointAfter(5);
		assertEquals(30d, svp._depths[t1], "correct depth");

		t1 = svp.pointAfter(15);
		assertEquals(30d, svp._depths[t1], "correct depth");

		t1 = svp.pointAfter(35);
		assertEquals(40d, svp._depths[t1], "correct depth");

		t1 = svp.pointAfter(50);
		assertEquals(60d, svp._depths[t1], "correct depth");

		t1 = svp.pointAfter(150);
		assertEquals(-1, t1, "correct depth");

	}

	// TODO FIX-TEST
	public void NtestMean() {
		final SVP svp = new SVP();

		assertEquals(null, svp._depths, "not got data");

		try {
			svp.load(SVP_FILE);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		double mean = svp.getMeanSpeedBetween(30, 55);
		assertEquals(1510.125, mean, "correct mean");

		// now for a calc that spans a SVP step
		mean = svp.getMeanSpeedBetween(20, 55);
		assertEquals(1508.42, mean, 0.01, "correct mean");

		// now for a calc that goes from surface
		mean = svp.getMeanSpeedBetween(0, 35);
		assertEquals(1503.03, mean, 0.01, "correct mean");

		// now for a calc with depths reversed
		mean = svp.getMeanSpeedBetween(55, 20);
		assertEquals(1508.42, mean, 0.01, "correct mean");
	}

	// TODO FIX-TEST
	public void NtestMean2() {
		final SVP svp = new SVP();

		assertEquals(null, svp._depths, "not got data");

		try {
			svp.load(SVP_FILE2);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		double mean;

		// now for a calc that spans a SVP step
		final int surface = 0;
		int transmitter = 50;
		int receiver = 30;
		mean = svp.getMeanSpeedBetween(surface, transmitter);
		assertEquals(1501.628, mean, 0.01, "correct mean");

		// now for a calc that goes from surface
		mean = svp.getMeanSpeedBetween(surface, receiver);
		assertEquals(1502.207, mean, 0.01, "correct mean");

		// and the direct path
		mean = svp.getMeanSpeedBetween(receiver, transmitter);
		assertEquals(1500.760, mean, 0.01, "correct mean");

		// try some other depths
		transmitter = 20;
		receiver = 75;

		mean = svp.getMeanSpeedBetween(surface, transmitter);
		assertEquals(1502.298, mean, 0.01, "correct mean");

		// now for a calc that goes from surface
		mean = svp.getMeanSpeedBetween(surface, receiver);
		assertEquals(1500.305, mean, 0.01, "correct mean");

		// and the direct path
		mean = svp.getMeanSpeedBetween(receiver, transmitter);
		assertEquals(1499.581, mean, 0.01, "correct mean");
	}

	// TODO FIX-TEST
	public void NtestMissingData() {
		final SVP svp = new SVP();

		assertEquals(null, svp._depths, "not got data");

		try {
			svp.load(SVP_FILE);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		// change the first point so we don't have data at zero
		svp._depths[0] = 4;

		try {
			svp.getMeanSpeedBetween(0, 22);
			fail("should not have found index");
		} catch (final CalculationException e) {
			assertTrue(e.getMessage().startsWith(SHALLOW_FAIL), "wrong message provided");
		}

		try {
			svp.getMeanSpeedBetween(33, 222);
			fail("should not have found index");
		} catch (final RuntimeException e) {
			assertTrue(e.getMessage().startsWith(DEEP_FAIL), "wrong message provided");
		}

	}

	@Test
	public void testDummy() {

	}
}
