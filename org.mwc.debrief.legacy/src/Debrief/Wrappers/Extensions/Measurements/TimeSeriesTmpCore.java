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
package Debrief.Wrappers.Extensions.Measurements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
abstract public class TimeSeriesTmpCore extends TimeSeriesCore
{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected final List<Long> _indices = new ArrayList<Long>();


  public TimeSeriesTmpCore(String name, String units)
  {
    super(units);
  }
  
  

  @Override
  public void setName(String name)
  {
    throw new IllegalArgumentException("Not implemented");
  }



  @Override
  public String getName()
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public int size()
  {
    return _indices.size();
  }

  @Override
  public Iterator<Long> getIndices()
  {
    return _indices.iterator();
  }
  
  /** get the index on (or after) the specified time
   * 
   * @param time
   * @return
   */
  @Override
  public int getIndexNearestTo(long time)
  {
    int ctr = 0;
    for(Long val: _indices)
    {
      if(val >= time)
      {
        return ctr;
      }
      ctr++;
    }
    
    // ok, didn't work. return negative index
    return INVALID_INDEX;
  }

}
