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

package org.mwc.debrief.lite.headless;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.mwc.debrief.lite.LiteApplication;
import org.mwc.debrief.lite.LiteSession;
import org.mwc.debrief.lite.util.PlotFiles;

import Debrief.Testing.GoldenFile;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Utilities.ReaderWriter.ImportManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Load sample files the way Lite does, save the plot the way Lite does, load
 * the saved plot back, and check the data survived.
 */
public class FileRoundTripTest {

	/** DPF stores positions to about 1e-9 degrees; allow well under a metre */
	private static final double POSITION_TOLERANCE_DEGS = 1e-6;

	/** DPF stores course (degs) and speed (kts) to 3 decimal places */
	private static final double DPF_COURSE_SPEED_TOLERANCE = 0.0005 + 1e-9;

	/** what we check about a track, before and after a round trip */
	private static final class TrackSummary {
		final String name;
		final List<String> fixes = new ArrayList<>();

		TrackSummary(final TrackWrapper track) {
			name = track.getName();
			for (final FixWrapper f : HeadlessFixture.fixes(track)) {
				fixes.add(f.getDTG().getMicros() + "|" + f.getLocation().getLat() + "|" + f.getLocation().getLong()
						+ "|" + f.getCourseDegs() + "|" + f.getSpeed());
			}
		}
	}

	private static List<TrackSummary> summarise(final LiteSession session) {
		final List<TrackSummary> res = new ArrayList<>();
		for (final TrackWrapper t : HeadlessFixture.tracks(session.getData())) {
			res.add(new TrackSummary(t));
		}
		return res;
	}

	/**
	 * the two track lists match: same names, same fix count, and each fix has
	 * the same time, course and speed, and (near enough) the same position
	 */
	private static void assertSameTracks(final List<TrackSummary> before, final List<TrackSummary> after) {
		assertEquals(before.size(), after.size(), "number of tracks");
		for (int i = 0; i < before.size(); i++) {
			final TrackSummary b = before.get(i);
			final TrackSummary a = after.get(i);
			assertEquals(b.name, a.name, "track name");
			assertEquals(b.fixes.size(), a.fixes.size(), "fixes in " + b.name);
			for (int j = 0; j < b.fixes.size(); j++) {
				final String[] bf = b.fixes.get(j).split("\\|");
				final String[] af = a.fixes.get(j).split("\\|");
				final String where = b.name + " fix " + j;
				assertEquals(bf[0], af[0], where + " time");
				assertEquals(Double.parseDouble(bf[1]), Double.parseDouble(af[1]), POSITION_TOLERANCE_DEGS,
						where + " lat");
				assertEquals(Double.parseDouble(bf[2]), Double.parseDouble(af[2]), POSITION_TOLERANCE_DEGS,
						where + " long");
				// the NMEA importer keeps courses in -180..180; a saved plot holds 0..360
				assertEquals(0, angleBetween(Double.parseDouble(bf[3]), Double.parseDouble(af[3])), DPF_COURSE_SPEED_TOLERANCE,
						where + " course");
				assertEquals(Double.parseDouble(bf[4]), Double.parseDouble(af[4]), DPF_COURSE_SPEED_TOLERANCE,
						where + " speed");
			}
		}
	}

	/** the smallest difference between two angles, in degrees */
	private static double angleBetween(final double a, final double b) {
		final double diff = Math.abs(a - b) % 360;
		return Math.min(diff, 360 - diff);
	}

	/**
	 * the plot Lite saved matches the committed golden file, once the fields that
	 * change from run to run are masked: the creation time, and the version date
	 * in the details note
	 */
	private void assertSavedMatchesGolden(final String goldenName) throws IOException {
		String saved = new String(Files.readAllBytes(_saved.toPath()), java.nio.charset.StandardCharsets.UTF_8);
		saved = GoldenFile.maskAttributes(saved, "Created");
		saved = saved.replaceAll("Saved with Debrief version dated [^\"<]*", "Saved with Debrief version dated "
				+ java.util.regex.Matcher.quoteReplacement(GoldenFile.MASK));
		GoldenFile.assertMatches(goldenName, saved);
	}

	private LiteApplication _app;
	private File _saved;

	/** save the session as Lite does, then load the saved file into a new session */
	private LiteSession saveAndReload(final LiteSession session) throws IOException {
		PlotFiles.writePlot(session, _saved);
		assertTrue(_saved.length() > 0, "plot was written");
		final LiteSession reloaded = HeadlessFixture.newSession(_app);
		PlotFiles.importPlot(_saved, reloaded, _app);
		return reloaded;
	}

	@BeforeEach
	public void setUp() throws Exception {
		_app = HeadlessFixture.newApp();
		_saved = Files.createTempFile("lite-roundtrip", ".dpf").toFile();
	}

