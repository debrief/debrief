// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportBearing.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: ImportBearing.java,v $
// Revision 1.5  2006/04/05 09:12:42  Ian.Mayo
// Minor tidying
//
// Revision 1.4  2006/03/22 10:56:45  Ian.Mayo
// Reflect property name changes
//
// Revision 1.3  2005/12/13 09:04:34  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:14  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:43  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:32+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:13+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:32+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-26 16:33:03+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:29+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:43+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:00  ianmayo
// initial import of files
//
// Revision 1.2  2000-11-03 12:08:17+00  ian_mayo
// new class
//
// Revision 1.1  2000-11-03 11:07:53+00  ian_mayo
// Initial revision
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

import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/** class to parse a label from a line of text
 */
final class ImportBearing implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";BRG:";

  /** read in this string and return a Label
   */
  public final Object readThisLine(String theLine){

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    WorldLocation start, end;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    String theText="";
    String theSymbology;
    HiResDate theDate;


//;BRG: BD YYMMDD HHMMSS DD MM SS.SS H DD MM SS.SS H CCC XXXX xx.xx
//;; symb, date, time, lat, long, orientation, length (yards), label (one word)

    // skip the comment identifier
    st.nextToken();

    // start with the symbology
    theSymbology = st.nextToken();

		// combine the date, a space, and the time
		String dateToken = st.nextToken();
		String timeToken = st.nextToken();

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // now the start location
    latDeg = Double.valueOf(st.nextToken());
    latMin = Double.valueOf(st.nextToken());
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
      longDeg  = Double.valueOf(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Double.valueOf(st.nextToken());
    }
    longMin = Double.valueOf(st.nextToken());
    longSec = Double.valueOf(st.nextToken()).doubleValue();
    longHem = st.nextToken().charAt(0);

    // we have our first location, create it
    start = new WorldLocation(latDeg, latMin, latSec, latHem,
                           longDeg, longMin, longSec, longHem,
                           0);



    // now the end location
    double orient =Double.valueOf(st.nextToken()).doubleValue();
    double length = Double.valueOf(st.nextToken()).doubleValue();

    // we have our second location, create it
    // now create the offset
    WorldVector offset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(orient),
                                         MWC.Algorithms.Conversions.Yds2Degs(length), 0);
    end = start.add(offset);

    // see if there are any more tokens waiting,
    if(st.hasMoreTokens())
    {
      // and lastly read in the message
      theText = st.nextToken("\r").trim();
    }

    // create the Line object
    PlainShape sp = new LineShape(start, end);
    sp.setColor(ImportReplay.replayColorFor(theSymbology));

    WorldArea tmp = new WorldArea(start, end);
    tmp.normalise();

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

    LineShape Line = (LineShape) theShape.getShape();

    // result value
    String line;

    line = _myType + " BD ";

    line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(Line.getLine_Start());

    line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(Line.getLineEnd());

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
      res = (ps instanceof LineShape);
    }

    return res;

  }

}
