package org.mwc.cmap.gridharness.data;

import java.text.*;
import java.util.*;

public class FormatDateTime {
	public static final String DEFAULT_PATTERN = "yy/MM/dd HH:mm:ss";
	public static final String DEFAULT_TIME_ZONE_ID = "GMT";
	static private SimpleDateFormat _df = null;

	static public String toString(long theVal) {
		return toStringLikeThis(theVal, DEFAULT_PATTERN);
	}

	static public String toStringLikeThis(long theVal, String thePattern) {
		java.util.Date theTime = new java.util.Date(theVal);
		String res;

		if (_df == null) {
			_df = new SimpleDateFormat(thePattern);
			_df.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_ID));
		}

		// do we need to change the pattern?
		if (_df.toPattern().equals(thePattern)) {
			// hey, don't bother, we're ok
		} else {
			// and update the pattern
			_df.applyPattern(thePattern);
		}

		res = _df.format(theTime);

		return res;
	}

	static public String getExample() {
		return "ddHHmm.ss";
	}
}
