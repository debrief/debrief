/*
 * Desciption: Class which produces a debrief-replay file of events in a scenario
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 9:14:23 AM
 */
package ASSET.Scenario.Observers.Recording;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import java.io.IOException;

public class CSVTrackObserver
  extends RecordStatusToFileObserverType
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * create a new monitor
   *
   * @param directoryName    the directory to output the plots to
   * @param recordDetections whether to record detections
   */
  public CSVTrackObserver(final String directoryName,
                          final String fileName,
                          final boolean recordDetections,
                          final boolean recordDecisions,
                          final boolean recordPositions,
                          final TargetType subjectType,
                          final String observerName,
                          final boolean isActive)
  {
    super(directoryName, fileName, recordDetections, recordDecisions, recordPositions, subjectType, observerName, isActive);
  }

  /**
   * create a new monitor (using the old constructor)
   *
   * @param directoryName    the directory to output the plots to
   * @param recordDetections whether to record detections
   */
  public CSVTrackObserver(final String directoryName,
                          final String fileName,
                          final boolean recordDetections,
                          final String observerName,
                          final boolean isActive)
  {
    super(directoryName, fileName, recordDetections, false, true, null, observerName, isActive);
  }


  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  public void writeThesePositionDetails(final MWC.GenericData.WorldLocation loc,
                                        final ASSET.Participants.Status stat,
                                        final ASSET.ParticipantType pt,
                                        long newTime)
  {

    final StringBuffer buff = new StringBuffer();

    String res;

    // convert the location to flat-earth yards
    double yM = MWC.Algorithms.Conversions.Degs2m(loc.getLat());
    double xM = MWC.Algorithms.Conversions.Degs2m(loc.getLong());
    double zM = -loc.getDepth();

    long theTime = stat.getTime();
    if (theTime == TimePeriod.INVALID_TIME)
      theTime = newTime;

    final String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(theTime);

    // get the demanded status
    DemandedStatus demStat = pt.getDemandedStatus();

    // get the activity
    String activity = pt.getActivity();

    buff.append(dateStr);
    buff.append(",");
    buff.append(pt.getName());
    buff.append(",");
    buff.append(df.format(xM));
    buff.append(",");
    buff.append(df.format(yM));
    buff.append(",");
    buff.append(df.format(zM));
    buff.append(",");
    buff.append(df.format(stat.getCourse()));
    buff.append(",");
    buff.append(df.format(stat.getSpeed().getValueIn(WorldSpeed.M_sec)));

    // do we have simple dem stat?
    if (demStat instanceof SimpleDemandedStatus)
    {
      buff.append(",");
      buff.append(df.format(((SimpleDemandedStatus) demStat).getCourse()));
      buff.append(",");
      buff.append(df.format(((SimpleDemandedStatus) demStat).getSpeed()));
      buff.append(",");
      buff.append(df.format(((SimpleDemandedStatus) demStat).getHeight()));
    }
    else
    {
      if (demStat instanceof HighLevelDemandedStatus)
      {
        HighLevelDemandedStatus ds = (HighLevelDemandedStatus) demStat;
        buff.append(",");
        buff.append("heading for waypoint#" + (ds.getCurrentTargetIndex() + 1));

        buff.append(",");

        WorldSpeed demSpeed = ds.getSpeed();
        if (demSpeed != null)
        {
          buff.append(df.format(demSpeed.getValueIn(WorldSpeed.M_sec)));
        }
        else
        {

        }

      }
    }
    buff.append(",");
    buff.append(df.format(stat.getFuelLevel()));
    buff.append(",");
    buff.append(activity);
    res = buff.toString();

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

  /**
   * write the current decision description to file
   *
   * @param pt       the participant we're looking at
   * @param activity a description of the current activity
   * @param dtg      the dtg at which the description was recorded
   */
  protected void writeThisDecisionDetail(ParticipantType pt, String activity, long dtg)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * write these detections to file
   *
   * @param pt         the participant we're on about
   * @param detections the current set of detections
   * @param dtg        the dtg at which the detections were observed
   */
  protected void writeTheseDetectionDetails(ParticipantType pt, DetectionList detections, long dtg)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void outputThisDetection(WorldLocation loc, long dtg, String hostName, Category hostCategory,
                                  double bearing, WorldDistance range, String sensor_name,
                                  String label)
  {
    // don't bother
    // todo: to implement (output this detection)
  }

  /**
   * ok, create the property editor for this class
   *
   * @return the custom editor
   */
  protected Editable.EditorType createEditor()
  {
    return new CSVTrackObserver.CSVTrackObserverInfo(this);
  }

  protected String newName(final String name)
  {
    return "res_" + name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System.currentTimeMillis()) + ".csv";
  }

  /**
   * determine the normal suffix for this file type
   */
  protected String getMySuffix()
  {
    return "csv";
  }

  /**
   * write out the file header details for this scenario
   *
   * @param title the scenario we're describing
   * @throws IOException
   */

  protected void writeFileHeaderDetails(final String title, long currentDTG) throws IOException
  {
    _os.write(";; ASSET Output recorded at:," + new java.util.Date() + ", name:," + title);
    _os.write("" + System.getProperty("line.separator"));
    _os.write("DTG,Track name, x (m), y (m), z (m), course (degs), speed (m/s), dem course (Degs), dem speed (m/s), dem depth (m), fuel, activity");
    _os.write("" + System.getProperty("line.separator"));
  }

  protected void writeBuildDate(String details) throws IOException
  {
    _os.write(";; ASSET build:," + details + System.getProperty("line.separator"));
  }
  //////////////////////////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////////////////////////

  static public class CSVTrackObserverInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public CSVTrackObserverInfo(final CSVTrackObserver data)
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
        final java.beans.PropertyDescriptor[] res = {
          prop("Directory", "The directory to place Debrief data-files"),
          prop("Active", "Whether this observer is active"),
        };

        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }
}
