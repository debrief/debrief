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