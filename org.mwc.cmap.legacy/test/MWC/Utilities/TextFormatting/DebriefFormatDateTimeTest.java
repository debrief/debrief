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

package MWC.Utilities.TextFormatting;

import static MWC.Utilities.TextFormatting.DebriefFormatDateTime.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.TextFormatting.DebriefFormatDateTime.*;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import MWC.GenericData.HiResDate;

public class DebriefFormatDateTimeTest {
	static public final String TEST_ALL_TEST_TYPE = "CONV";


	/**
	 * todo - fix this. it fails on the CI server, since the server is in a
	 * different time-zone or daylight savings mode
	 *
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public void notTestPadding() throws ParseException {
		HiResDate val = parseThis("700101", "010000");
		assertEquals(new Date(70, 00, 01, 02, 00, 00), val.getDate(), "correct date");

		val = parseThis("080101", "010000");
		assertEquals(new Date(108, 00, 01, 01, 00, 00), val.getDate(), "correct date");

		val = parseThis("080101", "010000.005");
		// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
		// Date(70,00,01,01,00,00).getTime());
		Date thisDate = new Date(108, 00, 01, 01, 00, 00);
		thisDate = new Date(thisDate.getTime() + 5);
		assertEquals(thisDate, val.getDate(), "correct date");

		val = parseThis("080101", "010000.5");
		// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
		// Date(70,00,01,01,00,00).getTime());
		thisDate = new Date(108, 00, 01, 01, 00, 00);
		thisDate = new Date(thisDate.getTime() + 500);
		assertEquals(thisDate, val.getDate(), "correct date");

		val = parseThis("80101", "10000");
		// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
		// Date(70,00,01,01,00,00).getTime());
		assertEquals(new Date(108, 00, 01, 01, 00, 00), val.getDate(), "correct date");

		val = parseThis("20080101", "10000");
		// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
		// Date(70,00,01,01,00,00).getTime());
		assertEquals(new Date(108, 00, 01, 01, 00, 00), val.getDate(), "correct date");

		val = parseThis("20080101", "200");
		// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
		// Date(70,00,01,01,00,00).getTime());
		assertEquals(new Date(108, 00, 01, 00, 02, 00), val.getDate(), "correct date");
	}

	/**
	 * todo - fix this. it fails on the CI server, since the server is in a
	 * different time-zone or daylight savings mode
	 *
	 * @throws ParseException
	 */
	@Test
	public void testMalformedDate() {
		String message = null;
		try {
			parseThis("700001", "010000");
		} catch (final ParseException e) {
			message = e.getMessage();
		}
		assertNotNull(message, "exception not thrown");

		message = null;

		try {
			parseThis("700100", "010000");
		} catch (final ParseException e) {
			message = e.getMessage();
		}
		assertNotNull(message, "exception not thrown");

		message = null;
	}

	@Test
	public void testPadding2() {
		assertEquals("000001", padToken("1"));
		assertEquals("001001", padToken("1001"));
		assertEquals("101001", padToken("101001"));
	}

	@Test
	public void testValues() {
		Date newDTG = new Date(1000);
		String res = DebriefFormatDateTime.toString(newDTG.getTime());
		assertEquals("700101 000001", res, "matches");

		newDTG = new Date(1);
		res = DebriefFormatDateTime.toString(newDTG.getTime());
		assertEquals("700101 000000.001", res, "matches");

		HiResDate hi = new HiResDate(1000);
		res = DebriefFormatDateTime.toStringHiRes(hi);
		assertEquals("700101 000001", res, "matches");

		hi = new HiResDate(1);
		res = DebriefFormatDateTime.toStringHiRes(hi);
		assertEquals("700101 000000.001", res, "matches");

		hi = new HiResDate(0, 1000);
		res = DebriefFormatDateTime.toStringHiRes(hi);
		assertEquals("700101 000000.001", res, "matches");

		hi = new HiResDate(2, 1000);
		res = DebriefFormatDateTime.toStringHiRes(hi);
		assertEquals("700101 000000.003", res, "matches");

		hi = new HiResDate(0, 11);
		res = DebriefFormatDateTime.toStringHiRes(hi);
		assertEquals("700101 000000.000011", res, "matches");

	}

	private static long utcMillis(final int year, final int month, final int day) {
		final java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(year, month - 1, day);
		return cal.getTimeInMillis();
	}

