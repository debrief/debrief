package ASSET.Scenario.Observers.Recording;

import ASSET.Models.Sensor.DeployableSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.SensorType;
import ASSET.Models.Vessels.SSN;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.ScenarioType;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import java.util.Iterator;
import java.util.Vector;

/**
 * Look out for deployable sensors, and track their location to a Replay file.
 *
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 * $Log: DebriefDeployableSensorLocationObserver.java,v $
 * Revision 1.1  2006/08/08 14:22:08  Ian.Mayo
 * Second import
 *
 * Revision 1.1  2006/08/07 12:26:16  Ian.Mayo
 * First versions
 *
 * Revision 1.1  2004/08/12 11:06:51  Ian.Mayo
 * Refactor observers into tidier packages
 *
 * Revision 1.10  2004/08/12 09:21:55  Ian.Mayo
 * Refactor all observers, to give CoreObserver greater responsibility, and to tidy up writing to file
 *
 * Revision 1.9  2004/08/10 08:50:10  Ian.Mayo
 * Change functionality of Debrief replay observer so that it can record decisions & detections aswell.  Also include ability to track particular type of target
 *
 * Revision 1.8  2004/08/09 15:50:49  Ian.Mayo
 * Refactor category types into Force, Environment, Type sub-classes
 *
 * Revision 1.7  2004/05/24 16:07:08  Ian.Mayo
 * Commit updates from home
 *
 * Revision 1.2  2004/04/08 20:27:54  ian
 * Restructured contructor for CoreObserver
 *
 * Revision 1.1.1.1  2004/03/04 20:30:56  ian
 * no message
 * <p/>
 * Revision 1.6  2004/02/16 13:46:51  Ian.Mayo
 * Minor mods
 * <p/>
 * Revision 1.5  2003/09/18 14:13:00  Ian.Mayo
 * Update with new World Speed class
 * <p/>
 * Revision 1.4  2003/09/12 13:15:29  Ian.Mayo
 * Pass scenario time to recorders
 * <p/>
 * Revision 1.3  2003/09/03 14:00:45  Ian.Mayo
 * And again
 * <p/>
 * Revision 1.2  2003/09/03 14:00:26  Ian.Mayo
 * correct macro keyword
 */
public class DebriefDeployableSensorLocationObserver extends DebriefReplayObserver
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  /**
   * the list of sensors we're watching
   */
  private Vector _mySensors = null;


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  /**
   * constructor
   *
   * @param directoryName
   * @param fileName
   * @param recordDetections
   */
  public DebriefDeployableSensorLocationObserver(final String directoryName,
                                                 final String fileName,
                                                 final boolean recordDetections,
                                                 final String name,
                                                 final boolean isActive)
  {
    super(directoryName, fileName, recordDetections, name, isActive);
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  /**
   * the scenario has stepped forward
   */
  public void step(long newTime)
  {
    if (!isActive())
      return;

    if (_mySensors != null)
    {

      // get the positions of the participants
      for (int i = 0; i < _mySensors.size(); i++)
      {
        DeployableSensor deployableSensor = (DeployableSensor) _mySensors.elementAt(i);
        SensorType cs = (SensorType) deployableSensor;

        // just check we have a host id
        int id = deployableSensor.getHostId();
        if (id != -1)
        {
          ParticipantType pt = _myScenario.getThisParticipant(id);
          WorldLocation loc = pt.getStatus().getLocation();
          loc = deployableSensor.getLocation(loc);

          // wrap the sensor location in a status
          Status stat = new Status(0, newTime);
          stat.setLocation(loc);
          stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));

          // and the core participant

          CoreParticipant cp = new SSN(cs.getId());
          cp.setName(cs.getName());
          cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.CROSS, Category.Type.SONAR_BUOY));

          // ok, now output these details in our special format
          writeThesePositionDetails(loc, stat, cp, newTime);
        }

      }
    }

  }


  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(ScenarioType scenario)
  {
    super.performSetupProcessing(scenario);    //To change body of overridden methods use File | Settings | File Templates.

    // ok, look for any sensors in the scenario

    // first step through the particiaapnts
    Integer[] parts = scenario.getListOfParticipants();
    for (int i = 0; i < parts.length; i++)
    {
      Integer part = parts[i];
      CoreParticipant thisP = (CoreParticipant) scenario.getThisParticipant(part.intValue());

      // now the sensors
      SensorList sl = thisP.getSensorFit();
      Iterator theSensors = sl.getSensors().iterator();
      while (theSensors.hasNext())
      {
        SensorType sensor = (SensorType) theSensors.next();
        if (sensor instanceof DeployableSensor)
        {
          addSensor(sensor);
        }
      }
    }
  }

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
    // and clear our list
    if (_mySensors != null)
    {
      _mySensors.removeAllElements();
    }

    super.performCloseProcessing(scenario);    //To change body of overridden methods use File | Settings | File Templates.
  }

  /**
   * ok, add this sensor and create the list if we have to
   *
   * @param sensor
   */
  private void addSensor(SensorType sensor)
  {
    if (_mySensors == null)
      _mySensors = new Vector(0, 1);

    _mySensors.add(sensor);
  }

}
