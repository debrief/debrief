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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

public class CLogFileImporter {

	public interface CLog_Helper {
		final String CANCEL_STRING = "<Pending>";

		String getTrackName();
	}

	/**
	 * package up the action that adds the data to the layers target
	 *
	 */
	private static class ImportCLogFileAction implements Action {
		private final List<FixWrapper> _track;
		private final Layers _layers;
		private final String _name;
		private boolean _trackCreated = false;

		public ImportCLogFileAction(final List<FixWrapper> track, final String name, final Layers layers) {
			super();

			_name = name;
			_track = track;
			_layers = layers;
		}

		@Override
		public void execute() {
			// see of we this track already exists
			final Layer layer = _layers.findLayer(_name);
			final TrackWrapper track;
			if (layer != null) {
				if (layer instanceof TrackWrapper) {
					track = (TrackWrapper) layer;
				} else {
					DialogFactory.showMessage("Import Log File",
							"Can't use this track name, it clashes with existing non-track layer");
					track = null;
				}
			} else {
				track = new TrackWrapper();
				track.setName(_name);
				track.setColor(DebriefColors.BLUE);
				_trackCreated = true;
				_layers.addThisLayer(track);
			}
			if (track != null) {
				for (final FixWrapper fix : _track) {
					track.addFix(fix);
				}
			}
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public boolean isUndoable() {
			return true;
		}

		@Override
		public void undo() {
			// see of we this track already exists
			final Layer layer = _layers.findLayer(_name);
			if (layer != null) {
				if (layer instanceof TrackWrapper) {
					final TrackWrapper track = (TrackWrapper) layer;
					for (final FixWrapper fix : _track) {
						track.removeFix(fix);
					}
					// also remove the track, if necessary
					if (_trackCreated) {
						_layers.removeThisLayer(track);
					}
				} else {
					DialogFactory.showMessage("Undo Import Log File", "Can't find track containing new fixes");
				}

			}
		}
	}

	private static final String HEADER_STR = "Unknown";

	static boolean _logErrors = false;

	public static boolean canLoad(final ErrorLogger logger, final BufferedReader r) throws IOException {
		// try this first line
		final String line = r.readLine();

		final boolean res = (line != null && line.startsWith(HEADER_STR));

		if (!res) {
			if (_logErrors) {
				logger.logError(ErrorLogger.INFO, "CLog Import rejecting file, Header:" + res, null);
			}
		}
		return res;
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
			logger.logError(ErrorLogger.ERROR, "Trouble whilst checking valid CLog", e);
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

	private static double courseFor(final String courseRadsStr, final ErrorLogger logger) {
		return Double.parseDouble(courseRadsStr);
	}

	private static long dateFor(final String timeStr, final ErrorLogger logger) {
		final long nanos = Long.parseLong(timeStr);
		return nanos / 1000000;
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

	private static WorldLocation locationFrom(final String latStr, final String longStr, final String depthStr,
			final ErrorLogger logger) {
		final double latRads = Double.parseDouble(latStr);
		final double longRads = Double.parseDouble(longStr);
		final double depthM = Double.parseDouble(depthStr);
		return new WorldLocation(Math.toDegrees(latRads), Math.toDegrees(longRads), depthM);
	}

	/**
	 *
	 * @param logger      error logger
	 * @param line        line of text to process
	 * @param nextTimeDue time the next item is due
	 * @param ctr
	 * @return
	 */
	private static FixWrapper produceFix(final ErrorLogger logger, final String line, final Long nextTimeDue,
			final int lineCtr) {
		// ok, tokenize the line
		final String[] tokens = line.split("\\s+");

		if (tokens.length != 17) {
			logger.logError(ErrorLogger.ERROR,
					"Expecting 17 tokens in CLog format at line:" + lineCtr + ". Found:" + tokens.length, null);
		}

		// sort out the date first
		final long timeStamp = dateFor(tokens[16], logger);
		final FixWrapper res;
		if (nextTimeDue == null || timeStamp >= nextTimeDue) {
			final WorldLocation loc = locationFrom(tokens[11], tokens[12], tokens[13], logger);
			final double courseRads = courseFor(tokens[9], logger);
			final double speedYps = speedFor(tokens[10], logger);
			final HiResDate date = new HiResDate(timeStamp);
			final Fix fix = new Fix(date, loc, courseRads, speedYps);
			res = new FixWrapper(fix);
			res.resetName();
		} else {
			res = null;
		}
		return res;
	}

	private static double speedFor(final String line, final ErrorLogger logger) {
		final Double speedMs = Double.parseDouble(line);
		return new WorldSpeed(speedMs, WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec) / 3;
	}

	/**
	 * whether the data-file should be resampled to 1Hz on import
	 *
	 */
	private boolean _resample = true;

	public Action importThis(final CLog_Helper helper, final InputStream is, final Layers layers,
			final ErrorLogger logger) throws Exception {
		final List<FixWrapper> brtData = readCLogData(is, logger);
		final String trackName = helper.getTrackName();
		if (CLog_Helper.CANCEL_STRING.equals(trackName)) {
			// clear the fixes, to cancel the import
			brtData.clear();
		}
		return new ImportCLogFileAction(brtData, trackName, layers);
	}

	private List<FixWrapper> readCLogData(final InputStream is, final ErrorLogger logger) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line;

		List<FixWrapper> res = null;

		// skip 2 header rows
		reader.readLine();
		reader.readLine();

		Long nextTimeDue = null;
		final long timeDelta = 1000; // 1 second
		int ctr = 2;

		while ((line = reader.readLine()) != null) {
			// ok, generate a position
			try {
				final FixWrapper wrapped = produceFix(logger, line, nextTimeDue, ctr);
				if (wrapped != null) {
					if (res == null) {
						res = new ArrayList<FixWrapper>();
					}

					final long thisT = wrapped.getDTG().getDate().getTime();

					// are we resample?
					if (_resample) {
						nextTimeDue = thisT + timeDelta;
					}

					// do we want to add this one?
					res.add(wrapped);
				}
			} catch (final Exception e) {
				logger.logError(ErrorLogger.ERROR, "Exception while reading CLog data at line:" + ctr, e);
			}
			ctr++;
		}
		return res;
	}

	protected void setResampleOnImport(final boolean doResample) {
		_resample = doResample;
	}
}
