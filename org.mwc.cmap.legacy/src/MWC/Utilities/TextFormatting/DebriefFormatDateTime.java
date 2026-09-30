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

// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : FormatRNDateTime.java

package MWC.Utilities.TextFormatting;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import MWC.GenericData.HiResDate;

public class DebriefFormatDateTime {
	static public class DebriefFormatTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "CONV";

		public DebriefFormatTest(final String val) {
			super(val);
		}

		/**
		 * todo - fix this. it fails on the CI server, since the server is in a
		 * different time-zone or daylight savings mode
		 *
		 * @throws ParseException
		 */
		@SuppressWarnings("deprecation")
		public void notTestPadding() throws ParseException {
			HiResDate val = parseThis("700101", "010000");
			assertEquals("correct date", new Date(70, 00, 01, 02, 00, 00), val.getDate());

			val = parseThis("080101", "010000");
			assertEquals("correct date", new Date(108, 00, 01, 01, 00, 00), val.getDate());

			val = parseThis("080101", "010000.005");
			// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
			// Date(70,00,01,01,00,00).getTime());
			Date thisDate = new Date(108, 00, 01, 01, 00, 00);
			thisDate = new Date(thisDate.getTime() + 5);
			assertEquals("correct date", thisDate, val.getDate());

			val = parseThis("080101", "010000.5");
			// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
			// Date(70,00,01,01,00,00).getTime());
			thisDate = new Date(108, 00, 01, 01, 00, 00);
			thisDate = new Date(thisDate.getTime() + 500);
			assertEquals("correct date", thisDate, val.getDate());

			val = parseThis("80101", "10000");
			// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
			// Date(70,00,01,01,00,00).getTime());
			assertEquals("correct date", new Date(108, 00, 01, 01, 00, 00), val.getDate());

			val = parseThis("20080101", "10000");
			// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
			// Date(70,00,01,01,00,00).getTime());
			assertEquals("correct date", new Date(108, 00, 01, 01, 00, 00), val.getDate());

			val = parseThis("20080101", "200");
			// System.out.println("1:" + val.getDate().getTime() + " 2:" + new
			// Date(70,00,01,01,00,00).getTime());
			assertEquals("correct date", new Date(108, 00, 01, 00, 02, 00), val.getDate());
		}

		/**
		 * todo - fix this. it fails on the CI server, since the server is in a
		 * different time-zone or daylight savings mode
		 *
		 * @throws ParseException
		 */
		public void testMalformedDate() {
			String message = null;
			try {
				parseThis("700001", "010000");
			} catch (final ParseException e) {
				message = e.getMessage();
			}
			assertNotNull("exception not thrown", message);

			message = null;

			try {
				parseThis("700100", "010000");
			} catch (final ParseException e) {
				message = e.getMessage();
			}
			assertNotNull("exception not thrown", message);

			message = null;
		}

		public void testPadding2() {
			assertEquals("000001", padToken("1"));
			assertEquals("001001", padToken("1001"));
			assertEquals("101001", padToken("101001"));
		}

