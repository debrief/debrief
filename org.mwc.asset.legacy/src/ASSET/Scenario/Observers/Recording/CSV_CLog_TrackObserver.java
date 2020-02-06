
package ASSET.Scenario.Observers.Recording;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class CSV_CLog_TrackObserver
  extends RecordStatusToFileObserverType
{
  /***************************************************************
   *  member variables
   ***************************************************************/
  private static final String CSV_DATE_FORMAT = "dd MMM yyyy - HH:mm:ss.SSS";
  private SimpleDateFormat _df;
  private DecimalFormat _nf;
  
  /***************************************************************
   *  constructor
   ***************************************************************/

  /**
   * create a new monitor (using the old constructor)
   *
   * @param directoryName    the directory to output the plots to
   * @param recordDetections whether to record detections
   */
  public CSV_CLog_TrackObserver(final String directoryName,
                          final String fileName,
                          final boolean recordDetections,
                          final String observerName,
                          final boolean isActive)
  {
    super(directoryName, fileName, recordDetections, false, true, null, observerName, isActive);
    _df = new SimpleDateFormat(CSV_DATE_FORMAT);
    _nf = new java.text.DecimalFormat("0.000000000");

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

    long theTime = stat.getTime();
    if (theTime == TimePeriod.INVALID_TIME)
      theTime = newTime;

    final String dateStr = _df.format(new Date(stat.getTime()));

    // get the demanded status
    DemandedStatus demStat = pt.getDemandedStatus();

    // get the activity
    String activity = pt.getActivity();

    buff.append(dateStr);
    buff.append(",");
    buff.append(pt.getName());
    buff.append(",");
    buff.append(pt.getName());
    buff.append(",");
    buff.append(pt.getName());
    buff.append(",");
    buff.append("123456789012"+pt.getId());
    buff.append(",attr_latitude,");
    buff.append(_nf.format(Math.toRadians(loc.getLat())));
    buff.append(",attr_longitude,");
    buff.append(_nf.format(Math.toRadians(loc.getLong())));
    buff.append(",attr_course,");
    buff.append(df.format(Math.toRadians(stat.getCourse())));
    buff.append(",attr_speed,");
    buff.append(df.format(stat.getSpeed().getValueIn(WorldSpeed.M_sec)));
    buff.append(",attr_trackNumber,");
    
    
    final String trackId;
    if(pt.getCategory().getForce().equals(Category.Force.BLUE))
    {
      trackId = "1";
    }
    else
    {
      final String tmpId = "" + pt.getId();
      if(tmpId.length() > 4)
      {
        final int len = tmpId.length();
        trackId = tmpId.substring(len - 4);
      }
      else
      {
        trackId = tmpId;
      }
    }
    
    buff.append(trackId);
    buff.append(",");
    buff.append(",attr_countryAbbreviation,");
    if(!pt.getCategory().getForce().equals(Category.Force.GREEN))
    {
      buff.append(pt.getCategory().getForce());
    }
    

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
  protected void writeThisDecisionDetail(NetworkParticipant pt, String activity, long dtg)
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
    return new CSV_CLog_TrackObserver.CSVTrackObserverInfo(this);
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
  }

  protected void writeBuildDate(String details) throws IOException
  {
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
    public CSVTrackObserverInfo(final CSV_CLog_TrackObserver data)
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
