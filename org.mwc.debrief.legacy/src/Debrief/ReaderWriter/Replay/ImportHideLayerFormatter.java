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

import junit.framework.TestCase;

import org.junit.Test;

import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.Formatters.HideLayerFormatListener;
import MWC.GUI.PlainWrapper;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

/**
 * class to parse a label from a line of text
 */
final class ImportHideLayerFormatter extends AbstractPlainLineImporter
{

  /*
   * example: ;FORMAT_FIX: 10_sec_sym SYMBOL NULL NULL TRUE 600000
   */

  /**
   * the type for this string
   */
  private final String _myType = ";FORMAT_LAYER_HIDE:";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(final String theLine)
  {

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String formatName;
    List<String> trackNames = new ArrayList<String>();

    // skip the comment identifier
    st.nextToken();

    formatName = checkForQuotedName(st).trim();

    while (st.hasMoreElements())
    {
      String nextItem = checkForQuotedName(st).trim();
      if (nextItem != null && nextItem.length() > 0)
      {
        trackNames.add(nextItem);
      }
    }

    String[] names = trackNames.toArray(new String[]
    {});

    PlainWrapper cif = new HideLayerFormatListener(formatName, names);

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

      ImportHideLayerFormatter iff = new ImportHideLayerFormatter();
      HideLayerFormatListener res =
          (HideLayerFormatListener) iff
              .readThisLine(";FORMAT_LAYER_HIDE: \"format name\" \"layer a name1\" layer_name2");
      assertNotNull(res);
      assertEquals("has correct name", "format name", res.getName());
      assertEquals("has correct freq", 2, res.getLayers().length);

      res =
          (HideLayerFormatListener) iff
              .readThisLine(";FORMAT_LAYER_HIDE: format_name");
      assertNotNull(res);
      assertEquals("has correct name", "format_name", res.getName());
      assertEquals("has correct freq", 0, res.getLayers().length);

    }
  }

}
