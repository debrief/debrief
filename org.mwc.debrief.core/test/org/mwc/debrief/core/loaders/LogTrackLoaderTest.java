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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.core.loaders.LogTrackLoader.*;
import org.mwc.debrief.core.loaders.LogTrackLoader.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class LogTrackLoaderTest {
	@Test
	public void testDateImport() throws ParseException {
		final String testLine1 = "23-08-2014 02136 53,01-01-2014 11 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
		final String testLine2 = "23-08-2014 02136 53,01-01-2014 12 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
		final String testLine3 = "23-08-2014 02136 53,01-01-2014 13 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

		// check the import, bit by bit
		final LogTrackLoader loader = new LogTrackLoader();

		final Date d1 = loader.dateFor(testLine1.split(",")[1]);
		final Date d2 = loader.dateFor(testLine2.split(",")[1]);
		final Date d3 = loader.dateFor(testLine3.split(",")[1]);

		assertEquals("11:00:12", new GMTDateFormat("HH:mm:ss", Locale.UK).format(d1), "correct hours");
		assertEquals("12:00:12", new GMTDateFormat("HH:mm:ss", Locale.UK).format(d2), "correct hours");
		assertEquals("13:00:12", new GMTDateFormat("HH:mm:ss", Locale.UK).format(d3), "correct hours");
	}

	@Test
	public void testDaylightSavings() throws ParseException {
		final String testLine1 = "23-08-2014 02136 53,01-09-2014 11 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
		final String testLine2 = "23-08-2014 02136 53,01-09-2014 12 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
		final String testLine3 = "23-08-2014 02136 53,01-09-2014 13 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

		// check the import, bit by bit
		final LogTrackLoader loader = new LogTrackLoader();

		final Date d1 = loader.dateFor(testLine1.split(",")[1]);
		final Date d2 = loader.dateFor(testLine2.split(",")[1]);
		final Date d3 = loader.dateFor(testLine3.split(",")[1]);

		final SimpleDateFormat sdf = new GMTDateFormat("HH:mm:ss");

		assertEquals("11:00:12", sdf.format(d1), "correct hours");
		assertEquals("12:00:12", sdf.format(d2), "correct hours");
		assertEquals("13:00:12", sdf.format(d3), "correct hours");
	}

	@Test
	public void testDuffImport() throws ParseException {
		final String testLine1 = "23-08-2014 02136 53,01-09-2014 00 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
		final String testLine2 = "23-08-2014 02 36 53,01-09-2014 00 00 12,31-08-2014 23 00 08,48A.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

		// check our understanding
		final String[] blocks = testLine1.split(",");
		assertEquals(29, blocks.length, "correct num");

		// check the import, bit by bit
		final LogTrackLoader loader = new LogTrackLoader();

		// and the date?
		boolean thrown = false;
		try {
			@SuppressWarnings("unused")
			final Date dt = loader.dateFor(blocks[0]);
		} catch (final ParseException pe) {
			thrown = true;
		}
		assertEquals(true, thrown, "failed date parse");

		thrown = false;
		try {
			@SuppressWarnings("unused")
			final FixWrapper res = loader.readLine(testLine2);
		} catch (final NumberFormatException pe) {
			thrown = true;
		}
		assertEquals(true, thrown, "failed number parse");
	}

	@Test
	public void testImport() throws ParseException {
		final String testLine = "23-08-2014 02 36 53,01-09-2014 09 13 14,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

		// check our understanding
		final String[] blocks = testLine.split(",");
		assertEquals(29, blocks.length, "correct num");

		// check the import, bit by bit
		final LogTrackLoader loader = new LogTrackLoader();

		// and the date?
		final Date dt = loader.dateFor(blocks[0]);
		assertEquals("23-Aug-2014", new GMTDateFormat("dd-MMM-yyyy", Locale.UK).format(dt), "correct date");
		// the log is in GMT (formatting with the JVM's zone and locale made this
		// pass only on a UK machine, where August's BST showed 03:36:53)
		assertEquals("02:36:53", new GMTDateFormat("HH:mm:ss", Locale.UK).format(dt), "correct time");
		assertEquals(1408761413000L, dt.getTime(), "correct millis");

		final FixWrapper res = loader.readLine(testLine);
		assertEquals("010913.14", FormatRNDateTime.toString(res.getDTG().getDate().getTime()), "correct lat");
		assertEquals(48.5556666, res.getLocation().getLat(), 0.001, "correct lat");
		assertEquals(-9.359333, res.getLocation().getLong(), 0.001, "correct lon");
		assertEquals(318.8, res.getCourseDegs(), 0.001, "correct course");
		assertEquals(8.922246, res.getSpeed(), 0.001, "correct speed");
	}

}
