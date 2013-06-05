// Copyright MWC 1999, Debrief 3 Project
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

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.TestCase;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.TextFormatting.GeneralFormat;

/**
 * class that is able to export a polygon - note the Replay file format doesn't include polygons, so we only export it.
 */
final class ImportPolygon implements PlainLineImporter
{

	/**
	 * the type for this string
	 */
	private final String _myType = ";;POLYGON:";

	@Override
	public final Object readThisLine(String theLine)
	{		
		// get a stream from the string
		StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;
		String theText = null;
		String theSymbology;

		// skip the comment identifier
		st.nextToken();

		// start with the symbology
		theSymbology = st.nextToken();
		
		// now the start date & time
		//TODO: read start date
		st.nextToken();
		st.nextToken();
		
		// now the end date & time
		//TODO: read end date
		st.nextToken();
		st.nextToken();
		
		Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		Integer counter = new Integer(1);
		// create the Polygon object
		PolygonShape sp = new PolygonShape(nodes);
		
		
		while (st.hasMoreTokens()) 
		{
			// meet the label
			String sts = st.nextToken();
			System.out.println(sts);
			if (Character.isLetter(sts.charAt(0))) {
				theText = sts;
				break;
			}
			
			// now the location
			latDeg = Double.valueOf(sts);
			latMin = Double.valueOf(st.nextToken());
			latSec = Double.valueOf(st.nextToken()).doubleValue();
		
			/**
			 * now, we may have trouble here, since there may not be a space between
			 * the hemisphere character and a 3-digit latitude value - so BE CAREFUL
			 */
			String vDiff = st.nextToken();
			if (vDiff.length() > 3) {
				// hmm, they are combined
				latHem = vDiff.charAt(0);
				String secondPart = vDiff.substring(1, vDiff.length());
				longDeg = Double.valueOf(secondPart);
			} else {
				// they are separate, so only the hem is in this one
				latHem = vDiff.charAt(0);
				longDeg = Double.valueOf(st.nextToken());
			}
			longMin = Double.valueOf(st.nextToken());
			longSec = Double.valueOf(st.nextToken()).doubleValue();
			longHem = st.nextToken().charAt(0);
			
			// we have our first location, create it
			WorldLocation theLoc = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg,
							longMin, longSec, longHem, 0);	
			PolygonNode newNode = new PolygonNode(counter.toString(), theLoc, sp);
			sp.add(newNode);
			
			counter += 1;
		}
		
		
				
		// and put Polygon into a shape		
		ShapeWrapper sw = new ShapeWrapper(theText, sp,
						ImportReplay.replayColorFor(theSymbology), null);

		return sw;
	}

	@Override
	public final String getYourType()
	{
		return _myType;
	}

	@Override
	public final String exportThis(MWC.GUI.Plottable theWrapper)
	{
		ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		PolygonShape polygon = (PolygonShape) theShape.getShape();

		// result value. Note - we're representing this as a Replay file comment marker - since
		// Debrief won't be able to import it.
		String line = ";;Debrief Polygon:" + polygon.getName();

		// remember how many pts we have
		int ctr = 1;
		
		// ok, start looping through them:
		Iterator<PolygonNode> pts = polygon.getPoints().iterator();
		while (pts.hasNext())
		{
			PolygonShape.PolygonNode node = (PolygonShape.PolygonNode) pts
					.next();
			// get the loc
			WorldLocation loc = node.getLocation();
			
			// convert to a string
			String str = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);
			
			// put in a leading newline
			line += GeneralFormat.LINE_SEPARATOR;
			
			// now our line
			line += ";;Point: " + ctr++ + " " + str;
			
		}

		return line;

	}

	@Override
	public final boolean canExportThis(Object val)
	{
		boolean res = false;

		if (val instanceof ShapeWrapper)
		{
			ShapeWrapper sw = (ShapeWrapper) val;
			PlainShape ps = sw.getShape();
			res = (ps instanceof PolygonShape);
		}

		return res;

	}
	
	public static class TestImport extends TestCase {
		public void testNoLabel() {
			String line = ";POLY: @@ 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
			ImportPolygon ip = new ImportPolygon();
			ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertNull(res.getLabel());
			assertNotNull("read it in", res);
			PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);
			
			Vector<PolygonNode> nodes = polygon.getPoints();
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
		
		public void testLeadingSpace() {
			String line = "	;POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
			ImportPolygon ip = new ImportPolygon();
			ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertNull(res.getLabel());
			assertNotNull("read it in", res);
			PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			Vector<PolygonNode> nodes = polygon.getPoints();
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
		
		public void testWithLabel() {
			String line = ";POLY: @J  49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E label";
			ImportPolygon ip = new ImportPolygon();
			ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
			assertNotNull(res.getLabel());
			assertNotNull("read it in", res);
			PolygonShape polygon = (PolygonShape) res.getShape();
			assertNotNull("found shape", polygon);

			Vector<PolygonNode> nodes = polygon.getPoints();
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
	}


}
