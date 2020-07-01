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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import Debrief.ReaderWriter.Nisida.ImportNisida.NisidaLoadState;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

public class ImportNisidaTest extends TestCase {
	private final String nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/nisida_sample.txt";
	private final String not_nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/CLOG_Trial.txt";

	public void testCanLoad() throws FileNotFoundException {
		assertTrue(ImportNisida.canLoadThisFile(new FileInputStream(nisida_track)));
		assertFalse(ImportNisida.canLoadThisFile(new FileInputStream(not_nisida_track)));
	}


	public void testParseValue() {
		Layers layers = new Layers();
		NisidaLoadState status = new NisidaLoadState(layers);
		assertEquals(ImportNisida.valueFor("1230", status), 1230d);
		assertEquals(ImportNisida.valueFor("-", status), null);
		assertEquals(ImportNisida.valueFor("", status), null);
	}
	
	public void testParseLocation() {
		assertEquals(ImportNisida.parseDegrees("1230.00N"), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.00S"), -12.5);
		assertEquals(ImportNisida.parseDegrees("01230.00E"), 12.5);
		assertEquals(ImportNisida.parseDegrees("01230.00W"), -12.5);
		// try some weird formats
		assertEquals(ImportNisida.parseDegrees("1230.0000N"), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.0000N"), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.00E"), 12.5);
		assertEquals(ImportNisida.parseDegrees("1230.00W"), -12.5);
	}
	
	public void testLoad() throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(nisida_track);
		final Layers layers = new Layers();
		ImportNisida.importThis(fis, layers);
		
		assertEquals("created layers", 2, layers.size());
		
		// check the narrative entries
		Layer narrativeLayer = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
		assertNotNull("found narratives");
		assertTrue("of correct type", narrativeLayer instanceof NarrativeWrapper);
		NarrativeWrapper narratives = (NarrativeWrapper) narrativeLayer;
		assertEquals("found entries", 3, narratives.size());
		
		
		Layer ownshipLayer = layers.findLayer("ADRI");
		assertNotNull("created O/S track", ownshipLayer);
		assertTrue("is track wrapper", ownshipLayer instanceof TrackWrapper);
		TrackWrapper ownship = (TrackWrapper) ownshipLayer;
		assertEquals("correct number of points", 4, ownship.numFixes());
	}
}
