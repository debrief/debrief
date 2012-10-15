// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportFix.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ImportFix.java,v $
// Revision 1.7  2004/11/25 10:24:16  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.6  2004/10/13 07:39:30  Ian.Mayo
// Correctly export duff (NaN) depths for tracks
//
// Revision 1.5  2004/08/31 11:02:35  Ian.Mayo
// Fix where track name is only one character long
//
// Revision 1.4  2004/08/26 13:09:15  Ian.Mayo
// Fix problem where quoted track name only contains single word (no spaces)
//
// Revision 1.3  2004/08/20 08:18:03  Ian.Mayo
// Allow 4-figure dates in REP files
//
// Revision 1.2  2004/08/19 14:12:45  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.1.1.2  2003/07/21 14:47:47  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-19 15:37:41+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2003-01-17 15:07:12+00  ian_mayo
// Handle NaN in depth data
//
// Revision 1.2  2002-05-28 12:28:13+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:35+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-02-26 16:35:29+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.1  2001-08-24 16:35:17+01  administrator
// Keep the strings tidy
//
// Revision 1.0  2001-07-17 08:41:30+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:10  ianmayo
// initial import of files
//
// Revision 1.11  2000-11-24 11:50:37+00  ian_mayo
// remove unnecessary comments
//
// Revision 1.10  2000-11-08 11:49:04+00  ian_mayo
// tidying up
//
// Revision 1.9  2000-11-03 12:09:14+00  ian_mayo
// tidy implementation, correct units when exporting Speed parameter
//
// Revision 1.8  2000-10-09 13:37:39+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.7  2000-08-14 11:01:13+01  ian_mayo
// we don't need to perform units conversion
//
// Revision 1.6  2000-03-17 13:37:41+00  ian_mayo
// Export fixes
//
// Revision 1.5  2000-03-14 09:49:15+00  ian_mayo
// assign icon names to tools
//
// Revision 1.4  2000-02-22 13:49:21+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.3  2000-02-02 14:26:54+00  ian_mayo
// minor reformatting
//
// Revision 1.2  1999-11-12 14:36:10+00  ian_mayo
// made them export aswell as import
//
// Revision 1.1  1999-10-12 15:34:11+01  ian_mayo
// Initial revision
//
// Revision 1.4  1999-08-04 09:45:32+01  administrator
// minor mods, tidying up
//
// Revision 1.3  1999-07-27 09:27:28+01  administrator
// added more error handlign
//
// Revision 1.2  1999-07-19 12:39:43+01  administrator
// Added painting to a metafile
//
// Revision 1.1  1999-07-07 11:10:16+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-16 15:24:22+01  sm11td
// before move around/ end of phase 1
//
// Revision 1.3  1999-06-01 16:49:18+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-01 16:08:47+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:03+00  sm11td
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * import a fix from a line of text (in Replay format)
 */
public final class ImportFix implements PlainLineImporter
{

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	/**
	 * use static strings to reduce initialisation times
	 */
	static private String dateStr;

