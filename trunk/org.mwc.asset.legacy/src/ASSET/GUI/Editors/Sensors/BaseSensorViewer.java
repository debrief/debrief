/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 30-Oct-2002
 * Time: 11:36:57
 */
package ASSET.GUI.Editors.Sensors;

import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.SensorType;

import java.util.Vector;

public abstract class BaseSensorViewer extends MWC.GUI.Properties.Swing.SwingCustomEditor implements java.beans.PropertyChangeListener, MWC.GUI.Properties.NoEditorButtons
{
  /**
   * the list of sensor components we build up, consuming them at each refresh
   */
  protected Vector _sensorEvents = new Vector(0, 1);
  /**
   * the sensor we're listening to
   */
  protected SensorType _mySensor;


  public boolean supportsCustomEditor()
  {
    return true;
  }


  public void setValue(final Object value)
  {
    //
    if (value instanceof SensorFitEditor.WrappedSensor)
    {
      //
      SensorFitEditor.WrappedSensor wrapper = (SensorFitEditor.WrappedSensor) value;

      // store the sensor.
      _mySensor = wrapper.MySensor;

      // ok, start listening to the sensor
      listenTo(_mySensor);

      // create the form
      initForm();

      // and update it.
      updateForm();

    }
  }

  /** start listening to this sensor
   *
   * @param newSensor the sensor to listen to
   */
  abstract protected void listenTo(SensorType newSensor);

  public void setObject(final Object value)
  {
    setValue(value);
  }


  public void doClose()
  {
    // stop listening to the sensor
    _mySensor.removeSensorCalculationListener(this);

    super.doClose();
  }


  protected abstract void initForm();

  protected abstract void updateForm();

  /**
   * receive update signals
   *
   * @param pe the changed data
   */
  public void propertyChange(final java.beans.PropertyChangeEvent pe)
  {
    // get the name
    final String type = pe.getPropertyName();

    // is it a new detection?
    if (type == ASSET.Models.Sensor.Initial.InitialSensor.SENSOR_COMPONENT_EVENT)
    {
      // store these components
      _sensorEvents.add(pe.getNewValue());

    }
    // does it mark the end of this step?
    else if (type == InitialSensor.DETECTION_CYCLE_COMPLETE)
    {
      // update the GUI
      updateForm();

      // clear our local list
      _sensorEvents.clear();
    }
  }
  ////////////////////////////////////////////////////
  // member objects
  //////////////////////////////////////////////////// 

  ////////////////////////////////////////////////////
  // member constructor
  ////////////////////////////////////////////////////
  
  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////  
}
