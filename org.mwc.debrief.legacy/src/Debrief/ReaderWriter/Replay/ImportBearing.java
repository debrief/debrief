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

import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/** class to parse a label from a line of text
 */
final class ImportBearing extends AbstractPlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";BRG:";

  /** read in this string and return a Label
   * @throws ParseException 
   */
  public final Object readThisLine(final String theLine) throws ParseException{

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    WorldLocation start, end;
    double latDeg=0.0, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    String theText="";
    HiResDate theDate;


//;BRG: BD YYMMDD HHMMSS DD MM SS.SS H DD MM SS.SS H CCC XXXX xx.xx
//;; symb, date, time, lat, long, orientation, length (yards), label (one word)

    // skip the comment identifier
    st.nextToken();

    // start with the symbology
    symbology = st.nextToken();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // now the start location
	
	try 
    {    	
		latDeg = MWCXMLReader.readThisDouble(st.nextToken());
		latMin = MWCXMLReader.readThisDouble(st.nextToken());
	    latSec = MWCXMLReader.readThisDouble(st.nextToken());
	 
   
	    latMin = MWCXMLReader.readThisDouble(st.nextToken());
	    latSec = MWCXMLReader.readThisDouble(st.nextToken());
   
	    /** now, we may have trouble here, since there may not be
	     * a space between the hemisphere character and a 3-digit
	     * latitude value - so BE CAREFUL
	     */
	    final String vDiff = st.nextToken();
	    if(vDiff.length() > 3)
	    {
	      // hmm, they are combined
	      latHem = vDiff.charAt(0);
	      final String secondPart = vDiff.substring(1, vDiff.length());
	      longDeg  = MWCXMLReader.readThisDouble(secondPart);
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
	    start = new WorldLocation(latDeg, latMin, latSec, latHem,
	                           longDeg, longMin, longSec, longHem,
	                           0);



	    // now the end location
	    final double orient = MWCXMLReader.readThisDouble(st.nextToken());
	    final double length = MWCXMLReader.readThisDouble(st.nextToken());

	    // we have our second location, create it
	    // now create the offset
	    final WorldVector offset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(orient),
	                                         MWC.Algorithms.Conversions.Yds2Degs(length), 0);
	    end = start.add(offset);
	
	    // see if there are any more tokens waiting,
	    if(st.hasMoreTokens())
	    {
	      // and lastly read in the message
	      theText = st.nextToken("\r").trim();
	    }

	    // create the Line object
	    final PlainShape sp = new LineShape(start, end);
	    Color c = ImportReplay.replayColorFor(symbology);
	    sp.setColor(c);
	
	    final WorldArea tmp = new WorldArea(start, end);
	    tmp.normalise();
	
	    // and put it into a shape
	    final ShapeWrapper sw = new ShapeWrapper(theText,
	                                       sp,
	                                       c,
	                                       theDate);
	
	    return sw;
    }
	catch (final ParseException pe) 
	{
		MWC.Utilities.Errors.Trace.trace(pe,
				"Whilst import bearing");
		return null;
	}

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
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    final ShapeWrapper theShape = (ShapeWrapper) theWrapper;

    final LineShape Line = (LineShape) theShape.getShape();

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
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if(val instanceof ShapeWrapper)
    {
      final ShapeWrapper sw = (ShapeWrapper) val;
      final PlainShape ps = sw.getShape();
      res = (ps instanceof LineShape);
    }

    return res;

  }

}
