/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: ImportPeriodText.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportPeriodText.java,v $
// Revision 1.3  2005/12/13 09:04:37  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:17  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:50  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:39+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:46+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:38+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-02-26 16:38:15+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.1  2002-02-26 16:36:11+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:31+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 11:36:57+00  novatech
// setString has changed to setLabel in label
//
// Revision 1.2  2001-01-17 13:23:46+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:13  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-09 13:37:37+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.1  2000-09-26 10:57:45+01  ian_mayo
// Initial revision
//


package Debrief.ReaderWriter.Replay;

import Debrief.Wrappers.LabelWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * class to parse a label from a line of text
 */
final class ImportPeriodText implements PlainLineImporter
{
  /**
   * the type for this string
   */
  private final String _myType = ";PERIODTEXT:";

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
    String theText = null;
    String theSymbology;
    String dateStr;
    HiResDate theDate = null;
    HiResDate theOtherDate = null;
    double theDepth = 0d;

    // skip the comment identifier
    st.nextToken();

    // start with the symbology
    theSymbology = st.nextToken();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		
    // combine the date, a space, and the time
    dateStr = st.nextToken() + " " + st.nextToken();

    // and extract the date
    theOtherDate = DebriefFormatDateTime.parseThis(dateStr);

    // now the location
    try
    {
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

	    final String depthStr = st.nextToken();
	    try
	    {
	      theDepth = MWCXMLReader.readThisDouble(depthStr);
	    }
	    catch (final ParseException e)
	    {
	      // hey, it didn't contain a double, just use it as a text string
	      theText = depthStr;
	    }

	    if (st.hasMoreTokens())
	    {
	      // if we haven't read in part of the message already, read in the remainder
	      if (theText != null)
	      {
	        // and now read in the remainder of the line, and append to the message
	        theText += " " + st.nextToken("\r").trim();
	      }
	      else
	      {
	        // and now read in the message, we have already read the depth
	        theText = st.nextToken("\r").trim();
	      }
	    }

	    // create the tactical data
	    theLoc = new WorldLocation(latDeg, latMin, latSec, latHem,
	                               longDeg, longMin, longSec, longHem,
	                               theDepth);

	    // create the fix ready to store it
	    final LabelWrapper lw = new LabelWrapper(theText,
	                                       theLoc,
	                                       ImportReplay.replayColorFor(theSymbology),
	                                       theDate,
	                                       theOtherDate);
	
	    return lw;
    }
    catch(final ParseException pe)
    {
    	MWC.Utilities.Errors.Trace.trace(pe,
				"Whilst import PeriodText");
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
    final LabelWrapper theLabel = (LabelWrapper) theWrapper;

    String line = null;

    final TimePeriod theTime = theLabel.getTimePeriod();

    line = _myType + " " + ImportReplay.replaySymbolFor(theLabel.getColor(), theLabel.getSymbolType());
    line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toStringHiRes(theTime.getStartDTG());
    line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toStringHiRes(theTime.getEndDTG());
    line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(theLabel.getLocation());

    line = line + " " + theLabel.getLabel();

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

    if (val instanceof LabelWrapper)
    {
      // also see if there is just the start time specified
      final LabelWrapper lw = (LabelWrapper) val;

      // does it have a time period?
      if ((lw.getTimePeriod() != null) && (lw.getTimePeriod().getStartDTG() != null))
      {
        // yes, this is a label with only the start time specified,
        // we can export it
        res = true;
      }
    }

    return res;

  }

}








