/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
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

public class BMRObserver extends RecordStatusToFileObserverType
{

  static public class BMRObserverInfo extends EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data
     *          the Layers themselves
     */
    public BMRObserverInfo(final BMRObserver data)
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

  /***************************************************************
   * constructor
   ***************************************************************/

  protected boolean _haveOutputPositions = false;
  /**
   * the (optional) type of sensor we listen to
   *
   */
  private final String _subjectSensor;

  final private SimpleDateFormat DAY_MARKER = new SimpleDateFormat("dd MMM yy");

  final private SimpleDateFormat TIME_MARKER = new SimpleDateFormat("ddHHmm");

  final private DecimalFormat ONE_DP = new DecimalFormat("0.0");

  final private String LB = System.getProperty("line.separator");

  String lastActivity;

  Date lastDay = null;

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
  public BMRObserver(final String directoryName, final String fileName,
      final TargetType subjectToTrack, final String observerName,
      final boolean isActive, final String subjectSensor)
  {
    super(directoryName, fileName, true, true, true, subjectToTrack,
        observerName, isActive);
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
    return new BMRObserver.BMRObserverInfo(this);
  }

  /**
   * write this text to our stream
   *
   * @param msg
   *          the string to write
   */
  private void doWriteToFile(final Long dtg, final String msg)
  {
    if (msg != null)
    {

      final String dateMsg;
      if (dtg != null)
      {
        dateMsg = TIME_MARKER.format(new Date(dtg)) + "\t" + msg + LB;
      }
      else
      {
        dateMsg = msg;
      }

      try
      {
        if (_os == null)
          super.createOutputFile();

        _os.write(dateMsg);
        _os.flush();
      }
      catch (final IOException e)
      {
        e.printStackTrace();
      }
    }
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

  /**
   * output the build details to file
   */
  @Override
  protected void writeBuildDate(final String theBuildDate) throws IOException
  {
    // ok, skip.
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
          .random() <= 0.1)
      {
        final double brg = MWC.Algorithms.Conversions.Degs2Rads(de
            .getBearing());
        final String tgtId = "" + de.getTarget();
        final String trimmedTgt = tgtId.substring(3);
        final double rng = de.getRange().getValueIn(WorldDistance.KM);
        final String FCS = "SR" + trimmedTgt + " AAAA 1936 BBB (Lost)  B-"
            + (int) brg + " R-" + ONE_DP.format(rng)
            + "kyds C-321 S-6kts  Classified AAAAAA BBBBBB AAAAAA";
        writeToFile(dtg, FCS);
      }
    }
  }

  @Override
  protected void writeThesePositionDetails(final WorldLocation loc,
      final Status stat, final ParticipantType pt, final long newTime)
  {
    final double thisNum = Math.random();

    if (thisNum > 0.8)
    {
      writeToFile(newTime, "CSD Crse:" + (int) stat.getCourse() + " Speed:"
          + ONE_DP.format(stat.getSpeed().getValue()));
    }
    if (thisNum > 0.95)
    {
      writeToFile(newTime, "Weather. Speed:" + (int) stat.getSpeed().getValueIn(
          WorldSpeed.M_sec) + " Speed:" + ONE_DP.format(stat.getCourse()));
    }

  }

  @Override
  protected void writeThisDecisionDetail(final NetworkParticipant pt,
      final String activity, final long dtg)
  {
    if (!activity.equals(lastActivity))
    {
      writeToFile(dtg, activity);
    }

    lastActivity = activity;
  }

  /**
   * write this text to our stream
   *
   * @param msg
   *          the string to write
   */
  @SuppressWarnings("deprecation")
  private void writeToFile(final long dtg, final String msg)
  {
    // have we changed day?
    final Date thisDay = new Date(dtg);
    thisDay.setHours(0);
    thisDay.setMinutes(0);
    thisDay.setSeconds(0);

    if (lastDay == null)
    {
      // ok, write the header
      doWriteToFile(null, "START OF RECORDS FOR TRIAL");
    }

    if (lastDay == null || !thisDay.equals(lastDay))
    {
      lastDay = thisDay;
      final String dateMarker = DAY_MARKER.format(lastDay) + LB;
      doWriteToFile(null, dateMarker);
    }

    doWriteToFile(dtg, msg);
  }
}
