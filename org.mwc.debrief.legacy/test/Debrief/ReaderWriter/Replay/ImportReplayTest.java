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

import static Debrief.ReaderWriter.Replay.ImportReplay.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportReplay.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector.ImportSettings;
import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.DynamicShapeLayer;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.IDynamicShapeWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ExportLayerAsSingleItem;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;
import MWC.Utilities.ReaderWriter.ImportProblems;
import MWC.Utilities.ReaderWriter.PlainImporter;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.ReaderWriter.PlainLineImporter.ImportRequiresFinalisation;
import MWC.Utilities.ReaderWriter.ReaderMonitor;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// ///////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ImportReplayTest {
	public static class TestParent implements ToolParent, ProvidesModeSelector {
		final ImportSettings settings;
		final Long freq;

		public TestParent(final String mode, final Long freq) {
			settings = new ImportSettings(mode, freq);
			this.freq = freq;
		}

		@Override
		public void addActionToBuffer(final Action theAction) {

		}

		@Override
		public Map<String, String> getPropertiesLike(final String pattern) {
			return null;
		}

		@Override
		public String getProperty(final String name) {
			if (name.equals(ImportReplay.TRACK_IMPORT_MODE)) {
				return settings.importMode;
			} else if (name.equals(ImportReplay.RESAMPLE_FREQUENCY)) {
				return "" + settings.sampleFrequency;
			} else {
				return null;
			}
		}

		@Override
		public Long getSelectedImportFrequency(final String trackName) {
			return null;
		}

		@Override
		public ImportSettings getSelectedImportMode(final String trackName) {
			return settings;
		}

		@Override
		public void logError(final int status, final String text, final Exception e) {

		}

		@Override
		public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
			logError(status, text, e);
		}

		@Override
		public void logStack(final int status, final String text) {
			logError(status, "Stack requested:" + text, null);
		}

		@Override
		public void restoreCursor() {

		}

		@Override
		public void setCursor(final int theCursor) {

		}

		@Override
		public void setProperty(final String name, final String value) {

		}
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private final static String shape_file = "../org.mwc.cmap.combined.feature/root_installs/sample_data/shapes.rep";

	private final static String boat_file = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";

	private String fileName = "test.rep";

	boolean fileFinished = false;

	boolean allFilesFinished = false;

	public ImportReplayTest() {
		final String fileRoot = "../org.mwc.debrief.legacy/src";

		//
		assertNotNull(fileRoot, "Check data directory is configured");
		fileName = fileRoot + File.separator + fileName;

		// and check the file exists
		final File iFile = new File(fileName);
		assertTrue(iFile.exists(), "Test file not found");
	}

	private void assertElementsInLayers(final Layer layer, final int count) {
		assertTrue(layer.elements().hasMoreElements());
		final Enumeration<Editable> elements = layer.elements();
		int lineCount = 0;
		while (elements.hasMoreElements()) {
			elements.nextElement();
			lineCount++;
		}
		assertEquals(lineCount, count);
	}

	private final void doReadRep(final String mode, final Long freq, final int LAYER_COUNT, final int NUM_FIXES,
			final boolean checkArea) {
		File testFile = null;

		// specify the parent object - so our processing can retrieve the
		// OTG setting
		ImportReplay.initialise(new TestParent(mode, freq));

		// can we load it directly
		testFile = new File(fileName);

		if (!testFile.exists()) {

			// first try to get the URL of the image
			final java.lang.ClassLoader loader = getClass().getClassLoader();
			if (loader != null) {
				final java.net.URL imLoc = loader.getResource(fileName);
				if (imLoc != null) {
					testFile = new File(imLoc.getFile());
				}
			} else {
				fail("Failed to find class loader");
			}
		}

		// did we find it?
		assertTrue(testFile.exists(), "Failed to find file:" + fileName);

		// ok, now try to read it in
		final MWC.GUI.Layers _theLayers = new MWC.GUI.Layers();
		final File[] _theFiles = new File[] { testFile };

		// add the REP importer
		MWC.Utilities.ReaderWriter.ImportManager.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

		// get our thread to import this
		final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
				_theFiles, _theLayers) {
			// handle completion of the full import process
			@Override
			public void allFilesFinished(final File[] fNames, final Layers newData) {
				allFilesFinished = true;
			}

			// handle the completion of each file
			@Override
			public void fileFinished(final File fName, final Layers newData) {
				fileFinished = true;
			}
		};

		// and start it running
		reader.start();

		// check it went ok
		assertTrue(fileFinished, "File finished received");
		assertTrue(allFilesFinished, "All Files finished received");

		assertEquals(LAYER_COUNT, _theLayers.size(), "Count of layers");

		final TrackWrapper track = (TrackWrapper) _theLayers.elementAt(0);
		final int numFixes = track.numFixes();
		assertEquals(NUM_FIXES, numFixes, "got correct num fixes");

		if (checkArea) {
			// area of coverage
			final MWC.GenericData.WorldArea area = _theLayers.elementAt(0).getBounds();
			Assertions.assertEquals(area.getTopLeft().getLat(), 11.92276, 0.01, "tl lat of first layer");
			Assertions.assertEquals(area.getTopLeft().getLong(), -11.59394, 0.01, "tl long of first layer");
			Assertions.assertEquals(area.getTopLeft().getDepth(), 0, 0.00001, "tl depth of first layer");

			Assertions.assertEquals(area.getBottomRight().getLat(), 11.89421, 0.001, "br lat of first layer");
			Assertions.assertEquals(area.getBottomRight().getLong(), -11.59376, 0.00001, "br long of first layer");
			Assertions.assertEquals(area.getBottomRight().getDepth(), 0, 0.00001, "br depth of first layer");
		}

		// check those narrative lines got read in
		final NarrativeWrapper narratives = (NarrativeWrapper) _theLayers.elementAt(2);
		assertEquals(2, narratives.size(), "have read in both narrative entries");
	}

	@Test
	public final void testDRimport() {
		doReadRep(ImportReplay.IMPORT_AS_DR, null, 3, 25, true);
	}

	@Test
	public void testIsContentImportable() {
		String textToPaste = ";LINE: @B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line\r\n"
				+ ";VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector\r\n"
				+ ";CIRCLE: @D    21.8 0 0 N 21.0 0 0 W 2000 test circle\r\n"
				+ ";TEXT: @E 21.7 0 0 N 21.5 0 0 W test text\r\n"
				+ ";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol\r\n"
				+ ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer\r\n"
				+ ";TEXT: CA[LAYER=Other_Special_Layer] 21.22 0 0 N 21.88 0 0 W Other layer 3\r\n"
				+ ";ELLIPSE: @F 951212 060200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse\r\n"
				+ ";POLY: @GA30 21.9 0 0 N 21.5 0 0 W 22 0 0 N 21.8 0 0 W 22.1 0 0 N 21.5 0 0 W test poly\r\n"
				+ ";POLYLINE: @C 21.1 0 0 N 21.5 0 0 W 21.2 0 0 N 21.8 0 0 W 21.3 0 0 N 21.5 0 0 W test polyline\r\n"
				+ ";NARRATIVE:  951212 050200 NEL_STYLE comment 3\r\n";
		assertTrue(isContentImportable(textToPaste));
		textToPaste = ";DYNAMIC_RECT: @A \"Dynamic A\" 951212 051000.000 22 00 0 N 21 00 0 W 21 50 0 N 20 50 0 W dynamic A rect 1\r\n"
				+ ";DYNAMIC_CIRCLE: @A \"Dynamic A\" 951212 052100.000 21 00 0 N 20 53 0 W 2000 dynamic A circ 12\r\n"
				+ ";DYNAMIC_POLY: @A \"Dynamic A\" 951212 052600.000 20 35 0 N 21 02 0 W 20 35 0 N 20 55 0 W 20 42 0 N 20 52 0 W 20 45 0 N 21 00 0 W  dynamic A POLY 170";
		assertTrue(isContentImportable(textToPaste));
		textToPaste = "@B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line ;VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector";
		assertFalse(isContentImportable(textToPaste));
		textToPaste = "";
		assertFalse(isContentImportable(textToPaste));
		textToPaste = "951212 050000.000 \"NEL STYLE\"   @C      22 12 10.63 N 21 31 52.37 W 269.7   2.0      0 ";
		assertTrue(isContentImportable(textToPaste));
		textToPaste = "19951212 050000.000 \"NEL STYLE\"   @C      22 12 10.63 N 21 31 52.37 W 269.7   2.0      0 ";
		assertTrue(isContentImportable(textToPaste));
	}

	@Test
	public void testNewSegment() throws IOException, ParseException {
		final String text = "951212 112700.000 NELSON   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
				+ "951212 112800.000 NELSON   @C   22  7  7.00 N 21 45 19.26 W 340.7   2.0      0 \n"
				+ "951212 112900.000 NELSON   @C[NEW_SEGMENT]   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
				+ "951212 113000.000 NELSON   @C   22  7 21.35 N 21 45 25.55 W  21.4   2.0      0 \n"
				+ "951212 113100.000 NELSON   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 ";

		final ImportReplay importReplay = new ImportReplay();
		final Layers dummyLayers = new Layers();
		importReplay.setLayers(dummyLayers);
		for (final String line : text.split("\n")) {
			importReplay.readLine(line);
		}
		final Enumeration<Editable> elemIterator = dummyLayers.elements();
		while (elemIterator.hasMoreElements()) {
			final Editable element = elemIterator.nextElement();
			assertEquals("NELSON", element.getName(), "Track Imported succesfully for new segment test");
			assertTrue(element instanceof TrackWrapper, "Correct type in the new segment test");
			final TrackWrapper trackWrapperNelson = (TrackWrapper) element;
			final SegmentList legList = trackWrapperNelson.getSegments();
			assertEquals(2, legList.size(), "Amount of segment in new segment test");
		}
	}

	@Test
	public void testMalformedLinesReported() {
		DialogFactory.setRunHeadless(true);
		final String text = "951212 112700.000 NELSON   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
				// latitude seconds are rubbish
				+ "951212 112800.000 NELSON   @C   22  7  x N 21 45 19.26 W 340.7   2.0      0 \n"
				+ "951212 112900.000 NELSON   @C   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
				// speed is rubbish
				+ "951212 113000.000 NELSON   @C   22  7 21.35 N 21 45 25.55 W  21.4   fast      0 \n"
				+ ";;just a comment\n"
				+ "951212 113100.000 NELSON   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 ";

		ImportReplay.initialise(new TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		final ImportReplay importReplay = new ImportReplay(new Runner() {
			@Override
			public void run(final Runnable runnable) {
				runnable.run();
			}
		});
		final Layers layers = new Layers();
		importReplay.setLayers(layers);
		importReplay.importThis(text, 6);

		final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON");
		assertNotNull(track, "track loaded");
		assertEquals(3, track.numFixes(), "the good fixes loaded");

		final ImportProblems problems = importReplay.getImportProblems();
		assertEquals(2, problems.size(), "both bad lines reported:" + problems.getProblems());
		assertTrue(problems.getProblems().get(0).startsWith("Line 2:"), problems.getProblems().get(0));
		assertTrue(problems.getProblems().get(1).startsWith("Line 4:"), problems.getProblems().get(1));
	}

	@Test
	public void testNewSegment2() throws IOException, ParseException {
		final String text = "951212 112700.000 NELSON   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
				+ "951212 112800.000 NELSON   @C[NEW_SEGMENT]   22  7  7.00 N 21 45 19.26 W 340.7   2.0      0 \n"
				+ "951212 112900.000 NELSON   @C[NEW_SEGMENT]   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
				+ "951212 113000.000 NELSON   @C[NEW_SEGMENT]   22  7 21.35 N 21 45 25.55 W  21.4   2.0      0 \n"
				+ "951212 113100.000 NELSON   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 ";

		final ImportReplay importReplay = new ImportReplay();
		final Layers dummyLayers = new Layers();
		importReplay.setLayers(dummyLayers);
		for (final String line : text.split("\n")) {
			importReplay.readLine(line);
		}
		final Enumeration<Editable> elemIterator = dummyLayers.elements();
		while (elemIterator.hasMoreElements()) {
			final Editable element = elemIterator.nextElement();
			assertEquals("NELSON", element.getName(), "Track Imported succesfully for new segment test");
			assertTrue(element instanceof TrackWrapper, "Correct type in the new segment test");
			final TrackWrapper trackWrapperNelson = (TrackWrapper) element;
			final SegmentList legList = trackWrapperNelson.getSegments();
			assertEquals(4, legList.size(), "Amount of segment in new segment test");
		}
	}

	@Test
	public final void testOTGimport1() {
		doReadRep(ImportReplay.IMPORT_AS_OTG, 0L, 3, 25, true);
	}

	@Test
	public final void testOTGimport2() {
		doReadRep(ImportReplay.IMPORT_AS_OTG, 300000L, 3, 5, false);
	}

	@Test
	public final void testOTGimport3() {
		doReadRep(ImportReplay.IMPORT_AS_OTG, Long.MAX_VALUE, 3, 1, false);
	}

	@Test
	public final void testParseSymbology() {
		final String test = ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer";
		final String test2 = ";TEXT: CA[LAYER=Special_Layer,TEST_ON=OFF] 21.42 0 0 N 21.88 0 0 W Other layer";

		final ImportReplay ir = new ImportReplay();

		assertEquals(null, ir.getThisSymProperty(test, "LAYDER"), "String not found");
		assertEquals("Special_Layer", ir.getThisSymProperty(test, "LAYER"), "String found");

		assertEquals(null, ir.getThisSymProperty(test2, "LAYDER"), "String not found");
		assertEquals("Special_Layer", ir.getThisSymProperty(test2, "LAYER"), "String found");
		assertEquals("OFF", ir.getThisSymProperty(test2, "TEST_ON"), "String found");
	}

	@Test
	public final void testParseSymbologyColor() {
		final String test1 = ";TEXT: 5j 21.42 0 0 N 21.88 0 0 W Other buoy";

		final LabelWrapper label1 = (LabelWrapper) new ImportLabel().readThisLine(test1);

		// correct symbol
		assertEquals("Kingpin", label1.getSymbolType(), "sym type correct");
		assertEquals(DebriefColors.LIGHT_GREEN, label1.getColor(), "color correct");

		final String test2 = ";TEXT: 4G 21.42 0 0 N 21.88 0 0 W Other buoy";

		final LabelWrapper label2 = (LabelWrapper) new ImportLabel().readThisLine(test2);

		// correct symbol
		assertEquals("Hidar", label2.getSymbolType(), "sym type correct");
		assertEquals(DebriefColors.PURPLE, label2.getColor(), "color correct");
	}

	// verify dynamic layers get added on paste
	@Test
	public final void testPasteRepDynamicShapes() {
		final String textToPaste = ";DYNAMIC_RECT: @A \"Dynamic A\" 951212 051000.000 22 00 0 N 21 00 0 W 21 50 0 N 20 50 0 W dynamic A rect 1\r\n"
				+ ";DYNAMIC_CIRCLE: @A \"Dynamic A\" 951212 052100.000 21 00 0 N 20 53 0 W 2000 dynamic A circ 12\r\n"
				+ ";DYNAMIC_POLY: @A \"Dynamic A\" 951212 052600.000 20 35 0 N 21 02 0 W 20 35 0 N 20 55 0 W 20 42 0 N 20 52 0 W 20 45 0 N 21 00 0 W  dynamic A POLY 170";
		final ImportReplay testImporter = new ImportReplay();
		final Layers tmpLayers = new Layers();
		final Layers dest = new Layers();
		testImporter.setLayers(tmpLayers);
		final int totalLines = textToPaste.split("\\r?\\n").length;
		testImporter.importThis(textToPaste, totalLines);
		ImportReplay.injectContent(tmpLayers, dest, true);
		assertElementsInLayers(tmpLayers.findLayer("Dynamic A"), 3);
		ImportReplay.injectContent(dest, tmpLayers, false);
		assertNull((tmpLayers.findLayer("Dynamic A")));

	}

	// test if previously loaded layers remains after pasting new content
	@Test
	public void testPasteRepExistingFile() {
		final String textToPaste = ";LINE: @B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line\r\n"
				+ ";VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector\r\n"
				+ ";CIRCLE: @D    21.8 0 0 N 21.0 0 0 W 2000 test circle\r\n"
				+ ";TEXT: @E 21.7 0 0 N 21.5 0 0 W test text\r\n"
				+ ";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol\r\n"
				+ ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer\r\n"
				+ ";TEXT: CA[LAYER=Other_Special_Layer] 21.22 0 0 N 21.88 0 0 W Other layer 3\r\n"
				+ ";ELLIPSE: @F 951212 060200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse\r\n"
				+ ";POLY: @GA30 21.9 0 0 N 21.5 0 0 W 22 0 0 N 21.8 0 0 W 22.1 0 0 N 21.5 0 0 W test poly\r\n"
				+ ";POLYLINE: @C 21.1 0 0 N 21.5 0 0 W 21.2 0 0 N 21.8 0 0 W 21.3 0 0 N 21.5 0 0 W test polyline\r\n"
				+ ";NARRATIVE:  951212 050200 NEL_STYLE comment 3\r\n";
		try {
			final Layers tLayers = new Layers();

			// start off with the ownship track
			final File boatFile = new File(boat_file);
			assertTrue(boatFile.exists());
			final InputStream bs = new FileInputStream(boatFile);
			final ImportReplay trackImporter = new ImportReplay();
			ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
			final int totalLines = textToPaste.split("\\r?\\n").length;
			trackImporter.importThis(shape_file, bs, tLayers);
			assertNotNull(tLayers.findLayer("NELSON"));
			final Layers tmpLayers = new Layers();
			trackImporter.setLayers(tmpLayers);
			trackImporter.importThis(textToPaste, totalLines);
			ImportReplay.injectContent(tmpLayers, tLayers, true);
			assertNotNull(tLayers.findLayer("NELSON"));
			assertElementsInLayers(tLayers.findLayer(ANNOTATION_LAYER), 8);
			assertElementsInLayers(tLayers.findLayer("Special_Layer"), 1);
			assertElementsInLayers(tLayers.findLayer("Other_Special_Layer"), 1);
			assertElementsInLayers(tLayers.findLayer(NARRATIVE_LAYER), 1);
			// test undo
			ImportReplay.injectContent(tmpLayers, tLayers, false);
			// first load a shapes rep or someother rep file, add more shapes to it and see
			// if they are added to the correct layer.
			// verify undo
			assertNull((tLayers.findLayer(ANNOTATION_LAYER)));
			assertNull(tLayers.findLayer("Special_Layer"));
			assertNull(tLayers.findLayer("Other_Special_Layer"));
			assertNull(tLayers.findLayer(NARRATIVE_LAYER));

		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	// verify layers get added and shapes get added on paste
	// verify that layers are removed when undone
	@Test
	public final void testPasteRepShapes() {
		final String textToPaste = ";LINE: @B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line\r\n"
				+ ";VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector\r\n"
				+ ";CIRCLE: @D    21.8 0 0 N 21.0 0 0 W 2000 test circle\r\n"
				+ ";TEXT: @E 21.7 0 0 N 21.5 0 0 W test text\r\n"
				+ ";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol\r\n"
				+ ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer\r\n"
				+ ";TEXT: CA[LAYER=Other_Special_Layer] 21.22 0 0 N 21.88 0 0 W Other layer 3\r\n"
				+ ";ELLIPSE: @F 951212 060200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse\r\n"
				+ ";POLY: @GA30 21.9 0 0 N 21.5 0 0 W 22 0 0 N 21.8 0 0 W 22.1 0 0 N 21.5 0 0 W test poly\r\n"
				+ ";POLYLINE: @C 21.1 0 0 N 21.5 0 0 W 21.2 0 0 N 21.8 0 0 W 21.3 0 0 N 21.5 0 0 W test polyline\r\n"
				+ ";NARRATIVE:  951212 050200 NEL_STYLE comment 3\r\n";
		final ImportReplay testImporter = new ImportReplay();
		final Layers tmpLayers = new Layers();
		final Layers dest = new Layers();
		testImporter.setLayers(tmpLayers);
		final int totalLines = textToPaste.split("\\r?\\n").length;
		testImporter.importThis(textToPaste, totalLines);
		ImportReplay.injectContent(tmpLayers, dest, true);
		assertElementsInLayers(tmpLayers.findLayer(ANNOTATION_LAYER), 8);
		assertElementsInLayers(tmpLayers.findLayer("Special_Layer"), 1);
		assertElementsInLayers(tmpLayers.findLayer("Other_Special_Layer"), 1);
		assertElementsInLayers(tmpLayers.findLayer(NARRATIVE_LAYER), 1);
		// test undo
		ImportReplay.injectContent(dest, tmpLayers, false);
		// first load a shapes rep or someother rep file, add more shapes to it and see
		// if they are added to the correct layer.
		// verify undo
		assertNull((tmpLayers.findLayer(ANNOTATION_LAYER)));
		assertNull(tmpLayers.findLayer("Special_Layer"));
		assertNull(tmpLayers.findLayer("Other_Special_Layer"));
		assertNull(tmpLayers.findLayer(NARRATIVE_LAYER));
	}

	@Test
	public void testReadShapes() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(shape_file);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(shape_file, bs, tLayers);

		assertEquals(16, tLayers.size(), "read in track");

		final TrackWrapper airTrack = (TrackWrapper) tLayers.findLayer("FRIENDLY_AIR");
		assertEquals(SymbolFactory.SVG_FORMAT_PREFIX + ":friend_air", airTrack.getSymbolType(),
				"indexed svg symbol used");

		final TrackWrapper track = (TrackWrapper) tLayers.findLayer("NEL STYLE");
		assertNotNull(track, "found track");

		// get the positions
		final Enumeration<Editable> pIter = track.getPositionIterator();
		final FixWrapper f1 = (FixWrapper) pIter.nextElement();
		assertNotNull(f1, "found item");
		assertEquals("120500", f1.getLabel(), "got label");
		final FixWrapper f2 = (FixWrapper) pIter.nextElement();
		assertEquals("Override label 1", f2.getLabel(), "got label");
		assertEquals(null, f2.getComment(), "no comment");
		final FixWrapper f3 = (FixWrapper) pIter.nextElement();
		assertEquals("Override label 2", f3.getLabel(), "got label");
		assertEquals(null, f3.getComment(), "no comment");
		final FixWrapper f4 = (FixWrapper) pIter.nextElement();
		assertEquals("Override label 3", f4.getLabel(), "got label");
		assertEquals("comment 3", f4.getComment(), "no comment");
		final FixWrapper f5 = (FixWrapper) pIter.nextElement();
		assertEquals("0504", f5.getLabel(), "got label");
		assertEquals("comment 4", f5.getComment(), "no comment");
	}

	// disabled intentionally due this:
	// https://github.com/debrief/debrief/pull/4934#issuecomment-669393732
	/*
	 * public void testTrackSplit() throws IOException, ParseException { final
	 * String text =
	 * "951212 112700.000 NELSON   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
	 * +
	 * "951212 112800.000 NELSON   @C   22  7  7.00 N 21 45 19.26 W 340.7   2.0      0 \n"
	 * +
	 * "951212 112900.000 NELSON   @C   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
	 * +
	 * "951212 113000.000 NELSON   @C   22  7 21.35 N 21 45 25.55 W  21.4   2.0      0 \n"
	 * +
	 * "951212 113100.000 NELSON   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 \n"
	 * + ";TRACKSPLIT 951212 112930.000 NELSON";
	 * 
	 * final ImportReplay importReplay = new ImportReplay(); final Layers
	 * dummyLayers = new Layers(); importReplay.setLayers(dummyLayers); for (final
	 * String line : text.split("\n")) { importReplay.readLine(line); } final
	 * Enumeration<Editable> elemIterator = dummyLayers.elements(); while
	 * (elemIterator.hasMoreElements()) { final Editable element =
	 * elemIterator.nextElement();
	 * assertEquals("Track Imported succesfully for track split test", "NELSON",
	 * element.getName()); assertTrue("Correct type in the new segment test",
	 * element instanceof TrackWrapper); final TrackWrapper trackWrapperNelson =
	 * (TrackWrapper) element; final SegmentList legList =
	 * trackWrapperNelson.getSegments();
	 * assertEquals("Amount of segment in new segment test", 2, legList.size()); } }
	 * 
	 * public void testTrackSplit2() throws IOException, ParseException { final
	 * String text =
	 * "951212 112700.000 NELSON   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
	 * +
	 * "951212 112800.000 NELSON   @C   22  7  7.00 N 21 45 19.26 W 340.7   2.0      0 \n"
	 * +
	 * "951212 112900.000 NELSON   @C   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
	 * +
	 * "951212 113000.000 NELSON   @C   22  7 21.35 N 21 45 25.55 W  21.4   2.0      0 \n"
	 * +
	 * "951212 113100.000 NELSON   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 \n"
	 * + ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 113030.000 NELSON";
	 * 
	 * final ImportReplay importReplay = new ImportReplay(); final Layers
	 * dummyLayers = new Layers(); importReplay.setLayers(dummyLayers); for (final
	 * String line : text.split("\n")) { importReplay.readLine(line); } final
	 * Enumeration<Editable> elemIterator = dummyLayers.elements(); while
	 * (elemIterator.hasMoreElements()) { final Editable element =
	 * elemIterator.nextElement();
	 * assertEquals("Track Imported succesfully for track split test", "NELSON",
	 * element.getName()); assertTrue("Correct type in the new segment test",
	 * element instanceof TrackWrapper); final TrackWrapper trackWrapperNelson =
	 * (TrackWrapper) element; final SegmentList legList =
	 * trackWrapperNelson.getSegments();
	 * assertEquals("Amount of segment in track split test", 3, legList.size()); } }
	 * 
	 * public void testTrackSplit3() throws IOException, ParseException { final
	 * String text =
	 * "951212 112700.000 NELSON   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
	 * +
	 * "951212 112800.000 NELSON   @C   22  7  7.00 N 21 45 19.26 W 340.7   2.0      0 \n"
	 * +
	 * "951212 112900.000 NELSON   @C   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
	 * +
	 * "951212 113000.000 NELSON   @C   22  7 21.35 N 21 45 25.55 W  21.4   2.0      0 \n"
	 * +
	 * "951212 113100.000 NELSON   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 \n"
	 * +
	 * "951212 112700.000 SAUL   @C   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
	 * +
	 * "951212 112800.000 SAUL   @C   22  7  7.00 N 21 45 19.26 W 340.7   2.0      0 \n"
	 * +
	 * "951212 112900.000 SAUL   @C   22  7 14.33 N 21 45 23.01 W 346.1   2.0      0 \n"
	 * +
	 * "951212 113000.000 SAUL   @C   22  7 21.35 N 21 45 25.55 W  21.4   2.0      0 \n"
	 * +
	 * "951212 113100.000 SAUL   @C   22  7 26.42 N 21 45 22.64 W  21.0   2.0      0 \n"
	 * + ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 112930.000 NELSON\n" +
	 * ";TRACKSPLIT 951212 113030.000 NELSON";
	 * 
	 * final ImportReplay importReplay = new ImportReplay(); final Layers
	 * dummyLayers = new Layers(); importReplay.setLayers(dummyLayers); for (final
	 * String line : text.split("\n")) { importReplay.readLine(line); } final
	 * Enumeration<Editable> elemIterator = dummyLayers.elements(); while
	 * (elemIterator.hasMoreElements()) { final Editable element =
	 * elemIterator.nextElement(); if ("SAUL".equals(element.getName())) {
	 * assertEquals("Track Imported succesfully for track split test", "SAUL",
	 * element.getName()); assertTrue("Correct type in the new segment test",
	 * element instanceof TrackWrapper); final TrackWrapper trackWrapperNelson =
	 * (TrackWrapper) element; final SegmentList legList =
	 * trackWrapperNelson.getSegments();
	 * assertEquals("Amount of segment in track non mentioned test", 1,
	 * legList.size()); } else if ("NELSON".equals(element.getName())) {
	 * assertEquals("Track Imported succesfully for track split test", "NELSON",
	 * element.getName()); assertTrue("Correct type in the new segment test",
	 * element instanceof TrackWrapper); final TrackWrapper trackWrapperNelson =
	 * (TrackWrapper) element; final SegmentList legList =
	 * trackWrapperNelson.getSegments();
	 * assertEquals("Amount of segment in new segment test", 3, legList.size()); } }
	 * }
	 */

	@Test
	public void testTrackSvgSymbol() throws IOException, ParseException {
		final String text = "951212 112700.000 FRIEND_AIR_INDEXED   yC   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 \n"
				+ "951212 112700.000 FRIEND_AIR_NAMED   @C[SYMBOL=friend_air]   22  7  0.63 N 21 45 14.91 W 334.9   2.0      0 ";

		final ImportReplay importReplay = new ImportReplay();
		final Layers dummyLayers = new Layers();
		importReplay.setLayers(dummyLayers);
		for (final String line : text.split("\n")) {
			importReplay.readLine(line);
		}

		final String friendAir = SymbolFactory.SVG_FORMAT_PREFIX + ":friend_air";
		final TrackWrapper indexed = (TrackWrapper) dummyLayers.findLayer("FRIEND_AIR_INDEXED");
		assertEquals(friendAir, indexed.getSymbolType(), "indexed symbol applied to track");
		final TrackWrapper named = (TrackWrapper) dummyLayers.findLayer("FRIEND_AIR_NAMED");
		assertEquals(friendAir, named.getSymbolType(), "named symbol applied to track");

		// and check it's written back out using the index
		assertEquals("yC", replaySymbolFor(DebriefColors.RED, friendAir), "exported with index");
	}

	@Test
	public void testTrailingComment() {
		final String test1 = " LABEL // COMMENT";
		assertEquals("LABEL", getLabel(test1));
		assertEquals("COMMENT", getComment(test1));

		final String test2 = " LABEL";
		assertEquals("LABEL", getLabel(test2));
		assertEquals(null, getComment(test2));

		final String test3 = " LABEL//";
		assertEquals("LABEL", getLabel(test3));
		assertEquals(null, getComment(test3));

		final String test4 = "//COMMENT";
		assertEquals(null, getLabel(test4));
		assertEquals("COMMENT", getComment(test4));

		final String test5 = "";
		assertEquals(null, getLabel(test5));
		assertEquals(null, getComment(test5));

		final String test6 = null;
		assertEquals(null, getLabel(test6));
		assertEquals(null, getComment(test6));
	}
}
