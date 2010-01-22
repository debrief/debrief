// Copyright MWC 1999, Debrief 3 Project
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

import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.PlainLineImporter;

/** class to parse a label from a line of text
 */
final class ImportLine implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";LINE:";
  
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
    
    // skip the comment identifier
    st.nextToken();
    
    // start with the symbology
    theSymbology = st.nextToken();
    
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
		end = new WorldLocation(latDeg, latMin, latSec, latHem, 
													 longDeg, longMin, longSec, longHem,
										       0);

		
    // see if there are any more tokens waiting,
    if(st.hasMoreTokens())
    {
      // and lastly read in the message
      theText = st.nextToken("\r");
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
