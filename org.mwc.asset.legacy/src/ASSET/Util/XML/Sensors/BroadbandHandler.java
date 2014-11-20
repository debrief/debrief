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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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
  protected final static String AMBIGUOUS = "Ambiguous";
  protected final static String PRODUCE_RANGE = "CanProduceRange";

  protected double _myAperture;
	protected boolean _isAmbiguous = false;
	private boolean _canProduceRange;

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
    
    super.addAttributeHandler(new HandleBooleanAttribute(AMBIGUOUS)
    {
      public void setValue(String name, final boolean val)
      {
        _isAmbiguous  = val;
      }
    });

    super.addAttributeHandler(new HandleBooleanAttribute(PRODUCE_RANGE)
    {
      public void setValue(String name, final boolean val)
      {
        _canProduceRange  = val;
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
  protected SensorType getSensor(int myId)
  {
    // get this instance
    final ASSET.Models.Sensor.Initial.BroadbandSensor bb = new ASSET.Models.Sensor.Initial.BroadbandSensor(myId);

    bb.setDetectionAperture(_myAperture);
    bb.setAmbiguous(_isAmbiguous);
    bb.setCanProduceRange(_canProduceRange);
    
    
    _myAperture = 0;
    _isAmbiguous = false;
    _canProduceRange = true;

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
    thisPart.setAttribute(AMBIGUOUS, writeThis(bb.isAmbiguous()));
    thisPart.setAttribute(PRODUCE_RANGE, writeThis(bb.canProduceRange()));

    parent.appendChild(thisPart);
  }

}