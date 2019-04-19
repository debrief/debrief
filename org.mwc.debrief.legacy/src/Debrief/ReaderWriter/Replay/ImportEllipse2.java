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
// $RCSfile: ImportEllipse.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportEllipse.java,v $
// Revision 1.3  2005/12/13 09:04:35  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:15  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:46  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-19 15:37:41+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2002-10-11 08:35:41+01  ian_mayo
// IntelliJ optimisations
//
// Revision 1.2  2002-05-28 12:28:14+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:34+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-03-13 08:56:36+00  administrator
// reflect name changes in ShapeWrapper class
//
// Revision 1.3  2002-02-26 16:35:05+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.2  2002-02-26 16:34:46+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.1  2002-01-17 20:21:31+00  administrator
// Reflect use of WorldDistance and duration objects
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
// Revision 1.1.1.1  2000/12/12 20:47:07  ianmayo
// initial import of files
//
// Revision 1.9  2000-10-09 13:37:40+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.8  2000-09-22 11:45:05+01  ian_mayo
// remove unnecesary units conversion
//
// Revision 1.7  2000-08-15 08:58:13+01  ian_mayo
// reflect Bean name changes
//
// Revision 1.6  2000-02-22 13:49:22+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.5  1999-11-12 14:36:11+00  ian_mayo
// made them export aswell as import
//
// Revision 1.4  1999-11-11 18:21:35+00  ian_mayo
// format of ShapeWrapper changed
//
// Revision 1.3  1999-11-11 10:34:08+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.2  1999-11-09 11:27:13+00  ian_mayo
// new file
//
//
package Debrief.ReaderWriter.Replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
final class ImportEllipse2 extends ImportEllipse
{

  // ////////////////////////////////////////////////
  // testing code...
  // ////////////////////////////////////////////////
  static public class testImport2 extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "CONV";

    public testImport2(final String val)
    {
      super(val);
    }

    public void testValues() throws ParseException
    {
      final String iLine =
          ";ELLIPSE2: @F 951212 060200 951212 061200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse";
      final AbstractPlainLineImporter iff = new ImportEllipse2();
      final ShapeWrapper res = (ShapeWrapper) iff.readThisLine(iLine);

      // and check the result
      assertNotNull("read it in", res);
      assertEquals("right track", "test ellipse", res.getLabel());
      assertEquals("right color", new java.awt.Color(255, 150, 0), res.getColor());
      assertEquals("right name", "test ellipse", res.getName());
      assertNotNull("start present", res.getStartDTG());
      assertNotNull("end present", res.getEndDTG());
      long diff = (res.getEndDTG().getMicros() - res.getStartDTG().getMicros()) / 1000;
      assertEquals("correct interval",600000, diff);
      final EllipseShape ell = (EllipseShape) res.getShape();
      assertEquals("right orient", 45.0, ell.getOrientation());
      assertEquals("right maxima", 5000, ell.getMaxima().getValueIn(
          WorldDistance.YARDS), 0.001);
      assertEquals("right minima", 3000, ell.getMinima().getValueIn(
          WorldDistance.YARDS), 0.001);
    }
  }

  /** our REP file type
   * 
   */
  private final static String TYPE_STR = ";ELLIPSE2:";

  public ImportEllipse2()
  {
    super(TYPE_STR);
  }

  @Override
  protected HiResDate endDateFor(StringTokenizer st) throws ParseException
  {
    // get date & time
    String dateStr = st.nextToken()+ " " + st.nextToken();

    // produce a date from this data
    final HiResDate date = DebriefFormatDateTime.parseThis(dateStr);
    
    return date;
  }

}
