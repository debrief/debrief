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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.junit.Test;

import Debrief.Wrappers.Formatters.SliceTrackFormatListener;
import MWC.GUI.Layers.INewItemListener;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import junit.framework.TestCase;

/**
 * class to parse a label from a line of text
 */
final class ImportTrackSplitFormatter extends AbstractPlainLineImporter
{

  /*
   * example: ;SPLIT_TRACK: One_Hour 3600000 TRACK_NAME TRACK_TWO ...
   * example: ;SPLIT_TRACK: One_Hour 3600000 
   */

  /**
   * the type for this string
   */
  private final String _myType = ";SPLIT_TRACK:";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(final String theLine)
  {

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String formatName;
    String periodToken;
    List<String> trackNames = new ArrayList<String>();

    // skip the comment identifier
    st.nextToken();

    formatName = checkForQuotedName(st).trim();
    periodToken = st.nextToken();
    final long period = Long.parseLong(periodToken);
    
    while (st.hasMoreElements())
    {
      String nextItem = checkForQuotedName(st).trim();
      if (nextItem != null && nextItem.length() > 0)
      {
        trackNames.add(nextItem);
      }
    }

    INewItemListener cif = new SliceTrackFormatListener(formatName, period, trackNames);

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
    throw new NotImplementedException("We don't export these to REP format");
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
    return false;
  }

  public static class TestMe extends TestCase
  {
    @Test
    public void testRead()
    {
      /*
       * example:
       */

      ImportTrackSplitFormatter iff = new ImportTrackSplitFormatter();
      SliceTrackFormatListener res = (SliceTrackFormatListener) iff
          .readThisLine(";SPLIT_TRACK: One_Hour 3600000");
      assertNotNull(res);
      assertEquals("correct name", "One_Hour", res.getName());
      assertEquals("correct period", 3600000, res.getInterval());

      res = (SliceTrackFormatListener) iff
          .readThisLine(";SPLIT_TRACK: One_Second 1000");
      assertNotNull(res);
      assertEquals("One_Second", res.getName());
      assertEquals("correct period", 1000, res.getInterval());


    }
  }

}