	/**
	 * two-digit years use a fixed 1950-2049 window, rather than SimpleDateFormat's
	 * moving (today - 80 years) window.
	 */
	@Test
	public void testTwoDigitYearPivotIsFixed() throws ParseException {
		assertEquals(utcMillis(1950, 1, 1), parseThis("500101 000000").getDate().getTime());
		assertEquals(utcMillis(2049, 12, 31), parseThis("491231 000000").getDate().getTime());
		// with the default moving window (seen from 2026-09) this becomes 1946
		assertEquals(utcMillis(2046, 12, 31), parseThis("461231 000000").getDate().getTime());
		assertEquals(utcMillis(2008, 1, 1), parseThis("080101", "000000").getDate().getTime());
		// four-digit years are unaffected by the window
		assertEquals(utcMillis(1945, 1, 1), parseThis("19450101 000000").getDate().getTime());
		assertEquals(utcMillis(2050, 1, 1), parseThis("20500101", "000000").getDate().getTime());
	}

	/**
	 * REP-style output keeps two-digit years, so a date outside 1950-2049 can't
	 * round-trip through it. The result is now deterministic (always read back
	 * inside the window), not dependent on the machine date.
	 */
	@Test
	public void testTwoDigitYearOutsideWindow() throws ParseException {
		final HiResDate ww2 = new HiResDate(utcMillis(1945, 1, 1));
		final String rep = toStringHiRes(ww2);
		assertEquals("450101 000000", rep);
		assertEquals(utcMillis(2045, 1, 1), parseThis(rep).getDate().getTime());

		final HiResDate future = new HiResDate(utcMillis(2050, 1, 1));
		assertEquals("500101 000000", toStringHiRes(future));
		assertEquals(utcMillis(1950, 1, 1), parseThis(toStringHiRes(future)).getDate().getTime());
	}

	/**
	 * XML attributes use four-digit years when (and only when) the year falls
	 * outside the two-digit window, so those dates survive a round trip.
	 */
	@Test
	public void testXMLDatesRoundTrip() throws ParseException {
		final HiResDate ww2 = new HiResDate(utcMillis(1945, 1, 1), 500);
		assertEquals("19450101 000000.000500", toStringHiResXML(ww2));
		assertEquals(ww2, parseThis(toStringHiResXML(ww2)));

		final HiResDate future = new HiResDate(utcMillis(2050, 6, 30) + 250);
		assertEquals("20500630 000000.250", toStringHiResXML(future));
		assertEquals(future, parseThis(toStringHiResXML(future)));

		// dates inside the window are written exactly as before
		final HiResDate normal = new HiResDate(utcMillis(2020, 3, 4) + 13 * 3600000L);
		assertEquals("200304 130000", toStringHiResXML(normal));
		assertEquals(toStringHiRes(normal), toStringHiResXML(normal));
		assertEquals(normal, parseThis(toStringHiResXML(normal)));

		final HiResDate edge = new HiResDate(utcMillis(1950, 1, 1));
		assertEquals("500101 000000", toStringHiResXML(edge));
		final HiResDate lastBefore = new HiResDate(utcMillis(1950, 1, 1) - 1000);
		assertEquals("19491231 235959", toStringHiResXML(lastBefore));
		assertEquals(lastBefore, parseThis(toStringHiResXML(lastBefore)));
	}

	/**
	 * sub-second parts of pre-1970 instants must not be dropped
	 */
	@Test
	public void testPre1970SubSeconds() throws ParseException {
		// 1969-12-31 23:59:59.500
		assertEquals("691231 235959.500", DebriefFormatDateTime.toString(-500));
		HiResDate hi = new HiResDate(-500);
		assertEquals("691231 235959.500", toStringHiRes(hi));
		assertEquals(hi, parseThis(toStringHiRes(hi)));

		// 1969-12-31 23:59:59.999999
		hi = new HiResDate(0, -1);
		assertEquals("691231 235959.999999", toStringHiRes(hi));
		assertEquals(hi, parseThis(toStringHiRes(hi)));
		assertEquals("999999", formatMicros(hi));

		// 1965-06-01 12:00:00.250
		hi = new HiResDate(utcMillis(1965, 6, 1) + 12 * 3600000L + 250);
		assertEquals("650601 120000.250", toStringHiRes(hi));
		assertEquals(hi, parseThis(toStringHiRes(hi)));

		// the legacy "null date" marker is written as whole seconds, which
		// isNotInitialized() recognises when read back (not as ".999", which
		// parseThis() would turn into a null date)
		assertEquals("691231 235959", toStringHiRes(HiResDate.NULL_DATE));
		assertTrue(HiResDate.isNotInitialized(parseThis(toStringHiRes(HiResDate.NULL_DATE))));
	}
}
