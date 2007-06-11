// Copyright MWC 1999, Debrief 3 Project
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

import MWC.Utilities.ReaderWriter.*;
import Debrief.Wrappers.*;
import MWC.GUI.Shapes.*;
import java.util.*;
import MWC.GenericData.*;

/** class to parse a label from a line of text
 */
final class ImportRectangle implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";RECT:";

  /** read in this string and return a Label
   */
  public final Object readThisLine(String theLine){

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    WorldLocation TL, BR;
    int latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    String theText;
    String theSymbology;

    // skip the comment identifier
    st.nextToken();

    // start with the symbology
    theSymbology = st.nextToken();

    // now the location
    latDeg = Integer.parseInt(st.nextToken());
    latMin = Integer.parseInt(st.nextToken());
    latSec = Double.valueOf(st.nextToken()).doubleValue();

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
      longDeg  = Integer.parseInt(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Integer.parseInt(st.nextToken());
    }
    longMin = Integer.parseInt(st.nextToken());
    longSec = Double.valueOf(st.nextToken()).doubleValue();
    longHem = st.nextToken().charAt(0);

		// we have our first location, create it
		TL = new WorldLocation(latDeg, latMin, latSec, latHem,
													 longDeg, longMin, longSec, longHem,
										       0);

    // now the location
    latDeg = Integer.parseInt(st.nextToken());
    latMin = Integer.parseInt(st.nextToken());
    latSec = Double.valueOf(st.nextToken()).doubleValue();

    /** now, we may have trouble here, since there may not be
     * a space between the hemisphere character and a 3-digit
     * latitude value - so BE CAREFUL
     */
    vDiff = st.nextToken();
    if(vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      String secondPart = vDiff.substring(1, vDiff.length());
      longDeg  = Integer.parseInt(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Integer.parseInt(st.nextToken());
    }
    longMin = Integer.parseInt(st.nextToken());
    longSec = Double.valueOf(st.nextToken()).doubleValue();
    longHem = st.nextToken().charAt(0);

		// we have our second location, create it
		BR = new WorldLocation(latDeg, latMin, latSec, latHem,
													 longDeg, longMin, longSec, longHem,
										       0);


    // and lastly read in the message
    theText = st.nextToken("\r");

    // create the Rectangle object
    PlainShape sp = new RectangleShape(TL, BR);
    sp.setColor(ImportReplay.replayColorFor(theSymbology));

		WorldArea tmp = new WorldArea(TL, BR);
		tmp.normalise();

    // and put it into a shape
    ShapeWrapper sw = new ShapeWrapper(theText,
                                       sp,
                                       ImportReplay.replayColorFor(theSymbology),
																			 null);

    return sw;
  }

  /** determine the identifier returning this type of annotation
   */
  public final String getYourType(){
    return _myType;
  }

	/** export the specified shape as a string
	 * @return the shape in String form
	 * @param shape the Shape we are exporting
	 */
	public final String exportThis(MWC.GUI.Plottable theWrapper)
	{
		ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		RectangleShape Rectangle = (RectangleShape) theShape.getShape();

		// result value
		String line;

		line = _myType + " BD ";

		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(Rectangle.getCorner_TopLeft());

		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(Rectangle.getCornerBottomRight());

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
			res = (ps instanceof RectangleShape);
		}

		return res;

	}

}
