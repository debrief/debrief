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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

public class CSVExportDetectionsObserver extends RecordStatusToFileObserverType
{
  static public class DebriefReplayInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     * 
     * @param data
     *          the Layers themselves
     */
    public DebriefReplayInfo(final CSVExportDetectionsObserver data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     * 
     * @return property descriptions
     */
    @Override
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res =
            {prop("Directory", "The directory to place Debrief data-files"),
                prop("Active", "Whether this observer is active"),};

        return res;
      }
      catch (final java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }

  /**
   * ************************************************************ member methods
   * *************************************************************
   */

  static public String writeDetailsToBuffer(
      final MWC.GenericData.WorldLocation loc,
      final ASSET.Participants.Status stat, final NetworkParticipant pt,
      final long newTime)
  {

    final StringBuffer buff = new StringBuffer();

    final String locStr =
        MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);

    long theTime = stat.getTime();

    if (theTime == TimePeriod.INVALID_TIME)
    {
      theTime = newTime;
    }

    final String dateStr =
        MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(theTime);

    // which force is it?
    buff.append(dateStr);
    buff.append(" ");
    buff.append(pt.getName());
    buff.append(" ");
    buff.append(locStr);
    buff.append(" ");
    buff.append(df.format(stat.getCourse()));
    buff.append(" ");
    buff.append(df.format(stat.getSpeed().getValueIn(WorldSpeed.Kts)));
    buff.append(" ");
    buff.append(df.format(loc.getDepth()));
    buff.append(System.getProperty("line.separator"));

