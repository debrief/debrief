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

package org.mwc.debrief.core.loaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.core.loaders.UK_CSV_Loader.*;
import org.mwc.debrief.core.loaders.UK_CSV_Loader.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

public final class UK_CSV_LoaderTest {

	boolean tripped = false;

	@Test
	public void testGetName() throws ParseException {
		final String line = "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n";
		final String name = UK_CSV_Loader.getName(line);
		assertEquals("NELSON", name, "Correct name");
	}

	@Test
	public void testImport() throws ParseException {

		final String testInput = "# UK TRACK EXCHANGE FORMAT, V1.0\r\n"
				+ "# Lat,Long,DTG,UnitName,CaseNumber,Type,Flag,Sensor,MajorAxis,SemiMajorAxis,SemiMinorAxis,Course,Speed,Depth,Likelihood,Confidence,SuppliedBy,Provenance,InfoCutoffDate,Purpose,Classification,DistributionStatement\r\n"
				+ "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n"
				+ "22.1862722, -21.7008278,19951212T050100Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long more content. Quite a more content.\"\r\n"
				+ "22.1862528, -21.7041139,19951212T050200Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.9000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long sentuite a cate to get more content.\"\r\n"
				+ "22.1862528, -21.707575,19951212T050300Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,268.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long senttent. Quite a long sentence decontent.\"";

		final InputStream stream = new java.io.ByteArrayInputStream(testInput.getBytes(Charset.forName("UTF-8")));

		final Layers layers = new Layers();

		assertEquals(0, layers.size(), "layers empty");

		UK_CSV_Loader.importThis(layers, "test_file.csv", stream);

		assertEquals(1, layers.size(), "layers not empty");

		final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON");
		assertNotNull(track, "found track");

		assertEquals(4, track.numFixes(), "all points");
	}

	@Test
	public void testSingleLine() throws ParseException {
		final String line = "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n";
		final FixWrapper fix = UK_CSV_Loader.readLine(line);
		assertNotNull(fix);

		// test the params
		assertEquals(22.1862861, fix.getLocation().getLat(), "lat");
		assertEquals(-21.6978806, fix.getLocation().getLong(), "lon");
		assertEquals(0d, fix.getLocation().getDepth(), "depth");
		assertEquals(818744400000L, fix.getDTG().getDate().getTime(), "date");
		assertEquals(269.7, fix.getCourseDegs(), "course");
		assertEquals(new WorldSpeed(2, WorldSpeed.M_sec).getValueIn(WorldSpeed.Kts), fix.getSpeed(), 0.001,
				"speed");
	}

	@Test
	public void testSingleLineBad1() throws ParseException {
		final String line = "22.18a62861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n";

		FixWrapper fix = null;
		try {
			fix = UK_CSV_Loader.readLine(line);
		} catch (final NumberFormatException e) {
			tripped = true;
		}
		assertNull(fix, "didn't manage to produce fix");
		assertTrue(tripped, "parse exception thrown");
	}

	@Test
	public void testSingleLineBad2() throws ParseException {
		final String line = "22.1862861, -21.6978806,19951212T050000Z";

		tripped = false;
		FixWrapper fix = null;
		try {
			fix = UK_CSV_Loader.readLine(line);
		} catch (final ArrayIndexOutOfBoundsException e) {
			tripped = true;
		}
		assertNull(fix, "didn't manage to produce fix");
		assertTrue(tripped, "parse exception thrown");
	}
}
