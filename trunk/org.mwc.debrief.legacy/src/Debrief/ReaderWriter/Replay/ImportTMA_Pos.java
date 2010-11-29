// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportTMA_Pos.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: ImportTMA_Pos.java,v $
// Revision 1.4  2005/12/13 09:04:38  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/11/25 10:24:19  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/08/19 14:12:49  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.1.1.2  2003/07/21 14:47:54  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.1  2003-07-03 15:02:22+01  ian_mayo
// Initial revision
//
// Revision 1.3  2003-06-23 08:40:31+01  ian_mayo
// Now in testing
//
// Revision 1.2  2003-06-19 11:19:54+01  ian_mayo
// <>
//
// Revision 1.1  2003-06-19 11:19:34+01  ian_mayo
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.util.StringTokenizer;

import Debrief.Wrappers.*;
import MWC.Algorithms.Conversions;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to read in a TMA Solution (incorporating Range and Bearing)
 */
public final class ImportTMA_Pos implements PlainLineImporter
{
	/**
	 * the type for this string
	 */
	private final String _myType = ";TMA_POS:";

	/**
	 * read in this string and return a Label
	 */
	public final Object readThisLine(String theLine)
	{

		// ;TMA_POS: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H
		// TT...TT OOO.O XXXX YYYY CCC SSS DDD xx.xx
		// ;; date, time, ownship name, symbology, tma lat, tma long, track name,
		// ellipse orientation (deg from north), maxima (yds), minima (yds), course,
		// speed, depth (m), label string

		// get a stream from the string
		StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		String theLabel;
		String theSymbology;
		String vesselName;
		String solutionName;
		String dateStr;
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;
		HiResDate theDtg = null;
		double course, speed, depth;
		double orientation, maxima, minima;
		WorldLocation origin = null;
		EllipseShape theEllipse = null;
		Color theColor;

		// skip the comment identifier
		st.nextToken();

		// now the dtg
		dateStr = st.nextToken();

		// append the time
		dateStr = dateStr + " " + st.nextToken();

		// and extract the date
		theDtg = DebriefFormatDateTime.parseThis(dateStr);

		// now the vessel name
		vesselName = ImportFix.checkForQuotedTrackName(st);

		// next with the symbology
		theSymbology = st.nextToken(normalDelimiters);

		// find out if it's our null value
		String next = st.nextToken();

		// get the deg out of this value
		latDeg = Double.valueOf(next);

		// ok, this is valid data, persevere with it
		latMin = Double.valueOf(st.nextToken());
		latSec = Double.valueOf(st.nextToken()).doubleValue();

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
		longSec = Double.valueOf(st.nextToken()).doubleValue();
		longHem = st.nextToken().charAt(0);

		// create the origin
		origin = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg,
				longMin, longSec, longHem, 0);

		// read in the solution name
		solutionName = st.nextToken();

		// trim the sensor name
		solutionName = solutionName.trim();

		// now the ellipse details (or null)
		next = st.nextToken();

		// let's trim this string aswell, just so we're sure N is the first letter
		// if that's its destiny
		next = next.trim();

		// find out if it's our null value
		if (next.startsWith("N"))
		{
			// ditch it,
		}
		else
		{
			// now the ellipse details
			orientation = Double.valueOf(next).doubleValue();
			maxima = Double.valueOf(st.nextToken()).doubleValue();
			minima = Double.valueOf(st.nextToken()).doubleValue();

			theEllipse = new EllipseShape(null, orientation, new WorldDistance(
					Conversions.Yds2Degs(maxima), WorldDistance.DEGS), new WorldDistance(
					Conversions.Yds2Degs(minima), WorldDistance.DEGS));

		} // whether the duff ellipse data was entered

		course = Double.valueOf(st.nextToken()).doubleValue();
		speed = Double.valueOf(st.nextToken()).doubleValue();
		depth = Double.valueOf(st.nextToken()).doubleValue();

		// and lastly read in the message
		theLabel = st.nextToken("\r").trim();
		// strip off any gash
		theLabel = theLabel.trim();

		theColor = ImportReplay.replayColorFor(theSymbology);

		String theStyle = ImportReplay.replayTrackSymbolFor(theSymbology);

		// create the contact object
		TMAContactWrapper data = new TMAContactWrapper(solutionName, vesselName,
				theDtg, origin, course, speed, depth, theColor, theLabel, theEllipse,
				theStyle);

