package ASSET.Util.XML.Sensors;

import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.SensorType;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 05-Feb-2004
 * Time: 11:29:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class CoreSensorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private static final String NAME = "Name";
  int _myId;
  String _myName;
  private final static String WORKING = "Working";
  protected boolean _working = true;
  private static final String ID_VAL = "id";

  public CoreSensorHandler(final String myType)
  {
    super(myType);

    super.addAttributeHandler(new HandleAttribute(ID_VAL)
    {
      public void setValue(String name, final String val)
      {
        _myId = Integer.parseInt(val);
      }
    });

    super.addAttributeHandler(new HandleAttribute(NAME)
    {
      public void setValue(String name, final String val)
      {
        _myName = val;
      }
    });
    super.addAttributeHandler(new HandleBooleanAttribute(WORKING)
    {
      public void setValue(String name, final boolean val)
      {
        _working = val;
      }
    });
  }

  public void elementClosed()
  {
    // do we have an id?
    if (_myId <= 0)
      _myId = ASSET.Util.IdNumber.generateInt();

    // get this instance
    final SensorType sensor = getSensor(_myId, _myName);

    // ok - now store it
    addSensor(sensor);
    sensor.setWorking(_working);

    // and clear the data
    _myName = null;
    _myId = -1;
    _working = true;
  }

  /**
   * callback to store the sensor in the parent
   *
   * @param sensor the new sensor
   */
  abstract public void addSensor(SensorType sensor);

  /**
   * method for child class to instantiate sensor
   *
   * @param myId
   * @param myName
   * @return the new sensor
   */
  abstract protected SensorType getSensor(int myId, String myName);


  /** export the sensor bits handled by this core class
   * 
   * @param sensorElement
   * @param toExport
   */
  static public void exportCoreSensorBits(final org.w3c.dom.Element sensorElement, final Object toExport)
  {
    CoreSensor cs = (CoreSensor) toExport;
    sensorElement.setAttribute(WORKING, writeThis(cs.isWorking()));
    sensorElement.setAttribute(NAME, cs.getName());
    sensorElement.setAttribute(ID_VAL, writeThis(cs.getId()));
  }

}
