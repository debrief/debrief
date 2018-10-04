/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: ImportRectangle.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportRectangle.java,v $
// Revision 1.3  2006/03/22 10:56:46  Ian.Mayo
// Reflect property name changes
//
// Revision 1.2  2005/12/13 09:04:37  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:50  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:39+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:16+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-29 12:56:49+00  administrator
// Reflect renamed setBottomRight method to put editable properties in correct order
//
// Revision 1.0  2001-07-17 08:41:32+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:16  ianmayo
// initial import of files
//
// Revision 1.7  2000-08-15 08:58:12+01  ian_mayo
// reflect Bean name changes
//
// Revision 1.6  2000-02-22 13:49:20+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.5  1999-11-12 14:36:11+00  ian_mayo
// made them export aswell as import
//
// Revision 1.4  1999-11-11 18:21:35+00  ian_mayo
// format of ShapeWrapper changed
//
// Revision 1.3  1999-11-11 10:34:09+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.2  1999-10-13 17:23:24+01  ian_mayo
// now working
//
// Revision 1.1  1999-10-12 15:34:11+01  ian_mayo
// Initial revision
//
//

package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a rectangle from a line of text
 */
final class ImportDynamicRectangle extends AbstractPlainLineImporter {
	/**
	 * the type for this string
	 */
	private final String _myType = ";DYNAMIC_RECT:";

	/**
	 * read in this string and return a Label
	 * @throws ParseException 
	 */
	public final Object readThisLine(final String theLine) throws ParseException {

		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		WorldLocation TL, BR;
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;
		String theText = null;
		
		// skip the comment identifier
		st.nextToken();

		// start with the symbology
		symbology = st.nextToken();
		
		String theName = checkForQuotedName(st).trim();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		HiResDate theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

		try
		{
			// now the location
			latDeg = MWCXMLReader.readThisDouble(st.nextToken());
			latMin = MWCXMLReader.readThisDouble(st.nextToken());
			latSec = MWCXMLReader.readThisDouble(st.nextToken());

			/**
			 * now, we may have trouble here, since there may not be a space between
			 * the hemisphere character and a 3-digit latitude value - so BE CAREFUL
			 */
			String vDiff = st.nextToken();
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
			TL = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg,
					longMin, longSec, longHem, 0);
	
			// now the location
			latDeg = MWCXMLReader.readThisDouble(st.nextToken());
			latMin = MWCXMLReader.readThisDouble(st.nextToken());
			latSec = MWCXMLReader.readThisDouble(st.nextToken());

			/**
			 * now, we may have trouble here, since there may not be a space between
			 * the hemisphere character and a 3-digit latitude value - so BE CAREFUL
			 */
			vDiff = st.nextToken();
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

			// we have our second location, create it
			BR = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg,
					longMin, longSec, longHem, 0);
	
			// and lastly read in the message
			if (st.hasMoreTokens()) {
				theText = st.nextToken("\r");
				if (theText != null)
					theText = theText.trim();
			}
			// create the Rectangle object
			final PlainShape sp = new RectangleShape(TL, BR);
			Color c = ImportReplay.replayColorFor(symbology);
			sp.setColor(c);
	
			final WorldArea tmp = new WorldArea(TL, BR);
			tmp.normalise();
	
			// and put it into a shape
			final DynamicShapeWrapper sw = new DynamicShapeWrapper(theText, sp,
					c, theDate, theName);
	
			return sw;
		}
		catch(final ParseException pe)
		{
			MWC.Utilities.Errors.Trace.trace(pe,
					"Whilst import Rectangle");
			return null;
		}
	}

	/**
	 * determine the identifier returning this type of annotation
	 */
	public final String getYourType() {
		return _myType;
	}

	/**
	 * export the specified shape as a string
	 * 
	 * @return the shape in String form
	 * @param shape
	 *            the Shape we are exporting
	 */
	public final String exportThis(final MWC.GUI.Plottable theWrapper) {
		final ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		final RectangleShape Rectangle = (RectangleShape) theShape.getShape();

		// result value
		String line;

		line = _myType + " BD ";

		line = line
				+ " "
				+ MWC.Utilities.TextFormatting.DebriefFormatLocation
						.toString(Rectangle.getCorner_TopLeft());

		line = line
				+ " "
				+ MWC.Utilities.TextFormatting.DebriefFormatLocation
						.toString(Rectangle.getCornerBottomRight());

		return line;

	}

	/**
	 * indicate if you can export this type of object
	 * 
	 * @param val
	 *            the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(final Object val) {
		boolean res = false;

		if (val instanceof ShapeWrapper) {
			final ShapeWrapper sw = (ShapeWrapper) val;
			final PlainShape ps = sw.getShape();
			res = (ps instanceof RectangleShape);
		}

		return res;

	}

	public static class TestImport extends TestCase {
		
		public void testWithLabel() throws ParseException {
			final String line1 = ";RECT_TRACK: @J \"Dynamic A\" 951212 051000.000 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E label";
			final ImportDynamicRectangle ir = new ImportDynamicRectangle();
			final DynamicShapeWrapper res = (DynamicShapeWrapper) ir.readThisLine(line1);
			assertNotNull("read it in", res);
			assertNotNull(res.getLabel());
			final RectangleShape rect = (RectangleShape) res.getShape();
			assertNotNull("found shape", rect);
			assertEquals("correct tl lat", 49.7303, rect.getCorner_TopLeft().getLat(), 0.0001);
			assertEquals("Track name isn't correct", "Dynamic A", res.getTrackName());
			assertEquals("Start DTG isn't correct", 818745000000000l, res.getStartDTG().getMicros());
			assertEquals("correct tl long", 4.16989, rect.getCorner_TopLeft().getLong(), 0.0001);
			assertEquals("correct br long", 49.6405, rect.getCornerBottomRight().getLat(), 0.0001);
			assertEquals("correct br lat", 4.39945, rect.getCornerBottomRight().getLong(), 0.0001);
		}
	}

}
