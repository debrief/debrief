/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.gridharness.data.base60;

import java.text.DecimalFormat;
import java.text.ParseException;

import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.PlainFormatLocation;

public abstract class SexagesimalFormat implements PlainFormatLocation{

	public static final char MINUS_LATITUDE = 'S';

	public static final char PLUS_LATITUDE = 'N';

	public static final char PLUS_LONGITUDE = 'E';

	public static final char MINUS_LONGITUDE = 'W';

	public static DecimalFormat DDD = new DecimalFormat("000");

	public static DecimalFormat XX = new DecimalFormat("00");

	public static DecimalFormat XX_XXX = new DecimalFormat("00.000");

	public abstract String format(Sexagesimal sexagesimal, boolean forLongitudeNotLatitude);

	public abstract Sexagesimal parse(String text, boolean forLongitudeNotLatitude) throws ParseException;

	public abstract Sexagesimal parseDouble(double degrees);

	public abstract String getNebulaPattern(boolean forLongitudeNotLatitude);

	protected void appendHemisphere(final Sexagesimal value, final boolean forLongitudeNotLatitude, final StringBuffer output) {
		if (value.getHemi() < 0) {
			output.append(forLongitudeNotLatitude ? MINUS_LONGITUDE : MINUS_LATITUDE);
		} else {
			output.append(forLongitudeNotLatitude ? PLUS_LONGITUDE : PLUS_LATITUDE);
		}
	}

	/**
	 * @return <code>1</code> if this text denotes positive hemisphere (N or W),
	 * 	<code>-1</code> otherwise
	 */
	protected int getHemisphereSignum(final String text, final boolean forLongitudeNotLatitude) throws ParseException {
		if (text.length() != 0) {
			final char last = text.charAt(text.length() - 1);
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

	public String convertToString(final WorldLocation theLocation)
	{
		final Sexagesimal theLat = parseDouble(theLocation.getLat());
		final Sexagesimal theLong = parseDouble(theLocation.getLong());
		final String res = format(theLat, false) + " " + format(theLong, true);
		return res;
	}

}