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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class LogTrackLoader extends CoreLoader {

	public static class TestLogImport extends TestCase {
		public void testDateImport() throws ParseException {
			final String testLine1 = "23-08-2014 02136 53,01-01-2014 11 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			final String testLine2 = "23-08-2014 02136 53,01-01-2014 12 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			final String testLine3 = "23-08-2014 02136 53,01-01-2014 13 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

			// check the import, bit by bit
			final LogTrackLoader loader = new LogTrackLoader();

			final Date d1 = loader.dateFor(testLine1.split(",")[1]);
			final Date d2 = loader.dateFor(testLine2.split(",")[1]);
			final Date d3 = loader.dateFor(testLine3.split(",")[1]);

			assertEquals("correct hours", "11:00:12", DateFormat.getTimeInstance().format(d1));
			assertEquals("correct hours", "12:00:12", DateFormat.getTimeInstance().format(d2));
			assertEquals("correct hours", "13:00:12", DateFormat.getTimeInstance().format(d3));
		}

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

			assertEquals("correct hours", "11:00:12", sdf.format(d1));
			assertEquals("correct hours", "12:00:12", sdf.format(d2));
			assertEquals("correct hours", "13:00:12", sdf.format(d3));
		}

		public void testDuffImport() throws ParseException {
			final String testLine1 = "23-08-2014 02136 53,01-09-2014 00 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			final String testLine2 = "23-08-2014 02 36 53,01-09-2014 00 00 12,31-08-2014 23 00 08,48A.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

			// check our understanding
			final String[] blocks = testLine1.split(",");
			assertEquals("correct num", 29, blocks.length);

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
			assertEquals("failed date parse", true, thrown);

			thrown = false;
			try {
				@SuppressWarnings("unused")
				final FixWrapper res = loader.readLine(testLine2);
			} catch (final NumberFormatException pe) {
				thrown = true;
			}
			assertEquals("failed number parse", true, thrown);
		}

		public void testImport() throws ParseException {
			final String testLine = "23-08-2014 02 36 53,01-09-2014 09 13 14,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

			// check our understanding
			final String[] blocks = testLine.split(",");
			assertEquals("correct num", 29, blocks.length);

			// check the import, bit by bit
			final LogTrackLoader loader = new LogTrackLoader();

			// and the date?
			final Date dt = loader.dateFor(blocks[0]);
			assertEquals("correct date", "23-Aug-2014", DateFormat.getDateInstance().format(dt));
			// note, 23rd Aug is summer, so daylight savings time is present in the
			// next value
			assertEquals("correct time", "03:36:53", DateFormat.getTimeInstance().format(dt));
			assertEquals("correct millis", 1408761413000L, dt.getTime());

			final FixWrapper res = loader.readLine(testLine);
			assertEquals("correct lat", "010913.14", FormatRNDateTime.toString(res.getDTG().getDate().getTime()));
			assertEquals("correct lat", 48.5556666, res.getLocation().getLat(), 0.001);
			assertEquals("correct lon", -9.359333, res.getLocation().getLong(), 0.001);
			assertEquals("correct course", 318.8, res.getCourseDegs(), 0.001);
			assertEquals("correct speed", 8.922246, res.getSpeed(), 0.001);
		}

	}

	private static String filename(final String fullPath) { // gets filename without extension
		final int sep = fullPath.lastIndexOf(File.separator);
		final int dot = fullPath.lastIndexOf(".");
		return fullPath.substring(sep + 1, dot);
	}

	public LogTrackLoader() {
		super("Log", ".csv");
	}

	public Date dateFor(final String date) throws ParseException {
		final SimpleDateFormat sdf = new GMTDateFormat("dd-MM-yyyy HH mm ss");
		return sdf.parse(date);
	}

	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers layers,
			final InputStream inputStream, final String fileName) {
		return new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor pm) {
				DebriefPlugin.logError(IStatus.INFO, "about to start loading:" + fileName, null);

				// ok, go for it.
				importThis(layers, fileName, inputStream);
			}
		};

	}

	/**
	 * import data from this stream
	 *
	 * @param theLayers
	 */
	public final void importThis(final Layers theLayers, final String fName, final java.io.InputStream is) {
		// declare linecounter
		int lineCounter = 0;

		TrackWrapper tw = null;

		final Reader reader = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(reader);
		String thisLine = null;

		// check stream is valid
		try {
			if (is.available() > 0) {

				// ok, we know we have a header line, so skip it.
				final String skipHeader = br.readLine();

				// do a quick check that the header looks how we expect
				if (!skipHeader.startsWith("SYS_INTERNAL_TIME,SYS_ORDINAL_TIME")) {
					// CODE RED, CODE RED!
					DebriefPlugin.getDefault().show("File Import", "Sorry this .log file isn't in the correct format",
							IStatus.ERROR);
					return;
				}

				// ok, now the first real line
				thisLine = br.readLine();

				final long start = System.currentTimeMillis();

				// loop through the lines
				while (thisLine != null) {

					// keep line counter (use it for error reporting)
					lineCounter++;

					// catch import problems
					final FixWrapper fw = readLine(thisLine);

					if (fw != null) {
						// ok, add the fix.

						// have we created a parent track yet?
						if (tw == null) {
							String trackName = filename(fName);

							final Layer matchingLayer = theLayers.findLayer(trackName);

							// is this name already in use?
							if (matchingLayer != null) {
								// oops, there's already a track/layer with this name. create
								// another
								trackName = trackName + "_" + (int) (Math.random() * 1000);
							}

							// ok, create the track
							tw = new TrackWrapper();

							// give it the name
							tw.setName(trackName);

							// set the default color
							tw.setColor(DebriefColors.BLUE);

							// store it
							theLayers.addThisLayer(tw);
						}

						// now add the fix
						tw.addFix(fw);
					}

					// read another line
					thisLine = br.readLine();
				}

				final long end = System.currentTimeMillis();
				System.out.print(" |Elapsed:" + (end - start) + " ");

			}
		} catch (final IOException fe) {
			DebriefPlugin.logError(IStatus.INFO, "Trouble creating input stream for " + fName, fe);
		} catch (final ParseException e) {
			DebriefPlugin.logError(IStatus.INFO, "Date parse exception reading line " + lineCounter + " in " + fName,
					e);
		} catch (final NumberFormatException e) {
			DebriefPlugin.logError(IStatus.INFO, "Number format exception reading line " + lineCounter + " in " + fName,
					e);
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				DebriefPlugin.logError(IStatus.INFO, "Buffer close problem:" + fName, e);
			}
			try {
				is.close();
			} catch (final IOException e) {
				DebriefPlugin.logError(IStatus.INFO, "Buffer close problem:" + fName, e);
			}
		}
	}

	private FixWrapper readLine(final String thisLine) throws ParseException {
		// sample line
		// 23-08-2014 02 36 53,01-09-2014 00 00 12,31-08-2014 23 00
		// 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0

		// ok, segment the line
		final String[] blocks = thisLine.split(",");
		final Date date = dateFor(blocks[1]);
		final double lat = Double.parseDouble(blocks[3]);
		final double lon = Double.parseDouble(blocks[4]);
		final double speedKts = Double.parseDouble(blocks[5]);
		final double courseDegs = Double.parseDouble(blocks[6]);

		final Fix theFix = new Fix(new HiResDate(date), new WorldLocation(lat, lon, 0),
				Conversions.Degs2Rads(courseDegs), Conversions.Kts2Yps(speedKts));
		final FixWrapper res = new FixWrapper(theFix);

		// reset the name (to put the time in as a label)
		res.resetName();

		return res;
	}

}
