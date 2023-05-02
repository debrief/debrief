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

// $RCSfile: ImportLine.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportLine.java,v $
// Revision 1.3  2006/03/22 10:56:45  Ian.Mayo
// Reflect property name changes
//
// Revision 1.2  2005/12/13 09:04:36  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:48  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:40+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:15+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:37+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:31+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:13  ianmayo
// initial import of files
//
// Revision 1.8  2000-09-21 12:22:52+01  ian_mayo
// handle missing text string
//
// Revision 1.7  2000-08-15 08:58:12+01  ian_mayo
// reflect Bean name changes
//
// Revision 1.6  2000-02-22 13:49:20+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.5  1999-11-12 14:36:12+00  ian_mayo
// made them export aswell as import
//
// Revision 1.4  1999-11-11 18:21:35+00  ian_mayo
// format of ShapeWrapper changed
//
// Revision 1.3  1999-11-11 10:34:09+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.2  1999-10-14 12:00:38+01  ian_mayo
// created
//
//
//

package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * class to parse a label from a line of text
 */
final class ImportLine extends AbstractPlainLineImporter {
	public static WorldLocation extractStart(final StringTokenizer st) throws ParseException {
		WorldLocation start;
		double latDeg;
		double longDeg;
		double latMin;
		double longMin;
		char latHem;
		char longHem;
		double latSec;
		double longSec;
		latDeg = MWCXMLReader.readThisDouble(st.nextToken());
		latMin = MWCXMLReader.readThisDouble(st.nextToken());
		latSec = MWCXMLReader.readThisDouble(st.nextToken());
		/**
		 * now, we may have trouble here, since there may not be a space between the
		 * hemisphere character and a 3-digit latitude value - so BE CAREFUL
		 */
		final String vDiff = st.nextToken();
		if (vDiff.length() > 3) {
			// hmm, they are combined
			latHem = vDiff.charAt(0);
			final String secondPart = vDiff.substring(1, vDiff.length());
			longDeg = MWCXMLReader.readThisDouble(secondPart);
		} else {
			// they are separate, so only the hem is in this one
			latHem = vDiff.charAt(0);
			longDeg = MWCXMLReader.readThisDouble(st.nextToken());
		}
		longMin = MWCXMLReader.readThisDouble(st.nextToken());
		longSec = MWCXMLReader.readThisDouble(st.nextToken());
		longHem = st.nextToken().charAt(0);

		// we have our first location, create it
		start = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg, longMin, longSec, longHem, 0);
		return start;
	}

	/**
	 * the type for this string
	 */
	private final String _myType = ";LINE:";

	/**
	 * indicate if you can export this type of object
	 *
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	@Override
	public final boolean canExportThis(final Object val) {
		boolean res = false;

		if (val instanceof ShapeWrapper) {
			final ShapeWrapper sw = (ShapeWrapper) val;
			final PlainShape ps = sw.getShape();
			res = (ps.getClass() == LineShape.class);
		}

		return res;

	}

	/**
	 * export the specified shape as a string
	 *
	 * @return the shape in String form
	 * @param shape the Shape we are exporting
	 */
	@Override
	public final String exportThis(final MWC.GUI.Plottable theWrapper) {
		final ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		final LineShape Line = (LineShape) theShape.getShape();

		// result value
		String line;

		line = _myType + " " + ImportReplay.replaySymbolFor(Line.getColor(), null) + "  ";

		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(Line.getLine_Start());

		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(Line.getLineEnd());

		line = line + " " + theWrapper.getName();

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
	 */
	@Override
	public final Object readThisLine(final String theLine) {

		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		WorldLocation start, end;
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;

		// skip the comment identifier
		st.nextToken();

		// start with the symbology
		symbology = st.nextToken();

		// now the start location
		String vDiff;

		try {
			start = extractStart(st);
			// now the end location
			latDeg = Integer.parseInt(st.nextToken());
			latMin = Integer.parseInt(st.nextToken());
			latSec = MWCXMLReader.readThisDouble(st.nextToken());

			/**
			 * now, we may have trouble here, since there may not be a space between the
			 * hemisphere character and a 3-digit latitude value - so BE CAREFUL
			 */
			vDiff = st.nextToken();
			if (vDiff.length() > 3) {
				// hmm, they are combined
				latHem = vDiff.charAt(0);
				final String secondPart = vDiff.substring(1, vDiff.length());
				longDeg = Integer.parseInt(secondPart);
			} else {
				// they are separate, so only the hem is in this one
				latHem = vDiff.charAt(0);
				longDeg = Integer.parseInt(st.nextToken());
			}
			longMin = Integer.parseInt(st.nextToken());
			longSec = MWCXMLReader.readThisDouble(st.nextToken());
			longHem = st.nextToken().charAt(0);

			// we have our second location, create it
			end = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg, longMin, longSec, longHem, 0);

			String theText = "";
			// see if there are any more tokens waiting,
			if (st.hasMoreTokens()) {
				// and lastly read in the message
				theText = st.nextToken("\r").trim();
			}

			// create the Line object
			final PlainShape sp = new LineShape(start, end);
			final Color c = ImportReplay.replayColorFor(symbology);
			sp.setColor(c);

			final WorldArea tmp = new WorldArea(start, end);
			tmp.normalise();

			// and put it into a shape
			final ShapeWrapper sw = new ShapeWrapper(theText, sp, c, null);

			return sw;
		} catch (final ParseException pe) {
			MWC.Utilities.Errors.Trace.trace(pe, "Whilst import Line");
			return null;
		}
	}

}
