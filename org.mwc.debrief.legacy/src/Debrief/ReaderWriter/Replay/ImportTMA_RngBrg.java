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
// $RCSfile: ImportTMA_RngBrg.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: ImportTMA_RngBrg.java,v $
// Revision 1.4  2005/12/13 09:04:39  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/11/25 10:24:20  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/08/19 14:12:50  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.1.1.2  2003/07/21 14:47:55  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-06-25 08:49:26+01  ian_mayo
// Method name changes
//
// Revision 1.4  2003-06-23 13:39:52+01  ian_mayo
// Finish testing
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
import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import junit.framework.TestCase;

/**
 * class to read in a TMA Solution (incorporating Range and Bearing)
 */
public final class ImportTMA_RngBrg extends AbstractPlainLineImporter
{
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testImportTMA_RngBrg extends TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testImportTMA_RngBrg(final String val)
    {
      super(val);
    }

    public final void testImport() throws ParseException
    {
      // ;TMA_RB: YYMMDD HHMMSS.SSS AAAAAA @@ RRR.R BBB.B TT...TT OOO.O XXXX
      // YYYY CCC SSS DDD xx.xx
      // ;; date, time, ownship name, symbology, bearing (deg), range (yds),
      // track name, elipse orientation (deg from north), maxima (yds), minima
      // (yds), course, speed, depth (m), label string

      final String testLine =
          ";TMA_RB: 030211 120312 CARPET S@ 124.5 12000 TRACK_060 045.0  4000 2000 050 12.4 100 Trial label";

      // ok, create the importer
      final ImportTMA_RngBrg importer = new ImportTMA_RngBrg();

      // see if we can read this type
      final String theType = importer.getYourType();
      assertEquals("returned correct type", theType, ";TMA_RB:");

      // now read the line
      final Object res = importer.readThisLine(testLine);
      assertNotNull("managed to read item", res);

      // check it's of the correct type
      assertEquals("of correct class",
          "class Debrief.Wrappers.TMAContactWrapper", res.getClass()
              .toString());
      final TMAContactWrapper tc = (TMAContactWrapper) res;

      // check the values we've used
      final HiResDate theDate = DebriefFormatDateTime.parseThis(
          "030211 120312.000");
      assertEquals("correct date", theDate, tc.getDTG());
      assertEquals("Correct track", "CARPET", tc.getTrackName());
      assertEquals("correct color", Color.white, tc.getColor());
      assertEquals("correct symbol", "Submarine", tc.getSymbol());
      assertEquals("correct range", MWC.Algorithms.Conversions.Yds2Degs(12000),
          tc.getRange().getValueIn(WorldDistance.DEGS), 0.001d);
      assertEquals("correct bearing", MWC.Algorithms.Conversions.Degs2Rads(
          124.5), tc.getBearingRads(), 0.001d);
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

    public final void testImportWithSpaces() throws ParseException
    {
      // ;TMA_RB: YYMMDD HHMMSS.SSS AAAAAA @@ RRR.R BBB.B TT...TT OOO.O XXXX
      // YYYY CCC SSS DDD xx.xx
      // ;; date, time, ownship name, symbology, bearing (deg), range (yds),
      // track name, elipse orientation (deg from north), maxima (yds), minima
      // (yds), course, speed, depth (m), label string

      final String testLine =
          ";TMA_RB: 030211 120312 CARPET S@ 124.5 12000 \"TRACK 060\" 045.0  4000 2000 050 12.4 100 Trial label";

      // ok, create the importer
      final ImportTMA_RngBrg importer = new ImportTMA_RngBrg();

      // see if we can read this type
      final String theType = importer.getYourType();
      assertEquals("returned correct type", theType, ";TMA_RB:");

      // now read the line
      final Object res = importer.readThisLine(testLine);
      assertNotNull("managed to read item", res);

      // check it's of the correct type
      assertEquals("of correct class",
          "class Debrief.Wrappers.TMAContactWrapper", res.getClass()
              .toString());
      final TMAContactWrapper tc = (TMAContactWrapper) res;

      // check the values we've used
      assertEquals("correct solution name", "TRACK 060", tc.getSolutionName());
    }

    public final void testImportNoEllipse() throws ParseException
    {
      // ;TMA_RB: YYMMDD HHMMSS.SSS AAAAAA @@ RRR.R BBB.B TT...TT OOO.O XXXX
      // YYYY CCC SSS DDD xx.xx
      // ;; date, time, ownship name, symbology, bearing (deg), range (yds),
      // track name, elipse orientation (deg from north), maxima (yds), minima
      // (yds), course, speed, depth (m), label string

      final String testLine =
          ";TMA_RB: 030211 120312 CARPET S@ 124.5 12000 TRACK_060 NULL 050 12.4 100 Trial label";

      // ok, create the importer
      final ImportTMA_RngBrg importer = new ImportTMA_RngBrg();

      // see if we can read this type
      final String theType = importer.getYourType();
      assertEquals("returned correct type", theType, ";TMA_RB:");

      // now read the line
      final Object res = importer.readThisLine(testLine);
      assertNotNull("managed to read item", res);

      // check it's of the correct type
      assertEquals("of correct class",
          "class Debrief.Wrappers.TMAContactWrapper", res.getClass()
              .toString());
      final TMAContactWrapper tc = (TMAContactWrapper) res;

      // check the values we've used
      final HiResDate theDate = DebriefFormatDateTime.parseThis(
          "030211 120312.000");
      assertEquals("correct date", theDate, tc.getDTG());
      assertEquals("Correct track", "CARPET", tc.getTrackName());
      assertEquals("correct color", Color.white, tc.getColor());
      assertEquals("correct symbol", "Submarine", tc.getSymbol());
      assertEquals("correct range", MWC.Algorithms.Conversions.Yds2Degs(12000),
          tc.getRange().getValueIn(WorldDistance.DEGS), 0.001d);
      assertEquals("correct bearing", MWC.Algorithms.Conversions.Degs2Rads(
          124.5), tc.getBearingRads(), 0.001d);
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

  public static void main(final String[] args) throws ParseException
  {
    final testImportTMA_RngBrg tm = new testImportTMA_RngBrg("scrap");
    tm.testImport();
    tm.testImportNoEllipse();
  }

  /**
   * the type for this string
   */
  private final String _myType = ";TMA_RB:";

  /**
   * indicate if you can export this type of object
   * 
   * @param val
   *          the object to test
   * @return boolean saying whether you can do it
   */
  @Override
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if (val instanceof SensorWrapper)
    {
      res = true;
    }

    return res;

  }

  /**
   * export the specified shape as a string
   * 
   * @param theWrapper
   *          the thing we are going to export
   * @return the shape in String form
   */
  @Override
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    // result value
    final String line = ";; Export of sensor data not implemented";
    return line;

  }

