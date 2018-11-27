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
package ASSET.Scenario.Observers.Recording;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import junit.framework.TestCase;

public class NMEAObserver extends RecordStatusToFileObserverType
{

  static public class NMEAObserverInfo extends EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data
     *          the Layers themselves
     */
    public NMEAObserverInfo(final NMEAObserver data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Directory", "The directory to place Debrief data-files"), prop(
            "Active", "Whether this observer is active"),};

        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  public static class TestConvert extends TestCase
  {
    public static void testPack()
    {
      assertEquals("valid string", "0000.0000,N", pack(0, true));
      assertEquals("valid string", "00000.0000,E", pack(0, false));

      assertEquals("valid string", "4530.0000,N", pack(45.5, true));
      assertEquals("valid string", "04530.0000,E", pack(45.5, false));

      assertEquals("valid string", "0000.6000,S", pack(-0.01, true));
      assertEquals("valid string", "00000.6000,W", pack(-0.01, false));

      assertEquals("valid string", "4530.0000,S", pack(-45.5, true));
      assertEquals("valid string", "04530.0000,W", pack(-45.5, false));
    }
  }

  private final static String pack(final double val, final boolean isLat)
  {
    final boolean isPos = val >= 0;
    final double aVal = Math.abs(val);

    final int intPart = (int) Math.floor(aVal);
    final double floatPart = (aVal - intPart) * 60d;
    final DecimalFormat p1 = new DecimalFormat("00");
    final DecimalFormat p2 = new DecimalFormat("000");
    final DecimalFormat p3 = new DecimalFormat("00.0000");

    final String degs = isLat ? p1.format(intPart) : p2.format(intPart);
    final String other = p3.format(floatPart);

    final String hemi;
    if (isLat)
    {
      hemi = isPos ? "N" : "S";
    }
    else
    {
      hemi = isPos ? "E" : "W";
    }

    return degs + other + "," + hemi;
  }

  /***************************************************************
   * constructor
   ***************************************************************/

  protected boolean _haveOutputPositions = false;
  /**
   * the (optional) type of sensor we listen to
   *
   */
  private final String _subjectSensor;

  final private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

  final private SimpleDateFormat time = new SimpleDateFormat("HHmmss.SSS");

  final private String LB = System.getProperty("line.separator");

  /**
   * create a new monitor
   *
   * @param directoryName
   *          the directory to output the plots to
   * @param recordDetections
   *          whether to record detections
   * @param subjectSensor
   * @param formatHelpers
   */
  public NMEAObserver(final String directoryName, final String fileName,
      final boolean recordDetections, final boolean recordPositions,
      final TargetType subjectToTrack, final String observerName,
      final boolean isActive, final String subjectSensor)
  {
    super(directoryName, fileName, recordDetections, false, recordPositions,
        subjectToTrack, observerName, isActive);
    _subjectSensor = subjectSensor;
  }

  /**
   * ok, create the property editor for this class
   *
   * @return the custom editor
   */
  @Override
  protected Editable.EditorType createEditor()
  {
    return new NMEAObserver.NMEAObserverInfo(this);
  }

  /**
   * determine the normal suffix for this file type
   */
  @Override
  protected String getMySuffix()
  {
    return "log";
  }

  @Override
  protected String newName(final String name)
  {
    return name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime
        .toString(System.currentTimeMillis()) + ".log";
  }

  private String writeAISContact(final WorldLocation loc, final int target)
  {
    final int MMSI = 100000 + target;
    final String res = "$POSL,AIS," + MMSI + "," + pack(loc.getLat(), true)
        + "," + pack(loc.getLong(), false) + ",a,b,c,a,b,AIS1,c,d" + LB;
    return res;
  }

  /**
   * output the build details to file
   */
  @Override
  protected void writeBuildDate(final String theBuildDate) throws IOException
  {
    // ok, skip.
  }

  private String writeCourse(final double courseDegs)
  {
    final String res = "$POSL,HDG," + courseDegs + ",a,b,c,d" + LB;
    return res;
  }

  private String writeDTG(final long newTime)
  {
    final String res = "$POSL,DZA," + date.format(new Date(newTime)) + ","
        + time.format(new Date(newTime)) + ",a,b,c,d" + LB;
    return res;
  }

  /**
   * write out the file header details for this scenario
   *
   * @param title
   *          the scenario we're describing
   * @throws IOException
   */

  @Override
  protected void writeFileHeaderDetails(final String title,
      final long currentDTG) throws IOException
  {
    _os.write("$POSL" + LB);
  }

  private String writeSpeed(final double speedMs)
  {
    final String res = "$POSL,VEL,SPL,a,b,c," + speedMs + ",a,b,c,d" + LB;
    return res;
  }

  /**
   *
   * @param loc
   * @param stat
   * @param pt
   * @param newTime
   * @return
   */
  private String writeStatus(final WorldLocation loc)
  {
    final double dLat = loc.getLat();
    final double dLong = loc.getLong();
    final String res = "$POSL,POS,GPS," + pack(dLat, true) + "," + pack(dLong,
        false) + ",a,b,c,d" + LB;
    return res;
  }

  /**
   * write these detections to file
   *
   * @param pt
   *          the participant we're on about
   * @param detections
   *          the current set of detections
   * @param dtg
   *          the dtg at which the detections were observed
   */
  @Override
  protected void writeTheseDetectionDetails(final ParticipantType pt,
      final DetectionList detections, final long dtg)
  {
    final Iterator<DetectionEvent> iter = detections.iterator();
    while (iter.hasNext())
    {
      final DetectionEvent de = iter.next();

      final SensorType sensor = pt.getSensorFit().getSensorWithId(de
          .getSensor());
      final String sensorName = sensor.getName();

      // do we have a sensor name specified?
      if ((_subjectSensor == null || _subjectSensor.equals(sensorName)) && Math
          .random() <= 0.2)
      {
        final double brg = de.getBearing();
        final double rng = de.getRange().getValueIn(WorldDistance.DEGS);
        final WorldVector offset = new WorldVector(brg, rng, 0d);
        final WorldLocation loc = de.getSensorLocation().add(offset);
        writeToFile(writeAISContact(loc, de.getTarget()));
      }
    }
  }

  @Override
  protected void writeThesePositionDetails(final WorldLocation loc,
      final Status stat, final ParticipantType pt, final long newTime)
  {
    // make a note that we've output some track positions now
    // (and are now happy to output sensor data)
    _haveOutputPositions = true;

    // ok, we output DTG for all entries
    writeToFile(writeDTG(newTime));

    // course
    if (Math.random() <= 0.6)
    {
      final String res = writeCourse(stat.getCourse());
      writeToFile(res);
    }

    // speed
    if (Math.random() <= 0.5)
    {
      final String res = writeSpeed(stat.getSpeed().getValueIn(
          WorldSpeed.M_sec));
      writeToFile(res);
    }

    // position
    if (Math.random() <= 0.3)
    {
      final String res = writeStatus(loc);
      writeToFile(res);
    }
  }

  @Override
  protected void writeThisDecisionDetail(final NetworkParticipant pt,
      final String activity, final long dtg)
  {
    throw new IllegalArgumentException("Not implemented for NMEA export");
  }

  /**
   * write this text to our stream
   *
   * @param msg
   *          the string to write
   */
  private void writeToFile(final String msg)
  {
    if (msg != null)
    {
      try
      {
        if (_os == null)
          super.createOutputFile();

        _os.write(msg);
        _os.flush();
      }
      catch (final IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
