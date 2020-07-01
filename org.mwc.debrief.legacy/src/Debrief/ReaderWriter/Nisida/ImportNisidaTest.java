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

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import junit.framework.TestCase;

public class ImportNisidaTest extends TestCase {
	private final String nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/nisida_sample.txt";
	private final String not_nisida_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/CLOG_Trial_sample.txt";

	public void testCanLoad() throws FileNotFoundException {
		ImportNisida importer = new ImportNisida();
		assertTrue(importer.canLoadThisFile(new FileInputStream(nisida_track)));
		assertFalse(importer.canLoadThisFile(new FileInputStream(not_nisida_track)));
	}
	
	public void testLoad() throws FileNotFoundException {
		ImportNisida importer = new ImportNisida();
		FileInputStream fis = new FileInputStream(nisida_track);
		final Layers layers = new Layers();
		importer.importThis(fis, layers);
		
		assertEquals("created layers", 4, layers.size());
		Layer ownshipLayer = layers.findLayer("ADRI");
		assertNotNull("created O/S track", ownshipLayer);
		assertTrue("is track wrapper", ownshipLayer instanceof TrackWrapper);
		TrackWrapper ownship = (TrackWrapper) ownshipLayer;
		assertEquals("correct number of points", 4, ownship.numFixes());
	}
}
