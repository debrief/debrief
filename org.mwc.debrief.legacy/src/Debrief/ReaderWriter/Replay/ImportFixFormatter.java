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
// $RCSfile: ImportTimeText.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportTimeText.java,v $
// Revision 1.3  2005/12/13 09:04:39  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:20  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:53  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:19+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-26 16:36:48+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:32+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 11:36:57+00  novatech
// setString has changed to setLabel in label
//
// Revision 1.2  2001-01-17 13:23:46+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:24  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-09 13:37:35+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.1  2000-09-26 10:57:52+01  ian_mayo
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.junit.Test;

import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.Formatters.CoreFormatItemListener;
import MWC.GUI.Properties.AttributeTypePropertyEditor;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

/**
 * class to parse a label from a line of text
 */
final class ImportFixFormatter extends AbstractPlainLineImporter
{

  /*
   * example: ;FORMAT_FIX: 10_sec_sym SYMBOL NULL NULL TRUE 600000
   */

  /**
   * the type for this string
   */
  private final String _myType = ";FORMAT_FIX:";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(final String theLine)
  {
 
    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String formatName;
    String trackName;
    String symbology;
    boolean regularTimes; 
    int interval;
    String attributeType;

    // skip the comment identifier
    st.nextToken();

    formatName = checkForQuotedName(st).trim();
    attributeType = st.nextToken();

    // do processing for track name in quotes
    // trouble - the track name may have been quoted, in which case we will
    // pull in the remaining fields aswell
    trackName = checkForQuotedName(st).trim();

    // bit of tidying
    if (trackName.equals("NULL"))
    {
      trackName = null;
    }

    symbology = st.nextToken();

    // bit of tidying
    if (symbology.equals("NULL"))
    {
      symbology = null;
    }
    regularTimes = Boolean.parseBoolean(st.nextToken());
    interval = Integer.parseInt(st.nextToken());

    AttributeTypePropertyEditor pe = new AttributeTypePropertyEditor();
    pe.setAsText(attributeType);
    int attType = (int) pe.getValue();

    CoreFormatItemListener cif =
        new CoreFormatItemListener(formatName, trackName, symbology, interval,
            regularTimes, attType, true);

    return cif;
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
   * @return the shape in String form
   * @param theWrapper
   *          the Shape we are exporting
   */
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    final LabelWrapper theLabel = (LabelWrapper) theWrapper;

    String line = null;

    line = _myType + " BB ";
    line =
        line
            + MWC.Utilities.TextFormatting.DebriefFormatLocation
                .toString(theLabel.getLocation());

    line = line + theLabel.getLabel();

    return line;
  }

  /**
   * indicate if you can export this type of object
   * 
   * @param val
   *          the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if (val instanceof LabelWrapper)
    {
      // also see if there is just the start time specified
      final LabelWrapper lw = (LabelWrapper) val;
      if ((lw.getStartDTG() != null) && (lw.getEndDTG() == null))
      {
        // yes, this is a label with only the start time specified,
        // we can export it
        res = true;
      }
    }
    return res;
  }

  public static class TestMe extends TestCase
  {
    @Test
    public void testRead()
    {
      /*
       * example:
       */

      ImportFixFormatter iff = new ImportFixFormatter();
      CoreFormatItemListener res =
          (CoreFormatItemListener) iff
              .readThisLine(";FORMAT_FIX: 10_sec_sym SYMBOL NULL NULL TRUE 600000");
      assertNotNull(res);
      assertNull("No track", res.getLayerName());
      assertNull("no sym", res.getSymbology());
      assertTrue("active", res.getVisible());
      assertNotNull("has name", res.getName());
      assertEquals("has correct attr", 1, res.getAttributeType());
      assertEquals("has correct freq", 600000, res.getInterval().getDate().getTime());
      assertTrue("regular intervals", res.getRegularIntervals());

      res =
          (CoreFormatItemListener) iff
              .readThisLine(";FORMAT_FIX: 10_sec_sym ARROW \"Track One\" @A FALSE 100000");
      assertNotNull(res);
      assertEquals("No track", "Track One", res.getLayerName());
      assertNotNull("sym", res.getSymbology());
      assertTrue("active", res.getVisible());
      assertNotNull("has name", res.getName());
      assertEquals("has correct attr", 0, res.getAttributeType());
      assertEquals("has correct freq", 100000, res.getInterval().getDate().getTime());
      assertFalse("regular intervals", res.getRegularIntervals());

      res =
          (CoreFormatItemListener) iff
              .readThisLine(";FORMAT_FIX: \"10 arrow\" ARROW \"Track One\" BB FALSE 100000");
      assertNotNull(res);
      assertEquals("No track", "Track One", res.getLayerName());
      assertNotNull("sym", res.getSymbology());
      assertEquals("correct sym", "BB", res.getSymbology());
      assertTrue("active", res.getVisible());
      assertNotNull("has name", res.getName());

    }
  }

}
