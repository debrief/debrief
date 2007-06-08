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

public abstract class BroadbandHandler extends CoreSensorHandler
{

  private final static String type = "BroadbandSensor";
  protected final static String APERTURE = "Aperture";

  protected double _myAperture;

  public BroadbandHandler(String myType)
  {
    super(myType);

    super.addAttributeHandler(new HandleDoubleAttribute(APERTURE)
    {
      public void setValue(String name, final double val)
      {
        _myAperture = val;
      }
    });
  }

  public BroadbandHandler()
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
    // get this instance
    final ASSET.Models.Sensor.Initial.BroadbandSensor bb = new ASSET.Models.Sensor.Initial.BroadbandSensor(myId);
    bb.setName(myName);

    bb.setDetectionAperture(_myAperture);

    return bb;
  }


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Sensor.Initial.BroadbandSensor bb = (ASSET.Models.Sensor.Initial.BroadbandSensor) toExport;

    // insert the parent bits first
    CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

    // and now our bits
    thisPart.setAttribute(APERTURE, writeThis(bb.getDetectionAperture()));

    parent.appendChild(thisPart);
  }

}