	@AfterEach
	public void tearDown() throws Exception {
		Files.deleteIfExists(_saved.toPath());
	}

	@Test
	public void testDpfRoundTrip() throws IOException {
		final LiteSession session = HeadlessFixture.newSession(_app);
		PlotFiles.importPlot(new File(HeadlessFixture.SAMPLE_DATA, "sample.dpf"), session, _app);

		// sample.dpf holds two tracks, NELSON and COLLINGWOOD, with 805 <fix> elements
		// between them. A failed import is only logged, so check it all arrived.
		final List<TrackSummary> before = summarise(session);
		assertEquals(2, before.size(), "tracks in sample.dpf");
		assertEquals("NELSON", before.get(0).name);
		assertEquals("COLLINGWOOD", before.get(1).name);
		assertEquals(805, before.get(0).fixes.size() + before.get(1).fixes.size(), "fixes in sample.dpf");
		final List<String> layersBefore = HeadlessFixture.layerNames(session.getData());

		final LiteSession reloaded = saveAndReload(session);
		assertSavedMatchesGolden("lite/sample_dpf_saved.dpf");
		assertSameTracks(before, summarise(reloaded));
		assertEquals(layersBefore, HeadlessFixture.layerNames(reloaded.getData()),
				"layers survive the round trip");
	}

	@Test
	public void testNmeaRoundTrip() throws IOException {
		final LiteSession session = HeadlessFixture.newSession(_app);
		PlotFiles.importNmea(new File(HeadlessFixture.SAMPLE_DATA, "other_formats/NMEA_TRIAL.log"),
				session.getData());

		final List<TrackSummary> before = summarise(session);
		assertFalse(before.isEmpty(), "NMEA_TRIAL.log gives tracks");

		// Lite samples NMEA tracks at one fix a minute
		for (final TrackWrapper t : HeadlessFixture.tracks(session.getData())) {
			final List<FixWrapper> fixes = HeadlessFixture.fixes(t);
			assertTrue(fixes.size() > 1, t.getName() + " has fixes");
			for (int i = 1; i < fixes.size(); i++) {
				final long gapMillis = fixes.get(i).getDTG().getDate().getTime()
						- fixes.get(i - 1).getDTG().getDate().getTime();
				assertTrue(gapMillis >= 60_000,
						t.getName() + " fixes at least a minute apart, got " + gapMillis + " ms");
			}
		}

		assertSameTracks(before, summarise(saveAndReload(session)));
	}

	@Test
	public void testRepRoundTrip() throws IOException {
		final File rep = new File(HeadlessFixture.SAMPLE_DATA, "boat1.rep");

		// what the file holds, read independently of Debrief's importer
		long fixLines = 0;
		for (final String line : Files.readAllLines(rep.toPath())) {
			final String[] tokens = line.trim().split("\\s+");
			if (!line.startsWith(";") && tokens.length > 8 && "NELSON".equals(tokens[2])) {
				fixLines++;
			}
		}
		assertEquals(402, fixLines, "boat1.rep fix lines");

		// load it as Lite does: through the import manager
		final LiteSession session = HeadlessFixture.newSession(_app);
		ImportManager.importThese(new File[] { rep }, session.getData(), null);

		final List<TrackWrapper> tracks = HeadlessFixture.tracks(session.getData());
		assertEquals(1, tracks.size(), "one track");
		final TrackWrapper nelson = tracks.get(0);
		assertEquals("NELSON", nelson.getName());
		final List<FixWrapper> fixes = HeadlessFixture.fixes(nelson);
		assertEquals(fixLines, fixes.size(), "every fix kept");

		// first fix: 951212 050000.000 NELSON @C 22 11 10.63 N 21 41 52.37 W 269.7 2.0
		final FixWrapper first = fixes.get(0);
		assertEquals("951212 050000", new java.text.SimpleDateFormat("yyMMdd HHmmss") {
					private static final long serialVersionUID = 1L;
					{
						setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
					}
				}.format(first.getDTG().getDate()),
				"first fix time");
		assertEquals(22 + 11 / 60d + 10.63 / 3600d, first.getLocation().getLat(), 1e-9, "first fix lat");
		assertEquals(-(21 + 41 / 60d + 52.37 / 3600d), first.getLocation().getLong(), 1e-9, "first fix long");
		assertEquals(269.7, first.getCourseDegs(), 1e-6, "first fix course");
		assertEquals(2.0, first.getSpeed(), 1e-6, "first fix speed");

		final List<TrackSummary> before = summarise(session);
		final LiteSession reloaded = saveAndReload(session);
		assertSavedMatchesGolden("lite/boat1_rep_saved.dpf");
		assertSameTracks(before, summarise(reloaded));
	}
}
