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

package Debrief.ReaderWriter.BRT;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.BRT.BRTImporter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Debrief.ReaderWriter.BRT.BRTImporter.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.WormInHoleOffset;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.TacticalData.NarrativeWrapper;
import org.junit.jupiter.api.Test;

public class BRTImporterTest {

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private static TrackWrapper createTrack(final String name, final Color color) {
		final TrackWrapper res = new TrackWrapper();
		res.setColor(color);
		res.setName(name);
		return res;
	}

	private static void loadLayers(final Layers layers, final String filename, final String root)
			throws IOException {
		final String fName = root + filename;
		// start off with the ownship track
		final File boatFile = new File(fName);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new Debrief.ReaderWriter.Replay.ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(fName, bs, layers);
	}

	final Layer[] allLayers = new Layer[] { new NarrativeWrapper("Test0"), new NarrativeWrapper("Test1"),
			createTrack("1", DebriefColors.BLUE), new NarrativeWrapper("Test2"),
			createTrack("2", DebriefColors.BLUE), createTrack("3", DebriefColors.RED),
			createTrack("4", DebriefColors.BLUE) };

	@Test
	public void testFindTrack() {

		final Layers testLayers = new Layers();
		for (final Layer l : allLayers) {
			testLayers.addThisLayer(l);
		}

		TrackWrapper selectedTrack;
		selectedTrack = findTrack(getTracks(testLayers));
		assertEquals(null, selectedTrack, "Return null since several blue tracks");

		testLayers.clear();
		testLayers.addThisLayer(allLayers[0]);
		testLayers.addThisLayer(allLayers[4]);

		selectedTrack = findTrack(getTracks(testLayers));
		assertEquals(allLayers[4], selectedTrack, "Selecting track from only one track");

		testLayers.clear();
		testLayers.addThisLayer(allLayers[0]);
		testLayers.addThisLayer(allLayers[4]);
		testLayers.addThisLayer(allLayers[5]);

		selectedTrack = findTrack(getTracks(testLayers));
		assertEquals(allLayers[4], selectedTrack, "Selecting track from only one blue tracks");

		testLayers.clear();
		testLayers.addThisLayer(allLayers[0]);
		testLayers.addThisLayer(allLayers[5]);

		selectedTrack = findTrack(getTracks(testLayers));
		assertEquals(allLayers[5], selectedTrack, "Selecting track from only one (non-blue) track");

		testLayers.clear();
		testLayers.addThisLayer(allLayers[0]);
		testLayers.addThisLayer(allLayers[5]);

		selectedTrack = findTrack(getTracks(testLayers));
		assertEquals(allLayers[5], selectedTrack, "Selecting track from only one (non-blue) track");

		testLayers.clear();
		testLayers.addThisLayer(allLayers[0]);
		testLayers.addThisLayer(allLayers[5]);
		testLayers.addThisLayer(allLayers[6]);

		selectedTrack = findTrack(getTracks(testLayers));
		assertEquals(allLayers[6], selectedTrack, "Selecting track from only one (non-blue) track");

	}

	@Test
	public void testGetTracks() {

		final Layers testLayers = new Layers();
		for (final Layer l : allLayers) {
			testLayers.addThisLayer(l);
		}

		final TrackWrapper[] tracks = BRTImporter.getTracks(testLayers);
		assertEquals(allLayers[2], tracks[0], "getTrack getting TrackWrapper");
		assertEquals(allLayers[4], tracks[1], "getTrack getting TrackWrapper");
		assertEquals(allLayers[5], tracks[2], "getTrack getting TrackWrapper");
		assertEquals(4, tracks.length, "amount of tracks extracted");

	}

