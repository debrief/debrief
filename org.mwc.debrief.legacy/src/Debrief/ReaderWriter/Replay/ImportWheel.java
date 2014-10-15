/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: ImportWheel.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportWheel.java,v $
// Revision 1.3  2005/12/13 09:04:40  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:21  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:55  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:38+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:18+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:42+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-13 08:56:35+00  administrator
// reflect name changes in ShapeWrapper class
//
// Revision 1.2  2002-02-26 16:37:15+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.1  2002-01-17 20:21:30+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.0  2001-07-17 08:41:32+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 13:23:44+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:27  ianmayo
// initial import of files
//
// Revision 1.3  2000-10-09 13:37:34+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.2  2000-10-03 14:18:38+01  ian_mayo
// implement class
//
//
//
package Debrief.ReaderWriter.Replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
final class ImportWheel implements PlainLineImporter
{

  /**
   * the type for this string
   */
  private final String _myType = ";WHEEL:";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(final String theLine)
  {

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    WorldLocation theLoc;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    double innerRadius, outerRadius;
    String theText;
    String theSymbology;
    String scrap;
    HiResDate theDate = null;

    // skip the comment identifier
    scrap = st.nextToken();
    if(scrap != null)
    	scrap = null;

    // start with the symbology
    theSymbology = st.nextToken();

    // now the date
    String dateStr = null;

    // get date
    dateStr = st.nextToken();

    // now get the time, and add it to the date
    dateStr = dateStr + " " + st.nextToken();

    // produce a date from this data
    theDate = DebriefFormatDateTime.parseThis(dateStr);

    try
    {
    	// now the location
    	latDeg = MWCXMLReader.readThisDouble(st.nextToken());
    	latMin = MWCXMLReader.readThisDouble(st.nextToken());
    	latSec = MWCXMLReader.readThisDouble(st.nextToken());

	    /** now, we may have trouble here, since there may not be
	     * a space between the hemisphere character and a 3-digit
	     * latitude value - so BE CAREFUL
	     */
	    final String vDiff = st.nextToken();
	    if (vDiff.length() > 3)
	    {
	      // hmm, they are combined
	      latHem = vDiff.charAt(0);
	      final String secondPart = vDiff.substring(1, vDiff.length());
	      longDeg =MWCXMLReader.readThisDouble(secondPart);
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

	    // now the radius of the circle
	    innerRadius = MWCXMLReader.readThisDouble(st.nextToken());
	    outerRadius = MWCXMLReader.readThisDouble(st.nextToken());
	
	
	    // and now read in the message
	    theText = st.nextToken("\r").trim();

	    // create the tactical data
	    theLoc = new WorldLocation(latDeg, latMin, latSec, latHem,
	                               longDeg, longMin, longSec, longHem,
	                               0);
	
	    // create the circle object
	    final PlainShape wh = new WheelShape(theLoc, innerRadius, outerRadius);
	    wh.setColor(ImportReplay.replayColorFor(theSymbology));
	
	    // and put it into a shape
	    final ShapeWrapper sw = new ShapeWrapper(theText,
	                                       wh,
	                                       ImportReplay.replayColorFor(theSymbology),
	                                       theDate);
	
	    return sw;
    }
    catch(final ParseException pe)
    {
    	MWC.Utilities.Errors.Trace.trace(pe,
				"Whilst import Wheel");
    	return null;
    }
  }

  /**
   * determine the identifier returning this type of annotation
   */
  public final String getYourType()
  {
    return _myType;
  }

  /**
   * export the specified shape as a string
   *
   * @param theWrapper the Shape we are exporting
   * @return the shape in String form
   */
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    final ShapeWrapper theShape = (ShapeWrapper) theWrapper;

    final EllipseShape ellipse = (EllipseShape) theShape.getShape();

    // result value
    String line;

    line = ";WHEEL: BD ";

    HiResDate tmpDate = theShape.getStartDTG();

    if (tmpDate == null)
    {
      tmpDate = new HiResDate(0);
    }

    line = line + " " + ImportReplay.formatThis(tmpDate);

    line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(ellipse.getCentre());

    line = line + " " + ellipse.getOrientation();

    line = line + " " + (long) (ellipse.getMaxima().getValueIn(WorldDistance.YARDS));

    line = line + " " + (long) (ellipse.getMinima().getValueIn(WorldDistance.YARDS));

    line = line + " " + theShape.getLabel();

    return line;

  }


  /**
   * indicate if you can export this type of object
   *
   * @param val the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if (val instanceof ShapeWrapper)
    {
      final ShapeWrapper sw = (ShapeWrapper) val;
      final PlainShape ps = sw.getShape();
      res = (ps instanceof EllipseShape);
    }

    return res;

  }

}

