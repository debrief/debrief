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
// $RCSfile: ImportEllipse.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportEllipse.java,v $
// Revision 1.3  2005/12/13 09:04:35  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:15  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:46  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-19 15:37:41+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2002-10-11 08:35:41+01  ian_mayo
// IntelliJ optimisations
//
// Revision 1.2  2002-05-28 12:28:14+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:34+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-03-13 08:56:36+00  administrator
// reflect name changes in ShapeWrapper class
//
// Revision 1.3  2002-02-26 16:35:05+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.2  2002-02-26 16:34:46+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.1  2002-01-17 20:21:31+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.0  2001-07-17 08:41:30+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:07  ianmayo
// initial import of files
//
// Revision 1.9  2000-10-09 13:37:40+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.8  2000-09-22 11:45:05+01  ian_mayo
// remove unnecesary units conversion
//
// Revision 1.7  2000-08-15 08:58:13+01  ian_mayo
// reflect Bean name changes
//
// Revision 1.6  2000-02-22 13:49:22+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.5  1999-11-12 14:36:11+00  ian_mayo
// made them export aswell as import
//
// Revision 1.4  1999-11-11 18:21:35+00  ian_mayo
// format of ShapeWrapper changed
//
// Revision 1.3  1999-11-11 10:34:08+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.2  1999-11-09 11:27:13+00  ian_mayo
// new file
//
//
package Debrief.ReaderWriter.Replay;

import java.text.ParseException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.TestCase;
import Debrief.Wrappers.PolygonWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class that is able to export a polygon - note the Replay file format doesn't
 * include polygons, so we only export it.
 */
class ImportPolygon extends AbstractPlainLineImporter
{

	/**
	 * the type for this string
	 */
	private final String _myType = ";POLY:";

	@Override
	public final Object readThisLine(final String theLine)
	{
		String line = theLine;
		// get a stream from the string
		StringTokenizer st = new StringTokenizer(line);

		// declare local variables
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;
		HiResDate startDate = null, endDate = null;
		String theText = null;

		// skip the comment identifier
		st.nextToken();

		// start with the symbology
		symbology = st.nextToken();

		String dateToken = st.nextToken();
		String timeToken = st.nextToken();
		if (hasStartDateOnly(line))
		{
			startDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		}
		else if (hasStartEndDates(line))
		{
			startDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

			dateToken = st.nextToken();
			timeToken = st.nextToken();
			endDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		}
		else
		{
			line = line.substring(line.indexOf(symbology));
			st = new StringTokenizer(line);
			st.nextToken(); // skip the simbology
		}

		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		Integer counter = new Integer(1);
		// create the Polygon object
		final PolygonShape sp = createShape(nodes);

		while (st.hasMoreTokens())
		{
			// meet the label
			final String sts = st.nextToken();

			if (Character.isDigit(sts.charAt(0)))
			{
				try
				{
					// now the location
					latDeg = MWCXMLReader.readThisDouble(sts);
					latMin = MWCXMLReader.readThisDouble(st.nextToken());
					latSec = MWCXMLReader.readThisDouble(st.nextToken());

					/**
					 * now, we may have trouble here, since there may not be a space
					 * between the hemisphere character and a 3-digit latitude value - so
					 * BE CAREFUL
					 */
					final String vDiff = st.nextToken();
					if (vDiff.length() > 3)
					{
						// hmm, they are combined
						latHem = vDiff.charAt(0);
						final String secondPart = vDiff.substring(1, vDiff.length());
						longDeg = MWCXMLReader.readThisDouble(secondPart);
					}
					else
					{
						// they are separate, so only the hem is in this one
						latHem = vDiff.charAt(0);
						longDeg = MWCXMLReader.readThisDouble(st.nextToken());
					}
					longMin = MWCXMLReader.readThisDouble(st.nextToken());
					longSec = MWCXMLReader.readThisDouble(st.nextToken());
					longHem = st.nextToken().charAt(0);

					// we have our first location, create it
					final WorldLocation theLoc = new WorldLocation(latDeg, latMin,
							latSec, latHem, longDeg, longMin, longSec, longHem, 0);
					final PolygonNode newNode = new PolygonNode(counter.toString(),
							theLoc, sp);
					sp.add(newNode);
				}
				catch (final ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe, "Whilst import Polygon");
					return null;
				}

				counter += 1;
			}
			else
			{
				theText = sts;
				if (st.hasMoreTokens())
				{
					// and lastly read in the message
					theText += st.nextToken("\r");
				}
			}
		}

