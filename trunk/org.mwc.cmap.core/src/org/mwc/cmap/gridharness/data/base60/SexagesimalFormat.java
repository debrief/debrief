package org.mwc.cmap.gridharness.data.base60;

import java.text.DecimalFormat;
import java.text.ParseException;

public abstract class SexagesimalFormat {

	public static final char MINUS_LATITUDE = 'S';

	public static final char PLUS_LATITUDE = 'N';

	public static final char PLUS_LONGITUDE = 'W';

	public static final char MINUS_LONGITUDE = 'E';

	public static DecimalFormat DDD = new DecimalFormat("000");

	public static DecimalFormat XX = new DecimalFormat("00");

	public static DecimalFormat XX_XXX = new DecimalFormat("00.000");

	public abstract String format(Sexagesimal sexagesimal, boolean forLongitudeNotLatitude);

	public abstract Sexagesimal parse(String text, boolean forLongitudeNotLatitude) throws ParseException;

	public abstract Sexagesimal parseDouble(double degrees);

	public abstract String getNebulaPattern(boolean forLongitudeNotLatitude);

	protected void appendHemisphere(Sexagesimal value, boolean forLongitudeNotLatitude, StringBuffer output) {
		if (value.getDegrees() < 0) {
			output.append(forLongitudeNotLatitude ? MINUS_LONGITUDE : MINUS_LATITUDE);
		} else {
			output.append(forLongitudeNotLatitude ? PLUS_LONGITUDE : PLUS_LATITUDE);
		}
	}

	/**
	 * @return <code>1</code> if this text denotes positive hemisphere (N or W),
	 * 	<code>-1</code> otherwise
	 */
	protected int getHemisphereSignum(String text, boolean forLongitudeNotLatitude) throws ParseException {
		if (text.length() != 0) {
			char last = text.charAt(text.length() - 1);
			if (forLongitudeNotLatitude) {
				switch (last) {
				case MINUS_LONGITUDE:
					return -1;
				case PLUS_LONGITUDE:
					return 1;
				}
			} else {
				switch (last) {
				case MINUS_LATITUDE:
					return -1;
				case PLUS_LATITUDE:
					return 1;
				}
			}
		}
		throw new ParseException("There should be hemisphere: " + (forLongitudeNotLatitude ? " (W/E)" : " (N/S)") + ": " + text, 0);
	}
}