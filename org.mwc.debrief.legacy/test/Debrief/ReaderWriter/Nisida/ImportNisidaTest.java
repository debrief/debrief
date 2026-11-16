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
package Debrief.ReaderWriter.Nisida;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import Debrief.ReaderWriter.Nisida.ImportNisida.NisidaLoadState;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import org.junit.jupiter.api.Test;

public class ImportNisidaTest {
	private final String nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/nisida_sample.txt";
	private final String not_nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/CLog_Trial.txt";
	private final String missing_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/NOT_EXIST_Trial.txt";

	@Test
	public void testAttack() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311206Z/ATT/OTHER";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		final Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull(narrativeLayer, "found narrative");
		assertTrue(narrativeLayer instanceof NarrativeWrapper, "of correct type");
		final NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals(1, narratives.size(), "found entries");
		final NarrativeEntry narrative = (NarrativeEntry) narratives.elements().nextElement();
		assertEquals("OTHER", narrative.getEntry(), "Content of the narrative");
	}

	@Test
	public void testCanLoad() throws FileNotFoundException {
		assertTrue(ImportNisida.canLoadThisFile(new FileInputStream(nisida_track)));
		assertFalse(ImportNisida.canLoadThisFile(new FileInputStream(not_nisida_track)));
		try {
			ImportNisida.canLoadThisFile(new FileInputStream(missing_track));
			fail("It was able to read a file that doesn't exits");
		} catch (final Exception e) {
			// OK
		}
	}

	@Test
	public void testDetection() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311200Z/DET/RDR/23/20/777/3602.02N/00412.12E/GPS/DETECTION RECORD";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		assertEquals(1, layers.size(), "Correct Layer Unit");
		final Layer ownshipLayer = layers.findLayer("ADRI");
		final TrackSegment leg = (TrackSegment) ownshipLayer.elements().nextElement();
		final FixWrapper fix = (FixWrapper) leg.elements().nextElement();

		assertEquals(36.03366666666667, fix.getLocation().getLat(), 1e-8, "Correct Location");
		assertEquals(4.202, fix.getLocation().getLong(), 1e-8, "Correct Location");
	}

	@Test
	public void testDetectionIncorrectValue() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311200Z/DET/RDR/23/20";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		assertEquals(1, layers.size(), "Correct Layer Unit");
		final Layer ownshipLayer = layers.findLayer("ADRI");
		final SegmentList net = (SegmentList) ownshipLayer.elements().nextElement();

		assertEquals(0, net.size(), "Incorrect Detection ignored sucessfully");

	}

	@Test
	public void testLoad() throws FileNotFoundException {
		final FileInputStream fis = new FileInputStream(nisida_track);
		final Layers layers = new Layers();
		ImportNisida.importThis(fis, layers);

		assertEquals(5, layers.size(), "created layers");

		// check the narrative entries
		final Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull(narrativeLayer, "found narratives");
		assertTrue(narrativeLayer instanceof NarrativeWrapper, "of correct type");
		final NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals(10, narratives.size(), "found entries");

		final Layer ownshipLayer = layers.findLayer("ADRI");
		assertNotNull(ownshipLayer, "created O/S track");
		assertTrue(ownshipLayer instanceof TrackWrapper, "is track wrapper");
		final TrackWrapper ownship = (TrackWrapper) ownshipLayer;
		assertEquals(5, ownship.numFixes(), "correct number of points");
	}

	@Test
	public void testNarrativeLine() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311056Z/NAR/TEXT FOR NARRATIVE PURPOSES WHICH";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		final Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull(narrativeLayer, "found narrative");
		assertTrue(narrativeLayer instanceof NarrativeWrapper, "of correct type");
		final NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals(1, narratives.size(), "found entries");
		final NarrativeEntry narrative = (NarrativeEntry) narratives.elements().nextElement();
		assertEquals("TEXT FOR NARRATIVE PURPOSES WHICH", narrative.getEntry(), "Content of the narrative");
	}

	@Test
	public void testParseLocation() {
		final NisidaLoadState status = new NisidaLoadState(null);
		assertEquals(ImportNisida.parseDegrees("1230.00N", status), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.00S", status), -12.5);
		assertEquals(ImportNisida.parseDegrees("01230.00E", status), 12.5);
		assertEquals(ImportNisida.parseDegrees("01230.00W", status), -12.5);
		// try some weird formats
		assertEquals(ImportNisida.parseDegrees("1230.0000N", status), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.0000N", status), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.00E", status), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.00W", status), -12.5);
	}

	@Test
	public void testParseValue() {
		final NisidaLoadState status = new NisidaLoadState(null);
		assertEquals(ImportNisida.valueFor("1230", status), 1230d);
		assertEquals(ImportNisida.valueFor("-", status), null);
		assertEquals(ImportNisida.valueFor("", status), null);
	}

	@Test
	public void testUnit() {
		final String inputFileContent = "UNIT/ADRI/SAUL//";
		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		final NisidaLoadState status = ImportNisida.importThis(targetStream, layers);

		assertEquals(1, layers.size(), "Correct Layer Unit");
		final Layer ownshipLayer = layers.findLayer("ADRI");
		assertNotNull(ownshipLayer, "created O/S track");
		// 0 because we have added a wrong date format
		assertEquals(0, status.getYear(), "Year value");
		assertEquals(0, status.getYear(), "Month value");

	}

	/**
	 * the UNIT month must be read with English month names, whatever the
	 * platform locale (and time zone)
	 */
	@Test
	public void testUnitNonEnglishLocale() {
		final Locale oldLocale = Locale.getDefault();
		final TimeZone oldZone = TimeZone.getDefault();
		try {
			Locale.setDefault(Locale.GERMANY);
			TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

			final String inputFileContent = "UNIT/ADRI/OCT20/SRF/\n311200Z/DET/RDR/23/20/777/3602.02N/00412.12E/GPS/DETECTION RECORD";
			final Layers layers = new Layers();
			final NisidaLoadState status = ImportNisida
					.importThis(new ByteArrayInputStream(inputFileContent.getBytes()), layers);

			assertEquals(0, status.getErrors().size(), "no errors");
			assertEquals(2020, status.getYear(), "Year value");
			assertEquals(Calendar.OCTOBER, status.getMonth(), "Month value");

			final TrackWrapper track = (TrackWrapper) layers.findLayer("ADRI");
			final FixWrapper fix = (FixWrapper) track.getPositionIterator().nextElement();
			final Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			expected.clear();
			expected.set(2020, Calendar.OCTOBER, 31, 12, 0);
			assertEquals(expected.getTimeInMillis(), fix.getDateTimeGroup().getDate().getTime(), "correct DTG");

			// and another month whose German abbreviation differs
			final NisidaLoadState status2 = ImportNisida
					.importThis(new ByteArrayInputStream("UNIT/ADRI/MAR20/SRF/".getBytes()), new Layers());
			assertEquals(0, status2.getErrors().size(), "no errors");
			assertEquals(2020, status2.getYear(), "Year value");
			assertEquals(Calendar.MARCH, status2.getMonth(), "Month value");
		} finally {
			Locale.setDefault(oldLocale);
			TimeZone.setDefault(oldZone);
		}
	}

	/**
	 * if the UNIT date can't be read, we mustn't create fixes (or narratives)
	 * with bogus dates
	 */
	@Test
	public void testNoDataWhenUnitDateInvalid() {
		final String inputFileContent = "UNIT/ADRI/SAUL//\n311200Z/DET/RDR/23/20/777/3602.02N/00412.12E/GPS/DETECTION RECORD\n311056Z/NAR/TEXT FOR NARRATIVE";
		final Layers layers = new Layers();
		final NisidaLoadState status = ImportNisida
				.importThis(new ByteArrayInputStream(inputFileContent.getBytes()), layers);

		final TrackWrapper track = (TrackWrapper) layers.findLayer("ADRI");
		assertNotNull(track, "created O/S track");
		assertEquals(0, track.numFixes(), "no fixes created");
		assertNull(layers.findLayer(NarrativeEntry.NARRATIVE_LAYER), "no narratives");
		assertEquals(3, status.getErrors().size(), "date error plus one per skipped line");
	}
}
