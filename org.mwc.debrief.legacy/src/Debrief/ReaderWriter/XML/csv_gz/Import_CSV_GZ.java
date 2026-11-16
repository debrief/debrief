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

package Debrief.ReaderWriter.XML.csv_gz;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.SafeArchive;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class Import_CSV_GZ {

	/**
	 * largest permitted uncompressed size of a .csv.gz file (protects against gzip
	 * bombs). The file is streamed line by line, so this is a guard against
	 * runaway input rather than a memory budget. The samples in the repo are tiny
	 * (10 KB) extracts, so doubling them isn't meaningful; real C-Log exports run
	 * to hundreds of MB.
	 */
	public static final long MAX_CSV_GZ_BYTES = 1024L * 1024 * 1024;

	private abstract class Core_Importer {
		/**
		 * map marker for the contents of the 5th column (when relevant)
		 *
		 */
		protected static final String SYSTEM_ID = "SYSTEM_ID";

		/**
		 * keep a central date formatter
		 *
		 */
		private final GMTDateFormat _formatter = new GMTDateFormat(CSV_DATE_FORMAT);

		Long perfStep = null;

		/**
		 * process the lines
		 *
		 * @param inputStream
		 *
		 * @param theLayers   destination for the data
		 * @param hostName    the recording platform
		 * @param logger
		 * @param fileName
		 */
		public final void doImport(final InputStream inputStream, final Layers theLayers, final String hostName,
				final ErrorLogger logger, final String fileName) {
			prepareForImport(theLayers, hostName, logger);

			final InputStreamReader isr = new InputStreamReader(inputStream);
			final BufferedReader br = new BufferedReader(isr);

			try (BufferedReader reader = new BufferedReader(br, 1048576 * 10)) {
				int ctr = 1;
				final Iterable<CSVRecord> tRecords = CSVFormat.RFC4180.parse(reader);
				for (final CSVRecord line : tRecords) {
					try {
						perfLog(ctr);
						processThis(theLayers, hostName, logger, line);
					} catch (final NumberFormatException | ParseException ne) {
						logger.logError(ErrorLogger.ERROR, "Problem at line:" + ctr + " " + ne.getMessage(), ne);
						DialogFactory.showMessage("Import CSV.GZ File",
								"Problem at line:" + ctr + " " + ne.getMessage());
					}
					ctr++;
				}

				// ok, finished processing
				finalise(theLayers);
			} catch (final IOException e) {
				LoggingService.INSTANCE().logError(ErrorLogger.ERROR, "Failed while importing CSV file:" + fileName, e);
			}

		}

		protected void finalise(final Layers theLayers) {

		}

		/**
		 * parse the date
		 *
		 * @param dateStr
		 * @return
		 * @throws ParseException
		 */
		protected Date getDate(final String dateStr) throws ParseException {
			// 20 Nov 2019 - 11:22:33.000
			final Date date = _formatter.parse(dateStr);
			return date;
		}

		/**
		 * wrap the date
		 *
		 * @param dateStr
		 * @return
		 * @throws ParseException
		 */
		protected HiResDate getHiResDate(final String dateStr) throws ParseException {
			return new HiResDate(getDate(dateStr).getTime());
		}

		/**
		 * retrieve the field titles for this processor
		 *
		 * @return
		 */
		abstract public List<String> getMyFields();

		/**
		 * parse the tokens on this line
		 *
		 * @param tokens
		 * @param myFields
		 * @return
		 */
		protected Map<String, String> getTokens(final Iterator<String> tokens, final List<String> myFields) {
			// we know each importer has already run once
			int ctr = 1;

			final Map<String, String> map = new HashMap<String, String>();

			String pendingToken = null;

			while (tokens.hasNext()) {
				// get a trimmed token
				final String token = tokens.next().trim();

				// is this the fifth column?
				if (++ctr == 5) {
					// quietly store it. Some formats rely on the
					// data in the 5th column
					map.put(SYSTEM_ID, token);
				}

				/**
				 * are we waiting for a value?
				 *
				 */
				if (pendingToken != null) {
					// ok, extract the next value
					map.put(pendingToken, token);
					pendingToken = null;
				} else {
					// is this a token we're looking for?
					for (final String thisT : myFields) {
						if (thisT.equals(token)) {
							pendingToken = thisT;
							break;
						}
					}
				}
			}
			return map;
		}

		/**
		 * parse this number
		 *
		 * @param value
		 * @return
		 * @throws ParseException
		 */
		protected Double parseThis(final String value, final String name) throws ParseException {
			final double res;
			if (value == null || value.length() == 0) {
				throw new ParseException("Missing token for " + name, 0);
			} else {
				try {
					res = Double.parseDouble(value);
				} catch (final NumberFormatException ne) {
					throw new NumberFormatException("While parsing " + name + "_" + ne.getMessage());
				}
			}
			return res;
		}

		protected void perfLog(final long ctr) {
			if (ctr != 0) {
				final double log10 = Math.log10(ctr);
				if (log10 == (int) log10) {
					perfStep = ctr;
					System.out.println(ctr);
				} else {
					if (perfStep != null) {
						final double steps = ((double) ctr) / perfStep;
						if (steps == (int) steps) {
							System.out.println(" " + ctr);
						}
					} else {
						System.err.println("Null perfStep encountered in Import_CSV_GZ");
					}
				}
			}
		}

		/**
		 * do any necessary preparation
		 *
		 * @param theLayers
		 * @param hostName
		 * @param logger
		 */
		protected abstract void prepareForImport(Layers theLayers, String hostName, ErrorLogger logger);

		/**
		 * handle this row
		 *
		 * @param theLayers
		 * @param hostName
		 * @param logger
		 * @param record
		 * @throws ParseException
		 */
		protected abstract void processThis(Layers theLayers, String hostName, ErrorLogger logger, CSVRecord record)
				throws ParseException;

		/**
		 * trim this track number
		 *
		 * @param trackStr
		 * @return
		 */
		protected String trimmedTrackNum(final String trackStr) {
			return trackStr.substring(6);
		}
	}

	class OSD_Importer extends Core_Importer {
		private static final String SPEED = "attr_speedOverTheGround";
		private static final String DEPTH = "attr_depth";
		private static final String LAT = "attr_latitude";
		private static final String LONG = "attr_longitude";
		private static final String COURSE = "attr_courseOverTheGround";
		private TrackWrapper _track;

		@Override
		public List<String> getMyFields() {
			final List<String> myTokens = new ArrayList<String>();
			myTokens.add(COURSE);
			myTokens.add(LONG);
			myTokens.add(LAT);
			myTokens.add(DEPTH);
			myTokens.add(SPEED);
			return myTokens;
		}

		@Override
		protected void prepareForImport(final Layers theLayers, final String hostName, final ErrorLogger logger) {
			_track = null;
		}

		public FixWrapper process(final Iterator<String> tokens, final ErrorLogger logger) throws ParseException {
			final String dateStr = tokens.next();
			final HiResDate date = getHiResDate(dateStr);
			final List<String> myFields = getMyFields();

			final Map<String, String> map = getTokens(tokens, myFields);

			final FixWrapper res;
			if (map.size() >= myFields.size()) {
				// create fix
				final WorldLocation loc = new WorldLocation(Math.toDegrees(parseThis(map.get(LAT), LAT)),
						Math.toDegrees(parseThis(map.get(LONG), LONG)), parseThis(map.get(DEPTH), DEPTH));
				final double speed = new WorldSpeed(parseThis(map.get(SPEED), SPEED), WorldSpeed.M_sec)
						.getValueIn(WorldSpeed.ft_sec) / 3d;
				final Fix fix = new Fix(date, loc, parseThis(map.get(COURSE), COURSE), speed);
				res = new FixWrapper(fix);
			} else {
				String missingFields = "";
				for (final String field : myFields) {
					if (!map.keySet().contains(field)) {
						if (missingFields.length() == 0)
							missingFields += field;
						else
							missingFields += ", " + field;
					}
				}

				// ok, insufficient tokens, throw wobbly?
				logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
				res = null;
			}

			return res;
		}

		@Override
		public void processThis(final Layers theLayers, final String hostName, final ErrorLogger logger,
				final CSVRecord record) throws ParseException {
			final FixWrapper nextFix = process(record.iterator(), logger);

			if (nextFix != null) {
				if (_track == null) {
					_track = trackFor(theLayers, hostName);
					// check it's Debrief blue
					_track.setColor(DebriefColors.BLUE);
				}
				_track.addFix(nextFix);
			}
		}
	}

	class Sensor_Importer extends Core_Importer {
		private static final String BEARING = "attr_bearing";
		private static final String TRACK_ID = "attr_trackNumber";

		private TrackWrapper track;
		private Map<String, SensorWrapper> map;

		@Override
		public List<String> getMyFields() {
			final List<String> myTokens = new ArrayList<String>();
			myTokens.add(BEARING);
			myTokens.add(TRACK_ID);
			return myTokens;
		}

		private Map<String, SensorWrapper> getSensors(final TrackWrapper parent) {
			final Map<String, SensorWrapper> res = new HashMap<String, SensorWrapper>();

			final BaseLayer trackSensors = parent.getSensors();
			if (trackSensors != null) {
				final Enumeration<Editable> sensors = trackSensors.elements();
				while (sensors.hasMoreElements()) {
					final SensorWrapper next = (SensorWrapper) sensors.nextElement();
					res.put(next.getName(), next);
				}
			}
			return res;
		}

		@Override
		protected void prepareForImport(final Layers theLayers, final String hostName, final ErrorLogger logger) {
			// get the host track
			final Layer host = theLayers.findLayer(hostName);
			if (host == null || !(host instanceof TrackWrapper)) {
				logger.logStack(ErrorLogger.ERROR, "Can't find host track:" + hostName);
				throw new RuntimeException("Host track not found:" + hostName);
			}

			track = (TrackWrapper) host;
			map = getSensors(track);

		}

		public SensorContactWrapper process(final Iterator<String> tokens, final ErrorLogger logger)
				throws ParseException {
			final String dateStr = tokens.next();
			final HiResDate date = getHiResDate(dateStr);
			final List<String> myFields = getMyFields();

			final Map<String, String> map = getTokens(tokens, myFields);

			final SensorContactWrapper res;
			if (map.size() >= myFields.size() + 1) {
				final Double bearingDegs = Math.toDegrees(parseThis(map.get(BEARING), BEARING));

				final String TRACK_NAME = trimmedTrackNum(map.get(SYSTEM_ID)) + "_" + map.get(TRACK_ID);

				// create sensor
				res = new SensorContactWrapper("PENDING", date, null, bearingDegs, null, null, null,
						LineStylePropertyEditor.SOLID, TRACK_NAME);
			} else {
				String missingFields = "";
				for (final String field : myFields) {
					if (!map.keySet().contains(field)) {
						if (missingFields.length() == 0)
							missingFields += field;
						else
							missingFields += ", " + field;
					}
				}

				// ok, insufficient tokens, throw wobbly?
				logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
				res = null;
			}

			return res;
		}

		@Override
		protected void processThis(final Layers theLayers, final String hostName, final ErrorLogger logger,
				final CSVRecord record) throws ParseException {
			final SensorContactWrapper nextFix = process(record.iterator(), logger);

			final String sensorId = nextFix.getSensorName();

			SensorWrapper sensor = map.get(sensorId);
			if (sensor == null) {
				sensor = new SensorWrapper(sensorId);

				final Color theCol = DebriefColors.RandomColorProvider.getRandomColor(colorCounter++);
				sensor.setColor(theCol);

				track.add(sensor);
				map.put(sensorId, sensor);
			}
			sensor.add(nextFix);
		}
	}

	class State_Importer extends Core_Importer {
		private static final String bearing = "attr_bearing";
		private static final String country = "attr_countryAbbreviation";
		private static final String course = "attr_course";
		private static final String latitude = "attr_latitude";
		private static final String longitude = "attr_longitude";
		private static final String speed = "attr_speed";
		private static final String trackNum = "attr_trackNumber";

		private static final String MY_TRACK_ID = "1";
		private final HashMap<String, LightweightTrackWrapper> _lightTracks;

		public State_Importer() {
			_lightTracks = new HashMap<String, LightweightTrackWrapper>();
		}

		@Override
		protected void finalise(final Layers theLayers) {
			// let parent tidy up
			super.finalise(theLayers);

			final Layer newBase = new BaseLayer();
			newBase.setName(BULK_TRACKS);

			// store the new lgithweight tracks
			for (final LightweightTrackWrapper track : _lightTracks.values()) {
				newBase.add(track);
			}

			theLayers.addThisLayer(newBase);
		}

		@Override
		public List<String> getMyFields() {
			final List<String> myTokens = new ArrayList<String>();
			myTokens.add(bearing);
			myTokens.add(country);
			myTokens.add(course);
			myTokens.add(latitude);
			myTokens.add(longitude);
			myTokens.add(speed);
			myTokens.add(trackNum);
			return myTokens;
		}

		@Override
		protected void prepareForImport(final Layers theLayers, final String hostName, final ErrorLogger logger) {

		}

		public FixWrapper process(final Iterator<String> tokens, final ErrorLogger logger, final String hostname)
				throws ParseException {
			final String dateStr = tokens.next();
			final HiResDate date = getHiResDate(dateStr);
			final List<String> myFields = getMyFields();

			final Map<String, String> map = getTokens(tokens, myFields);

			final FixWrapper res;
			// note: we allow for missing country field
			if (map.size() >= myFields.size() - 1) {
				// create fix
				final Double latVal = parseThis(map.get(latitude), latitude);
				final Double longVal = parseThis(map.get(longitude), longitude);
				if (latVal != 0d && longVal != 0d) {

					final WorldLocation loc = new WorldLocation(Math.toDegrees(latVal), Math.toDegrees(longVal), 0d);
					final double speedVal = new WorldSpeed(parseThis(map.get(speed), speed), WorldSpeed.M_sec)
							.getValueIn(WorldSpeed.ft_sec) / 3d;
					final Fix fix = new Fix(date, loc, parseThis(map.get(course), course), speedVal);
					res = new FixWrapper(fix);

					// store the track name
					final String trackName;
					final String trackStr = map.get(trackNum);
					if (trackStr != null && trackStr.equals(MY_TRACK_ID)) {
						trackName = hostname;
					} else {
						final String countryStr = map.get(country);
						String workingName = trackStr;
						if (countryStr != null && countryStr.length() > 0) {
							workingName += "_" + countryStr;
						}
						trackName = workingName;
					}

					res.setComment(trackName);

				} else {
					res = null;
				}
			} else {
				String missingFields = "";
				for (final String field : myFields) {
					if (!map.keySet().contains(field)) {
						if (missingFields.length() == 0)
							missingFields += field;
						else
							missingFields += ", " + field;
					}
				}

				// ok, insufficient tokens, throw wobbly?
				logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
				res = null;
			}

			return res;
		}

		@Override
		protected void processThis(final Layers theLayers, final String hostName, final ErrorLogger logger,
				final CSVRecord record) throws ParseException {
			final FixWrapper nextFix = process(record.iterator(), logger, hostName);

			if (nextFix != null) {
				// reset name
				nextFix.resetName();

				final String trackId = nextFix.getComment();
				final String trackName;
				final boolean isOwnship = trackId.equals(hostName);
				final boolean isOfInterest = trackId.contains("_");

				LightweightTrackWrapper thisTrack;
				if (isOwnship || isOfInterest) {
					trackName = isOwnship ? hostName : trackId;
					thisTrack = (LightweightTrackWrapper) theLayers.findLayer(trackName);
					if (thisTrack == null) {
						final Color trackColor = isOwnship ? DebriefColors.BLUE : DebriefColors.RED;
						thisTrack = new TrackWrapper();
						thisTrack.setName(trackName);
						theLayers.addThisLayer(thisTrack);
						thisTrack.setColor(trackColor);
					}
				} else {
					trackName = trackId;

					// see if we have it already
					thisTrack = _lightTracks.get(trackName);
					if (thisTrack == null) {
						thisTrack = new LightweightTrackWrapper(trackName, true, true, DebriefColors.RED,
								LineStylePropertyEditor.SOLID);
						thisTrack.setColor(DebriefColors.GREEN);
						_lightTracks.put(trackName, thisTrack);
					}
				}

				thisTrack.add(nextFix);
			}
		}
	}

	static final String BULK_TRACKS = "Bulk Tracks";

	static final String CSV_DATE_FORMAT = "dd MMM yyyy - HH:mm:ss.SSS";

	static String getTrackPrefix(final String fullPath) {
		// note: we have to create a fictional file
		// in order to convert the above path to a URI
		// On MS-Windows machines we get a file path,
		// not a URI, and the `Paths.get()` method throws
		// a wobbly
		final File dummy = new File(fullPath);
		final Path path = Paths.get(dummy.toURI());

		// call getFileName() and get FileName path object
		final Path fileName = path.getFileName();
		final String gName = fileName.toString();
		final int index = gName.indexOf("_");
		return gName.substring(0, index);
	}

	/**
	 * keep track of how many tracks we've created, so we can generate unique colors
	 */
	private int colorCounter = 0;

	private void doImport(final Layers theLayers, final InputStream inputStream, final String fileName,
			final ErrorLogger logger) {
		final String trackName = getTrackPrefix(fileName);

		// find out which type it is
		final Core_Importer importer = importerFor(fileName);

		if (importer == null) {
			logger.logStack(ErrorLogger.ERROR, "Cant find importer for " + fileName);
		} else {
			// go for it
			importer.doImport(inputStream, theLayers, trackName, logger, fileName);
		}
	}

	public void doZipImport(final Layers theLayers, final InputStream inputStream, final String fileName,
			final ErrorLogger logger) {
		doZipImport(theLayers, inputStream, fileName, logger, MAX_CSV_GZ_BYTES);
	}

	void doZipImport(final Layers theLayers, final InputStream inputStream, final String fileName,
			final ErrorLogger logger, final long maxBytes) {
		// stream the uncompressed data straight into the (line by line) importer,
		// with a cap on how much we'll inflate (gzip bomb)
		try (GZIPInputStream in = new GZIPInputStream(new BufferedInputStream(inputStream, 65536))) {
			doImport(theLayers, SafeArchive.limit(in, maxBytes, fileName), fileName, logger);
		} catch (final IOException e) {
			reportZipFailure(fileName, logger, e);
		} catch (final RuntimeException re) {
			// the CSV parser wraps read errors in runtime exceptions
			Throwable cause = re;
			while (cause != null && !(cause instanceof SafeArchive.ArchiveException)) {
				cause = cause.getCause();
			}
			if (cause == null) {
				throw re;
			}
			reportZipFailure(fileName, logger, (SafeArchive.ArchiveException) cause);
		}
	}

	private static void reportZipFailure(final String fileName, final ErrorLogger logger, final IOException e) {
		final String msg = "Failed to import " + fileName + ": " + e.getMessage();
		logger.logError(ErrorLogger.ERROR, msg, e);
		DialogFactory.showMessage("Import CSV.GZ File", msg);
	}

	private Core_Importer importerFor(final String filename) {
		if (filename.indexOf("_OSD_") != -1) {
			return new OSD_Importer();
		} else if (filename.contains("_SystemTrack")) {
			return new State_Importer();
		} else if (filename.contains("SensorTrack")) {
			return new Sensor_Importer();
		} else
			return null;
	}

	private TrackWrapper trackFor(final Layers layers, final String trackName) {
		final TrackWrapper track;
		final Layer layer = layers.findLayer(trackName);
		if (layer != null && layer instanceof TrackWrapper) {
			track = (TrackWrapper) layer;
		} else {
			final boolean needsRename;
			if (layer == null) {
				needsRename = false;
			} else {
				needsRename = true;
			}

			final String nameToUse;
			if (needsRename) {
				final String suffix = "-" + (int) Math.random() * 1000;
				nameToUse = trackName + suffix;
			} else {
				nameToUse = trackName;
			}

			track = new TrackWrapper();
			track.setName(nameToUse);

			// sort out a color
			final Color theCol = DebriefColors.RandomColorProvider.getRandomColor(colorCounter++);
			track.setColor(theCol);

			layers.addThisLayer(track);
		}

		return track;
	}
}
