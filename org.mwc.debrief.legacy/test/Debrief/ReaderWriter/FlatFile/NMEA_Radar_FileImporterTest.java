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

import static Debrief.ReaderWriter.FlatFile.NMEA_Radar_FileImporter.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.FlatFile.NMEA_Radar_FileImporter.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NMEA_Radar_FileImporterTest {
	static class Logger implements ErrorLogger {
		private final List<String> messages = new ArrayList<>();

		private final boolean console = true;

		private void clear() {
			messages.clear();
		}

		public boolean isEmpty() {
			return messages.isEmpty();
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

		@BeforeEach
		public void setUp() {
		}
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private final Logger _logger = new Logger();

	@BeforeEach
	public void setUp() {
		_logger.clear();
	}

	@Test
	public void testCanLoad() throws Exception {

		final String initialString = "700101_010601:$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";

		final Reader inputString = new StringReader(initialString);
		final BufferedReader reader = new BufferedReader(inputString);

		assertTrue(canLoad(_logger, reader));
	}

	@Test
	public void testParseLine() throws Exception {

		final String initialString = "700101_010601:$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";
		final Optional<RadarEntry> entry = readLine(initialString, 5);
		assertTrue(entry.isPresent());
	}

	@Test
	public void testTwentyFourHourClock() throws Exception {
		final String tail = ":$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";
		// afternoon
		Optional<RadarEntry> entry = readLine("700101_130601" + tail, 5);
		assertTrue(entry.isPresent(), "PM record read");
		assertEquals((13 * 3600 + 6 * 60 + 1) * 1000L, entry.get().dtg.getDate().getTime());
		// noon
		entry = readLine("700101_120601" + tail, 5);
		assertTrue(entry.isPresent(), "noon record read");
		assertEquals((12 * 3600 + 6 * 60 + 1) * 1000L, entry.get().dtg.getDate().getTime());
		// just after midnight
		entry = readLine("700101_000601" + tail, 5);
		assertTrue(entry.isPresent(), "midnight record read");
		assertEquals((6 * 60 + 1) * 1000L, entry.get().dtg.getDate().getTime());
		// morning
		entry = readLine("700101_010601" + tail, 5);
		assertEquals((3600 + 6 * 60 + 1) * 1000L, entry.get().dtg.getDate().getTime());
	}

	@Test
	public void testInvalidDate() throws Exception {
		final String initialString = "700101010601:$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";
		final Optional<RadarEntry> entry = readLine(initialString, 5);
		assertFalse(entry.isPresent());
	}

	@Test
	public void testWrongNumColumns() throws Exception {
		final String initialString = "700101_010601:$RATTM,002,0.665,224.7,17,31.2,R,0.16,99.99,N,,T,,,A*2A";
		final Optional<RadarEntry> entry = readLine(initialString, 5);
		assertFalse(entry.isPresent());
	}

	@Test
	public void testCannotLoad() throws Exception {

		final String initialString = "700101_010601:$OTTER,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";

		final Reader inputString = new StringReader(initialString);
		final BufferedReader reader = new BufferedReader(inputString);

		assertFalse(canLoad(_logger, reader));
	}
}
