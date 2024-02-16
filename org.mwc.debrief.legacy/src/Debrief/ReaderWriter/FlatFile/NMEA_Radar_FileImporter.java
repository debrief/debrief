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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public class NMEA_Radar_FileImporter {
	
	private static class RadarEntry {
		int trackId;
		double rangeNm;
		double brgDegs;
		public HiResDate dtg;
		public double speedKts;
		public double courseDegs;
	}
	
	public static class RadarImporter_Test extends TestCase {
		static class Logger implements ErrorLogger {
			private final List<String> messages = new ArrayList<String>();

			private final boolean console = true;

			private void clear() {
				messages.clear();
			}

			public boolean isEmpty() {
				return messages.isEmpty();
			}

			@Override
			public void logError(final int status, final String text, final Exception e) {
				output(text, e);
			}

			@Override
			public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
				output(text, e);
			}

			@Override
			public void logStack(final int status, final String text) {
				output(text, null);
			}

			public void output(final String text, final Exception e) {
				messages.add(text);
				if (console) {
					System.out.println(text);
					if (e != null) {
						e.printStackTrace();
					}
				}
			}

			public void setUp() {
			}
		}

		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		
		static private WorldLocation _origin = new WorldLocation(54.5, -7.6, 0);

		private static void perfLog(final long ctr) {
			final double log10 = Math.log10(ctr);
			if (log10 == (int) log10) {
				System.out.println(ctr);
			}
		}

		private final Logger _logger = new Logger();

		private final String ownship_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/CLog_Trial.txt";

		private FixWrapper createF(final long time) {
			final WorldLocation loc = new WorldLocation(2, 2, 2);
			final Fix newF = new Fix(new HiResDate(time), loc, 0, 0);
			final FixWrapper fw = new FixWrapper(newF);
			return fw;
		}

		public void noTtestExport() throws IOException {
			final String ownship_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
			final Layers tLayers = new Layers();

			// start off with the ownship track
			final File boatFile = new File(ownship_track);
			assertTrue(boatFile.exists());
			final InputStream bs = new FileInputStream(boatFile);

			final ImportReplay trackImporter = new ImportReplay();
			ImportReplay.initialise(new ImportReplay.testImport.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
			trackImporter.importThis(ownship_track, bs, tLayers);

			assertEquals("read in track", 1, tLayers.size());

			// ok, now export in the new format
			final FileWriter fw = new FileWriter("CLog_Trial.txt");
			fw.write("Unknown blah blah blah\n");
			fw.write("Blah blah blah blah\n");

			// and now the positions
			int ctr = 0;
			final TrackWrapper track = (TrackWrapper) tLayers.findLayer("Nelson");
			track.setInterpolatePoints(true);

			final long milli_Step = 2;
			final long micro_Step = milli_Step * 1000;
			for (long tNow = track.getStartDTG().getMicros(); tNow < 818746200000000L; tNow += micro_Step) {
				final Watchable[] newF = track.getNearestTo(new HiResDate(0, tNow));
				final FixWrapper fix = (FixWrapper) newF[0];
				final String asLog = toLogFile(fix);
				fw.write(asLog);
				perfLog(ctr++);
			}

			fw.close();
		}

		@Override
		public void setUp() {
			_logger.clear();
		}

		public void testCanLoad() throws Exception {

			final String initialString = "700101_010601:$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";
			
			final Reader inputString = new StringReader(initialString);
			final BufferedReader reader = new BufferedReader(inputString);

			assertTrue(canLoad(_logger, reader));
		}

		public void testParseLine() throws Exception {

			final String initialString = "700101_010601:$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";
			final Optional<RadarEntry> entry = readLine(initialString, 5);
			assertTrue(entry.isPresent());
		}

		public void testInvalidDate() throws Exception {
			final String initialString = "700101010601:$RATTM,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";
			final Optional<RadarEntry> entry = readLine(initialString, 5);
			assertFalse(entry.isPresent());
		}

		public void testWrongNumColumns() throws Exception {
			final String initialString = "700101_010601:$RATTM,002,0.665,224.7,17,31.2,R,0.16,99.99,N,,T,,,A*2A";
			final Optional<RadarEntry> entry = readLine(initialString, 5);
			assertFalse(entry.isPresent());
		}

		public void testCannotLoad() throws Exception {

			final String initialString = "700101_010601:$OTTER,002,0.665,224.7,T,0.17,31.2,R,0.16,99.99,N,,T,,,A*2A";

			final Reader inputString = new StringReader(initialString);
			final BufferedReader reader = new BufferedReader(inputString);

			assertFalse(canLoad(_logger, reader));
		}

		public void testGoodLoad() throws Exception {
			final NMEA_Radar_FileImporter importer = new NMEA_Radar_FileImporter();

			final Layers layers = new Layers();

			assertTrue("input file exists", new File(ownship_track).exists());

			assertEquals("empty before", 0, layers.size());

			final InputStream is = new FileInputStream(ownship_track);
			final Action action = importer.importThis(_origin, is, layers, _logger);
			action.execute();

			assertEquals("has data", 1, layers.size());

			final TrackWrapper track = (TrackWrapper) layers.elementAt(0);

			assertEquals("correct fixes", 6227, track.numFixes());

			// and undo it
			action.undo();

			assertEquals("has data", 0, layers.size());
		}

		public void testGoodLoadResample() throws Exception {
			final Layers layers = new Layers();

			assertTrue("input file exists", new File(ownship_track).exists());

			assertEquals("empty before", 0, layers.size());

			final InputStream is = new FileInputStream(ownship_track);

			final NMEA_Radar_FileImporter importer = new NMEA_Radar_FileImporter();

			final Action action = importer.importThis(_origin, is, layers, _logger);
			action.execute();

			assertEquals("has data", 1, layers.size());

			final TrackWrapper track = (TrackWrapper) layers.elementAt(0);

			assertEquals("correct fixes", 623, track.numFixes());

			// and undo it
			action.undo();

			assertEquals("has data", 0, layers.size());
		}

		public void testMangledText() throws Exception {

			final String initialString = "Unknown blah blah blah\n" + "Blah blah blah blah\n"
					+ "blah blah blah blah blah blah blah blah blah 4.707152992628706 1.028888888888889 BANANA -0.37869945639890085 0.0 blah blah 818744400000000000 \n"
					+ "blah blah blah blah blah blah blah blah blah 4.707152992628706 1.028888888888889 0.38722349902153685 -0.37875089513046656 0.0 blah blah 818744460000000000 \n"
					+ "blah blah blah blah blah blah blah blah blah 4.710643651132695  1.028888888888889 0.38722315965196 -0.3788082485889418 0.0 blah blah 818744520000000000 \n";
			final NMEA_Radar_FileImporter importer = new NMEA_Radar_FileImporter();
			final Layers layers = new Layers();


			assertTrue("logger empty", _logger.isEmpty());

			final InputStream is = new ByteArrayInputStream(initialString.getBytes());
			final Action action = importer.importThis(_origin, is, layers, _logger);
			action.execute();

			assertEquals("has data", 1, layers.size());
			assertFalse("logger not empty", _logger.isEmpty());

			assertEquals("correct logging message", "Exception while reading CLog data at line:2",
					_logger.messages.get(0));

			// check other data still got loaded
			final TrackWrapper track = (TrackWrapper) layers.findLayer("Dave");
			assertEquals("loaded other posits", 2, track.numFixes());
		}

		public void testTooFewTokens() throws Exception {

			final String initialString = "Unknown blah blah blah\n" + "Blah blah blah blah\n"
					+ "blah blah blah blah blah blah blah blah blah 4.707152992628706 1.028888888888889 0.3872237414283774 -0.37869945639890085 0.0 blah blah 818744400000000000 \n"
					+ "blah blah blah blah blah blah blah blah blah 4.707152992628706 1.028888888888889 0.38722349902153685 -0.37875089513046656 0.0 818744460000000000 \n"
					+ "blah blah blah blah blah blah blah blah blah 4.710643651132695  1.028888888888889 0.38722315965196 -0.3788082485889418 0.0 blah blah 818744520000000000 \n";
			final NMEA_Radar_FileImporter importer = new NMEA_Radar_FileImporter();
			final Layers layers = new Layers();

			assertTrue("logger empty", _logger.isEmpty());

			final InputStream is = new ByteArrayInputStream(initialString.getBytes());
			final Action action = importer.importThis(_origin, is, layers, _logger);
			action.execute();

			assertEquals("has data", 1, layers.size());
			assertFalse("logger not empty", _logger.isEmpty());

			assertEquals("correct logging message", "Expecting 17 tokens in CLog format at line:3. Found:15",
					_logger.messages.get(0));
		}

		public void testTooManyTokens() throws Exception {

			final String initialString = "Unknown blah blah blah\n" + "Blah blah blah blah\n"
					+ "blah blah blah blah blah blah blah blah blah 4.707152992628706 1.028888888888889 0.3872237414283774 -0.37869945639890085 0.0 blah blah 818744400000000000 EXTRA\n"
					+ "blah blah blah blah blah blah blah blah blah 4.707152992628706 1.028888888888889 0.38722349902153685 -0.37875089513046656 0.0 blah blah 818744460000000000 \n"
					+ "blah blah blah blah blah blah blah blah blah 4.710643651132695  1.028888888888889 0.38722315965196 -0.3788082485889418 0.0 blah blah 818744520000000000 \n";
			final NMEA_Radar_FileImporter importer = new NMEA_Radar_FileImporter();
			final Layers layers = new Layers();

			assertTrue("logger empty", _logger.isEmpty());

			final InputStream is = new ByteArrayInputStream(initialString.getBytes());
			final Action action = importer.importThis(_origin, is, layers, _logger);
			action.execute();

			assertEquals("has data", 1, layers.size());
			assertFalse("logger not empty", _logger.isEmpty());

			assertEquals("correct logging message", "Expecting 17 tokens in CLog format at line:2. Found:18",
					_logger.messages.get(0));
		}

		private long timeStampFor(final HiResDate date) {
			final long millis = date.getMicros();
			return millis * 1000;
		}

		private String toLogFile(final FixWrapper fix) {
			String res = "";
			final String blah = "blah";
			final String separator = " ";
			final String nl = System.lineSeparator();

			res += (blah + separator); // 1
			res += (blah + separator); // 2
			res += (blah + separator); // 3
			res += (blah + separator); // 4
			res += (blah + separator); // 5
			res += (blah + separator); // 6
			res += (blah + separator); // 7
			res += (blah + separator); // 8
			res += (blah + separator); // 9

			res += (fix.getCourse() + separator); // 10 - Course in radians
			res += (new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec) + separator); // 11 -
																												// Speed
																												// in
																												// metres/second
			res += (Math.toRadians(fix.getLocation().getLat()) + separator); // 12 - Latitude in Radians
			res += (Math.toRadians(fix.getLocation().getLong()) + separator); // 13 - Longitude in Radians
			res += (fix.getLocation().getDepth() + separator); // 14 - Depth in metres
			res += (blah + separator); // 15
			res += (blah + separator); // 16
			res += (timeStampFor(fix.getDTG()) + separator); // 17 - timestamp in Nanos since
																// epoch (19 digits!)

			// and newline
			res += nl;

			return res;
		}
	}
	
	private static FixWrapper generateFix(final WorldLocation origin, final RadarEntry entry) {
		Fix theFix = new Fix(entry.dtg, origin, );
		return new FixWrapper(theFix);
	}

	/**
	 * package up the action that adds the data to the layers target
	 *
	 */
	private static class ImportNmeaRadarFileAction implements Action {
		private final List<RadarEntry> _entries;
		private final Layers _layers;
		private final WorldLocation _origin;
		private boolean _trackCreated = false;

		public ImportNmeaRadarFileAction(final List<RadarEntry> track, final WorldLocation origin, final Layers layers) {
			super();
			_origin = origin;
			_entries = track;
			_layers = layers;
		}

		@Override
		public void execute() {
			for (RadarEntry e: _entries) {
				// get the parent track
				final Layer layer = _layers.findLayer("" + e.trackId);
				final TrackWrapper track;
				if(layer == null) {
					track = new TrackWrapper();
					track.setName(""+ e.trackId);
					track.setColor(DebriefColors.GREEN);
					_trackCreated = true;
				} else {
					track = (TrackWrapper) layer;
				}
				
				// now generate the fix
				
				// add to the track
				
			}
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

	public Action importThis(final WorldLocation origin, final InputStream is, final Layers layers,
			final ErrorLogger logger) throws Exception {
		final List<RadarEntry> brtData = readRadarData(is, logger);
		return new ImportNmeaRadarFileAction(brtData, origin, layers);
	}
	
	private static Date getDate(final String item) throws ParseException {
		final SimpleDateFormat dateFormatter = new GMTDateFormat("yyMMdd_hhmmss");
		return dateFormatter.parse(item);
	}
	
	private static Optional<RadarEntry> readLine(String line, int lineNum) {
		// split line
		final String[] entries = line.split(",");
		
		// check it's of correct length
		if (entries.length == 16) {
				final RadarEntry entry = new RadarEntry();
				final String header = entries[0];
				final String[] headers = header.split(":");
				if (headers.length == 2) {
					final String brgIndicator = entries[4];
					if(brgIndicator == "T") {

						try {
						final Date date = getDate(headers[0]);
						final HiResDate dtg = new HiResDate(date.getTime());
						
						entry.dtg = dtg;
						entry.trackId = Integer.parseInt(entries[1]);	
						entry.rangeNm = Double.parseDouble(entries[2]);
						entry.brgDegs = Double.parseDouble(entries[3]);
						
						entry.speedKts = Double.parseDouble(entries[5]);
						entry.courseDegs = Double.parseDouble(entries[6]);
						
						return Optional.of(entry);
						} catch (ParseException pe) {
							System.out.println("Parse exception:" + pe.getMessage() );
							return Optional.empty();
						}
					} else {
						System.out.println("Can only expect T (True) values for bearing for TTM messages at line:" + lineNum);
						return Optional.empty();						
					}
				} else {
					System.out.println("too few headers. Expected 2 got:" + headers.length);
					return Optional.empty();
				}
				
		} else {
			System.out.println("NMEA Radar Importer. Expected 16 columns, got " + entries.length);
			return Optional.empty();
		}
	}

	private List<RadarEntry> readRadarData(final InputStream is, final ErrorLogger logger) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line;

		List<RadarEntry> res = null;

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
			ctr++;
		}
		return res;
	}
}
