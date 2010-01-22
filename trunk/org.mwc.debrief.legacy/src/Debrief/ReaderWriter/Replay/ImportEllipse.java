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

import MWC.Utilities.ReaderWriter.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import Debrief.Wrappers.*;
import MWC.GUI.Shapes.*;
import java.util.*;
import MWC.GenericData.*;
import MWC.Algorithms.*;

/** class to parse a label from a line of text
 */
final class ImportEllipse implements PlainLineImporter
{

  /** the type for this string
   */
  private final String _myType = ";ELLIPSE:";

  /** read in this string and return a Label
   */
  public final Object readThisLine(String theLine){

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    WorldLocation theLoc;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    double maxima, minima, orient;
    String theText;
    String theSymbology;
		HiResDate theDate= null;

    // skip the comment identifier
    st.nextToken();

		// start with the symbology
    theSymbology = st.nextToken();

    // now the date
    String dateStr=null;

    // get date
    dateStr = st.nextToken();

		// now get the time, and add it to the date
		dateStr = dateStr + " " + st.nextToken();

    // produce a date from this data
		theDate = DebriefFormatDateTime.parseThis(dateStr);

    // now the location
    latDeg = Double.valueOf(st.nextToken());
    latMin = Double.valueOf(st.nextToken());
    latSec = Double.valueOf(st.nextToken());

    /** now, we may have trouble here, since there may not be
     * a space between the hemisphere character and a 3-digit
     * latitude value - so BE CAREFUL
     */
    String vDiff = st.nextToken();
    if(vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      String secondPart = vDiff.substring(1, vDiff.length());
      longDeg  = Double.valueOf(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Double.valueOf(st.nextToken());
    }
    longMin = Double.valueOf(st.nextToken());
    longSec = Double.valueOf(st.nextToken());
    longHem = st.nextToken().charAt(0);

    // now the radius of the circle
    orient = Double.valueOf(st.nextToken()).doubleValue();
    maxima = Conversions.Yds2Degs(Double.valueOf(st.nextToken()).doubleValue());
    minima = Conversions.Yds2Degs(Double.valueOf(st.nextToken()).doubleValue());

    // and now read in the message
    theText = st.nextToken("\r");

    // create the tactical data
    theLoc = new WorldLocation(latDeg, latMin, latSec, latHem,
                               longDeg, longMin, longSec, longHem,
                               0);

    // create the circle object
    PlainShape sp = new EllipseShape(theLoc, orient, maxima, minima);
    sp.setColor(ImportReplay.replayColorFor(theSymbology));

    // and put it into a shape
    ShapeWrapper sw = new ShapeWrapper(theText,
                                       sp,
                                       ImportReplay.replayColorFor(theSymbology),
																			 theDate);

    return sw;
  }

  /** determine the identifier returning this type of annotation
   */
  public final String getYourType(){
    return _myType;
  }

	/** export the specified shape as a string
	 * @return the shape in String form
	 * @param theWrapper the Shape we are exporting
	 */
	public final String exportThis(MWC.GUI.Plottable theWrapper)
	{
		ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		EllipseShape ellipse = (EllipseShape) theShape.getShape();

		// result value
		String line;

		line = ";ELLIPSE: BD ";

		HiResDate tmpDate = theShape.getStartDTG();

		if (tmpDate == null)
		{
			tmpDate = new HiResDate(new Date());
		}

		line = line + " " + ImportReplay.formatThis(tmpDate);




		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(ellipse.getCentre());

		line = line + " " + ellipse.getOrientation();

		line = line + " " + (long)(ellipse.getMaxima().getValueIn(WorldDistance.YARDS));

		line = line + " " + (long)(ellipse.getMinima().getValueIn(WorldDistance.YARDS));

		line = line + " " + theShape.getLabel();

		return line;

	}



	/** indicate if you can export this type of object
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(Object val)
	{
		boolean res = false;

		if(val instanceof ShapeWrapper)
		{
			ShapeWrapper sw = (ShapeWrapper) val;
			PlainShape ps = sw.getShape();
			res = (ps instanceof EllipseShape);
		}

		return res;

	}

}

