// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportContact.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportContact.java,v $
// Revision 1.3  2005/12/13 09:04:34  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:15  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:45  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:31+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:11+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:34+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-26 16:34:24+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:29+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:04  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-09 13:37:41+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.1  2000-09-26 10:57:40+01  ian_mayo
// Initial revision
//
//

package Debrief.ReaderWriter.Replay;

import java.util.StringTokenizer;

import Debrief.Wrappers.ContactWrapper;
import MWC.Algorithms.Conversions;
import MWC.GenericData.*;
import MWC.TacticalData.Contact;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/** class to parse a label from a line of text
 */
final class ImportContact implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";VLCONTACT:";

  /** read in this string and return a Label
   */
	public final Object readThisLine(String theLine)
	{

		// ;VLCONTACT: @F CARPET 951212 113200.000 180 1000 145 5000 nb 1

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theText;
    String theSymbology;
		String theTrack;
		String dateStr;
		WorldVector sensorOffset;
		HiResDate theDtg= null;
		double offsetBrg, offsetRng;
		double brg, rng;

    // skip the comment identifier
    st.nextToken();

    // start with the symbology
    theSymbology = st.nextToken();

		// now the track name
    theTrack = st.nextToken();

		// now the dtg
    dateStr = st.nextToken();

		// append the time
		dateStr = dateStr + " " + st.nextToken();

		// and extract the date
		theDtg = DebriefFormatDateTime.parseThis(dateStr);
    
		// now the sensor offsets
    offsetBrg = Double.valueOf(st.nextToken()).doubleValue();
    offsetRng = Double.valueOf(st.nextToken()).doubleValue();
		sensorOffset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(offsetBrg),
																	 MWC.Algorithms.Conversions.Yds2Degs(offsetRng),
																	 0.0);

    brg = Double.valueOf(st.nextToken()).doubleValue();
    rng = Double.valueOf(st.nextToken()).doubleValue();

    // and lastly read in the message
    theText = st.nextToken("\r");

		// create the contact object
		MWC.TacticalData.Contact ct = new MWC.TacticalData.Contact(theTrack,
																															 theDtg,
																															 sensorOffset,
																															 rng,
																															 brg,
																															 null,
																															 theText);


    // and put it into a shape
		ContactWrapper sw = new ContactWrapper(ct,
																				 null);

		sw.setColor(ImportReplay.replayColorFor(theSymbology));

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
		ContactWrapper theContact = (ContactWrapper) theWrapper;
		Contact contact = theContact.getContact();

		// result value
		String line;

		// ;VLCONTACT: @F CARPET 951212 113200.000 180 1000 145 5000 nb 1
		line = _myType + " @F";

		// export the origin
		line += " " + contact.getTrackName();
		line += " " + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toStringHiRes(contact.getTime());
		line += " " + MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(Conversions.Rads2Degs(contact.getOffset().getBearing()));
		line += " " + MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(Conversions.Degs2Yds(contact.getOffset().getRange()));
		line += " " + MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(Conversions.Rads2Degs(contact.getBearing()));
		line += " " + MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(Conversions.Degs2Yds(contact.getRange()));
		line += " " + contact.getString();

		return line;

	}

	/** indicate if you can export this type of object
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(Object val)
	{
		boolean res = false;

		if(val instanceof ContactWrapper)
		{
			res = true;
		}

		return res;

	}

}