	/**
	 * the type for this string
	 */
	private final String _myType = " ";

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	/**
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
		HiResDate theDate = null;
		double theCourse;
		double theSpeed;
		double theDepth;

		String theTrackName;
		String theSymbology;

		// dateStr = new StringBuffer(100);

		// parse the line
		// 951212 050000.000 CARPET @C 12 11 10.63 N 11 41 52.37 W 269.7 2.0 0

		// first the date

		// combine the date, a space, and the time
		String dateToken = st.nextToken();
		String timeToken = st.nextToken();

		// check the date for missing leading zeros
		dateToken = String.format("%06d", Integer.parseInt(dateToken));

		// check the time (Excel may have omitted leading zeros)

		// do we have millis?
		int decPoint = timeToken.indexOf(".");
		String milliStr, timeStr;
		if (decPoint > 0)
		{
			milliStr = timeToken.substring(decPoint, timeToken.length());
			timeStr = timeToken.substring(0, decPoint);
		}
		else
		{
			milliStr = "";
			timeStr = timeToken;
		}

		// left-pad the time string
		timeStr = String.format("%06d", Integer.parseInt(timeStr));
		timeStr = timeStr + milliStr;

		// ok, all done.

		dateStr = dateToken + " " + timeStr;

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateStr);

		// trouble - the track name may have been quoted, in which case we will
		// pull
		// in the remaining fields aswell
		theTrackName = checkForQuotedTrackName(st);

		// trim the track name, just in case
		theTrackName.trim();

		theSymbology = st.nextToken(normalDelimiters);

		latDeg = Double.valueOf(st.nextToken());
		latMin = Double.valueOf(st.nextToken());
		latSec = Double.valueOf(st.nextToken());

		/**
		 * now, we may have trouble here, since there may not be a space between the
		 * hemisphere character and a 3-digit latitude value - so BE CAREFUL
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
		longSec = Double.valueOf(st.nextToken());
		longHem = st.nextToken().charAt(0);

		// parse (and convert) the vessel status parameters
		theCourse = MWC.Algorithms.Conversions.Degs2Rads(Double.valueOf(
				st.nextToken()).doubleValue());
		theSpeed = MWC.Algorithms.Conversions.Kts2Yps(Double
				.valueOf(st.nextToken()).doubleValue());

		// get the depth value
		String depthStr = st.nextToken();

		// we know that the Depth str may be NaN, but Java can interpret this
		// directly
		if (depthStr.equals("NaN"))
			theDepth = Double.NaN;
		else
			theDepth = Double.valueOf(depthStr).doubleValue();

		// NEW FEATURE: we take any remaining text, and use it as a label
		String txtLabel = null;
		if (st.hasMoreTokens())
			txtLabel = st.nextToken("\r");
		if (txtLabel != null)
			txtLabel = txtLabel.trim();

		// create the tactical data
		theLoc = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg,
				longMin, longSec, longHem, theDepth);

		// create the fix ready to store it
		Fix res = new Fix(theDate, theLoc, theCourse, theSpeed);
		ReplayFix rf = new ReplayFix();
		rf.theFix = res;
		rf.theTrackName = theTrackName;
		rf.theSymbology = theSymbology;
		if ((txtLabel != null) && (txtLabel.length() > 0))
			rf.label = txtLabel;

		return rf;
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
	 * @param theWrapper
	 *          the Shape we are exporting
	 * @return the shape in String form
	 */
	public final String exportThis(MWC.GUI.Plottable theWrapper)
	{
		FixWrapper theFix = (FixWrapper) theWrapper;
		Fix fix = theFix.getFix();

		// result value
		String line;

		// ;VLFix: @F CARPET 951212 113200.000 180 1000 145 5000 nb 1
		// 990408 133322.000 stingr @B 24 22 16.73 N 77 34 9.20 W 52.0 24.8 20

		// export the origin
		line = ""
				+ MWC.Utilities.TextFormatting.DebriefFormatDateTime.toStringHiRes(fix
						.getTime());

		// the track name may contain spaces - wrap in quotes if we have to
		line = exportTrackName(theFix.getTrackWrapper().getName(), line);

		line += " " + ImportReplay.replaySymbolFor(theFix.getColor(), null);
		line += " "
				+ MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(fix
						.getLocation());
		line += " "
				+ MWC.Utilities.TextFormatting.GeneralFormat
						.formatOneDecimalPlace(Conversions.Rads2Degs(fix.getCourse()));
		double theSpeedYPS = fix.getSpeed();
		double theSpeedKts = Conversions.Yps2Kts(theSpeedYPS);
		WorldSpeed wS = new WorldSpeed(fix.getSpeed()*3, WorldSpeed.ft_sec);
		double otherSpeedKts = wS.getValueIn(WorldSpeed.Kts);
		line += " "
				+ MWC.Utilities.TextFormatting.GeneralFormat
						.formatOneDecimalPlace(theSpeedKts);

		// special handling. check if we're actually storing a duff (NaN) depth
		String depthText;
		if (Double.isNaN(fix.getLocation().getDepth()))
		{
			depthText = "NaN";
		}
		else
		{
			depthText = MWC.Utilities.TextFormatting.GeneralFormat
					.formatOneDecimalPlace(fix.getLocation().getDepth());
		}
		line += " " + depthText;

		return line;
	}

