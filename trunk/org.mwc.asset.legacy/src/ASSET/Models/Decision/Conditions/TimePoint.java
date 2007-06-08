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

import java.util.Date;

/**
 * condition which is successful when the scenario is equal or after the indicated time
 */

public class TimePoint extends Condition.CoreCondition
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
   * the time we're checking for
   */
  private long _theTime = -1;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////


  /**
   * @param theTime the time we are checking for
   */
  public TimePoint(final long theTime)
  {
    super("TimePoint");

    _theTime = theTime;
  }


  ////////////////////////////////////////////////////
  // condition fields
  ////////////////////////////////////////////////////

  public Object test(Status status,
                     DetectionList detections,
                     final long time, ScenarioActivityMonitor monitor)
  {

    Date res = null;

    if (time >= _theTime)
    {
      res = new Date(_theTime);
    }

    return res;
  }

  /**
   * restart the condition
   */
  public void restart()
  {
    // ignore, we don't need to restart
  }

  public long getTime()
  {
    return _theTime;
  }

  public void setTime(final long theTime)
  {
    this._theTime = theTime;
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
      _myEditor = new TimePointInfo(this);

    return _myEditor;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class TimePointInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public TimePointInfo(final TimePoint data)
    {
      super(data, data.getName(), "Time Point");
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
          prop("Time", "the time we are checking for"),
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
  static public class TimePointTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public TimePointTest(final String name)
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
      return new TimePoint(300);
    }

    public void testIt()
    {
      // build it
      final TimePoint loc = new TimePoint(300);
      Object res = loc.test(null, null, 0, null);
      // test it
      assertNull("we are before time", res);

      res = loc.test(null, null, 300, null);
      assertNotNull("we are at time", res);

      res = loc.test(null, null, 400, null);
      assertNotNull("we are after time", res);
    }

  }
}