    return buff.toString();
  }

  /**
   * ************************************************************ member variables
   * *************************************************************
   */
  protected boolean _haveOutputPositions = false;

  private final ArrayList<Integer> _recordedDetections =
      new ArrayList<Integer>();

  /***************************************************************
   * constructor
   ***************************************************************/

  private String _subjectName;

  private String _sensorName;

  /**
   * create a new monitor
   * 
   * @param directoryName
   *          the directory to output the plots to
   * @param sensorName
   * @param recordDetections
   *          whether to record detections
   */
  public CSVExportDetectionsObserver(final String directoryName,
      final String fileName, final TargetType subjectToTrack,
      final String observerName, final boolean isActive,
      final String subjectName, final String sensorName)
  {
    super(directoryName, fileName, true, false, false, subjectToTrack,
        observerName, isActive);
    _subjectName = subjectName;
    _sensorName = sensorName;
  }

  /**
   * ok, create the property editor for this class
   * 
   * @return the custom editor
   */
  @Override
  protected Editable.EditorType createEditor()
  {
    return new CSVExportDetectionsObserver.DebriefReplayInfo(this);
  }

  /**
   * determine the normal suffix for this file type
   */
  @Override
  protected String getMySuffix()
  {
    return "csv";
  }

  public String getSensorName()
  {
    return _sensorName;
  }

  public String getSubjectName()
  {
    return _subjectName;
  }

  @Override
  protected String newName(final String name)
  {
    return name
        + "_"
        + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System
            .currentTimeMillis()) + ".csv";
  }

  /**
   * output this series of locations
   * 
   * @param thePath
   */
  public void outputTheseLocations(final WorldPath thePath)
  {
    final Collection<WorldLocation> pts = thePath.getPoints();
    int counter = 0;
    for (final Iterator<WorldLocation> iterator = pts.iterator(); iterator
        .hasNext();)
    {
      final WorldLocation location = iterator.next();
      outputThisLocation(location, _os, "p:" + ++counter);
    }
  }

  public void outputThisArea(final WorldArea area)
  {
    final String topLeft =
        MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(area
            .getTopLeft());
    final String botRight =
        MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(area
            .getBottomRight());
    // String msg = ";TEXT: AA " + locStr + " " + message +
    // System.getProperty("line.separator");
    final String msg =
        ";RECT: @@ " + topLeft + " " + botRight + " some area "
            + System.getProperty("line.separator");
    try
    {
      // check our output file is created
      if (_os == null)
      {
        super.createOutputFile();
      }

      super._os.write(msg);
    }
    catch (final IOException e)
    {
      e.printStackTrace(); // To change body of catch statement use Options |
                           // File Templates.
    }
  }

  private void outputThisLocation(final WorldLocation loc,
      final java.io.OutputStreamWriter os, final String message)
  {
    final String locStr =
        MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);
    final String msg =
        ";TEXT: AA " + locStr + " " + message
            + System.getProperty("line.separator");
    try
    {
      os.write(msg);
    }
    catch (final IOException e)
    {
      e.printStackTrace(); // To change body of catch statement use Options |
                           // File Templates.
    }
  }

  @Override
  public void restart(final ScenarioType scenario)
  {
    super.restart(scenario);

    // and clear the stored detections
    _recordedDetections.clear();
  }

  public void setSensorName(final String _sensorName)
  {
    this._sensorName = _sensorName;
  }

  public void setSubjectName(final String _subjectName)
  {
    this._subjectName = _subjectName;
  }

  /**
   * output the build details to file
   */
  @Override
  protected void writeBuildDate(final String theBuildDate) throws IOException
  {
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
    _os.write("Time,Lat, Long,  Bearing (Degs), Frequency (Hz), Strength (dB)");

    // end the line
    _os.write(System.getProperty("line.separator"));
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
    // just double check this is us
    final boolean subjectValid =
        _subjectName == null || _subjectName.equals(pt.getName());

    if (subjectValid)
    {

      final Iterator<DetectionEvent> iter = detections.iterator();
      while (iter.hasNext())
      {
        final DetectionEvent de = iter.next();

        final SensorType theSensor = pt.getSensorAt(de.getSensor());
        final boolean sensorValid =
            _sensorName == null || _sensorName.equals(theSensor.getName());

        if (sensorValid)
        {
          final StringBuffer buff = new StringBuffer();

          final long theTime = de.getTime();

          final String dateStr =
              MWC.Utilities.TextFormatting.FullFormatDateTime
                  .toISOString(theTime);
          final WorldLocation loc = de.getSensorLocation();

          // _os.write("Date, Bearing (Degs), Frequency (Hz), Strength");
          
          DecimalFormat threeDP = new DecimalFormat("0.###");
          DecimalFormat sixDP = new DecimalFormat("0.######");


          buff.append(dateStr);
          buff.append(", ");
          buff.append(sixDP.format(loc.getLat()));
          buff.append(", ");
          buff.append(sixDP.format(loc.getLong()));
          buff.append(", ");
          buff.append(threeDP.format(de.getBearing()));
          buff.append(", ");
          
          final String freqStr = de.getFreq() == null ? "N/A" : threeDP.format(de.getFreq().floatValue());        
          buff.append(freqStr);
          
          buff.append(", ");
          
          final String strengthStr = de.getStrength() == null ? "N/A" : threeDP.format(de.getStrength().floatValue());
          buff.append(strengthStr);

          buff.append(System.getProperty("line.separator"));

          try
          {
            _os.write(buff.toString());
            _os.flush();
          }
          catch (final IOException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

      }
    }
  }

  @Override
  public void writeThesePositionDetails(
      final MWC.GenericData.WorldLocation loc,
      final ASSET.Participants.Status stat, final ASSET.ParticipantType pt,
      final long newTime)
  {
  }

  // ////////////////////////////////////////////////////////////////////
  // editable properties
  // ////////////////////////////////////////////////////////////////////

  /**
   * write the current decision description to file
   * 
   * @param pt
   *          the participant we're looking at
   * @param activity
   *          a description of the current activity
   * @param dtg
   *          the dtg at which the description was recorded
   */
  @Override
  protected void writeThisDecisionDetail(final NetworkParticipant pt,
      final String activity, final long dtg)
  {
  }
}
