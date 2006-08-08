/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 01-Nov-2002
 * Time: 12:18:41
 */
package ASSET.Models.Decision.Responses;

import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;


/**
 * response which is capable of switching sensors on and off, as decided
 * by the medium they receive/transmit
 *
 * @see ASSET.Models.Environment.EnvironmentType
 * @see ASSET.Util.XML.Decisions.Responses.ChangeSensorLineUpHandler
 */
public class ChangeSensorLineUp extends Response.CoreResponse
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
   * the medium of sensors we change
   *
   * @see ASSET.Models.Environment.EnvironmentType
   */
  private int _medium;

  /**
   * whether to switch on or off
   */
  private boolean _switchOn = true;

  ////////////////////////////////////////////////////
  // member constructor
  ////////////////////////////////////////////////////


  public ChangeSensorLineUp(int medium, boolean switchOn)
  {
    this._medium = medium;
    this._switchOn = switchOn;
  }

  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////


  /**
   * get the description of what we're doing
   */
  public String getActivity()
  {
    String res;
    if (_switchOn)
      res = "on";
    else
      res = "off";

    return "Switch " + res;
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
   * @see Condition
   */
  public DemandedStatus direct(Object conditionResult,
                               Status status,
                               DemandedStatus demStat, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               long time)
  {
    // ok, request the sensor change
    DemandedSensorStatus ds = new DemandedSensorStatus(_medium, _switchOn);

    // and produce the demanded status
    DemandedStatus dem = new SimpleDemandedStatus(time, status);
    dem.add(ds);

    // done
    return dem;
  }

  ////////////////////////////////////////////////////
  // accessors
  ////////////////////////////////////////////////////

  /**
   * the medium of the sensor we are changing
   *
   * @return the medium
   * @see ASSET.Models.Environment.EnvironmentType
   */
  public int getMedium()
  {
    return _medium;
  }

  /**
   * change the medium of the sensor we are changing
   *
   * @param medium the medium
   * @see ASSET.Models.Environment.EnvironmentType
   */
  public void setMedium(int medium)
  {
    this._medium = medium;
  }

  public boolean getSwitchOn()
  {
    return _switchOn;
  }

  public void setSwitchOn(boolean switchOn)
  {
    this._switchOn = switchOn;
  }

  /**
   * reset the local data
   */
  public void restart()
  {
  }

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
      _myEditor = new ChangeSensorInfo(this);

    return _myEditor;
  }

  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class ChangeSensorInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public ChangeSensorInfo(final ChangeSensorLineUp data)
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
        java.beans.PropertyDescriptor[] res = {
          prop("Name", "the name of this response"),
          prop("Medium", "the medium of the sensor to change"),
          prop("SwitchOn", "whether to switch on or off"),
        };
        res[1].setPropertyEditorClass(EnvironmentType.MediumPropertyEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // testing support
  //////////////////////////////////////////////////
  public static class SensorLineupTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new ChangeSensorLineUp(2, true);
    }
  }

}
