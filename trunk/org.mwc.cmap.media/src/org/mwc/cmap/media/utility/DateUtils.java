package org.mwc.cmap.media.utility;

import java.util.Date;

public class DateUtils {
	
	public static void removeMilliSeconds(Date date) {
		date.setTime((date.getTime() / 1000) * 1000);
	}
}