		// and put Polygon into a shape
		final PolygonWrapper sw = new PolygonWrapper(theText, sp,
				ImportReplay.replayColorFor(symbology), startDate, endDate);
		
		// switch off the labels if it's coming from REP - since 
		// the user is probably confident in the locations
		sp.setShowNodeLabels(false);

		return sw;
	}

	private boolean hasStartEndDates(final String line)
	{
		return hasDate(line, 9);
	}

	private boolean hasStartDateOnly(final String line)
	{
		return hasDate(line, 7);
	}

	private boolean hasDate(final String line, final int skip)
	{
		StringTokenizer st = new StringTokenizer(line);
		for (int i = 0; i < skip; i++)
		{
			if (!st.hasMoreTokens())
				return false;
			st.nextToken();
		}
		final char longHem = st.nextToken().charAt(0);
		return 'N' == longHem || 'S' == longHem;
	}

	protected PolygonShape createShape(final Vector<PolygonNode> nodes)
	{
		return new PolygonShape(nodes);
	}

	@Override
	public String getYourType()
	{
		return _myType;
	}

	@Override
	public final String exportThis(final MWC.GUI.Plottable theWrapper)
	{
		final ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		final PolygonShape polygon = (PolygonShape) theShape.getShape();

		String line = getYourType();
		line += " " + ImportReplay.replaySymbolFor(polygon.getColor(), null);

		// ok, start looping through them:
		final Iterator<PolygonNode> pts = polygon.getPoints().iterator();
		while (pts.hasNext())
		{
			final PolygonShape.PolygonNode node = (PolygonShape.PolygonNode) pts
					.next();
			// get the loc
			final WorldLocation loc = node.getLocation();

			// convert to a string
			final String str = MWC.Utilities.TextFormatting.DebriefFormatLocation
					.toString(loc);

			// now our line
			line += " " + str;

		}

		return line + " " + polygon.getName();

	}

	@Override
	public final boolean canExportThis(final Object val)
	{
		if (val instanceof ShapeWrapper)
		{
			final ShapeWrapper sw = (ShapeWrapper) val;
			final PlainShape ps = sw.getShape();
			if (ps instanceof PolygonShape)
				return canExport((PolygonShape) ps);
		}

		return false;

	}

	protected boolean canExport(final PolygonShape ps)
	{
		return ps.getClosed();
	}

	public static class TestImport extends TestCase
	{

		public void testNoLabel()
		{
			final String line = ";POLY: @@ 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertEquals("", res.getLabel());
			assertNotNull("read it in", res);
			final PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			final Vector<PolygonNode> nodes = polygon.getPoints();
			assertEquals(2, nodes.size());

			assertEquals("1", nodes.get(0).getName());
			WorldLocation loc = nodes.get(0).getLocation();
			assertEquals("correct lat", 49.7303, loc.getLat(), 0.0001);
			assertEquals("correct long", 4.16989, loc.getLong(), 0.0001);

			assertEquals("2", nodes.get(1).getName());
			loc = nodes.get(1).getLocation();
			assertEquals("correct long", 49.6405, loc.getLat(), 0.0001);
			assertEquals("correct lat", 4.39945, loc.getLong(), 0.0001);
		}

		public void testLeadingSpace()
		{
			final String line = "    	;POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E 49.7303 0 0 N 4.16989 0 0 E";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertEquals("", res.getLabel());
			assertNotNull("read it in", res);
			final PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			final Vector<PolygonNode> nodes = polygon.getPoints();
			assertEquals(3, nodes.size());

			assertEquals("1", nodes.get(0).getName());
			WorldLocation loc = nodes.get(0).getLocation();
			assertEquals("correct lat", 49.7303, loc.getLat(), 0.0001);
			assertEquals("correct long", 4.16989, loc.getLong(), 0.0001);

			assertEquals("2", nodes.get(1).getName());
			loc = nodes.get(1).getLocation();
			assertEquals("correct long", 49.6405, loc.getLat(), 0.0001);
			assertEquals("correct lat", 4.39945, loc.getLong(), 0.0001);

			assertEquals("3", nodes.get(2).getName());
			loc = nodes.get(2).getLocation();
			assertEquals("correct lat", 49.7303, loc.getLat(), 0.0001);
			assertEquals("correct long", 4.16989, loc.getLong(), 0.0001);
		}

		public void testWithLabel()
		{
			final String line = " ;POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E label";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertEquals("label", res.getLabel());
			assertNotNull("read it in", res);
			final PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			final Vector<PolygonNode> nodes = polygon.getPoints();
			assertEquals(2, nodes.size());

			assertEquals("1", nodes.get(0).getName());
			WorldLocation loc = nodes.get(0).getLocation();
			assertEquals("1 correct lat", 49.7303, loc.getLat(), 0.0001);
			assertEquals("1 correct long", 4.16989, loc.getLong(), 0.0001);

			assertEquals("2", nodes.get(1).getName());
			loc = nodes.get(1).getLocation();
			assertEquals("2 correct long", 49.6405, loc.getLat(), 0.0001);
			assertEquals("2 correct lat", 4.39945, loc.getLong(), 0.0001);
		}

		public void testWithCompoundLabel()
		{
			final String line = ";POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E test long label";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertEquals("test long label", res.getLabel());
		}

		public void testWithoutEndDate()
		{
			final String line = ";POLY: @@ 120505 120505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertEquals("", res.getLabel());
			assertNotNull("read it in", res);
			final PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			final Vector<PolygonNode> nodes = polygon.getPoints();
			assertEquals(2, nodes.size());

			assertEquals("1", nodes.get(0).getName());
			WorldLocation loc = nodes.get(0).getLocation();
			assertEquals("correct lat", 49.7303, loc.getLat(), 0.0001);
			assertEquals("correct long", 4.16989, loc.getLong(), 0.0001);

			assertEquals("2", nodes.get(1).getName());
			loc = nodes.get(1).getLocation();
			assertEquals("correct long", 49.6405, loc.getLat(), 0.0001);
			assertEquals("correct lat", 4.39945, loc.getLong(), 0.0001);
		}

		public void testWithoutDates()
		{
			final String line = ";POLY: @@ 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertEquals("", res.getLabel());
			assertNotNull("read it in", res);
			final PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			final Vector<PolygonNode> nodes = polygon.getPoints();
			assertEquals(2, nodes.size());

			assertEquals("1", nodes.get(0).getName());
			WorldLocation loc = nodes.get(0).getLocation();
			assertEquals("correct lat", 49.7303, loc.getLat(), 0.0001);
			assertEquals("correct long", 4.16989, loc.getLong(), 0.0001);

			assertEquals("2", nodes.get(1).getName());
			loc = nodes.get(1).getLocation();
			assertEquals("correct long", 49.6405, loc.getLat(), 0.0001);
			assertEquals("correct lat", 4.39945, loc.getLong(), 0.0001);
		}

		public void testExport()
		{
			final String line = ";POLY: @@ 49 43 49.08 N 004 10 11.60 E 49 38 25.80 N 004 23 58.02 E label";
			final ImportPolygon ip = new ImportPolygon();
			final ShapeWrapper sw = (ShapeWrapper) ip.readThisLine(line);
			assertEquals(line, ip.exportThis(sw));
		}
	}

}
