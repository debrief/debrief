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

package Debrief.ReaderWriter.FlatFile;

import static Debrief.ReaderWriter.FlatFile.OTH_Importer.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.FlatFile.OTH_Importer.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OTH_ImporterTest {
	static class Logger implements ErrorLogger {
		private final List<String> messages = new ArrayList<String>();

		private final boolean console = true;

		private void clear() {
			messages.clear();
		}

		private boolean isEmpty() {
			return messages.isEmpty();
		}

		private String last() {
			return messages.get(messages.size() - 1);
		}

		@Override
		public void logError(final int status, final String text, final Exception e) {
			output(text, e);
		}

		@Override
		public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
			output(text, e);
		}

		@Override
		public void logStack(final int status, final String text) {
			output(text, null);
		}

		public void output(final String text, final Exception e) {
			messages.add(text);
			if (console) {
				System.out.println(text);
				if (e != null) {
					e.printStackTrace();
				}
			}
		}
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private final String root = "../org.mwc.debrief.legacy/test_data/OTH_Import";

	private final Logger _logger = new Logger();

	@BeforeEach
	public void setUp() {
		_logger.clear();
		setLogFeedback(true);
	}

	@Test
	public void testBadlyFormattedFields() {

		/*
		 * unexpected units in the bagging area
		 *
		 */
		_logger.clear();
		assertEquals(0d, courseFor("POS/112313Z1/AUG/4612N34/02122W7//170T/11NM/13NM/300a/2K/", _logger),
				"not T markers");
		assertEquals("Failed to parse course:For input string: \"300a\"", _logger.last(), "correct message");

		_logger.clear();
		assertEquals(0d, speedFor("POS/112313Z1/AUG/4612N34/02122W7//170T/11NM/13NM/200T/2MS/", _logger),
				"got speed");
		assertEquals("Failed to parse speed:For input string: \"2MS\"", _logger.last(), "correct message");

	}

	@Test
	public void testCanLoad() {
		assertFalse(canLoad(root + "/really_bad.txt", new Logger()), "doesn't load dodgy file");
		assertTrue(canLoad(root + "/valid.txt", new Logger()), "loads valid file");

		// this time, verify the message
		assertFalse(canLoad(root + "/missing_pos.txt", _logger), "missing pos");
		assertEquals("OTH Import rejecting file, Header:true Track:true Pos:false", _logger.last(),
				"correct error message");
	}

	@Test
	public void testGetLocation() {
		assertEquals(" 46\u00B012'00.00\"N 021\u00B022'00.00\"E ", locationFrom("POS/112313Z1/AUG/4612N34/02122E7//170T/11NM/13NM/000T/0K/", _logger).toString(),
				"got location");

		assertEquals(" 22\u00B011'00.00\"N 021\u00B042'00.00\"W ", locationFrom("POS/120502Z0/DEC/2211N6/02142W9///170T/11NM/13NM/000T/0K/", _logger).toString(),
				"got location");
	}

	@Test
	public void testGetName() {
		assertEquals("TYPE 12-HOOD", nameFrom("CTC/12345AB6/TYPE 12-HOOD//DDGH/NAV/D 32/UK/"), "got good name");
		assertNull(nameFrom("CTC/12345AB6"), "not got name");
	}

	@Test
	public void testGoodFields() {

		assertEquals(Math.toRadians(300d), courseFor("POS/112313Z1/AUG/4612N34/02122W7//170T/11NM/13NM/300T/2K/", _logger),
				"got course");
		assertEquals(new WorldSpeed(2, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3d, speedFor("POS/112313Z1/AUG/4612N34/02122W7//170T/11NM/13NM/200T/2K/", _logger),
				"got speed");
	}

	@Test
	public void testGoodLoad() throws Exception {
		final OTH_Importer importer = new OTH_Importer();
		final Layers layers = new Layers();

		final OTH_Helper brtHelper = new OTH_Helper_Headless(true);
		final InputStream is = new FileInputStream(root + "/valid.txt");
		final ImportOTHAction action = importer.importThis(brtHelper, is, layers, _logger);
		action.execute();

		assertEquals(2, layers.size(), "has data");

		final TrackWrapper track = (TrackWrapper) layers.elementAt(0);
		final BaseLayer ellipses = (BaseLayer) layers.elementAt(1);

		assertEquals(3, track.numFixes(), "correct fixes");
		assertEquals(3, ellipses.size(), "correct ellipses");

		// and undo it
		action.undo();

		assertEquals(0, layers.size(), "has data");
	}

	@Test
	public void testInsufficientFields() {
		/*
		 * missing fields
		 *
		 */
		_logger.clear();
		assertEquals(0d, speedFor("POS/112313Z1/AUG/4612N34/02122W7", _logger), "not got speed");
		assertEquals("Insufficient fields in POS line:POS/112313Z1/AUG/4612N34/02122W7", _logger.last(),
				"correct message");

		_logger.clear();
		assertEquals(0d, courseFor("POS/112313Z1/AUG/4612N34/02122W7//170T/11NM/13NM", _logger), "not got course");
		assertEquals("Insufficient fields in POS line:POS/112313Z1/AUG/4612N34/02122W7//170T/11NM/13NM",
				_logger.last(), "correct message");
	}

	@Test
	public void testParseYear() {
		_logger.clear();
		assertEquals(19, parseYear(_logger, "some string more string 19"), "good year");
		assertTrue(_logger.isEmpty(), "empty logger");

		_logger.clear();
		assertEquals(YEAR_UNKNOWN, parseYear(_logger, "some string more string 1a9"), "bad year");
		assertEquals("Failed to extract year from last token in:some string more string 1a9", _logger.last(),
				"correct message");

		_logger.clear();
		assertEquals(YEAR_UNKNOWN, parseYear(_logger, ""), "bad year");
		assertEquals("Failed to extract year from empty first line", _logger.last(), "correct message");

	}

	@Test
	public void testTrackAlreadyPresent() throws Exception {
		final OTH_Importer importer = new OTH_Importer();
		final Layers layers = new Layers();

		final TrackWrapper existing_track = new TrackWrapper();
		existing_track.setName("TYPE 12-HOOD");
		existing_track
				.addFix(new FixWrapper(new Fix(new HiResDate(10000000), new WorldLocation(33, 4, 0d), 33, 22)));
		layers.addThisLayer(existing_track);
		assertEquals(1, existing_track.numFixes(), "just one position");

		final OTH_Helper brtHelper = new OTH_Helper_Headless(true);
		final InputStream is = new FileInputStream(root + "/valid.txt");
		final ImportOTHAction action = importer.importThis(brtHelper, is, layers, _logger);
		action.execute();

		assertEquals(2, layers.size(), "has data");

		final TrackWrapper track = (TrackWrapper) layers.elementAt(0);
		final BaseLayer ellipses = (BaseLayer) layers.elementAt(1);

		assertEquals(4, track.numFixes(), "correct fixes");
		assertEquals(3, ellipses.size(), "correct ellipses");

		// and undo it
		action.undo();

		assertEquals(1, layers.size(), "has data");
		assertEquals(1, existing_track.numFixes(), "just one position");
	}
}
