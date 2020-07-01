package Debrief.ReaderWriter.Nisida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.NumericFunction;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

/**
 * Nisida Format Importer
 */
public class ImportNisida {

	public static class ImportNisidaError {
		private String type;
		private String message;

		public ImportNisidaError(final String type, final String message) {
			this.type = type;
			this.message = message;
		}

		public String getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}
	}

	final static Map<String, String> SENSOR_CODE_TO_NAME = new HashMap<String, String>();

	static {
		SENSOR_CODE_TO_NAME.put("RDR", "Radar");
		SENSOR_CODE_TO_NAME.put("PSON", "Passive Sonar");
		SENSOR_CODE_TO_NAME.put("ASON", "Active Sonar");
		SENSOR_CODE_TO_NAME.put("VDS", "Variable Depth Sonar");
		SENSOR_CODE_TO_NAME.put("HSON", "Helo Sonar");
		SENSOR_CODE_TO_NAME.put("HRDR", "Helo Radar");
		SENSOR_CODE_TO_NAME.put("TAS", "Array Sonar");
		SENSOR_CODE_TO_NAME.put("VIS", "Visible");
		SENSOR_CODE_TO_NAME.put("IR", "Infrared");
		SENSOR_CODE_TO_NAME.put("OTHER", "Generic");
	}

	final static Map<String, String> POS_SOURCE_TO_NAME = new HashMap<String, String>();

	static {
		POS_SOURCE_TO_NAME.put("GPS", "GPS");
		POS_SOURCE_TO_NAME.put("DR", "Dead Recknoning");
		POS_SOURCE_TO_NAME.put("IN", "Inertial");
	}

	public static class NisidaLoadState {

		private String lastEntryWithText;

		private int month;

		private int year;

		private TrackWrapper platform;

		private List<ImportNisidaError> errors = new ArrayList<ImportNisida.ImportNisidaError>();

		private Date timestamp;
		
		private int lineNumber;
		
		private Layers layers;

		public NisidaLoadState(final Layers _layers) {
			this.layers = _layers;
		}

		public String getLastEntryWithText() {
			return lastEntryWithText;
		}

		public void setLastEntryWithText(String lastEntryWithText) {
			this.lastEntryWithText = lastEntryWithText;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public TrackWrapper getPlatform() {
			return platform;
		}

		public void setPlatform(TrackWrapper platform) {
			this.platform = platform;
		}

		public List<ImportNisidaError> getErrors() {
			return errors;
		}

		public void setErrors(List<ImportNisidaError> errors) {
			this.errors = errors;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		public Layers getLayers() {
			return layers;
		}

		
	}

	/**
	 * Nisida Importer
	 */
	public ImportNisida() {

	}

	public static boolean canLoadThisFile(final InputStream is) {
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String nisidaLine;
			while ((nisidaLine = br.readLine()) != null) {
				if (nisidaLine != null && nisidaLine.startsWith("UNIT/")) {
					return true;
				}
			}
		} catch (IOException e) {
			// There were problems reading the file. It cannot be loaded.
		}
		return false;
	}

	/**
	 * Method that should be called to load the NISIDA file
	 * 
	 * @param is
	 */
	public static void importThis(final InputStream is, final Layers layers) {
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String nisidaLine;
			int lineNumber = 1;
			final NisidaLoadState status = new NisidaLoadState(layers);
			while ((nisidaLine = br.readLine()) != null) {
				status.setLineNumber(lineNumber);
				loadThisLine(nisidaLine, status);
				++lineNumber;
			}
		} catch (IOException e) {
			// There were problems reading the file. It cannot be loaded.
		}
	}

	private static void loadThisLine(final String line,
			final NisidaLoadState status) {
		if (line.startsWith("UNIT/")) {
			/**
			 * Handle UNIT line giving month, year and platform Format is:
			 * UNIT/ADRI/OCT03/SRF/
			 */
			final String[] tokens = line.split("/");

			final String platformName = tokens[1];
			
			// FIND THE PLATFORM.
			TrackWrapper track = (TrackWrapper) status.getLayers().findLayer(platformName);
			if(track == null) {
				track = new TrackWrapper();
				track.setName(platformName);
				status.getLayers().addThisLayer(track);
			}
			status.setPlatform(track);

			final String dateString = tokens[2];
			final DateFormat dateFormatter = new SimpleDateFormat("MMMyy");
			try {
				final Date date = dateFormatter.parse(dateString);
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				status.setMonth(calendar.get(Calendar.MONTH));
				status.setYear(calendar.get(Calendar.YEAR));
			} catch (ParseException e) {

			}
		} else if (line.startsWith("//")) {
			/**
			 * This is a continuation of the previous line, so add whatever else is in this
			 * line to the content field of the previous entry
			 */

			if (status.getLastEntryWithText() == null) {
				status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber(),
						"Line continuation not immediately after valid line: " + line));
				return;
			}

			final String textToAdd;
			if (line.endsWith("/")) {
				textToAdd = line.substring(2, line.length() - 1);
			} else {
				textToAdd = line.substring(2);
			}

		} else if (line.length() > 7 && line.charAt(7) == '/' && allNumbersDigit(line.substring(0, 6))) {
			/**
			 * Check whether line starts with something like "311206Z/" (a timestamp and a
			 * slash) Checking like this is faster than using regular expressions on each
			 * line
			 */

			/**
			 * Reset last_entry_with_text, so that if continuation characters aren't
			 * directly after an entry we processed, then we will raise an error rather than
			 * add to the incorrect entry
			 */
			status.setLastEntryWithText(null);

			// Split line by slash
			final String[] tokens = line.split("/");

			status.setTimestamp(parseTimestamp(tokens[0], status));

			try {
				final String operation = tokens[1];
				final String operationUpper = operation.toUpperCase();
				if ("NAR".equals(operationUpper) || "COC".equals(operationUpper)) {
					/**
					 * The COC and NAR messages have the same format COC isn't actually described in
					 * the documentation for the format, but seems to be Commanding Officer
					 * Comments, and is present in the example
					 */

					processNarrative(tokens, status);
				} /*else if ("DET".equals(operationUpper)) {
					processDetection(dataStore, datafile, changeId);
				} else if ("ATT".equals(operationUpper)) {
					processAttack(dataStore, datafile, changeId);
				} else if ("DIP".equals(operationUpper) || "SSQ".equals(operationUpper)) {
					processDipOrBoy(dataStore, datafile, changeId);
				} else if ("EXP".equals(operationUpper)) {
					processMastexposure(dataStore, datafile, changeId);
				} else if ("SEN".equals(operationUpper)) {
					processSensor(dataStore, datafile, changeId);
				} else if ("ENV".equals(operationUpper)) {
					processEnvironment(dataStore, datafile, changeId);
				} else if ("GPS".equals(operationUpper) || "DR".equals(operationUpper) || "IN".equals(operationUpper)) {
					processPosition(dataStore, datafile, changeId);
				} */else {
					// ok, it's probably a position.
					final String nextToken = tokens[2];
					if(operationUpper.endsWith("N") || operationUpper.endsWith("S")) {
						// ok, it's a position
						processPosition(tokens, status);
					}
					status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber(),
							"Line does not match any known message format: " + line));
				}
			} catch (Exception e) {
				status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber(),
						"General error processing line - " + line));
			}
		} else {
			// Not a line we recognise, so just skip to next one
			return;
		}
	}

	private static void processNarrative(final String[] tokens,
			final NisidaLoadState status) {
		final String commentText = tokens[2];

		Layer dest = status.getLayers().findLayer(NarrativeEntry.NARRATIVE_LAYER, true);
		if (dest == null) {
			dest = new NarrativeWrapper(NarrativeEntry.NARRATIVE_LAYER);

			// add it to the manager
			status.getLayers().addThisLayer(dest);
		}

		final NarrativeEntry entry = new NarrativeEntry(status.getPlatform().getName(),
				new HiResDate(parseTimestamp(tokens[0], status)), commentText);

		if ("NAR".equals(tokens[1])) {
			entry.setType("Narrative");
		} else if ("COC".equals(tokens[1])) {
			entry.setType("CO Comments");
		}

		dest.add(entry);
	}
	
	/** parse lat/long value in degrees, using NISIDA position structure
	 * 
	 * @param value string to parse
	 * @param status 
	 * @return double value, -ve if West/South
	 */
	public static Double parseDegrees(final String value, NisidaLoadState status) {
		try {
			final int point = value.indexOf(".");
			final String minString = value.substring(point - 2, value.length() - 1);
			final double mins = Double.parseDouble(minString);
			final String degString = value.substring(0, point - 2);
			final double degs = Double.parseDouble(degString);
			final String suffix = value.substring(value.length() - 1, value.length());
			final double scalar = suffix.toUpperCase().equals("N") || suffix.toUpperCase().equals("E") ? 1d : -1d;
			final double res = scalar * (degs + mins / 60d);
			return res;
		} catch (NumberFormatException nfe) {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber(),
					"Failed to parse numeric field - " + value));
			return null;
		}

	}
	
	/** parse the string, with Nisida standard states for missing data ("-" or "")
	 * 
	 * @param value string to be parsed
	 * @param status supporter object
	 * @return double value, or null if field is empty
	 */
	public static Double valueFor(final String value, final NisidaLoadState status) {
		final Double res;
		if(value.length() == 0) {
			res = null;
		} else if(value.equals("-")) {
			res = null;
		} else {
			try {
				res = Double.parseDouble(value);				
			} catch (NumberFormatException nfe) {
				status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber(),
						"Failed to parse numeric field - " + value));
				return null;
			}
		}
		return res;
	}

	private static void processPosition(final String[] tokens, final NisidaLoadState status) {
		// sample:
		// 311002Z/3623.00N/00412.02E/GPS/359/03/-/ 
		final HiResDate dtg = new HiResDate(status.timestamp);
		final Double latVal = parseDegrees(tokens[1], status);
		final Double longVal = parseDegrees(tokens[2], status);
		if(dtg != null && latVal != null && longVal != null) {
			final WorldLocation location = new WorldLocation(latVal, longVal, 0d);
			
			final String source = tokens[3];
			final Double courseVal = valueFor(tokens[4], status);
			final Double speedVal = valueFor(tokens[5], status);
			final Double depthVal = valueFor(tokens[6], status);
			
			Fix fix = new Fix(dtg, location, 0d, 0d);
			if(courseVal != null) {
				fix.setCourse(Math.toRadians(courseVal));
			}
			if(speedVal != null) {
				fix.setSpeed(Conversions.Kts2Yps(speedVal));
			}
			if(depthVal != null) {
				fix.getLocation().setDepth(depthVal);
			}
			FixWrapper res = new FixWrapper(fix);
			
			// sort out the sensor
			final String sourceName = POS_SOURCE_TO_NAME.get(source);
			if(sourceName != null) {
				res.setLabel(sourceName);
			} else {
				res.setLabel(source);
			}
			
			status.getPlatform().addFix(res);			
		}
	}

	private void processEnvironment(Object dataStore, Object datafile, String changeId) {
		// TODO Auto-generated method stub

	}

	private void processSensor(Object dataStore, Object datafile, String changeId) {
		// TODO Auto-generated method stub

	}

	private void processMastexposure(Object dataStore, Object datafile, String changeId) {
		// TODO Auto-generated method stub

	}

	private void processDipOrBoy(Object dataStore, Object datafile, String changeId) {
		// TODO Auto-generated method stub

	}

	private void processAttack(Object dataStore, Object datafile, String changeId) {
		// TODO Auto-generated method stub

	}

	private void processDetection(Object dataStore, Object datafile, String changeId) {
		// TODO Auto-generated method stub

	}

	public static boolean allNumbersDigit(final String text) {
		for (char ch : text.toCharArray()) {
			if (!Character.isDigit(ch)) {
				return false;
			}
		}
		return true;
	}

	public static Date parseTimestamp(final String timestampText, final NisidaLoadState status) {
		if (timestampText.charAt(timestampText.length() - 1) != 'Z') {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid format for timestamp - missing Z character: " + timestampText));
			return null;
		}

		try {
			final int day = Integer.parseInt(timestampText.substring(0, 2));
			final int hour = Integer.parseInt(timestampText.substring(2, 4));
			final int minute = Integer.parseInt(timestampText.substring(4, 6));

			final Calendar calendar = Calendar.getInstance();
			calendar.set(status.getYear(), status.getMonth(), day - 1, hour, minute);
			return calendar.getTime();
		} catch (Exception e) {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid format for timestamp - day, hour or min could not be converted to float: "
							+ timestampText));
			return null;
		}
	}

	public WorldLocation parseLocation() {
		return null;
	}
}
