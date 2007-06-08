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

import java.util.Vector;

/**
 * condition which is successful when the scenario is equal or after the indicated time
 */

public class OrCondition extends Condition.CoreCondition
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
   * the list of conditions we are after
   */
  private Vector _myConditions;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////


  /**
   *
   */
  private OrCondition()
  {
    super("OR condition");
  }

  private void addCondition(final Condition newCondition)
  {
    if (_myConditions == null)
      _myConditions = new Vector(0, 1);

    _myConditions.add(newCondition);
  }

  ////////////////////////////////////////////////////
  // condition fields
  ////////////////////////////////////////////////////

  public Object test(final Status status,
                     final DetectionList detections,
                     final long time, ScenarioActivityMonitor monitor)
  {

    Object res = null;

    // pass through the conditions
    for (int i = 0; i < _myConditions.size(); i++)
    {
      final Condition condition = (Condition) _myConditions.elementAt(i);
      res = condition.test(status, detections, time, null);
      if (res != null)
        break;
    }

    return res;
  }

  /**
   * restart the condition
   */
  public void restart()
  {
    // ignore, we don't bother it
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
      _myEditor = new OrConditionInfo(this);

    return _myEditor;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class OrConditionInfo extends Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public OrConditionInfo(final OrCondition data)
    {
      super(data, data.getName(), "OR condition");
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
  public static class OrTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public OrTest(final String name)
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
      return new OrCondition();
    }

    public void testIt()
    {
      // build it
      final OrCondition loc = new OrCondition();

      // create our real conditions

      // time elapsed first
      final TimePoint tpa = new TimePoint(2000);
      final TimePoint tpb = new TimePoint(3000);

      // now for the location
      final Location theLoc = new Location(new WorldLocation(1, 1, 1), new WorldDistance(4, WorldDistance.DEGS));
      theLoc.setSucceedIfCloser(false);

      // build up the condition
      final OrCondition or = new OrCondition();
      or.addCondition(tpb);
      or.addCondition(theLoc);

      // and now the test
      final Status newStat = new Status(12, 300);
      newStat.setLocation(new WorldLocation(2, 2, 2));

      Object res = or.test(newStat, null, 300, null);
      // check it failed
      assertNull("no tests passed", res);

      // see if we pass the first time test
      newStat.setTime(3200);
      res = or.test(newStat, null, 3200, null);
      // check it failed
      assertNotNull("passed time test", res);

      // check the correct time was identified
      java.util.Date val = (java.util.Date) res;
      assertEquals("correct time returned", val.getTime(), 3000);

      or.addCondition(tpa);
      newStat.setTime(3200);
      res = or.test(newStat, null, 3200, null);
      // check it failed
      assertNotNull("passed time test", res);

      // check that we didn't continue through the tests
      val = (java.util.Date) res;
      assertEquals("correct time returned", val.getTime(), 3000);

    }

  }

  //////////////////////////////////////////////////
  // property testing
  //////////////////////////////////////////////////
  public static class Or2Test extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new OrCondition();
    }
  }


}
