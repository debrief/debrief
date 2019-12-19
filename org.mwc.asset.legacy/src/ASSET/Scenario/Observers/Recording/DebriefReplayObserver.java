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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Category;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

public class DebriefReplayObserver extends RecordStatusToFileObserverType
{
  static public class DebriefReplayInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data
     *          the Layers themselves
     */
    public DebriefReplayInfo(final DebriefReplayObserver data)
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
        {prop("Directory", "The directory to place Debrief data-files"), prop(
            "Active", "Whether this observer is active"),};

        return res;
      }
      catch (final java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }

  /**
   * keep our own little register of symbols for participant types - the method to retreive the
   * symbol for a participant type is a compleicated one
   */
  static private HashMap<String, String> _mySymbolRegister =
      new HashMap<String, String>();

  private static String colorFor(final String category)
  {
    String res;

    if (category == ASSET.Participants.Category.Force.RED)
      res = "C";
    else if (category == ASSET.Participants.Category.Force.BLUE)
      res = "A";
    else
      res = "I";

    return res;
  }

  private static void outputThisLocation(final WorldLocation loc,
      final java.io.OutputStreamWriter os, final String message)
  {
    final String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation
        .toString(loc);
    final String msg = ";TEXT: AA " + locStr + " " + message + System
        .getProperty("line.separator");
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

  /**
   * method to replace spaces in the vessel name with underscores
   *
   * @param name
   * @return
   */
  private static String wrapName(final String name)
  {
    final String res = name.replace(' ', '_');
    return res;
  }

  /**
   *
   * @param loc
   * @param stat
   * @param pt
   * @param newTime
   * @param targetFolder
   *          (optional) destination for lightweight tracks
   * @return
   */
  static public String writeDetailsToBuffer(
      final MWC.GenericData.WorldLocation loc,
      final ASSET.Participants.Status stat, final NetworkParticipant pt,
      final long newTime, final String targetFolder)
  {

    final StringBuffer buff = new StringBuffer();

    final String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation
        .toString(loc);

    long theTime = stat.getTime();

    if (theTime == TimePeriod.INVALID_TIME)
      theTime = newTime;

    final String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime
        .toString(theTime);

    // which force is it?
    final String force = pt.getCategory().getForce();

    // see if we can remember this symbol
    String hisSymbol = _mySymbolRegister.get(pt.getCategory().getType());
    if (hisSymbol == null)
    {
      // bugger. we haven't had this one before. retrieve it the long way
      hisSymbol = SymbolFactory.findIdForSymbolType(pt.getCategory().getType());

      // did we find one?
      if (hisSymbol == null)
        hisSymbol = "@";

      // ok, and remember it
      _mySymbolRegister.put(pt.getCategory().getType(), hisSymbol);

    }

    // get the symbol type
    String col = hisSymbol + colorFor(force);

    // do we have a target folder?
    if (targetFolder != null)
    {
      col += "[LAYER=" + targetFolder + "]";
    }

    // wrap the vessel name if we have to
    final String theName = wrapName(pt.getName());

    buff.append(dateStr);
    buff.append(" ");
    buff.append(theName);
    buff.append(" ");
    buff.append(col);
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

  /***************************************************************
   * constructor
   ***************************************************************/

  private final ArrayList<Integer> _recordedDetections =
      new ArrayList<Integer>();

  private final List<String> _formatHelpers;

  /**
   * if user wants track to go into a folder (as lightweight tracks).
   *
   */
  private String _targetFolder;

  /**
   * the (optional) type of sensor we listen to
   *
   */
  private String _subjectSensor;

  /**
   * whether to include sensor location (or just null)
   *
   */
  private boolean _includeSensorLocation = false;

  /** list of last messages for each participant
   * - only required if we're skipping duplicate activities
   */
  HashMap<String, String> _lastMessages = new HashMap<String, String>();

  private final boolean _skipDuplicateDecisions = true;
  
  /**
   * create a new monitor
   *
   * @param directoryName
   *          the directory to output the plots to
   * @param recordDetections
   *          whether to record detections
   * @param formatHelpers
   */
  public DebriefReplayObserver(final String directoryName,
      final String fileName, final boolean recordDetections,
      final boolean recordDecisions, final boolean recordPositions,
      final TargetType subjectToTrack, final String observerName,
      final boolean isActive, final List<String> formatHelpers)
  {
    super(directoryName, fileName, recordDetections, recordDecisions,
        recordPositions, subjectToTrack, observerName, isActive);
    _formatHelpers = formatHelpers;
  }

  /**
   * constructor respecting old constructor signature
   *
   * @param directoryName
   * @param fileName
   * @param recordDetections
   * @param observerName
   * @param isActive
   */
  public DebriefReplayObserver(final String directoryName,
      final String fileName, final boolean recordDetections,
      final String observerName, final boolean isActive)
  {
    this(directoryName, fileName, recordDetections, false, true, null,
        observerName, isActive, null);
  }

  /**
   * ok, create the property editor for this class
   *
   * @return the custom editor
   */
  @Override
  protected Editable.EditorType createEditor()
  {
    return new DebriefReplayObserver.DebriefReplayInfo(this);
  }

  public List<String> getFormatHelpers()
  {
    return _formatHelpers;
  }

  /**
   * determine the normal suffix for this file type
   */
  @Override
  protected String getMySuffix()
  {
    // hey, if we're just recording sensor data, we should be a
    // DSF file.
    final String suffix;
    if (getRecordDetections() && !getRecordDecisions() && !getRecordPositions())
    {
      suffix = "dsf";
    }
    else
    {
      suffix = "rep";
    }

    return suffix;
  }

  public String getSubjectSensor()
  {
    return _subjectSensor;
  }

  public String getTargetFolder()
  {
    return _targetFolder;
  }

  public boolean isIncludeSensorLocation()
  {
    return _includeSensorLocation;
  }

  @Override
  protected String newName(final String name)
  {
    return name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime
        .toString(System.currentTimeMillis()) + ".rep";
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
    final String topLeft = MWC.Utilities.TextFormatting.DebriefFormatLocation
        .toString(area.getTopLeft());
    final String botRight = MWC.Utilities.TextFormatting.DebriefFormatLocation
        .toString(area.getBottomRight());
    // String msg = ";TEXT: AA " + locStr + " " + message +
    // System.getProperty("line.separator");
    final String msg = ";RECT: @@ " + topLeft + " " + botRight + " some area "
        + System.getProperty("line.separator");
    try
    {
      // check our output file is created
      if (_os == null)
        super.createOutputFile();

      super._os.write(msg);
    }
    catch (final IOException e)
    {
      e.printStackTrace(); // To change body of catch statement use Options |
                           // File Templates.
    }
  }

  /**
   * note that we only output detections once some positions have been written to file, since
   * Debrief likes to know about tracks before loading sensor data
   *
   * @param loc
   * @param dtg
   * @param hostName
   * @param hostCategory
   * @param bearing
   * @param range
   * @param sensor_name
   * @param label
   */
  private void outputThisDetection(final WorldLocation loc, final long dtg,
      final String hostName, final Category hostCategory, final Float bearing,
      final WorldDistance range, final String sensor_name, final String label)
  {
    // first see if we have output any positions yet -
    // since Debrief wants to know the position of any tracks before it writes
    // to file
    // if (!haveOutputPositions)
    // return;

    final String locStr = isIncludeSensorLocation()
        ? MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc)
        : "NULL";

    final String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime
        .toString(dtg);

    final String force = hostCategory.getForce();
    String col;

    col = "@" + colorFor(force);

    String brgTxt = null;
    if (bearing == null)
    {
      brgTxt = "00.000";
    }
    else
    {
      brgTxt = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(
          bearing.floatValue());
    }

    String rangeTxt = null;
    if (range == null)
    {
      rangeTxt = "0000";
    }
    else
    {
      rangeTxt = MWC.Utilities.TextFormatting.GeneralFormat
          .formatOneDecimalPlace(range.getValueIn(WorldDistance.YARDS));
    }

    final String msg = ";SENSOR: " + dateStr + " " + wrapName(hostName) + " "
        + col + " " + locStr + " " + brgTxt + " " + rangeTxt + " " + sensor_name
        + " " + label + System.getProperty("line.separator");

    try
    {
      // have we already output this?
      final int hashCode = msg.hashCode();
      if (_recordedDetections.contains(hashCode))
      {
        // ok, skip it
      }
      else
      {
        // nope, this is a new one. output it.
        _os.write(msg);

        // and remember that we've output it
        _recordedDetections.add(hashCode);
      }

    }
    catch (final IOException e)
    {
      e.printStackTrace(); // To change body of catch statement use Options |
                           // File Templates.
    }

  }

  /**
   * note that we only output detections once some positions have been written to file, since
   * Debrief likes to know about tracks before loading sensor data
   * 
   * @param worldLocation
   *
   * @param loc
   * @param dtg
   * @param hostName
   * @param hostCategory
   * @param bearing
   * @param range
   * @param sensor_name
   * @param label
   */
  private void outputThisDetection2(final WorldLocation loc, final long dtg,
      final String hostName, final Category hostCategory, final Float bearing,
      final Float ambigBearing, final WorldDistance range,
      final String sensor_name, final String label, final Float freq)
  {
    final String locStr = isIncludeSensorLocation()
        ? MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc)
        : "NULL";

    final String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime
        .toString(dtg);

    final String force = hostCategory.getForce();
    String col;

    col = "@" + colorFor(force);

    String brgTxt = null;
    if (bearing == null)
    {
      brgTxt = "NULL";
    }
    else
    {
      brgTxt = MWC.Utilities.TextFormatting.GeneralFormat
          .formatTwoDecimalPlaces(bearing.floatValue());
    }

    String ambigTxt = null;
    if (ambigBearing == null)
    {
      ambigTxt = "NULL";
    }
    else
    {
      ambigTxt = MWC.Utilities.TextFormatting.GeneralFormat
          .formatTwoDecimalPlaces(ambigBearing.floatValue());
    }

    String freqTxt = null;
    if (freq == null)
    {
      freqTxt = "NULL";
    }
    else
    {
      freqTxt = MWC.Utilities.TextFormatting.GeneralFormat
          .formatThreeDecimalPlaces(freq.floatValue());
    }

    String rangeTxt = null;
    if (range == null)
    {
      rangeTxt = "NULL";
    }
    else
    {
      rangeTxt = MWC.Utilities.TextFormatting.GeneralFormat
          .formatOneDecimalPlace(range.getValueIn(WorldDistance.YARDS));
    }

    // do we have an ambig bearing? if we do, use Sensor2, else
    // use Sensor3
    final String msg;
    if ("NULL".equals(ambigTxt))
    {
      // use 3

      // ;SENSOR3: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B CCC.C
      // FFF.F GGG.G RRRR yy..yy xx..xx
      // ;; date, ownship name, symbology, sensor lat/long (or the single word NULL),
      // bearing (degs) [or the single word NULL], bearing accuracy (degs)
      // [or the single word NULL], frequency(Hz) [or the single word NULL],
      // frequency accuracy (Hz) [or the single word NULL], range(yds)
      // [or the single word NULL], sensor name, label (to end of line)
      msg = ";SENSOR3: " + dateStr + " " + wrapName(hostName) + " " + col + " "
          + locStr + " " + brgTxt + " NULL  " + freqTxt + " NULL " + rangeTxt
          + " " + sensor_name + " " + label + System.getProperty(
              "line.separator");
    }
    else
    {
      // use 2
      msg = ";SENSOR2: " + dateStr + " " + wrapName(hostName) + " " + col + " "
          + locStr + " " + brgTxt + " " + ambigTxt + " " + freqTxt + " "
          + rangeTxt + " " + sensor_name + " " + label + System.getProperty(
              "line.separator");
    }

    try
    {
      _os.write(msg);
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
    
    _lastMessages.clear();
  }

  public void setIncludeSensorLocation(final boolean includeSensorLocation)
  {
    this._includeSensorLocation = includeSensorLocation;
  }

  public void setSubjectSensor(final String subjectSensor)
  {
    this._subjectSensor = subjectSensor;
  }

  // ;RECT: @@ DD MM SS.S H DDD MM SS.S H DDMMSS H DDDMMSS H
  // ;; symb, tl corner lat & long, br corner lat & long

  public void setTargetFolder(final String targetFolder)
  {
    _targetFolder = targetFolder;
  }

  /**
   * output the build details to file
   */
  @Override
  protected void writeBuildDate(final String theBuildDate) throws IOException
  {
    _os.write(";; ASSET Build version:" + theBuildDate + System.getProperty(
        "line.separator"));
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
    _os.write(";; ASSET Output" + new java.util.Date() + " " + title);
    _os.write("" + System.getProperty("line.separator"));

    // any format helpers?
    if (_formatHelpers != null)
    {
      for (final String helper : _formatHelpers)
      {
        _os.write(helper);
        _os.write("" + System.getProperty("line.separator"));
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////
  // editable properties
  // ////////////////////////////////////////////////////////////////////

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
      if (_subjectSensor == null || _subjectSensor.equals(sensorName))
      {
        // wrap the sensor name, if we have to
        final String safeSensorName;
        if (sensorName.contains(" "))
        {
          safeSensorName = "\"" + sensorName + "\"";
        }
        else
        {
          safeSensorName = sensorName;
        }

        // hmm, do we have freq?
        if (de.getFreq() != null || de.isAmbiguous())
        {
          Float ambig = null;
          if (de.isAmbiguous())
          {
            ambig = (float) de.getAmbiguousBearing();
          }

          outputThisDetection2(de.getSensorLocation(), de.getTime(), pt
              .getName(), pt.getCategory(), de.getBearing(), ambig, de
                  .getRange(), safeSensorName, de.toString(), de.getFreq());

        }
        else
        {
          outputThisDetection(de.getSensorLocation(), de.getTime(), pt
              .getName(), pt.getCategory(), de.getBearing(), de.getRange(),
              safeSensorName, de.toString());
        }
      }
    }
  }

  @Override
  public void writeThesePositionDetails(final MWC.GenericData.WorldLocation loc,
      final ASSET.Participants.Status stat, final ASSET.ParticipantType pt,
      final long newTime)
  {
    // make a note that we've output some track positions now
    // (and are now happy to output sensor data)
    _haveOutputPositions = true;

    final String res = writeDetailsToBuffer(loc, stat, pt, newTime,
        _targetFolder);
    writeToFile(res);
  }

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
    // To change body of implemented methods use File | Settings | File
    // Templates.
    final String msg = ";NARRATIVE2: "
        + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(dtg) + " "
        + wrapName(pt.getName()) + " DECISION " + activity + System.getProperty(
            "line.separator");

    final String hisLastMessage = _lastMessages.get(pt.getName());
    if (!_skipDuplicateDecisions ||  !activity.equals(hisLastMessage))
    {
      writeToFile(msg);
      _lastMessages.put(pt.getName(), activity);
    }

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