  /**
   * determine the identifier returning this type of annotation
   */
  @Override
  public final String getYourType()
  {
    return _myType;
  }

  /**
   * read in this string and return a Label
   * @throws ParseException 
   */
  @Override
  public final Object readThisLine(final String theLine) throws ParseException
  {

    // ;TMA_RB: YYMMDD HHMMSS.SSS AAAAAA @@ RRR.R BBB.B TT...TT OOO.O XXXX YYYY
    // CCC SSS DDD xx.xx
    // ;; date, time, ownship name, symbology, bearing (deg), range (yds), track
    // name, elipse orientation (deg from north), maxima (yds), minima (yds),
    // course, speed, depth (m), label string

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theLabel;
    String vesselName;
    String solutionName;
    HiResDate theDtg = null;
    double brg, rng;
    double course, speed, depth;
    double orientation, maxima, minima;
    EllipseShape theEllipse = null;
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // now the vessel name
    vesselName = AbstractPlainLineImporter.checkForQuotedName(st);

    // next with the symbology
    symbology = st.nextToken(normalDelimiters);

    try
    {
      // now get the range and bearing
      brg = MWCXMLReader.readThisDouble(st.nextToken());
      rng = MWCXMLReader.readThisDouble(st.nextToken());

      // read in the solution name
      solutionName = AbstractPlainLineImporter.checkForQuotedName(st);

      // trim the sensor name
      solutionName = solutionName.trim();

      // now the ellipse details (or null)
      String next = st.nextToken();

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
        orientation = MWCXMLReader.readThisDouble(next);
        maxima = MWCXMLReader.readThisDouble(st.nextToken());
        minima = MWCXMLReader.readThisDouble(st.nextToken());

        theEllipse = new EllipseShape(null, orientation, new WorldDistance(
            MWC.Algorithms.Conversions.Yds2Degs(maxima), WorldDistance.DEGS),
            new WorldDistance(MWC.Algorithms.Conversions.Yds2Degs(minima),
                WorldDistance.DEGS));

      } // whether the duff ellipse data was entered

      course = MWCXMLReader.readThisDouble(st.nextToken());
      speed = MWCXMLReader.readThisDouble(st.nextToken());
      depth = MWCXMLReader.readThisDouble(st.nextToken());

      // and lastly read in the message
      theLabel = st.nextToken("\r");
      // strip off any gash
      theLabel = theLabel.trim();

      theColor = ImportReplay.replayColorFor(symbology);

      final String theStyle = ImportReplay.replayTrackSymbolFor(symbology);

      // create the contact object
      final TMAContactWrapper data = new TMAContactWrapper(solutionName,
          vesselName, theDtg, rng, brg, course, speed, depth, theColor,
          theLabel, theEllipse, theStyle);

      return data;
    }
    catch (final ParseException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst import TMA_RngBrg");
      return null;
    }
  }

}
