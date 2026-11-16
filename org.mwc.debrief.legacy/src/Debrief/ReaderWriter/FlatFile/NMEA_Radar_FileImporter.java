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
package Debrief.ReaderWriter.FlatFile;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class NMEA_Radar_FileImporter {

	static class RadarEntry {
		int trackId;
		double rangeNm;
		double brgDegs;
		public HiResDate dtg;
		public double speedKts;
		public double courseDegs;
	}

	private static FixWrapper generateFix(final WorldLocation origin, final RadarEntry entry) {
		// calculate the new origin
		final WorldVector vector = new WorldVector(Conversions.Degs2Rads(entry.brgDegs), new WorldDistance(entry.rangeNm, WorldDistance.NM), null);
		final WorldLocation newLoc = origin.add(vector);

		final Fix theFix = new Fix(entry.dtg, newLoc, Conversions.Degs2Rads(entry.courseDegs), Conversions.Kts2Yps(entry.speedKts));
		final FixWrapper fix = new FixWrapper(theFix);
		fix.resetName();
		return fix;
	}

	/**
	 * package up the action that adds the data to the layers target
	 *
	 */
	private static class ImportNmeaRadarFileAction implements Action {
		private final List<RadarEntry> _entries;
		private final Layers _layers;
		private final WorldLocation _origin;

		public ImportNmeaRadarFileAction(final List<RadarEntry> track, final WorldLocation origin, final Layers layers) {
			super();
			_origin = origin;
			_entries = track;
			_layers = layers;
		}

		@Override
		public void execute() {
			final List<TrackWrapper> newTracks = new ArrayList<TrackWrapper>();
			for (RadarEntry entry: _entries) {
				// get the parent track
				final Layer layer = _layers.findLayer("" + entry.trackId);
				final TrackWrapper track;
				if(layer == null) {
					track = new TrackWrapper();
					track.setName(""+ entry.trackId);
					track.setColor(DebriefColors.RandomColorProvider.getRandomColor(entry.trackId));
					_layers.addThisLayer(track);
					newTracks.add(track);
				} else {
					track = (TrackWrapper) layer;
				}

				// now generate the fix
				final FixWrapper fix = generateFix(_origin, entry);

				// add to the track
				track.addFix(fix);
			}
			
			// split the tracks
			for (final TrackWrapper track: newTracks) {
				final List<TrackSegment> legs = TrackWrapper_Support.splitTrackAtSpatialJumps(track, 10d);
				// shade each leg differnetly
				if (legs.size() > 1) {
					for (TrackSegment leg: legs) {
						final Color col = DebriefColors.RandomColorProvider.getRandomColor((int)(Math.random() * 100));
						final Enumeration<Editable> ele = leg.elements();
						while(ele.hasMoreElements()) {
							FixWrapper fix = (FixWrapper) ele.nextElement();
							fix.setColor(col);
						}
					}					
				}
			}
			
			// update layers
    		_layers.fireExtended();
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public boolean isUndoable() {
			return false;
		}

		@Override
		public void undo() {
		}
	}

	private static final String RADAR_STR = "$RATTM";

	public static boolean canLoad(final ErrorLogger logger, final BufferedReader r) throws IOException {
		// scan through lines

		// does this contain NMEA radar string
		boolean found = false;
		String nextLine;
		while ((nextLine = r.readLine()) != null && !found) {
			if(nextLine.indexOf(RADAR_STR) != -1)
				found = true;
		}

		return found;
	}

	public static boolean canLoad(final String fileName, final ErrorLogger logger) {
		boolean res = false;
		BufferedReader r = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			r = new BufferedReader(new InputStreamReader(fis));
			res = canLoad(logger, r);
		} catch (final Exception e) {
			logger.logError(ErrorLogger.ERROR, "Trouble whilst checking valid NMEA Radar Log", e);
		} finally {
			try {
				if (r != null)
					r.close();
				if (fis != null)
					fis.close();
			} catch (final IOException e) {
				logger.logError(ErrorLogger.ERROR, "Couldn't close file:" + fileName, e);
			}
		}
		return res;
	}

	public static TrackWrapper findTrack(final TrackWrapper[] allTracks) {
		if (allTracks.length == 1) {
			return allTracks[0];
		}
		int amountOfBlueTracks = 0;
		int indexOfBlueTrack = 0;
		for (int i = 0; i < allTracks.length; i++) {
			if (DebriefColors.BLUE.equals(allTracks[i].getTrackColor())) {
				++amountOfBlueTracks;
				indexOfBlueTrack = i;
			}
		}
		if (amountOfBlueTracks == 1) {
			return allTracks[indexOfBlueTrack];
		}
		return null;
	}

	public Action importThis(final WorldLocation origin, final InputStream is, final Layers layers,
			final ErrorLogger logger) throws Exception {
		final List<RadarEntry> brtData = readRadarData(is, logger);
		if (brtData != null) {
			System.out.println("loaded " + brtData.size() + " rows");			
		}
		return new ImportNmeaRadarFileAction(brtData, origin, layers);
	}

	private static Date getDate(final String item) throws ParseException {
		// note: HH (0-23), not hh (1-12). Two-digit years use the fixed Debrief window
		final SimpleDateFormat dateFormatter = DebriefFormatDateTime
				.applyTwoDigitYearWindow(new GMTDateFormat("yyMMdd_HHmmss"));
		return dateFormatter.parse(item);
	}

	static Optional<RadarEntry> readLine(final String line, final int lineNum) {
		// check if it's our type of message
		if (line.indexOf(RADAR_STR) != -1) {
			// split line
			final String[] entries = line.split(",");
			
			// check it's of correct length
			if (entries.length == 16) {
					final RadarEntry entry = new RadarEntry();
					final String header = entries[0];
					final String[] headers = header.split(":");
					if (headers.length == 2) {
						final String brgIndicator = entries[4];
						if(brgIndicator.equals("T")) {

							try {
							final Date date = getDate(headers[0]);
							final HiResDate dtg = new HiResDate(date.getTime());

							entry.dtg = dtg;
							entry.trackId = Integer.parseInt(entries[1]);
							entry.rangeNm = Double.parseDouble(entries[2]);
							entry.brgDegs = Double.parseDouble(entries[3]);
							final String speedText = entries[5];
							entry.speedKts = speedText.equals("") ? 0 : Double.parseDouble(entries[5]);
							final String courseText = entries[6];
							entry.courseDegs = courseText.equals("") ? 0 : Double.parseDouble(entries[6]);
							
							return Optional.of(entry);
							} catch (ParseException pe) {
								System.out.println("Parse exception:" + pe.getMessage() );
								return Optional.empty();
							}
						} else {
							System.out.println("Can only accept T (True) value for bearing for TTM messages at line:" + lineNum + " got:" + brgIndicator);
							return Optional.empty();
						}
					} else {
						System.out.println("too few headers. Expected 2 got:" + headers.length + " at line " + lineNum);
						return Optional.empty();
					}

			} else {
				System.out.println("NMEA Radar Importer. Expected 16 columns, got " + entries.length + " at line " + lineNum);
				return Optional.empty();
			}
		} else {
			// System.out.println("Not our message type - at line " + lineNum);
			return Optional.empty();
		}

	}

	private List<RadarEntry> readRadarData(final InputStream is, final ErrorLogger logger) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line;

		List<RadarEntry> res = new ArrayList<RadarEntry>();

		int ctr = 0;

		while ((line = reader.readLine()) != null) {
			// ok, generate a position
			try {
				final Optional<RadarEntry> entry = readLine(line, ++ctr);

				if (entry.isPresent()) {
					res.add(entry.get());
				}
			} catch (final Exception e) {
				logger.logError(ErrorLogger.ERROR, "Exception while reading Radar data at line:" + ctr, e);
			}
		}
		return res;
	}
}
