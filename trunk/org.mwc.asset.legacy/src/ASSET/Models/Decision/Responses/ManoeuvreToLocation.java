/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 25-Sep-2002
 * Time: 13:32:33
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Responses;

import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class ManoeuvreToLocation extends Response.CoreResponse
{
  ////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////

  /**
   * the location to head to
   */
  private WorldLocation _myLocation;

  /**
   * the speed to travel at (kts)
   */
  private WorldSpeed _mySpeed;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////

  /**
   * @param location the location to steer to (we use the depth value for the transit depth)
   * @param speed    the speed to travel at (m), or null to continue at current speed
   */
  public ManoeuvreToLocation(final WorldLocation location,
                             final WorldSpeed speed)
  {
    this._myLocation = location;
    _mySpeed = speed;
  }

  ////////////////////////////////////////////////////
  // response object
  ////////////////////////////////////////////////////


  /**
   * get the description of what we're doing
   */
  public String getActivity()
  {
    return "Move to location";
  }

  /**
   * produce the required response
   *
   * @param conditionResult the result from the condition test
   * @param status          the current status
   * @param detections      the current set of detections
   * @param monitor         the object monitoring us(for add/remove participants, detonations, etc)
   * @param time            the current time
   * @return
   * @see ASSET.Models.Decision.Conditions.Condition
   */

  public DemandedStatus direct(Object conditionResult,
                               final Status status,
                               DemandedStatus demStat, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               final long time)
  {
    // produce a vector to the demanded location
    final WorldVector newDir = _myLocation.subtract(status.getLocation());

    // steer a course to the demanded location
    final double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(newDir.getBearing());

    final SimpleDemandedStatus ds = new SimpleDemandedStatus(time, status);

    // set the course
    ds.setCourse(brgDegs);

    // set the depth
    ds.setHeight(-_myLocation.getDepth());

    // check the other data
    if (_mySpeed != null)
    {
      ds.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));
    }

    return ds;
  }

  public void restart()
  {
    // don't bother, we don't react to this
  }

  public WorldLocation getLocation()
  {
    return _myLocation;
  }

  public void setLocation(final WorldLocation location)
  {
    this._myLocation = location;
  }

  public WorldSpeed getSpeed()
  {
    return _mySpeed;
  }

  public void setSpeed(final WorldSpeed spd_kts)
  {
    this._mySpeed = spd_kts;
  }

  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new GotoLocationInfo(this);

    return _myEditor;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class GotoLocationInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public GotoLocationInfo(final ManoeuvreToLocation data)
    {
      super(data, data.getName(), "Location Response");
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
        java.beans.PropertyDescriptor[] res = {
          prop("Name", "the name of this response"),
          prop("Location", "the location to head for"),
          prop("Speed", "the speed to travel at (kts)"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  ////////////////////////////////////////////////////
  // testing support
  ////////////////////////////////////////////////////
  public static class ManToLocationTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ManToLocationTest(final String name)
    {
      super(name);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      final WorldLocation newLoc = new WorldLocation(0.4, 0.4, 40);
      final WorldLocation origin = new WorldLocation(0, 0, 40);
      final Status currentStat = new Status(12, 200);
      currentStat.setLocation(origin);
      currentStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

      final ManoeuvreToLocation ml = new ManoeuvreToLocation(newLoc, null);
      return ml;
    }

    public void testIt()
    {
      final WorldLocation newLoc = new WorldLocation(0.4, 0.4, 40);
      final WorldLocation origin = new WorldLocation(0, 0, 40);
      final Status currentStat = new Status(12, 200);
      currentStat.setLocation(origin);
      currentStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

      final ManoeuvreToLocation ml = new ManoeuvreToLocation(newLoc, null);
      DemandedStatus ds = ml.direct(null, currentStat, null, null, null, -1);
      // check we are at the same speed
      assertEquals("maintain speed", 12, ((SimpleDemandedStatus) ds).getSpeed(), 0.001);
      assertEquals("on correct course", 45, ((SimpleDemandedStatus) ds).getCourse(), 1);

      // now provide a speed value
      ml.setSpeed(new WorldSpeed(20, WorldSpeed.M_sec));
      ds = ml.direct(null, currentStat, null, null, null, -1);
      // check we are at the same speed
      assertEquals("maintain speed", 20, ((SimpleDemandedStatus) ds).getSpeed(), 0.001);
      assertEquals("on correct course", 45, ((SimpleDemandedStatus) ds).getCourse(), 1);


    }

  }
}
