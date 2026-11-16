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

package Debrief.ReaderWriter.Word;

import static Debrief.ReaderWriter.Word.ImportWordDummy.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Word.ImportWordDummy.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.junit.jupiter.api.Test;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportWordDummyTest {
	private final static String doc_path = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/narrative.doc";

	List<String> tstMessages = new ArrayList<String>();

	private FixWrapper createF(final HiResDate dtg) {

		final WorldLocation loc = new WorldLocation(2, 2, 2);
		final Fix newF = new Fix(dtg, loc, 0, 0);
		final FixWrapper fw = new FixWrapper(newF);
		return fw;
	}

	@Test
	public void testImportEmptyLayers() throws FileNotFoundException {
		final String testFile = doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final Layers tLayers = new Layers();

		final ImportWordDummy importer = new ImportWordDummy(tLayers);
		importer.importThis(testFile, is);

		// hmmm, how many tracks
		assertEquals(1, tLayers.size(), "got new tracks");
	}

	@Test
	public void testImportHostPresentNoFixes() throws FileNotFoundException {
		tstMessages.clear();
		final String testFile = doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final Layers tLayers = new Layers();

		final TrackWrapper track = new TrackWrapper();
		track.setName("Nelson");
		tLayers.addThisLayer(track);

		final ImportWordDummy importer = new ImportWordDummy(tLayers) {

			@Override
			public void logError(final String msg, final Exception e) {
				tstMessages.add(msg);
			}

		};
		importer.importThis(testFile, is);

		// hmmm, how many tracks
		assertEquals(2, tLayers.size(), "got new tracks");

		assertEquals(19, tstMessages.size(), "received messages");
	}

	@Test
	public void testImportHostPresentWithFixes() throws FileNotFoundException, ParseException {
		tstMessages.clear();
		final String testFile = doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final DateFormat df = new GMTDateFormat("yyyy,MM,dd,HH:mm:ss");

		final InputStream is = new FileInputStream(testI);

		final Layers tLayers = new Layers();

		final TrackWrapper track = new TrackWrapper();
		track.setName("Nelson");
		tLayers.addThisLayer(track);

		track.add(createF(new HiResDate(df.parse("1995,12,12,06:21:32"))));
		track.add(createF(new HiResDate(df.parse("1995,12,12,06:34:32"))));
		track.add(createF(new HiResDate(df.parse("1995,12,12,06:56:32"))));

		final ImportWordDummy importer = new ImportWordDummy(tLayers) {

			@Override
			public void logError(final String msg, final Exception e) {
				tstMessages.add(msg);
			}

		};
		importer.importThis(testFile, is);

		// hmmm, how many tracks
		assertEquals(4, tLayers.size(), "got new tracks");

		assertEquals(1, tstMessages.size(), "received messages");
	}

	@Test
	public void testNameHandler() {
		final Layers layers = new Layers();
		final TrackWrapper track = new TrackWrapper();
		track.setName("Nelson");
		layers.addThisLayer(track);
		final TrackWrapper track2 = new TrackWrapper();
		track2.setName("Iron Duck");
		layers.addThisLayer(track2);
		final ImportWordDummy iw = new ImportWordDummy(layers);
		String match = iw.trackFor("HMS Boat", "HMS Boat");
		assertNull(match, "not found match");
		match = iw.trackFor("HMS Nelson", "HMS Nelson");
		assertNotNull(match, "found match");
		match = iw.trackFor("Hms Nelson", "Hms Nelson");
		assertNotNull(match, "found match");
		match = iw.trackFor("RNAS Nelson", "RNAS Nelson");
		assertNotNull(match, "found match");

		// check we've created new entries
		assertEquals(3, iw.nameMatches.size(), "name matches");

		// and the two word name
		match = iw.trackFor("Hms Iron Duck", "Hms Iron Duck");
		assertNotNull(match, "found match");

		// check we've created new entries
		assertEquals(4, iw.nameMatches.size(), "name matches");

	}

	@Test
	public void testParseFCS() {
		FCSEntry fe = new FCSEntry(" B:124 R:23434 Track:T_NAME");
		assertEquals(23434d, fe.rangYds, "got range:");
		assertEquals(124d, fe.brgDegs, "got brg:");
		assertEquals("T_NAME", fe.tgtName, "got name:");

		fe = new FCSEntry(" B:124.44 R:23434.2 Track:T NAME");
		assertEquals(23434.2, fe.rangYds, "got range:");
		assertEquals(124.44, fe.brgDegs, "got brg:");
		assertEquals("T NAME", fe.tgtName, "got name:");

	}

}