		public void testValues() {
			Date newDTG = new Date(1000);
			String res = DebriefFormatDateTime.toString(newDTG.getTime());
			assertEquals("matches", "700101 000001", res);

			newDTG = new Date(1);
			res = DebriefFormatDateTime.toString(newDTG.getTime());
			assertEquals("matches", "700101 000000.001", res);

			HiResDate hi = new HiResDate(1000);
			res = DebriefFormatDateTime.toStringHiRes(hi);
			assertEquals("matches", "700101 000001", res);

			hi = new HiResDate(1);
			res = DebriefFormatDateTime.toStringHiRes(hi);
			assertEquals("matches", "700101 000000.001", res);

			hi = new HiResDate(0, 1000);
			res = DebriefFormatDateTime.toStringHiRes(hi);
			assertEquals("matches", "700101 000000.001", res);

			hi = new HiResDate(2, 1000);
			res = DebriefFormatDateTime.toStringHiRes(hi);
			assertEquals("matches", "700101 000000.003", res);

			hi = new HiResDate(0, 11);
			res = DebriefFormatDateTime.toStringHiRes(hi);
			assertEquals("matches", "700101 000000.000011", res);

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

	private static DateFormat _dfMillis = null;
	private static DateFormat _df = null;
	private static NumberFormat _micros = null;
	private static NumberFormat _millis = null;
	private static DateFormat _dfFourDigit = null;
	private static DateFormat _dfMillisFourDigit = null;

	/**
	 * first year of the fixed window used for two-digit years: "50" to "99" are
	 * read as 1950-1999, "00" to "49" as 2000-2049. This is fixed (rather than
	 * SimpleDateFormat's default of "80 years before today") so that a file reads
	 * the same whatever the date on the analyst's machine.
	 */
	public static final int TWO_DIGIT_YEAR_START = 1950;

	/**
	 * last year that can be represented unambiguously with a two-digit year
	 */
	public static final int TWO_DIGIT_YEAR_END = TWO_DIGIT_YEAR_START + 99;

	private static final DateFormat FOUR_DIGIT_YEAR_FORMAT = new GMTDateFormat("yyyyMMdd HHmmss");

	private static final DateFormat TWO_DIGIT_YEAR_FORMAT = applyTwoDigitYearWindow(
			new GMTDateFormat("yyMMdd HHmmss"));

	/**
	 * make the supplied format read two-digit years in the fixed
	 * {@link #TWO_DIGIT_YEAR_START} - {@link #TWO_DIGIT_YEAR_END} window
	 *
	 * @param format format to configure (its time zone is not changed)
	 * @return the same format, for convenience
	 */
	public static <T extends SimpleDateFormat> T applyTwoDigitYearWindow(final T format) {
		final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(TWO_DIGIT_YEAR_START, Calendar.JANUARY, 1);
		format.set2DigitYearStart(cal.getTime());
		return format;
	}

	/**
	 * there are also some instances where invalid dates have crept in, possibly
	 * related to Debrief storing 0 and trying to write this to disk. Problem
	 * probably occured during Hi-Res times transition.
	 */
	// private static final String INVALID_DATE_STRING = "700101 000000";

	/**
	 * the string which in the past could appear, when the intention of the software
	 * was to store a null string
	 */
	private static final String NULL_DATE_STRING = "691231 235959.999";

	/**
	 * millis value of the legacy null date (see HiResDate.NULL_DATE)
	 */
	private static final long NULL_DATE_MILLIS = -1;

	/**
	 * formatting method which just exports the micro-seconds within a DTG
	 *
	 * @param dtg
	 * @return
	 */
	public static String formatMicros(final HiResDate dtg) {
		// check our declarations
		initialisePatterns();
		return _micros.format(Math.floorMod(dtg.getMicros(), 1000000L));
	}

	/**
	 * we use static instances of patterns. just initialise them once
	 *
	 */
	private synchronized static void initialisePatterns() {
		if (_dfMillis == null) {
			_dfMillis = new GMTDateFormat("yyMMdd HHmmss.SSS");
			_df = new GMTDateFormat("yyMMdd HHmmss");
			_dfMillisFourDigit = new GMTDateFormat("yyyyMMdd HHmmss.SSS");
			_dfFourDigit = new GMTDateFormat("yyyyMMdd HHmmss");

			// and the microsecond bits
			_micros = new DecimalFormat("000000");
			_millis = new DecimalFormat("000");
		}
	}

	private static String padToken(final String token) {
		final String res;
		if (token.length() == 6) {
			res = token;
		} else {
			final int numMissing = 6 - token.length();
			final StringBuffer buffer = new StringBuffer(6);
			for (int i = 0; i < numMissing; i++) {
				buffer.append("0");
			}
			buffer.append(token);
			res = buffer.toString();
		}
		return res;
	}

	/**
	 * parse a date string using our format
	 *
	 * @throws ParseException on malformed date
	 */
	public synchronized static HiResDate parseThis(final String rawText) throws ParseException {
		// make sure our two and four-digit date bits are initialised
		initialisePatterns();

		Date date = null;
		HiResDate res = null;

		// right, start off by trimming spaces off the date
		final String theRawText = rawText.trim();

		// right. Special check to see if this is an incorrectly represented null
		// date (-1)
		// if (theRawText.equals(NULL_DATE_STRING) ||
		// theRawText.equals(INVALID_DATE_STRING))
		if (theRawText.equals(NULL_DATE_STRING)) {
			System.err.println("Invalid date read from xml file: " + theRawText);
			res = null;
		} else {

			String secondPart = theRawText;
			String subSecondPart = null;

			// start off by seeing if we have sub-millisecond date
			final int subSecondIndex = theRawText.indexOf('.');
			if (subSecondIndex > 0) {
				// so, there is a separator - extract the text before the separator
				secondPart = theRawText.substring(0, subSecondIndex);

				// just check that the '.' isn't the last character
				if (subSecondIndex < theRawText.length() - 1) {
					// yes, we do have digits after the separator
					subSecondPart = theRawText.substring(subSecondIndex + 1);
				}
			}

			// next determine if we have a 4-figure year value (in which case the
			// space will be in column 9
			final int spaceIndex = secondPart.indexOf(" ");

			if (spaceIndex > 6) {
				date = FOUR_DIGIT_YEAR_FORMAT.parse(secondPart);
			} else {
				date = TWO_DIGIT_YEAR_FORMAT.parse(secondPart);
			}

			int micros = 0;

			// do we have a sub-second part?
			if (subSecondPart != null) {
				// get the value
				micros = Integer.parseInt(subSecondPart);

				final int subSecLen = subSecondPart.length();

				// are we within the acceptable data resolution?
				if (subSecLen <= 6) {
					micros = micros * (int) (Math.pow(10, 6 - subSecLen));
				} else {
					// System.err
					// .println("Debrief is only capable of reading data to microsecond resolution
					// (dtg:"
					// + theRawText + ")");
					micros = -1;
				}
			}

			if (micros != -1) {
				if (date != null) {
					res = new HiResDate(date.getTime(), micros);
				}
			}
		}

		return res;
	}

	/**
	 * parse a date string using our format
	 *
	 * @throws ParseException on malformed date
	 */
	public static HiResDate parseThis(final String dateToken, final String timeToken) throws ParseException {
		// do we have millis?
		final int decPoint = timeToken.indexOf(".");
		String milliStr, timeStr;
		if (decPoint > 0) {
			milliStr = timeToken.substring(decPoint, timeToken.length());
			timeStr = timeToken.substring(0, decPoint);
		} else {
			milliStr = "";
			timeStr = timeToken;
		}

		// sort out if we have to padd
		// check the date for missing leading zeros
		final String theDateToken = padToken(dateToken);
		timeStr = padToken(timeStr);

		final String composite = theDateToken + " " + timeStr + milliStr;

		return parseThis(composite);
	}

	static public String toString(final long theVal) {
		return toString(theVal, false);
	}

	/**
	 * format the time using two-digit years, or four-digit years if requested
	 *
	 * @param theVal         millis since epoch
	 * @param fourDigitYears whether to use yyyy
	 * @return formatted string
	 */
	private static String toString(final long theVal, final boolean fourDigitYears) {
		initialisePatterns();

		final java.util.Date theTime = new java.util.Date(theVal);
		String res;

		// first determine which pattern to use. Note: use floorMod, since pre-1970
		// times have negative remainders. The legacy "null" date (-1 millis) is
		// still written as whole seconds ("691231 235959"), which
		// isNotInitialized() recognises when read back.
		final boolean hasMillis = Math.floorMod(theVal, 1000L) != 0 && theVal != NULL_DATE_MILLIS;
		DateFormat selectedFormat;
		if (hasMillis) {
			// ok, it contains milliseconds - include them in the output
			selectedFormat = fourDigitYears ? _dfMillisFourDigit : _dfMillis;
		} else {
			selectedFormat = fourDigitYears ? _dfFourDigit : _df;
		}

		res = selectedFormat.format(theTime);

		return res;
	}

	/**
	 * output the hi-res date as a formatted string, supplying micro-second and
	 * milli-second decimal places as required. Two-digit years are used (as
	 * required by the REP format), so dates outside
	 * {@link #TWO_DIGIT_YEAR_START}-{@link #TWO_DIGIT_YEAR_END} will be read
	 * back inside that window.
	 *
	 * @param time - can't imagine. What-ever could this parameter be called for?
	 * @return formatted string
	 */
	public static String toStringHiRes(final HiResDate time) {
		return toStringHiRes(time, false);
	}

	/**
	 * output the hi-res date for storage in an XML attribute. This matches
	 * {@link #toStringHiRes(HiResDate)}, except that a four-digit year
	 * (yyyyMMdd) is used when the year falls outside the two-digit window, so
	 * that the date survives a round trip. {@link #parseThis(String)} accepts
	 * both forms.
	 *
	 * @param time the time to format
	 * @return formatted string
	 */
	public static String toStringHiResXML(final HiResDate time) {
		final long millis = Math.floorDiv(time.getMicros(), 1000L);
		final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(millis);
		final int year = cal.get(Calendar.YEAR);
		final boolean fourDigits = year < TWO_DIGIT_YEAR_START || year > TWO_DIGIT_YEAR_END
				|| cal.get(Calendar.ERA) != java.util.GregorianCalendar.AD;
		return toStringHiRes(time, fourDigits);
	}

	private static String toStringHiRes(final HiResDate time, final boolean fourDigitYears) {
		// check our declarations
		initialisePatterns();

		// so, have a look at the data
		final long micros = time.getMicros();

		// the legacy "null" date is written as "691231 235959" (see toString)
		if (micros == NULL_DATE_MILLIS * 1000) {
			return toString(NULL_DATE_MILLIS, fourDigitYears);
		}

		// use floor division, so pre-1970 times round down to the previous second
		// and leave a positive sub-second part
		final long wholeSeconds = Math.floorDiv(micros, 1000000L);
		final long subSecondMicros = Math.floorMod(micros, 1000000L);

		final StringBuffer res = new StringBuffer();
		res.append(toString(wholeSeconds * 1000, fourDigitYears));

		// do we have micros?
		if (subSecondMicros % 1000 > 0) {
			// yes
			res.append(".");
			res.append(_micros.format(subSecondMicros));
		} else {
			// do we have millis?
			if (subSecondMicros > 0) {
				// yes, convert the value to millis
				final long millis = subSecondMicros / 1000;

				res.append(".");
				res.append(_millis.format(millis));
			} else {
				// just use the normal output
			}
		}

		return res.toString();

	}

	/**
	 * output the hi-res date as a formatted string, supplying micro-second and
	 * milli-second decimal places as required.
	 *
	 * @param time - can't imagine. What-ever could this parameter be called for?
	 * @return formatted string
	 */
	public static String toStringHiRes(final HiResDate time, final String formatStr) {
		String res;

		// hmm, see if we are actually working in micros
		final long micros = time.getMicros();
		if (Math.floorMod(micros, 1000L) > 0) {
			res = toStringHiRes(time);
		} else {
			final DateFormat myDF = new GMTDateFormat(formatStr);
			res = myDF.format(time.getDate());
		}

		// cool, all finished
		return res;
	}

}
