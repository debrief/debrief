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
package Debrief.Wrappers.Extensions;

import java.io.Serializable;
import java.util.ArrayList;

/** store list of data items of unpredictable type (since they're provided by extension)
 * 
 * @author ian
 *
 */
public class AdditionalData extends ArrayList<Object> implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  /** get a child item of the provided type
   * 
   * @param clazz
   * @return
   */
  public Object getThisType(Class<?> clazz)
  {
    Object res = null;
    
    // loop through our data
    for(Object item: this)
    {
      if(item.getClass().equals(clazz))
      {
        res = item;
      }
    }
    
    return res;
  }

}
