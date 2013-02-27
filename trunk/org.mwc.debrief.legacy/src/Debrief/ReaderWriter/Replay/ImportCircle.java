// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportCircle.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ImportCircle.java,v $
// Revision 1.2  2005/12/13 09:04:34  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:44  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-19 15:37:42+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2003-02-21 11:13:34+00  ian_mayo
// Correct javadoc
//
// Revision 1.2  2002-05-28 12:28:12+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:33+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-27 09:58:11+00  administrator
// Corrected exporter
//
// Revision 1.0  2001-07-17 08:41:29+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:00  ianmayo
// initial import of files
//
// Revision 1.7  2000-08-15 08:58:13+01  ian_mayo
// reflect Bean name changes
//
// Revision 1.6  2000-02-22 13:49:22+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.5  1999-11-12 14:36:12+00  ian_mayo
// made them export aswell as import
//
// Revision 1.4  1999-11-11 18:21:36+00  ian_mayo
// format of ShapeWrapper changed
//
// Revision 1.3  1999-11-11 10:34:09+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.2  1999-10-14 12:03:19+01  ian_mayo
// improved
//
// Revision 1.1  1999-10-12 15:34:11+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-07-27 09:27:29+01  administrator
// added more error handlign
//
// Revision 1.1  1999-07-07 11:10:16+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-16 15:24:21+01  sm11td
// before move around/ end of phase 1
//
// Revision 1.1  1999-01-31 13:33:04+00  sm11td
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.PlainLineImporter;

/** class to parse a label from a line of text
 */
final class ImportCircle implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";CIRCLE:";
  
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
    double radius;
    String theText = null;
    String theSymbology;
    
    // skip the comment identifier
    st.nextToken();
    
    // start with the symbology
    theSymbology = st.nextToken();
    
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
    radius = Double.valueOf(st.nextToken()).doubleValue();
    
    
    // and now read in the message
	if (st.hasMoreTokens()) {
		theText = st.nextToken("\r");
		if (theText != null)
			theText = theText.trim();
	}
    
    // create the tactical data    
    theLoc = new WorldLocation(latDeg, latMin, latSec, latHem,
                               longDeg, longMin, longSec, longHem,
                               0);
  
    // create the circle object
    PlainShape sp = new CircleShape(theLoc, radius);
    sp.setColor(ImportReplay.replayColorFor(theSymbology));
    
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
	 * @param theWrapper the Shape we are exporting
	 */	
	public final String exportThis(MWC.GUI.Plottable theWrapper)
	{
		ShapeWrapper theShape = (ShapeWrapper) theWrapper;
		
		CircleShape circle = (CircleShape) theShape.getShape();
		
		// result value
		String line;
		
		line = _myType + " BD ";
							
		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(circle.getCentre());

		line = line + " " + circle.getRadius().getValueIn(WorldDistance.YARDS);
				
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
			res = (ps instanceof CircleShape);
		}
		
		return res;
	}
	
}

