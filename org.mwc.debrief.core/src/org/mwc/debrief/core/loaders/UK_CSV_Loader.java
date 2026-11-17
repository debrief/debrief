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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

/**
 * @author ian.mayo
 */
public class UK_CSV_Loader extends CoreLoader {

	private static TrackWrapper createParentTrack(final Layers theLayers, final String thisLine) throws ParseException {
		TrackWrapper tw;
		String trackName = getName(thisLine);

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
		return tw;
	}

	private static Date dateFor(final String date) throws ParseException {
		final DateFormat sdf = new GMTDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
		return sdf.parse(date);
	}

	static String getName(final String thisLine) throws ParseException {
		// ok, segment the line
		final String[] blocks = thisLine.split(",");
		final String name = blocks[3];
		return name;
	}

	/**
	 * import data from this stream
	 *
	 * @param theLayers
	 */
	final static void importThis(final Layers theLayers, final String fName, final java.io.InputStream is) {
		// declare linecounter
		int lineCounter = 0;
		TrackWrapper tw = null;
		final Reader reader = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(reader);
		try {
			if (is.available() > 0) {

				// ok, we know we have a header line, so skip it.
				br.readLine();
				br.readLine();

				final long start = System.currentTimeMillis();

				// loop through the lines
				while (br.read() != -1) {
					final String thisLine = br.readLine();

					// keep line counter (use it for error reporting)
					lineCounter++;

					// catch import problems
					final FixWrapper fw = readLine(thisLine);

					if (fw != null) {
						// ok, add the fix.

						// have we created a parent track yet?
						if (tw == null) {
							// sort out the track
							tw = createParentTrack(theLayers, thisLine);

							// store it
							theLayers.addThisLayer(tw);
						}

						// now add the fix
						tw.addFix(fw);
					}
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
			CorePlugin.showMessage("Import CSV", "Incorrectly formatter number at line " + lineCounter);
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

	static FixWrapper readLine(final String thisLine) throws ParseException, NumberFormatException {
		// sample line
		// final String line = "22.1862861,
		// -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT
		// ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s. I'll 'll
		// duplicate to get more
		// content.\"\r\n";

		// ok, segment the line
		final String[] blocks = thisLine.split(",");
		final Date date = dateFor(blocks[2]);
		final double lat = Double.parseDouble(blocks[0]);
		final double lon = Double.parseDouble(blocks[1]);
		final double courseDegs = Double.parseDouble(blocks[11]);
		final double speedM_Sec = Double.parseDouble(blocks[12]);
		final WorldSpeed speed = new WorldSpeed(speedM_Sec, WorldSpeed.M_sec);

		final Fix theFix = new Fix(new HiResDate(date), new WorldLocation(lat, lon, 0),
				Conversions.Degs2Rads(courseDegs), speed.getValueIn(WorldSpeed.ft_sec) / 3d);
		final FixWrapper res = new FixWrapper(theFix);

		// reset the name (to put the time in as a label)
		res.resetName();

		return res;
	}

	public UK_CSV_Loader() {
		super("CSV Track format", "csv");
	}

	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers theLayers,
			final InputStream inputStream, final String fileName) {
		return new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor pm) {
				try {
					// ok, go for it.
					importThis(theLayers, fileName, inputStream);
				} catch (final RuntimeException e) {
					CorePlugin.showMessage("Import CSV Track file",
							"Failed to read:" + fileName + ". Please see error log.");
					DebriefPlugin.logError(IStatus.ERROR, "Problem loading log:" + fileName, e);
				}
			}
		};
	}

}
