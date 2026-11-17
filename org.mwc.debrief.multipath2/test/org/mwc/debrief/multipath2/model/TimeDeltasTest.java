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
import static org.mwc.debrief.multipath2.model.TimeDeltas.*;
import org.mwc.debrief.multipath2.model.TimeDeltas.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.junit.jupiter.api.Test;

// /////////////////////////////////////////////////
// and the testing goes here
// /////////////////////////////////////////////////
public class TimeDeltasTest {
	public static final String TEST_TIMES_FILE = "..//org.mwc.debrief.multipath2/src/org/mwc/debrief/multipath2/model/test_times.csv";

	@Test
	public void testMe() {
		final TimeDeltas times = new TimeDeltas();

		assertEquals(null, times._myData, "not got data");

		// and missing file
		try {
			times.load("../org.mwc.debrief.multipath2/src/org/mwc/debrief/multipath/model2/test_times_bad.csv");
			fail("should not have found file");
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			// ok - this should have been thrown
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		assertEquals(null, times._myData, "still not got data");

		try {
			times.load(TEST_TIMES_FILE);
		} catch (final ParseException e) {
			fail("wrong number format");
		} catch (final IOException e) {
			fail("unable to read lines");
		} catch (final DataFormatException e) {
			fail("bad data");
		}

		assertEquals(379, times._myData.size(), "got correct num entries");

		assertNotNull(times.iterator(), "return iterator");

	}

}
