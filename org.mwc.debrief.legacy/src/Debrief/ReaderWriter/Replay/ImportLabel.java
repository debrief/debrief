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
// $RCSfile: ImportLabel.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ImportLabel.java,v $
// Revision 1.2  2005/12/13 09:04:35  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:47  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:31+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:47+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:36+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:30+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-24 11:36:57+00  novatech
// setString has changed to setLabel in label
//
// Revision 1.1  2001-01-03 13:40:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:10  ianmayo
// initial import of files
//
// Revision 1.4  2000-04-19 11:25:10+01  ian_mayo
// only white space
//
// Revision 1.3  2000-02-22 13:49:21+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.2  1999-11-12 14:36:12+00  ian_mayo
// made them export aswell as import
//
// Revision 1.1  1999-10-12 15:34:12+01  ian_mayo
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

import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.LabelWrapper;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/** class to parse a label from a line of text
 */
final class ImportLabel extends AbstractPlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";TEXT:";

  /** read in this string and return a Label
   */
  public final Object readThisLine(final String theLine){

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    WorldLocation theLoc;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    String theText;
    
    // skip the comment identifier
    st.nextToken();

    // start with the symbology
    symbology = st.nextToken();

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

	    // and now read in the message
	    theText = st.nextToken("\r").trim();
	
	    // create the tactical data
	    theLoc = new WorldLocation(latDeg, latMin, latSec, latHem,
	                               longDeg, longMin, longSec, longHem,
	                               0);
	
	    // create the fix ready to store it
	    final LabelWrapper lw = new LabelWrapper(theText,
	                                       theLoc,
	                                       ImportReplay.replayColorFor(symbology));
	    
	    // also get the symbol type
	    final String symType = ImportReplay.replayTrackSymbolFor(symbology);
	    lw.setSymbolType(symType);
	
	    return lw;
    }
    catch(final ParseException pe)
    {
    	MWC.Utilities.Errors.Trace.trace(pe,
				"Whilst import Label");
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
	 * @param shape the Shape we are exporting
	 */
	public final String exportThis(final MWC.GUI.Plottable theWrapper)
	{
		final LabelWrapper theLabel = (LabelWrapper) theWrapper;

    String line=null;

    // no, just output it as a dumb text label
    line = _myType;

    line = line + " " + ImportReplay.replaySymbolFor(theLabel.getColor(), theLabel.getSymbolType()) + " ";

    line = line + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(theLabel.getLocation());

    line = line + " " + theLabel.getLabel();

		return line;
	}


	/** indicate if you can export this type of object
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(final Object val)
	{
		boolean res = false;

		if(val instanceof LabelWrapper)
		{
			res = true;

      // just check if it has time data
		}

		return res;

	}

}








