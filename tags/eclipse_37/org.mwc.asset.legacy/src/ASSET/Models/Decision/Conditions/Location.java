/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 25-Sep-2002
 * Time: 09:47:27
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Conditions;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class Location extends Condition.CoreCondition
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
   * the location we're relative to
   */
  private WorldLocation _theLocation = null;

  /**
   * the allowable range error from the location
   */
  private WorldDistance _theDistance = null;

  /**
   * whether are success if less than the distance (not MORE than)
   */
  private boolean _succeedIfCloser = true;

  /**
   * the default distance to use
   */
  private WorldDistance _defaultDistance = new WorldDistance(5, WorldDistance.NM);

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////

  /**
   * @param theLocation the location we are trying to reach
   * @param theDistance the allowable distance from the location
   */
  public Location(final WorldLocation theLocation, final WorldDistance theDistance)
  {
    super("Location");

    this._theLocation = theLocation;
    // just check if a distance was supplied
    if (theDistance != null)
      this._theDistance = theDistance;
    else
      this._theDistance = _defaultDistance;
  }


  ////////////////////////////////////////////////////
  // condition fields
  ////////////////////////////////////////////////////

  public Object test(final Status status,
                     DetectionList detections,
                     long time, ScenarioActivityMonitor monitor)
  {

    Boolean res = null;

    // how far are we from our destination?
    final WorldVector dist = status.getLocation().subtract(_theLocation);

    final double distDegs = dist.getRange();
    // is this less than our distance
    final double myDistDegs = _theDistance.getValueIn(WorldDistance.DEGS);
    if (this._succeedIfCloser)
    {
      if (distDegs <= myDistDegs)
      {
        res = Boolean.TRUE;
      }
    }
    else
    {
      if (distDegs >= myDistDegs)
      {
        res = Boolean.TRUE;
      }
    }


    return res;
  }

  public void restart()
  {
    // ignore, we don't bother with it
  }

  public WorldLocation getLocation()
  {
    return _theLocation;
  }

  public void setLocation(final WorldLocation theLocation)
  {
    this._theLocation = theLocation;
  }

  public WorldDistance getDistance()
  {
    return _theDistance;
  }

  public void setDistance(final WorldDistance theDistance)
  {
    this._theDistance = theDistance;
  }

  public boolean isSucceedIfCloser()
  {
    return _succeedIfCloser;
  }

  public void setSucceedIfCloser(final boolean succeedIfCloser)
  {
    this._succeedIfCloser = succeedIfCloser;
  }
  ////////////////////////////////////////////////////
  // editor fields
  ////////////////////////////////////////////////////

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
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
      _myEditor = new LocationInfo(this);

    return _myEditor;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class LocationInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public LocationInfo(final Location data)
    {
      super(data, data.getName(), "Location Condition");
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
          prop("Name", "the name of this condition"),
          prop("Location", "the location we are heading for"),
          prop("Distance", "the allowable distance from this location"),
          prop("SucceedIfCloser", "whether we succeed when nearer than range (not further than range)"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  /**
   * *************************************************
   * testing
   * *************************************************
   */
  static public class LocationTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public LocationTest(final String name)
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
      final Location loc = new Location(null, new WorldDistance(1, WorldDistance.DEGS));
      return loc;
    }

    public void testIt()
    {
      // build it
      final WorldLocation wl = new WorldLocation(1, 1, 1);
      Status newStat = new Status(12, 200);
      newStat.setLocation(wl.add(new WorldVector(0, 2, 0)));
      final Location loc = new Location(wl, new WorldDistance(1, WorldDistance.DEGS));
      Object res = loc.test(newStat, null, 0, null);
      // test it
      assertNull("we are not in range", res);

      newStat = new Status(12, 200);
      newStat.setLocation(wl.add(new WorldVector(0, 1, 0)));
      res = loc.test(newStat, null, 0, null);
      assertNotNull("we are at range", res);

      // try inside range
      newStat = new Status(12, 200);
      newStat.setLocation(wl.add(new WorldVector(0, 0.5, 0)));
      res = loc.test(newStat, null, 0, null);
      assertNotNull("we are in range", res);

      // switch around when we succeed
      loc.setSucceedIfCloser(false);

      newStat.setLocation(wl.add(new WorldVector(0, 0.5, 0)));
      res = loc.test(newStat, null, 0, null);
      // test it
      assertNull("we are not in range", res);

      newStat = new Status(12, 200);
      newStat.setLocation(wl.add(new WorldVector(0, 1, 0)));
      res = loc.test(newStat, null, 0, null);
      assertNotNull("we are at range", res);

      // try inside range
      newStat = new Status(12, 200);
      newStat.setLocation(wl.add(new WorldVector(0, 2, 0)));
      res = loc.test(newStat, null, 0, null);
      assertNotNull("we are in range", res);

    }

  }
}
