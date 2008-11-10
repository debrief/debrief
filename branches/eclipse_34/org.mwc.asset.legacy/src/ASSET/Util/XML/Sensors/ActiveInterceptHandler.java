package ASSET.Util.XML.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.SensorType;

public abstract class ActiveInterceptHandler extends CoreSensorHandler
{

  private final static String type = "ActiveInterceptSensor";

  public ActiveInterceptHandler(String myType)
  {
    super(myType);
  }

  public ActiveInterceptHandler()
  {
    this(type);
  }

  /**
   * method for child class to instantiate sensor
   *
   * @param myId
   * @param myName
   * @return the new sensor
   */
  protected SensorType getSensor(int myId, String myName)
  {
    final ASSET.Models.Sensor.Initial.ActiveInterceptSensor bb = new ASSET.Models.Sensor.Initial.ActiveInterceptSensor(myId);
    bb.setName(myName);
    return bb;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Sensor.Initial.ActiveInterceptSensor bb = (ASSET.Models.Sensor.Initial.ActiveInterceptSensor) toExport;

    parent.appendChild(thisPart);

  }
}