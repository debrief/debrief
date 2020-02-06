/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.cmap.core.custom_widget;

import java.util.EventObject;

public class LocationModifiedEvent extends EventObject{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Object _newLatValue;
  private Object _newLongValue;

  public LocationModifiedEvent(Object source,Object newLatValue,Object newLongValue)
  {
    super(source);
    _newLatValue = newLatValue;
    _newLongValue = newLongValue;
    
  }

  public Object getNewLatValue()
  {
    return _newLatValue;
  }
  public Object getNewLongValue()
  {
    return _newLongValue;
  }
  
  
}