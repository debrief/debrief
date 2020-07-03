package Debrief.ReaderWriter.Nisida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
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

	public static String ATTACKS_LAYER = "Attacks";

	public static class NisidaLoadState {

		private Object lastEntryWithText;

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

		public Object getLastEntryWithText() {
			return lastEntryWithText;
		}

		public void setLastEntryWithText(Object lastEntryWithText) {
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
			int lineNumber = 0;
			while ((nisidaLine = br.readLine()) != null && lineNumber < 50) {
				if (nisidaLine != null && nisidaLine.startsWith("UNIT/")) {
					return true;
				}
				++lineNumber;
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
			final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMyy");
			String nisidaLine;
			int lineNumber = 1;
			final NisidaLoadState status = new NisidaLoadState(layers);
			while ((nisidaLine = br.readLine()) != null) {
				status.setLineNumber(lineNumber);
				loadThisLine(nisidaLine, status, dateFormatter);
				++lineNumber;
			}
		} catch (IOException e) {
			// There were problems reading the file. It cannot be loaded.
		}
	}

	private static void loadThisLine(final String line, final NisidaLoadState status,
			final SimpleDateFormat dateFormatter) {
		if (line.startsWith("UNIT/")) {
			processUnit(line, status, dateFormatter);
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

			processContinue(line, status);
		} else if (line.length() > 7 && line.charAt(7) == '/' && allNumbersDigit(line.substring(0, 6))) {
			processOperation(line, status);
		} else {
			// Not a line we recognise, so just skip to next one
			return;
		}
	}

	protected static void processOperation(final String line, final NisidaLoadState status) {
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
			final String operationUpper3;
			if (tokens.length > 3) {
				operationUpper3 = tokens[3].toUpperCase();
			} else {
				operationUpper3 = "";
			}
			if (isNarrativeOrCoc(operationUpper)) {
				/**
				 * The COC and NAR messages have the same format COC isn't actually described in
				 * the documentation for the format, but seems to be Commanding Officer
				 * Comments, and is present in the example
				 */

				processNarrative(tokens, status);
			} else if ("DET".equals(operationUpper)) {
				processDetection(tokens, status);
			} else if ("ATT".equals(operationUpper)) {
				processAttack(tokens, status);
			} else if (isDipOrBoy(operationUpper)) {
				processDipOrBoy(tokens, status);
			} else if ("EXP".equals(operationUpper)) {
				processMastexposure(tokens, status);
			} else if ("SEN".equals(operationUpper)) {
				processSensor(tokens, status);
			} else if ("ENV".equals(operationUpper)) {
				processEnvironment(tokens, status);
			} else if (isPositionProcess(operationUpper3)) {
				processPosition(tokens, status);
			} else {
				// ok, it's probably a position.
				if (isNorthOrSouth(operationUpper)) {
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
	}

	protected static boolean isNorthOrSouth(final String process) {
		return process.endsWith("N") || process.endsWith("S");
	}

	protected static boolean isNarrativeOrCoc(final String process) {
		return "NAR".equals(process) || "COC".equals(process);
	}

	protected static boolean isDipOrBoy(final String process) {
		return "DIP".equals(process) || "SSQ".equals(process);
	}

	protected static boolean isPositionProcess(final String process) {
		return "GPS".equals(process) || "DR".equals(process)
				|| "IN".equals(process);
	}

	protected static void processContinue(final String line, final NisidaLoadState status) {
		final String textToAdd;
		if (line.endsWith("/")) {
			textToAdd = line.substring(2, line.length() - 1);
		} else {
			textToAdd = line.substring(2);
		}

		final Object lastEntry = status.getLastEntryWithText();
		if (lastEntry instanceof NarrativeEntry) {
			final NarrativeEntry lastNarrative = (NarrativeEntry) lastEntry;
			lastNarrative.setEntry(lastNarrative.getEntry() + textToAdd);
		} else if (lastEntry instanceof SensorContactWrapper) {
			final SensorContactWrapper lastContact = (SensorContactWrapper) lastEntry;
			lastContact.setComment(lastContact.getComment() + textToAdd);
		}
	}

	protected static void processUnit(final String line, final NisidaLoadState status,
			final SimpleDateFormat dateFormatter) {
		/**
		 * Handle UNIT line giving month, year and platform Format is:
		 * UNIT/ADRI/OCT03/SRF/
		 */
		final String[] tokens = line.split("/");

		final String platformName = tokens[1];

		// FIND THE PLATFORM.
		TrackWrapper track = (TrackWrapper) status.getLayers().findLayer(platformName);
		if (track == null) {
			track = new TrackWrapper();
			track.setColor(DebriefColors.RED);
			track.setName(platformName);
			status.getLayers().addThisLayer(track);
		}
		status.setPlatform(track);

		final String dateString = tokens[2];

		try {
			final Date date = dateFormatter.parse(dateString);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			status.setMonth(calendar.get(Calendar.MONTH));
			status.setYear(calendar.get(Calendar.YEAR));
		} catch (ParseException e) {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber(),
					"Parse error in the date: " + dateString));
		}
	}

	/**
	 * Process Nisida narrative Format [DayTime/NAR/Narrative /] sample
	 * 311056Z/NAR/TEXT FOR NARRATIVE PURPOSES/
	 * 
	 * @param tokens input split in tokens
	 * @param status class status
	 */
	private static void processNarrative(final String[] tokens, final NisidaLoadState status) {
		final String type;
		if ("NAR".equals(tokens[1])) {
			type = "Narrative";
		} else if ("COC".equals(tokens[1])) {
			type = "CO Comments";
		} else {
			type = tokens[1];
		}
		final NarrativeEntry newNarrative = createNarrative(Arrays.copyOfRange(tokens, 2, tokens.length), type, status);
		status.setLastEntryWithText(newNarrative);
	}

	/**
	 * parse lat/long value in degrees, using NISIDA position structure
	 * 
	 * @param value  string to parse
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

	/**
	 * parse the string, with Nisida standard states for missing data ("-" or "")
	 * 
	 * @param value  string to be parsed
	 * @param status supporter object
	 * @return double value, or null if field is empty
	 */
	public static Double valueFor(final String value, final NisidaLoadState status) {
		final Double res;
		if (value.length() == 0) {
			res = null;
		} else if (value.equals("-")) {
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
		if (dtg != null && latVal != null && longVal != null) {
			final WorldLocation location = new WorldLocation(latVal, longVal, 0d);

			final String source = tokens[3];
			final Double courseVal = valueFor(tokens[4], status);
			final Double speedVal = valueFor(tokens[5], status);
			final Double depthVal = valueFor(tokens[6], status);

			Fix fix = new Fix(dtg, location, 0d, 0d);
			if (courseVal != null) {
				fix.setCourse(Math.toRadians(courseVal));
			}
			if (speedVal != null) {
				fix.setSpeed(Conversions.Kts2Yps(speedVal));
			}
			if (depthVal != null) {
				fix.getLocation().setDepth(depthVal);
			}
			FixWrapper res = new FixWrapper(fix);

			// sort out the sensor
			final String sourceName = POS_SOURCE_TO_NAME.get(source);
			if (sourceName != null) {
				res.setLabel(sourceName);
			} else {
				res.setLabel(source);
			}

			status.getPlatform().addFix(res);
		}
	}

	private static void processEnvironment(final String[] tokens, final NisidaLoadState status) {
		// sample:
		// 311212Z/ENV/12/12/5/5/3/12/12/22/ENVIRONMENT TEXT/
		// format:
		// [DayTime/ENV/Windcourse/Windspeed in kts/ Seastate/Visibility in NM/ cloud
		// coverage in octals/ PSR in yds/ Layerdepth in m/ PRR in NM /Remarks/ ]

		if (tokens.length >= 2) {
			final NarrativeEntry newNarrative = createNarrative(Arrays.copyOfRange(tokens, 2, tokens.length), "Environment", status);
			status.setLastEntryWithText(newNarrative);
		} else {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid amount of fields. Expected format should be 2 fields at least."));
			return;
		}
	}

	private static void processSensor(final String[] tokens, final NisidaLoadState status) {
		// sample:
		// 311300Z/SEN/TAS/-/13:00/SENSOR TIME OFF EXAMPLE/
		// format:
		// [DayTime/SEN/Sensor/Time on/Time off/Remarks/]

		if (tokens.length >= 2) {
			final NarrativeEntry newNarrative = createNarrative(Arrays.copyOfRange(tokens, 2, tokens.length), "Sensor Activation", status);
			status.setLastEntryWithText(newNarrative);
		} else {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid amount of fields. Expected format should be 2 fields at least."));
			return;
		}
	}

	private static void processMastexposure(final String[] tokens, final NisidaLoadState status) {
		// sample:
		// 171000Z/EXP/PER/10:00/-/FULLY CHARGED AND READY TO KILL/
		// format:
		// [DayTime /EXP/ Mast /Time up/Time down/Remarks/]
		//

		if (tokens.length >= 2) {
			final NarrativeEntry newNarrative = createNarrative(Arrays.copyOfRange(tokens, 2, tokens.length), "Mast-Exposure", status);
			status.setLastEntryWithText(newNarrative);

		} else {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid amount of fields. Expected format should be 2 fields at least."));
			return;
		}
	}

	private static void processDipOrBoy(final String[] tokens, final NisidaLoadState status) {
		// sample:
		// 311305Z/DIP/5/23/14:00/3502.02N/00502.06E/ASW DIP EXAMPLE/
		// or
		// 312100Z/SSQ/33/05/2/3605.00N/00512.12E/ASW BUOY TEXT/
		// format:
		// [DayTime Mark/DIP/Dip NR/ Ball Depth in m/Time Break/Dip Lat/Dip Lon /
		// Remarks/
		// or
		// [DayTime Drop/SSQ/ Buoy NR/Hydro Depth in m/Buoy LifeSetting in hrs /Buoy
		// Lat/Buoy Lon/Remarks/]

		if (tokens.length >= 7) {

			// This is a comment (NarrativeEntry). As with other comments, we can use the
			// current track name as “track”.
			final String operation = tokens[1];
			final String operationUpper = operation.toUpperCase();
			final String type;
			final String layer;
			final String symbol;
			if ("DIP".equals(operationUpper)) {
				type = "DIP";
				layer = "DIPs";
				symbol = "svg:" + SymbolFactory.BUOY_1;
			} else if ("SSQ".equals(operationUpper)) {
				type = "Buoy-Drop";
				layer = "Buoys";
				symbol = "svg:" + SymbolFactory.BUOY_2;
			} else {
				// this will never happen
				type = operationUpper;
				layer = "";
				symbol = "";
			}

			final NarrativeEntry newNarrativeEntry = createNarrative(Arrays.copyOfRange(tokens, 2, tokens.length), type, status);
			status.setLastEntryWithText(newNarrativeEntry);

			final WorldLocation location = parseLocation(tokens[5], tokens[6], status);
			final LabelWrapper labelWrapper = new LabelWrapper(tokens[0], location, DebriefColors.RED);
			labelWrapper.setSymbolType(symbol);
			Layer dest = status.getLayers().findLayer(layer, true);
			if (dest == null) {
				dest = new BaseLayer();
				dest.setName(layer);

				// add it to the manager
				status.getLayers().addThisLayer(dest);
			}
			dest.add(labelWrapper);

		} else {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid amount of fields. Expected format should be 7 fields at least."));
			return;
		}
	}

	private static void processAttack(final String[] tokens, final NisidaLoadState status) {
		// Sample:
		// 311206Z/ATT/OTHER/63/12/775/3623.23N/00500.25E/GPS/TEXT FOR ATTAC
		// K/
		// [DayTime/ATT/WPN/TGT Bearing/TGT RNGE in NM/TN / Own Lat/ Own Lon /Position
		// Source /Remarks/]

		// capture another “FixWrapper” for Own lat/lon

		if (tokens.length >= 9) {
			final WorldLocation location = parseLocation(tokens[6], tokens[7], status);
			final Fix fix = new Fix(new HiResDate(status.timestamp), location, 0d, 0d);
			final FixWrapper res = new FixWrapper(fix);

			// sort out the sensor
			final String source = tokens[8];
			final String sourceName = POS_SOURCE_TO_NAME.get(source);
			if (sourceName != null) {
				res.setLabel(sourceName);
			} else {
				res.setLabel(source);
			}

			// It will also be a LabelWrapper in an "Attacks" layer.
			final LabelWrapper labelWrapper = new LabelWrapper(tokens[0], location, DebriefColors.RED);
			labelWrapper.setSymbolType(SymbolFactory.DATUM);
			Layer dest = status.getLayers().findLayer(ATTACKS_LAYER, true);
			if (dest == null) {
				dest = new BaseLayer();
				dest.setName(ATTACKS_LAYER);

				// add it to the manager
				status.getLayers().addThisLayer(dest);
			}
			dest.add(labelWrapper);

			// This is a comment, with "Attack" as Comment-Type

			final NarrativeEntry newNarrative = createNarrative(Arrays.copyOfRange(tokens, 2, tokens.length), "Attack", status);
			// let's save the new narrative as a last
			status.setLastEntryWithText(newNarrative);

		} else {
			status.getErrors().add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
					"Invalid amount of fields. Expected format should be: [DayTime/ATT/WPN/TGT Bearing/TGT RNGE in NM/TN / Own Lat/ Own Lon /Position Source /Remarks/"));
			return;
		}

	}

	private static NarrativeEntry createNarrative(final String[] text, final String narrativeType,
			final NisidaLoadState status) {
		Layer narrativeDest = status.getLayers().findLayer(NarrativeEntry.NARRATIVE_LAYER, true);
		if (narrativeDest == null) {
			narrativeDest = new NarrativeWrapper(NarrativeEntry.NARRATIVE_LAYER);

			// add it to the manager
			status.getLayers().addThisLayer(narrativeDest);
		}

		final StringBuilder narrativeTextBuilder = new StringBuilder();
		for (String t : text) {
			narrativeTextBuilder.append("/");
			narrativeTextBuilder.append(t);
		}
		String finalText = narrativeTextBuilder.toString(); 
		if (finalText.length() > 1) {
			finalText = finalText.substring(1);
		}
		final NarrativeEntry entry = new NarrativeEntry(status.getPlatform().getName(),
				new HiResDate(status.getTimestamp()), finalText.trim());

		entry.setType(narrativeType);

		narrativeDest.add(entry);

		return entry;
	}

	private static void processDetection(final String[] tokens, final NisidaLoadState status) {
		// sample:
		// [DayTime/DET/DetectingSensor/Bearing/Range in NM/TN/Own Lat/Own Lon/Position
		// Source/Remarks/]
		// 311200Z/DET/RDR/23/20/777/3602.02N/00412.12E/GPS/DETECTION RECORD

		/**
		 * Create a sensor using the expanded `Sensor Code` name plus the track number
		 * (TN).
		 */
		final String sensorCodeToken = tokens[2];

		final Double trackNumber = valueFor(tokens[5], status);

		final String trackNumberString;
		if (trackNumber != null) {
			trackNumberString = " " + trackNumber;
		} else {
			trackNumberString = "";
		}

		final String sensorName;
		if (!SENSOR_CODE_TO_NAME.containsKey(sensorCodeToken)) {
			sensorName = sensorCodeToken + "-" + trackNumberString;
		} else {
			sensorName = SENSOR_CODE_TO_NAME.get(sensorCodeToken) + "-" + trackNumberString;
		}

		/**
		 * Create a FixWrapper on the parent track for the “Own Lat/OwnLon” position.
		 * Course/speed are zeroes
		 */

		final WorldLocation ownLocation = parseLocation(tokens[6], tokens[7], status);
		final Fix fix = new Fix(new HiResDate(status.timestamp), ownLocation, 0d, 0d);
		final FixWrapper res = new FixWrapper(fix);

		// sort out the sensor
		final String source = tokens[8];
		final String sourceName = POS_SOURCE_TO_NAME.get(source);
		if (sourceName != null) {
			res.setLabel(sourceName);
		} else {
			res.setLabel(source);
		}
		// store the new position
		status.getPlatform().addFix(res);

		/**
		 * Ok, we now create the ContactWrapper.
		 */

		final Enumeration<Editable> enumer = status.getPlatform().getSensors().elements();

		// search the sensor in the platform
		SensorWrapper theSensor = null;
		while (enumer.hasMoreElements()) {
			final SensorWrapper currentSensor = (SensorWrapper) enumer.nextElement();
			if (currentSensor.getName().equals(sensorName)) {
				theSensor = currentSensor;
				break;
			}
		}

		// we didn't find it, let's create a new sensor then
		if (theSensor == null) {
			theSensor = new SensorWrapper(sensorName);
			status.getPlatform().add(theSensor);
		}

		// Let's get the range & bearing
		final Double bearing = valueFor(tokens[3], status);
		final Double rangeVal = valueFor(tokens[4], status);
		final WorldDistance range = rangeVal != null ? new WorldDistance(rangeVal, MWC.GenericData.WorldDistance.NM)
				: null;

		// calculate target location
		final WorldLocation tgtLocation;
		if (bearing != null && range != null) {
			tgtLocation = ownLocation.add(new WorldVector(Math.toRadians(bearing), range, null));
		} else {
			tgtLocation = null;
		}

		final SensorContactWrapper contact = new SensorContactWrapper(status.getPlatform().getName(),
				new HiResDate(status.getTimestamp()), range, bearing, tgtLocation, null, sensorName, 0,
				theSensor.getName());
		theSensor.add(contact);
		contact.setComment(tokens[9]);
		status.setLastEntryWithText(contact);
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
			status.getErrors()
					.add(new ImportNisidaError("Error on line " + status.getLineNumber() + ".",
							"Invalid format for timestamp - day, hour or min could not be converted to float: "
									+ timestampText));
			return null;
		}
	}

	public static WorldLocation parseLocation(final String latString, final String longString,
			final NisidaLoadState status) {
		final Double latVal = parseDegrees(latString, status);
		final Double longVal = parseDegrees(longString, status);

		final WorldLocation location = new WorldLocation(latVal, longVal, 0d);
		return location;
	}
}
