package Debrief.ReaderWriter.Antares;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class ImportAntaresImpl {

	/**
	 * Class used to trigger errors while parsing the Antares file
	 *
	 */
	public static class ImportAntaresException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8651821797693893982L;

		public ImportAntaresException(final String _message) {
			super(_message);
		}
	}

	/**
	 * Method that returns true if the Stream given contains a valid Antares file.
	 * 
	 * @param is Input Stream to read from
	 * @return true only if it is a valid Antares file.
	 */
	public static boolean canLoadThisStream(final InputStream is) {
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String nisidaLine;
			if ((nisidaLine = br.readLine()) != null) {
				return nisidaLine.startsWith("TRACK/");
			}
		} catch (final IOException e) {
			// There were problems reading the file. It cannot be loaded.
		}
		return false;
	}

	/**
	 * Method that import the Antares format given through a InputStream into the
	 * Layers.
	 * 
	 * @param _is        InputStream where the Antares format is available.
	 * @param _layers    New Track will be inserted into this layers
	 * @param _trackName Name of the new track (since it is not available in the
	 *                   Antares format)
	 * @param _month     Month to use in the new track (since it is not available in
	 *                   the Antares format)
	 * @param _year      Year to use in the new track (since it is not available in
	 *                   the Antares format)
	 * @return A list with the errors found while reading the Antares format.
	 */
	public static List<ImportAntaresException> importThis(final InputStream _is, final Layers _layers,
			final String _trackName, final int _month, final int _year) {
		final ArrayList<ImportAntaresException> errors = new ArrayList<>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(_is));

		/**
		 * Let's create the layer where we are going to insert the track data
		 */
		TrackWrapper track = (TrackWrapper) _layers.findLayer(_trackName, true);

		if (track == null) {
			// We didn't find the track, let's create it then.
			track = new TrackWrapper();
			track.setColor(DebriefColors.RED);
			track.setName(_trackName);
			_layers.addThisLayer(track);
		}

		String antaresLine;
		int lineNumber = 0;
		try {
			while ((antaresLine = br.readLine()) != null) {
				try {
					++lineNumber;
					loadThisLine(antaresLine, track, _trackName, _month, _year);
				} catch (Exception e) {
					errors.add(new ImportAntaresException("Error in line " + lineNumber + "\n" + e.getMessage()));
				}
			}
		} catch (IOException e1) {
			errors.add(new ImportAntaresException("Error reading line from InputStream"));
		}

		try {
			br.close();
		} catch (IOException e) {
			errors.add(new ImportAntaresException(e.getMessage()));
		}

		return errors;
	}

	/**
	 * Method for parsing the Antares lines.
	 * 
	 * Sample: TRACK/DDHHMMZ/LAT-LONG/COURSE/SPEED/DEPTH//
	 * 
	 * @param _antaresLine line to parse
	 * @param _track    Layer where we will load the tracks read
	 * @param _trackName   Name of the new track (since it is not available in the
	 *                     Antares format)
	 * @param _month       Month to use in the new track (since it is not available
	 *                     in the Antares format)
	 * @param _year        Year to use in the new track (since it is not available
	 *                     in the Antares format)
	 * @throws ImportAntaresException In case of any error, we will throw an
	 *                                ImportAntaresException with a descriptive
	 *                                message
	 */
	private static void loadThisLine(final String _antaresLine, final TrackWrapper _track, final String _trackName,
			final int _month, final int _year) throws ImportAntaresException {

		/**
		 * We can conclude it is an invalid line.
		 */
		if (!_antaresLine.startsWith("TRACK/")) {
			throw new ImportAntaresException("Line doesn't start with the reserverved word TRACK/");
		}

		/**
		 * Let's split the track into tokens.
		 */
		final String[] tokens = _antaresLine.split("/");
		if (tokens.length != 6) {
			throw new ImportAntaresException("Incorrect amount of fields");
		}

		final HiResDate date = parseDate(tokens[1], _month, _year);
		final WorldLocation location = parseWorldLocation(tokens[2]);
		final double course = parseDouble(tokens[3], "Course");
		final double speed = parseDouble(tokens[4], "Speed");
		final double depth = parseDouble(tokens[5], "Depth");

		/**
		 * Ok, at this point we have everything to create the Fix, then the FixWrapper
		 * and after all that we will insert it into the Track.
		 */
		final Fix newFix = new Fix(date, location, course, speed);
		final FixWrapper newFixWrapper = new FixWrapper(newFix);
		newFixWrapper.setDepth(depth);
		newFixWrapper.resetName();

		_track.add(newFixWrapper);
	}

	/**
	 * Parse the given date, using the month, and year given
	 * 
	 * @param dateAsString Date in the Antares format (DDHHMMZ)
	 * @param _month       month to assign to the date
	 * @param _year        year to assign to the date
	 * @return Date in the HiResDate format
	 * @throws ImportAntaresException In case of any error, we will throw an
	 *                                ImportAntaresException with a descriptive
	 *                                message
	 */
	private static HiResDate parseDate(final String dateAsString, int _month, int _year) throws ImportAntaresException {
		if (dateAsString.charAt(dateAsString.length() - 1) != 'Z') {
			throw new ImportAntaresException("Invalid format for timestamp - missing Z character: " + dateAsString);
		}

		try {
			final int day = Integer.parseInt(dateAsString.substring(0, 2));
			final int hour = Integer.parseInt(dateAsString.substring(2, 4));
			final int minute = Integer.parseInt(dateAsString.substring(4, 6));

			final Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			calendar.set(_year, _month, day, hour, minute);
			return new HiResDate(calendar.getTime());
		} catch (final Exception e) {
			throw new ImportAntaresException(
					"Invalid format for timestamp - day, hour or min could not be converted to date");
		}
	}

	/**
	 * Parses a location in the following format: LAT-LONG
	 * 
	 * @param locationString location to parse
	 * @return A WorldLocation Object equivalent
	 * @throws ImportAntaresException In case of any error, we will throw an
	 *                                ImportAntaresException with a descriptive
	 *                                message
	 */
	private static WorldLocation parseWorldLocation(final String locationString) throws ImportAntaresException {
		final String[] tokens = locationString.split("-");
		if (tokens.length != 2) {
			throw new ImportAntaresException("Incorrect Location format");
		}

		final Double latVal = parseDegrees(tokens[0]);
		final Double longVal = parseDegrees(tokens[1]);

		final WorldLocation location = new WorldLocation(latVal, longVal, 0d);
		return location;
	}

	/**
	 * Parse a location degree, Examples: 
	 * 2512.0N
	 * 03010.5W
	 * @param degrees
	 * @return Equivalent location as double
	 * @throws ImportAntaresException
	 */
	private static Double parseDegrees(String degrees) throws ImportAntaresException {
		try {
			final int point = degrees.indexOf(".");
			final String minString = degrees.substring(point - 2, degrees.length() - 1);
			final double mins = Double.parseDouble(minString);
			final String degString = degrees.substring(0, point - 2);
			final double degs = Double.parseDouble(degString);
			final String suffix = degrees.substring(degrees.length() - 1, degrees.length());
			final double scalar = suffix.toUpperCase().equals("N") || suffix.toUpperCase().equals("E") ? 1d : -1d;
			final double res = scalar * (degs + mins / 60d);
			return res;
		} catch (final NumberFormatException nfe) {
			throw new ImportAntaresException("Error parsing degrees in location " + degrees);
		}
	}

	private static double parseDouble(String doubleAsString, String fieldName) throws ImportAntaresException {
		try {
			return Double.parseDouble(doubleAsString);
		} catch (Exception e) {
			throw new ImportAntaresException("Error parsing the " + fieldName);
		}
	}
}
