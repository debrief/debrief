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

import java.util.List;

import MWC.GUI.Editable;

/** API for Debrief extensions that are able to put content
 * into the Outline view
 * 
 * @author ian
 *
 */
public interface ExtensionContentProvider
{

  /** produce a (possibly empty) list of UI elements for this item
   * 
   * @param item the extension object
   * @return UI elements to represent the object
   */
  List<Editable> itemsFor(Object subject);
}