	/**
	 * indicate if you can export this type of object
	 * 
	 * @param val
	 *          the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(Object val)
	{
		boolean res = false;

		if (val instanceof FixWrapper)
		{
			res = true;
		}

		return res;
	}

	// ////////////////////////////////////////////////
	// multi-word import/export utilities
	// ////////////////////////////////////////////////

	/**
	 * when importing the track name, it may turn out to be quoted, in which case
	 * we consume the remaining tokens until we get another quote
	 * 
	 * @param st
	 *          the tokenised stream to read the name from
	 * @return a string containing the (possibly multi-word) track name
	 */
	static public String checkForQuotedTrackName(StringTokenizer st)
	{
		String theTrackName = st.nextToken();

		// so, does the track name contain a quote character?
		int quoteIndex = theTrackName.indexOf("\"");
		if (quoteIndex >= 0)
		{
			// aah, but, we may have just read in all of the item. just check if
			// the
			// token contains
			// both speech marks...
			int secondQuoteIndex = theTrackName.indexOf("\"", quoteIndex + 1);

			if (secondQuoteIndex >= 0)
			{
				// yes, we have caught both quotes
				// just trim off the quote marks
				theTrackName = theTrackName.substring(1, theTrackName.length() - 1);
			}
			else
			{
				// no, we just caught the first quote.
				// fish around for the second one.

				String lastPartOfName = st.nextToken(quoteDelimiter);

				// yup. the ne
				theTrackName += lastPartOfName;

				// and trim away the quote
				theTrackName = theTrackName.substring(theTrackName.indexOf("\"") + 1);

				// consume the trailing quote delimiter (note - we allow spaces
				// & tabs)
				lastPartOfName = st.nextToken(" \t");
			}
		}
		return theTrackName;
	}

	/**
	 * export this (possibly multi-word) name to the line of text, wrapping in
	 * double quotes if we have ot.
	 * 
	 * @param trackName
	 *          the name we are exporting
	 * @param line
	 *          the line to append the data to
	 * @return the extended line
	 */
	public static String exportTrackName(String trackName, String line)
	{
		// right, we may need to quote the track name
		if (trackName.indexOf(" ") >= 0)
		{
			trackName = "\"" + trackName + "\"";
		}

		line += " " + trackName;
		return line;
	}

	// ////////////////////////////////////////////////
	// testing code...
	// ////////////////////////////////////////////////
	static public class testImport extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "CONV";

		public testImport(String val)
		{
			super(val);
		}

		public void testDecimalVals()
		{
			// test the secs component
			String iLine = "951212 051600 CARPET   @C   22 0 45 N 22 0 1.45 W 239.9   2.0      0 ";
			ImportFix iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("right lat", 22.0125, res.theFix.getLocation().getLat(),
					0.001);
			assertEquals("right long", -22.000402,
					res.theFix.getLocation().getLong(), 0.00001);

			// now the mins component
			iLine = "951212 051600 CARPET   @C   22 0.5 00 N 14 0.5 0 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("right lat", 22.008, res.theFix.getLocation().getLat(), 0.01);
			assertEquals("right long", -14.008333,
					res.theFix.getLocation().getLong(), 0.00001);

			// now the degs component
			iLine = "951212 051600 CARPET   @C   22.5 0 00 N 14.5 0 0 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("right lat", 22.5, res.theFix.getLocation().getLat(), 0.01);
			assertEquals("right long", -14.5, res.theFix.getLocation().getLong(),
					0.00001);

			//
			// NOW LET'S REVERSE THE HEMISPHERE
			// test the secs component
			iLine = "951212 051600 CARPET   @C   22 0 45 S 22 0 1.45 E 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("right lat", -22.0125, res.theFix.getLocation().getLat(),
					0.001);
			assertEquals("right long", 22.000402, res.theFix.getLocation().getLong(),
					0.00001);

