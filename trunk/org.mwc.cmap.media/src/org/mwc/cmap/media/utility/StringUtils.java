package org.mwc.cmap.media.utility;

public class StringUtils {
	
	public static boolean safeEquals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.equals(s2);
	}
}
