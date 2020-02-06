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
package MWC.Utilities.ReaderWriter.XML;



/**
 * interface for classes that are able to export a data object to XML
 * 
 * @author ian
 * 
 */
public interface ISAXImporter
{
  
  /** helper classes that can store data we've loaded
   * 
   * @author ian
   *
   */
  public static interface DataCatcher
  {
    /** store this data item
     * 
     * @param data
     */
    public void storeThis(Object data);
  }
  
  /** provide a suitable handler object
   * @param storeMe helper class, to store new data
   * 
   * @return
   */
  MWCXMLReader getHandler(DataCatcher storeMe);
}
