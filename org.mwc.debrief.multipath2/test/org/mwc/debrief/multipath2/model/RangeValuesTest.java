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
import static org.mwc.debrief.multipath2.model.RangeValues.*;
import org.mwc.debrief.multipath2.model.RangeValues.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import flanagan.interpolation.LinearInterpolation;
import org.junit.jupiter.api.Test;

// /////////////////////////////////////////////////
// and the testing goes here
// /////////////////////////////////////////////////
public class RangeValuesTest {
	public static final String TEST_RANGES_FILE = "src/org/mwc/debrief/multipath2/model/TestRangeValues.csv";

	// TODO FIX-TEST
	public void NtestMe() {
		final RangeValues times = new RangeValues();

		assertEquals(null, times._times, "not got data");
		assertEquals(null, times._ranges, "not got data");

		// and missing file
		try {
			times.load("src/org/mwc/debrief/multipath/model2/test_times_bad.csv");
			fail("should not have found file");
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			// ok - this should have been thrown
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		assertEquals(null, times._ranges, "still not got data");

		try {
			times.load(TEST_RANGES_FILE);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		assertEquals(801, times._times.length, "got correct num entries");

		assertEquals(2530.96, times.valueAt(10000), 0.0001, "got correct range");
		assertEquals(2529.144, times.valueAt(11000), 0.0001, "got correct range");
		assertTrue(times.hasValueAt(0), "correct time checking");
		assertTrue(times.hasValueAt(2000), "correct time checking");
		assertTrue(!times.hasValueAt(36000000), "correct time checking");

	}

	@Test
	public void testDummy() {

	}

}
