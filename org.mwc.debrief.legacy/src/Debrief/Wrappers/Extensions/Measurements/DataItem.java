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

/** in our temporary data structures we allow a folder to contain
 * both dataset and other folders. This API is common
 * to both
 * 
 * @author ian
 *
 */
public interface DataItem
{
  /** the name of this item
   * 
   * @return
   */
  public String getName();
  
  /** for diagnostics - list the contents
   * of this item
   */
  public void printAll();

  /** keep track of the parent for this item
   * 
   * @param dataFolder
   */
  public void setParent(DataFolder dataFolder);
  
  /** find the parent of this item, if possible
   * 
   * @return
   */
  public DataFolder getParent();

  /** change the name of this item
   * 
   * @param name
   */
  void setName(final String name);
  
}
