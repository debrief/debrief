// Copyright MWC 1999, Debrief 3 Project
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
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

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
  public final Object readThisLine(String theLine)
  {

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

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
    dateStr = st.nextToken() + " " + st.nextToken();

    // and extract the date
    theDate = DebriefFormatDateTime.parseThis(dateStr);


    // combine the date, a space, and the time
    dateStr = st.nextToken() + " " + st.nextToken();

    // and extract the date
    theOtherDate = DebriefFormatDateTime.parseThis(dateStr);

    // now the location
    latDeg = Double.valueOf(st.nextToken());
    latMin = Double.valueOf(st.nextToken());
    latSec = Double.valueOf(st.nextToken()).doubleValue();

    /** now, we may have trouble here, since there may not be
     * a space between the hemisphere character and a 3-digit
     * latitude value - so BE CAREFUL
     */
    String vDiff = st.nextToken();
    if (vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      String secondPart = vDiff.substring(1, vDiff.length());
      longDeg = Double.valueOf(secondPart);
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

    String depthStr = st.nextToken();
    try
    {
      theDepth = Double.parseDouble(depthStr);
    }
    catch (NumberFormatException fe)
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
    LabelWrapper lw = new LabelWrapper(theText,
                                       theLoc,
                                       ImportReplay.replayColorFor(theSymbology),
                                       theDate,
                                       theOtherDate);

    return lw;
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
  public final String exportThis(MWC.GUI.Plottable theWrapper)
  {
    LabelWrapper theLabel = (LabelWrapper) theWrapper;

    String line = null;

    TimePeriod theTime = theLabel.getTimePeriod();

    line = _myType + " " + ImportReplay.replaySymbolFor(theLabel.getColor());
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
  public final boolean canExportThis(Object val)
  {
    boolean res = false;

    if (val instanceof LabelWrapper)
    {
      // also see if there is just the start time specified
      LabelWrapper lw = (LabelWrapper) val;

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








