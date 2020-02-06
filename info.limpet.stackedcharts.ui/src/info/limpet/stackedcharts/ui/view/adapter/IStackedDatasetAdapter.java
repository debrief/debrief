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

import info.limpet.stackedcharts.model.Dataset;

import java.util.List;

public interface IStackedDatasetAdapter
{
  /** whether this adapter can convert objects of the
   * supplied type.  This is expected to return
   * promptly, so it can be used when determining hover target
   * 
   * @param data the object to convert
   * @return yes/no
   */
  boolean canConvertToDataset(Object data);
  
  
  /** convert the supplied data object into a Limpet dataset
   * if possible
   * @param data the object to convert
   * @return the dataset (or null if this class can't convert it)
   */
  List<Dataset> convertToDataset(Object data);
}
