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

import static Debrief.ReaderWriter.Replay.ImportNarrative.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportNarrative.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.StringTokenizer;
import MWC.GUI.Layers;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class ImportNarrativeTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@BeforeEach
	public void setUp() {
		DialogFactory.setRunHeadless(true);
	}

	@Test
	public void testImportNarrative() {

		final String testFile = "../org.mwc.debrief.legacy/src"
				+ "/Debrief/ReaderWriter/Replay/test_import_narrative.rep";

		// check we can find the file
		final java.io.File file = new java.io.File(testFile);
		assertTrue(file.exists(), "test file not found:" + testFile);

		// ok, now try to read it in
		final Layers theHolder = new Layers();

		// add the REP importer
		MWC.Utilities.ReaderWriter.ImportManager.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

		final PlainImporterBase pib = new Debrief.ReaderWriter.Replay.ImportReplay();

		try {
			pib.importThis(testFile, new FileInputStream(testFile), theHolder);
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options | File Templates.
		}

		// check we have the right number of lines read in
		final NarrativeWrapper nw = (NarrativeWrapper) theHolder.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(nw);

		// and count the items
		assertEquals(11, nw.getData().size(), "read in correct num of lines");
	}

	@Test
	public void testExportRoundTrip() throws ParseException {
		final ImportNarrative in = new ImportNarrative();
		final NarrativeEntry original = (NarrativeEntry) in
				.readThisLine(";NARRATIVE:	020421	121857	NELSON	Contact regained");
		final String exported = in.exportThis(original);
		final NarrativeEntry reread = (NarrativeEntry) in.readThisLine(exported);
		assertEquals("NELSON", reread.getTrackName(), "track name kept:" + exported);
		assertEquals("Contact regained", reread.getEntry(), "entry kept:" + exported);

		// and a multi-word track name
		final NarrativeEntry quoted = (NarrativeEntry) in
				.readThisLine(";NARRATIVE:	020421	121857	\"HMS TORBAY\"	Contact regained");
		final NarrativeEntry reQuoted = (NarrativeEntry) in.readThisLine(in.exportThis(quoted));
		assertEquals("HMS TORBAY", reQuoted.getTrackName());
		assertEquals("Contact regained", reQuoted.getEntry());
	}

	@Test
	public void testImportQuotedLine() throws ParseException {
		final String theLine = ";NARRATIVE:	020421	121857	\"HMS TORBAY\" 	GenComment	Mk Rge BAAA R121212";
		final ImportNarrative in = new ImportNarrative();
		final Object res = in.readThisLine(theLine);
		final NarrativeEntry ne = (NarrativeEntry) res;

		// check it contains the right data
		final String theDate = ne.getDTGString();
		assertEquals("020421 121857", theDate);
		assertEquals("HMS TORBAY", ne.getTrackName(), "found track name");
	}

	@Test
	public void testImportSingleLine() throws ParseException {
		final String theLine = ";NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
		final ImportNarrative in = new ImportNarrative();
		final Object res = in.readThisLine(theLine);
		final NarrativeEntry ne = (NarrativeEntry) res;

		// check it contains the right data
		final String theDate = ne.getDTGString();
		assertEquals(theDate, "020421 121857");
		assertEquals("HMS_TORBAY", ne.getTrackName(), "found track name");
	}

	@Test
	public void testImportWithWhitespace1() throws ParseException {
		final String theLine = "  ;NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
		final ImportNarrative in = new ImportNarrative();
		final Object res = in.readThisLine(theLine);
		final NarrativeEntry ne = (NarrativeEntry) res;

		// check it contains the right data
		final String theDate = ne.getDTGString();
		assertEquals(theDate, "020421 121857");
		assertEquals("HMS_TORBAY", ne.getTrackName(), "found track name");
	}

	@Test
	public void testImportWithWhitespace2() throws ParseException {
		final String theLine = "\t;NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
		final ImportNarrative in = new ImportNarrative();
		final Object res = in.readThisLine(theLine);
		final NarrativeEntry ne = (NarrativeEntry) res;

		// check it contains the right data
		final String theDate = ne.getDTGString();
		assertEquals(theDate, "020421 121857");
		assertEquals("HMS_TORBAY", ne.getTrackName(), "found track name");
	}
}