		return data;
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
	 *          the thing we are going to export
	 * @return the shape in String form
	 */
	public final String exportThis(MWC.GUI.Plottable theWrapper)
	{
		// result value
		String line = ";; Export of sensor data not implemented";
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

		if (val instanceof SensorWrapper)
		{
			res = true;
		}

		return res;

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testImportTMA_POS extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testImportTMA_POS(final String val)
		{
			super(val);
		}

		public final void testImport()
		{

			// ;TMA_POS: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H
			// TT...TT OOO.O XXXX YYYY CCC SSS DDD xx.xx
			// ;; date, time, ownship name, symbology, tma lat, tma long, track name,
			// ellipse orientation (deg from north), maxima (yds), minima (yds),
			// course, speed, depth (m), label string

			final String testLine = ";TMA_POS: 030211 120312 CARPET S@ 22 11 10.63 N 21 41 52.37 W TRACK_060 045.0  4000 2000 050 12.4 100 Trial label";

			// ok, create the importer
			ImportTMA_Pos importer = new ImportTMA_Pos();

			// see if we can read this type
			String theType = importer.getYourType();
			assertEquals("returned correct type", theType, ";TMA_POS:");

			// now read the line
			Object res = importer.readThisLine(testLine);
			assertNotNull("managed to read item", res);

			// check it's of the correct type
			assertEquals("of correct class",
					"class Debrief.Wrappers.TMAContactWrapper", res.getClass().toString());
			TMAContactWrapper tc = (TMAContactWrapper) res;

			// check the values we've used
			HiResDate theDate = DebriefFormatDateTime.parseThis("030211 120312.000");
			assertEquals("correct date", theDate, tc.getDTG());
			assertEquals("Correct track", "CARPET", tc.getTrackName());
			assertEquals("correct color", Color.white, tc.getColor());
			assertEquals("correct symbol", "Submarine", tc.getSymbol());
			assertNotNull("correct origin", tc.buildGetOrigin());
			assertEquals("correct solution name", "TRACK_060", tc.getSolutionName());
			assertEquals("correct orientation", 45, tc.getEllipse().getOrientation(),
					0.0001d);
			assertEquals("correct maxima", 4000, tc.getEllipse().getMaxima()
					.getValueIn(WorldDistance.YARDS), 0.0001d);
			assertEquals("correct minima", 2000, tc.getEllipse().getMinima()
					.getValueIn(WorldDistance.YARDS), 0.0001d);
			assertEquals("correct course", 50, tc.getTargetCourse(), 0.001d);
			assertEquals("correct speed", 12.4, tc.getSpeed(), 0.001d);
			assertEquals("correct depth", 100, tc.getDepth(), 0.001d);
			assertEquals("correct label", "Trial label", tc.getLabel());

		}

		public final void testImportNoEllipse()
		{
			// ;TMA_POS: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H
			// TT...TT OOO.O XXXX YYYY CCC SSS DDD xx.xx
			// ;; date, time, ownship name, symbology, tma lat, tma long, track name,
			// ellipse orientation (deg from north), maxima (yds), minima (yds),
			// course, speed, depth (m), label string

			final String testLine = ";TMA_POS: 030211 120312 CARPET S@ 22 11 10.63 N 21 41 52.37 W TRACK_060 NULL 050 12.4 100 Trial label";

			// ok, create the importer
			ImportTMA_Pos importer = new ImportTMA_Pos();

			// see if we can read this type
			String theType = importer.getYourType();
			assertEquals("returned correct type", theType, ";TMA_POS:");

			// now read the line
			Object res = importer.readThisLine(testLine);
			assertNotNull("managed to read item", res);

			// check it's of the correct type
			assertEquals("of correct class",
					"class Debrief.Wrappers.TMAContactWrapper", res.getClass().toString());
			TMAContactWrapper tc = (TMAContactWrapper) res;

			// check the values we've used
			HiResDate theDate = DebriefFormatDateTime.parseThis("030211 120312.000");
			assertEquals("correct date", theDate, tc.getDTG());
			assertEquals("Correct track", "CARPET", tc.getTrackName());
			assertEquals("correct color", Color.white, tc.getColor());
			assertEquals("correct symbol", "Submarine", tc.getSymbol());
			assertNotNull("correct origin", tc.buildGetOrigin());
			assertEquals("correct range", Conversions.Yds2Degs(0), tc
					.getRange().getValueIn(WorldDistance.YARDS), 0.001d);
			assertEquals("correct bearing", 0, tc.getBearing(),
					0.001d);
			assertEquals("correct solution name", "TRACK_060", tc.getSolutionName());
			assertEquals("correct orientation", 0, tc.getEllipse().getOrientation(),
					0.0001d);
			assertEquals("correct maxima", 0, tc.getEllipse().getMaxima().getValueIn(
					WorldDistance.YARDS), 0.0001d);
			assertEquals("correct minima", 0, tc.getEllipse().getMinima().getValueIn(
					WorldDistance.YARDS), 0.0001d);
			assertEquals("correct course", 50, tc.getTargetCourse(), 0.001d);
			assertEquals("correct speed", 12.4, tc.getSpeed(), 0.001d);
			assertEquals("correct depth", 100, tc.getDepth(), 0.001d);
			assertEquals("correct label", "Trial label", tc.getLabel());
		}
	}

	public static void main(String[] args)
	{
		testImportTMA_POS tm = new testImportTMA_POS("scrap");
		tm.testImport();
		tm.testImportNoEllipse();
	}

}
