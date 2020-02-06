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


/** API for classes that are capable of storing supplementatal datasets
 * 
 * @author ian
 *
 */
public interface AdditionalProvider
{
  /** get the additional data
   * 
   * @return
   */
  AdditionalData getAdditionalData();

  /** marker for classes that MAY need the existing elements() to be
   * wrapped. SensorWrapper will need to be wrapped. TrackWrapper
   * already breaks elements() down into a number of children
   * 
   * @author ian
   *
   */
  public static interface ExistingChildrenMayNeedToBeWrapped
  {
    
    /** whether to wrap the elements() entry in an wrapper
     * object - so that this wrapped object and AdditionalData
     * both sit at the same level
     * @return
     */
    boolean childrenNeedWrapping();
    
    /** get the collective name for other objects held by this object.
     * This is used in the Outline view.  If there is measured data,
     * we don't show all the children then the measured data.  We demote
     * the children to a named item, then we show the measured data
     * 
     * @return
     */
    String getItemsName();
  }
}
