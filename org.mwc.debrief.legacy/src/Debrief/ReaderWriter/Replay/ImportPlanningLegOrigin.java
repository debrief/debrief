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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.PlanningSegment.ClosingSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import junit.framework.TestCase;

/**
 * class to parse a label from a line of text
 */
final public class ImportPlanningLegOrigin extends AbstractPlainLineImporter
{

  /**
   * @author Administrator
   * 
   */
  public static class PlanningLegImporterTest extends TestCase
  {

    private final static String tracks =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/planning_tracks.rep";

    public void testImport() throws FileNotFoundException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(tracks);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(tracks, bs, tLayers);

      assertEquals("read in tracks", 2, tLayers.size());
      
      // ok, now export one of them
      CompositeTrackWrapper ctw = (CompositeTrackWrapper) tLayers.findLayer("SENSOR_PLANNED");
      assertNotNull("found planning leg", ctw);
      trackImporter.exportThis(ctw);
      
      String output = trackImporter.getBuffer().toString();
      assertNotNull("produced output");
      assertTrue("has content", output.length() > 200);   // was originally 388 cgars
    }

  }

  public static final String CLOSING = "CLOSING";
  /**
   * the type for this string
   */
  private final String _myTypeOrigin = ";PLANNING_ORIGIN:";
  public final String _myTypePlanning_spd_time = ";PLANNING_SPEED_TIME:";
  public final String _myTypePlanning_rng_time = ";PLANNING_RANGE_TIME:";
  public final String _myTypePlanning_rng_spd = ";PLANNING_RANGE_SPEED:";

  /**
   * read in this string and return a Label
   * 
   * @throws ParseException
   */
  public final Object readThisLine(final String theLine) throws ParseException
  {

    // ;PLANNING_ORIGIN: YYMMDD HHMMSS AAAAAA @@ 22 12 10.28 N 21 32 40.33 W

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    HiResDate theDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // get the (possibly multi-word) track name
    String theTrack = ImportFix.checkForQuotedName(st);

    // start with the symbology
    symbology = st.nextToken(normalDelimiters);

    double latDeg = MWCXMLReader.readThisDouble(st.nextToken());
    double latMin = MWCXMLReader.readThisDouble(st.nextToken());
    double latSec = MWCXMLReader.readThisDouble(st.nextToken());

    /**
     * now, we may have trouble here, since there may not be a space between the hemisphere
     * character and a 3-digit latitude value - so BE CAREFUL
     */
    final String vDiff = st.nextToken();
    char latHem;
    double longDeg;
    if (vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      final String secondPart = vDiff.substring(1, vDiff.length());
      longDeg = MWCXMLReader.readThisDouble(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = MWCXMLReader.readThisDouble(st.nextToken());
    }
    double longMin = MWCXMLReader.readThisDouble(st.nextToken());
    double longSec = MWCXMLReader.readThisDouble(st.nextToken());
    char longHem = st.nextToken().charAt(0);

    double theDepth = MWCXMLReader.readThisDouble(st.nextToken());

    WorldLocation theLoc = new WorldLocation(latDeg, latMin, latSec, latHem,
        longDeg, longMin, longSec, longHem, theDepth);

    Color theColor = ImportReplay.replayColorFor(symbology);

    final CompositeTrackWrapper data = new CompositeTrackWrapper(theDtg,
        theLoc);
    data.setColor(theColor);
    data.setName(theTrack);

    return data;
  }

  /**
   * determine the identifier returning this type of annotation
   */
  public final String getYourType()
  {
    return _myTypeOrigin;
  }

  /**
   * export the specified shape as a string
   * 
   * @param theWrapper
   *          the thing we are going to export
   * @return the shape in String form
   */
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    CompositeTrackWrapper track = (CompositeTrackWrapper) theWrapper;

    final DecimalFormat sig6 = new DecimalFormat("0.######");

    // output the origin
    String line = _myTypeOrigin + " ";

    // export the origin
    line += DebriefFormatDateTime.toStringHiRes(track.getStartDate());

    // the track name may contain spaces - wrap in quotes if we have to
    line += " " + ImportFix.wrapTrackName(track.getName());

    line += " " + ImportReplay.replaySymbolFor(track.getColor(), null);

    line += " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(
        track.getOrigin());

    // finally the depth
    WorldDistance depth = new WorldDistance(track.getOrigin().getDepth(),
        WorldDistance.METRES);
    line += " " + sig6.format(depth.getValueIn(WorldDistance.METRES));

    // now work through the legs
    Enumeration<Editable> legs = track.elements();
    while (legs.hasMoreElements())
    {
      // start on a new line
      line += System.lineSeparator();

      final String NULL = "NULL";

      Editable next = legs.nextElement();

      if (next instanceof PlanningSegment)
      {
        PlanningSegment leg = (PlanningSegment) next;

        // is it the closing leg?
        boolean closing = leg instanceof ClosingSegment;

        int model = leg.getCalculation();
        final String trackName = ImportFix.wrapTrackName(track.getName());

        final String legName = ImportFix.wrapTrackName(leg.getName());

        final WorldDistance rng = leg.getDistance();
        final String rngStr = rng != null ? sig6.format(rng.getValueIn(
            WorldDistance.YARDS)) : NULL;
        final Duration len = leg.getDuration();
        final String durStr = len != null ? sig6.format(len.getValueIn(
            Duration.SECONDS)) : NULL;
        final WorldSpeed spd = leg.getSpeed();
        final String spdStr = spd != null ? sig6.format(spd.getValueIn(
            WorldSpeed.Kts)) : NULL;

        switch (model)
        {
          case 0:
            line += _myTypePlanning_spd_time + " " + trackName + " " + legName
                + " " + spdStr + " " + durStr;
            break;
          case 1:
            line += _myTypePlanning_rng_time + " " + trackName + " " + legName
                + " " + rngStr + " " + durStr;
            break;
          case 2:
            line += _myTypePlanning_rng_spd + " " + trackName + " " + legName
                + " " + rngStr + " " + spdStr;
            break;
          default:
            break;
        }

        // and the course
        line += " " + sig6.format(leg.getCourse());

        String closeStr = closing ? CLOSING : "";

        line += " " + closeStr;
      }
    }
    line += System.lineSeparator();

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
    return val instanceof CompositeTrackWrapper;
  }

}
