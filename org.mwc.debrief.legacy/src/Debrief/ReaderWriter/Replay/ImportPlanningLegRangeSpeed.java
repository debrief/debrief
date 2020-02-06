/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

// $RCSfile: ImportSensor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ImportSensor.java,v $
// Revision 1.7  2006/02/13 16:19:06  Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.6  2006/01/06 10:36:40  Ian.Mayo
// Reflect tidying of sensor wrapper naming
//
// Revision 1.5  2005/12/13 09:04:38  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/25 10:24:18  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/08/19 14:12:48  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.2  2004/07/06 13:35:22  Ian.Mayo
// Correct class naming typo
//
// Revision 1.1.1.2  2003/07/21 14:47:52  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-05-06 09:09:16+01  ian_mayo
// Corrected javadoc
//
// Revision 1.3  2003-03-19 15:37:28+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:16+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:09+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:52+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:40+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-02-26 16:36:29+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.3  2001-08-24 16:35:11+01  administrator
// Keep the strings tidy
//
// Revision 1.2  2001-08-24 09:53:48+01  administrator
// Modified to reflect new way of representing null data in Sensor line
//
// Revision 1.1  2001-08-23 11:41:30+01  administrator
// first attempt at handling null position values
//
// Revision 1.0  2001-08-13 12:50:12+01  administrator
// Initial revision
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

import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.Track.PlanningSegment;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * class to parse a label from a line of text
 */
final class ImportPlanningLegRangeSpeed extends AbstractPlainLineImporter
{
  /**
   * the type for this string
   */
  // private final String _myTypeOrigin = ";PLANNING_ORIGIN:";
  //public final String _myTypePlanning_spd_time = ";PLANNING_SPEED_TIME:";
  // public final String _myTypePlanning_rng_time = ";PLANNING_RANGE_TIME:";
  public final String _myTypePlanning_rng_spd = ";PLANNING_RANGE_SPEED:";

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
    return false;
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
    throw new IllegalArgumentException(
        "Don't export with this class, use ImportPlanningLegOrigin");
  }

  /**
   * determine the identifier returning this type of annotation
   */
  @Override
  public final String getYourType()
  {
    return _myTypePlanning_rng_spd;
  }

  /**
   * read in this string and return a Label
   *
   * @throws ParseException
   */
  @Override
  public final Object readThisLine(final String theLine) throws ParseException
  {

    // ;PLANNING_RANGE_SPEED: AAAAAA BBBBBB RRRR SSS CCC

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // skip the comment identifier
    st.nextToken();

    // get the (possibly multi-word) track name
    final String theTrack = AbstractPlainLineImporter.checkForQuotedName(st);
    final String theLeg = AbstractPlainLineImporter.checkForQuotedName(st);

    final WorldDistance range = new WorldDistance(MWCXMLReader.readThisDouble(st
        .nextToken()), WorldDistance.YARDS);
    final WorldSpeed speed = new WorldSpeed(MWCXMLReader.readThisDouble(st
        .nextToken()), WorldSpeed.Kts);
    final double course = MWCXMLReader.readThisDouble(st.nextToken());
    
    boolean closing = false;
    if(st.hasMoreTokens())
    {
      String next = st.nextToken();
      if(ImportPlanningLegOrigin.CLOSING.equals(next))
      {
        closing = true;
      }
    }

    final PlanningSegment res; 
    if(closing)
    {
      res = new PlanningSegment.ClosingSegment(theLeg, course, speed,
          range);
    }
    else
    {
      res = new PlanningSegment(theLeg, course, speed,
          range);
    }

    res.setParentName(theTrack);
    res.setCalculation(PlanningLegCalcModelPropertyEditor.RANGE_SPEED);

    return res;
  }

}
