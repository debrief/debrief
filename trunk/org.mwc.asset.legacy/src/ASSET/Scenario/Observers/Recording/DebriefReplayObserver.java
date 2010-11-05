/*
 * Desciption: Class which produces a debrief-replay file of events in a scenario
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 9:14:23 AM
 */
package ASSET.Scenario.Observers.Recording;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class DebriefReplayObserver extends RecordStatusToFileObserverType
{
  /**
   * ************************************************************
   * member variables
   * *************************************************************
   */
	protected boolean _haveOutputPositions = false;

  /**
   * keep our own little register of symbols for participant types
   * - the method to retreive the symbol for a participant type is a compleicated one
   */
  static private HashMap<String, String> _mySymbolRegister = new HashMap<String, String>();
  
  /***************************************************************
   *  constructor
   ***************************************************************/


  /**
   * create a new monitor
   *
   * @param directoryName    the directory to output the plots to
   * @param recordDetections whether to record detections
   */
  public DebriefReplayObserver(final String directoryName,
                               final String fileName,
                               final boolean recordDetections,
                               final boolean recordDecisions,
                               final boolean recordPositions,
                               final TargetType subjectToTrack,
                               final String observerName,
                               boolean isActive)
  {
    super(directoryName, fileName, recordDetections, recordDecisions, recordPositions, subjectToTrack, observerName, isActive);
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
                               final String fileName,
                               final boolean recordDetections,
                               final String observerName,
                               boolean isActive)
  {
    this(directoryName, fileName, recordDetections, false, true, null, observerName, isActive);
  }

  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */


  static public String writeDetailsToBuffer(final MWC.GenericData.WorldLocation loc,
                                            final ASSET.Participants.Status stat,
                                            final NetworkParticipant pt,
                                            long newTime)
  {

    StringBuffer buff = new StringBuffer();

    final String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);

    long theTime = stat.getTime();

    if (theTime == TimePeriod.INVALID_TIME)
      theTime = newTime;

    final String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(theTime);

    // which force is it?
    final String force = pt.getCategory().getForce();

    // see if we can remember this symbol
    String hisSymbol = (String) _mySymbolRegister.get(pt.getCategory().getType());
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

    // wrap the vessel name if we have to
    String theName = wrapName(pt.getName());

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
   * method to replace spaces in the vessel name with underscores
   *
   * @param name
   * @return
   */
  private static String wrapName(String name)
  {
    String res = name.replace(' ', '_');
    return res;
  }


  public void writeThesePositionDetails(final MWC.GenericData.WorldLocation loc,
                                        final ASSET.Participants.Status stat,
                                        final ASSET.ParticipantType pt,
                                        long newTime)
  {
    // make a note that we've output some track positions now
    // (and are now happy to output sensor data)
    _haveOutputPositions = true;

    String res = writeDetailsToBuffer(loc, stat, pt, newTime);
    writeToFile(res);
  }

  /**
   * write this text to our stream
   *
   * @param msg the string to write
   */
  private void writeToFile(String msg)
  {
    if (msg != null)
    {
      try
      {
      	if(_os == null)
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
   * @param pt         the participant we're on about
   * @param detections the current set of detections
   * @param dtg        the dtg at which the detections were observed
   */
  protected void writeTheseDetectionDetails(ParticipantType pt, DetectionList detections, long dtg)
  {
    Iterator<DetectionEvent> iter = detections.iterator();
    while (iter.hasNext())
    {
      DetectionEvent de = (DetectionEvent) iter.next();
      outputThisDetection(de.getSensorLocation(), dtg, pt.getName(), pt.getCategory(), de.getBearing(),
                          de.getRange(), pt.getSensorFit().getSensorWithId(de.getSensor()).getName(), de.toString());
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
    String msg = ";NARRATIVE2: " + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(dtg) + " " + wrapName(pt.getName()) + " DECISION " + activity + System.getProperty("line.separator");
    writeToFile(msg);
  }


  /**
   * ok, create the property editor for this class
   *
   * @return the custom editor
   */
  protected Editable.EditorType createEditor()
  {
    return new DebriefReplayObserver.DebriefReplayInfo(this);
  }

  protected String newName(final String name)
  {
    return name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System.currentTimeMillis()) + ".rep";
  }

  /**
   * determine the normal suffix for this file type
   */
  protected String getMySuffix()
  {
    return "rep";
  }

  /**
   * write out the file header details for this scenario
   *
   * @param title the scenario we're describing
   * @throws IOException
   */

  protected void writeFileHeaderDetails(final String title, long currentDTG) throws IOException
  {
    _os.write(";; ASSET Output" + new java.util.Date() + " " + title);
    _os.write("" + System.getProperty("line.separator"));
  }

  /**
   * output the build details to file
   */
  protected void writeBuildDate(String theBuildDate) throws IOException
  {
    _os.write(";; ASSET Build version:" + theBuildDate + System.getProperty("line.separator"));
  }

  /**
   * output this series of locations
   *
   * @param thePath
   */
  public void outputTheseLocations(WorldPath thePath)
  {
    Collection<WorldLocation> pts = thePath.getPoints();
    int counter = 0;
    for (Iterator<WorldLocation> iterator = pts.iterator(); iterator.hasNext();)
    {
      WorldLocation location = (WorldLocation) iterator.next();
      outputThisLocation(location, _os, "p:" + ++counter);
    }
  }

  private void outputThisLocation(WorldLocation loc,
                                  java.io.OutputStreamWriter os,
                                  String message)
  {
    String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);
    String msg = ";TEXT: AA " + locStr + " " + message + System.getProperty("line.separator");
    try
    {
      os.write(msg);
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }

  public void outputThisArea(WorldArea area)
  {
    String topLeft = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(area.getTopLeft());
    String botRight = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(area.getBottomRight());
    //    String msg = ";TEXT: AA " + locStr + " " + message + System.getProperty("line.separator");
    String msg = ";RECT: @@ " + topLeft + " " + botRight + " some area " + System.getProperty("line.separator");
    try
    {
    	// check our output file is created
    	if(_os == null)
    		super.createOutputFile();
    	
      super._os.write(msg);
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }



  // ;RECT: @@ DD MM SS.S H DDD MM SS.S H DDMMSS H DDDMMSS H
  // ;; symb, tl corner lat & long, br corner lat & long


  private static String colorFor(String category)
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

  /**
   * note that we only output detections once some positions have been written to file,
   * since Debrief likes to know about tracks before loading sensor data
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
  private void outputThisDetection(WorldLocation loc, long dtg, String hostName,
                                   Category hostCategory,
                                   Float bearing, WorldDistance range, String sensor_name,
                                   String label)
  {
    // first see if we have output any positions yet -
    // since Debrief wants to know the position of any tracks before it writes to file
    //    if (!haveOutputPositions)
    //      return;

    String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);
    String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(dtg);

    String force = hostCategory.getForce();
    String col;

    col = "@" + colorFor(force);


    String brgTxt = null;
    if (bearing == null)
    {
      brgTxt = "00.000";
    }
    else
    {
      brgTxt = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(bearing.floatValue());
    }

    String rangeTxt = null;
    if (range == null)
    {
      rangeTxt = "0000";
    }
    else
    {
      rangeTxt = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(range.getValueIn(WorldDistance.YARDS));
    }

    String msg = ";SENSOR: " + dateStr + " " + wrapName(hostName) + " " + col + " " + locStr + " " +
      brgTxt + " " + rangeTxt +
      " " + sensor_name + " " + label + System.getProperty("line.separator");

    try
    {
      _os.write(msg);
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }

  }

  //////////////////////////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////////////////////////

  static public class DebriefReplayInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
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
