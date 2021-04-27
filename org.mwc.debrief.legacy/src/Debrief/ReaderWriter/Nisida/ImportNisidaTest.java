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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Debrief.ReaderWriter.Nisida.ImportNisida.NisidaLoadState;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

public class ImportNisidaTest extends TestCase {
	private final String nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/nisida_sample.txt";
	private final String not_nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/Clog_Trial.txt";

	public void testAttack() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311206Z/ATT/OTHER";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		final Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull("found narrative", narrativeLayer);
		assertTrue("of correct type", narrativeLayer instanceof NarrativeWrapper);
		final NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals("found entries", 1, narratives.size());
		final NarrativeEntry narrative = (NarrativeEntry) narratives.elements().nextElement();
		assertEquals("Content of the narrative", "OTHER", narrative.getEntry());
	}

	public void testCanLoad() throws FileNotFoundException {
		assertTrue(ImportNisida.canLoadThisFile(new FileInputStream(nisida_track)));
		try {
			ImportNisida.canLoadThisFile(new FileInputStream(not_nisida_track));
			fail("It was able to read a file that doesn't exits");
		} catch (final Exception e) {
			// OK
		}

	}

	public void testDetection() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311200Z/DET/RDR/23/20/777/3602.02N/00412.12E/GPS/DETECTION RECORD";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		assertEquals("Correct Layer Unit", 1, layers.size());
		final Layer ownshipLayer = layers.findLayer("ADRI");
		final TrackSegment leg = (TrackSegment) ownshipLayer.elements().nextElement();
		final FixWrapper fix = (FixWrapper) leg.elements().nextElement();

		assertEquals("Correct Location", 36.03366666666667, fix.getLocation().getLat(), 1e-8);
		assertEquals("Correct Location", 4.202, fix.getLocation().getLong(), 1e-8);
	}

	public void testDetectionIncorrectValue() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311200Z/DET/RDR/23/20";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		assertEquals("Correct Layer Unit", 1, layers.size());
		final Layer ownshipLayer = layers.findLayer("ADRI");
		final SegmentList net = (SegmentList) ownshipLayer.elements().nextElement();

		assertEquals("Incorrect Detection ignored sucessfully", 0, net.size());

	}

	public void testLoad() throws FileNotFoundException {
		final FileInputStream fis = new FileInputStream(nisida_track);
		final Layers layers = new Layers();
		ImportNisida.importThis(fis, layers);

		assertEquals("created layers", 5, layers.size());

		// check the narrative entries
		final Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull("found narratives", narrativeLayer);
		assertTrue("of correct type", narrativeLayer instanceof NarrativeWrapper);
		final NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals("found entries", 10, narratives.size());

		final Layer ownshipLayer = layers.findLayer("ADRI");
		assertNotNull("created O/S track", ownshipLayer);
		assertTrue("is track wrapper", ownshipLayer instanceof TrackWrapper);
		final TrackWrapper ownship = (TrackWrapper) ownshipLayer;
		assertEquals("correct number of points", 5, ownship.numFixes());
	}

	public void testNarrativeLine() {
		final String inputFileContent = "UNIT/ADRI/OCT03/SRF/\n311056Z/NAR/TEXT FOR NARRATIVE PURPOSES WHICH";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		ImportNisida.importThis(targetStream, layers);

		final Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull("found narrative", narrativeLayer);
		assertTrue("of correct type", narrativeLayer instanceof NarrativeWrapper);
		final NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals("found entries", 1, narratives.size());
		final NarrativeEntry narrative = (NarrativeEntry) narratives.elements().nextElement();
		assertEquals("Content of the narrative", "TEXT FOR NARRATIVE PURPOSES WHICH", narrative.getEntry());
	}

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

	public void testParseValue() {
		final NisidaLoadState status = new NisidaLoadState(null);
		assertEquals(ImportNisida.valueFor("1230", status), 1230d);
		assertEquals(ImportNisida.valueFor("-", status), null);
		assertEquals(ImportNisida.valueFor("", status), null);
	}

	public void testUnit() {
		final String inputFileContent = "UNIT/ADRI/SAUL//";
		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		final NisidaLoadState status = ImportNisida.importThis(targetStream, layers);

		assertEquals("Correct Layer Unit", 1, layers.size());
		final Layer ownshipLayer = layers.findLayer("ADRI");
		assertNotNull("created O/S track", ownshipLayer);
		// 0 because we have added a wrong date format
		assertEquals("Year value", 0, status.getYear());
		assertEquals("Month value", 0, status.getYear());

	}
}
