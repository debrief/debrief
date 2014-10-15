/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Control.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.DetectionObserver;
import ASSET.Scenario.Observers.Plotting.PlotSensorObserver;

abstract class PlotSensorObserverHandler extends DetectionObserverHandler
{
  /**
   * ************************************************************
   * member variables
   * *************************************************************
   */
  private final static String _myNewType = "PlotSensorObserver";
  private final static String SHOW_NAMES_TYPE = "ShowNames";
  private final static String SHADE_CIRCLE_TYPE = "ShadeCircle";
	protected boolean _showNames = false;
	protected boolean _shadeCircle  = false;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public PlotSensorObserverHandler()
  {
    super(_myNewType);
    

    addAttributeHandler(new HandleBooleanAttribute(SHOW_NAMES_TYPE)
    {
      public void setValue(String name, final boolean val)
      {
        _showNames  = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHADE_CIRCLE_TYPE)
    {
      public void setValue(String name, final boolean val)
      {
        _shadeCircle    = val;
      }
    });

    
  }

  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */
  static public String getType()
  {
    return _myNewType;
  }

  protected DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name, final Integer detectionLevel,boolean isActive)
  {
    return new PlotSensorObserver(watch, target, name, detectionLevel,isActive, _showNames, _shadeCircle);
  }

}
