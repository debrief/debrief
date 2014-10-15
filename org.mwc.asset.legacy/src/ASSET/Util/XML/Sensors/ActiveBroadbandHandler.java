/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Sensors;

import ASSET.Models.SensorType;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */


public abstract class ActiveBroadbandHandler extends BroadbandHandler
{

  protected final static String type = "ActiveBroadbandSensor";
  protected final static String SOURCE_LEVEL = "SourceLevel";

  protected double _mySourceLevel;

  public ActiveBroadbandHandler()
  {
    this(type);
  }

  public ActiveBroadbandHandler(String thisType)
  {
    super(thisType);

    super.addAttributeHandler(new HandleDoubleAttribute(SOURCE_LEVEL)
    {
      public void setValue(String name, final double val)
      {
        _mySourceLevel = val;
      }
    });


  }

  public void elementClosed()
  {
    // let the parent do it's stuff
    super.elementClosed();

    // and clear our values
    _working = true;
  }

  /**
   * method for child class to instantiate sensor
   *
   * @param myId
   * @param myName
   * @return the new sensor
   */
  protected SensorType getSensor(int myId)
  {
    // get this instance
    final ASSET.Models.Sensor.Initial.ActiveBroadbandSensor bb = new ASSET.Models.Sensor.Initial.ActiveBroadbandSensor(myId);

    bb.setDetectionAperture(_myAperture);
    bb.setSourceLevel(_mySourceLevel);
    bb.setWorking(_working);
    return bb;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ASSET.Models.Sensor.Initial.ActiveBroadbandSensor bb = (ASSET.Models.Sensor.Initial.ActiveBroadbandSensor) toExport;

    CoreSensorHandler.exportCoreSensorBits(thisPart, bb);
    thisPart.setAttribute(APERTURE, writeThis(bb.getDetectionAperture()));
    thisPart.setAttribute(SOURCE_LEVEL, writeThis(bb.getSourceLevel()));

    parent.appendChild(thisPart);

  }
}