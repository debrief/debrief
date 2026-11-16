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

import static Debrief.ReaderWriter.Replay.ImportPlanningLegOrigin.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportPlanningLegOrigin.*;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.PlanningSegment.ClosingSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

/**
 * @author Administrator
 *
 */
public class ImportPlanningLegOriginTest {

	private final static String tracks = "../org.mwc.cmap.combined.feature/root_installs/sample_data/planning_tracks.rep";

	@Test
	public void testImport() throws FileNotFoundException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(tracks);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(tracks, bs, tLayers);

		assertEquals(2, tLayers.size(), "read in tracks");

		// ok, now export one of them
		final CompositeTrackWrapper ctw1 = (CompositeTrackWrapper) tLayers.findLayer("SENSOR_PLANNED");
		assertNotNull(ctw1, "found planning leg");
		trackImporter.exportThis(ctw1);

		final String output = trackImporter.getBuffer().toString();
		assertNotNull("produced output");
		assertTrue(output.length() > 200, "has content"); // was originally 388 cgars

		final SegmentList legs1 = ctw1.getSegments();
		assertEquals(5, legs1.size(), "correct number of legs");

		// and the other one
		final CompositeTrackWrapper ctw = (CompositeTrackWrapper) tLayers.findLayer("SENSOR_PLANNED2");
		assertNotNull(ctw, "found planning leg");
		trackImporter.exportThis(ctw);

		final SegmentList legs = ctw.getSegments();
		assertEquals(6, legs.size(), "correct number of legs");

		final PlanningSegment closer = (PlanningSegment) legs.last();
		assertTrue(closer instanceof PlanningSegment.ClosingSegment, "is closing segment");
	}

}