			// now the mins component
			iLine = "951212 051600 CARPET   @C   22 0.5 00 S 14 0.5 0 E 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("right lat", -22.008, res.theFix.getLocation().getLat(),
					0.01);
			assertEquals("right long", 14.008333, res.theFix.getLocation().getLong(),
					0.00001);

			// now the degs component
			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("right lat", -22.5, res.theFix.getLocation().getLat(), 0.01);
			assertEquals("right long", 14.5, res.theFix.getLocation().getLong(),
					0.00001);

			// what about hte label?
			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 brian may smells";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("label not read", "brian may smells", res.label);

			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 brian may smells   ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("label not read", "brian may smells", res.label);

			iLine = "951212 051600 CARPET   @C   22.5 0 00 S 14.5 0 0 E 239.9   2.0      0 a   ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);
			assertEquals("label not read", "a", res.label);

		}

		public void testValues()
		{
			String iLine = "951212 051600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			ImportFix iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET", res.theTrackName);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);

			// ok, try our more difficult import string
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);

			// ok, try long years
			iLine = "19951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
			Date fixDate = res.theFix.getTime().getDate();
			DateFormat yearFormat = new SimpleDateFormat("yyyy");
			String dateYear = yearFormat.format(fixDate);
			assertEquals("right date", "1995", dateYear);

			// ok, try short years
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new SimpleDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("right date", "1995", dateYear);

			// ok, try short name
			iLine = "951212 051600 \"1\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "1", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new SimpleDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("right date", "1995", dateYear);

			// try with nice spaces (suitable for comparing on the way out
			iLine = "951212 051600 \"CARPET bag\" @C 22 10 53.54 N 021 45 14.20 W 239.9 2.0 0.0";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);

			// find the track name
			String theTrack = res.theTrackName;
			Color thisColor = ImportReplay.replayColorFor(res.theSymbology);

			// create the wrapper for this annotation
			SupportsPropertyListeners thisWrapper = new FixWrapper(res.theFix);

			TrackWrapper parentTrack = new TrackWrapper();
			parentTrack.setName(theTrack);

			// get the colour for this track
			parentTrack.setColor(thisColor);

			// set the sym type for the track
			String theSymType = ImportReplay.replayTrackSymbolFor(res.theSymbology);
			parentTrack.setSymbolType(theSymType);

			FixWrapper thisFix = (FixWrapper) thisWrapper;
			thisFix.setTrackWrapper(parentTrack);

			// now do the export
			String oLine = iff.exportThis(thisFix);

			// and check they're the same
			assertEquals("exported line matches", iLine, oLine);
		}

		public void testHiResParse()
		{
			String val = "700101 000000";
			HiResDate ers = DebriefFormatDateTime.parseThis(val);
			assertNull("zero micros doesn't get parsed", ers);

			val = "700101 000000.001";
			ers = DebriefFormatDateTime.parseThis(val);
			long micros = ers.getMicros();
			assertEquals("zero micros", 1000, micros);

			val = "19700101 000000.001";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals("zero micros", 1000, micros);

			val = "700101 000000.000001";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals("zero micros", 1, micros);

			val = "700101 000001.";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals("zero micros", 1000000, micros);

			val = "700101 000001.0";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals("zero micros", 1000000, micros);

			val = "700101 000001.01";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals("zero micros", 1010000, micros);

			val = "700101 000001.01001";
			ers = DebriefFormatDateTime.parseThis(val);
			micros = ers.getMicros();
			assertEquals("zero micros", 1010010, micros);

			val = "700101 000000.000000000022";
			ers = DebriefFormatDateTime.parseThis(val);
			assertNull("Shouldn't manage to produce value", ers);
		}

		public void testMilliSecValues()
		{
			String iLine = "700101 000001 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			ImportFix iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			iLine = "700101 000000.100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);

			iLine = "700101 000000.100100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			iff = new ImportFix();
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET", res.theTrackName);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
		}

		@SuppressWarnings("deprecation")
		public void testPadding()
		{
			String iLine = "951212 051600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			ImportFix iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			// and our special time data
			Date date = res.theFix.getTime().getDate();
			assertEquals("right hours", 5, date.getHours());
			assertEquals("right mins", 16, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 51600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 5, date.getHours());
			assertEquals("right mins", 16, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 1600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 16, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 6, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 00.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 0, date.getMinutes());
			assertEquals("right secs", 0, date.getSeconds());

			iLine = "951212 0.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 0, date.getMinutes());
			assertEquals("right secs", 0, date.getSeconds());

			// and sans-millis
			iLine = "951212 51600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 5, date.getHours());
			assertEquals("right mins", 16, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 1600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 16, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 6, date.getMinutes());
			assertEquals("right secs", 00, date.getSeconds());

			iLine = "951212 00 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 0, date.getMinutes());
			assertEquals("right secs", 0, date.getSeconds());

			iLine = "951212 0.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 0, date.getHours());
			assertEquals("right mins", 0, date.getMinutes());
			assertEquals("right secs", 0, date.getSeconds());

			// quick date test
			iLine = "951212 051600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 95, date.getYear());

			iLine = "51212 051600 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);
			date = res.theFix.getTime().getDate();
			assertEquals("right hours", 105, date.getYear());

		}

		public void testHiResValues()
		{
			String iLine = "951212 051600.000100 CARPET   @C   22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			ImportFix iff = new ImportFix();
			ReplayFix res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET", res.theTrackName);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);

			// and check the DTG
			HiResDate hrd = DebriefFormatDateTime.parseThis("951212 051600.000100");
			assertEquals("time hasn't got mangled", hrd, res.theFix.getTime());

			DateFormat otherFormat = new SimpleDateFormat("yyMMdd HHmmss");
			otherFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date theDate = null;
			try
			{
				theDate = otherFormat.parse("951212 051600");
			}
			catch (ParseException e)
			{
				e.printStackTrace(); // To change body of catch statement use
				// File |
				// Settings | File Templates.
				return;
			}
			HiResDate calculated = new HiResDate(theDate.getTime(), 100);
			assertEquals("time hasn't got mangled", calculated, res.theFix.getTime());

			// ok, try our more difficult import string
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);

			// ok, try long years
			iLine = "19951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
			Date fixDate = res.theFix.getTime().getDate();
			DateFormat yearFormat = new SimpleDateFormat("yyyy");
			String dateYear = yearFormat.format(fixDate);
			assertEquals("right date", "1995", dateYear);

			// ok, try short years
			iLine = "951212 051600 \"CARPET bag\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new SimpleDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("right date", "1995", dateYear);

			// ok, try short name
			iLine = "951212 051600 \"1\"     @C 22 10 53.54 N 21 45 14.20 W 239.9   2.0      0 ";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "1", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);
			fixDate = res.theFix.getTime().getDate();
			yearFormat = new SimpleDateFormat("yyyy");
			dateYear = yearFormat.format(fixDate);
			assertEquals("right date", "1995", dateYear);

			// try with nice spaces (suitable for comparing on the way out
			iLine = "951212 051600 \"CARPET bag\" @C 22 10 53.54 N 021 45 14.20 W 239.9 2.0 0.0";
			res = (ReplayFix) iff.readThisLine(iLine);

			// and check the result
			assertEquals("right track", "CARPET bag", res.theTrackName);
			assertEquals("right course", 4.187, res.theFix.getCourse(), 0.01);
			assertEquals("right symbology", "@C", res.theSymbology);
			assertEquals("right speed", 2.0, res.theFix.getSpeed(), 2.0);
			assertEquals("right depth", 0.0, res.theFix.getLocation().getDepth(),
					0.01);

			// find the track name
			String theTrack = res.theTrackName;
			Color thisColor = ImportReplay.replayColorFor(res.theSymbology);

			// create the wrapper for this annotation
			SupportsPropertyListeners thisWrapper = new FixWrapper(res.theFix);

			TrackWrapper parentTrack = new TrackWrapper();
			parentTrack.setName(theTrack);

			// get the colour for this track
			parentTrack.setColor(thisColor);

			// set the sym type for the track
			String theSymType = ImportReplay.replayTrackSymbolFor(res.theSymbology);
			parentTrack.setSymbolType(theSymType);

			FixWrapper thisFix = (FixWrapper) thisWrapper;
			thisFix.setTrackWrapper(parentTrack);

			// now do the export
			String oLine = iff.exportThis(thisFix);

			// and check they're the same
			assertEquals("exported line matches", iLine, oLine);
		}
	}

	// ////////////////////////////////////////////////
	// testing code
	// ////////////////////////////////////////////////
	public static void main(String[] args)
	{
		String test = "NaN";
		double val = Double.valueOf(test).doubleValue();

		System.out.println("res is:" + val);
	}

}
