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

// $RCSfile: ImportNarrative.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ImportNarrative.java,v $
// Revision 1.7  2006/08/08 12:55:29  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.6  2006/07/17 11:06:15  Ian.Mayo
// Tidy export formatting, only export if not Narrative2 entrty
//
// Revision 1.5  2005/12/13 09:04:36  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/25 10:24:16  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/11 11:52:44  Ian.Mayo
// Reflect new directory structure
//
// Revision 1.2  2004/08/19 14:12:47  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.1.1.2  2003/07/21 14:47:49  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-07-01 14:11:50+01  ian_mayo
// extend test
//
// Revision 1.4  2003-06-03 16:25:32+01  ian_mayo
// Minor tidying, lots of testing
//
// Revision 1.3  2003-03-19 15:37:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 11:34:44+01  ian_mayo
// Check we're of the correct type
//
// Revision 1.1  2002-05-28 09:12:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-26 16:35:47+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-12 12:14:18+01  novatech
// Trim any leading whitespace from the narrative text
//
// Revision 1.1  2001-07-09 13:57:58+01  novatech
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
public final class ImportNarrative extends AbstractPlainLineImporter {


	/**
	 * the type for this string
	 */
	private final String _myType = ";NARRATIVE:";

	/**
	 * indicate if you can export this type of object
	 *
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	@Override
	public final boolean canExportThis(final Object val) {
		boolean res = false;

		if (val instanceof NarrativeEntry) {
			// right, we have our narrative.
			final NarrativeEntry ne = (NarrativeEntry) val;

			// does it have a type? if it does, it should be the type-2 narrative exporter
			// that's used
			if (ne.getType() == null)
				res = true;
		}

		return res;

	}

	/**
	 * export the specified shape as a string
	 *
	 * @return the shape in String form
	 * @param theWrapper the Shape we are exporting
	 */
	@Override
	public final String exportThis(final MWC.GUI.Plottable theWrapper) {
		final NarrativeEntry theEntry = (NarrativeEntry) theWrapper;

		String line = null;

		line = _myType;
		line = line + " " + DebriefFormatDateTime.toStringHiRes(theEntry.getDTG());

		// careful how we export it, in case it's a multi-word track name
		line = ImportFix.exportTrackName(theEntry.getTrackName(), line);

		line = line + " " + theEntry.getEntry();

		return line;
	}

	/**
	 * determine the identifier returning this type of annotation
	 */
	@Override
	public final String getYourType() {
		return _myType;
	}

	/**
	 * read in this string and return a Label
	 *
	 * @throws ParseException
	 */
	@Override
	public final Object readThisLine(final String theLine) throws ParseException {

		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		HiResDate DTG = null;
		String theTrack = null;
		String theEntry = null;

		// skip the comment identifier
		st.nextToken();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		DTG = DebriefFormatDateTime.parseThis(dateToken, timeToken);

		// now the track name
		theTrack = AbstractPlainLineImporter.checkForQuotedName(st);

		// and now read in the message
		theEntry = st.nextToken("\r").trim();

		// can we trim any leading whitespace?
		theEntry = theEntry.trim();

		final NarrativeEntry entry = new NarrativeEntry(theTrack, DTG, theEntry);

		return entry;
	}

}
