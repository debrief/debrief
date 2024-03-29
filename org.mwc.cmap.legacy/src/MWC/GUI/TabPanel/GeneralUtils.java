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

package MWC.GUI.TabPanel;

//???RKM??? I would have called this Util.java, but for the bug in the project manager

// 	06/11/97	LAB	Added frameTarget_* varaibles for convenience in classes
//					which need reference to these strings.
// 	07/08/97	LAB	Added checkValidPercent() function
//					Changed GeneralUtils to a "final" class so JIT can inline it.

// Written by Levi Brown and Rod Magnuson 1.1, July 8, 1997.

/**
 * Useful utility functions and constants.
 *
 * @version 1.1, July 8, 1997
 * @author Symantec
 */
public final class GeneralUtils {
	/**
	 * A constant indicating a document should be shown in the current frame. It is
	 * the second parameter for the method:
	 * java.applet.AppletContext.showDocument(URL, String). It is provided here for
	 * general use.
	 *
	 * @see java.applet.AppletContext#showDocument(java.net.URL, java.lang.String)
	 */
	public static String frameTarget_self = "_self";

	/**
	 * A constant indicating a document should be shown in the parent frame. It is
	 * the second parameter for the method:
	 * java.applet.AppletContext.showDocument(URL, String). It is provided here for
	 * general use.
	 *
	 * @see java.applet.AppletContext#showDocument(java.net.URL, java.lang.String)
	 */
	public static String frameTarget_parent = "_parent";
	/**
	 * A constant indicating a document should be shown in the topmost frame. It is
	 * the second parameter for the method:
	 * java.applet.AppletContext.showDocument(URL, String). It is provided here for
	 * general use.
	 *
	 * @see java.applet.AppletContext#showDocument(java.net.URL, java.lang.String)
	 */
	public static String frameTarget_top = "_top";
	/**
	 * A constant indicating a document should be shown in a new unnamed top-level
	 * window. It is the second parameter for the method:
	 * java.applet.AppletContext.showDocument(URL, String). It is provided here for
	 * general use.
	 *
	 * @see java.applet.AppletContext#showDocument(java.net.URL, java.lang.String)
	 */
	public static String frameTarget_blank = "_blank";

	/**
	 * Checks to make sure the percent parameter is in range.
	 *
	 * @exception IllegalArgumentException if the specified percentage value is
	 *                                     unacceptable
	 */
	public static void checkValidPercent(final double percent) throws IllegalArgumentException {
		if (percent > 1 || percent < 0)
			throw new IllegalArgumentException(percent + " is not a valid percentage value. It should be <= 1 && >= 0");
	}

	/**
	 * Compares two objects passed in for equality. Handle null objects.
	 *
	 * @param objectA one of the objects to be compared
	 * @param objectB one of the objects to be compared
	 */
	public static boolean objectsEqual(final Object objectA, final Object objectB) {
		if (objectA == null)
			return (objectB == null);

		return objectA.equals(objectB);
	}

	/**
	 * Do not use, all-static class.
	 */
	public GeneralUtils() {
	}

}
