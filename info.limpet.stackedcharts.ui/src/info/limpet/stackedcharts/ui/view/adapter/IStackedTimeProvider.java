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
package info.limpet.stackedcharts.ui.view.adapter;

public interface IStackedTimeProvider
{
  /** configure to send time updates to this listener
   * 
   * @param listener
   */
  void controlThis(IStackedTimeListener listener);
  
  /** stop sending time updates to this listener
   * 
   * @param listener
   */
  void releaseThis(IStackedTimeListener listener);

  /** identify if this object is able to provide time control
   * (we only wish to allow one)
   * @return yes/no
   */
  boolean canProvideControl();  
}
