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
package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.TowedArrayLocationObserver;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

abstract class TowedArrayLocationObserverHandler extends CoreFileObserverHandler
{

  private final static String type = "TowedArrayLocationObserver";

  TargetType _subjectName;
  List<Double> offsets;
  String messageName;
  Double defaultDepth;
  String sensorName;
  
  public TowedArrayLocationObserverHandler()
  {
    super(type);

    // add the other handlers
    addHandler(new TargetTypeHandler("SubjectToTrack")
    {
      public void setTargetType(TargetType type1)
      {
        _subjectName = type1;
      }
    });
    addAttributeHandler(new HandleAttribute("OFFSETS")
    {
      public void setValue(String name, final String val)
      {
        offsets = new ArrayList<Double>();
        
        String[] items = val.split(",");
        for (int i = 0; i < items.length; i++)
        {
          String string = items[i];
          offsets.add(Double.parseDouble(string));
        }
      }
    });
    addAttributeHandler(new HandleAttribute("MESSAGE_NAME")
    {
      public void setValue(String name, final String val)
      {
        messageName = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute("DEFAULT_DEPTH")
    {
      @Override
      public void setValue(String name, double value)
      {
        defaultDepth = value;
      }
    });
    addAttributeHandler(new HandleAttribute("SENSOR_NAME")
    {
      public void setValue(String name, final String val)
      {
        sensorName = val;
      }
    });
  }

  public void elementClosed()
  {
    defaultDepth = 12d;
    // create ourselves
    final CoreObserver timeO = new TowedArrayLocationObserver(_directory, _fileName, _subjectName, _name, _isActive, offsets, messageName,
        defaultDepth, sensorName);
    
    setObserver(timeO);

    // and reset
    super.elementClosed();
    
    // clear the params
    _subjectName = null;
    messageName = null;
    defaultDepth = null;
    sensorName = null;
  }


  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    throw new UnsupportedOperationException("Method not implemented");
  }


}