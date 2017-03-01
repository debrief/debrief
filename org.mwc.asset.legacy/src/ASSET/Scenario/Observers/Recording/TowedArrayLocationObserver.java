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
import java.util.HashMap;
import java.util.List;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionList;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * record the probable location of array elements
 * 
 * @author ian
 * 
 */
public class TowedArrayLocationObserver extends RecordStatusToFileObserverType
{
  /***************************************************************
   * member variables
   ***************************************************************/
  final private HashMap<String, TrackWrapper> _trackList =
      new HashMap<String, TrackWrapper>();
  final private List<Double> _offsets;
  final private String _messageName;
  final private String _sensorName;
  final private double _defaultDepth;

  final private RecorderType _recorderType;

  public static enum RecorderType
  {
    HDG_DEPTH, LOC_RELATIVE, LOC_ABS
  }

  /***************************************************************
   * constructor
   ***************************************************************/
  /**
   * create a new monitor
   * 
   * @param directoryName
   *          the directory to output the plots to
   * @param defaultDepth
   * @param recordDetections
   *          whether to record detections
   */
  public TowedArrayLocationObserver(final String directoryName,
      final String fileName, final TargetType subjectType,
      final String observerName, final boolean isActive,
      final List<Double> offsets, final RecorderType recorderType,
      final String messageName, final double defaultDepth,
      final String sensorName)
  {
    super(directoryName, fileName, false, false, true, subjectType,
        observerName, isActive);

    // store the list of offsets
    _offsets = offsets;

    _defaultDepth = defaultDepth;
    _sensorName = sensorName;
    _recorderType = recorderType;
    _messageName = messageName;
  }

  /**
   * ************************************************************ member methods
   * *************************************************************
   */

  public void writeThesePositionDetails(
      MWC.GenericData.WorldLocation platformLocation,
      final ASSET.Participants.Status stat, final ASSET.ParticipantType pt,
      long newTime)
  {
    final String ptName = pt.getName();
    final HiResDate debTime = new HiResDate(newTime);

    // find this track
    TrackWrapper track = _trackList.get(ptName);
    if (track == null)
    {
      track = new TrackWrapper();
      track.setName(ptName);
      _trackList.put(ptName, track);
    }

    // find the last position in the track
    Watchable[] lastItems = track.getNearestTo(track.getEndDTG());
    WorldLocation lastLoc = null;
    double lastDepth = _defaultDepth;
    if (lastItems != null)
    {
      if (lastItems.length > 0)
      {
        Watchable lastItem = lastItems[0];
        lastLoc = lastItem.getLocation();
        lastDepth = lastLoc.getDepth();
      }
    }

    // sort out the heading for the item
    final double bearingRads;
    if (lastLoc != null)
    {
      bearingRads = platformLocation.subtract(lastLoc).getBearing();
    }
    else
    {
      bearingRads = 0d;
    }

    // add the new fix to the track
    Fix newFix = new Fix(debTime, platformLocation, bearingRads, 4d);

    final double randomDepth = lastDepth + Math.random() * 1d;

    newFix.getLocation().setDepth(randomDepth);
    FixWrapper wrapper = new FixWrapper(newFix);
    track.addFix(wrapper);

    final StringBuffer theseValues = new StringBuffer();

    DecimalFormat df = new DecimalFormat("0.00");

    // ok, see if we are ready to sort out the array locations
    for (Double thisO : _offsets)
    {
      ArrayLength sensorOffset = new ArrayLength(thisO);

      // get the backtrace to this distance
      FixWrapper pos = track.getBacktraceTo(debTime, sensorOffset, true);
      if (pos != null)
      {
        final WorldLocation aLoc = pos.getLocation();
        final double depthM = pos.getDepth();

        // ok, output the relevant data
        switch (_recorderType)
        {
        case HDG_DEPTH:
          // get the heading and depth
          double hdgDegs = pos.getCourseDegs();
          theseValues.append("[" + df.format(depthM) + " " + df.format(hdgDegs)
              + "] ");
          break;
        case LOC_ABS:
          theseValues.append("" + aLoc.getLat() + " " + aLoc.getLong() + " "
              + df.format(depthM));
          break;
        case LOC_RELATIVE:
          // ok, we need an offset here
          WorldVector offset = aLoc.subtract(platformLocation);

          // ok, we need to convert to metres
          double rangeM =
              new WorldDistance(offset.getRange(), WorldDistance.DEGS)
                  .getValueIn(WorldDistance.METRES);

          // and the two components
          double xDelta = rangeM * Math.sin(offset.getBearing());
          double yDelta = rangeM * Math.cos(offset.getBearing());

          theseValues.append(df.format(xDelta) + " " + df.format(yDelta) + " "
              + depthM);
          // create xy from this
          break;
        }

      }
    }

    // do we have anything to output?
    if (theseValues.length() > 0)
    {
      // ok, go for it.
      final StringBuffer buff = new StringBuffer();

      // dtg
      final String dtg = DebriefFormatDateTime.toString(newTime);

      // message type
      buff.append(";");
      buff.append(_messageName);
      buff.append(": ");
      buff.append(dtg);
      buff.append(" ");
      buff.append(pt.getName());
      buff.append(" ");
      buff.append(_sensorName);
      buff.append(" ");
      buff.append(theseValues.toString());

      final String res = buff.toString();

      if (res != null)
      {
        try
        {
          _os.write(res);
          _os.write("" + System.getProperty("line.separator"));
          _os.flush();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }

  }

  @Override
  protected void performSetupProcessing(ScenarioType scenario)
  {
    // ok, let the parent do its stuff
    super.performSetupProcessing(scenario);

    // clear our set of tracks
    _trackList.clear();
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
  protected void writeThisDecisionDetail(NetworkParticipant pt,
      String activity, long dtg)
  {
    // To change body of implemented methods use File | Settings | File Templates.
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
    throw new UnsupportedOperationException("Method not implemented");
  }

  protected String newName(final String name)
  {
    return "res_"
        + name
        + "_"
        + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System
            .currentTimeMillis()) + ".csv";
  }

  /**
   * determine the normal suffix for this file type
   */
  protected String getMySuffix()
  {
    return "dsf";
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
    _os.write(";; ASSET Output recorded at:," + new java.util.Date()
        + ", name:," + title);
    _os.write("" + System.getProperty("line.separator"));
    _os.write(";; See Debrief reference document for file formats");
    _os.write("" + System.getProperty("line.separator"));
  }

  protected void writeBuildDate(String details) throws IOException
  {
    _os.write(";; ASSET build:," + details
        + System.getProperty("line.separator"));
  }

  // ////////////////////////////////////////////////////////////////////
  // editable properties
  // ////////////////////////////////////////////////////////////////////

  static public class TowedArrayLocationObserverInfo extends
      Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     * 
     * @param data
     *          the Layers themselves
     */
    public TowedArrayLocationObserverInfo(final TowedArrayLocationObserver data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     * 
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res =
            {prop("Directory", "The directory to place Debrief data-files"),
                prop("Active", "Whether this observer is active"),};

        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }
}
