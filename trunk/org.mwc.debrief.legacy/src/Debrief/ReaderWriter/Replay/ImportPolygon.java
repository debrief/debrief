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
		throw new RuntimeException("Debrief does not support the import of Polygons in the Replay file format");
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

}
