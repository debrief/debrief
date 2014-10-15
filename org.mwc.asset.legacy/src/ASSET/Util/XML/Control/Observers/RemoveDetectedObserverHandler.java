/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
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
import ASSET.Scenario.Observers.RemoveDetectedObserver;

abstract class RemoveDetectedObserverHandler extends DetectionObserverHandler
{
  /**
   * ************************************************************
   * member variables
   * *************************************************************
   */
  private final static String _myNewType = "RemoveDetectedObserver";
  private final static String DEAD_TYPE = "PlotTheDead";
  private Boolean _plotTheDead = null;
  

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public RemoveDetectedObserverHandler()
  {
    super(_myNewType);
    
    addAttributeHandler(new HandleBooleanAttribute(DEAD_TYPE)
    {
      public void setValue(String name, final boolean val)
      {
        _plotTheDead = new Boolean(val);
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
  	RemoveDetectedObserver res =  new RemoveDetectedObserver(watch, target, name, detectionLevel,isActive);
  	if(_plotTheDead != null)
  		res.setPlotTheDead(_plotTheDead.booleanValue());
  	
  	_plotTheDead = null;
  	
  	return res;
  }

}
