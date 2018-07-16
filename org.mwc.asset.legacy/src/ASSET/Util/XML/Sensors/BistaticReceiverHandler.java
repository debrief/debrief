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
import ASSET.Models.Sensor.Initial.BistaticReceiver;

public abstract class BistaticReceiverHandler extends CoreSensorHandler
{

  private final static String type = "BistaticReceiver";
  
  private final static String SUPPRESS = "Suppress";
  private final static String SUPPRESS_ANGLE = "SuppressAngle";

  private Boolean _suppress = null;
  private Double _suppressAngle = null;

  public BistaticReceiverHandler(String myType)
  {
    super(myType);

    addAttributeHandler(new HandleBooleanAttribute(SUPPRESS)
    {
      @Override
      public void setValue(String name, boolean value)
      {
        _suppress = value;
      }
    });
    
    addAttributeHandler(new HandleDoubleAttribute(SUPPRESS_ANGLE)
    {
      @Override
      public void setValue(String name, double value)
      {
        _suppressAngle = value;
      }
    });
    
  }

  public BistaticReceiverHandler()
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
    final BistaticReceiver bb = new BistaticReceiver(myId);
    
    if(_suppress != null)
    {
      bb.setSuppressDirect(_suppress);
    }
    
    if(_suppressAngle != null) 
    {
      bb.setObscureAngle(_suppressAngle);
    }
    
    _suppress = null;
    _suppressAngle = null;
    
    return bb;
  }


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final BistaticReceiver bb = (BistaticReceiver) toExport;

    // insert the parent bits first
    CoreSensorHandler.exportCoreSensorBits(thisPart, bb);
    
    thisPart.setAttribute(SUPPRESS, writeThis(bb.isSuppressDirect()));
    thisPart.setAttribute(SUPPRESS_ANGLE, writeThis(bb.getObscureAngle()));
    
    parent.appendChild(thisPart);
  }

}