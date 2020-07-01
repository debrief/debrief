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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Debrief.ReaderWriter.Nisida.ImportNisida.ImportNisidaError;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;

/**
 * Nisida Format Importer
 */
public class ImportNisida {

	public class ImportNisidaError {
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

	private String lastEntryWithText;

	private int month;

	private int year;

	private TrackWrapper platform;

	private Date timestamp;

	private List<ImportNisidaError> errors = new ArrayList<ImportNisida.ImportNisidaError>();

	/**
	 * Nisida Importer
	 */
	public ImportNisida() {
		errors.clear();
	}

	public boolean canLoadThisFile(final InputStream is) {
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
	 * @param is
	 */
	public void importThis(final InputStream is, final Layers layers) {
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String nisidaLine;
			int lineNumber = 1;
			while ((nisidaLine = br.readLine()) != null) {
				loadThisLine(layers, lineNumber, nisidaLine);
				++lineNumber;
			}
		} catch (IOException e) {
			// There were problems reading the file. It cannot be loaded.
		}
	}
	
	private void loadThisLine(final Layers layers, final int lineNumber, final String line) {
		if (line.startsWith("UNIT/")) {
			/**
			 * Handle UNIT line giving month, year and platform Format is:
			 * UNIT/ADRI/OCT03/SRF/
			 */
			final String[] tokens = line.split("/");

			final String platformName = tokens[1];

			// TODO
			// FIND THE PLATFORM.

			final String dateString = tokens[2];
			final DateFormat dateFormatter = new SimpleDateFormat("MMMyy");
			try {
				final Date date = dateFormatter.parse(dateString);
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				this.month = calendar.get(Calendar.MONTH);
				this.year = calendar.get(Calendar.YEAR);
			} catch (ParseException e) {

			}
		} else if (line.startsWith("//")) {
			/**
			 * This is a continuation of the previous line, so add whatever else is in this
			 * line to the content field of the previous entry
			 */

			if (this.lastEntryWithText == null) {
				this.errors.add(new ImportNisidaError("Error on line " + lineNumber,
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
			this.lastEntryWithText = null;

			// Split line by slash
			final String[] tokens = line.split("/");

			this.timestamp = parseTimestamp(matcher.group(0));

			try {
				final String operation = matcher.group(1);
				final String operationUpper = operation.toUpperCase();
				if ("NAR".equals(operationUpper) || "COC".equals(operationUpper)) {
					/**
					 * The COC and NAR messages have the same format COC isn't actually described in
					 * the documentation for the format, but seems to be Commanding Officer
					 * Comments, and is present in the example
					 */

					processNarrative(layers);
				} else if ("DET".equals(operationUpper)) {
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
				} else {
					this.errors.add(new ImportNisidaError("Error on line " + this.currentLineNo,
							"Line does not match any known message format: " + line));
				}
			} catch (Exception e) {
				this.errors.add(new ImportNisidaError("Error on line " + this.currentLineNo,
						"General error processing line - " + line));
			}
		} else {
			// Not a line we recognise, so just skip to next one
			return;
		}
	}

	private void processPosition(Object dataStore, Object datafile, String changeId) {
		final String posSourceMatched = matcher.group(3);
		if (!POS_SOURCE_TO_NAME.containsKey(posSourceMatched)) {
			this.errors.add(new ImportNisidaError("Error on line " + this.currentLineNo, "Invalid position source value: " + posSourceMatched));
		}
		final String posSource = POS_SOURCE_TO_NAME.get(posSourceMatched);
		// TODO add posSource to sensor types
		
		
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

	private void processNarrative(Object dataStore, Object datafile, String changeId) {
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

	public Date parseTimestamp(final String timestampText) {
		if (timestampText.charAt(timestampText.length() - 1) != 'Z') {
			this.errors.add(new ImportNisidaError("Error on line " + this.currentLineNo + ".",
					"Invalid format for timestamp - missing Z character: " + timestampText));
			return null;
		}

		try {
			final int day = Integer.parseInt(timestampText.substring(0, 2));
			final int hour = Integer.parseInt(timestampText.substring(2, 4));
			final int minute = Integer.parseInt(timestampText.substring(4, 6));

			final Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day - 1, hour, minute);
			return calendar.getTime();
		} catch (Exception e) {
			this.errors.add(new ImportNisidaError("Error on line " + this.currentLineNo + ".",
					"Invalid format for timestamp - day, hour or min could not be converted to float: "
							+ timestampText));
			return null;
		}
	}

	public WorldLocation parseLocation() {
		return null;
	}
}
