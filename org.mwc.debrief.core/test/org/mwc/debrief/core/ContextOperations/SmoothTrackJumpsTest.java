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

package org.mwc.debrief.core.ContextOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.core.ContextOperations.SmoothTrackJumps.*;
import org.mwc.debrief.core.ContextOperations.SmoothTrackJumps.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.PlainImporter.MonitorProvider;
import org.junit.jupiter.api.Test;

public class SmoothTrackJumpsTest {
	private static boolean isOnTrackDegs(final double existingDegs, final double newDegs) {
		return isOnTrack(MWC.Algorithms.Conversions.Degs2Rads(existingDegs),
				MWC.Algorithms.Conversions.Degs2Rads(newDegs));
	}

	@Test
	public static void testOnTrack() {
		assertTrue(isOnTrackDegs(90, 90), "is on track");
		assertTrue(isOnTrackDegs(90, 1), "is on track");
		assertTrue(isOnTrackDegs(90, 179), "is on track");

		assertFalse(isOnTrackDegs(90, 359), "not on track");
		assertFalse(isOnTrackDegs(90, 270), "not on track");
		assertFalse(isOnTrackDegs(90, 181), "not on track");
		assertFalse(isOnTrackDegs(90, -90), "not on track");
		assertFalse(isOnTrackDegs(90, -1), "not on track");

		assertTrue(isOnTrackDegs(-90, -90), "is on track");
		assertTrue(isOnTrackDegs(-90, 270), "is on track");
		assertTrue(isOnTrackDegs(-90, -1), "is on track");
		assertTrue(isOnTrackDegs(-90, -179), "is on track");
		assertTrue(isOnTrackDegs(-90, 181), "is on track");
		assertTrue(isOnTrackDegs(-90, 359), "is on track");

		assertFalse(isOnTrackDegs(-90, 90), "is on track");
		assertFalse(isOnTrackDegs(-90, -270), "is on track");
		assertFalse(isOnTrackDegs(-90, 1), "is on track");
		assertFalse(isOnTrackDegs(-90, 179), "is on track");
		assertFalse(isOnTrackDegs(-90, -181), "is on track");
		assertFalse(isOnTrackDegs(-90, -359), "is on track");

		assertTrue(isOnTrackDegs(0, 89), "is on track");
		assertTrue(isOnTrackDegs(0, 1), "is on track");
		assertTrue(isOnTrackDegs(0, -89), "is on track");
		assertTrue(isOnTrackDegs(0, -1), "is on track");
		assertTrue(isOnTrackDegs(0, 0), "is on track");

		assertFalse(isOnTrackDegs(0, 159), "not on track");
		assertFalse(isOnTrackDegs(0, 260), "not on track");
		assertFalse(isOnTrackDegs(0, 181), "not on track");
		assertFalse(isOnTrackDegs(0, -91), "not on track");
		assertFalse(isOnTrackDegs(0, -260), "not on track");

		assertTrue(isOnTrackDegs(180, 159), "is on track");
		assertTrue(isOnTrackDegs(180, 260), "is on track");
		assertTrue(isOnTrackDegs(180, 181), "is on track");
		assertTrue(isOnTrackDegs(180, -91), "is on track");
		assertTrue(isOnTrackDegs(-180, 181), "is on track");
		assertTrue(isOnTrackDegs(-180, -91), "is on track");
		assertTrue(isOnTrackDegs(180, -260), "is on track");

		assertFalse(isOnTrackDegs(180, 89), "not on track");
		assertFalse(isOnTrackDegs(180, 1), "not on track");
		assertFalse(isOnTrackDegs(180, -89), "not on track");
		assertFalse(isOnTrackDegs(180, -1), "not on track");
		assertFalse(isOnTrackDegs(180, 0), "not on track");
	}

	@Test
	public void testFindLegs() throws FileNotFoundException, ExecutionException {
		final ImportReplay importer = new ImportReplay();
		final Layers theLayers = new Layers();
		final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/smooth_back_jumps/jumps.rep";
		final File inFile = new File(fName);
		assertTrue(inFile.exists(), "input file exists");
		final FileInputStream is = new FileInputStream(fName);
		importer.importThis(fName, is, theLayers, new MonitorProvider() {

			@Override
			public void done() {
				// not implemented.
			}

			@Override
			public void init(final String fileName, final int length) {
				System.out.println("Loading " + fileName);
			}

			@Override
			public void progress(final int _progress) {
				// not implemented.

				// comment out progress marker - too verbose
				// System.out.println(_progress);
			}
		});

		// sort out the sensors
		importer.storePendingSensors();

		// get the sensor track
		final TrackWrapper track = (TrackWrapper) theLayers.findLayer("NONSUCH");

		final TrackSegment seg = (TrackSegment) track.getSegments().first();

		final int len = 826;

		assertEquals(len, seg.size(), "correct num positions");

		final SmoothJumps jumps = new SmoothJumps("smooth jumps", theLayers, track, seg.getData());

		// ok, apply operation
		jumps.execute(null, null);

		assertEquals(len, seg.size(), "still correct num positions");

		assertEquals(7, jumps._newFixes.size(), "found correct num jumps");

		// ok, undo operation
		jumps.undo(null, null);

		assertEquals(len, seg.size(), "still correct num positions");

		assertEquals(7, jumps._newFixes.size(), "found correct num jumps");

		// ok, redo operation
		jumps.undo(null, null);

		assertEquals(len, seg.size(), "still correct num positions");

		assertEquals(7, jumps._newFixes.size(), "found correct num jumps");

	}
}
