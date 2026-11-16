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

package MWC.GUI.ETOPO;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.ETOPO.ETOPO_2_Minute.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import MWC.GUI.ETOPO.ETOPO_2_Minute.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Properties.LineWidthPropertyEditor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

public final class ETOPO_2_MinuteTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	public String _etopoPath;

	public ETOPO_2_MinuteTest() {

		final String pathFromProp = System.getProperty("etopoDir");
		if (pathFromProp == null) {
			// are we in OS?
			final String sys = System.getProperty("os.name");
			if ("Mac OS X".equals(sys))
				_etopoPath = "../deploy/";
			else
				_etopoPath = "C:\\Program Files\\Debrief 2003\\etopo";
		} else
			_etopoPath = pathFromProp;

	}

	// TODO FIX-TEST
	// check data
	public void NtestFindData() {

		// String thefile = THE_PATH;
		assertTrue(ETOPO_2_Minute.dataFileExists(_etopoPath), "Failed to find the 2 minute dataset:" + _etopoPath);
	}

	@Test
	public void testConversions() {
		final ETOPO_2_Minute e2m = new ETOPO_2_Minute(_etopoPath);

		WorldLocation loc = new WorldLocation(54, -3, 0);
		int lat = e2m.getLatIndex(loc);
		int lon = e2m.getLongIndex(loc);
		double dLat = ETOPO_2_Minute.getLatitudeFor(lat);
		double dLong = ETOPO_2_Minute.getLongitudeFor(lon);
		WorldLocation loc2 = new WorldLocation(dLat, dLong, 0);

		System.out.println("dist:" + loc2.subtract(loc).getRange());
		assertTrue(loc2.subtract(loc).getRange() < 1, "points close enough, original: " + loc + " res:" + loc2);

		loc = new WorldLocation(-54, -3, 0);
		lat = e2m.getLatIndex(loc);
		lon = e2m.getLongIndex(loc);
		dLat = ETOPO_2_Minute.getLatitudeFor(lat);
		dLong = ETOPO_2_Minute.getLongitudeFor(lon);
		loc2 = new WorldLocation(dLat, dLong, 0);
		assertTrue(loc2.subtract(loc).getRange() < 1, "points close enough, original: " + loc + " res:" + loc2);

		loc = new WorldLocation(-54, 3, 0);
		lat = e2m.getLatIndex(loc);
		lon = e2m.getLongIndex(loc);
		dLat = ETOPO_2_Minute.getLatitudeFor(lat);
		dLong = ETOPO_2_Minute.getLongitudeFor(lon);
		loc2 = new WorldLocation(dLat, dLong, 0);
		assertTrue(loc2.subtract(loc).getRange() < 1, "points close enough, original: " + loc + " res:" + loc2);

		loc = new WorldLocation(54, 3, 0);
		lat = e2m.getLatIndex(loc);
		lon = e2m.getLongIndex(loc);
		dLat = ETOPO_2_Minute.getLatitudeFor(lat);
		dLong = ETOPO_2_Minute.getLongitudeFor(lon);
		loc2 = new WorldLocation(dLat, dLong, 0);
		assertTrue(loc2.subtract(loc).getRange() < 1, "points close enough, original: " + loc + " res:" + loc2);

		// assertEquals(loc, loc2);
	}

	@Test
	public final void testMyParams() {
		ETOPO_2_Minute ed = new ETOPO_2_Minute(null);
		ed.setName("blank");
		editableTesterSupport.testParams(ed);
		ed = null;
	}
}