	@Test
	public void testImportHull() throws Exception {
		final Layers layers = new Layers();
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_data/brt_import/";

		// load the parent track
		loadLayers(layers, "All Tracks.rep", root);

		assertEquals(2, layers.size(), "read in track");

		final String brtName = root + "HULL_ZERO.brt";

		final TrackWrapper sensorTrack = (TrackWrapper) layers.findLayer("SENSOR");
		final WorldDistance offset = null;
		final WorldDistance length = new WorldDistance(20000, WorldDistance.METRES);
		final BRTHelperHeadless headless = new BRTHelperHeadless(false, offset, DebriefColors.BLUE, length,
				sensorTrack, true, "test_headless_sensor");

		assertEquals(0, sensorTrack.getSensors().size(), "has zero sensors");

		final BRTImporter importer = new BRTImporter();
		final ImportBRTAction action = importer.importThis(headless, new FileInputStream(brtName));

		action.execute();

		// check the data is loaded as expected
		assertEquals(1, sensorTrack.getSensors().size(), "now has new sensor");

		action.undo();

		// check the data is loaded as expected
		assertEquals(0, sensorTrack.getSensors().size(), "now has zero sensors");

		action.execute();

		// check the data is loaded as expected
		assertEquals(1, sensorTrack.getSensors().size(), "now has new sensor");

		// have a look at the data
		final SensorWrapper sensor = (SensorWrapper) sensorTrack.getSensors().elements().nextElement();
		assertEquals(31, sensor.size(), "has all cuts");
		assertEquals(0d, sensor.getSensorOffset().getValue(), "correct offset");

		// look at a cut
		final SensorContactWrapper cut = (SensorContactWrapper) sensor.elements().nextElement();
		assertEquals(length, cut.getRange(), "correct length");
		assertEquals(sensorTrack.getName(), cut.getTrackName(), "correct track");
		assertEquals(0d, cut.getBearing(), "correct bearing");
		assertEquals(Double.NaN, cut.getAmbiguousBearing(), "correct ambig bearing");

		final double course = MWC.Algorithms.Conversions
				.Rads2Degs(sensorTrack.getNearestTo(cut.getDTG())[0].getCourse());
		assertEquals(291d, course, "correct ownship course");

	}

	@Test
	public void testImportTowed() throws Exception {
		final Layers layers = new Layers();
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_data/brt_import/";

		// load the parent track
		loadLayers(layers, "All Tracks.rep", root);

		assertEquals(2, layers.size(), "read in track");

		final String brtName = root + "TOWED_1000m.brt";

		final TrackWrapper sensorTrack = (TrackWrapper) layers.findLayer("SENSOR");
		final WorldDistance offset = new WorldDistance(1000, WorldDistance.METRES);
		final WorldDistance length = new WorldDistance(20000, WorldDistance.METRES);
		final BRTHelperHeadless headless = new BRTHelperHeadless(true, offset, DebriefColors.BLUE, length,
				sensorTrack, true, "test_headless_sensor");

		assertEquals(0, sensorTrack.getSensors().size(), "has zero sensors");

		final BRTImporter importer = new BRTImporter();
		final ImportBRTAction action = importer.importThis(headless, new FileInputStream(brtName));

		action.execute();

		// check the data is loaded as expected
		assertEquals(1, sensorTrack.getSensors().size(), "now has new sensor");

		action.undo();

		// check the data is loaded as expected
		assertEquals(0, sensorTrack.getSensors().size(), "now has zero sensors");

		action.execute();

		// check the data is loaded as expected
		assertEquals(1, sensorTrack.getSensors().size(), "now has new sensor");

		// have a look at the data
		final SensorWrapper sensor = (SensorWrapper) sensorTrack.getSensors().elements().nextElement();
		assertEquals(31, sensor.size(), "has all cuts");
		assertEquals(offset, sensor.getSensorOffset(), "correct offset");

		// look at a cut
		final SensorContactWrapper cut = (SensorContactWrapper) sensor.elements().nextElement();
		assertEquals(length, cut.getRange(), "correct length");
		assertEquals(sensorTrack.getName(), cut.getTrackName(), "correct track");
		assertEquals(0d, cut.getBearing(), "correct bearing");
		assertEquals(222d, cut.getAmbiguousBearing(), "correct ambig bearing");

		final double course = MWC.Algorithms.Conversions
				.Rads2Degs(sensorTrack.getNearestTo(cut.getDTG())[0].getCourse());
		assertEquals(291d, course, "correct ownship course");

	}

	@Test
	public void testLoadBRT() throws IOException, Exception {

		final String fileContent = "1263297600.000000, 69.00\n" + "1263297840.000000, 58.90\n"
				+ "1263298080.000000, 56.70";

		final BRTData data = BRTImporter
				.readBRT(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));
		assertEquals(3, data.getBearings().length, "Size of bearing is correct");
		assertEquals(3, data.getTimes().length, "Size of time is correct");
		assertEquals(69.0, data.getBearings()[0], 0.001, "Reading first bearing correctly");
		assertEquals(58.9, data.getBearings()[1], 0.001, "Reading second bearing correctly");
		assertEquals(56.7, data.getBearings()[2], 0.001, "Reading third bearing correctly");

		assertEquals(1263297600000L, (long) data.getTimes()[0], "Reading first date correctly");
		assertEquals(1263297840000L, (long) data.getTimes()[1], "Reading first date correctly");
		assertEquals(1263298080000L, (long) data.getTimes()[2], "Reading first date correctly");

	}
}
