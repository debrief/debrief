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
import MWC.GenericData.Duration;
import MWC.GenericData.TimePeriod;

import java.util.Date;

/**
 * condition which is successful for a particular duration after first being called.
 * A zero length duration will cause the event to happen just once
 */

public class ElapsedTime extends Condition.CoreCondition
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
   * the duration we're checking for
   */
  private Duration _theDuration = null;

  /**
   * the time we are due to elapse (start time plus duration)
   */
  private long _endTime = TimePeriod.INVALID_TIME;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////


  /**
   * @param theTime the time we are checking for
   */
  public ElapsedTime(final Duration theTime)
  {
    super("ElapsedTime");

    _theDuration = theTime;
  }


  ////////////////////////////////////////////////////
  // condition fields
  ////////////////////////////////////////////////////

  public Object test(Status status,
                     DetectionList detections,
                     final long time, ScenarioActivityMonitor monitor)
  {

    Date res = null;

    // are we already active?
    if (_endTime != TimePeriod.INVALID_TIME)
    {
      // have we expired yet?
      if (time >= _endTime)
      {
        // yup, just quit out
        res = null;
      }
      else
      {
        // nope, still active, so produce some outptu
        res = new Date(time);
      }
    }
    else
    {
      // nope, let's get started
      _endTime = time + (long) _theDuration.getValueIn(Duration.MILLISECONDS);

      // and produce an output
      res = new Date(time);
    }

    return res;
  }

  /**
   * restart the condition
   */
  public void restart()
  {
    // ignore, we don't need to restart
    _endTime = TimePeriod.INVALID_TIME;
  }

  public Duration getDuration()
  {
    return _theDuration;
  }

  public void setDuration(final Duration theTime)
  {
    this._theDuration = theTime;
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
      _myEditor = new ElapsedTimeInfo(this);

    return _myEditor;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class ElapsedTimeInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public ElapsedTimeInfo(final ElapsedTime data)
    {
      super(data, data.getName(), "");
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
          prop("Duration", "the period we remain active for for"),
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
  static public class TimeElapsedTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public TimeElapsedTest(final String name)
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
      return new ElapsedTime(new Duration(500, Duration.MILLISECONDS));
    }

    public void testIt()
    {
      // build it
      final ElapsedTime loc = new ElapsedTime(new Duration(500, Duration.MILLISECONDS));
      Object res = loc.test(null, null, 200, null);

      // test it
      assertNotNull("we are alive", res);
      assertEquals("correct expiry time set", loc._endTime, 700);

      res = loc.test(null, null, 300, null);
      assertNotNull("still going", res);

      res = loc.test(null, null, 100, null);
      assertNotNull("still going", res);

      res = loc.test(null, null, 700, null);
      assertNull("we have expired", res);

      // check the "single-shot" mode
      loc.setDuration(new Duration(0, Duration.MILLISECONDS));
      // do a reset
      loc.restart();
      res = loc.test(null, null, 300, null);
      assertNotNull("single true value", res);
      res = loc.test(null, null, 400, null);
      assertNull("now returns false", res);
    }

  }

}
