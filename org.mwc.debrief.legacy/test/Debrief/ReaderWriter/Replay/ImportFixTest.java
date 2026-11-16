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

package Debrief.ReaderWriter.Replay;

import static Debrief.ReaderWriter.Replay.ImportFix.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportFix.*;
import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	/**
	 * use strings to reduce initialisation times
	 */
//	String dateStr;

	// ////////////////////////////////////////////////
	// testing code...
	// ////////////////////////////////////////////////
	public class ImportFixTest {
		static public final String TEST_ALL_TEST_TYPE = "CONV";


		// TODO FIX-TEST
		@SuppressWarnings("deprecation")
		public void NtestPadding() throws ParseException {
			String iLine = "951212 051600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			final AbstractPlainLineImporter iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			// and our special time data
			Date date = res.theFix.getTime().getDate();
			assertEquals(5, date.getHours(), "right hours");
			assertEquals(16, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 51600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(5, date.getHours(), "right hours");
			assertEquals(16, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 1600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(16, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(6, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 00.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(0, date.getMinutes(), "right mins");
			assertEquals(0, date.getSeconds(), "right secs");

			iLine = "951212 0.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(0, date.getMinutes(), "right mins");
			assertEquals(0, date.getSeconds(), "right secs");

			// and sans-millis
			iLine = "951212 51600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(5, date.getHours(), "right hours");
			assertEquals(16, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 1600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(16, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(6, date.getMinutes(), "right mins");
			assertEquals(00, date.getSeconds(), "right secs");

			iLine = "951212 00 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(0, date.getMinutes(), "right mins");
			assertEquals(0, date.getSeconds(), "right secs");

			iLine = "951212 0.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(0, date.getHours(), "right hours");
			assertEquals(0, date.getMinutes(), "right mins");
			assertEquals(0, date.getSeconds(), "right secs");

			// quick date test
			iLine = "951212 051600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(95, date.getYear(), "right hours");

			iLine = "51212 051600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals(105, date.getYear(), "right hours");

		}

		@Test
		public void testDecimalVals() throws ParseException {
			// test the secs component
			String iLine = "951212 051600 CARPET   @C   22 0 45 N 22 0 1.45 W 239.9   2.0      0 ";
			AbstractPlainLineImporter iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals(22.0125, res.theFix.getLocation().getLat(), 0.001, "right lat");
			assertEquals(-22.000402, res.theFix.getLocation().getLong(), 0.00001, "right long");

			// now the mins component
			iLine = "951212 051600 CARPET   @C   22 0.5 00 N 14 0.5 0 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals(22.008, res.theFix.getLocation().getLat(), 0.01, "right lat");
			assertEquals(-14.008333, res.theFix.getLocation().getLong(), 0.00001, "right long");

			// now the degs component
			iLine = "951212 051600 CARPET   @C   22.5 0 00 N 14.5 0 0 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals(22.5, res.theFix.getLocation().getLat(), 0.01, "right lat");
			assertEquals(-14.5, res.theFix.getLocation().getLong(), 0.00001, "right long");

			//
			// NOW LET'S REVERSE THE HEMISPHERE
			// test the secs component
			iLine = "951212 051600 CARPET   @C   22 0 45 S 22 0 1.45 E 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals(-22.0125, res.theFix.getLocation().getLat(), 0.001, "right lat");
			assertEquals(22.000402, res.theFix.getLocation().getLong(), 0.00001, "right long");

			// now the mins component
			iLine = "951212 051600 CARPET   @C   22 0.5 00 S 14 0.5 0 E 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals(-22.008, res.theFix.getLocation().getLat(), 0.01, "right lat");
			assertEquals(14.008333, res.theFix.getLocation().getLong(), 0.00001, "right long");

			// now the degs component
			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals(-22.5, res.theFix.getLocation().getLat(), 0.01, "right lat");
			assertEquals(14.5, res.theFix.getLocation().getLong(), 0.00001, "right long");

			// what about hte label?
			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 brian may smells";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("brian may smells", res.label, "label not read");

			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 brian may smells   ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("brian may smells", res.label, "label not read");

			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 a   ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("a", res.label, "label not read");

		}

		@Test
		public void testHiResParse() throws ParseException {
			String val = "700101 000000";
			HiResDate ers = DebriefFormatDateTime.parseThis(val);
			assertNotNull(ers, "zero micros doesn't get parsed");

			val = "700101 000000.001";
			ers = DebriefFormatDateTime.parseThis(val);
			long micros = ers.getMicros();
			assertEquals(1000, micros, "zero micros");

			val = "19700101 000000.001";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals(1000, micros, "zero micros");

			val = "700101 000000.000001";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals(1, micros, "zero micros");

			val = "700101 000001.";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals(1000000, micros, "zero micros");

			val = "700101 000001.0";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals(1000000, micros, "zero micros");

			val = "700101 000001.01";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals(1010000, micros, "zero micros");

			val = "700101 000001.01001";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals(1010010, micros, "zero micros");

			val = "700101 000000.000000000022";
			ers = DebriefFormatDateTime.parseThis(val);
			assertNull(ers, "Shouldn't manage to produce value");
		}

		@Test
		public void testHiResValues() throws ParseException {
			String iLine = "951212 051600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			final AbstractPlainLineImporter iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET", res.theTrackName, "right track");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");

			// and check the DTG
			final HiResDate hrd = DebriefFormatDateTime.parseThis("951212 051600.000100");
			assertEquals(hrd, res.theFix.getTime(), "time hasn't got mangled");

			final DateFormat otherFormat = new GMTDateFormat("yyMMdd HHmmss");
			Date theDate = null;
			try {
				theDate = otherFormat.parse("951212 051600");
			} catch (final ParseException e) {
				e.printStackTrace(); // To change body of catch statement use
				// File |
				// Settings | File Templates.
				return;
			}
			final HiResDate calculated = new HiResDate(theDate.getTime(), 100);
			assertEquals(calculated, res.theFix.getTime(), "time hasn't got mangled");

			// ok, try our more difficult import string
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");

			// ok, try long years
			iLine = "19951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
			Date fixDate = res.theFix.getTime().getDate();
			DateFormat yearFormat = new GMTDateFormat("yyyy");
			String dateYear = yearFormat.format(fixDate);
			assertEquals("1995", dateYear, "right date");

			// ok, try short years
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new GMTDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("1995", dateYear, "right date");

			// ok, try short name
			iLine = "951212 051600 \"1\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("1", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new GMTDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("1995", dateYear, "right date");

			// try with nice spaces (suitable for comparing on the way out
			iLine = "951212 051600 \"CARPET bag\" @C 22 10 53.54 N 021 45 14.20 W 239.9 2.0 0.0";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");

			// find the track name
			final String theTrack = res.theTrackName;
			final Color thisColor = ImportReplay.replayColorFor(res.theSymbology);

			// create the wrapper for this annotation
			final SupportsPropertyListeners thisWrapper = new FixWrapper(res.theFix);

			final TrackWrapper parentTrack = new TrackWrapper();
			parentTrack.setName(theTrack);

			// get the colour for this track
			parentTrack.setColor(thisColor);

			// set the sym type for the track
			final String theSymType = ImportReplay.replayTrackSymbolFor(res.theSymbology);
			parentTrack.setSymbolType(theSymType);

			final FixWrapper thisFix = (FixWrapper) thisWrapper;
			thisFix.setTrackWrapper(parentTrack);

			// now do the export
			final String oLine = iff.exportThis(thisFix);

			// and check they're the same
			assertEquals(iLine, oLine, "exported line matches");
		}

		@Test
		public void testMilliSecValues() throws ParseException {
			String iLine = "700101 000001 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			AbstractPlainLineImporter iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			iLine = "700101 000000.100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);

			iLine = "700101 000000.100100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET", res.theTrackName, "right track");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
		}

		@Test
		public void testPaddingStrings() throws ParseException {
			String iLine = "0101 01 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			AbstractPlainLineImporter iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			iLine = "700101 000000.100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);

			iLine = "700101 000000.100100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET", res.theTrackName, "right track");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
		}

		@Test
		public void testValues() throws ParseException {
			String iLine = "951212 051600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			final AbstractPlainLineImporter iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET", res.theTrackName, "right track");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");

			// ok, try our more difficult import string
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");

			// ok, try long years
			iLine = "19951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
			Date fixDate = res.theFix.getTime().getDate();
			DateFormat yearFormat = new GMTDateFormat("yyyy");
			String dateYear = yearFormat.format(fixDate);
			assertEquals("1995", dateYear, "right date");

			// ok, try short years
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new GMTDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("1995", dateYear, "right date");

			// ok, try short name
			iLine = "951212 051600 \"1\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("1", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new GMTDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("1995", dateYear, "right date");

			// try with nice spaces (suitable for comparing on the way out
			iLine = "951212 051600 \"CARPET bag\" @C 22 10 53.54 N 021 45 14.20 W 239.9 2.0 0.0";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("CARPET bag", res.theTrackName, "right track");
			assertEquals(4.187, res.theFix.getCourse(), 0.01, "right course");
			assertEquals("@C", res.theSymbology, "right symbology");
			assertEquals(2.0, res.theFix.getSpeed(), 2.0, "right speed");
			assertEquals(0.0, res.theFix.getLocation().getDepth(), 0.01, "right depth");

			// find the track name
			final String theTrack = res.theTrackName;
			final Color thisColor = ImportReplay.replayColorFor(res.theSymbology);

			// create the wrapper for this annotation
			final SupportsPropertyListeners thisWrapper = new FixWrapper(res.theFix);

			final TrackWrapper parentTrack = new TrackWrapper();
			parentTrack.setName(theTrack);

			// get the colour for this track
			parentTrack.setColor(thisColor);

			// set the sym type for the track
			final String theSymType = ImportReplay.replayTrackSymbolFor(res.theSymbology);
			parentTrack.setSymbolType(theSymType);

			final FixWrapper thisFix = (FixWrapper) thisWrapper;
			thisFix.setTrackWrapper(parentTrack);

			// now do the export
			final String oLine = iff.exportThis(thisFix);

			// and check they're the same
			assertEquals(iLine, oLine, "exported line matches");
		}
	}
