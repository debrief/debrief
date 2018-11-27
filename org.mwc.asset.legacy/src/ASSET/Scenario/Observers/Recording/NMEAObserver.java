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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
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

  protected boolean _haveOutputPositions = false;

  private ArrayList<Integer> _recordedDetections = new ArrayList<Integer>();

  /**
   * the (optional) type of sensor we listen to
   * 
   */
  private final String _subjectSensor;

  /***************************************************************
   * constructor
   ***************************************************************/

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
    String res = "$POSL,POS,GPS," + pack(dLat, true) + "," + pack(dLong, false)
        + ",a,b,c,d" + LB;
    return res;
  }

  private final static String pack(double val, boolean isLat)
  {
    boolean isPos = val >= 0;
    double aVal = Math.abs(val);

    final int intPart = (int) Math.floor(aVal);
    final double floatPart = (aVal - intPart) * 60d;
    DecimalFormat p1 = new DecimalFormat("00");
    DecimalFormat p2 = new DecimalFormat("000");
    DecimalFormat p3 = new DecimalFormat("00.0000");

    String degs = isLat ? p1.format(intPart) : p2.format(intPart);
    String other = p3.format(floatPart);

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

  @Override
  public void restart(ScenarioType scenario)
  {
    super.restart(scenario);

    // and clear the stored detections
    _recordedDetections.clear();
  }

  public void writeThesePositionDetails(final WorldLocation loc,
      final Status stat, final ParticipantType pt,
      long newTime)
  {
    // make a note that we've output some track positions now
    // (and are now happy to output sensor data)
    _haveOutputPositions = true;

    // ok, we output DTG for all entries
    writeToFile(writeDTG(newTime));

    // course
    if (Math.random() <= 0.6)
    {
      String res = writeCourse(stat.getCourse());
      writeToFile(res);
    }

    // speed
    if (Math.random() <= 0.5)
    {
      String res = writeSpeed(stat.getSpeed().getValueIn(WorldSpeed.M_sec));
      writeToFile(res);
    }

    // position
    if (Math.random() <= 0.3)
    {
      String res = writeStatus(loc);
      writeToFile(res);
    }
  }

  final private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
  final private SimpleDateFormat time = new SimpleDateFormat("HHmmss.SSS");
  final private String LB = System.getProperty("line.separator");

  private String writeDTG(final long newTime)
  {
    String res = "$POSL,DZA," + date.format(new Date(newTime)) + "," + time
        .format(new Date(newTime)) + ",a,b,c,d" + LB;
    return res;
  }

  private String writeCourse(final double courseDegs)
  {
    String res = "$POSL,HDG," + courseDegs + ",a,b,c,d" + LB;
    return res;
  }

  private String writeSpeed(final double speedMs)
  {
    String res = "$POSL,VEL,SPL,a,b,c," + speedMs + ",a,b,c,d" + LB;
    return res;
  }

  /**
   * write this text to our stream
   * 
   * @param msg
   *          the string to write
   */
  private void writeToFile(String msg)
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
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
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
  protected void writeTheseDetectionDetails(ParticipantType pt,
      DetectionList detections, long dtg)
  {
    Iterator<DetectionEvent> iter = detections.iterator();
    while (iter.hasNext())
    {
      DetectionEvent de = (DetectionEvent) iter.next();

      final SensorType sensor = pt.getSensorFit().getSensorWithId(de
          .getSensor());
      final String sensorName = sensor.getName();

      // do we have a sensor name specified?
      if ((_subjectSensor == null || _subjectSensor.equals(sensorName)) && Math
          .random() <= 0.2)
      {
        final double brg = de.getBearing();
        final double rng = de.getRange().getValueIn(WorldDistance.DEGS);
        WorldVector offset = new WorldVector(brg, rng, 0d);
        WorldLocation loc = de.getSensorLocation().add(offset);
        writeToFile(writeAISContact(loc, de.getTarget()));
      }
    }
  }

  private String writeAISContact(WorldLocation loc, int target)
  {
    int MMSI = 100000 + target;
    String res = "$POSL,AIS," + MMSI + "," + pack(loc.getLat(), true) + ","
        + pack(loc.getLong(), false) + ",a,b,c,a,b,AIS1,c,d" + LB;
    return res;
  }

  /**
   * ok, create the property editor for this class
   * 
   * @return the custom editor
   */
  protected Editable.EditorType createEditor()
  {
    return new NMEAObserver.NMEAObserverInfo(this);
  }

  protected String newName(final String name)
  {
    return name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime
        .toString(System.currentTimeMillis()) + ".log";
  }

  /**
   * determine the normal suffix for this file type
   */
  protected String getMySuffix()
  {
    return "log";
  }

  /**
   * write out the file header details for this scenario
   * 
   * @param title
   *          the scenario we're describing
   * @throws IOException
   */

  protected void writeFileHeaderDetails(final String title, long currentDTG)
      throws IOException
  {
    _os.write("$POSL" + LB);
  }

  /**
   * output the build details to file
   */
  protected void writeBuildDate(String theBuildDate) throws IOException
  {
    // ok, skip.
  }


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
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Directory", "The directory to place Debrief data-files"), prop(
            "Active", "Whether this observer is active"),};

        return res;
      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  @Override
  protected void writeThisDecisionDetail(NetworkParticipant pt, String activity,
      long dtg)
  {
    throw new IllegalArgumentException("Not implemented for NMEA export");
  }
}
